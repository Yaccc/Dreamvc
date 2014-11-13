package org.majorxie.dreamvc.helpers;

import org.majorxie.dreamvc.exception.NoIocInstanceException;
import org.majorxie.dreamvc.ioc.factory.IocFactory;

/**
 * 单例模式,产生对应的容器
 * 
 * @author xiezhaodong 2014-10-29
 */
public class FactoryHelper {

	private FactoryHelper() {
	}

	private static class SingletonClassInstance {
		private static final FactoryHelper instance = new FactoryHelper();
	}

	public static FactoryHelper getInstance() {
		return SingletonClassInstance.instance;
	}

	/**
	 * 
	 * @param clazzName
	 *            org.majorxie.dreamvc.ioc.factory.SpringIocFactory
	 * @return
	 * @throws Exception
	 */
	public static IocFactory createIocFactory(String clazzName)
			throws Exception {
		IocFactory iocFactory = null;
		Class clazz=null;
		//可能文件找不到
		try {
			clazz= Class.forName(clazzName);
		} catch (Exception e) {
			throw new NoIocInstanceException("No IOC container in your project,check your ioc class file url");
		}
		
		Object obj = clazz.newInstance();
		if (obj instanceof IocFactory) {
			iocFactory = (IocFactory) obj;
		}
		//不属于子类
		if (iocFactory == null||iocFactory.equals("")) {
			throw new NoIocInstanceException("your class must be a IocFactory class");
		}

		return iocFactory;
	}

}
