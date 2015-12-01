package org.majorxie.dreamvc.helpers;

import org.majorxie.dreamvc.exception.NoIocInstanceException;
import org.majorxie.dreamvc.exception.NoTemplateFactory;
import org.majorxie.dreamvc.ioc.factory.IocFactory;
import org.majorxie.dreamvc.template.TemplateFactory;

/**
 * 工厂helper类
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
	 * ioc生产工厂
	 * @param clazzName
	 *            org.majorxie.dreamvc.ioc.factory.SpringIocFactory
	 * @return
	 * @throws Exception
	 */
	public static IocFactory createIocFactory(String clazzName)
			throws Exception {
		IocFactory iocFactory = null;
		Class clazz=null;
		//是否能够找到该类
		try {
			clazz= Class.forName(clazzName);
		} catch (Exception e) {
			throw new NoIocInstanceException("No IOC container in your project,check your ioc class file url");
		}
		//是否属于该子类
		Object obj = clazz.newInstance();
		if (obj instanceof IocFactory) {
			iocFactory = (IocFactory) obj;
		}
		
		if (iocFactory == null) {
			throw new NoIocInstanceException("Your class must be a subclass of IocFactory");
		}

		return iocFactory;
	}
	
	
	/**
	 * 模板生产工厂
	 * @param clazzName ��ȫ����
	 * @return
	 */
	public static TemplateFactory createTemplateFactory(String clazzName)throws Exception{
		TemplateFactory templateFactory=null;
		Class clazz=null;
		try {
			clazz= Class.forName(clazzName);
		} catch (Exception e) {
			throw new NoTemplateFactory("Your template factory class not found");
		}
		
		Object obj = clazz.newInstance();
		if (obj instanceof TemplateFactory) {
			templateFactory = (TemplateFactory) obj;
		}
		if (templateFactory == null) {
			throw new NoIocInstanceException("Your class must be a subclass of TemplateFactory");
		}
			
		
		return templateFactory;
	}

}
