package com.yeelei.mall.service.impl;

import com.yeelei.mall.common.Constant;
import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.exception.YeeLeiMallExcetion;
import com.yeelei.mall.model.dao.CartMapper;
import com.yeelei.mall.model.dao.ProductMapper;
import com.yeelei.mall.model.pojo.Cart;
import com.yeelei.mall.model.pojo.Product;
import com.yeelei.mall.model.vo.CartVO;
import com.yeelei.mall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：购物车Service实现类
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 查询前台购物车列表
     *
     * @param userId 用户id
     * @return 购物车列表
     */
    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOList = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOList) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOList;
    }

    /**
     * 添加商品
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param count     商品数量
     * @return 购物车列表
     */
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        //判断商品是否存在，商品是否上架
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里,需要新增一个记录
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            //商品默认选中状态
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            //该商品已经存在购物车中，则数量相加
            count = cart.getQuantity() + count;
            Cart newCart = new Cart();
            newCart.setQuantity(count);
            newCart.setId(cart.getId());
            newCart.setProductId(cart.getProductId());
            newCart.setUserId(cart.getUserId());
            newCart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.list(userId);
    }

    /**
     * 更新商品数量
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param count     商品数量
     * @return 购物车列表
     */
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车，无法更新
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品已经在购物车了，则更新数量
            Cart cartNew = new Cart();
            cartNew.setQuantity(count);
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    /**
     * 根据用户id和商品id删除商品，并返回购物车列表
     *
     * @param userId    用户id
     * @param productId 商品id
     * @return 购物车列表
     */
    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //商品不再购物车
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.DELETE_FAILED);
        } else {
            //这个商品已经在购物车了，则可以删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    /**
     * 选中/不选中购物车商品
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param selected  选中状态 0-不选中 1-选中
     * @return 购物车列表
     */
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品不在购物车，则不可以进行更新选中状态
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品已经在购物车里面了，则可以选中/不选中
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    /**
     * 全选/不全选购物车商品
     *
     * @param userId   用户id
     * @param selected 选中状态 0-不选中 1-选中
     * @return 购物车列表
     */
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        //传入productId为null，实现全选/全不选
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }

    /**
     * 判断商品是否存在，商品是否上架
     *
     * @param productId 商品id
     * @param count 商品数量
     */
    private void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NOT_SALE);
        }
        //判断库存是否足够
        if (count > product.getStock()) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NOT_ENOUGH);
        }
    }
}
