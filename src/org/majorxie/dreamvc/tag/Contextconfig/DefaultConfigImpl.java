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
 *配置默认实现
 *对象适配
 *有3个构造函数,选择需要的构造函数实例化
 *servletconfig用于serlvet进入
 *filterconfig用于filter进入
 *
 */
public class DefaultConfigImpl implements FixableConfig {
	private ServletConfig servletConfig;
	private FilterConfig filterConfig;
	
	public DefaultConfigImpl(ServletConfig servletConfig) {
		this(servletConfig,null);
	}
	public DefaultConfigImpl(FilterConfig filterConfig) {
		this(null,filterConfig);
	}
	
	public DefaultConfigImpl(ServletConfig servletConfig,FilterConfig filterConfig) {
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
