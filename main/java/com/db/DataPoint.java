package com.db;

public class DataPoint {
    private float wendu;
    private float shidu;
    private int qiwei;

    // 构造函数
    public DataPoint(float wendu, float shidu, int qiwei) {
        this.wendu = wendu;
        this.shidu = shidu;
        this.qiwei = qiwei;
    }

    public float getWendu() {
        return wendu;
    }

    public float getShidu() {
        return shidu;
    }

    public int getQiwei() {
        return qiwei;
    }
}
