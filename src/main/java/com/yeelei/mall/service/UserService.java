package com.yeelei.mall.service;

import com.yeelei.mall.model.pojo.User;

public interface UserService {
    User getUser();

    void register(String username, String password);

    User login(String username, String password);

    void updateInformation(User user);

    boolean checkAdminRole(User user);
}
