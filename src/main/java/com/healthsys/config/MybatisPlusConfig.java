package com.healthsys.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * MyBatis-Plus 配置类。
 * 配置 MyBatis-Plus 相关参数。
 * 
 * @author 梦辰
 */
public class MybatisPlusConfig {

  private static final Logger logger = LoggerFactory.getLogger(MybatisPlusConfig.class);

  /**
   * 创建SqlSessionFactory
   * 
   * @return SqlSessionFactory
   */
  public static SqlSessionFactory createSqlSessionFactory() {
    try {
      DataSource dataSource = DatabaseConfig.getDataSource();

      // MyBatis配置
      MybatisConfiguration configuration = new MybatisConfiguration();
      configuration.setMapUnderscoreToCamelCase(true);
      configuration.setCacheEnabled(true);
      configuration.setLazyLoadingEnabled(true);
      configuration.setUseGeneratedKeys(true);

      // 设置数据源
      org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment("development",
          new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory(),
          dataSource);
      configuration.setEnvironment(environment);

      // 设置实体类别名包
      configuration.getTypeAliasRegistry().registerAliases("com.healthsys.model.entity");

      // 全局配置
      GlobalConfig globalConfig = new GlobalConfig();
      GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
      dbConfig.setLogicDeleteField("deleted");
      dbConfig.setLogicDeleteValue("1");
      dbConfig.setLogicNotDeleteValue("0");
      globalConfig.setDbConfig(dbConfig);

      // 插件配置
      MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
      interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
      configuration.addInterceptor(interceptor);

      // 注册Mapper接口
      configuration.addMapper(com.healthsys.dao.UserMapper.class);
      configuration.addMapper(com.healthsys.dao.CheckItemMapper.class);
      configuration.addMapper(com.healthsys.dao.CheckGroupMapper.class);
      configuration.addMapper(com.healthsys.dao.AppointmentMapper.class);
      configuration.addMapper(com.healthsys.dao.ExaminationResultMapper.class);
      configuration.addMapper(com.healthsys.dao.MedicalHistoryMapper.class);

      SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
      logger.info("MyBatis-Plus配置完成");
      return factory;

    } catch (Exception e) {
      logger.error("创建SqlSessionFactory失败", e);
      throw new RuntimeException("MyBatis-Plus配置初始化失败", e);
    }
  }
}