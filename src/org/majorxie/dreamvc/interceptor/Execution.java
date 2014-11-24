package org.majorxie.dreamvc.interceptor;

import org.majorxie.dreamvc.tag.Action;
/**
 * 执行方法
 * @author xiezhaodong
 *2014-11-10
 */
public class Execution {
	
	private Action action;
	private Object[] args;
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public Execution(Action action, Object[] args) {
		super();
		this.action = action;
		this.args = args;
	}
	/**
	 * 执行真正的方法
	 * @return
	 * @throws Exception
	 */
	Object execute() throws Exception{
		try {	
			return action.getMethod().invoke(action.getInstance(), args);		
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	
	
	
}
