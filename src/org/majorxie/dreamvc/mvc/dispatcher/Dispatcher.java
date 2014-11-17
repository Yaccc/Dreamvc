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
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

/**
 * 
 * dispater
 * update 2014-11-17
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
	 *  controller/Interceptor/
	 * @param config context
	 * @throws Exception
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
		//controller/interceptor
		initControllerHander(controllerBean);
		initInterceptorHander(InterceptorBeans);
		
		
		initTemplates(config);
		
	}
	
		
		
		
		/**
		 *
		 * @param req
		 * @param resp
		 * @return
		 * @throws Exception
		 */
	  public boolean service(HttpServletRequest req, HttpServletResponse resp) throws  ServletException, IOException{
		  String url=req.getServletPath();
		  URI uri=new URI(url);
		  if(!uri_action.containsKey(uri)){
			  return false;
		  }
		  Execution execution=null;
		  Action action=uri_action.get(uri);
		 
		  Method method=action.getMethod();
		  Map<String, Object[]> parameters_name_args=req.getParameterMap();
		  if(parameters_name_args.size()==0){
			  execution=new Execution(action, null);
		  }else{
		  
		  Class<?>[] clazz=method.getParameterTypes();
		  List<String> ParametersName;
		try {
			ParametersName = getMethodParametersName(CodeEnhancement, method);
		} catch (Exception e1) {
			throw new ServletException(e1);
		}
		  Object[] parameters=new Object[clazz.length];
		  
		  for (int i = 0; i <ParametersName.size(); i++) {
			String name=ParametersName.get(i);
			String args=(String) parameters_name_args.get(name)[0];
			if(clazz[i].equals(String.class)){
				parameters[i]=args;
			}else{
				try {				
				parameters[i]=switcherFactory.switcher(clazz[i], args);
				} catch (Exception e) {
					resp.sendError(400);
					break;
				}
			}
		  }  
		  execution=new Execution(action, parameters);
		  }
		if(execution!=null){
			interceptors=regexpActionAndInterceptor(uri);
			handleExecution(req,resp,execution);
		}
		  
		return execution!=null;
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
		  ActionContext.setActionContext(servletContext, req, resp);
		  
		   InterceptorChain chain=new InterceptorChain(execution, interceptors);
		   
			try {
				Object result=chain.exeInterceptor();
				chain.exeAfterInterceptor();
				handleResult(req,resp,result);
			} catch (Exception e) {	
				handleException(req,resp,e);
				log.warn("��׽���쳣");
						
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
		DefaultExceptionHandler defaultExceptionHandler=new DefaultExceptionHandler();
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
			if(result instanceof Renderer){
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
	 *
	 */
	private Interceptor[] regexpActionAndInterceptor(URI uri) {
		List<Interceptor> list_inters=uri.getMatchedInterceptor(interceptor_uri);
		  
		Interceptor[] interceptors= list_inters.toArray(new Interceptor[list_inters.size()]);
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
	 * 
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
		templateFactory.setInstance(templateFactory);
		
		
	}
	
	/**
	 * Interceptor
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
	 * ����controllers
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
	 * 
	 * @param method 
	 * @return
	 */
	private boolean isLegalMethod(Method method) {
		RequestURI requestURI=method.getAnnotation(RequestURI.class);
		
		if(requestURI==null||requestURI.value().length()==0){
			return false;
		}
		
		if(Modifier.isStatic(method.getModifiers())){
			
			return false;
		}		
		Class<?>[] putParameters=method.getParameterTypes();
		
		for (Class<?> class1 : putParameters) {
			if(!switcherFactory.isLegalMethod(class1)){			
				return false;
			}
		}
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
	 * @param CodeEnhancement LocalVariableTableParameterNameDiscoverer
	 * @param method 
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
