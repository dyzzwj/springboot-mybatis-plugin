package com.dyzwj.springbootmybatisplugin.mapper;

import com.dyzwj.springbootmybatisplugin.po.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/1 11:29
 * 类说明
 */
@Mapper
public interface UserMapper {

    List<User> selectAllByPage(Map<String,Object> param);

    int insert(User user);
}
