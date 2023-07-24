/**
 * Copyright (C) 2016-Now 思程科技 All rights reserved.
 * 思程科技(北京)有限公司对本内容拥有著作权，禁止外泄，禁止未经授权使用。
 */
package com.sicheng.common.persistence.wrapper;

/**
 * <p>标题: WrapperException</p>
 * <p>描述: Wrapper异常类</p>
 * <p>公司: 思程科技 www.sicheng.net</p>
 *
 * @author zhaolei
 * @date 2017年1月31日 下午9:55:22
 */
public class WrapperException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WrapperException(String message) {
        super(message);
    }

    public WrapperException(Throwable throwable) {
        super(throwable);
    }

    public WrapperException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
