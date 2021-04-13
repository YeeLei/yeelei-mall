package com.yeelei.mall.service;

import com.yeelei.mall.model.vo.CartVO;

import java.util.List;

public interface CartService {
    List<CartVO> list(Integer id);

    List<CartVO> add(Integer id, Integer productId, Integer count);

    List<CartVO> update(Integer id, Integer productId, Integer count);

    List<CartVO> delete(Integer id, Integer productId);

    List<CartVO> selectOrNot(Integer id, Integer productId, Integer selected);

    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
