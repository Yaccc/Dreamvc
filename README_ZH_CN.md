Dreamvc
===================================
一个简单的和支持的Java的MVC框架，我才疏学浅，希望大家多多指教
>`Dreamvc`结合了`Struts2`和`SpringMVC`框架的思想，但是`Dreamvc`有两个入口(filter和servlet均可)，`Dreamvc`结合了`Python-flask`框架的模板机制，实现了自己的模板机制，可自行扩展，目前Dreamvc实现了`jsp`和`velocity`模板。Dreamvc提供开发者的IOC接口，可以和任何IOC框架结合，Dreamvc的拦截器采用`Struts2`拦截器机制(栈式)，annotation方式方便简单，匹配算法可以模糊匹配/精准匹配，方法的参数注入依赖`javassist`或者Spring框架自带的字节码方案。

####Dreamvc大致流程图
![image](https://github.com/xiexiaodong/Dreamvc/blob/master/library/Dreamvc.png)

####IOC工厂接口
- 只要实现这个接口，就可以让Dramvc和任何IOC容器结合
```java
package org.majorxie.dreamvc.ioc.factory;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
/**
 *IOC 工厂
 * @author xiezhaodong(majorxie@139.com)
 *2014-10-24
 */
public interface IocFactory {
	/**
	 * 初始化配置
	 */
	void init(ServletContext context);
	/**
	 * destory
	 */
	void destroy();
	/**
	 *得到所有的controller
	 */
	List<Object> getControllers()throws Exception;
	/**
	 * 得到所有的拦截器
	 */
	List<Object> getInterceptors();
	/**
	 * 得到IOC容器中其他对象
	 */
	List<Object> getOthers();
}
```
- 然后将实现类的全包路径在`web.xml`传入就行了，我默认实现了一个`Springioc`(如下)
```xml
<init-param>
	<param-name>container</param-name>
	<param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value>
</init-param>
```
####模板模式集成，默认jsp模板
- 结合了flask框架的思想。让用户可以选择自己的模板比如JSP/velocity/freemarker等等，只要继承这个模板工厂（如下）
```java
package org.majorxie.dreamvc.template;
import org.majorxie.dreamvc.tag.Contextconfig.StrategyConfig;
/**
 * 结合python的flask框架，抽象出来一个模板方法
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
- 实现这个接口，完成模板的实现（具体可参见jsp模板的实现）
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
>如果你是默认使用`jsp`模板的话，你完全舍去这个参数，dreamvc会自动帮你选择jsp模板

###如何使用
- 把`dreamvc-core.jar`包包含进您的项目（加入WEB-INF/lib中）,然后项目用我提供的`pom.xml`构建,里面有必要的三方包和构建方式（example/example2.0是一个完整的示例）特别注意这个配置,这能让你的maven编译lib中的jar包。如下
```xml
<!--add your dreamvc-core.jar's address-->
    <dependency>
      <groupId>dreamvc.majorxie</groupId>
      <artifactId>mvc-dream</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>${project.basedir}/src/main/webapp/WEB-INF/lib/dreamvc-core.jar</systemPath>
    </dependency>
```


- 最简单的`web.xml`配置（更多方式参见`example`）
```xml
 <!-- 配置文件位置，默认为/WEB-INF/applicationContext.xml -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/applicationContext.xml</param-value>
    </context-param>
    <!-- 上下文Spring监听器 -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    <filter>
        <filter-name>DispatcherFilter</filter-name>
        <filter-class>org.majorxie.dreamvc.mvc.dispatcher.DispatcherFilter
        </filter-class><!-- 选择filter进入，或者选择servlet进入（如上） -->
        <init-param>
            <param-name>container</param-name>
            <param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value><!-- 选择springioc作为ioc容器 -->
        </init-param>
        <init-param>
            <param-name>CodeEnhancement</param-name>
            <param-value>SpringAsm</param-value><!-- 选择SpringAsm或者javassist -->

        </init-param>
        <init-param>
            <param-name>template</param-name>
            <param-value></param-value><!-- 选择返回模板这里不填自动选择jsp模板 -->
        </init-param>
    </filter>
 <filter-mapping>
        <filter-name>DispatcherFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>
```
- `controller`如何写，模板如何使用？
```java
@Controller//用controller注解表示该类或者实现controller接口
public class ConTest {
    @RequestURI("/login.do")//建议用.do的方式类似/user/login/check.do,参数传递最好全部都传，不传递会报404
	public Renderer hehe(String name,int  s) throws IOException{//目前还不支持bean传递，只要传统的参数
			//传递，函数返回值可以使String、void、render.render表示只用模板目前有/JsonTemplate/TextTemplate/
			//jsp模板TemplateRender,默认跳转是forword跳转，可以看构造函数使用FORWARD.Rediect设置客户端跳转
			//服务器端跳转可以传递map对象，也可以像下面这种方式
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
>如果你想得到servletapi，你可以在`ActionContext.getHttpServletRequest()`得到request对象,其他也是一样,参见`example`项目

#### 关于拦截器的使用
首先你必须实现`Interceptor`接口或者继承`AbstractInterceptor`类，实现`doInterceptor（）`和`afterInterceptor（）`方法，而且必须要使用
`InterceptorURI`注解来指定需要拦截的路径，如下
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
`interceptor`返回`true`将会放行，执行下一个拦截器，返回`false`则不会对应执行方法
而且匹配度最高的路径会优先拦截，同时拦截路径的相对长度必须小于等于方法路径长度.等于的时候不确定的路径用*星*(星代替`*`字符)代替
比如我的方法路径是`/user/login/check.do`
那么我可以用/星/星/check.do拦截,也可以用/user/星/check.do来任意匹配，当然也可以如果短路径最后为`星`，那么星前面的路径应该相对相同,







