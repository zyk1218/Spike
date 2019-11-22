package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * 第一次使用这个Mapper的时候发现无法导入注解，原因是依赖中没有声明版本。
 */
@Mapper
@Component
public interface UserDao {
    @Select("select * from user where id = #{id}")
    User getById(@Param("id") int id);

    @Select("select * from user where name = #{name}")
    User getByName(@Param("name") String name);

    @Insert("insert into user(id,name) values(#{id},#{name})")
    int insert(User user);
}
