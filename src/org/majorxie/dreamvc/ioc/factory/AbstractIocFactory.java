package org.majorxie.dreamvc.ioc.factory;

import java.util.List;


/**
 * 如果是Spring容器就让他自己destory,其他的可以继承该类覆盖此方法
 * 如果想要扩展ioc，则可以选择使用extends还是implements
 *  @author xiezhaodong
 *2014-10-25
 */
public abstract class AbstractIocFactory implements IocFactory {
	/**
	 * 默认实现为空
	 */
	public void destroy() {
		
		
	}
	
	public List<Object> getOthers() {
		
		return null;
	}
	
	
}
