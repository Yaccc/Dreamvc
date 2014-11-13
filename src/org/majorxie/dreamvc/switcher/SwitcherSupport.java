package org.majorxie.dreamvc.switcher;
/**
 * 转换String，所有静态内部类
 * @author xiezhaodong
 *
 */
public abstract class SwitcherSupport {
	
	public static class BooleanSwitcher implements Switcher<Boolean>{
		public Boolean switcher(String s)throws Exception {	
			return 	Boolean.parseBoolean(s);			
		}		
	}
	public static class ByteSwitcher implements Switcher<Byte>{

		public Byte switcher(String s) throws Exception{	
			return 	Byte.parseByte(s);
		}		
	}
	public static class FloatSwitcher implements Switcher<Float>{
		public Float switcher(String s)throws Exception {
			return Float.parseFloat(s);
		}
	}
	public static class DoubleSwitcher implements Switcher<Double>{
		public Double switcher(String s)throws Exception {
			return Double.parseDouble(s);
		}
	}
	public static class LongSwitcher implements Switcher<Long>{
		public Long switcher(String s) throws Exception {
			return Long.parseLong(s);
		}
	}
	public static class ShortSwitcher implements Switcher<Short>{
		public Short switcher(String s) throws Exception {
			return Short.parseShort(s);
		}
	}	
	public static class IntegerSwitcher implements Switcher<Integer>{
		public Integer switcher(String s) throws Exception {
			try {
			
				return Integer.parseInt(s);
			} catch (Exception e) {
				throw e;
			}
			
		}
	}
	public static class CharacterSwitcher implements Switcher<Character>{
		public Character switcher(String s) {
			if (s.length()==0)
	            throw new IllegalArgumentException("Cannot convert empty string to char.");
	        return s.charAt(0);
		}
	}

}
