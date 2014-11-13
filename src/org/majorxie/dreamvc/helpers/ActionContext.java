package org.majorxie.dreamvc.helpers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Holds all Servlet objects in ThreadLocal.
 * 
 * @author xiezhaodong
 * 2014-11-9
 */
public final class ActionContext {

    private static final ThreadLocal<ActionContext> actionContextThreadLocal = new ThreadLocal<ActionContext>();

    private ServletContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * Return the ServletContext of current web application.
     */
    public ServletContext getServletContext() {
        return context;
    }

    /**
     * Return current request object.
     */
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    /**
     * Return current response object.
     */
    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    /**
     * Return current session object.
     */
    public HttpSession getHttpSession() {
        return request.getSession();
    }

    /**
     * Get current ActionContext object.
     */
    public static ActionContext getActionContext() {
        return actionContextThreadLocal.get();
    }

    public static void setActionContext(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
        ActionContext ctx = new ActionContext();
        ctx.context = context;
        ctx.request = request;
        ctx.response = response;
        actionContextThreadLocal.set(ctx);
    }

   public static void removeActionContext() {
        actionContextThreadLocal.remove();
    }
}
