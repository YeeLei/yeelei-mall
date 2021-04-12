package com.yeelei.mall.service.impl;

import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.exception.YeeLeiMallExcetion;
import com.yeelei.mall.model.dao.UserMapper;
import com.yeelei.mall.model.pojo.User;
import com.yeelei.mall.service.UserService;
import com.yeelei.mall.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(13);
    }

    @Override
    public void register(String username, String password) {
        User result = userMapper.selectByUserName(username);
        //查询用户名是否存在,不允许重名
        if (result != null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.USER_NAME_EXISTED);
        }
        User user = new User();
        user.setUsername(username);

        //对密码进行MD5加密
        try {
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public User login(String username, String password) {
        String md5Str = null;
        try {
            md5Str = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(username, md5Str);
        if (user == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    @Override
    public void updateInformation(User user) {
        //更新个性签名
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count > 1) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public boolean checkAdminRole(User user) {
        //1是普通用户 2是管理员
        return user.getRole().equals(2);
    }
}
