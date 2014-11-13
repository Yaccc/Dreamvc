package org.majorxie.dreamvc.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.majorxie.dreamvc.interceptor.Interceptor;

/**
 * uri 类
 * @author xiezhaodong
 *
 */
public class URI {
	
	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public URI(String uri) {
		super();
		this.uri = uri;
	}
	/**
	 * 匹配相应的interceptor
	 * @param interceptor_map  装有interceptor的map
	 * @return 该请求路径的拦截器链
	 */
	public List<Interceptor> getMatchedInterceptor(Map<String,Interceptor> interceptor_map){
		List<Interceptor> list=new ArrayList<Interceptor>();
		for (String interceptorUri:interceptor_map.keySet()) {
			String returnInterceptor=matcher(this.uri, interceptorUri);
			if(returnInterceptor!=null){
				list.add(interceptor_map.get(returnInterceptor));
			}		
		}	
		return list;
	}
	
	
	/**
	 * 判断url和拦截器路径是否相对等价比如 /user/login和/user/*是相对等价的，就能够匹配
	 * @param url 请求url
	 * @param interceptors 拦截器url
	 * @return 匹配成功返回，否则返回null
	 */
public String matcher(String url,String interceptors){
		
		
		if(url.equals(interceptors))return interceptors;//完全相同		
		if(interceptors.endsWith("/"))return null;//不能这样结尾
		String[] urlsArray=url.split("/");
		String[] interceptorsArray=interceptors.split("/");
		
		
		if(interceptorsArray.length<urlsArray.length){
			boolean isMatched=true;
			if(interceptorsArray[interceptorsArray.length-1].equals("*")){
				//如果比他url短最后必须要以*结尾
			for(int i = 0; i < interceptorsArray.length; i++) {
				if(!isMatched(urlsArray[i], interceptorsArray[i])){//以短的一个为遍历
					isMatched=false;
					break;
				}
			}
				if(isMatched)return interceptors;
			
			}else{		
				return null;
			}
			
		}
		
		if(interceptorsArray.length==urlsArray.length){
			//等于
			boolean isMatched=true;
			for (int i = 0; i < interceptorsArray.length; i++) {//长度都一样
				if(!isMatched(urlsArray[i], interceptorsArray[i])){			
					isMatched=false;
					break;
				}
			}
			if(isMatched){//如果最后匹配完还是相同的话		
				return interceptors;
			}			
		}
	
		return null;
	
	}
	/**
	 * 匹配每一个节点
	 * @param urlPart 原始路径节点
	 * @param intersPart 拦截路径节点
	 * @return
	 */
	private  boolean isMatched(String urlPart,String interceptorPart){
		return urlPart.equals(interceptorPart)||interceptorPart.equals("*");
	}
	
	//重写hashcode()和equals方法，要作为map的key
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return uri.hashCode();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(this==obj){
			return true;
		}else if(obj instanceof URI){
			return ((URI) obj).uri.equals(this.uri);
			
		}
		return false;

	}
	

}
