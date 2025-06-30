package com.healthsys.viewmodel.admin.checkgroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.swing.SwingUtilities;

import com.healthsys.model.entity.CheckGroup;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.service.impl.CheckItemServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;

import cn.hutool.core.util.StrUtil;

/**
 * 检查组编辑视图模型。
 * 支持体检分组的编辑与校验。
 * 
 * @author 梦辰
 */
public class CheckGroupEditViewModel extends BaseViewModel {

  // 服务层
  private ICheckGroupService checkGroupService;
  private ICheckItemService checkItemService;

  // 数据属性
  private CheckGroup checkGroup;
  private boolean editMode = false; // false=新增模式, true=编辑模式
  private boolean submitting = false;

  // 表单字段
  private String groupCode = "";
  private String groupName = "";
  private String description = "";

  // 检查项相关
  private List<CheckItem> allCheckItems = new ArrayList<>();
  private Set<Integer> selectedCheckItemIds = new HashSet<>();
  private boolean loadingCheckItems = false;

  // 验证状态
  private boolean groupCodeValid = false;
  private boolean groupNameValid = false;
  private String groupCodeError = "";
  private String groupNameError = "";

  /**
   * 构造函数
   */
  public CheckGroupEditViewModel() {
    this(null);
  }

  /**
   * 构造函数
   * 
   * @param checkGroup 要编辑的检查组，null表示新增模式
   */
  public CheckGroupEditViewModel(CheckGroup checkGroup) {
    super();
    this.checkGroupService = new CheckGroupServiceImpl();
    this.checkItemService = new CheckItemServiceImpl();

    // 加载所有检查项
    loadAllCheckItems();

    if (checkGroup != null) {
      // 编辑模式
      this.editMode = true;
      this.checkGroup = checkGroup;
      loadCheckGroupData();
    } else {
      // 新增模式
      this.editMode = false;
      this.checkGroup = new CheckGroup();
      resetForm();
    }
  }

