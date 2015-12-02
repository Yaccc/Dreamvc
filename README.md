Dreamvc
=================================== 
A simple and support the restful structure of the Java MVC framework, I have little talent and less learning, we hope the exhibitions
>`Dreamvc` combines the ideas of `Struts2` and `SpringMVC` framework，But `Dreamvc` has two entries(filter and servlet)，`Dreamvc` combines the template mechanism of `Python-flask` framework，Achieve their own template，Self expanding，At present, the `JSP` and `velocity` templates are implemented by Dreamvc.Dreamvc provides the developer's IOC interface can be combined with any IOC framework，Dreamvc uses the `Struts2` interceptor mechanism(Stack)，Annotation convenient way，Matching algorithm can be fuzzy matching / precision matching，The parameters of the method are injected into the `javassist` or `Spring framework`.

####IOC factory interface
- As long as the implementation of this interface, you can let Dramvc and any IOC container
```java
package org.majorxie.dreamvc.ioc.factory;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
/**
 *IOC Factory
 * @author xiezhaodong(majorxie@139.com)
 *2014-10-24
 */
public interface IocFactory {
	/**
	 * init config
	 */
	void init(ServletContext context);
	/**
	 * destory
	 */
	void destroy();
	/**
	 *get all controller
	 */
	List<Object> getControllers()throws Exception;
	/**
	 * get all interceptors
	 */
	List<Object> getInterceptors();
	/**
	 * init others object from IOC
	 */
	List<Object> getOthers();
}
```
- Then the implementation of full path incoming classes on the line in `web.xml`, I implemented a default `Springioc` (below)
```xml
<init-param>
	<param-name>container</param-name>
	<param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value>
</init-param>
```
####Template mode integration, default JSP template
- Combined with the idea of flask framework. So that users can choose their own templates such as JSP/velocity/freemarker, etc., as long as the successor to the template factory (as follows)
```java
package org.majorxie.dreamvc.template;
import org.majorxie.dreamvc.tag.Contextconfig.StrategyConfig;
/**
 * Python flask framework, abstract a template method
 * @author xiezhaodong
 *2014-11-14
 */
public abstract class TemplateFactory {
	private static TemplateFactory instance;
	public static void setInstance(TemplateFactory instance) {
		TemplateFactory.instance = instance;
	}
	public static TemplateFactory getInstance(){
		return instance;
	}
	public abstract void init(StrategyConfig config);
	public abstract Template initTemplate(String path,ForwardType type) throws Exception;
}
```
- Achieve this interface, complete the implementation of the template (see the implementation of the JSP template specifically)
```java
package org.majorxie.dreamvc.template;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public interface Template {
	void handleRender(HttpServletRequest req,HttpServletResponse resp,Map<String, Object> models)throws Exception;

}
```
```xml
 </init-param>
	    <init-param>
	    <param-name>template</param-name>
	    <param-value>org.majorxie.dreamvc.template.JspTemplateFactory</param-value>
 </init-param>
```
>If you are using the default `jsp` template, you can give this parameter, dreamvc will automatically help you select the JSP template

###How to use
- The `dreamvc-core.jar` package is included in your project (WEB-INF/lib), and then the project is built with the `pom.xml` I provide, which is necessary for the three party and the construction method (example/example2.0 is a complete example).

- The simplest `web.xml` configuration (see `example`).
```xml
 <!-- Configuration file location, the default is /WEB-INF/applicationContext.xml -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/applicationContext.xml</param-value>
    </context-param>
    <!-- The Spring context listener -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <filter>
        <filter-name>DispatcherFilter</filter-name>
        <filter-class>org.majorxie.dreamvc.mvc.dispatcher.DispatcherFilter
        </filter-class><!-- Select filter to enter, or select servlet to enter (as above) -->
        <init-param>
            <param-name>container</param-name>
            <param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value><!-- Select springioc as the IOC container -->
        </init-param>
        <init-param>
            <param-name>CodeEnhancement</param-name>
            <param-value>SpringAsm</param-value><!-- Choose SpringAsm or javassist -->

        </init-param>
        <init-param>
            <param-name>template</param-name>
            <param-value></param-value><!-- Select the template for the return of the template here and not automatically select the JSP template -->
        </init-param>
    </filter>
 <filter-mapping>
        <filter-name>DispatcherFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>
```
- `Controller` how to write, how to use the template?
```java
@Controller//Using controller annotations to indicate the class or implement the controller interface
public class ConTest {
    @RequestURI("/login.do")//It is suggested that the.Do is similar to /user/login/check.do, and the best parameters are passed, and no delivery will be reported to 404.
	public Renderer hehe(String name,int  s) throws IOException{//Bean is currently not supported, as long as the traditional parameters
			//Pass, function return value can make String, void, render.render said only template currently has /JsonTemplate/TextTemplate/
			//JSP template TemplateRender, the default jump is forword jump, you can see the constructor using FORWARD.Rediect set the client jump
			//Server side jump can pass the map object, you can also like the following way
			TemplateRender render=new TemplateRender("WEB-INF/pages/test.jsp");
			render.addVaule("posts", "qwoeqwe");
			return render;
	}

	@RequestURI("/check.do")
	public void haha() {
		try {
			System.out.println("do something...");
			} catch (Exception e) {
			    e.printStackTrace();
			}
	}
}
```
>If you want to get servletapi, you can get the request object in `ActionContext.getHttpServletRequest (), and the other is the same, see the `example` project

#### The use of the interceptor
First, you must implement the `Interceptor` interface or the `AbstractInterceptor` class to implement the `doInterceptor () and `afterInterceptor () method, and must be used.`InterceptorURI` comment to specify the path to intercept, as follows
```java
@InterceptorURI(url="/login.do")
public class Interceptor_02 extends AbstractInterceptor {

			@Override
			public boolean doInterceptor() {
				System.out.println("strat——02");
				return true;
			}

			@Override
			public void afterInterceptor() {
				System.out.println("end_02");
			}
}
@InterceptorURI(url="/*")
public class LoginInterceptor implements Interceptor {
			public void destory() {
			}
			public void init() {
			}
			public boolean doInterceptor() {
				System.out.println("login_start");
				return true;
			}
			public void afterInterceptor() {
				System.out.println("login_end");
			}
}
```
`interceptor` `true` will return to the release, the execution of the next interceptor, `false` does not return a corresponding executing method
And the highest degree of matching path will be a priority，While blocking the path of the relative length must be less than or equal to the path length. Equal to the uncertain path with `*` instead of
For example, the path is `/user/login/check.do`
So I can use `/* /*/check.do` to intercept it,You can also use the `/user/*/check.do` to match, if the short path to the end of the star, then the front of the path should be relatively the same.

- [English document](https://github.com/xiexiaodong/Dreamvc/blob/master/README.md)
- [中文文档](https://github.com/xiexiaodong/Dreamvc/blob/master/README_ZH_CN.md)
		

  
  
  
  
