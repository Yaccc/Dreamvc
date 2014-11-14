package org.majorxie.dreamvc.mvc.dispatcher;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
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
import org.majorxie.dreamvc.switcher.SwitcherFactory;
import org.majorxie.dreamvc.tag.Action;
import org.majorxie.dreamvc.tag.URI;
import org.majorxie.dreamvc.tag.Contextconfig.FixableConfig;
import org.majorxie.dreamvc.template.TemplateFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import test.T;

/**
 * 
 * dispater
 * update 2014-10-31
 * @author xiezhaodong
 *
 */
public class Dispatcher {
	private final Log log=LogFactory.getLog(getClass());
	private ServletContext servletContext;
	private SwitcherFactory switcherFactory=new SwitcherFactory();
	private Map<URI, Action> uri_action=new HashMap<URI, Action>();
	private ExceptionHandler handler=null;
	private Interceptor[] interceptors=null;
	private Map<String,Interceptor> interceptor_uri=new HashMap<String, Interceptor>();
	private String CodeEnhancement=null;
	private static final String JSPTEMPLATE="org.majorxie.dreamvc.template.JspTemplateFactory";
	
	void init(FixableConfig config)throws ServletException{
		this.servletContext=config.getServletContext();
		
		try {
			initProxy(config);
			log.info("init controllers and control");
		} catch (ServletException e) {
			throw e;
		} catch (Exception e) {
			 throw new ServletException("Dispatcher init failed.", e);
		}
		
	}
	/**
	 * 一些初始化工作 controller/Interceptor/模板的加载等
	 * @param config context容器
	 * @throws Exception 抛出该有的异常
	 */
	private void initProxy(FixableConfig config)throws Exception {
		
		String IocName=config.getInitParameter("container");
		if(IocName==null||IocName.equals("")){
			throw new NoParamterException("Missing init parameter <container>.");	
		}
		
		
		 CodeEnhancement=config.getInitParameter("CodeEnhancement");
		if(CodeEnhancement==null||CodeEnhancement.equals("")){
			throw new NoParamterException("Missing init parameter <CodeEnhancement>.");	
		}	
		if(!CodeEnhancement.equals("SpringAsm")&!CodeEnhancement.equals("javassist")){
			throw new NoParamterException("You must get a right codeEnhancement handler like SpringAsm if your IOC is Spring");	
		}
		
		IocFactory factory=FactoryHelper.getInstance().createIocFactory(IocName);
		factory.init(servletContext);
		List<Object> controllerBean=factory.getControllers();
		List<Object> InterceptorBeans=factory.getInterceptors();	
		//controller/interceptor的加载
		initControllerHander(controllerBean);
		initInterceptorHander(InterceptorBeans);
		
		//模板加载
		initTemplates(config);
		
	}
	
		
		
		
		/**
		 * 分析路径
		 * @param req
		 * @param resp
		 * @return
		 * @throws Exception
		 */
	  public boolean service(HttpServletRequest req, HttpServletResponse resp) throws  ServletException, IOException{
		  String url=req.getServletPath();//得到路径如/user/login
		  URI uri=new URI(url);
		  if(!uri_action.containsKey(uri)){//如果没有匹配返回false
			  return false;
		  }
		  Execution execution=null;
		  Action action=uri_action.get(uri);//得到该url对应的方法包装对象
		 
		  Method method=action.getMethod();//得到方法
		  Map<String, Object[]> parameters_name_args=req.getParameterMap();
		  if(parameters_name_args.size()==0){
			  execution=new Execution(action, null);
		  }else{
		  //针对get方法
		  Class<?>[] clazz=method.getParameterTypes();
		  List<String> ParametersName;
		try {
			ParametersName = getMethodParametersName(CodeEnhancement, method);//得到该方法的参数名字
		} catch (Exception e1) {
			throw new ServletException(e1);
		}
		  Object[] parameters=new Object[clazz.length];//要传入到执行方法中的可变参数
		  
		  for (int i = 0; i <ParametersName.size(); i++) {
			String name=ParametersName.get(i);//得到参数名字
			String args=(String) parameters_name_args.get(name)[0];//根据相应的名字得到参数
			if(clazz[i].equals(String.class)){
				parameters[i]=args;
			}else{
				try {				
				parameters[i]=switcherFactory.switcher(clazz[i], args);//转换真实数据类型
				} catch (Exception e) {
					resp.sendError(400);
					break;
				}
			}
		  }  
		  execution=new Execution(action, parameters);
		  }
		if(execution!=null){
			interceptors=regexpActionAndInterceptor(uri);//得到了該url的拦截器链
			handleExecution(req,resp,execution);
		}
		  
		return execution!=null;
  }
	  /**
	   * 执行exe
	   * @param req
	   * @param resp
	   * @param execution
	   * @throws Exception
	   */
	  void handleExecution(HttpServletRequest req,
				HttpServletResponse resp, Execution execution)throws  ServletException, IOException {
		  ActionContext.setActionContext(servletContext, req, resp);
		  
		   InterceptorChain chain=new InterceptorChain(execution, interceptors);
		   
			try {
				Object result=chain.exeInterceptor();
				chain.exeAfterInterceptor();
				handleResult(req,resp,result);
			} catch (Exception e) {	
				handleException(req,resp,e);//执行方法抛出来的异常
				log.warn("捕捉到异常");
						
			}finally{
				  ActionContext.removeActionContext();//删除该上下文
			}
		}
	/**
	   * 处理默认异常页面
	   * @param req
	   * @param resp
	 * @throws Exception 
	   */
	 void handleException(HttpServletRequest req,
				HttpServletResponse resp,Exception e) throws  ServletException, IOException {
		DefaultExceptionHandler defaultExceptionHandler=new DefaultExceptionHandler();
		try {
			defaultExceptionHandler.handle(req, resp, e);
		} catch (Exception e1) {
		throw new ServletException(e1);
		}
		 //默认异常
		}
	/**
	 * 处理结果
	 * @param req
	 * @param resp
	 * @param result
	 */
	 void handleResult(HttpServletRequest req,
				HttpServletResponse resp, Object result) throws  Exception{
			if(result==null){
				return;
			}			
			if(result instanceof Renderer){
				Renderer r=(Renderer) result;
				r.render(servletContext, req, resp);
				return ;
			}
			
			
		}
	/**
	 * //匹配拦截路径和相应的action，并初始化interceptors
	 */
	private Interceptor[] regexpActionAndInterceptor(URI uri) {
		List<Interceptor> list_inters=uri.getMatchedInterceptor(interceptor_uri);
		  
		Interceptor[] interceptors= list_inters.toArray(new Interceptor[list_inters.size()]);// 转换成数组
		Arrays.sort(interceptors,new Comparator<Interceptor>() {
			  public int compare(Interceptor o1, Interceptor o2) {
				String url_1=o1.getClass().getAnnotation(InterceptorURI.class).url();
				String url_2=o2.getClass().getAnnotation(InterceptorURI.class).url();
				if(url_1.length()>url_2.length()){//長的排在前面
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
	 * 加载模板
	 * @param config
	 */
	private void initTemplates(FixableConfig config) throws Exception{
		
		String template=config.getInitParameter("template");
		if("".equals(template)||template==null||template.equals("jsp")){
			log.info("You don't have template Parameters ,we will user default JSP template");	
			template=JSPTEMPLATE;//默认jsp模板
		} 
		
		TemplateFactory templateFactory=FactoryHelper.getInstance().createTemplateFactory(template);
		templateFactory.init(config);
		templateFactory.setInstance(templateFactory);
		
		
	}
	
	/**
	 * Interceptor处理
	 * @param interceptorBeans
	 */
	private void initInterceptorHander(List<Object> interceptorBeans) {
		int size=interceptorBeans.size();
		for (int i = 0; i <size; i++) {
			Interceptor interceptor=(Interceptor) interceptorBeans.get(i);//得到interceptor
			InterceptorURI interceptorURI=interceptor.getClass().getAnnotation(InterceptorURI.class);
			String annotationUri=interceptorURI.url();//得到拦截路径
			interceptor_uri.put(annotationUri, interceptor);//将拦截路径和相关Interceptor关联起来
		}
	
	}
	
	/**
	 * 处理controllers
	 * @param controllerBean
	 */
	private void initControllerHander(List<Object> controllerBean) {
		log.info("handler controller init");
		int size=controllerBean.size();
		for (int i = 0; i < size; i++) {			
			Object obj=controllerBean.get(i);
			addUrlMather(obj);//讲方法和uri关联起来			
		}
	
		
	}
	private void addUrlMather(Object obj) {
		Class clazz=obj.getClass();
		Method[] method=clazz.getMethods();
		
		for (int i = 0; i < method.length; i++) {
			if(isLegalMethod(method[i])){//该方法是否符合标准 
				//得到注解值,也就是方法的拦截器路径
				 String annotation=method[i].getAnnotation(RequestURI.class).value();
				 Action action=new Action(obj, method[i]);
				 URI uri=new URI(annotation);
				 uri_action.put(uri, action);//将路径和对象已经方法放入到一个map中区				 
			}
			
		}
		
	}

	/**
	 * 方法类型是否合法
	 * @param method 方法
	 * @return
	 */
	private boolean isLegalMethod(Method method) {
		RequestURI requestURI=method.getAnnotation(RequestURI.class);
		//不能为空
		if(requestURI==null||requestURI.value().length()==0){
			return false;
		}
		//不能为静态方法
		if(Modifier.isStatic(method.getModifiers())){
			
			return false;
		}		
		Class<?>[] putParameters=method.getParameterTypes();//得到所有参数
		//检测该方法的参数是否合法
		for (Class<?> class1 : putParameters) {
			if(!switcherFactory.isLegalMethod(class1)){			
				return false;
			}
		}
		 Class<?> retType = method.getReturnType();//得到该方法的返回值
	     if (retType.equals(void.class)//为void或者String或者
	                || retType.equals(String.class)
	                || Renderer.class.isAssignableFrom(retType)//是否符合render类或者子类的返回类型
	        ){
	    	
	    	 return true;
	     }else{
	    	 log.warn("Your method named "+method.getName()+"'s result type must be String/void/Templement");
	     }
		
		return false;
	}
	/**
	 * 得到方法的参数名字 从左往右
	 * @param CodeEnhancement 字节码增加方式选择使用gjavassist或者LocalVariableTableParameterNameDiscoverer
	 * @param method 要得到参数名字的方法
	 * @return List<String>
	 * @throws Exception 
	 */
	private List<String> getMethodParametersName(String CodeEnhancement,Method method) throws Exception{
		List<String> name=new LinkedList<String>();
		if(CodeEnhancement.equals("javassist")){
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
		       return name;
			} catch (Exception e) {
				throw e;
			}
		}else{
			LocalVariableTableParameterNameDiscoverer u =   
	            new LocalVariableTableParameterNameDiscoverer();  
	        String[] params = u.getParameterNames(method);  
	        for (int i = 0; i < params.length; i++) {  
	            name.add(params[i]); 
	        }  
	        return name;
			
		}
		
	}
	

	
}
