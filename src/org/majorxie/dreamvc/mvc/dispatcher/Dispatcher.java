package org.majorxie.dreamvc.mvc.dispatcher;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.majorxie.dreamvc.tag.Contextconfig.StrategyContext;
/**
 * filter½øÈë
 * @author xiezhaodong
 *2014-10-31
 */
public class DispatcherFilter implements Filter{
	private Log log=LogFactory.getLog(getClass());
	private Dispatcher dispatcher
	;

	public void destroy() {
		log.info("filter enter destory");
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		 HttpServletRequest req = (HttpServletRequest) arg0;
	        HttpServletResponse resp = (HttpServletResponse) arg1;
	        String method = req.getMethod();
	        if ("GET".equals(method) || "POST".equals(method)) {
	            if (!dispatcher.service(req, resp))
	            	arg2.doFilter(req, resp);
	            return;
	        }
	      
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		this.dispatcher=new Dispatcher();
		log.info("filter enter start...");
		StrategyContext config=new StrategyContext(arg0);
		dispatcher.init(config);
			
		
	}

}
