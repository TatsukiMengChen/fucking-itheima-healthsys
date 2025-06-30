package com.healthsys.viewmodel.user.appointment;

import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.service.IAppointmentService;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.impl.AppointmentServiceImpl;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 预约ViewModel
 * 管理预约表单数据、预约历史列表，并提供相关命令
 * 
 * @author AI Assistant
 */
public class AppointmentViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(AppointmentViewModel.class);

  private final IAppointmentService appointmentService;
  private final ICheckGroupService checkGroupService;

  // 预约表单数据
  private Integer selectedCheckGroupId;
  private String selectedCheckGroupName;
  private LocalDate appointmentDate;
  private LocalTime appointmentTime;
  private String examinationMethod;

  // 数据列表
  private List<CheckGroup> availableCheckGroups;
  private List<Appointment> appointmentHistory;

  // UI状态
  private boolean isLoading;
  private String statusMessage;

  // 体检方式选项
  private final String[] examinationMethods = { "上门体检", "诊所体检", "医院体检" };

  public AppointmentViewModel() {
    this.appointmentService = new AppointmentServiceImpl();
    this.checkGroupService = new CheckGroupServiceImpl();
    this.availableCheckGroups = new ArrayList<>();
    this.appointmentHistory = new ArrayList<>();
    this.examinationMethod = examinationMethods[0]; // 默认选择第一个

    // 初始化时加载检查组数据
    loadAvailableCheckGroups();
  }

  /**
   * 提交预约命令
   */
  public CompletableFuture<Boolean> submitAppointmentCommand() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在提交预约...");

        // 验证表单数据
        if (!validateAppointmentForm()) {
          return false;
        }

        // 创建预约对象
        Appointment appointment = new Appointment();
        appointment.setUserId(getCurrentUserId()); // 需要从登录状态获取
        appointment.setGroupId(selectedCheckGroupId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setExaminationMethod(examinationMethod);
        appointment.setStatus("待确认");

        // 提交预约
        boolean success = appointmentService.createAppointment(appointment);

        if (success) {
          setStatusMessage("预约提交成功！");
          // 清空表单
          clearAppointmentForm();
          // 重新加载历史记录
          loadAppointmentHistory();

          // 通知UI更新
          SwingUtilities.invokeLater(() -> {
            firePropertyChange("appointmentSubmitted", false, true);
          });
        } else {
          setStatusMessage("预约提交失败，请重试");
        }

        return success;

      } catch (Exception e) {
        logger.error("提交预约时发生错误", e);
        setStatusMessage("提交预约时发生错误：" + e.getMessage());
        return false;
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 加载预约历史命令
   */
  public CompletableFuture<Void> loadAppointmentHistoryCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载预约历史...");

        List<Appointment> histories = appointmentService.getAppointmentsByUserId(getCurrentUserId());

        if (histories != null) {
          setAppointmentHistory(histories);
          setStatusMessage("预约历史加载完成");
        } else {
          setStatusMessage("加载预约历史失败");
        }

      } catch (Exception e) {
        logger.error("加载预约历史时发生错误", e);
        setStatusMessage("加载预约历史时发生错误：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 取消预约命令
   */
  public CompletableFuture<Boolean> cancelAppointmentCommand(Integer appointmentId) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在取消预约...");

        boolean success = appointmentService.cancelAppointment(appointmentId);

        if (success) {
          setStatusMessage("预约已取消");
          // 重新加载历史记录
          loadAppointmentHistory();
        } else {
          setStatusMessage("取消预约失败");
        }

        return success;

      } catch (Exception e) {
        logger.error("取消预约时发生错误", e);
        setStatusMessage("取消预约时发生错误：" + e.getMessage());
        return false;
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 加载可用检查组
   */
  private void loadAvailableCheckGroups() {
    CompletableFuture.runAsync(() -> {
      try {
        List<CheckGroup> checkGroups = checkGroupService.getAllActiveCheckGroups();
        if (checkGroups != null) {
          setAvailableCheckGroups(checkGroups);
        }
      } catch (Exception e) {
        logger.error("加载检查组失败", e);
      }
    });
  }

  /**
   * 加载预约历史记录（私有方法）
   */
  private void loadAppointmentHistory() {
    loadAppointmentHistoryCommand();
  }

  /**
   * 验证预约表单
   */
  private boolean validateAppointmentForm() {
    if (selectedCheckGroupId == null) {
      setStatusMessage("请选择检查组");
      return false;
    }

    if (appointmentDate == null) {
      setStatusMessage("请选择预约日期");
      return false;
    }

    if (appointmentDate.isBefore(LocalDate.now())) {
      setStatusMessage("预约日期不能早于今天");
      return false;
    }

    if (appointmentTime == null) {
      setStatusMessage("请选择预约时间");
      return false;
    }

    if (examinationMethod == null || examinationMethod.trim().isEmpty()) {
      setStatusMessage("请选择体检方式");
      return false;
    }

    return true;
  }

  /**
   * 清空预约表单
   */
  private void clearAppointmentForm() {
    setSelectedCheckGroupId(null);
    setSelectedCheckGroupName(null);
    setAppointmentDate(null);
    setAppointmentTime(null);
    setExaminationMethod(examinationMethods[0]);
  }

  /**
   * 获取当前登录用户ID
   * TODO: 实现从全局状态获取当前用户ID的逻辑
   */
  private Integer getCurrentUserId() {
    // 这里需要从全局应用状态或会话中获取当前登录用户的ID
    // 暂时返回一个测试值
    return 1;
  }

  // Getters and Setters
  public Integer getSelectedCheckGroupId() {
    return selectedCheckGroupId;
  }

  public void setSelectedCheckGroupId(Integer selectedCheckGroupId) {
    Integer oldValue = this.selectedCheckGroupId;
    this.selectedCheckGroupId = selectedCheckGroupId;
    firePropertyChange("selectedCheckGroupId", oldValue, selectedCheckGroupId);
  }

  public String getSelectedCheckGroupName() {
    return selectedCheckGroupName;
  }

  public void setSelectedCheckGroupName(String selectedCheckGroupName) {
    String oldValue = this.selectedCheckGroupName;
    this.selectedCheckGroupName = selectedCheckGroupName;
    firePropertyChange("selectedCheckGroupName", oldValue, selectedCheckGroupName);
  }

  public LocalDate getAppointmentDate() {
    return appointmentDate;
  }

  public void setAppointmentDate(LocalDate appointmentDate) {
    LocalDate oldValue = this.appointmentDate;
    this.appointmentDate = appointmentDate;
    firePropertyChange("appointmentDate", oldValue, appointmentDate);
  }

  public LocalTime getAppointmentTime() {
    return appointmentTime;
  }

  public void setAppointmentTime(LocalTime appointmentTime) {
    LocalTime oldValue = this.appointmentTime;
    this.appointmentTime = appointmentTime;
    firePropertyChange("appointmentTime", oldValue, appointmentTime);
  }

  public String getExaminationMethod() {
    return examinationMethod;
  }

  public void setExaminationMethod(String examinationMethod) {
    String oldValue = this.examinationMethod;
    this.examinationMethod = examinationMethod;
    firePropertyChange("examinationMethod", oldValue, examinationMethod);
  }

  public List<CheckGroup> getAvailableCheckGroups() {
    return availableCheckGroups;
  }

  public void setAvailableCheckGroups(List<CheckGroup> availableCheckGroups) {
    List<CheckGroup> oldValue = this.availableCheckGroups;
    this.availableCheckGroups = availableCheckGroups;
    firePropertyChange("availableCheckGroups", oldValue, availableCheckGroups);
  }

  public List<Appointment> getAppointmentHistory() {
    return appointmentHistory;
  }

  public void setAppointmentHistory(List<Appointment> appointmentHistory) {
    List<Appointment> oldValue = this.appointmentHistory;
    this.appointmentHistory = appointmentHistory;
    firePropertyChange("appointmentHistory", oldValue, appointmentHistory);
  }

  public String[] getExaminationMethods() {
    return examinationMethods;
  }

  public boolean isLoading() {
    return isLoading;
  }

  public void setLoading(boolean loading) {
    boolean oldValue = this.isLoading;
    this.isLoading = loading;
    firePropertyChange("loading", oldValue, loading);
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    String oldValue = this.statusMessage;
    this.statusMessage = statusMessage;
    firePropertyChange("statusMessage", oldValue, statusMessage);
  }

  /**
   * 初始化加载数据
   */
  public void initialize() {
    loadAvailableCheckGroups();
    loadAppointmentHistoryCommand();
  }
}