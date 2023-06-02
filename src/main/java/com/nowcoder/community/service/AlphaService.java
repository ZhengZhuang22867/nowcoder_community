package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {
    @Autowired
    private AlphaDao alphaDao;

//    @PostConstruct
//    public void init(){
//        System.out.println("initialize");
//    }
//
//    @PreDestroy
//    public void destroy(){
//        System.out.println("destroy");
//    }
//
//    public AlphaService(){
//        System.out.println("AlphaService");
//    }

    public String find(){
        return alphaDao.select();
    }
}
