package com.yeelei.mall.config;

import com.yeelei.mall.filter.AdminFilter;
import com.yeelei.mall.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * 描述： User过滤器的配置
 */
@Configuration
public class UserFilterConfig {
    @Bean
    public UserFilter userFilter() {
        return new UserFilter();
    }

    @Bean(name = "userFilterConf")
    public FilterRegistrationBean adminFilterConfig() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(userFilter());
        filterFilterRegistrationBean.addUrlPatterns("/order/*");
        filterFilterRegistrationBean.addUrlPatterns("/cart/*");
        filterFilterRegistrationBean.addUrlPatterns("/admin/order/*");
        filterFilterRegistrationBean.addUrlPatterns("/pay/*");
        filterFilterRegistrationBean.setName("userFilterConf");
        return filterFilterRegistrationBean;
    }
}
