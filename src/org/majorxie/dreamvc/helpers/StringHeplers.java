package org.majorxie.dreamvc.helpers;
/**
 * String 的一些操作
 * @author xiezhaodong
 *
 */
public class StringHeplers {
	
	private StringHeplers() {
	}

	
	
	 public static String captureName(String name) {
	       name = name.substring(0, 1).toUpperCase() + name.substring(1);
	       return  name;    
	  
	 
	 }
}
