package org.majorxie.dreamvc.exception;
/**
 * 是否有控制器异常
 * @author xiezhaodong
 *2014-10-27
 */
public class NotHaveControllerException extends Exception {

	
	public NotHaveControllerException() {
		super();
	}
	
	public NotHaveControllerException(String message){
		
		super(message);
	}
}
