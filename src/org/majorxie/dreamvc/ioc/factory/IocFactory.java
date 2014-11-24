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
