package com.dao;

import com.db.DBHelper;

public class EnvDao {
    public void save(String[] datas){
        String sql="insert into envtable(etype,val) values(?,?)";
        int code=DBHelper.executeUpdate(sql,new String[]{datas[0],datas[1]});
        if(code>0){
            System.out.println("保存成功！");
        }else{
            System.out.println("保存失败！");
        }
    }
}

