Dreamvc
=================================== 
A simple and support the restful structure of the Java MVC framework, I have little talent and less learning, we hope the exhibitions
>Dreamvc结合了Struts2和SpringMVC框架的思想，但是Dreamvc有两个入口(filter和servlet均可)，Dreamvc结合了python-flask框架的模板机制，实现了自己的模板，可自行扩展，目前Dreamvc自动实现了jsp和velocity模板。Dreamvc提供开发者自行的ioc接口，可以和任何ioc框架结合，Dramvc的拦截器采用struts2拦截器机制，annotation方式方便简单，匹配算法可以模糊匹配/精准匹配，方法的参数注入依赖javassist。

###Dreamvc大致流程图
![image](https://github.com/xiexiaodong/Dreamvc/blob/master/library/Dreamvc.png)

####ioc工厂接口
只要实现这个接口，就可以让Dramvc和任何ioc容器结合
```java
package org.majorxie.dreamvc.ioc.factory;

import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
/**
 *IOC 工厂
 *
 * @author xiezhaodong(majorxie@139.com)
 *2014-10-24
 */
public interface IocFactory {
	/**
	 * 初始化
	 * @param config
	 */
	void init(ServletContext context);
	/**
	 * destory ioc
	 */
	void destroy();
	/**
	 * 得到controler
	 * @return
	 */
	List<Object> getControllers()throws Exception;
	/**
	 * 得到interpcetor
	 * @return
	 */
	List<Object> getInterceptors();
	
	/**
	 * 得到其他
	 * @return
	 */
	List<Object> getOthers();
}

```
然后将实现类的全包路径在web.xml传入就行了，我默认实现了一个Springioc，具体可以见源代码
然后这样就行了
####xml文件
```xml
<init-param>
	<param-name>container</param-name>
	<param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value>
</init-param>
```
这样就实现dreamvc和ioc模块的集成，当然还可以自己实现自己的伪ioc，哈哈。
####模板模式集成，默认jsp模板
结合了flask框架的思想。让用户可以选择自己的模板比如JSP/VELOCITY/FREEMAKER，继承这个模板工厂
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
	/**
	 * 初始化
	 * @param config
	 */
	public abstract void init(StrategyConfig config);
	/**
	 * 主要方法
	 * @param path 跳转的路径
	 */
	public abstract Template initTemplate(String path,ForwardType type) throws Exception;
}
```
####然后是这个接口，实现完成的模板
```java
package org.majorxie.dreamvc.template;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 结合python的flask
 * @author xiezhaodong
 *2014-11-14
 */
public interface Template {
	/**
	 * @param req  request
	 * @param resp response
	 * @param models 
	 * @throws Exception
	 */
	void handleRender(HttpServletRequest req,HttpServletResponse resp,Map<String, Object> models)throws Exception;
}
```
#### 以下的xml是我自己实现的模板(jsp).
```xml
 </init-param>
	    <init-param>
	    <param-name>template</param-name>
	    <param-value>test.JspTemplateFactory</param-value>
 </init-param>
```
如果你是默认使用jsp模板的话，你完全舍去这个参数，dreamvc会自动帮你选择jsp模板

#### 如何使用
```java
@Controller//用controller注解表示该类或者实现controller接口
public class ConTest {
    @RequestURI("/login.do")//建议用.do的方式类似/user/login/check.do,参数传递最好全部都传，不传递会报404
	public Renderer hehe(String name,int  s) throws IOException{//目前还不支持bean传递，只要传统的参数
			//传递，函数返回值可以使String、void、render.render表示只用模板目前有/JsonTemplate/TextTemplate/
			//和jsp模板TemplateRender,默认跳转是forword跳转，可以看构造函数设置FORWARD.Rediect设置客户端跳转
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
如果你想得到servletapi，你可以在ActionContext.getHttpServletRequest()得到request对象,其他也是一样
### 关于拦截器的使用
首先你必须实现Interceptor接口或者继承AbstractInterceptor类，实现doInterceptor（）和afterInterceptor（）方法，而且必须要使用
InterceptorURI来指定需要拦截的路径，比如
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
interceptor返回true将会放行，执行下一个拦截器，返回false则不会对应执行方法
而且匹配度最高的路径会优先拦截，同时拦截路径的相对长度必须小于等于方法路径长度.等于的时候不确定的路径用*代替
比如我的方法路径是/user/login/check.do
那么我可以/`*`/`*`/check.do拦截可以/user/星/check.do来任意匹配，当然也可以如果短路径最后为`*`，那么星前面的路径应该相对相同,

		

  
  
  
  
