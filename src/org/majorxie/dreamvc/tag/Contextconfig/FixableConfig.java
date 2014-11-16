package org.majorxie.dreamvc.tag.Contextconfig;

import org.majorxie.dreamvc.tag.Contextconfig.StrategyConfig;

/**
 * 2014-10-28
 * @author xiezhaodong
 *
 */
public interface FixableConfig extends StrategyConfig {
	
	/**
	 * 得到所有的参数
	 * @return
	 */
	String[] getInitParameters();

}
