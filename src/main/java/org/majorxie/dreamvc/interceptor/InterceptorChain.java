package org.majorxie.dreamvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 拦截器链
 * @author xiezhaodong
 *
 */
public class InterceptorChain {
	
	private Execution execution;
	private Interceptor[] interceptors;
	int index=0;
	public Execution getExecution() {
		return execution;
	}
	public void setExecution(Execution execution) {
		this.execution = execution;
	}
	public Interceptor[] getInterceptors() {
		return interceptors;
	}
	public void setInterceptors(Interceptor[] interceptors) {
		this.interceptors = interceptors;
	}
	public InterceptorChain(Execution execution, Interceptor[] interceptors) {
		super();
		this.execution = execution;
		this.interceptors = interceptors;
	}
	/**
	 * ִ执行拦截器
	 * @return
	 * @throws Exception
	 */
 public	Object exeInterceptor()throws Exception{
		boolean flag=true;
		//依次执行，遇到false跳出，否则放开执行下一个拦截器
		for (int i = 0; i < interceptors.length; i++) {
			index=i;
			if(!interceptors[i].doInterceptor()){
				flag=false;
				break;		
			}
			
		}
		//没有遇到false才执行方法
		if(flag){
			Object result=execution.execute();
			return result;
		}
		return null;
		
	}
 /**
  * 执行方法后
  */
 public void exeAfterInterceptor(){
	 if(interceptors.length!=0){//有拦截器才执行，不然会抛数组越界
	 for (int i = index; i>= 0; i--) {
			interceptors[i].afterInterceptor();
		}
	 }
 }
	
	
}
