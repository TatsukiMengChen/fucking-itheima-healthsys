package com.healthsys.viewmodel.admin.appointment;

import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.service.IAppointmentService;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.dao.UserMapper;
import com.healthsys.service.impl.AppointmentServiceImpl;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.config.DataAccessManager;
import com.healthsys.config.AppContext;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 管理员预约管理ViewModel
 * 管理所有用户的预约记录，提供查看、状态更新等功能
 * 
 * @author AI Assistant
 */
public class AdminAppointmentViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(AdminAppointmentViewModel.class);

  private final IAppointmentService appointmentService;
  private final ICheckGroupService checkGroupService;
  private final UserMapper userMapper;

  // 预约数据
  private List<Appointment> appointmentList;
  private Appointment selectedAppointment;

  // 缓存数据
  private Map<Integer, CheckGroup> checkGroupCache;
  private Map<Integer, User> userCache;

  // 搜索和筛选条件
  private String searchKeyword;
  private String selectedStatus;
  private Integer selectedUserId;
  private String selectedDateRange;

  // UI状态
  private boolean isLoading;
  private String statusMessage;

  // 页面跳转回调
  private NavigationCallback navigationCallback;

  // 预约状态选项
  private final String[] appointmentStatuses = { "全部", "待确认", "已确认", "已完成", "已取消" };

  // 页面跳转回调接口
  public interface NavigationCallback {
    void navigateToHealthDataEntry(com.healthsys.model.entity.Appointment appointment);
  }

  public AdminAppointmentViewModel() {
    this.appointmentService = new AppointmentServiceImpl();
    this.checkGroupService = new CheckGroupServiceImpl();
    this.userMapper = DataAccessManager.getUserMapperStatic();
    this.appointmentList = new ArrayList<>();
    this.checkGroupCache = new HashMap<>();
    this.userCache = new HashMap<>();
    this.searchKeyword = "";
    this.selectedStatus = "全部";
    this.selectedDateRange = "全部";

    User currentUser = AppContext.getCurrentUser();
    if (currentUser != null) {
      logger.info("管理员预约管理ViewModel初始化，当前用户: {} ({})",
          currentUser.getUsername(), currentUser.getRole());
    }

    // 初始化数据
    initialize();
  }

  /**
   * 初始化
   */
  public void initialize() {
    loadAppointmentsCommand();
    loadCacheData();
  }

  /**
   * 加载预约列表命令
   */
  public CompletableFuture<Void> loadAppointmentsCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载预约记录...");

        List<Appointment> appointments;

        if (!"全部".equals(selectedStatus)) {
          // 按状态筛选
          appointments = appointmentService.getAppointmentsByStatus(selectedStatus);
        } else {
          // 加载所有预约
          appointments = appointmentService.getAllAppointments(1, 1000);
        }

        if (appointments != null) {
          setAppointmentList(appointments);
          setStatusMessage("预约记录加载完成，共 " + appointments.size() + " 条记录");
        } else {
          setAppointmentList(new ArrayList<>());
          setStatusMessage("暂无预约记录");
        }

      } catch (Exception e) {
        logger.error("加载预约记录时发生错误", e);
        setStatusMessage("加载预约记录失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 更新预约状态命令
   */
  public CompletableFuture<Boolean> updateAppointmentStatusCommand(Integer appointmentId, String newStatus) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在更新预约状态...");

        boolean success = appointmentService.updateAppointmentStatus(appointmentId, newStatus);

        if (success) {
          setStatusMessage("预约状态更新成功");
          // 重新加载数据
          loadAppointmentsCommand();
        } else {
          setStatusMessage("预约状态更新失败");
        }

        return success;

      } catch (Exception e) {
        logger.error("更新预约状态时发生错误", e);
        setStatusMessage("更新预约状态失败：" + e.getMessage());
        return false;
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 搜索预约命令
   */
  public CompletableFuture<Void> searchAppointmentsCommand(String keyword) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setSearchKeyword(keyword);
        setStatusMessage("正在搜索...");

        // 这里可以实现搜索逻辑
        List<Appointment> allAppointments = appointmentService.getAllAppointments(1, 1000);
        List<Appointment> filteredAppointments = new ArrayList<>();

        if (allAppointments != null) {
          for (Appointment appointment : allAppointments) {
            if (keyword.isEmpty() ||
                appointment.getAppointmentId().toString().contains(keyword) ||
                (getUserName(appointment.getUserId()) != null &&
                    getUserName(appointment.getUserId()).contains(keyword))
                ||
                (getCheckGroupName(appointment.getGroupId()) != null &&
                    getCheckGroupName(appointment.getGroupId()).contains(keyword))) {
              filteredAppointments.add(appointment);
            }
          }
        }

        setAppointmentList(filteredAppointments);
        setStatusMessage("搜索完成，找到 " + filteredAppointments.size() + " 条记录");

      } catch (Exception e) {
        logger.error("搜索预约时发生错误", e);
        setStatusMessage("搜索失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 加载缓存数据
   */
  private void loadCacheData() {
    CompletableFuture.runAsync(() -> {
      try {
        // 加载检查组缓存
        List<CheckGroup> checkGroups = checkGroupService.getAllActiveCheckGroups();
        if (checkGroups != null) {
          for (CheckGroup group : checkGroups) {
            checkGroupCache.put(group.getGroupId(), group);
          }
        }

        // 加载用户缓存 - 直接使用UserMapper
        List<User> users = userMapper.selectList(null);
        if (users != null) {
          for (User user : users) {
            userCache.put(user.getUserId(), user);
          }
        }

      } catch (Exception e) {
        logger.error("加载缓存数据失败", e);
      }
    });
  }

  /**
   * 获取检查组名称
   */
  public String getCheckGroupName(Integer groupId) {
    if (groupId == null) {
      return "未知检查组";
    }

    CheckGroup group = checkGroupCache.get(groupId);
    if (group != null) {
      return group.getGroupName() + " (" + group.getGroupCode() + ")";
    }

    return "检查组_" + groupId;
  }

  /**
   * 获取用户名称
   */
  public String getUserName(Integer userId) {
    if (userId == null) {
      return "未知用户";
    }

    User user = userCache.get(userId);
    if (user != null) {
      String realName = user.getUname();
      if (realName != null && !realName.trim().isEmpty()) {
        return user.getUsername() + " (" + realName + ")";
      } else {
        return user.getUsername();
      }
    }

    return "用户_" + userId;
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    loadAppointmentsCommand();
    loadCacheData();
  }

  /**
   * 清空搜索条件
   */
  public void clearSearchConditions() {
    setSearchKeyword("");
    setSelectedStatus("全部");
    setSelectedUserId(null);
    setSelectedDateRange("全部");
    loadAppointmentsCommand();
  }

  // Getters and Setters
  public List<Appointment> getAppointmentList() {
    return appointmentList;
  }

  public void setAppointmentList(List<Appointment> appointmentList) {
    List<Appointment> oldValue = this.appointmentList;
    this.appointmentList = appointmentList;
    firePropertyChange("appointmentList", oldValue, appointmentList);
  }

  public Appointment getSelectedAppointment() {
    return selectedAppointment;
  }

  public void setSelectedAppointment(Appointment selectedAppointment) {
    Appointment oldValue = this.selectedAppointment;
    this.selectedAppointment = selectedAppointment;
    firePropertyChange("selectedAppointment", oldValue, selectedAppointment);
  }

  public String getSearchKeyword() {
    return searchKeyword;
  }

  public void setSearchKeyword(String searchKeyword) {
    String oldValue = this.searchKeyword;
    this.searchKeyword = searchKeyword;
    firePropertyChange("searchKeyword", oldValue, searchKeyword);
  }

  public String getSelectedStatus() {
    return selectedStatus;
  }

  public void setSelectedStatus(String selectedStatus) {
    String oldValue = this.selectedStatus;
    this.selectedStatus = selectedStatus;
    firePropertyChange("selectedStatus", oldValue, selectedStatus);
    // 状态改变时重新加载数据
    loadAppointmentsCommand();
  }

  public Integer getSelectedUserId() {
    return selectedUserId;
  }

  public void setSelectedUserId(Integer selectedUserId) {
    Integer oldValue = this.selectedUserId;
    this.selectedUserId = selectedUserId;
    firePropertyChange("selectedUserId", oldValue, selectedUserId);
  }

  public String getSelectedDateRange() {
    return selectedDateRange;
  }

  public void setSelectedDateRange(String selectedDateRange) {
    String oldValue = this.selectedDateRange;
    this.selectedDateRange = selectedDateRange;
    firePropertyChange("selectedDateRange", oldValue, selectedDateRange);
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

  public String[] getAppointmentStatuses() {
    return appointmentStatuses;
  }

  /**
   * 设置页面跳转回调
   */
  public void setNavigationCallback(NavigationCallback callback) {
    this.navigationCallback = callback;
  }

  /**
   * 跳转到健康数据录入页面命令
   */
  public void navigateToHealthDataEntryCommand(Appointment appointment) {
    if (navigationCallback != null && appointment != null) {
      if (!"已完成".equals(appointment.getStatus())) {
        logger.warn("尝试为非已完成状态的预约录入数据: {}", appointment.getAppointmentId());
        setStatusMessage("只能为已完成的预约录入体检数据");
        return;
      }

      logger.info("跳转到健康数据录入页面，预约ID: {}", appointment.getAppointmentId());
      navigationCallback.navigateToHealthDataEntry(appointment);
    } else {
      logger.warn("导航回调未设置或预约对象为空");
      setStatusMessage("页面跳转失败，请重试");
    }
  }
}