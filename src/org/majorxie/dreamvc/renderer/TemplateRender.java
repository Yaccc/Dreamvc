package org.majorxie.dreamvc.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.majorxie.dreamvc.template.ForwardType;
import org.majorxie.dreamvc.template.TemplateFactory;



public class TemplateRender extends Renderer   {
	private String path;
	private ForwardType type;
	public Map<String, Object> models;

	public TemplateRender(String path) {		
		this(path,null,null);
		models=new HashMap<String, Object>();
	}
	public void addVaule(String name,Object value){
		models.put(name, value);
	}


	public TemplateRender(String path, ForwardType type) {
		this(path,type,null);
	}

	public TemplateRender(String path, ForwardType type,
			Map<String, Object> models) {
		this.path = path;
		this.type = type;
		this.models = models;
	}

	private TemplateRender() {
	}
	
	
	@Override
	public void render(ServletContext context, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		TemplateFactory.getInstance().initTemplate(path, type).handleRender(request, response, models);
	}

	
	
	
}
