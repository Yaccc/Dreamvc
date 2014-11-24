package org.majorxie.dreamvc.ioc.factory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.majorxie.dreamvc.exception.NotHaveControllerException;
import org.majorxie.dreamvc.interceptor.Interceptor;
import org.majorxie.dreamvc.tag.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * spring默认实现
 * 
 * @author xiezhaodong 2014-10-25 01:34
 * 
 */
public class SpringIocFactory extends AbstractIocFactory {
	private final Log log = LogFactory.getLog(SpringIocFactory.class);

	private ApplicationContext applicationContext;
	private List<Object> controllerBeans = new ArrayList<Object>();
	private List<Object> otherBeans = new ArrayList<Object>();
	private List<Object> interceptorBeans = new ArrayList<Object>();

	public void init(ServletContext context) {
		log.info("init context...");
		applicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(context);
		initBeans();
	}

	public List<Object> getControllers() throws NotHaveControllerException {

		if (controllerBeans.size() == 0) {
			throw new NotHaveControllerException("you need at least one controller ");
		} else {
			return controllerBeans;
		}
	}

	public List<Object> getInterceptors() {
		return interceptorBeans;
	}

	/**
	 * 加载bean，
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void initBeans() {
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			if (applicationContext.getBean(beanName) instanceof Interceptor/*||applicationContext.getType(beanName).isAnnotationPresent(org.majorxie.dreamvc.annotation.Interceptor.class)==true*/) {
				// applicationContext.getBean(beanName, Interceptor.class);
				interceptorBeans.add(applicationContext.getBean(beanName));
				log.info("init interceptor..");
			} else

			if (applicationContext.getBean(beanName) instanceof Controller||applicationContext.getType(beanName).isAnnotationPresent(org.majorxie.dreamvc.annotation.Controller.class)==true) {
				controllerBeans.add(applicationContext.getBean(beanName));
				log.info("init controller....");
			} else {
				otherBeans.add(applicationContext.getBean(beanName));
				log.info("init others...");
			}

		}

	}

	@Override
	public List<Object> getOthers() {

		return otherBeans;
	}

}
