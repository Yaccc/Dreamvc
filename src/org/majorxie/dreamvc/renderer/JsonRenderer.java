package org.majorxie.dreamvc.renderer;

/**
 * Convenience for render JavaScript.
 * 
 * @author xiezhaodong
 */
public class JsonRenderer extends TextRenderer {

    static final String MIME_JAVASCRIPT = "application/json";

    private JsonRenderer() {
        setContentType(MIME_JAVASCRIPT);
    }

    public JsonRenderer(String text) {
        super(text);
        setContentType(MIME_JAVASCRIPT);
    }

    public JsonRenderer(String text, String characterEncoding) {
        super(text, characterEncoding);
        setContentType(MIME_JAVASCRIPT);
    }

}
