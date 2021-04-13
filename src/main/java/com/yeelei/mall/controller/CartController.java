package com.yeelei.mall.controller;

import com.yeelei.mall.common.ApiRestResponse;
import com.yeelei.mall.filter.UserFilter;
import com.yeelei.mall.model.vo.CartVO;
import com.yeelei.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：购物车Controller
 */
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @ApiOperation("前台购物车列表")
    @GetMapping("/cart/list")
    public ApiRestResponse list() {
        //内部获取用户id，防止横向越权
        List<CartVO> list = cartService.list(UserFilter.currUser.getId());
        return ApiRestResponse.success(list);
    }

    @ApiOperation("前台添加商品到购物车")
    @PostMapping("/cart/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        //内部获取用户id，防止横向越权
        List<CartVO> cartVOList = cartService.add(UserFilter.currUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("前台购物车更新商品数量")
    @PostMapping("/cart/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        //内部获取用户id，防止横向越权
        List<CartVO> cartVOList = cartService.update(UserFilter.currUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("前台购物车删除商品")
    @PostMapping("/cart/delete")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        //内部获取用户id，防止横向越权
        List<CartVO> cartVOList = cartService.delete(UserFilter.currUser.getId(), productId);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("前台购物车选中/不选中商品")
    @PostMapping("/cart/select")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        //内部获取用户id，防止横向越权
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.currUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOList);
    }

    @ApiOperation("前台购物车全选/全不选商品")
    @PostMapping("/cart/selectAll")
    public ApiRestResponse selectAll(Integer selected) {
        //内部获取用户id，防止横向越权
        List<CartVO> cartVOList = cartService.selectAllOrNot(UserFilter.currUser.getId(), selected);
        return ApiRestResponse.success(cartVOList);
    }

}
