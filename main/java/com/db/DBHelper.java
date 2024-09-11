package com.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class DBHelper {
    private static Connection conn;
    private static PreparedStatement pst;
    private static ResultSet rst;

    /**
     * 通用的增删改方法
     * @param sql 传递的sql语句，要执行的sql语句
     * @param obs 传递的参数值，用于替代sql中的?
     *  */
    public static int  executeUpdate(String sql,Object[] obs){
        //获取数据库连接对象
        conn=DBConnection.getConn();
        try {
            //获取语句执行器对象
            pst=conn.prepareStatement(sql);
            /********************************************/
            //判断是否有参数传递
            if(obs!=null&&obs.length>0){
                //使用pst对象为sql语句的？赋值
                for (int i=0;i<obs.length;i++){
                    pst.setObject(i+1,obs[i]);//?对应的索引位置的值
                }
            }
            /*******************************************/
            //执行sql
            int code=pst.executeUpdate();
            return code;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBConnection.closeAll(rst,pst,conn);//关闭数据库
        }

        return -1;
    }
    /**
     * 通用的查询方法
     * @param sql 传递的sql语句，要执行的sql语句
     * @param obs 传递的参数值，用于替代sql中的?
     *  */
    public static ResultSet  executeQuery(String sql,Object[] obs){
        //获取数据库连接对象
        conn=DBConnection.getConn();
        try {
            //获取语句执行器对象
            pst=conn.prepareStatement(sql);
            /********************************************/
            //判断是否有参数传递
            if(obs!=null&&obs.length>0){
                //使用pst对象为sql语句的？赋值
                for (int i=0;i<obs.length;i++){
                    pst.setObject(i+1,obs[i]);//?对应的索引位置的值
                }
            }
            /*******************************************/
            //执行sql
            rst=pst.executeQuery();
            return rst;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rst;
    }

}
