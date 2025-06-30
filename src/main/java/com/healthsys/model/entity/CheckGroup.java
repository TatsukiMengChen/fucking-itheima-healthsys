package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 检查组实体类
 * 对应数据库check_groups表
 * 
 * @author AI健康管理系统开发团队
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("check_groups")
public class CheckGroup {

  /**
   * 检查组ID（主键，自增）
   */
  @TableId(value = "group_id", type = IdType.AUTO)
  private Integer groupId;

  /**
   * 检查组唯一代码
   */
  @TableField("group_code")
  private String groupCode;

  /**
   * 检查组名称
   */
  @TableField("group_name")
  private String groupName;

  /**
   * 检查组描述
   */
  @TableField("description")
  private String description;

  /**
   * 创建者用户ID
   */
  @TableField("created_by")
  private Integer createdBy;

  /**
   * 创建时间戳
   */
  @TableField(value = "created_at", fill = FieldFill.INSERT)
  private LocalDateTime createdAt;

  /**
   * 最后更新时间戳
   */
  @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updatedAt;

  /**
   * 状态（true为活动，false为非活动/已删除）
   */
  @TableField("is_active")
  @Builder.Default
  private Boolean isActive = true;

  /**
   * 便利构造函数 - 用于创建新检查组
   */
  public CheckGroup(String groupCode, String groupName, String description) {
    this.groupCode = groupCode;
    this.groupName = groupName;
    this.description = description;
    this.isActive = true;
  }
}