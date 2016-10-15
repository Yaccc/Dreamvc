package org.majorxie.dreamvc.mvc.dispatcher;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.majorxie.dreamvc.annotation.InterceptorURI;
import org.majorxie.dreamvc.annotation.RequestURI;
import org.majorxie.dreamvc.exception.DefaultExceptionHandler;
import org.majorxie.dreamvc.exception.ExceptionHandler;
import org.majorxie.dreamvc.exception.NoParamterException;
import org.majorxie.dreamvc.helpers.ActionContext;
import org.majorxie.dreamvc.helpers.FactoryHelper;
import org.majorxie.dreamvc.interceptor.Execution;
import org.majorxie.dreamvc.interceptor.Interceptor;
import org.majorxie.dreamvc.interceptor.InterceptorChain;
import org.majorxie.dreamvc.ioc.factory.IocFactory;
import org.majorxie.dreamvc.renderer.Renderer;
import org.majorxie.dreamvc.renderer.TextRenderer;
import org.majorxie.dreamvc.switcher.SwitcherFactory;
import org.majorxie.dreamvc.tag.Action;
import org.majorxie.dreamvc.tag.URI;
import org.majorxie.dreamvc.tag.Contextconfig.FixableConfig;
import org.majorxie.dreamvc.template.TemplateFactory;

/**
 * 
 * dispater
 * update 2014-11-17
 * @author xiezhaodong
 *22:52
 */
public class Dispatcher {
	private final Log log=LogFactory.getLog(getClass());
	private ServletContext servletContext;
	private SwitcherFactory switcherFactory=new SwitcherFactory();
	private Map<URI, Action> uri_action=new HashMap<URI, Action>();
	private ExceptionHandler handler=null;
	private Map<String,Interceptor> interceptor_uri=new HashMap<String, Interceptor>();
	private String CodeEnhancement=null;
	private static final String JSPTEMPLATE="org.majorxie.dreamvc.template.JspTemplateFactory";
	
	void init(FixableConfig config)throws ServletException{
		this.servletContext=config.getServletContext();
		
		try {
			initProxy(config);//初始化
			log.info("init controllers and control");
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			 throw new ServletException("Dispatcher init failed.", e);
		}
		
	}
	/**
	 *  controller/Interceptor/
	 * @param config context
	 * @throws Exception
	 */
	private void initProxy(FixableConfig config)throws Exception {
		/*
		 * 初始化容器类型
		 * */
		String IocName=config.getInitParameter("container");
		if(IocName==null||"".equals(IocName)){
			throw new NoParamterException("Missing init parameter <container>.");	
		}
		
		/*
		 CodeEnhancement=config.getInitParameter("CodeEnhancement");
		if(CodeEnhancement==null||CodeEnhancement.equals("")){
			throw new NoParamterException("Missing init parameter <CodeEnhancement>.");	
		}	
		if(!CodeEnhancement.equals("SpringAsm")&!CodeEnhancement.equals("javassist")){
			throw new NoParamterException("You must get a right codeEnhancement handler like SpringAsm if your IOC is Spring");	
		}*/
		//拿到ioc工厂，得到controller和interceptor类的集合
		IocFactory factory=FactoryHelper.getInstance().createIocFactory(IocName);
		factory.init(servletContext);
		List<Object> controllerBean=factory.getControllers();
		List<Object> InterceptorBeans=factory.getInterceptors();	
		//依次加载controller、interceptor和返回模板类型
		initControllerHander(controllerBean);
		initInterceptorHander(InterceptorBeans);	
		initTemplates(config);
		
	}
	
		
		
		
		/**
		 *
		 *
		 *servlet或者filter将要调用的真正的方法
		 * @param req  
		 * @param resp
		 * @return 是否有路径映射
		 * @throws Exception
		 */
	protected boolean service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String url = req.getServletPath();//得到serlvetr请求路径比如/login/check.do
		URI uri = new URI(url);//得到uri包装对象，判断controller的uri包装对象中是否有该映射
		if (!uri_action.containsKey(uri)) {
			return false;
		}
		Execution execution = null;
		Action action = uri_action.get(uri); //拿到跟uri封装在一起的action对象

