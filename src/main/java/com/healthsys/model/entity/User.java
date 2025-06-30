package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库users表
 * 
 * @author AI健康管理系统开发团队
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("users")
public class User {

  /**
   * 用户ID（主键，自增）
   */
  @TableId(value = "user_id", type = IdType.AUTO)
  private Integer userId;

  /**
   * 唯一用户名
   */
  @TableField("username")
  private String username;

  /**
   * 哈希密码
   */
  @TableField("password")
  private String password;

  /**
   * 用户电子邮件
   */
  @TableField("email")
  private String email;

  /**
   * 姓名
   */
  @TableField("uname")
  private String uname;

  /**
   * 电话号码
   */
  @TableField("tel")
  private String tel;

  /**
   * 性别
   */
  @TableField("sex")
  private String sex;

  /**
   * 出生日期
   */
  @TableField("bir")
  private LocalDate bir;

  /**
   * 身份证号码
   */
  @TableField("idcard")
  private String idcard;

  /**
   * 家庭住址
   */
  @TableField("address")
  private String address;

  /**
   * 科室
   */
  @TableField("dep")
  private String dep;

  /**
   * 级别/职称
   */
  @TableField("lev")
  private String lev;

  /**
   * 用户头像路径
   */
  @TableField("avatar")
  private String avatar;

  /**
   * 用户角色
   */
  @TableField("role")
  private String role;

  /**
   * 记录创建时间戳
   */
  @TableField(value = "created_at", fill = FieldFill.INSERT)
  private LocalDateTime createdAt;

  /**
   * 最后更新时间戳
   */
  @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedAt;

  /**
   * 便利构造函数 - 用于用户注册
   */
  public User(String username, String password, String email, String role) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
  }

  @Override
  public String toString() {
    return "User{" +
        "userId=" + userId +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        ", uname='" + uname + '\'' +
        ", tel='" + tel + '\'' +
        ", sex='" + sex + '\'' +
        ", bir=" + bir +
        ", role='" + role + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }
}