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
