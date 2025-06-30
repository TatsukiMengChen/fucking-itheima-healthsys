package com.healthsys.service;

import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;

/**
 * 用户服务接口。
 * 定义用户相关的业务操作。
 * 
 * @author 梦辰
 */
public interface IUserService {

  /**
   * 用户注册
   * 
   * @param username         用户名
   * @param password         密码（明文，方法内部会进行加密）
   * @param email            邮箱
   * @param verificationCode 验证码
   * @return 注册结果，true表示成功，false表示失败
   * @throws RuntimeException 当用户名已存在或验证码不正确时抛出异常
   */
  boolean register(String username, String password, String email, String verificationCode);

  /**
   * 用户登录
   * 
   * @param username 用户名
   * @param password 密码（明文）
   * @return 登录成功返回用户对象，失败返回null
   */
  User login(String username, String password);

  /**
   * 根据用户ID获取用户信息
   * 
   * @param userId 用户ID
   * @return 用户对象，不存在返回null
   */
  User getUserById(Integer userId);

  /**
   * 根据用户名获取用户信息
   * 
   * @param username 用户名
   * @return 用户对象，不存在返回null
   */
  User getUserByUsername(String username);

  /**
   * 检查用户名是否已存在
   * 
   * @param username 用户名
   * @return true表示已存在，false表示不存在
   */
  boolean isUsernameExists(String username);

  /**
   * 检查邮箱是否已存在
   * 
   * @param email 邮箱
   * @return true表示已存在，false表示不存在
   */
  boolean isEmailExists(String email);

  /**
   * 添加或更新用户（管理员功能）
   * 
   * @param user 用户对象
   * @return 操作结果，true表示成功
   */
  boolean addOrUpdateUser(User user);

  /**
   * 删除用户（管理员功能）
   * 
   * @param userId 用户ID
   * @return 操作结果，true表示成功
   */
  boolean deleteUser(Integer userId);

  /**
   * 分页查询用户（管理员功能）
   * 
   * @param searchKeyword 搜索关键词（用户名、邮箱等）
   * @param page          页码（从1开始）
   * @param size          每页大小
   * @return 用户列表
   */
  java.util.List<User> queryUsers(String searchKeyword, int page, int size);
}