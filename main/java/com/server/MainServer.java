package com.server;

import com.dao.EnvDao;
import com.db.DBHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainServer {
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private EnvDao envDao=new EnvDao();

    public MainServer(){
        try {
            // 创建服务器套接字对象
            serverSocket=new ServerSocket(5001);
            System.out.println("服务器已经启动，请勿关闭......");
            while (true){
                //监听客户端请求的消息，并获取套接字过道对象
                socket=serverSocket.accept();
                //------接收客户端的消息-------------------------
                inputStream=socket.getInputStream();//从套接字获取输入流对象
                byte[] b=new byte[1024];
                inputStream.read(b);//从管道读取数据给字节数组
                //将字节数组转为字符串输出
                String restr=new String(b);
                System.out.println("received:"+restr.trim());
                //将传递的数据转为字符串数组
                String [] valuedatas=restr.trim().split(",");

                System.out.println("温度:"+valuedatas[0]);
                System.out.println("湿度:"+valuedatas[1]);
                System.out.println("气味:"+valuedatas[2]);
                System.out.println("光强:"+valuedatas[3]);
                String [] wenduvals={"温度",valuedatas[0]};
                String [] shiduvals={"湿度",valuedatas[1]};
                String [] qiweivals={"气味",valuedatas[2]};
                String [] guangqiangvals={"光强",valuedatas[3]};

                envDao.save(wenduvals);
                envDao.save(shiduvals);
                envDao.save(qiweivals);
                envDao.save(guangqiangvals);

                Thread.sleep(1000);//每过1秒钟，采集存储一次


                socket.shutdownInput();//结束输入
                //------------------------------------------------
                //===========向客户端hi3861版发出相应消息----------

                Integer qiwei = Integer.parseInt(valuedatas[2]);
                Integer guangqiang = Integer.parseInt(valuedatas[3]);
                Double wendu = Double.parseDouble(valuedatas[0]);
                Double shidu = Double.parseDouble(valuedatas[1]);
                String sendmsg="basic data received";

                if(wendu >= 35 || shidu <= 25 || qiwei >= 1000 || guangqiang >= 800 || guangqiang <= 200){
                    sendmsg = "error";
                }

                outputStream=socket.getOutputStream();
                outputStream.write(sendmsg.getBytes());
                outputStream.flush();
                socket.shutdownOutput();//结束输出


//                try{
//                    String sql = "SELECT etype ,ROUND(AVG(val),2) enval FROM envtable GROUP BY etype";
//                    String [] obs={};
//                    ResultSet resultSet=DBHelper.executeQuery(sql, obs);
//                    System.out.println("stage 1");
//                    while (resultSet.next()) {
//                        float id = resultSet.getFloat("enval"); // 假设查询结果中有名为 id 的整型字段
//                        String name = resultSet.getString("etype"); // 假设查询结果中有名为 name 的字符串字段
//
//                        // 进行进一步处理，比如打印数据
//                        System.out.println("ID: " + id + ", Name: " + name);
//                    }
//                    System.out.println("stage 2");
//                    resultSet.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new MainServer();
    }
}
