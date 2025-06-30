package com.healthsys.config;

import com.healthsys.dao.UserMapper;
import com.healthsys.dao.CheckItemMapper;
import com.healthsys.dao.CheckGroupMapper;
import com.healthsys.dao.AppointmentMapper;
import com.healthsys.dao.MedicalHistoryMapper;
import com.healthsys.dao.ExaminationResultMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据访问管理器。
 * 统一管理数据源和 Mapper 实例。
 * 
 * @author 梦辰
 */
public class DataAccessManager {

  private static final Logger logger = LoggerFactory.getLogger(DataAccessManager.class);
  private static volatile DataAccessManager instance;
  private static final Object lock = new Object();

  private SqlSessionFactory sqlSessionFactory;
  private SqlSession sqlSession;

  /**
   * 私有构造函数
   */
  private DataAccessManager() {
    initializeMyBatis();
  }

  /**
   * 获取单例实例
   */
  public static DataAccessManager getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new DataAccessManager();
        }
      }
    }
    return instance;
  }

  /**
   * 初始化MyBatis配置
   */
  private void initializeMyBatis() {
    try {
      logger.info("开始初始化MyBatis配置...");

      // 使用MybatisPlusConfig创建SqlSessionFactory
      sqlSessionFactory = MybatisPlusConfig.createSqlSessionFactory();

      // 创建SqlSession
      sqlSession = sqlSessionFactory.openSession(true); // 自动提交事务

      logger.info("MyBatis配置初始化成功");

    } catch (Exception e) {
      logger.error("MyBatis配置初始化失败: {}", e.getMessage(), e);
      throw new RuntimeException("数据访问层初始化失败", e);
    }
  }

  /**
   * 获取UserMapper实例
   */
  public UserMapper getUserMapper() {
    if (sqlSession == null) {
      throw new RuntimeException("SqlSession未初始化");
    }
    return sqlSession.getMapper(UserMapper.class);
  }

  /**
   * 获取CheckItemMapper实例
   */
  public CheckItemMapper getCheckItemMapper() {
    if (sqlSession == null) {
      throw new RuntimeException("SqlSession未初始化");
    }
    return sqlSession.getMapper(CheckItemMapper.class);
  }

  /**
   * 获取CheckGroupMapper实例
   */
  public CheckGroupMapper getCheckGroupMapper() {
    if (sqlSession == null) {
      throw new RuntimeException("SqlSession未初始化");
    }
    return sqlSession.getMapper(CheckGroupMapper.class);
  }

  /**
   * 获取AppointmentMapper实例
   */
  public AppointmentMapper getAppointmentMapper() {
    if (sqlSession == null) {
      throw new RuntimeException("SqlSession未初始化");
    }
    return sqlSession.getMapper(AppointmentMapper.class);
  }

  /**
   * 获取MedicalHistoryMapper实例
   */
  public MedicalHistoryMapper getMedicalHistoryMapper() {
    if (sqlSession == null) {
      throw new RuntimeException("SqlSession未初始化");
    }
    return sqlSession.getMapper(MedicalHistoryMapper.class);
  }

  /**
   * 获取ExaminationResultMapper实例
   */
  public ExaminationResultMapper getExaminationResultMapper() {
    if (sqlSession == null) {
      throw new RuntimeException("SqlSession未初始化");
    }
    return sqlSession.getMapper(ExaminationResultMapper.class);
  }

  /**
   * 获取SqlSession
   */
  public SqlSession getSqlSession() {
    return sqlSession;
  }

  /**
   * 获取SqlSessionFactory
   */
  public SqlSessionFactory getSqlSessionFactory() {
    return sqlSessionFactory;
  }

  /**
   * 测试数据库连接
   */
  public boolean testConnection() {
    try {
      if (sqlSession != null) {
        // 通过连接对象执行简单查询来测试连接
        sqlSession.getConnection().createStatement().executeQuery("SELECT 1").close();
        logger.info("数据库连接测试成功");
        return true;
      }
    } catch (Exception e) {
      logger.error("数据库连接测试失败: {}", e.getMessage());
    }
    return false;
  }

  /**
   * 关闭资源
   */
  public void close() {
    try {
      if (sqlSession != null) {
        sqlSession.close();
        logger.info("SqlSession已关闭");
      }
    } catch (Exception e) {
      logger.error("关闭SqlSession时发生错误: {}", e.getMessage());
    }
  }

  /**
   * 重新初始化（用于配置更新后的重新加载）
   */
  public void reinitialize() {
    synchronized (lock) {
      close();
      initializeMyBatis();
      logger.info("数据访问管理器已重新初始化");
    }
  }

  // 静态方法，便于在Service中调用
  public static UserMapper getUserMapperStatic() {
    return getInstance().getUserMapper();
  }

  public static CheckItemMapper getCheckItemMapperStatic() {
    return getInstance().getCheckItemMapper();
  }

  public static CheckGroupMapper getCheckGroupMapperStatic() {
    return getInstance().getCheckGroupMapper();
  }

  public static AppointmentMapper getAppointmentMapperStatic() {
    return getInstance().getAppointmentMapper();
  }

  public static MedicalHistoryMapper getMedicalHistoryMapperStatic() {
    return getInstance().getMedicalHistoryMapper();
  }

  public static ExaminationResultMapper getExaminationResultMapperStatic() {
    return getInstance().getExaminationResultMapper();
  }
}