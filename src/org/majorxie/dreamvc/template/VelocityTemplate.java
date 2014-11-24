package org.majorxie.dreamvc.template;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;

/**
 * Template using Velocity.
 * 集成velocity模板
 * @author xiezhaodong
 */
public class VelocityTemplate implements Template {

    private org.apache.velocity.Template template;
    private String contentType;
    private String encoding;

    public VelocityTemplate(org.apache.velocity.Template template, String contentType, String encoding) {
        this.template = template;
        this.contentType = contentType;
        this.encoding = encoding;
    }

    public void handleRender(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) throws Exception {
        StringBuilder sb = new StringBuilder(64);
        sb.append(contentType==null ? "text/html" : contentType)
                .append(";charset=")
                .append(encoding==null ? "UTF-8" : encoding);
        response.setContentType(sb.toString());
        response.setCharacterEncoding(encoding==null ? "UTF-8" : encoding);
        // init context:
        Context context = new VelocityContext(model);
        afterContextPrepared(context);
        // render:
        VelocityWriter vw = new VelocityWriter(response.getWriter());
        try {
            template.merge(context, vw);
            vw.flush();
        }
        finally {
            vw.recycle(null);
        }
    }

    /**
     * Let subclass do some initial work after Velocity context prepared.
     * 
     * @param context Velocity context object.
     */
    protected void afterContextPrepared(Context context) {
    }

	
}
