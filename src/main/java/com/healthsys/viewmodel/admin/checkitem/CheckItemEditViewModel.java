package com.healthsys.viewmodel.admin.checkitem;

import com.healthsys.model.entity.CheckItem;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.impl.CheckItemServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import com.healthsys.viewmodel.common.NotificationViewModel;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 检查项编辑ViewModel
 * 管理单个检查项实体的新建和编辑操作
 * 
 * @author HealthSys Team
 */
public class CheckItemEditViewModel extends BaseViewModel {

  // 服务层
  private ICheckItemService checkItemService;

  // 通知ViewModel
  private NotificationViewModel notificationViewModel;

  // 数据属性
  private CheckItem checkItem;
  private boolean editMode = false; // false=新增模式, true=编辑模式
  private boolean submitting = false;

  // 表单字段
  private String itemCode = "";
  private String itemName = "";
  private String referenceVal = "";
  private String unit = "";

  // 按钮状态
  private boolean submitButtonEnabled = true;
  private boolean cancelButtonEnabled = true;

  // 验证状态
  private boolean itemCodeValid = false;
  private boolean itemNameValid = false;
  private String itemCodeError = "";
  private String itemNameError = "";

  /**
   * 构造函数（新增模式）
   */
  public CheckItemEditViewModel() {
    this(null);
  }

  /**
   * 构造函数
   * 
   * @param checkItem 要编辑的检查项，null表示新增模式
   */
  public CheckItemEditViewModel(CheckItem checkItem) {
    super();
    this.checkItemService = new CheckItemServiceImpl();
    this.notificationViewModel = new NotificationViewModel();

    if (checkItem != null) {
      // 编辑模式
      this.editMode = true;
      this.checkItem = checkItem;
      loadCheckItemData();
    } else {
      // 新增模式
      this.editMode = false;
      this.checkItem = new CheckItem();
      resetForm();
    }
  }

  /**
   * 加载检查项数据到表单
   */
  private void loadCheckItemData() {
    if (checkItem != null) {
      setItemCode(checkItem.getItemCode() != null ? checkItem.getItemCode() : "");
      setItemName(checkItem.getItemName() != null ? checkItem.getItemName() : "");
      setReferenceVal(checkItem.getReferenceVal() != null ? checkItem.getReferenceVal() : "");
      setUnit(checkItem.getUnit() != null ? checkItem.getUnit() : "");
    }
    validateForm();
  }

  /**
   * 重置表单
   */
  public void resetForm() {
    setItemCode("");
    setItemName("");
    setReferenceVal("");
    setUnit("");
    clearValidationErrors();
  }

  /**
   * 清空验证错误
   */
  private void clearValidationErrors() {
    setItemCodeError("");
    setItemNameError("");
    setItemCodeValid(true);
    setItemNameValid(true);
  }

  /**
   * 验证表单
   */
  public void validateForm() {
    validateItemCode();
    validateItemName();
    updateSubmitButtonState();
  }

  /**
   * 验证检查项代码
   */
  private void validateItemCode() {
    String code = getItemCode().trim();

    if (StrUtil.isBlank(code)) {
      setItemCodeError("检查项代码不能为空");
      setItemCodeValid(false);
      return;
    }

    if (code.length() > 50) {
      setItemCodeError("检查项代码长度不能超过50个字符");
      setItemCodeValid(false);
      return;
    }

    // 简单的代码格式验证
    if (!code.matches("^[a-zA-Z0-9_]+$")) {
      setItemCodeError("检查项代码只能包含字母、数字和下划线");
      setItemCodeValid(false);
      return;
    }

    setItemCodeError("");
    setItemCodeValid(true);
  }

  /**
   * 验证检查项名称
   */
  private void validateItemName() {
    String name = getItemName().trim();

    if (StrUtil.isBlank(name)) {
      setItemNameError("检查项名称不能为空");
      setItemNameValid(false);
      return;
    }

    if (name.length() > 100) {
      setItemNameError("检查项名称长度不能超过100个字符");
      setItemNameValid(false);
      return;
    }

    setItemNameError("");
    setItemNameValid(true);
  }

  /**
   * 更新提交按钮状态
   */
  private void updateSubmitButtonState() {
    boolean enabled = itemCodeValid && itemNameValid && !submitting;
    setSubmitButtonEnabled(enabled);
  }

