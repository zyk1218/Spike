package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.UserDao;
import com.imooc.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;


    public User getById(int id){
        return userDao.getById(id);
    }


    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(12);
        u1.setName("test");
        userDao.insert(u1);
        User u2 = new User();
        u2.setId(11);
        u2.setName("testtx");
        userDao.insert(u2);
        return true;
    }


}
