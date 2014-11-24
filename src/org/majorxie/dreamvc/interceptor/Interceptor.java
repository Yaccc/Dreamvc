package org.majorxie.dreamvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器接口
 * @author xiezhaodong
 *2014-10-27
 */
public interface Interceptor {
	/**
	 * 销毁
	 */
	void destory();
	
	/**
	 * 初始化
	 */
	void init();
	/**
	 * 执行intercptor
	 */
	boolean doInterceptor();
	
	
	void afterInterceptor();

}
