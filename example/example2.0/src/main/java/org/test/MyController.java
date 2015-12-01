package org.test;

import org.majorxie.dreamvc.annotation.Controller;
import org.majorxie.dreamvc.annotation.RequestURI;

/**
 * Created by zhaodong on 15/12/1.
 */
@Controller
public class MyController {


    @RequestURI("/user/string.do")
    public String stringReturn(){
        return "<h1>hello,wolrd <h1/>";
    }
}
