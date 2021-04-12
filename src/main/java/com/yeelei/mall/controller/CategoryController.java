package com.yeelei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.yeelei.mall.common.ApiRestResponse;
import com.yeelei.mall.model.pojo.Category;
import com.yeelei.mall.model.request.AddCategoryReq;
import com.yeelei.mall.model.request.UpdateCategoryReq;
import com.yeelei.mall.model.vo.CategoryVO;
import com.yeelei.mall.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 描述：商品分类Controller
 */
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    public ApiRestResponse add(@RequestBody @Valid AddCategoryReq addCategoryReq) {
        categoryService.add(addCategoryReq);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    public ApiRestResponse update(@RequestBody @Valid UpdateCategoryReq updateCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReq,category);
        categoryService.update(category);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    public ApiRestResponse delete(@RequestParam Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @GetMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listCategoryForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @GetMapping("/category/list")
    public ApiRestResponse listCategoryForConsumer() {
        List<CategoryVO> categoryVOS = categoryService.listCategoryForConsumer(0);
        return ApiRestResponse.success(categoryVOS);
    }
}