		Method method = action.getMethod();  
		Map<String, String[]> parameters_name_args = req.getParameterMap();//得到参数列表，也就是传进来的参数
		if (parameters_name_args.size() == 0) {//如果参数为空
			execution = new Execution(action, null);
		} else {

			Class<?>[] clazz = method.getParameterTypes();//得到形参class对象
			List<String> ParametersName;
			try {
				ParametersName = getMethodParametersName(CodeEnhancement,//得到方法中形参的名字
						method);
			} catch (Exception e1) {
				throw new ServletException(e1);
			}
			Object[] parameters = new Object[clazz.length];//依次装参数
			//根据形参的名字从map中找到值
			for (int i = 0; i < ParametersName.size(); i++) {
				String name = ParametersName.get(i);
				String args = (String) parameters_name_args.get(name)[0];
				if (clazz[i].equals(String.class)) {
					parameters[i] = args;//如果是STring的话，直接赋值
				} else {
					try {
						parameters[i] = switcherFactory//通过转换类进行适配类的转换
								.switcher(clazz[i], args);
					} catch (Exception e) {
						resp.sendError(400);//参数错误返回400
						break;
					}
				}
			}
			execution = new Execution(action, parameters);
		}
		//根据相应的uri找到对应的interceptor
		if (execution != null) {
			handleExecution(req, resp, execution);//执行结果
		}

