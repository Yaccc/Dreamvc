package example.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.majorxie.dreamvc.annotation.InterceptorURI;
import org.majorxie.dreamvc.helpers.ActionContext;
import org.majorxie.dreamvc.interceptor.AbstractInterceptor;
@InterceptorURI(url="/user/string.do")//这里我们队string.do拦截
public class Interceptor_01 extends AbstractInterceptor {

	@Override
	public boolean doInterceptor() {
		HttpServletRequest req=ActionContext.getActionContext().getHttpServletRequest();//也可以得到对象
		HttpSession session=ActionContext.getActionContext().getHttpSession();
		//然后对session进行操作
		System.out.println("FIRST START JSESSIONID:"+session.getId());
		return true;//true为放行，执行下一个拦截器，返回false则不会继续往下
	}
	
	@Override
	public void afterInterceptor() {//覆盖父类方法
		//执行完方法之后执行的方法
		System.out.println("FIRST END ");
		super.afterInterceptor();
	}

}
