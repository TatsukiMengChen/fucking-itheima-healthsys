package com.healthsys.service;

import com.healthsys.model.entity.MedicalHistory;
import java.util.List;

/**
 * 病史服务接口
 * 提供病史记录的相关业务逻辑
 * 
 * @author AI Assistant
 */
public interface IMedicalHistoryService {

  /**
   * 添加病史记录
   * 
   * @param medicalHistory 病史记录
   * @return 添加是否成功
   */
  boolean addMedicalHistory(MedicalHistory medicalHistory);

  /**
   * 更新病史记录
   * 
   * @param medicalHistory 病史记录
   * @return 更新是否成功
   */
  boolean updateMedicalHistory(MedicalHistory medicalHistory);

  /**
   * 删除病史记录
   * 
   * @param historyId 病史ID
   * @return 删除是否成功
   */
  boolean deleteMedicalHistory(Integer historyId);

  /**
   * 根据用户ID查询病史记录
   * 
   * @param userId 用户ID
   * @return 病史记录列表
   */
  List<MedicalHistory> getMedicalHistoryByUserId(Integer userId);

  /**
   * 根据病史ID查询病史详情
   * 
   * @param historyId 病史ID
   * @return 病史详情
   */
  MedicalHistory getMedicalHistoryById(Integer historyId);

  /**
   * 根据诊断日期范围查询病史
   * 
   * @param userId    用户ID
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 病史记录列表
   */
  List<MedicalHistory> getMedicalHistoryByDateRange(Integer userId, String startDate, String endDate);

  /**
   * 根据诊断关键词查询病史
   * 
   * @param userId  用户ID
   * @param keyword 关键词
   * @return 病史记录列表
   */
  List<MedicalHistory> getMedicalHistoryByDiagnosis(Integer userId, String keyword);

  /**
   * 获取用户最近的病史记录
   * 
   * @param userId 用户ID
   * @param limit  记录数量限制
   * @return 最近的病史记录列表
   */
  List<MedicalHistory> getRecentMedicalHistory(Integer userId, int limit);
}