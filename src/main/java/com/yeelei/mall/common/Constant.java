package com.yeelei.mall.common;

import com.google.common.collect.Sets;
import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import com.yeelei.mall.exception.YeeLeiMallExcetion;
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

    //商品上架状态
    public interface SaleStatus {
        //商品下架状态
        int NOT_SALE = 0;
        //商品上架状态
        int SALE = 1;
    }

    //商品选中/未选中状态
    public interface Cart {
        //购物车未选中状态
        int UN_CHECKED = 0;
        //购物车选中状态
        int CHECKED = 1;
    }

    public enum OrderStatusEnum {
        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private int code;
        private String value;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }
        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum:values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new YeeLeiMallExcetion(YeeLeiMallExceptionEnum.NOT_ENUM);
        }
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
