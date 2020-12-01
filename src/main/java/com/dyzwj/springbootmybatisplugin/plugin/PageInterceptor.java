package com.dyzwj.springbootmybatisplugin.plugin;

import com.dyzwj.springbootmybatisplugin.util.Pageable;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/1 15:51
 * 类说明
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class PageInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取数据库连接 StatementHandler.prepare()方法的第一个参数就是Connection
        Connection connection = (Connection) invocation.getArgs()[0];
        //获取拦截的目标对象StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //获取mybatis中存储sql语句实例的对象
        BoundSql boundSql = statementHandler.getBoundSql();
        //获取mybatis中存储mapper中参数 实例的对象
        ParameterHandler parameterHandler = statementHandler.getParameterHandler();
        Optional.ofNullable(getPageable(statementHandler)).ifPresent(pageable -> {
            //设置分页总数
            setPageTotal(connection, boundSql, pageable, parameterHandler);
            //设置分页sql语句
            setSqlStatement(pageable, boundSql);

        });

        //执行拦截的方法 并返回
        return invocation.proceed();
    }

    /**
     * 设置哪些Mybatis对象需要被该插件拦截
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        if(target instanceof StatementHandler){
            if (getPageable((StatementHandler)target) != null){
                return Plugin.wrap(target,this);
            }
        }
        return target;
    }

    private void setSqlStatement(Pageable pageable, BoundSql boundSql) {
        int start = (pageable.getPage() - 1) * pageable.getSize();
        int size = pageable.getSize();
        String pageSql = boundSql.getSql() + " limit " + start + "," + size;
        System.out.println(pageSql);
        MetaObject metaObject =
                MetaObject.forObject(boundSql, SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                        SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        metaObject.setValue("sql", pageSql);
    }

    /**
     * 获取方法中传递归来的Pageable对象 没有则返回null
     *
     * @param statementHandler
     * @return
     */
    private Pageable getPageable(StatementHandler statementHandler) {
        //去的传递过来的实参
        Object parameterObject = statementHandler.getParameterHandler().getParameterObject();

        //方式一：如果只有一个参数
        if (parameterObject instanceof Pageable) {
            return (Pageable) parameterObject;
        }

        //方式二：如果有多个参数，则会封装成map
        if (parameterObject instanceof Map) {
            for (Object value : ((Map) parameterObject).values()) {
                if (value instanceof Pageable) {
                    return (Pageable) value;
                }
            }
        }
        return null;
    }

    private void setPageTotal(Connection connection, BoundSql boundSql, Pageable pageable, ParameterHandler parameterHandler) {
        String countSql = convertToCountSql(boundSql.getSql());
        System.out.println(countSql);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(countSql);
            //利用mybatis原生方法 对sql语句设置参数
            parameterHandler.setParameters(preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                pageable.setTotal(resultSet.getInt(1));
                pageable.setTotalPage((int) Math.ceil((double) pageable.getTotal() / (double) pageable.getSize()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 将原有语句转化成 统计数量的语句 select XX From XXXXXXXX -> select count(*) From XXXXXXXX
     *
     * @param originalSql
     * @return
     */
    private String convertToCountSql(String originalSql) {
        originalSql = originalSql.toLowerCase();
        StringBuilder countSql = new StringBuilder("select count(*) ");
        String[] split = originalSql.split("from");
        split[0] = "";
        boolean flag = true;
        for (String s : split) {
            if (flag) {
                flag = false;
                countSql.append(s);
            } else {
                countSql.append("from").append(s);
            }
        }
        return countSql.toString();
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
