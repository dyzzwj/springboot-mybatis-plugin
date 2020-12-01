package com.dyzwj.springbootmybatisplugin.config;

import com.dyzwj.springbootmybatisplugin.plugin.MyPlugin;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/1 14:32
 * 类说明
 */
@Configuration
public class MybatisConfig {

    @Autowired
    private DataSource dataSource;

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactory() throws IOException {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        // 能加载多个，所以可以配置通配符(如：classpath*:mapper/**/*.xml)
//        sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(mapperLocations));        Interceptor[] intercepts = new Interceptor[1];
//        intercepts[0]  = plugin();
//        sqlSessionFactoryBean.setPlugins();
//        return sqlSessionFactoryBean;
//    }

    @Bean
    public MyPlugin plugin(){
        return new MyPlugin(".*Page$");
    }
}
