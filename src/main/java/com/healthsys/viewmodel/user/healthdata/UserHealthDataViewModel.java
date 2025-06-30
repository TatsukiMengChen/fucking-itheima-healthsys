package com.healthsys.viewmodel.user.healthdata;

import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.model.entity.User;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.impl.ExaminationResultServiceImpl;
import com.healthsys.service.impl.CheckItemServiceImpl;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.config.AppContext;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;

/**
 * 用户健康数据管理ViewModel
 * 管理用户自主录入的健康数据
 * 
 * @author AI Assistant
 */
public class UserHealthDataViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(UserHealthDataViewModel.class);

  private final IExaminationResultService examinationResultService;
  private final ICheckItemService checkItemService;
  private final ICheckGroupService checkGroupService;

  // 健康数据列表
  private List<ExaminationResult> healthDataList;

  // 缓存映射，用于显示名称而不是ID
  private java.util.Map<Integer, String> checkItemNameCache;
  private java.util.Map<Integer, String> checkGroupNameCache;

  // 查询相关属性
  private String searchKeyword;
  private String selectedDateRange;
  private Integer selectedAppointmentId; // 添加选中的预约ID

  // 状态属性
  private boolean isLoading;
  private String statusMessage;
  private ExaminationResult selectedHealthData;

  public UserHealthDataViewModel() {
    this.examinationResultService = new ExaminationResultServiceImpl();
    this.checkItemService = new CheckItemServiceImpl();
    this.checkGroupService = new CheckGroupServiceImpl();
    this.healthDataList = new ArrayList<>();
    this.checkItemNameCache = new HashMap<>();
    this.checkGroupNameCache = new HashMap<>();
    this.searchKeyword = "";
    this.selectedDateRange = "全部";
    this.selectedAppointmentId = null; // 初始化为null，表示查询所有预约

    logger.info("用户健康数据ViewModel初始化完成");

    // 初始化数据
    initialize();
  }

  /**
   * 初始化
   */
  public void initialize() {
    loadHealthDataCommand();
  }

  /**
   * 加载健康数据命令
   */
  public CompletableFuture<Void> loadHealthDataCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载健康数据...");

        List<ExaminationResult> results;

        if (selectedAppointmentId != null) {
          // 按预约ID查询
          results = examinationResultService.getExaminationResultsByAppointmentId(selectedAppointmentId);
          logger.info("按预约ID查询体检结果，预约ID: {}, 结果数量: {}",
              selectedAppointmentId, results != null ? results.size() : 0);
        } else {
          // 按用户ID查询所有数据
          results = examinationResultService.getExaminationResultsByUserId(getCurrentUserId());
          logger.info("按用户ID查询体检结果，用户ID: {}, 结果数量: {}",
              getCurrentUserId(), results != null ? results.size() : 0);
        }

        if (results != null && !results.isEmpty()) {
          setHealthDataList(results);
          setStatusMessage("健康数据加载完成，共 " + results.size() + " 条记录");
        } else {
          setHealthDataList(new ArrayList<>());
          if (selectedAppointmentId != null) {
            setStatusMessage("该预约暂无体检结果数据");
          } else {
            setStatusMessage("暂无健康数据");
          }
        }

      } catch (Exception e) {
        logger.error("加载健康数据时发生错误", e);
        setStatusMessage("加载健康数据失败：" + e.getMessage());
        setHealthDataList(new ArrayList<>());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 搜索健康数据命令
   */
  public CompletableFuture<Void> searchHealthDataCommand(String keyword) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setSearchKeyword(keyword);
        setStatusMessage("正在搜索...");

        List<ExaminationResult> allResults;

        if (selectedAppointmentId != null) {
          // 在指定预约的结果中搜索
          allResults = examinationResultService.getExaminationResultsByAppointmentId(selectedAppointmentId);
        } else {
          // 在用户所有结果中搜索
          allResults = examinationResultService.getExaminationResultsByUserId(getCurrentUserId());
        }

        List<ExaminationResult> filteredResults = new ArrayList<>();

        if (allResults != null) {
          for (ExaminationResult result : allResults) {
            if (keyword.isEmpty() ||
                (result.getResultNotes() != null && result.getResultNotes().contains(keyword)) ||
                (result.getMeasuredValue() != null && result.getMeasuredValue().contains(keyword)) ||
                getCheckItemName(result.getItemId()).contains(keyword) ||
                getCheckGroupName(result.getGroupId()).contains(keyword)) {
              filteredResults.add(result);
            }
          }
        }

        setHealthDataList(filteredResults);
        setStatusMessage("搜索完成，找到 " + filteredResults.size() + " 条记录");

      } catch (Exception e) {
        logger.error("搜索健康数据时发生错误", e);
        setStatusMessage("搜索失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 删除健康数据命令
   * 只有管理员才能删除健康数据
   */
  public CompletableFuture<Void> deleteHealthDataCommand(Integer resultId) {
    return CompletableFuture.runAsync(() -> {
      try {
        // 权限检查：只有管理员才能删除
        if (!isCurrentUserAdmin()) {
          setStatusMessage("权限不足：只有管理员才能删除健康数据");
          logger.warn("普通用户尝试删除健康数据，用户ID: {}", getCurrentUserId());
          return;
        }

        setLoading(true);
        setStatusMessage("正在删除健康数据...");

        boolean success = examinationResultService.deleteExaminationResult(resultId);

        if (success) {
          setStatusMessage("健康数据删除成功");
          // 重新加载数据
          loadHealthDataCommand();
        } else {
          setStatusMessage("健康数据删除失败");
        }

      } catch (Exception e) {
        logger.error("删除健康数据时发生错误", e);
        setStatusMessage("删除失败：" + e.getMessage());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    loadHealthDataCommand();
  }

  /**
   * 清空搜索条件
   */
  public void clearSearchConditions() {
    setSearchKeyword("");
    setSelectedDateRange("全部");
    loadHealthDataCommand();
  }

  /**
   * 获取当前登录用户ID
   */
  private Integer getCurrentUserId() {
    User currentUser = AppContext.getCurrentUser();
    if (currentUser == null) {
      throw new SecurityException("用户未登录");
    }
    return currentUser.getUserId();
  }

  /**
   * 检查当前用户是否为管理员
   */
  public boolean isCurrentUserAdmin() {
    User currentUser = AppContext.getCurrentUser();
    if (currentUser == null) {
      return false;
    }
    return UserRoleEnum.ADMIN.equals(currentUser.getRole()) ||
        UserRoleEnum.SUPER_ADMIN.equals(currentUser.getRole());
  }

  /**
   * 获取检查项名称
   */
  public String getCheckItemName(Integer itemId) {
    if (itemId == null) {
      return "未知检查项";
    }

    // 先从缓存中查找
    if (checkItemNameCache.containsKey(itemId)) {
      return checkItemNameCache.get(itemId);
    }

    try {
      CheckItem checkItem = checkItemService.getCheckItemById(itemId);
      String itemName = checkItem != null ? checkItem.getItemName() : "检查项_" + itemId;
      checkItemNameCache.put(itemId, itemName);
      return itemName;
    } catch (Exception e) {
      logger.warn("获取检查项名称失败，ID: {}", itemId, e);
      return "检查项_" + itemId;
    }
  }

  /**
   * 获取检查组名称
   */
  public String getCheckGroupName(Integer groupId) {
    if (groupId == null) {
      return "未知检查组";
    }

    // 先从缓存中查找
    if (checkGroupNameCache.containsKey(groupId)) {
      return checkGroupNameCache.get(groupId);
    }

    try {
      String groupName = checkGroupService.getCheckGroupNameById(groupId);
      if (groupName == null) {
        groupName = "检查组_" + groupId;
      }
      checkGroupNameCache.put(groupId, groupName);
      return groupName;
    } catch (Exception e) {
      logger.warn("获取检查组名称失败，ID: {}", groupId, e);
      return "检查组_" + groupId;
    }
  }

  /**
   * 获取检查项参考值
   */
  public String getCheckItemReferenceValue(Integer itemId) {
    if (itemId == null) {
      return "-";
    }

    try {
      CheckItem checkItem = checkItemService.getCheckItemById(itemId);
      if (checkItem != null && checkItem.getReferenceVal() != null) {
        return checkItem.getReferenceVal();
      }
      return "-";
    } catch (Exception e) {
      logger.warn("获取检查项参考值失败，ID: {}", itemId, e);
      return "-";
    }
  }

  /**
   * 设置选中的预约ID并重新加载数据
   */
  public void setSelectedAppointmentId(Integer appointmentId) {
    if (!java.util.Objects.equals(this.selectedAppointmentId, appointmentId)) {
      this.selectedAppointmentId = appointmentId;
      logger.info("切换预约筛选，预约ID: {}", appointmentId);

      // 清空搜索关键词
      setSearchKeyword("");

      // 重新加载数据
      loadHealthDataCommand();

      // 通知属性变化
      firePropertyChange("selectedAppointmentId", null, appointmentId);
    }
  }

  /**
   * 获取选中的预约ID
   */
  public Integer getSelectedAppointmentId() {
    return selectedAppointmentId;
  }

  // Getters and Setters
  public List<ExaminationResult> getHealthDataList() {
    return healthDataList;
  }

  public void setHealthDataList(List<ExaminationResult> healthDataList) {
    List<ExaminationResult> oldValue = this.healthDataList;
    this.healthDataList = healthDataList;
    firePropertyChange("healthDataList", oldValue, healthDataList);
  }

  public String getSearchKeyword() {
    return searchKeyword;
  }

  public void setSearchKeyword(String searchKeyword) {
    String oldValue = this.searchKeyword;
    this.searchKeyword = searchKeyword;
    firePropertyChange("searchKeyword", oldValue, searchKeyword);
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

  public ExaminationResult getSelectedHealthData() {
    return selectedHealthData;
  }

  public void setSelectedHealthData(ExaminationResult selectedHealthData) {
    ExaminationResult oldValue = this.selectedHealthData;
    this.selectedHealthData = selectedHealthData;
    firePropertyChange("selectedHealthData", oldValue, selectedHealthData);
  }
}