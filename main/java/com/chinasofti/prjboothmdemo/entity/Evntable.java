package com.chinasofti.prjboothmdemo.entity;

import java.io.Serializable;

/**
 * (Evntable)实体类
 *
 * @author makejava
 * @since 2024-03-06 12:51:08
 */
public class Evntable implements Serializable {
    private static final long serialVersionUID = 632453969816850605L;
    /**
     * 主键自增
     */
    private Integer eid;
    /**
     * 类型:温度、湿度、气味
     */
    private String etype;
    /**
     * 值
     */
    private String val;
    /**
     * 备注
     */
    private String note;


    public Integer getEid() {
        return eid;
    }

    public void setEid(Integer eid) {
        this.eid = eid;
    }

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}

