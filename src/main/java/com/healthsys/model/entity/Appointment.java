package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 预约实体类。
 * 表示用户预约信息。
 * 
 * @author 梦辰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("appointments")
public class Appointment {

  /**
   * 预约ID（主键，自增）
   */
  @TableId(value = "appointment_id", type = IdType.AUTO)
  private Integer appointmentId;

  /**
   * 预约用户ID
   */
  @TableField("user_id")
  private Integer userId;

  /**
   * 预约检查组ID
   */
  @TableField("group_id")
  private Integer groupId;

  /**
   * 预约体检日期
   */
  @TableField("appointment_date")
  private LocalDate appointmentDate;

  /**
   * 预约体检时间
   */
  @TableField("appointment_time")
  private LocalTime appointmentTime;

  /**
   * 体检方式选择
   */
  @TableField("examination_method")
  private String examinationMethod;

  /**
   * 预约状态
   */
  @TableField("status")
  @Builder.Default
  private String status = "待确认";

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
   * 便利构造函数 - 用于创建新预约
   */
  public Appointment(Integer userId, Integer groupId, LocalDate appointmentDate,
      LocalTime appointmentTime, String examinationMethod) {
    this.userId = userId;
    this.groupId = groupId;
    this.appointmentDate = appointmentDate;
    this.appointmentTime = appointmentTime;
    this.examinationMethod = examinationMethod;
    this.status = "待确认";
  }
}