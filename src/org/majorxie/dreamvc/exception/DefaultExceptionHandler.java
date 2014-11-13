package org.majorxie.dreamvc.exception;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultExceptionHandler implements ExceptionHandler {

	public void handle(HttpServletRequest request,
			HttpServletResponse response, Exception e) throws Exception {
			PrintWriter pw = response.getWriter();
	        pw.write("<html><head><title>Exception</title></head><body><pre>");
	        e.printStackTrace(pw);
	        pw.write("</pre></body></html>");
	        pw.flush();
	}

}
