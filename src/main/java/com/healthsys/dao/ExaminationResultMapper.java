package com.healthsys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.model.entity.ExaminationResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 体检结果数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI健康管理系统开发团队
 */
@Mapper
public interface ExaminationResultMapper extends BaseMapper<ExaminationResult> {

  /**
   * 根据用户ID查询体检结果
   * 
   * @param userId 用户ID
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE user_id = #{userId} ORDER BY recorded_at DESC")
  List<ExaminationResult> findByUserId(@Param("userId") Integer userId);

  /**
   * 根据用户ID分页查询体检结果
   * 
   * @param page   分页参数
   * @param userId 用户ID
   * @return 分页查询结果
   */
  @Select("SELECT * FROM examination_results WHERE user_id = #{userId} ORDER BY recorded_at DESC")
  Page<ExaminationResult> findByUserIdWithPage(Page<ExaminationResult> page, @Param("userId") Integer userId);

  /**
   * 根据预约ID查询体检结果
   * 
   * @param appointmentId 预约ID
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE appointment_id = #{appointmentId} ORDER BY recorded_at DESC")
  List<ExaminationResult> findByAppointmentId(@Param("appointmentId") Integer appointmentId);

  /**
   * 根据检查项ID查询体检结果
   * 
   * @param itemId 检查项ID
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE item_id = #{itemId} ORDER BY recorded_at DESC")
  List<ExaminationResult> findByItemId(@Param("itemId") Integer itemId);

  /**
   * 根据用户ID和检查项ID查询体检结果
   * 
   * @param userId 用户ID
   * @param itemId 检查项ID
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE user_id = #{userId} AND item_id = #{itemId} ORDER BY recorded_at DESC")
  List<ExaminationResult> findByUserIdAndItemId(@Param("userId") Integer userId, @Param("itemId") Integer itemId);

  /**
   * 查询指定用户在指定时间范围内的体检结果
   * 
   * @param userId    用户ID
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE user_id = #{userId} " +
      "AND recorded_at BETWEEN #{startTime} AND #{endTime} " +
      "ORDER BY recorded_at DESC")
  List<ExaminationResult> findByUserIdAndTimeRange(@Param("userId") Integer userId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  /**
   * 查询指定用户的最近体检结果
   * 
   * @param userId 用户ID
   * @param limit  数量限制
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE user_id = #{userId} " +
      "ORDER BY recorded_at DESC LIMIT #{limit}")
  List<ExaminationResult> findRecentByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);

  /**
   * 查询指定用户和检查项的最近体检结果
   * 
   * @param userId 用户ID
   * @param itemId 检查项ID
   * @param limit  数量限制
   * @return 体检结果列表
   */
  @Select("SELECT * FROM examination_results WHERE user_id = #{userId} AND item_id = #{itemId} " +
      "ORDER BY recorded_at DESC LIMIT #{limit}")
  List<ExaminationResult> findRecentByUserIdAndItemId(@Param("userId") Integer userId,
      @Param("itemId") Integer itemId,
      @Param("limit") Integer limit);

  /**
   * 统计用户的体检结果数量
   * 
   * @param userId 用户ID
   * @return 体检结果总数
   */
  @Select("SELECT COUNT(*) FROM examination_results WHERE user_id = #{userId}")
  Integer countByUserId(@Param("userId") Integer userId);

  /**
   * 统计指定检查项的体检结果数量
   * 
   * @param itemId 检查项ID
   * @return 体检结果数量
   */
  @Select("SELECT COUNT(*) FROM examination_results WHERE item_id = #{itemId}")
  Integer countByItemId(@Param("itemId") Integer itemId);
}