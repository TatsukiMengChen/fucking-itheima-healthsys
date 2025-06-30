package com.healthsys.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 数据库配置类。
 * 配置数据源和 MyBatis 相关参数。
 * 
 * @author 梦辰
 */
public class DatabaseConfig {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
  private static volatile DataSource dataSource;
  private static final Object lock = new Object();

  /**
   * 获取数据源实例（单例模式）
   * 
   * @return DataSource 数据源
   */
  public static DataSource getDataSource() {
    if (dataSource == null) {
      synchronized (lock) {
        if (dataSource == null) {
          dataSource = createDataSource();
        }
      }
    }
    return dataSource;
  }

  /**
   * 创建数据源
   * 
   * @return DataSource 数据源
   */
  private static DataSource createDataSource() {
    try {
      Properties props = loadProperties();

      HikariConfig config = new HikariConfig();

      // 基本连接信息
      config.setJdbcUrl(props.getProperty("spring.datasource.url",
          "jdbc:postgresql://localhost:5432/health_management_system"));
      config.setUsername(props.getProperty("spring.datasource.username", "postgres"));
      config.setPassword(props.getProperty("spring.datasource.password", "password"));
      config.setDriverClassName(props.getProperty("spring.datasource.driver-class-name",
          "org.postgresql.Driver"));

      // 连接池配置
      config
          .setMaximumPoolSize(Integer.parseInt(props.getProperty("spring.datasource.hikari.maximum-pool-size", "20")));
      config.setMinimumIdle(Integer.parseInt(props.getProperty("spring.datasource.hikari.minimum-idle", "5")));
      config.setConnectionTimeout(
          Long.parseLong(props.getProperty("spring.datasource.hikari.connection-timeout", "30000")));
      config.setIdleTimeout(Long.parseLong(props.getProperty("spring.datasource.hikari.idle-timeout", "600000")));
      config.setMaxLifetime(Long.parseLong(props.getProperty("spring.datasource.hikari.max-lifetime", "1800000")));

      // 连接池名称
      config.setPoolName("HealthSysHikariCP");

      // 连接测试查询
      config.setConnectionTestQuery("SELECT 1");

      logger.info("数据库连接池配置完成");
      return new HikariDataSource(config);

    } catch (Exception e) {
      logger.error("创建数据源失败", e);
      throw new RuntimeException("数据库配置初始化失败", e);
    }
  }

  /**
   * 加载配置文件
   * 
   * @return Properties 配置属性
   */
  private static Properties loadProperties() {
    Properties props = new Properties();
    try (InputStream is = DatabaseConfig.class.getClassLoader()
        .getResourceAsStream("application.properties")) {
      if (is != null) {
        props.load(is);
        logger.info("成功加载应用配置文件");
      } else {
        logger.warn("未找到application.properties文件，使用默认配置");
      }
    } catch (IOException e) {
      logger.warn("加载配置文件失败，使用默认配置", e);
    }
    return props;
  }

  /**
   * 关闭数据源
   */
  public static void closeDataSource() {
    if (dataSource instanceof HikariDataSource) {
      ((HikariDataSource) dataSource).close();
      logger.info("数据库连接池已关闭");
    }
  }
}