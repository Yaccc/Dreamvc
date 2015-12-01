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
	 * 
	 * @param req  request
	 * @param resp response
	 * @param models 
	 * @throws Exception
	 */
	void handleRender(HttpServletRequest req,HttpServletResponse resp,Map<String, Object> models)throws Exception;
}
