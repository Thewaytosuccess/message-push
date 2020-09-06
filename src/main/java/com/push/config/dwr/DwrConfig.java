package com.push.config.dwr;

import org.directwebremoting.spring.DwrSpringServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xhzy
 */
@Configuration
public class DwrConfig {

    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        //注册dwr,拦截所有以dwr开头的请求
        ServletRegistrationBean dwrBean = new ServletRegistrationBean(new DwrSpringServlet(),
                "/dwr/*");
        //set debug true
        dwrBean.addInitParameter("debug","true");
        //增强连接能力
        dwrBean.addInitParameter("pollAndCometEnabled","true");
        //激活ajax反转
        dwrBean.addInitParameter("activeReverseAjaxEnabled","true");
        dwrBean.addInitParameter("maxWaitAfterWrite","60");

        //allow cross-domain
        dwrBean.addInitParameter("allowScriptTagRemoting","true");
        dwrBean.addInitParameter("crossDomainSessionSecurity","false");
        return dwrBean;
    }
}
