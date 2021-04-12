package com.yeelei.mall.common;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.Set;

/**
 * 描述：常量值
 */
@Controller
public class Constant {
    //盐值
    public static final String SALT = "fdsklajf！kdls32e432?fds.[]{!";
    //用户登录信息
    public static final String YEELEI_MALL_USER = "yeelei_mall_user";
    //上传文件地址
    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    //商品排序
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }
}
