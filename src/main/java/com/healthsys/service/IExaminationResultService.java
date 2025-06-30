package com.healthsys.service;

import com.healthsys.model.entity.ExaminationResult;
import java.util.List;

/**
 * 体检结果服务接口。
 * 定义体检结果相关的业务操作。
 * 
 * @author 梦辰
 */
public interface IExaminationResultService {

  /**
   * 添加体检结果
   * 
   * @param examinationResult 体检结果
   * @return 添加是否成功
   */
  boolean addExaminationResult(ExaminationResult examinationResult);

  /**
   * 更新体检结果
   * 
   * @param examinationResult 体检结果
   * @return 更新是否成功
   */
  boolean updateExaminationResult(ExaminationResult examinationResult);

  /**
   * 删除体检结果
   * 
   * @param resultId 结果ID
   * @return 删除是否成功
   */
  boolean deleteExaminationResult(Integer resultId);

  /**
   * 根据用户ID查询体检结果
   * 
   * @param userId 用户ID
   * @return 体检结果列表
   */
  List<ExaminationResult> getExaminationResultsByUserId(Integer userId);

  /**
   * 根据预约ID查询体检结果
   * 
   * @param appointmentId 预约ID
   * @return 体检结果列表
   */
  List<ExaminationResult> getExaminationResultsByAppointmentId(Integer appointmentId);

  /**
   * 根据检查项ID查询体检结果
   * 
   * @param itemId 检查项ID
   * @return 体检结果列表
   */
  List<ExaminationResult> getExaminationResultsByItemId(Integer itemId);

  /**
   * 根据结果ID查询体检结果详情
   * 
   * @param resultId 结果ID
   * @return 体检结果详情
   */
  ExaminationResult getExaminationResultById(Integer resultId);

  /**
   * 根据用户ID和检查项ID查询体检结果历史
   * 
   * @param userId 用户ID
   * @param itemId 检查项ID
   * @return 体检结果历史列表
   */
  List<ExaminationResult> getExaminationResultsHistory(Integer userId, Integer itemId);

  /**
   * 根据日期范围查询体检结果
   * 
   * @param userId    用户ID
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 体检结果列表
   */
  List<ExaminationResult> getExaminationResultsByDateRange(Integer userId, String startDate, String endDate);

  /**
   * 分页查询所有体检结果（管理员用）
   * 
   * @param current 当前页
   * @param size    每页大小
   * @return 体检结果列表
   */
  List<ExaminationResult> getAllExaminationResults(int current, int size);

  /**
   * 批量保存体检结果
   * 确保批量保存的事务性和数据一致性
   * 
   * @param results 体检结果列表
   * @return 保存成功返回true，失败返回false
   */
  boolean batchSaveExaminationResults(List<ExaminationResult> results);
}