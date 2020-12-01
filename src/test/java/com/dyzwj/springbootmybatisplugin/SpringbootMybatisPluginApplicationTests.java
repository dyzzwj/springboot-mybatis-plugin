package com.dyzwj.springbootmybatisplugin;

import com.dyzwj.springbootmybatisplugin.mapper.UserMapper;
import com.dyzwj.springbootmybatisplugin.po.User;
import com.dyzwj.springbootmybatisplugin.util.MyPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.processor.ObjectRowListProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SpringbootMybatisPluginApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    UserMapper userMapper;

    @Test
    public void test1() {
        Map<String, Object> param = new HashMap<>();
        param.put("page", new MyPage(2, 10));
        userMapper.selectAllByPage(param).forEach(System.out::println);
        System.out.println(param.get("page"));
    }

    @Test
    public void test2() {
        for (int i = 0; i < 200; i++) {
            userMapper.insert(new User("zwj" + i, "11111" + i));
        }
    }


}
