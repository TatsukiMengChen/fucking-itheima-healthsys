package com.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.dao.ExaminationResultMapper;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.config.DataAccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 体检结果服务实现类
 * 
 * @author AI Assistant
 */
public class ExaminationResultServiceImpl implements IExaminationResultService {

  private static final Logger logger = LoggerFactory.getLogger(ExaminationResultServiceImpl.class);

  private ExaminationResultMapper examinationResultMapper;

  public ExaminationResultServiceImpl() {
    this.examinationResultMapper = DataAccessManager.getExaminationResultMapperStatic();
  }

  @Override
  public boolean addExaminationResult(ExaminationResult examinationResult) {
    try {
      // 设置记录时间
      if (examinationResult.getRecordedAt() == null) {
        examinationResult.setRecordedAt(LocalDateTime.now());
      }

      int result = examinationResultMapper.insert(examinationResult);
      logger.info("添加体检结果: 用户ID={}, 检查项ID={}, 测量值={}",
          examinationResult.getUserId(), examinationResult.getItemId(),
          examinationResult.getMeasuredValue());
      return result > 0;
    } catch (Exception e) {
      logger.error("添加体检结果失败", e);
      return false;
    }
  }

  @Override
  public boolean updateExaminationResult(ExaminationResult examinationResult) {
    try {
      int result = examinationResultMapper.updateById(examinationResult);
      logger.info("更新体检结果: 结果ID={}", examinationResult.getResultId());
      return result > 0;
    } catch (Exception e) {
      logger.error("更新体检结果失败: 结果ID={}", examinationResult.getResultId(), e);
      return false;
    }
  }

  @Override
  public boolean deleteExaminationResult(Integer resultId) {
    try {
      int result = examinationResultMapper.deleteById(resultId);
      logger.info("删除体检结果: 结果ID={}", resultId);
      return result > 0;
    } catch (Exception e) {
      logger.error("删除体检结果失败: 结果ID={}", resultId, e);
      return false;
    }
  }

  @Override
  public List<ExaminationResult> getExaminationResultsByUserId(Integer userId) {
    try {
      QueryWrapper<ExaminationResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .orderByDesc("recorded_at");

      List<ExaminationResult> results = examinationResultMapper.selectList(queryWrapper);
      logger.info("查询用户体检结果: 用户ID={}, 记录数={}", userId, results.size());
      return results;
    } catch (Exception e) {
      logger.error("查询用户体检结果失败: 用户ID={}", userId, e);
      return null;
    }
  }

  @Override
  public List<ExaminationResult> getExaminationResultsByAppointmentId(Integer appointmentId) {
    try {
      QueryWrapper<ExaminationResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("appointment_id", appointmentId)
          .orderByAsc("item_id");

      List<ExaminationResult> results = examinationResultMapper.selectList(queryWrapper);
      logger.info("查询预约体检结果: 预约ID={}, 记录数={}", appointmentId, results.size());
      return results;
    } catch (Exception e) {
      logger.error("查询预约体检结果失败: 预约ID={}", appointmentId, e);
      return null;
    }
  }

  @Override
  public List<ExaminationResult> getExaminationResultsByItemId(Integer itemId) {
    try {
      QueryWrapper<ExaminationResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("item_id", itemId)
          .orderByDesc("recorded_at");

      List<ExaminationResult> results = examinationResultMapper.selectList(queryWrapper);
      logger.info("查询检查项体检结果: 检查项ID={}, 记录数={}", itemId, results.size());
      return results;
    } catch (Exception e) {
      logger.error("查询检查项体检结果失败: 检查项ID={}", itemId, e);
      return null;
    }
  }

  @Override
  public ExaminationResult getExaminationResultById(Integer resultId) {
    try {
      ExaminationResult result = examinationResultMapper.selectById(resultId);
      logger.info("查询体检结果详情: 结果ID={}", resultId);
      return result;
    } catch (Exception e) {
      logger.error("查询体检结果详情失败: 结果ID={}", resultId, e);
      return null;
    }
  }

  @Override
  public List<ExaminationResult> getExaminationResultsHistory(Integer userId, Integer itemId) {
    try {
      QueryWrapper<ExaminationResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .eq("item_id", itemId)
          .orderByDesc("recorded_at");

      List<ExaminationResult> results = examinationResultMapper.selectList(queryWrapper);
      logger.info("查询体检结果历史: 用户ID={}, 检查项ID={}, 记录数={}",
          userId, itemId, results.size());
      return results;
    } catch (Exception e) {
      logger.error("查询体检结果历史失败: 用户ID={}, 检查项ID={}", userId, itemId, e);
      return null;
    }
  }

  @Override
  public List<ExaminationResult> getExaminationResultsByDateRange(Integer userId, String startDate, String endDate) {
    try {
      QueryWrapper<ExaminationResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .between("recorded_at", startDate, endDate)
          .orderByDesc("recorded_at");

      List<ExaminationResult> results = examinationResultMapper.selectList(queryWrapper);
      logger.info("按日期范围查询体检结果: 用户ID={}, 开始日期={}, 结束日期={}, 记录数={}",
          userId, startDate, endDate, results.size());
      return results;
    } catch (Exception e) {
      logger.error("按日期范围查询体检结果失败: 用户ID={}", userId, e);
      return null;
    }
  }

  @Override
  public List<ExaminationResult> getAllExaminationResults(int current, int size) {
    try {
      Page<ExaminationResult> page = new Page<>(current, size);
      QueryWrapper<ExaminationResult> queryWrapper = new QueryWrapper<>();
      queryWrapper.orderByDesc("recorded_at");

      Page<ExaminationResult> result = examinationResultMapper.selectPage(page, queryWrapper);
      logger.info("分页查询所有体检结果: 当前页={}, 每页大小={}, 总记录数={}",
          current, size, result.getTotal());
      return result.getRecords();
    } catch (Exception e) {
      logger.error("分页查询所有体检结果失败", e);
      return null;
    }
  }

  @Override
  public boolean batchSaveExaminationResults(List<ExaminationResult> results) {
    if (results == null || results.isEmpty()) {
      logger.warn("批量保存体检结果失败：结果列表为空");
      return false;
    }

    try {
      // 验证数据
      for (ExaminationResult result : results) {
        if (result.getAppointmentId() == null || result.getUserId() == null ||
            result.getItemId() == null || result.getMeasuredValue() == null) {
          logger.error("批量保存体检结果失败：存在无效数据 - appointmentId={}, userId={}, itemId={}, measuredValue={}",
              result.getAppointmentId(), result.getUserId(), result.getItemId(), result.getMeasuredValue());
          return false;
        }
      }

      // 批量保存
      int successCount = 0;
      for (ExaminationResult result : results) {
        // 设置创建时间
        if (result.getRecordedAt() == null) {
          result.setRecordedAt(LocalDateTime.now());
        }

        int insertResult = examinationResultMapper.insert(result);
        if (insertResult > 0) {
          successCount++;
        }
      }

      boolean allSuccess = successCount == results.size();
      if (allSuccess) {
        logger.info("批量保存体检结果成功：共保存 {} 条记录", successCount);
      } else {
        logger.warn("批量保存体检结果部分成功：成功 {} 条，总计 {} 条", successCount, results.size());
      }

      return allSuccess;

    } catch (Exception e) {
      logger.error("批量保存体检结果失败", e);
      return false;
    }
  }
}