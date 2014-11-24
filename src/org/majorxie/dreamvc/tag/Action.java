package org.majorxie.dreamvc.tag;

import java.lang.reflect.Method;

/**
 * 方法和类的warpper
 * @author xiezhaodong
 *2014-10-31
 */
public class Action {
	
	private Object instance;
	private Method method;
	private Class<?>[] arguments;
	
	public Object getInstance() {
		return instance;
	}
	public void setInstance(Object instance) {
		this.instance = instance;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Class<?>[] getArguments() {
		return arguments;
	}
	public void setArguments(Class<?>[] arguments) {
		this.arguments = arguments;
	}
	/**
	 * 
	 * @param intsance ����
	 * @param method ����
	 * @param arguments ����
	 */
	public Action(Object intsance, Method method, Class<?>[] arguments) {
		this.instance = intsance;
		this.method = method;
		this.arguments = arguments;
	}
	/**
	 * 
	 * @param instance ����
	 * @param method ����
	 */
	public Action(Object instance, Method method) {
		this(instance,method,method.getParameterTypes());
	}
	
	

}
