package com.yeelei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.exception.YeeLeiMallExcetion;
import com.yeelei.mall.model.dao.CategoryMapper;
import com.yeelei.mall.model.pojo.Category;
import com.yeelei.mall.model.request.AddCategoryReq;
import com.yeelei.mall.model.vo.CategoryVO;
import com.yeelei.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述：商品分类实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category oldCategory = categoryMapper.selectByName(addCategoryReq.getName());
        if (oldCategory != null) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CATEGORY_NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category oldCategory = categoryMapper.selectByName(updateCategory.getName());
            //保证传入的分类id和查出的分类id一致
            if (oldCategory != null && !updateCategory.getId().equals(oldCategory.getId())) {
                throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.CATEGORY_NAME_EXISTED);
            }
            int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
            if (count == 0) {
                throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.UPDATE_FAILED);
            }
        }
    }

    @Override
    public void delete(Integer id) {
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if (categoryOld == null) {
            //查不到记录，删除失败
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize,"type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        return pageInfo;
    }

    @Override
    public List<CategoryVO> listCategoryForConsumer(Integer parentId) {
        ArrayList<CategoryVO> categoryVOS = new ArrayList<>();
        recursivelyFindCategories(categoryVOS,parentId);
        return categoryVOS;
    }

    //递归获取所有子类别,并组合称为一个目录树
    private void recursivelyFindCategories(List<CategoryVO> categoryVOS, Integer parentId) {
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category,categoryVO);
                categoryVOS.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategory(),categoryVO.getId());
            }
        }
    }
}
