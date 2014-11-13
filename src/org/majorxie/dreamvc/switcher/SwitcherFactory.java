package org.majorxie.dreamvc.switcher;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.majorxie.dreamvc.helpers.StringHeplers;

import test.T;

/**
 *检测工厂
 * @author xiezhaodong
 *
 */
public class SwitcherFactory {


    private final Log log = LogFactory.getLog(getClass());

    private Map<Class<?>, Switcher<?>> map = new HashMap<Class<?>,Switcher<?>>();
    
    public SwitcherFactory() {
    	loadParameters();
    }
    //加载基本数据类型
	private void loadParameters() {
			Switcher<?> switcher = null;
			switcher =  new SwitcherSupport.BooleanSwitcher();
	        map.put(boolean.class, switcher);
	        map.put(Boolean.class, switcher);

	        switcher = new SwitcherSupport.CharacterSwitcher();
	        map.put(char.class, switcher);
	        map.put(Character.class, switcher);

	        switcher = new SwitcherSupport.ByteSwitcher();
	        map.put(byte.class, switcher);
	        map.put(Byte.class, switcher);

	        switcher = new SwitcherSupport.ShortSwitcher();
	        map.put(short.class, switcher);
	        map.put(Short.class, switcher);

	        switcher = new SwitcherSupport.IntegerSwitcher();
	        map.put(int.class, switcher);
	        map.put(Integer.class, switcher);

	        switcher = new SwitcherSupport.LongSwitcher();
	        map.put(long.class, switcher);
	        map.put(Long.class, switcher);

	        switcher = new SwitcherSupport.FloatSwitcher();
	        map.put(float.class, switcher);
	        map.put(Float.class, switcher);

	        switcher = new SwitcherSupport.DoubleSwitcher();
	        map.put(double.class, switcher);
	        map.put(Double.class, switcher);
	}
	
	
	
	/**
	 * String 和基本数据类型的转换
	 * @param clazz
	 * @param s
	 * @return
	 * @throws Exception 
	 */
	public Object switcher(Class<?> clazz,String s) throws Exception{
		Switcher<?> switcher=map.get(clazz);
		return  switcher.switcher(s);	
	}
	
	/**
	 * 是否属于基本数据类型或者Stirng
	 * @param clazz class
	 * @return
	 */
	public boolean isBaseDataTypeOrString(Class<?> clazz){
		return clazz.equals(String.class)||map.containsKey(clazz);	
	}
	
	/**
	 * 方法参数是否合法，包括是否是基本数据类型，或者是bean对象(字段必须要求同时拥有set和get方法)
	 * @param clazz clazz
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isLegalMethod(Class<?> clazz) {
		if(clazz.equals(String.class)||map.containsKey(clazz)){
			return true;
		}else{//可能是表单对象
			boolean returnFlag=false;
			Field[] field=clazz.getFields();
			//检测是否字段同时拥有set和get字段
			for (Field field2 : field) {
				String name=field2.getName();
				try {
					if(clazz.getMethod("set"+StringHeplers.captureName(name), field2.getType()) != null&&clazz.getMethod("get"+StringHeplers.captureName(name)) != null){				
						returnFlag=true;				
					}			
				} catch (NoSuchMethodException e) {
					returnFlag=false;
					log.warn("Your bean named "+field2+" do not have set or get method");
				}
			}
			return returnFlag;
		}
	
		
	}
	
	
//	public static void main(String[] args) {
//		String s="12";
//		SwitcherFactory factory=new SwitcherFactory();
//		System.out.println(factory.switcher(Float.class, s).getClass());
//	}
 
 
}
