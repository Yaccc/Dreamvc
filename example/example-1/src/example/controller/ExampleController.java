package example.controller;

import java.io.IOException;
import java.io.PrintWriter;

import org.majorxie.dreamvc.annotation.Controller;
import org.majorxie.dreamvc.annotation.RequestURI;
import org.majorxie.dreamvc.helpers.ActionContext;
import org.majorxie.dreamvc.renderer.JsonRenderer;
import org.majorxie.dreamvc.renderer.Renderer;
import org.majorxie.dreamvc.renderer.TemplateRender;
import org.majorxie.dreamvc.template.ForwardType;

import javax.servlet.http.HttpServletRequest;


@Controller//这里选择注解的方式或者选择实现controller接口
public class ExampleController {
	@RequestURI("/request.do")
	public void getServletContext() throws IOException{
		//得到request、response对象
		HttpServletRequest req=ActionContext.getActionContext().getHttpServletRequest();
		javax.servlet.http.HttpServletResponse resp=ActionContext.getActionContext().getHttpServletResponse();
		String param=req.getParameter("id");
		PrintWriter out=resp.getWriter();
		out.print(param);
		out.close();
	}
	@RequestURI("/user/string.do")
	public String stringReturn(String s1,String s2){
		System.out.println(s1+""+s2);
		return "<h1>"+s1+"</h1><h2>"+s2+"</h2>";
	}

	@RequestURI("/render.do")
	public Renderer rendererReturn(String name,int id){
		//1	、TextRender
		//return new TextRenderer("<h1>"+name+"</h1>", "utf-8");
		//2、jsonRender 可以用于ajax请求
		String json="{\"flag\":"+id+",\"msg\":\""+name+"\"}";
		return new JsonRenderer(json);
		//3、TemplateRender
//		TemplateRender render=new TemplateRender("index.jsp");
//		render.addVaule("name", name);
//		render.addVaule("id", id);
//		return render;
	}
	@RequestURI("/forward.do")
	public Renderer forward(String url){

		return new TemplateRender(url,ForwardType.Redirect);
	}




}
