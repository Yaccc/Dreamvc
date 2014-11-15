Dreamvc
=================================== 
A simple and support the restful structure of the Java MVC framework, I have little talent and less learning, we hope the exhibitions
###这里只是简单的列举一些用法，更多详细请移步http://blog.csdn.net/a837199685
  我就用中文写了~~~~^_^,这个框架是我自己的第一个开元作品，还是非常用心的！:-D 在javaweb开发的世界里有很多开元的mvc框架,balalal~~  因为在这一年中学习了Struts2,Spring IOC,SpringMVC,Mybatis.虽然我也还只是一个菜鸟，但是我也想学习大神的代码，就花了一些时间去看Springmvc和Struts2的源代码，因为当时觉得mvc最厉害O哈哈~，现在发现并不是这样，
  而且之前用过python的flask框架，和模板机制，所以我采用的类似flask框架的模板。可以继承jsp/velocity/freemaker等模板，并且提供了一个ioc容器接口，可以继承SPRING容器或者其他，甚至是自己写的容器，在拦截器方面我没有采用Struts2的思想，而是在SpringMVC的思想下有所改进。在参数注入方面，我采用的是springmvc的方法机制，这样更好控制，然而在方法参数注入这一块，如果你没有用spring的情况下需要加入一个字节码增强包javassist.当然如果你用了spring你就可以不用这个依赖了.然后，Dreamvc可以选择两个入口。比如struts2是采用filter的，springmvc是采用servlet的，dreamvc适配了两个接口，开发者可以选择自己的喜好而定，当然，在我写下这篇东西的时候，这个作品并没有完全完成，但是大部分功能都实现了,接下来做的事情还有很多，我正在努力，详细文档讲陆续写出来
  我现在讲几个重要的部分:
###一、ioc集成
    dreamvc可以集成任何ioc框架，只要按照指定接口就行
### 看这个接口
		package org.majorxie.dreamvc.ioc.factory;

		import java.util.List;

		import javax.servlet.ServletConfig;
		import javax.servlet.ServletContext;

		/**
		 *IOC 容器 工厂接口
		 *
	 	* @author xiezhaodong(majorxie@139.com)
		 *2014-10-24
		 */
		public interface IocFactory {
			/**
			 * 加载容器
			 * @param config
			 */
			void init(ServletContext context);
			
			
			/**
			 * destory ioc
			 */
			void destroy();
			
			
			/**
			 * 得到所有的controller对象
			 * @return
			 */
			List<Object> getControllers()throws Exception;
			
			/**
			 * 是否是拦截器
			 * @return
			 */
			List<Object> getInterceptors();
			
			/**
			 * 得到其他对象
			 * @return
			 */
			List<Object> getOthers();
		}
然后将实现类的全包路径在web.xml传入就行了，我默认实现了一个Springioc，具体可以见源代码
然后这样就行了
### 看xml文件
		  <init-param>
		<param-name>container</param-name>
		<param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value>
		</init-param>
这样就实现dreamvc和ioc模块的集成，当然还可以自己实现自己的伪ioc，哈哈。
###二、模板模式集成，默认jsp模板
结合了flask框架的思想。让用户可以选择自己的模板比如JSP/VELOCITY/FREEMAKER，继承这个模板工厂
		package org.majorxie.dreamvc.template;
		
		import org.majorxie.dreamvc.tag.Contextconfig.Config;
		
		/**
		 * 抽象工厂，用于初始化模板工厂
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
			 * 初始化一些上下文内容
			 * @param config
			 */
			public abstract void init(Config config);
			
			/**
			 * 加载模板
			 * @param path 要返回的路径
			 */
			public abstract Template initTemplate(String path,ForwardType type);
			
			
		}
然后是这个接口，实现正真的模板
		package org.majorxie.dreamvc.template;
		
		import java.util.Map;
		
		import javax.servlet.http.HttpServletRequest;
		import javax.servlet.http.HttpServletResponse;
		
		/**
		 * 基于python的模板机制
		 * @author xiezhaodong
		 *2014-11-14
		 */
		public interface Template {
		
			
			
			/**
			 * 
			 * @param req  request
			 * @param resp response
			 * @param models 要传递的数据，默认是model
			 * @throws Exception
			 */
			void handleRender(HttpServletRequest req,HttpServletResponse resp,Map<String, Object> models)throws Exception;
		}
### 以下的xml是我自己实现的模板(jsp).
		    </init-param>
		    <init-param>
		    <param-name>template</param-name>
		    <param-value>test.JspTemplateFactory</param-value>
		    </init-param>
如果你是默认使用jsp模板的话，你完全舍去这个参数，dreamvc会自动帮你选择jsp模板

### 如何使用
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
					System.out.println("-------");
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				
			}
		
		}

如果你想得到servletapi，你可以在ActionContext.getHttpServletRequest()得到request对象,其他也是一样
### 关于拦截器的使用
首先你必须实现Interceptor接口或者继承AbstractInterceptor类，实现doInterceptor（）和afterInterceptor（）方法，而且必须要使用
InterceptorURI来指定需要拦截的路径，比如

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
		interceptor返回true将会放行，执行下一个拦截器，返回false则不会对应执行方法
		而且匹配度最高的路径会优先拦截，同时拦截路径的相对长度必须小于等于方法路径长度.等于的时候不确定的路径用*代替
		比如我的方法路径是/user/login/check.do(星表示*)
		那么我可以/星/星/check.do拦截可以/user/星/check.do来任意匹配，当然也可以如果短路径最后为星，那么星前面的路径			应该相对相同

  
  
  
  