		return execution != null;
	}
	  /**
	   *
	   * @param req
	   * @param resp
	   * @param execution
	   * @throws Exception
	   */
	  void handleExecution(HttpServletRequest req,
				HttpServletResponse resp, Execution execution)throws  ServletException, IOException {
		   ActionContext.setActionContext(servletContext, req, resp);//把servlet上下文放入actioncontext中
		  //将Interceptor的创建和使用都放在同一个方法中
		   Interceptor[] interceptors = regexpActionAndInterceptor(new URI(req.getServletPath()));
		   InterceptorChain chain=new InterceptorChain(execution, interceptors);//将得到的拦截器封装成一个拦截器链
		   
			try {//分别执行前后方法
				Object result=chain.exeInterceptor();
				chain.exeAfterInterceptor();
				handleResult(req,resp,result);
			} catch (Exception e) {	
				handleException(req,resp,e);
				log.warn("throws Exception");
						
			}finally{
				  ActionContext.removeActionContext();
			}
		}
	/**
	   * 
	   * @param req
	   * @param resp
	 * @throws Exception 
	   */
	 void handleException(HttpServletRequest req,
				HttpServletResponse resp,Exception e) throws  ServletException, IOException {
		DefaultExceptionHandler defaultExceptionHandler=new DefaultExceptionHandler();//默认的异常类
		try {
			defaultExceptionHandler.handle(req, resp, e);
		} catch (Exception e1) {
		throw new ServletException(e1);
		}
		
		}
	/**
	 * ������
	 * @param req
	 * @param resp
	 * @param result
	 */
	 void handleResult(HttpServletRequest req,
				HttpServletResponse resp, Object result) throws  Exception{
			if(result==null){
				return;
			}			
			if(result instanceof Renderer){//如果返回的是模板
				Renderer r=(Renderer) result;
				r.render(servletContext, req, resp);
				return ;
			}
			if(result instanceof String){
				new TextRenderer((String)result).render(servletContext, req, resp);
				return;
			}
			
			
		}
	/**
	 *匹配拦截器uri和方法uri是否相匹配
	 */
	private Interceptor[] regexpActionAndInterceptor(URI uri) {
		List<Interceptor> list_inters=uri.getMatchedInterceptor(interceptor_uri);
		  
		Interceptor[] interceptors= list_inters.toArray(new Interceptor[list_inters.size()]);
		//对拦截器排序，相似度大的排在前面执行
		Arrays.sort(interceptors,new Comparator<Interceptor>() {
			  public int compare(Interceptor o1, Interceptor o2) {
				String url_1=o1.getClass().getAnnotation(InterceptorURI.class).url();
				String url_2=o2.getClass().getAnnotation(InterceptorURI.class).url();
				if(url_1.length()>url_2.length()){
					return -1;
				}
				
				if(url_1.length()<url_2.length()){
					return 1;
				}
				return 0;
			}
		
		  }); 
		return interceptors;
	} 
	/**
	 * 初始化返回模板
	 * @param config
	 */
	private void initTemplates(FixableConfig config) throws Exception{
		
		String template=config.getInitParameter("template");
		if("".equals(template)||template==null){
			log.info("You don't have template Parameters ,we will user default JSP template");	
			template=JSPTEMPLATE;
		} 
		
		TemplateFactory templateFactory=FactoryHelper.getInstance().createTemplateFactory(template);
		templateFactory.init(config);
		templateFactory.setInstance(templateFactory);//设置模板类型
		
		
	}
	
	/**
	 * 得到interceptor并将interceptor和uri绑定起来
	 * @param interceptorBeans
	 */
	private void initInterceptorHander(List<Object> interceptorBeans) {
		int size=interceptorBeans.size();
		for (int i = 0; i <size; i++) {
			Interceptor interceptor=(Interceptor) interceptorBeans.get(i);
			InterceptorURI interceptorURI=interceptor.getClass().getAnnotation(InterceptorURI.class);
			String annotationUri=interceptorURI.url();
			interceptor_uri.put(annotationUri, interceptor);
		}
	
	}
	
	/**
	 * 初始化controllers
	 * @param controllerBean
	 */
	private void initControllerHander(List<Object> controllerBean) {
		log.info("handler controller init");
		int size=controllerBean.size();
		for (int i = 0; i < size; i++) {			
			Object obj=controllerBean.get(i);
			addUrlMather(obj);		
		}

	}
	/**
	 * 将uri和方法类，方法本身绑定
	 * @param obj
	 */
	private void addUrlMather(Object obj) {
		Class clazz=obj.getClass();
		Method[] method=clazz.getMethods();
		
		for (int i = 0; i < method.length; i++) {
			if(isLegalMethod(method[i])){
				
				 String annotation=method[i].getAnnotation(RequestURI.class).value();
				 Action action=new Action(obj, method[i]);
				 URI uri=new URI(annotation);
				 uri_action.put(uri, action);				 
			}
			
		}
		
	}

	/**
	 * 验证方法是否合法
	 * @param method 
	 * @return
	 */
	private boolean isLegalMethod(Method method) {
		RequestURI requestURI=method.getAnnotation(RequestURI.class);
		
		if(requestURI==null||requestURI.value().length()==0){//没有该注解默认不是
			return false;
		}
		
		if(Modifier.isStatic(method.getModifiers())){//不能使静态方法
			
			return false;
		}		
		Class<?>[] putParameters=method.getParameterTypes();
		
		for (Class<?> class1 : putParameters) {
			if(!switcherFactory.isLegalMethod(class1)){			
				return false;
			}
		}
		//返回值是否是这3种
		 Class<?> retType = method.getReturnType();
	     if (retType.equals(void.class)
	                || retType.equals(String.class)
	                || Renderer.class.isAssignableFrom(retType)
	        ){
	    	
	    	 return true;
	     }else{
	    	 log.warn("Your method named "+method.getName()+"'s result type must be String/void/Templement");
	     }
		
		return false;
	}
	/**
	 *
	 * @param 得到方法参数名字
	 * @param method 
	 * @return 返回参数列表
	 * @throws Exception 
	 */
	private List<String> getMethodParametersName(String CodeEnhancement,Method method) throws Exception{
		List<String> name=new LinkedList<String>();
		
			try {
				Class clazz = method.getDeclaringClass();  
				String methodName = method.getName();  
		        ClassPool pool = ClassPool.getDefault();  
		        pool.insertClassPath(new ClassClassPath(clazz));  
		        CtClass cc = pool.get(clazz.getName());  
		        CtMethod cm = cc.getDeclaredMethod(methodName);  
		        MethodInfo methodInfo = cm.getMethodInfo();  
		        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
		        LocalVariableAttribute attr =   
		                (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
		        String[] paramNames = new String[cm.getParameterTypes().length];  
		        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
		        for (int i = 0; i < paramNames.length; i++)  
		            name.add(attr.variableName(i + pos));  
		      
			} catch (Exception e) {
				throw e;
			}
		
			 return name;
	}
	

}
