package com.yeelei.mall.controller;

import com.yeelei.mall.common.ApiRestResponse;
import com.yeelei.mall.common.Constant;
import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.model.pojo.User;
import com.yeelei.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 描述：用户Controller
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

//    @ApiOperation("测试接口1")
//    @RequestMapping("/test1")
//    public User person() {
//        return userService.getUser();
//    }
//
//    @ApiOperation("测试接口2")
//    @RequestMapping("/test2")
//    public ApiRestResponse test() {
//        return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_USER_NAME);
//    }

    @ApiOperation("注册接口")
    @PostMapping("/register")
    public ApiRestResponse register(@RequestParam("username") String username, @RequestParam("password") String password) {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.PASSWORD_TO_SHORT);
        }
        userService.register(username, password);
        return ApiRestResponse.success();
    }

    @ApiOperation("登录接口")
    @PostMapping("/login")
    public ApiRestResponse login(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 HttpSession session) {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username, password);
        //不保存密码
        user.setPassword(null);
        //将user存入到session中
        session.setAttribute(Constant.YEELEI_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    @ApiOperation("用户注销接口")
    @PostMapping("/logout")
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.YEELEI_MALL_USER);
        return ApiRestResponse.success();
    }

    @ApiOperation("更新个性签名")
    @PostMapping("/user/update")
    public ApiRestResponse update(@RequestParam String signature, HttpSession session) {
        User currUser = (User) session.getAttribute(Constant.YEELEI_MALL_USER);
        if (currUser == null) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    @ApiOperation("管理员登录")
    @PostMapping("/admin/login")
    public ApiRestResponse adminLogin(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpSession session) {
        if (StringUtils.isEmpty(username)) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_PASSWORD);
        }

        User user = userService.login(username, password);
        //检验是否是管理员
        if (!userService.checkAdminRole(user)) {
            //不是管理员
            return ApiRestResponse.error(YeeLeiMallExceptionEnum.NEED_ADMIN);
        }
        user.setPassword(null);
        session.setAttribute(Constant.YEELEI_MALL_USER, user);
        return ApiRestResponse.success(user);
    }
}
