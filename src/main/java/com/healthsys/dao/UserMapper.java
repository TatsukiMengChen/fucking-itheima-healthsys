package com.healthsys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthsys.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问接口。
 * 提供用户相关的数据库操作方法。
 * 
 * @author 梦辰
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

  /**
   * 根据用户名查找用户
   * 
   * @param username 用户名
   * @return 用户实体，如果未找到返回null
   */
  @Select("SELECT * FROM users WHERE username = #{username}")
  User findByUsername(@Param("username") String username);

  /**
   * 根据邮箱查找用户
   * 
   * @param email 邮箱
   * @return 用户实体，如果未找到返回null
   */
  @Select("SELECT * FROM users WHERE email = #{email}")
  User findByEmail(@Param("email") String email);

  /**
   * 根据角色查找用户列表
   * 
   * @param role 角色
   * @return 用户列表
   */
  @Select("SELECT * FROM users WHERE role = #{role} ORDER BY created_at DESC")
  List<User> findByRole(@Param("role") String role);

  /**
   * 检查用户名是否存在
   * 
   * @param username 用户名
   * @return 存在返回true，否则返回false
   */
  @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username}")
  boolean existsByUsername(@Param("username") String username);

  /**
   * 检查邮箱是否存在
   * 
   * @param email 邮箱
   * @return 存在返回true，否则返回false
   */
  @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email}")
  boolean existsByEmail(@Param("email") String email);

  /**
   * 根据用户名和邮箱查找用户（用于验证）
   * 
   * @param username 用户名
   * @param email    邮箱
   * @return 用户实体，如果未找到返回null
   */
  @Select("SELECT * FROM users WHERE username = #{username} AND email = #{email}")
  User findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);
}