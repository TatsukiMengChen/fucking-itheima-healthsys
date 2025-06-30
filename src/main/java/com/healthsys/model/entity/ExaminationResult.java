package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 体检结果实体类
 * 对应数据库examination_results表
 * 
 * @author AI健康管理系统开发团队
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("examination_results")
public class ExaminationResult {

  /**
   * 结果ID（主键，自增）
   */
  @TableId(value = "result_id", type = IdType.AUTO)
  private Integer resultId;

  /**
   * 对应预约ID
   */
  @TableField("appointment_id")
  private Integer appointmentId;

  /**
   * 用户ID
   */
  @TableField("user_id")
  private Integer userId;

  /**
   * 检查项ID
   */
  @TableField("item_id")
  private Integer itemId;

  /**
   * 实际测量值
   */
  @TableField("measured_value")
  private String measuredValue;

  /**
   * 结果备注/医生建议
   */
  @TableField("result_notes")
  private String resultNotes;

  /**
   * 结果记录时间
   */
  @TableField(value = "recorded_at", fill = FieldFill.INSERT)
  private LocalDateTime recordedAt;

  /**
   * 便利构造函数 - 用于创建新的检查结果
   */
  public ExaminationResult(Integer appointmentId, Integer userId, Integer itemId, String measuredValue) {
    this.appointmentId = appointmentId;
    this.userId = userId;
    this.itemId = itemId;
    this.measuredValue = measuredValue;
  }
}