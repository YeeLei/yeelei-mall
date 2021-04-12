package com.yeelei.mall.filter;

import com.yeelei.mall.common.Constant;
import com.yeelei.mall.model.pojo.User;
import com.yeelei.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminFilter implements Filter {
    @Autowired
    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User currUser = (User) session.getAttribute(Constant.YEELEI_MALL_USER);
        if (currUser == null) {
            PrintWriter out = new ServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("\"status\": 10007,\n" +
                    "    \"msg\": \"NEED_LOGIN\",\n" +
                    "    \"data\": null");
            out.flush();
            out.close();
            return;
        }
        //判断是否为管理员
        if (!userService.checkAdminRole(currUser)) {
            //不是管理员
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("\"status\": 10009,\n" +
                    "    \"msg\": \"NEED_ADMIN\",\n" +
                    "    \"data\": null");
            out.flush();
            out.close();
            return;
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
