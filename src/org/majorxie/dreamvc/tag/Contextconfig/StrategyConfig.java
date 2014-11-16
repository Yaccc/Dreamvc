package org.majorxie.dreamvc.tag.Contextconfig;

import javax.servlet.ServletContext;
/**
 * 2014-10-28
 * @author xiezhaodong
 *
 */
public interface StrategyConfig {
	
	/**
	 * 得到指定了参数
	 * @return
	 */
	String getInitParameter(String name);
	/**
	 * 
	 * @return servlet context
	 */
	ServletContext getServletContext();

}
