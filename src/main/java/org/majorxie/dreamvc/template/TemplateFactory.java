package org.majorxie.dreamvc.template;

import org.majorxie.dreamvc.tag.Contextconfig.StrategyConfig;

/**
 * 结合python的flask框架，抽象出来一个模板方法
 * @author xiezhaodong
 *2014-11-14
 */
public abstract class TemplateFactory {
	private static TemplateFactory instance;
	
	
	public static void setInstance(TemplateFactory instance) {
		TemplateFactory.instance = instance;
	}
	
	public static TemplateFactory getInstance(){
		return instance;
	}
	
	/**
	 * 初始化
	 * @param config
	 */
	public abstract void init(StrategyConfig config);
	
	/**
	 * 主要方法
	 * @param path 跳转的路径
	 */
	public abstract Template initTemplate(String path,ForwardType type) throws Exception;
	
	
}
