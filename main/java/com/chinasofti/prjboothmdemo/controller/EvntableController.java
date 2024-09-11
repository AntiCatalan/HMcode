package com.chinasofti.prjboothmdemo.controller;


import com.chinasofti.prjboothmdemo.entity.Evntable;
import com.chinasofti.prjboothmdemo.service.EvntableService;

import com.dao.EnvDao;
import com.db.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



/**
 * (Evntable)表控制层
 *
 * @author makejava
 * @since 2024-03-06 12:51:10
 */
@RestController
public class EvntableController {
    /**
     * 服务对象
     */
    static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(8001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private EnvDao envDao=new EnvDao();
    @Autowired
    private EvntableService evntableService;

    /**
     * 分页查询
     *
     * @param evntable 筛选条件
     * @param page pagesize    分页对象
     * @return 查询结果
     */
    @RequestMapping(value="queryByPage_Evntable")
    public Map<String,Object> queryByPage(Evntable evntable, Integer page, Integer pagesize) {
        return null;
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @RequestMapping(value="queryById_Evntable")
    public Evntable queryById(Integer id) {

        return this.evntableService.queryById(id);
    }

    /**
     * 新增数据
     *
     * @param evntable 实体
     * @return 新增结果
     */
    @RequestMapping(value="save_Evntable")
    public int save(Evntable evntable) {
        return 0;
    }

    /**
     * 编辑数据
     *
     * @param evntable 实体
     * @return 编辑结果
     */
    @RequestMapping(value="update_Evntable")
    public int update(Evntable evntable) {
        return 0;
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @RequestMapping(value="deleteById_Evntable")
    public int deleteById(Integer id) {
        return 0;
    }

    @RequestMapping(value="findGroupVlaue_Evntable")
    public List<Map<String, Object>> findGroupVlaue(){
        System.out.println("test stage1");
        List<Map<String, Object>> mapList=evntableService.findGroupVlaue();
        List<Map<String,Object>> listmapresult=new ArrayList<>();
        mapList.forEach(map->{
            Map<String,Object> mapresult=new HashMap<>();
            //获取类别名称
            String tname= (String) map.get("etype");
            mapresult.put("name",tname);
            Double eval= (Double) map.get("enval");
            mapresult.put("value",eval);
            listmapresult.add(mapresult);
        });
        System.out.println("test stage 2");
        return listmapresult;
    }

    @RequestMapping(value="click_send")
    public void click_send(String param) {
        System.out.println("test");
        try {
            // 创建服务器套接字对象

            while (true){
                //监听客户端请求的消息，并获取套接字过道对象
                socket=serverSocket.accept();


                outputStream=socket.getOutputStream();
                String sendmsg=param;
                outputStream.write(sendmsg.getBytes());
                outputStream.flush();
                socket.shutdownOutput();//结束输出

                //------接收客户端的消息-------------------------
                inputStream=socket.getInputStream();//从套接字获取输入流对象
                byte[] b=new byte[1024];
                inputStream.read(b);//从管道读取数据给字节数组
                String restr=new String(b).trim();

                socket.shutdownInput();//结束输入


                if(restr.equals("end")) {
                    socket.close();
                    System.out.println(restr);
                    return;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="show_wendu")
    public List<Float> show_wendu() {
        List<Float> mapList=evntableService.findLastWendu();
        List<Float> ret=new ArrayList<>();
        ret.addAll(mapList);
        Collections.reverse(ret);
        // System.out.println(mapList);
        return ret;
    }

    @RequestMapping(value="show_shidu")
    public List<Float> show_shidu() {
        System.out.println("test stage 1");
        List<Float> mapList=evntableService.findLastShidu();
        List<Float> ret=new ArrayList<>();
        ret.addAll(mapList);
        Collections.reverse(ret);
        System.out.println("test stage 2");
        // System.out.println(mapList);
        return ret;
    }

    @RequestMapping(value="show_qiwei")
    public List<Float> show_qiwei() {
        List<Float> mapList=evntableService.findLastQiwei();
        List<Float> ret=new ArrayList<>();
        ret.addAll(mapList);
        Collections.reverse(ret);
        // System.out.println(mapList);
        return ret;
    }

    @RequestMapping(value="show_guangqiang")
    public List<Float> show_guangqiang() {
        List<Float> mapList=evntableService.findLastGuangqiang();
        List<Float> ret=new ArrayList<>();
        ret.addAll(mapList);
        Collections.reverse(ret);
        // System.out.println(mapList);
        return ret;
    }

    @RequestMapping(value="show_rightnow")
    public List<Float> show_rightnow() {
        List<Float> mapList=evntableService.findRightnow();
        List<Float> ret=new ArrayList<>();
        ret.addAll(mapList);
        // System.out.println(mapList);
        return ret;
    }

    @RequestMapping(value="change")
    public void change(String param) {
        System.out.println(param);
    }

}

