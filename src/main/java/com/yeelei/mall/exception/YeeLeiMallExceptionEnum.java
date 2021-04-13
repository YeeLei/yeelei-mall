package com.yeelei.mall.exception;

/**
 * 描述：异常枚举
 */
public enum YeeLeiMallExceptionEnum {
    NEED_USER_NAME(10001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TO_SHORT(10003, "密码不能少于8位"),
    USER_NAME_EXISTED(10004, "用户名已存在，注册失败"),
    INSERT_FAILED(10005, "注册失败，请重试"),
    WRONG_PASSWORD(10006, "密码错误"),
    NEED_LOGIN(10007, "请登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009,"无管理员权限"),
    CATEGORY_NAME_EXISTED(10010,"商品分类名称已存在"),
    CREATE_FAILED(10011,"添加失败"),
    DELETE_FAILED(10012,"删除失败"),
    REQUEST_PARAM_ERROR(10013,"参数错误"),
    PRODUCT_NAME_EXISTED(10014,"商品名称已存在"),
    MAKDIR_FAILED(10015,"创建文件夹失败"),
    UPLOAD_FAILED(10016,"上传文件失败"),
    PRODUCT_NOT_EXISTED(10017,"商品不存在"),
    NOT_SALE(10018,"商品状态不可售"),
    NOT_ENOUGH(10019,"商品库存不足"),
    CART_EMPTY(10020,"购物车为空"),
    NOT_ENUM(10021,"未找到对应的枚举类"),
    NO_ORDER(10022,"未找到此订单"),
    CANCEL_ERROR(10022,"取消订单失败"),
    WRONG_ORDER_STATUS(10023,"订单状态不符"),
    SYSTEM_ERROR(20000, "系统异常");

    private Integer code;
    private String message;

    YeeLeiMallExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
