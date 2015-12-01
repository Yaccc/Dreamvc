package org.majorxie.dreamvc.switcher;
/**
 * switch String to any given type.
 * @author xiezhaodong
 *2014-11-9
 * @param <E> Generic type of switched result.
 */
public interface Switcher<E> {
	
	public E switcher(String s) throws Exception;

}
