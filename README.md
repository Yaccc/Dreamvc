Dreamvc
=================================== 
A simple and support the restful structure of the Java MVC framework, I have little talent and less learning, we hope the exhibitions
  我就用中文写了~~~~^_^,这个框架是我自己的第一个开元作品，还是非常用心的！:-D 在javaweb开发的世界里有很多开元的mvc框架,balalal~~  因为在这一年中学习了Struts2,Spring IOC,SpringMVC,Mybatis.虽然我也还只是一个菜鸟，但是我也想学习大神的代码，就花了一些时间去看Springmvc和Struts2的源代码，因为当时觉得mvc最厉害O哈哈~，现在发现并不是这样，
  而且之前用过python的flask框架，和模板机制，所以我采用的类似flask框架的模板。可以继承jsp/velocity/freemaker等模板，并且提供了一个ioc容器接口，可以继承SPRING容器或者其他，甚至是自己写的容器，在拦截器方面我没有采用Struts2的思想，而是在SpringMVC的思想下有所改进。在参数注入方面，我采用的是springmvc的方法机制，这样更好控制，然而在方法参数注入这一块，如果你没有用spring的情况下需要加入一个字节码增强包javassist.当然如果你用了spring你就可以不用这个依赖了.然后，Dreamvc可以选择两个入口。比如struts2是采用filter的，springmvc是采用servlet的，dreamvc适配了两个接口，开发者可以选择自己的喜好而定，当然，在我写下这篇东西的时候，这个作品并没有完全完成，但是大部分功能都实现了,接下来做的事情还有很多，我正在努力，详细文档讲陆续写出来
  我现在讲几个重要的部分:
  一、ioc集成
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
		 <init-param>
			<param-name>container</param-name>
			
		    <param-value>org.majorxie.dreamvc.ioc.factory.SpringIocFactory</param-value>
		    
		   </init-param>



  
  
  
  
