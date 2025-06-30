package com.healthsys.service;

import com.healthsys.model.entity.Appointment;
import java.util.List;

/**
 * 预约服务接口。
 * 定义预约相关的业务操作。
 * 
 * @author 梦辰
 */
public interface IAppointmentService {

  /**
   * 创建新的预约
   * 
   * @param appointment 预约信息
   * @return 创建是否成功
   */
  boolean createAppointment(Appointment appointment);

  /**
   * 更新预约状态
   * 
   * @param appointmentId 预约ID
   * @param status        新状态
   * @return 更新是否成功
   */
  boolean updateAppointmentStatus(Integer appointmentId, String status);

  /**
   * 根据用户ID查询预约历史
   * 
   * @param userId 用户ID
   * @return 预约历史列表
   */
  List<Appointment> getAppointmentsByUserId(Integer userId);

  /**
   * 根据预约ID查询预约详情
   * 
   * @param appointmentId 预约ID
   * @return 预约详情
   */
  Appointment getAppointmentById(Integer appointmentId);

  /**
   * 取消预约
   * 
   * @param appointmentId 预约ID
   * @return 取消是否成功
   */
  boolean cancelAppointment(Integer appointmentId);

  /**
   * 分页查询所有预约（管理员用）
   * 
   * @param current 当前页
   * @param size    每页大小
   * @return 预约列表
   */
  List<Appointment> getAllAppointments(int current, int size);

  /**
   * 根据状态查询预约
   * 
   * @param status 预约状态
   * @return 预约列表
   */
  List<Appointment> getAppointmentsByStatus(String status);
}