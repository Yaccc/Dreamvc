package org.majorxie.dreamvc.template;

import org.majorxie.dreamvc.tag.Contextconfig.StrategyConfig;

/**
 * 抽象工厂，用于初始化模板工厂
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
	 * 初始化一些上下文内容
	 * @param config
	 */
	public abstract void init(StrategyConfig config);
	
	/**
	 * 加载模板
	 * @param path 要返回的路径
	 */
	public abstract Template initTemplate(String path,ForwardType type);
	
	
}
