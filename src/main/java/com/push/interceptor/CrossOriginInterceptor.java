package com.push.interceptor;


import com.push.constant.ConstantPool;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xhzy
 */
public class CrossOriginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //跨域请求，*代表允许全部类型
        response.setHeader("Access-Control-Allow-Origin", "*");
        //允许请求方式
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        //用来指定本次预检请求的有效期，单位为秒，在此期间不用发出另一条预检请求
        response.setHeader("Access-Control-Max-Age", "3600");
        //请求包含的字段内容，如有多个可用哪个逗号分隔如下
        response.setHeader("Access-Control-Allow-Headers", "content-type,x-requested-with,Authorization");
        //访问控制允许凭据，true为允许
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 浏览器是会先发一次options请求，如果请求通过，则继续发送正式的post请求
        // 配置options的请求返回
        if (request.getMethod().equals(ConstantPool.OPTIONS)) {
            response.setStatus(HttpStatus.OK.value());
            try {
                response.getWriter().write("OPTIONS returns OK");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
