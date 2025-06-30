package com.healthsys.viewmodel.user.healthdata;

import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.service.impl.ExaminationResultServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 批量体检数据录入ViewModel
 * 管理检查项列表、输入数据、验证状态，实现批量保存命令和相关业务逻辑
 * 
 * @author AI Assistant
 */
public class BatchExaminationDataEntryViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(BatchExaminationDataEntryViewModel.class);

  private final ICheckGroupService checkGroupService;
  private final IExaminationResultService examinationResultService;
  private final Appointment appointment;

  // 数据属性
  private List<CheckItem> checkItems;
  private boolean isLoading = false;
  private String statusMessage = "";

  // 验证状态
  private boolean isValid = true;
  private String validationMessage = "";

  public BatchExaminationDataEntryViewModel(Appointment appointment) {
    this.appointment = appointment;
    this.checkGroupService = new CheckGroupServiceImpl();
    this.examinationResultService = new ExaminationResultServiceImpl();
    this.checkItems = new ArrayList<>();

    logger.info("初始化批量体检数据录入ViewModel，预约ID: {}", appointment.getAppointmentId());
  }

  /**
   * 加载检查项命令
   */
  public CompletableFuture<Void> loadCheckItemsCommand() {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在加载检查项数据...");

        // 根据检查组ID获取包含的检查项
        List<CheckItem> items = checkGroupService.getCheckItemsByGroupId(appointment.getGroupId());

        if (items != null && !items.isEmpty()) {
          setCheckItems(items);
          setStatusMessage("检查项数据加载完成，共 " + items.size() + " 项");
          logger.info("成功加载检查项数据，检查组ID: {}, 检查项数量: {}",
              appointment.getGroupId(), items.size());
        } else {
          setCheckItems(new ArrayList<>());
          setStatusMessage("该检查组暂无检查项");
          logger.warn("检查组没有关联的检查项，检查组ID: {}", appointment.getGroupId());
        }

      } catch (Exception e) {
        logger.error("加载检查项数据时发生错误", e);
        setStatusMessage("加载检查项数据失败：" + e.getMessage());
        setCheckItems(new ArrayList<>());
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 保存体检结果命令
   */
  public CompletableFuture<Void> saveExaminationResultsCommand(List<ExaminationResult> results) {
    return CompletableFuture.runAsync(() -> {
      try {
        setLoading(true);
        setStatusMessage("正在保存体检数据...");

        // 验证输入数据
        if (!validateExaminationResults(results)) {
          throw new IllegalArgumentException("数据验证失败：" + validationMessage);
        }

        // 批量保存体检结果
        boolean success = examinationResultService.batchSaveExaminationResults(results);

        if (success) {
          setStatusMessage("体检数据保存成功");
          logger.info("成功保存体检数据，预约ID: {}, 数据条数: {}",
              appointment.getAppointmentId(), results.size());
        } else {
          throw new RuntimeException("体检数据保存失败");
        }

      } catch (Exception e) {
        logger.error("保存体检数据时发生错误", e);
        setStatusMessage("保存体检数据失败：" + e.getMessage());
        throw new RuntimeException(e.getMessage(), e);
      } finally {
        setLoading(false);
      }
    });
  }

  /**
   * 验证体检结果数据
   */
  private boolean validateExaminationResults(List<ExaminationResult> results) {
    if (results == null || results.isEmpty()) {
      setValidationMessage("请至少录入一项体检数据");
      setValid(false);
      return false;
    }

    for (ExaminationResult result : results) {
      // 验证必填字段
      if (result.getAppointmentId() == null) {
        setValidationMessage("预约ID不能为空");
        setValid(false);
        return false;
      }

      if (result.getUserId() == null) {
        setValidationMessage("用户ID不能为空");
        setValid(false);
        return false;
      }

      if (result.getGroupId() == null) {
        setValidationMessage("检查组ID不能为空");
        setValid(false);
        return false;
      }

      if (result.getItemId() == null) {
        setValidationMessage("检查项ID不能为空");
        setValid(false);
        return false;
      }

      if (result.getMeasuredValue() == null || result.getMeasuredValue().trim().isEmpty()) {
        setValidationMessage("测量值不能为空");
        setValid(false);
        return false;
      }

      // 验证测量值长度
      if (result.getMeasuredValue().length() > 255) {
        setValidationMessage("测量值不能超过255个字符");
        setValid(false);
        return false;
      }

      // 验证备注长度
      if (result.getResultNotes() != null && result.getResultNotes().length() > 1000) {
        setValidationMessage("备注不能超过1000个字符");
        setValid(false);
        return false;
      }
    }

    setValid(true);
    setValidationMessage("");
    return true;
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    loadCheckItemsCommand();
  }

  // Getter和Setter方法

  public List<CheckItem> getCheckItems() {
    return checkItems;
  }

  public void setCheckItems(List<CheckItem> checkItems) {
    List<CheckItem> oldValue = this.checkItems;
    this.checkItems = checkItems;
    firePropertyChange("checkItems", oldValue, checkItems);
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

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    boolean oldValue = this.isValid;
    this.isValid = valid;
    firePropertyChange("valid", oldValue, valid);
  }

  public String getValidationMessage() {
    return validationMessage;
  }

  public void setValidationMessage(String validationMessage) {
    String oldValue = this.validationMessage;
    this.validationMessage = validationMessage;
    firePropertyChange("validationMessage", oldValue, validationMessage);
  }

  public Appointment getAppointment() {
    return appointment;
  }

  /**
   * 获取预约用户名称
   */
  public String getUserName() {
    // 这里可以通过UserService获取用户名称
    // 为了简化，暂时返回用户ID
    return "用户ID: " + appointment.getUserId();
  }

  /**
   * 获取检查组名称
   */
  public String getCheckGroupName() {
    try {
      return checkGroupService.getCheckGroupNameById(appointment.getGroupId());
    } catch (Exception e) {
      logger.warn("获取检查组名称失败，检查组ID: {}", appointment.getGroupId(), e);
      return "检查组ID: " + appointment.getGroupId();
    }
  }

  /**
   * 获取检查项数量
   */
  public int getCheckItemCount() {
    return checkItems != null ? checkItems.size() : 0;
  }

  /**
   * 检查是否有检查项数据
   */
  public boolean hasCheckItems() {
    return checkItems != null && !checkItems.isEmpty();
  }
}