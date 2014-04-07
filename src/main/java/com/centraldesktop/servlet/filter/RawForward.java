package com.centraldesktop.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;


public class RawForward implements Filter {
    private static int RAW_OFFSET = "/plugins/servlet/raw".length();
    private static final Logger log = LoggerFactory.getLogger(RawForward.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init raw filter");
    }

    public void destroy() {
    }


    /**
     * Use a different url pattern for RAW urls so you can serve relative content transparently
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hr = (HttpServletRequest) request;
        String context = hr.getContextPath();


        String uri = hr.getRequestURI();

        System.out.println("f translated " + hr.getPathTranslated());
        System.out.println("f pathinfo " + hr.getPathInfo());
        System.out.println("f context " + context);
        System.out.println("f qs " + hr.getQueryString());


        String rawPath = uri.substring(context.length() + RAW_OFFSET);

        System.out.println("f New path -- " + rawPath);
//
//        request.getRequestDispatcher(rawPath).
//                include(new RawWrappedServletRequest(hr),
//                        response);

        request.getRequestDispatcher(rawPath).
                forward(new RawWrappedServletRequest(hr), response);



//        //continue the request
//        chain.doFilter(new RawWrappedServletRequest((HttpServletRequest) request), response);
    }

}


class RawWrappedServletRequest extends HttpServletRequestWrapper {


    public RawWrappedServletRequest(HttpServletRequest request) {
        super(request);
    }

    //    @Override
//    public String
//    @Override
//    public String getQueryString() {
//        return "raw";
//    }


}