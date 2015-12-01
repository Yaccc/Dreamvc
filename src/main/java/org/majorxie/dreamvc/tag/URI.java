package org.majorxie.dreamvc.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.majorxie.dreamvc.interceptor.Interceptor;

/**
 * uri wrapper
 * @author xiezhaodong
 *majorxie@139.com
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
	 * 得到和该uri想匹配的拦截器类
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
	 * 匹配uri
	 * @param url方法
	 * @param interceptors url
	 * @return 是否为空匹配
	 */
public String matcher(String url,String interceptors){
		
		
		if(url.equals(interceptors))return interceptors;//如果该uri和interceptor中的完全相同	
		if(interceptors.endsWith("/"))return null;//interceptor不能以/结尾，如果想表示所有应该是/*
		
		//分开所有节点
		String[] urlsArray=url.split("/");
		String[] interceptorsArray=interceptors.split("/");
		
		
		if(interceptorsArray.length<urlsArray.length){
			boolean isMatched=true;
			if(interceptorsArray[interceptorsArray.length-1].equals("*")){
				//表示该拦截器的uri长度小于本身uri，同时是以*结尾
			for(int i = 0; i < interceptorsArray.length; i++) {
				if(!isMatched(urlsArray[i], interceptorsArray[i])){//依次匹配前面的节点
					isMatched=false;
					break;
				}
			}
				if(isMatched)return interceptors;//前面的节点相同，返回
			
			}else{		
				return null;
			}
			
		}
		
		if(interceptorsArray.length==urlsArray.length){
			//如果长度相等
			boolean isMatched=true;
			for (int i = 0; i < interceptorsArray.length; i++) {//依次比较
				if(!isMatched(urlsArray[i], interceptorsArray[i])){			
					isMatched=false;
					break;
				}
			}
			if(isMatched){//符合返回	
				return interceptors;
			}			
		}
		//拦截器的长度不能够比方法上的uri长，默认不匹配
		return null;
	
	}
	/**
	 *该节点是否匹配 相同或者为*
	 * @param urlPart ԭʼ·���ڵ�
	 * @param intersPart ����·���ڵ�
	 * @return
	 */
	private  boolean isMatched(String urlPart,String interceptorPart){
		return urlPart.equals(interceptorPart)||interceptorPart.equals("*");
	}
	
	//覆盖hadhCODE和equals方法，因为要作为map的key
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
