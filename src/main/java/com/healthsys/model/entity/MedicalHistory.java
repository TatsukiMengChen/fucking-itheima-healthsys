package com.healthsys.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病史记录实体类
 * 对应数据库medical_history表
 * 
 * @author AI健康管理系统开发团队
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("medical_history")
public class MedicalHistory {

  /**
   * 病史ID（主键，自增）
   */
  @TableId(value = "history_id", type = IdType.AUTO)
  private Integer historyId;

  /**
   * 用户ID
   */
  @TableField("user_id")
  private Integer userId;

  /**
   * 诊断结果
   */
  @TableField("diagnosis")
  private String diagnosis;

  /**
   * 医生姓名
   */
  @TableField("doctor_name")
  private String doctorName;

  /**
   * 诊断日期
   */
  @TableField("diagnosis_date")
  private LocalDate diagnosisDate;

  /**
   * 治疗方案
   */
  @TableField("treatment")
  private String treatment;

  /**
   * 备注
   */
  @TableField("notes")
  private String notes;

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
   * 便利构造函数 - 用于创建新的病史记录
   */
  public MedicalHistory(Integer userId, String diagnosis, String doctorName, LocalDate diagnosisDate) {
    this.userId = userId;
    this.diagnosis = diagnosis;
    this.doctorName = doctorName;
    this.diagnosisDate = diagnosisDate;
  }
}