  /**
   * 提交表单
   */
  public void submit() {
    // 最终验证
    validateForm();

    if (!itemCodeValid || !itemNameValid) {
      notificationViewModel.showWarning("请修正表单中的错误后重试");
      return;
    }

    if (submitting) {
      return; // 防止重复提交
    }

    setSubmitting(true);
    setSubmitButtonEnabled(false);
    setCancelButtonEnabled(false);

    // 准备数据
    CheckItem submitItem = prepareCheckItemData();

    // 在后台线程执行保存操作
    CompletableFuture.supplyAsync(() -> {
      try {
        if (editMode) {
          return checkItemService.updateCheckItem(submitItem);
        } else {
          return checkItemService.addCheckItem(submitItem);
        }
      } catch (Exception e) {
        throw new RuntimeException((editMode ? "更新" : "添加") + "检查项失败: " + e.getMessage(), e);
      }
    }).thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        setSubmitting(false);
        setSubmitButtonEnabled(true);
        setCancelButtonEnabled(true);

        if (success) {
          String message = editMode ? "更新检查项成功" : "添加检查项成功";
          notificationViewModel.showSuccess(message);

          // 通知视图层操作成功
          firePropertyChange("submitSuccess", false, true);
        } else {
          String message = editMode ? "更新检查项失败" : "添加检查项失败";
          notificationViewModel.showError(message);
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setSubmitting(false);
        setSubmitButtonEnabled(true);
        setCancelButtonEnabled(true);

        String message = throwable.getMessage();
        notificationViewModel.showError(message);
      });
      return null;
    });
  }

  /**
   * 准备检查项数据
   */
  private CheckItem prepareCheckItemData() {
    CheckItem item = editMode ? checkItem : new CheckItem();

    item.setItemCode(getItemCode().trim());
    item.setItemName(getItemName().trim());
    item.setReferenceVal(StrUtil.isNotBlank(getReferenceVal()) ? getReferenceVal().trim() : null);
    item.setUnit(StrUtil.isNotBlank(getUnit()) ? getUnit().trim() : null);

    if (!editMode) {
      // 新增时设置默认值
      LocalDateTime now = LocalDateTime.now();
      item.setCreatedAt(now);
      item.setUpdatedAt(now);
      item.setIsActive(true);
      // TODO: 设置创建者ID（从当前登录用户获取）
    } else {
      // 编辑时只更新修改时间
      item.setUpdatedAt(LocalDateTime.now());
    }

    return item;
  }

  /**
   * 取消操作
   */
  public void cancel() {
    // 通知视图层取消操作
    firePropertyChange("cancelled", false, true);
  }

  /**
   * 异步验证检查项代码是否重复
   */
  public void validateItemCodeAsync() {
    String code = getItemCode().trim();

    if (StrUtil.isBlank(code) || !itemCodeValid) {
      return; // 如果代码为空或格式不正确，不进行重复检查
    }

    // 在后台线程检查代码是否重复
    CompletableFuture.supplyAsync(() -> {
      try {
        Integer excludeId = editMode && checkItem != null ? checkItem.getItemId() : null;
        return checkItemService.isItemCodeExists(code, excludeId);
      } catch (Exception e) {
        return false; // 发生异常时假设不重复
      }
    }).thenAccept(exists -> {
      SwingUtilities.invokeLater(() -> {
        if (exists) {
          setItemCodeError("检查项代码已存在");
          setItemCodeValid(false);
        } else if (StrUtil.isNotBlank(getItemCodeError()) &&
            getItemCodeError().contains("已存在")) {
          // 如果当前错误是重复错误，清除它
          validateItemCode();
        }
        updateSubmitButtonState();
      });
    });
  }

  // Getter和Setter方法

  public CheckItem getCheckItem() {
    return checkItem;
  }

  public boolean isEditMode() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
    firePropertyChange("editMode", !editMode, editMode);
  }

  public void loadCheckItem(CheckItem checkItem) {
    this.checkItem = checkItem;
    if (checkItem != null) {
      setEditMode(true);
      loadCheckItemData();
    } else {
      setEditMode(false);
      this.checkItem = new CheckItem();
      resetForm();
    }
  }

  public boolean isSubmitting() {
    return submitting;
  }

  public void setSubmitting(boolean submitting) {
    this.submitting = setProperty(this.submitting, submitting, "submitting");
  }

  public String getItemCode() {
    return itemCode;
  }

  public void setItemCode(String itemCode) {
    this.itemCode = setProperty(this.itemCode, itemCode != null ? itemCode : "", "itemCode");
    validateItemCode();
    updateSubmitButtonState();
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = setProperty(this.itemName, itemName != null ? itemName : "", "itemName");
    validateItemName();
    updateSubmitButtonState();
  }

  public String getReferenceVal() {
    return referenceVal;
  }

  public void setReferenceVal(String referenceVal) {
    this.referenceVal = setProperty(this.referenceVal, referenceVal != null ? referenceVal : "", "referenceVal");
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = setProperty(this.unit, unit != null ? unit : "", "unit");
  }

  public boolean isSubmitButtonEnabled() {
    return submitButtonEnabled;
  }

  public void setSubmitButtonEnabled(boolean submitButtonEnabled) {
    this.submitButtonEnabled = setProperty(this.submitButtonEnabled, submitButtonEnabled, "submitButtonEnabled");
  }

  public boolean isCancelButtonEnabled() {
    return cancelButtonEnabled;
  }

  public void setCancelButtonEnabled(boolean cancelButtonEnabled) {
    this.cancelButtonEnabled = setProperty(this.cancelButtonEnabled, cancelButtonEnabled, "cancelButtonEnabled");
  }

  public boolean isItemCodeValid() {
    return itemCodeValid;
  }

  public void setItemCodeValid(boolean itemCodeValid) {
    this.itemCodeValid = setProperty(this.itemCodeValid, itemCodeValid, "itemCodeValid");
  }

  public boolean isItemNameValid() {
    return itemNameValid;
  }

  public void setItemNameValid(boolean itemNameValid) {
    this.itemNameValid = setProperty(this.itemNameValid, itemNameValid, "itemNameValid");
  }

  public String getItemCodeError() {
    return itemCodeError;
  }

  public void setItemCodeError(String itemCodeError) {
    this.itemCodeError = setProperty(this.itemCodeError, itemCodeError != null ? itemCodeError : "", "itemCodeError");
  }

  public String getItemNameError() {
    return itemNameError;
  }

  public void setItemNameError(String itemNameError) {
    this.itemNameError = setProperty(this.itemNameError, itemNameError != null ? itemNameError : "", "itemNameError");
  }

  public NotificationViewModel getNotificationViewModel() {
    return notificationViewModel;
  }

  /**
   * 获取窗口标题
   */
  public String getWindowTitle() {
    return editMode ? "编辑检查项" : "添加检查项";
  }
}