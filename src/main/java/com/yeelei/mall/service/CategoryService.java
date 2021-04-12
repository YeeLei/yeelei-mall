package com.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import com.yeelei.mall.model.pojo.Category;
import com.yeelei.mall.model.request.AddCategoryReq;
import com.yeelei.mall.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category category);

    void delete(Integer id);

    PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForConsumer(Integer parentId);
}
