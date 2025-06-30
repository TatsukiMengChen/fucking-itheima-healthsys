package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 体检结果实体类。
 * 存储用户体检的各项结果。
 * 
 * @author 梦辰
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
   * 检查组ID
   */
  @TableField("group_id")
  private Integer groupId;

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
  public ExaminationResult(Integer appointmentId, Integer userId, Integer groupId, Integer itemId,
      String measuredValue) {
    this.appointmentId = appointmentId;
    this.userId = userId;
    this.groupId = groupId;
    this.itemId = itemId;
    this.measuredValue = measuredValue;
  }
}