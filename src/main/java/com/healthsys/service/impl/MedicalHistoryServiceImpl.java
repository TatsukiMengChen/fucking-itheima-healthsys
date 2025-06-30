package com.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.healthsys.dao.MedicalHistoryMapper;
import com.healthsys.model.entity.MedicalHistory;
import com.healthsys.service.IMedicalHistoryService;
import com.healthsys.config.DataAccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 病史服务实现类
 * 
 * @author AI Assistant
 */
public class MedicalHistoryServiceImpl implements IMedicalHistoryService {

  private static final Logger logger = LoggerFactory.getLogger(MedicalHistoryServiceImpl.class);

  private MedicalHistoryMapper medicalHistoryMapper;

  public MedicalHistoryServiceImpl() {
    this.medicalHistoryMapper = DataAccessManager.getMedicalHistoryMapperStatic();
  }

  @Override
  public boolean addMedicalHistory(MedicalHistory medicalHistory) {
    try {
      // 设置创建时间和更新时间
      medicalHistory.setCreatedAt(LocalDateTime.now());
      medicalHistory.setUpdatedAt(LocalDateTime.now());

      int result = medicalHistoryMapper.insert(medicalHistory);
      logger.info("添加病史记录: 用户ID={}, 诊断={}",
          medicalHistory.getUserId(), medicalHistory.getDiagnosis());
      return result > 0;
    } catch (Exception e) {
      logger.error("添加病史记录失败", e);
      return false;
    }
  }

  @Override
  public boolean updateMedicalHistory(MedicalHistory medicalHistory) {
    try {
      medicalHistory.setUpdatedAt(LocalDateTime.now());

      int result = medicalHistoryMapper.updateById(medicalHistory);
      logger.info("更新病史记录: 病史ID={}", medicalHistory.getHistoryId());
      return result > 0;
    } catch (Exception e) {
      logger.error("更新病史记录失败: 病史ID={}", medicalHistory.getHistoryId(), e);
      return false;
    }
  }

  @Override
  public boolean deleteMedicalHistory(Integer historyId) {
    try {
      int result = medicalHistoryMapper.deleteById(historyId);
      logger.info("删除病史记录: 病史ID={}", historyId);
      return result > 0;
    } catch (Exception e) {
      logger.error("删除病史记录失败: 病史ID={}", historyId, e);
      return false;
    }
  }

  @Override
  public List<MedicalHistory> getMedicalHistoryByUserId(Integer userId) {
    try {
      QueryWrapper<MedicalHistory> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .orderByDesc("diagnosis_date");

      List<MedicalHistory> histories = medicalHistoryMapper.selectList(queryWrapper);
      logger.info("查询用户病史: 用户ID={}, 记录数={}", userId, histories.size());
      return histories;
    } catch (Exception e) {
      logger.error("查询用户病史失败: 用户ID={}", userId, e);
      return null;
    }
  }

  @Override
  public MedicalHistory getMedicalHistoryById(Integer historyId) {
    try {
      MedicalHistory history = medicalHistoryMapper.selectById(historyId);
      logger.info("查询病史详情: 病史ID={}", historyId);
      return history;
    } catch (Exception e) {
      logger.error("查询病史详情失败: 病史ID={}", historyId, e);
      return null;
    }
  }

  @Override
  public List<MedicalHistory> getMedicalHistoryByDateRange(Integer userId, String startDate, String endDate) {
    try {
      QueryWrapper<MedicalHistory> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .between("diagnosis_date", startDate, endDate)
          .orderByDesc("diagnosis_date");

      List<MedicalHistory> histories = medicalHistoryMapper.selectList(queryWrapper);
      logger.info("按日期范围查询病史: 用户ID={}, 开始日期={}, 结束日期={}, 记录数={}",
          userId, startDate, endDate, histories.size());
      return histories;
    } catch (Exception e) {
      logger.error("按日期范围查询病史失败: 用户ID={}", userId, e);
      return null;
    }
  }

  @Override
  public List<MedicalHistory> getMedicalHistoryByDiagnosis(Integer userId, String keyword) {
    try {
      QueryWrapper<MedicalHistory> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .like("diagnosis", keyword)
          .orderByDesc("diagnosis_date");

      List<MedicalHistory> histories = medicalHistoryMapper.selectList(queryWrapper);
      logger.info("按诊断关键词查询病史: 用户ID={}, 关键词={}, 记录数={}",
          userId, keyword, histories.size());
      return histories;
    } catch (Exception e) {
      logger.error("按诊断关键词查询病史失败: 用户ID={}", userId, e);
      return null;
    }
  }

  @Override
  public List<MedicalHistory> getRecentMedicalHistory(Integer userId, int limit) {
    try {
      QueryWrapper<MedicalHistory> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .orderByDesc("diagnosis_date")
          .last("LIMIT " + limit);

      List<MedicalHistory> histories = medicalHistoryMapper.selectList(queryWrapper);
      logger.info("查询最近病史: 用户ID={}, 限制数量={}, 记录数={}",
          userId, limit, histories.size());
      return histories;
    } catch (Exception e) {
      logger.error("查询最近病史失败: 用户ID={}", userId, e);
      return null;
    }
  }
}