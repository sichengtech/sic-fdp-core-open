/**
 * SiC B2B2C Shop 使用 木兰公共许可证,第2版（Mulan PubL v2） 开源协议，请遵守相关条款，或者联系sicheng.net获取商用授权书。
 * Copyright (c) 2016 SiCheng.Net
 * SiC B2B2C Shop is licensed under Mulan PubL v2.
 * You can use this software according to the terms and conditions of the Mulan PubL v2.
 * You may obtain a copy of Mulan PubL v2 at:
 *          http://license.coscl.org.cn/MulanPubL-2.0
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PubL v2 for more details.
 */
package com.sicheng.common.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sicheng.admin.sys.entity.User;
import com.sicheng.common.utils.IdWorker;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 数据Entity类
 *
 * @author zhaolei
 * @version 2014-05-16
 */
public abstract class DataEntity<T> extends BaseEntity {

    private static final long serialVersionUID = 1L;

    protected String remarks;    // 备注
    protected User createBy;    // 创建者
    protected Date createDate;    // 创建日期
    protected User updateBy;    // 更新者
    protected Date updateDate;    // 更新日期
    protected String delFlag = DEL_FLAG_NORMAL;    // 删除标记（0：正常；1：删除；2：审核）

    public DataEntity() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public DataEntity(Long id) {
        super(id);
        this.delFlag = DEL_FLAG_NORMAL;
    }

    /**
     * 插入之前执行方法，需要手动调用
     * 给非Admin系统使用
     */
    public void preInsert() {
        preInsert(null);
    }

    /**
     * 插入之前执行方法，需要手动调用
     * 给Admin系统专用
     */
    public void preInsert(User user) {
        //主键生成策略
        //1表示由开发人员来指定主键，getIsNewRecord()==true表示，
        //开发人员未指定主键，这里由程序自动生成一个18位的数值类型的主键。
        if (getPkMode() == 1 && getIsNewRecord()) {
            //使用snowflake算法的JAVA版本来生成ID主键
            setId(IdWorker.getId());
        }
        if (user != null && user.getId() != null) {
            this.updateBy = user;
            this.createBy = user;
        }
        this.updateDate = new Date();
        this.createDate = this.updateDate;//新插入时，两个日期相同
    }

    /**
     * 更新之前执行方法，需要手动调用
     * 给非Admin系统使用
     */
    public void preUpdate() {
        preUpdate(null);
    }

    /**
     * 更新之前执行方法，需要手动调用
     * 给Admin系统专用
     */
    public void preUpdate(User user) {
        if (user != null && user.getId() != null) {
            this.updateBy = user;
        }
        this.updateDate = new Date();
    }

    @Length(min = 0, max = 255)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonIgnore
    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonIgnore
    public User getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @JsonIgnore
    @Length(min = 1, max = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

}