  /**
   * 加载所有检查项
   */
  private void loadAllCheckItems() {
    setLoadingCheckItems(true);

    CompletableFuture.supplyAsync(() -> {
      try {
        return checkItemService.getAllActiveCheckItems();
      } catch (Exception e) {
        throw new RuntimeException("加载检查项列表失败: " + e.getMessage(), e);
      }
    }).thenAccept(items -> {
      SwingUtilities.invokeLater(() -> {
        setAllCheckItems(items);
        setLoadingCheckItems(false);
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setLoadingCheckItems(false);
        firePropertyChange("loadCheckItemsError", null, throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 加载检查组数据到表单
   */
  private void loadCheckGroupData() {
    if (checkGroup != null) {
      setGroupCode(checkGroup.getGroupCode() != null ? checkGroup.getGroupCode() : "");
      setGroupName(checkGroup.getGroupName() != null ? checkGroup.getGroupName() : "");
      setDescription(checkGroup.getDescription() != null ? checkGroup.getDescription() : "");

      // 加载关联的检查项
      loadSelectedCheckItems();
    }
    validateForm();
  }

  /**
   * 加载已选择的检查项
   */
  private void loadSelectedCheckItems() {
    if (checkGroup == null || checkGroup.getGroupId() == null) {
      return;
    }

    CompletableFuture.supplyAsync(() -> {
      try {
        return checkGroupService.getCheckItemIdsByGroupId(checkGroup.getGroupId());
      } catch (Exception e) {
        throw new RuntimeException("加载检查组关联项失败: " + e.getMessage(), e);
      }
    }).thenAccept(itemIds -> {
      SwingUtilities.invokeLater(() -> {
        selectedCheckItemIds.clear();
        if (itemIds != null) {
          selectedCheckItemIds.addAll(itemIds);
        }
        firePropertyChange("selectedCheckItemIds", null, selectedCheckItemIds);
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        firePropertyChange("loadSelectedItemsError", null, throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 重置表单
   */
  public void resetForm() {
    setGroupCode("");
    setGroupName("");
    setDescription("");
    selectedCheckItemIds.clear();
    firePropertyChange("selectedCheckItemIds", null, selectedCheckItemIds);
    clearValidationErrors();
  }

  /**
   * 清空验证错误
   */
  private void clearValidationErrors() {
    setGroupCodeError("");
    setGroupNameError("");
    setGroupCodeValid(true);
    setGroupNameValid(true);
  }

  /**
   * 验证表单
   */
  public void validateForm() {
    validateGroupCode();
    validateGroupName();
    firePropertyChange("submitButtonEnabled", null, isSubmitButtonEnabled());
  }

  /**
   * 验证检查组代码
   */
  private void validateGroupCode() {
    String code = getGroupCode().trim();

    if (StrUtil.isBlank(code)) {
      setGroupCodeError("检查组代码不能为空");
      setGroupCodeValid(false);
      return;
    }

    if (code.length() > 50) {
      setGroupCodeError("检查组代码长度不能超过50个字符");
      setGroupCodeValid(false);
      return;
    }

    if (!code.matches("^[a-zA-Z0-9_]+$")) {
      setGroupCodeError("检查组代码只能包含字母、数字和下划线");
      setGroupCodeValid(false);
      return;
    }

    setGroupCodeError("");
    setGroupCodeValid(true);
  }

  /**
   * 验证检查组名称
   */
  private void validateGroupName() {
    String name = getGroupName().trim();

    if (StrUtil.isBlank(name)) {
      setGroupNameError("检查组名称不能为空");
      setGroupNameValid(false);
      return;
    }

    if (name.length() > 100) {
      setGroupNameError("检查组名称长度不能超过100个字符");
      setGroupNameValid(false);
      return;
    }

    setGroupNameError("");
    setGroupNameValid(true);
  }

  /**
   * 异步验证检查组代码重复
   */
  public void validateGroupCodeAsync() {
    String code = getGroupCode().trim();
    if (StrUtil.isBlank(code) || !groupCodeValid) {
      return;
    }

    Integer excludeId = editMode && checkGroup != null ? checkGroup.getGroupId() : null;

    CompletableFuture.supplyAsync(() -> {
      try {
        return checkGroupService.isGroupCodeExists(code, excludeId);
      } catch (Exception e) {
        return false;
      }
    }).thenAccept(exists -> {
      SwingUtilities.invokeLater(() -> {
        if (exists) {
          setGroupCodeError("检查组代码已存在");
          setGroupCodeValid(false);
        } else if (StrUtil.isNotBlank(getGroupCodeError()) && getGroupCodeError().contains("已存在")) {
          // 清除重复错误，重新验证格式
          validateGroupCode();
        }
        firePropertyChange("submitButtonEnabled", null, isSubmitButtonEnabled());
      });
    });
  }

  /**
   * 异步验证检查组名称重复
   */
  public void validateGroupNameAsync() {
    String name = getGroupName().trim();
    if (StrUtil.isBlank(name) || !groupNameValid) {
      return;
    }

    Integer excludeId = editMode && checkGroup != null ? checkGroup.getGroupId() : null;

    CompletableFuture.supplyAsync(() -> {
      try {
        return checkGroupService.isGroupNameExists(name, excludeId);
      } catch (Exception e) {
        return false;
      }
    }).thenAccept(exists -> {
      SwingUtilities.invokeLater(() -> {
        if (exists) {
          setGroupNameError("检查组名称已存在");
          setGroupNameValid(false);
        } else if (StrUtil.isNotBlank(getGroupNameError()) && getGroupNameError().contains("已存在")) {
          // 清除重复错误，重新验证格式
          validateGroupName();
        }
        firePropertyChange("submitButtonEnabled", null, isSubmitButtonEnabled());
      });
    });
  }

  /**
   * 提交表单
   */
  public void submit() {
    validateForm();

    if (!groupCodeValid || !groupNameValid) {
      firePropertyChange("submitError", null, "请修正表单中的错误后重试");
      return;
    }

    if (selectedCheckItemIds.isEmpty()) {
      firePropertyChange("submitError", null, "请至少选择一个检查项");
      return;
    }

    if (submitting) {
      return;
    }

    setSubmitting(true);

    CompletableFuture.supplyAsync(() -> {
      try {
        CheckGroup groupToSave = prepareCheckGroupData();
        List<Integer> itemIds = new ArrayList<>(selectedCheckItemIds);

        if (editMode) {
          return checkGroupService.updateCheckGroup(groupToSave, itemIds);
        } else {
          return checkGroupService.addCheckGroup(groupToSave, itemIds);
        }
      } catch (Exception e) {
        throw new RuntimeException((editMode ? "更新" : "添加") + "检查组失败: " + e.getMessage(), e);
      }
    }).thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        setSubmitting(false);
        if (success) {
          firePropertyChange("submitSuccess", false, true);
        } else {
          firePropertyChange("submitError", null, (editMode ? "更新" : "添加") + "检查组失败");
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setSubmitting(false);
        firePropertyChange("submitError", null, throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 准备检查组数据
   */
  private CheckGroup prepareCheckGroupData() {
    CheckGroup group = new CheckGroup();

    if (editMode && checkGroup != null) {
      group.setGroupId(checkGroup.getGroupId());
      group.setCreatedAt(checkGroup.getCreatedAt());
      group.setCreatedBy(checkGroup.getCreatedBy());
    }

    group.setGroupCode(getGroupCode().trim());
    group.setGroupName(getGroupName().trim());
    group.setDescription(StrUtil.isNotBlank(getDescription()) ? getDescription().trim() : null);
    group.setUpdatedAt(LocalDateTime.now());

    if (group.getIsActive() == null) {
      group.setIsActive(true);
    }

    return group;
  }

  /**
   * 取消操作
   */
  public void cancel() {
    firePropertyChange("cancelled", false, true);
  }

  // Getter和Setter方法

  public CheckGroup getCheckGroup() {
    return checkGroup;
  }

  public boolean isEditMode() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
    firePropertyChange("editMode", !editMode, editMode);
  }

  public void loadCheckGroup(CheckGroup checkGroup) {
    this.checkGroup = checkGroup;
    if (checkGroup != null) {
      setEditMode(true);
      loadCheckGroupData();
    } else {
      setEditMode(false);
      this.checkGroup = new CheckGroup();
      resetForm();
    }
  }

  public boolean isSubmitting() {
    return submitting;
  }

  public void setSubmitting(boolean submitting) {
    this.submitting = setProperty(this.submitting, submitting, "submitting");
  }

  public String getGroupCode() {
    return groupCode;
  }

  public void setGroupCode(String groupCode) {
    this.groupCode = setProperty(this.groupCode, groupCode != null ? groupCode : "", "groupCode");
    validateGroupCode();
    firePropertyChange("submitButtonEnabled", null, isSubmitButtonEnabled());
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = setProperty(this.groupName, groupName != null ? groupName : "", "groupName");
    validateGroupName();
    firePropertyChange("submitButtonEnabled", null, isSubmitButtonEnabled());
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = setProperty(this.description, description != null ? description : "", "description");
  }

  public List<CheckItem> getAllCheckItems() {
    return allCheckItems;
  }

  public void setAllCheckItems(List<CheckItem> allCheckItems) {
    this.allCheckItems = allCheckItems != null ? allCheckItems : new ArrayList<>();
    firePropertyChange("allCheckItems", null, this.allCheckItems);
  }

  public Set<Integer> getSelectedCheckItemIds() {
    return selectedCheckItemIds;
  }

  public void setSelectedCheckItemIds(Set<Integer> selectedCheckItemIds) {
    this.selectedCheckItemIds = selectedCheckItemIds != null ? selectedCheckItemIds : new HashSet<>();
    firePropertyChange("selectedCheckItemIds", null, this.selectedCheckItemIds);
  }

  public boolean isLoadingCheckItems() {
    return loadingCheckItems;
  }

  public void setLoadingCheckItems(boolean loadingCheckItems) {
    this.loadingCheckItems = setProperty(this.loadingCheckItems, loadingCheckItems, "loadingCheckItems");
  }

  public boolean isSubmitButtonEnabled() {
    return groupCodeValid && groupNameValid && !submitting;
  }

  public boolean isCancelButtonEnabled() {
    return !submitting;
  }

  public boolean isGroupCodeValid() {
    return groupCodeValid;
  }

  public void setGroupCodeValid(boolean groupCodeValid) {
    this.groupCodeValid = setProperty(this.groupCodeValid, groupCodeValid, "groupCodeValid");
  }

  public boolean isGroupNameValid() {
    return groupNameValid;
  }

  public void setGroupNameValid(boolean groupNameValid) {
    this.groupNameValid = setProperty(this.groupNameValid, groupNameValid, "groupNameValid");
  }

  public String getGroupCodeError() {
    return groupCodeError;
  }

  public void setGroupCodeError(String groupCodeError) {
    this.groupCodeError = setProperty(this.groupCodeError, groupCodeError != null ? groupCodeError : "",
        "groupCodeError");
  }

  public String getGroupNameError() {
    return groupNameError;
  }

  public void setGroupNameError(String groupNameError) {
    this.groupNameError = setProperty(this.groupNameError, groupNameError != null ? groupNameError : "",
        "groupNameError");
  }

  public String getWindowTitle() {
    return editMode ? "编辑检查组" : "新增检查组";
  }
}