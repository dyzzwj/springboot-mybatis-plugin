package com.dyzwj.springbootmybatisplugin.plugin;

import com.dyzwj.springbootmybatisplugin.util.MyPage;
import com.sun.org.omg.CORBA.ExcDescriptionSeqHelper;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/1 12:06
 * 类说明
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class MyPlugin implements Interceptor {
    private String regex;

    public MyPlugin(String regex) {
        this.regex = regex;
    }

    /**
     * 拦截处理 在StatementHandler.prepare()方法执行之前进行拦截   拦截点在@Signature中指定
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //获取拦截的目标对象
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        //sqlId: namespace + id
        String sqlId = mappedStatement.getId();
        if(sqlId.matches(regex)){

            //获取mybatis中存储sql语句实例的对象
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            String sql = boundSql.getSql();
            //拿到传进来的方法实参
            HashMap<String,Object> param = (HashMap<String, Object>) boundSql.getParameterObject();
            MyPage page = (MyPage) param.get("page");
            //重写sql
            //查count
            String countSql = "select count(*) from (" + sql + ") as temp";
            //分页sql
            String pageSql = sql + " limit " + page.getPageBegin() + ", " + page.getPageSize();

            System.out.println(countSql);
            System.out.println(pageSql);
            //获取数据库连接
            Connection connection = (Connection) invocation.getArgs()[0];
            PreparedStatement preparedStatement=null;
            ResultSet rs=null;
            int totalCount=0;
            try{
                preparedStatement = connection.prepareStatement(countSql);
                //获取mybatis中存储mapper中参数 实例的对象
                ParameterHandler parameterHandler = statementHandler.getParameterHandler();
                parameterHandler.setParameters(preparedStatement);
                rs = preparedStatement.executeQuery();
                if(rs.next()){
                    totalCount = rs.getInt(1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    rs.close();
                    preparedStatement.close();
                }catch (Exception e){

                }
            }
            //替换sql语句
            metaObject.setValue("delegate.boundSql.sql",pageSql);
            page.setNumCount(totalCount);
        }
        //获取mybatis中存储sql语句实例的对象
//        BoundSql boundSql = statementHandler.getBoundSql();
        //执行sql
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
//        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
