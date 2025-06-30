package com.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.dao.AppointmentMapper;
import com.healthsys.model.entity.Appointment;
import com.healthsys.service.IAppointmentService;
import com.healthsys.config.DataAccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务实现类
 * 
 * @author AI Assistant
 */
public class AppointmentServiceImpl implements IAppointmentService {

  private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

  private AppointmentMapper appointmentMapper;

  public AppointmentServiceImpl() {
    this.appointmentMapper = DataAccessManager.getAppointmentMapperStatic();
  }

  @Override
  public boolean createAppointment(Appointment appointment) {
    try {
      // 设置创建时间和更新时间
      appointment.setCreatedAt(LocalDateTime.now());
      appointment.setUpdatedAt(LocalDateTime.now());

      // 设置默认状态为待确认
      if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
        appointment.setStatus("待确认");
      }

      int result = appointmentMapper.insert(appointment);
      logger.info("创建预约: 用户ID={}, 检查组ID={}, 预约日期={}",
          appointment.getUserId(), appointment.getGroupId(), appointment.getAppointmentDate());
      return result > 0;
    } catch (Exception e) {
      logger.error("创建预约失败", e);
      return false;
    }
  }

  @Override
  public boolean updateAppointmentStatus(Integer appointmentId, String status) {
    try {
      Appointment appointment = new Appointment();
      appointment.setAppointmentId(appointmentId);
      appointment.setStatus(status);
      appointment.setUpdatedAt(LocalDateTime.now());

      int result = appointmentMapper.updateById(appointment);
      logger.info("更新预约状态: 预约ID={}, 新状态={}", appointmentId, status);
      return result > 0;
    } catch (Exception e) {
      logger.error("更新预约状态失败: 预约ID={}", appointmentId, e);
      return false;
    }
  }

  @Override
  public List<Appointment> getAppointmentsByUserId(Integer userId) {
    try {
      QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("user_id", userId)
          .orderByDesc("created_at");

      List<Appointment> appointments = appointmentMapper.selectList(queryWrapper);
      logger.info("查询用户预约历史: 用户ID={}, 记录数={}", userId, appointments.size());
      return appointments;
    } catch (Exception e) {
      logger.error("查询用户预约历史失败: 用户ID={}", userId, e);
      return null;
    }
  }

  @Override
  public Appointment getAppointmentById(Integer appointmentId) {
    try {
      Appointment appointment = appointmentMapper.selectById(appointmentId);
      logger.info("查询预约详情: 预约ID={}", appointmentId);
      return appointment;
    } catch (Exception e) {
      logger.error("查询预约详情失败: 预约ID={}", appointmentId, e);
      return null;
    }
  }

  @Override
  public boolean cancelAppointment(Integer appointmentId) {
    try {
      return updateAppointmentStatus(appointmentId, "已取消");
    } catch (Exception e) {
      logger.error("取消预约失败: 预约ID={}", appointmentId, e);
      return false;
    }
  }

  @Override
  public List<Appointment> getAllAppointments(int current, int size) {
    try {
      Page<Appointment> page = new Page<>(current, size);
      QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
      queryWrapper.orderByDesc("created_at");

      Page<Appointment> result = appointmentMapper.selectPage(page, queryWrapper);
      logger.info("分页查询所有预约: 当前页={}, 每页大小={}, 总记录数={}",
          current, size, result.getTotal());
      return result.getRecords();
    } catch (Exception e) {
      logger.error("分页查询所有预约失败", e);
      return null;
    }
  }

  @Override
  public List<Appointment> getAppointmentsByStatus(String status) {
    try {
      QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("status", status)
          .orderByDesc("created_at");

      List<Appointment> appointments = appointmentMapper.selectList(queryWrapper);
      logger.info("根据状态查询预约: 状态={}, 记录数={}", status, appointments.size());
      return appointments;
    } catch (Exception e) {
      logger.error("根据状态查询预约失败: 状态={}", status, e);
      return null;
    }
  }
}