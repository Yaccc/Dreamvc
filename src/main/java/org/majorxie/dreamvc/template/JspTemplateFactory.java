package org.majorxie.dreamvc.template;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.majorxie.dreamvc.tag.Contextconfig.StrategyConfig;

public class JspTemplateFactory extends TemplateFactory {
	private final Log log=LogFactory.getLog(getClass());
	@Override
	public void init(StrategyConfig config) {
		log.info("default init jsp template ,if you want to implements others,you can do something from doc");
	}

	@Override
	public Template initTemplate(String path, ForwardType type) {
		return new JspTemplate(path, type);				
	}

}
