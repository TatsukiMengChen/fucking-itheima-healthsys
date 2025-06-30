package com.healthsys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.model.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约数据访问接口。
 * 提供预约相关的数据库操作方法。
 * 
 * @author 梦辰
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

  /**
   * 根据用户ID查询预约记录
   * 
   * @param userId 用户ID
   * @return 预约记录列表
   */
  @Select("SELECT * FROM appointments WHERE user_id = #{userId} ORDER BY appointment_date DESC, appointment_time DESC")
  List<Appointment> findByUserId(@Param("userId") Integer userId);

  /**
   * 根据用户ID分页查询预约记录
   * 
   * @param page   分页参数
   * @param userId 用户ID
   * @return 分页查询结果
   */
  @Select("SELECT * FROM appointments WHERE user_id = #{userId} ORDER BY appointment_date DESC, appointment_time DESC")
  Page<Appointment> findByUserIdWithPage(Page<Appointment> page, @Param("userId") Integer userId);

  /**
   * 根据状态查询预约记录
   * 
   * @param status 预约状态
   * @return 预约记录列表
   */
  @Select("SELECT * FROM appointments WHERE status = #{status} ORDER BY appointment_date DESC, appointment_time DESC")
  List<Appointment> findByStatus(@Param("status") String status);

  /**
   * 根据检查组ID查询预约记录
   * 
   * @param groupId 检查组ID
   * @return 预约记录列表
   */
  @Select("SELECT * FROM appointments WHERE group_id = #{groupId} ORDER BY appointment_date DESC, appointment_time DESC")
  List<Appointment> findByGroupId(@Param("groupId") Integer groupId);

  /**
   * 查询指定日期的预约记录
   * 
   * @param appointmentDate 预约日期
   * @return 预约记录列表
   */
  @Select("SELECT * FROM appointments WHERE appointment_date = #{appointmentDate} ORDER BY appointment_time")
  List<Appointment> findByAppointmentDate(@Param("appointmentDate") LocalDate appointmentDate);

  /**
   * 查询指定用户在指定日期范围内的预约记录
   * 
   * @param userId    用户ID
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 预约记录列表
   */
  @Select("SELECT * FROM appointments WHERE user_id = #{userId} " +
      "AND appointment_date BETWEEN #{startDate} AND #{endDate} " +
      "ORDER BY appointment_date DESC, appointment_time DESC")
  List<Appointment> findByUserIdAndDateRange(@Param("userId") Integer userId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  /**
   * 查询指定用户的最近预约记录
   * 
   * @param userId 用户ID
   * @param limit  数量限制
   * @return 预约记录列表
   */
  @Select("SELECT * FROM appointments WHERE user_id = #{userId} " +
      "ORDER BY appointment_date DESC, appointment_time DESC LIMIT #{limit}")
  List<Appointment> findRecentByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);

  /**
   * 统计用户的预约数量
   * 
   * @param userId 用户ID
   * @return 预约总数
   */
  @Select("SELECT COUNT(*) FROM appointments WHERE user_id = #{userId}")
  Integer countByUserId(@Param("userId") Integer userId);

  /**
   * 统计指定状态的预约数量
   * 
   * @param status 预约状态
   * @return 预约数量
   */
  @Select("SELECT COUNT(*) FROM appointments WHERE status = #{status}")
  Integer countByStatus(@Param("status") String status);
}