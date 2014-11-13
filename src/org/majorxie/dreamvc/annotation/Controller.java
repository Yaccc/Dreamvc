package org.majorxie.dreamvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解的tag
 * @author xiezhaodong
 *
 */

@Target(ElementType.TYPE)//标注为控制器哦
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

	
}
