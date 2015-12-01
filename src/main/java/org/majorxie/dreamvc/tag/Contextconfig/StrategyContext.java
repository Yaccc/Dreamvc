package org.majorxie.dreamvc.tag.Contextconfig;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
/**
 * 2014-10-28
 * @author xiezhaodong
 *策略模式，匹配filter或者servlet
 *servletconfig servlet
 *filterconfig filter
 *
 */
public class StrategyContext implements FixableConfig {
	private ServletConfig servletConfig;
	private FilterConfig filterConfig;
	
	public StrategyContext(ServletConfig servletConfig) {
		this(servletConfig,null);
	}
	public StrategyContext(FilterConfig filterConfig) {
		this(null,filterConfig);
	}
	/**
	 * peivate 访问权限，避免异常错误
	 * @param servletConfig
	 * @param filterConfig
	 */
	private StrategyContext(ServletConfig servletConfig,FilterConfig filterConfig) {
		this.filterConfig =filterConfig;
		this.servletConfig=servletConfig;
	}

	public String getInitParameter(String name) {
		return servletConfig==null?filterConfig.getInitParameter(name):servletConfig.getInitParameter(name);
	}

	public ServletContext getServletContext() {
		return servletConfig==null?filterConfig.getServletContext():servletConfig.getServletContext();
	}

	@SuppressWarnings("unchecked")
	public String[] getInitParameters() {
		Enumeration<String> names=servletConfig==null?filterConfig.getInitParameterNames():servletConfig.getInitParameterNames();
		List<String> list=new ArrayList<String>();
		while(names.hasMoreElements()){
			list.add(this.getInitParameter(names.nextElement()));
		}
		return (String[]) list.toArray();
	}

}
