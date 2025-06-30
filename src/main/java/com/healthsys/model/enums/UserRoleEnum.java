package com.healthsys.model.enums;

/**
 * 用户角色枚举
 * 定义系统中的用户角色类型
 * 
 * @author AI健康管理系统开发团队
 */
public enum UserRoleEnum {

  /**
   * 普通用户
   */
  NORMAL_USER("NORMAL_USER", "普通用户", "具有基本的健康数据管理和查看权限"),

  /**
   * 管理员
   */
  ADMIN("ADMIN", "管理员", "具有检查项、检查组管理权限，可以管理普通用户"),

  /**
   * 超级管理员
   */
  SUPER_ADMIN("SUPER_ADMIN", "超级管理员", "具有所有权限，可以管理所有用户和系统设置");

  /**
   * 角色代码（数据库存储值）
   */
  private final String code;

  /**
   * 角色名称（显示名称）
   */
  private final String name;

  /**
   * 角色描述
   */
  private final String description;

  /**
   * 构造函数
   * 
   * @param code        角色代码
   * @param name        角色名称
   * @param description 角色描述
   */
  UserRoleEnum(String code, String name, String description) {
    this.code = code;
    this.name = name;
    this.description = description;
  }

  /**
   * 获取角色代码
   * 
   * @return 角色代码
   */
  public String getCode() {
    return code;
  }

  /**
   * 获取角色名称
   * 
   * @return 角色名称
   */
  public String getName() {
    return name;
  }

  /**
   * 获取角色描述
   * 
   * @return 角色描述
   */
  public String getDescription() {
    return description;
  }

  /**
   * 根据代码获取角色枚举
   * 
   * @param code 角色代码
   * @return 对应的角色枚举，如果未找到返回null
   */
  public static UserRoleEnum fromCode(String code) {
    if (code == null) {
      return null;
    }
    for (UserRoleEnum role : UserRoleEnum.values()) {
      if (role.getCode().equals(code)) {
        return role;
      }
    }
    return null;
  }

  /**
   * 检查当前角色是否有管理权限
   * 
   * @return true如果是管理员或超级管理员
   */
  public boolean hasAdminPrivileges() {
    return this == ADMIN || this == SUPER_ADMIN;
  }

  /**
   * 检查当前角色是否有超级管理员权限
   * 
   * @return true如果是超级管理员
   */
  public boolean hasSuperAdminPrivileges() {
    return this == SUPER_ADMIN;
  }

  /**
   * 检查当前角色是否可以管理指定角色
   * 
   * @param targetRole 目标角色
   * @return true如果当前角色可以管理目标角色
   */
  public boolean canManage(UserRoleEnum targetRole) {
    if (targetRole == null) {
      return false;
    }

    switch (this) {
      case SUPER_ADMIN:
        // 超级管理员可以管理所有角色
        return true;
      case ADMIN:
        // 管理员可以管理普通用户，但不能管理其他管理员
        return targetRole == NORMAL_USER;
      case NORMAL_USER:
        // 普通用户不能管理任何角色
        return false;
      default:
        return false;
    }
  }

  @Override
  public String toString() {
    return name;
  }
}