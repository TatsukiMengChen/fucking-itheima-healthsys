package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 检查项实体类
 * 对应数据库check_items表
 * 
 * @author AI健康管理系统开发团队
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("check_items")
public class CheckItem {

  /**
   * 检查项ID（主键，自增）
   */
  @TableId(value = "item_id", type = IdType.AUTO)
  private Integer itemId;

  /**
   * 检查项的唯一代码
   */
  @TableField("item_code")
  private String itemCode;

  /**
   * 检查项名称
   */
  @TableField("item_name")
  private String itemName;

  /**
   * 项的参考值
   */
  @TableField("reference_val")
  private String referenceVal;

  /**
   * 测量单位
   */
  @TableField("unit")
  private String unit;

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
   * 便利构造函数 - 用于创建新检查项
   */
  public CheckItem(String itemCode, String itemName, String referenceVal, String unit) {
    this.itemCode = itemCode;
    this.itemName = itemName;
    this.referenceVal = referenceVal;
    this.unit = unit;
    this.isActive = true;
  }
}