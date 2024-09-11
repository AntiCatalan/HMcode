package com.chinasofti.prjboothmdemo;

import com.dao.EnvDao;
import com.server.MainServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@SpringBootApplication
@MapperScan(basePackages = {"com.chinasofti.prjboothmdemo.dao"})






public class PrjboothmdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrjboothmdemoApplication.class, args);
        new MainServer();
    }

}
