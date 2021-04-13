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
import java.util.List;

/**
 * 描述：商品分类实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加商品分类
     *
     * @param addCategoryReq 商品分类请求参数
     */
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

    /**
     * 更新商品分类
     *
     * @param updateCategory 更新商品分类请求参数
     */
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

    /**
     * 根据id删除商品分类
     *
     * @param id 商品分类id
     */
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

    /**
     * 商品分类后台列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示的条数
     * @return 后台分类列表
     */
    @Override
    public PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        return pageInfo;
    }

    /**
     * 根据parentId查询前台分类商品列表
     *
     * @param parentId
     * @return 前台分类列表
     */
    @Override
    public List<CategoryVO> listCategoryForConsumer(Integer parentId) {
        ArrayList<CategoryVO> categoryVOS = new ArrayList<>();
        recursivelyFindCategories(categoryVOS, parentId);
        return categoryVOS;
    }

    /**
     * 递归获取所有子类别,并组合称为一个目录树
     *
     * @param categoryVOS
     * @param parentId
     */
    private void recursivelyFindCategories(List<CategoryVO> categoryVOS, Integer parentId) {
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOS.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
