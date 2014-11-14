package org.majorxie.dreamvc.template;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspTemplate implements Template {
	private String path;
	private ForwardType type;
	
	
	private JspTemplate() {
	}

	public JspTemplate(String path, ForwardType type) {
		this.path = path;
		this.type = type;
	}

	public void handleRender(HttpServletRequest req, HttpServletResponse resp,
			Map<String, Object> models) throws Exception {
		if(models!=null){
			
		   Set<String> keys = models.keySet();
	        for (String key : keys) {
	           req.setAttribute(key, models.get(key));
	        }
		}       
	        if(ForwardType.Forward==type||type==null){
	        	req.getRequestDispatcher(path).forward(req, resp);
	        	return;
	        	
	        }
	        resp.sendRedirect(path);
	        
		
	}

}
