package com.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yeelei.mall.common.Constant;
import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.exception.YeeLeiMallExcetion;
import com.yeelei.mall.model.dao.ProductMapper;
import com.yeelei.mall.model.pojo.Product;
import com.yeelei.mall.model.query.ProductListQuery;
import com.yeelei.mall.model.request.AddProductReq;
import com.yeelei.mall.model.request.ProductListReq;
import com.yeelei.mall.model.request.UpdateProductReq;
import com.yeelei.mall.model.vo.CategoryVO;
import com.yeelei.mall.service.CategoryService;
import com.yeelei.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 商品service实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product oldProduct = productMapper.selectByName(addProductReq.getName());
        if (oldProduct != null) {
            //添加商品名称已存在
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.PRODUCT_NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(UpdateProductReq updateProductReq) {
        Product oldProduct = productMapper.selectByName(updateProductReq.getName());
        //同名且不同id,不能进行修改
        if (oldProduct != null && !oldProduct.getId().equals(updateProductReq.getId())) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.PRODUCT_NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        //查不到商品，无法删除
        if (product == null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.DELETE_FAILED);
        }

        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listProductForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.selectListForAdmin();
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public PageInfo listProductForConsumer(ProductListReq productListReq) {
        //构建商品query
        ProductListQuery query = new ProductListQuery();
        //搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            //有keyword，进行拼接查询字符
            String keyword = new StringBuilder()
                    .append("%").append(productListReq.getKeyword())
                    .append("%").toString();
            query.setKeyword(keyword);
        }
        //目录处理:如果查某个目录下的商品，不仅是需要查找出该目录下的，还需要把所有子目录下的所有商品都查出来
        //所以要拿到一个目录的id的list
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOList = categoryService.listCategoryForConsumer(productListReq.getCategoryId());
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            query.setCategoryIds(categoryIds);
        }
        //排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> productList = productMapper.selectList(query);
        return new PageInfo<>(productList);
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList, List<Integer> categoryIds) {
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (product ==null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.PRODUCT_NOT_EXISTED);
        }
        return product;
    }
}
