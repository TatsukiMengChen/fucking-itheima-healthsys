package com.healthsys.config;

import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;

/**
 * 应用程序上下文管理类
 * 用于管理全局的用户状态和应用程序状态
 * 
 * @author HealthSys Team
 * @since 1.0
 */
public class AppContext {

  private static volatile AppContext instance;
  private static final Object lock = new Object();

  private User currentUser;

  /**
   * 私有构造函数
   */
  private AppContext() {
  }

  /**
   * 获取单例实例
   */
  public static AppContext getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new AppContext();
        }
      }
    }
    return instance;
  }

  /**
   * 设置当前登录用户
   */
  public static void setCurrentUser(User user) {
    getInstance().currentUser = user;
  }

  /**
   * 获取当前登录用户
   */
  public static User getCurrentUser() {
    return getInstance().currentUser;
  }

  /**
   * 获取当前用户角色
   */
  public static UserRoleEnum getCurrentUserRole() {
    User user = getCurrentUser();
    if (user == null || user.getRole() == null) {
      return null;
    }

    try {
      return UserRoleEnum.valueOf(user.getRole());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * 检查当前用户是否有指定角色
   */
  public static boolean hasRole(UserRoleEnum role) {
    UserRoleEnum currentRole = getCurrentUserRole();
    return currentRole != null && currentRole == role;
  }

  /**
   * 检查当前用户是否是管理员（包括超级管理员）
   */
  public static boolean isAdmin() {
    UserRoleEnum role = getCurrentUserRole();
    return role == UserRoleEnum.ADMIN || role == UserRoleEnum.SUPER_ADMIN;
  }

  /**
   * 检查当前用户是否是超级管理员
   */
  public static boolean isSuperAdmin() {
    return hasRole(UserRoleEnum.SUPER_ADMIN);
  }

  /**
   * 检查用户是否已登录
   */
  public static boolean isLoggedIn() {
    return getCurrentUser() != null;
  }

  /**
   * 用户退出登录
   */
  public static void logout() {
    getInstance().currentUser = null;
  }

  /**
   * 清理上下文
   */
  public static void clear() {
    getInstance().currentUser = null;
  }
}