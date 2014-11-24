package org.majorxie.dreamvc.ioc.factory;

import java.util.List;


/**
 * 抽象实现接口
 *  @author xiezhaodong
 *2014-10-25
 */
public abstract class AbstractIocFactory implements IocFactory {
	/**
	 * 默认为空
	 */
	public void destroy() {
		
		
	}
	
	public List<Object> getOthers() {
		
		return null;
	}
	
	
}
