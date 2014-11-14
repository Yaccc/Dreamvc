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
