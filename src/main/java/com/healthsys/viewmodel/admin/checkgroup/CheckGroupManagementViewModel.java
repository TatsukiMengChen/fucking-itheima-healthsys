package com.healthsys.viewmodel.admin.checkgroup;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import com.healthsys.viewmodel.common.NotificationViewModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 检查组管理ViewModel
 * 管理检查组列表、搜索条件、分页信息和相关命令
 * 
 * @author HealthSys Team
 */
public class CheckGroupManagementViewModel extends BaseViewModel {

  // 服务层
  private ICheckGroupService checkGroupService;

  // 通知ViewModel
  private NotificationViewModel notificationViewModel;

  // 数据属性
  private List<CheckGroup> checkGroupList = new ArrayList<>();
  private CheckGroup selectedCheckGroup;
  private String searchName = "";
  private String searchCode = "";
  private boolean loading = false;

  // 分页属性
  private int currentPage = 1;
  private int pageSize = 10;
  private int totalPages = 1;
  private int totalRecords = 0;

  // 操作状态
  private boolean addButtonEnabled = true;
  private boolean editButtonEnabled = false;
  private boolean deleteButtonEnabled = false;
  private boolean searchButtonEnabled = true;

  /**
   * 构造函数
   */
  public CheckGroupManagementViewModel() {
    super();
    this.checkGroupService = new CheckGroupServiceImpl();
    this.notificationViewModel = new NotificationViewModel();

    // 初始化加载数据
    loadCheckGroups();
  }

  /**
   * 加载检查组列表
   */
  public void loadCheckGroups() {
    if (loading) {
      return; // 防止重复加载
    }

    setLoading(true);
    setSearchButtonEnabled(false);

    // 使用CompletableFuture在后台线程执行
    CompletableFuture.supplyAsync(() -> {
      try {
        Page<CheckGroup> page = checkGroupService.queryCheckGroups(
            searchName.trim().isEmpty() ? null : searchName.trim(),
            searchCode.trim().isEmpty() ? null : searchCode.trim(),
            currentPage,
            pageSize);
        return page;
      } catch (Exception e) {
        throw new RuntimeException("加载检查组列表失败: " + e.getMessage(), e);
      }
    }).thenAccept(page -> {
      // 在EDT线程中更新UI
      SwingUtilities.invokeLater(() -> {
        updatePageData(page);
        setLoading(false);
        setSearchButtonEnabled(true);
      });
    }).exceptionally(throwable -> {
      // 在EDT线程中处理异常
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        setSearchButtonEnabled(true);
        firePropertyChange("operationError", null, "加载检查组列表失败: " + throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 搜索检查组
   */
  public void searchCheckGroups() {
    setCurrentPage(1); // 重置到第一页
    loadCheckGroups();
  }

  /**
   * 清空搜索条件
   */
  public void clearSearch() {
    setSearchName("");
    setSearchCode("");
    setCurrentPage(1);
    loadCheckGroups();
  }

  /**
   * 添加检查组
   */
  public void addCheckGroup() {
    // 权限检查
    if (!AppContext.isAdmin()) {
      firePropertyChange("operationError", null, "您没有权限执行此操作");
      return;
    }

    // 通过事件通知视图层打开添加对话框
    firePropertyChange("addCheckGroupRequested", false, true);
  }

  /**
   * 编辑检查组
   */
  public void editCheckGroup() {
    // 权限检查
    if (!AppContext.isAdmin()) {
      firePropertyChange("operationError", null, "您没有权限执行此操作");
      return;
    }

    if (selectedCheckGroup == null) {
      firePropertyChange("operationError", null, "请先选择要编辑的检查组");
      return;
    }

    // 通过事件通知视图层打开编辑对话框
    firePropertyChange("editCheckGroupRequested", null, selectedCheckGroup);
  }

  /**
   * 删除检查组
   */
  public void deleteCheckGroup() {
    // 权限检查
    if (!AppContext.isAdmin()) {
      firePropertyChange("operationError", null, "您没有权限执行此操作");
      return;
    }

    if (selectedCheckGroup == null) {
      firePropertyChange("operationError", null, "请先选择要删除的检查组");
      return;
    }

    // 确认删除
    int result = JOptionPane.showConfirmDialog(
        null,
        "确定要删除检查组 \"" + selectedCheckGroup.getGroupName() + "\" 吗？\n" +
            "删除后不可恢复！\n\n" +
            "注意：如果此检查组正在被使用，删除操作将失败。",
        "确认删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (result != JOptionPane.YES_OPTION) {
      return;
    }

    setLoading(true);

    // 在后台线程执行删除操作
    CompletableFuture.supplyAsync(() -> {
      try {
        return checkGroupService.deleteCheckGroup(selectedCheckGroup.getGroupId());
      } catch (Exception e) {
        throw new RuntimeException("删除检查组失败: " + e.getMessage(), e);
      }
    }).thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        if (success) {
          firePropertyChange("operationSuccess", false, true);
          setSelectedCheckGroup(null); // 清空选择
          loadCheckGroups(); // 重新加载列表
        } else {
          firePropertyChange("operationError", null, "删除检查组失败，可能正在被使用");
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        firePropertyChange("operationError", null, throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 删除指定ID的检查组
   */
  public void deleteCheckGroup(Integer groupId) {
    if (groupId == null) {
      return;
    }

    setLoading(true);

    // 在后台线程执行删除操作
    CompletableFuture.supplyAsync(() -> {
      try {
        return checkGroupService.deleteCheckGroup(groupId);
      } catch (Exception e) {
        throw new RuntimeException("删除检查组失败: " + e.getMessage(), e);
      }
    }).thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        if (success) {
          firePropertyChange("operationSuccess", false, true);
          setSelectedCheckGroup(null); // 清空选择
          loadCheckGroups(); // 重新加载列表
        } else {
          firePropertyChange("operationError", null, "删除检查组失败");
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        firePropertyChange("operationError", null, throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 批量删除检查组
   */
  public void deleteSelectedCheckGroups(List<CheckGroup> selectedGroups) {
    if (selectedGroups == null || selectedGroups.isEmpty()) {
      firePropertyChange("operationError", null, "请先选择要删除的检查组");
      return;
    }

    // 确认删除
    int result = JOptionPane.showConfirmDialog(
        null,
        "确定要删除选中的 " + selectedGroups.size() + " 个检查组吗？\n" +
            "删除后不可恢复！\n\n" +
            "注意：正在被使用的检查组将无法删除。",
        "确认批量删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (result != JOptionPane.YES_OPTION) {
      return;
    }

    setLoading(true);

    // 在后台线程执行批量删除操作
    List<Integer> groupIds = new ArrayList<>();
    for (CheckGroup group : selectedGroups) {
      groupIds.add(group.getGroupId());
    }

    CompletableFuture.supplyAsync(() -> {
      try {
        return checkGroupService.deleteCheckGroups(groupIds);
      } catch (Exception e) {
        throw new RuntimeException("批量删除检查组失败: " + e.getMessage(), e);
      }
    }).thenAccept(successCount -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        if (successCount > 0) {
          firePropertyChange("operationSuccess", false, true);
          setSelectedCheckGroup(null); // 清空选择
          loadCheckGroups(); // 重新加载列表
        } else {
          firePropertyChange("operationError", null, "删除检查组失败");
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        firePropertyChange("operationError", null, throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    loadCheckGroups();
  }

  /**
   * 页码改变
   */
  public void onPageChanged(int newPage, int newPageSize) {
    if (newPage != currentPage || newPageSize != pageSize) {
      setCurrentPage(newPage);
      setPageSize(newPageSize);
      loadCheckGroups();
    }
  }

  /**
   * 更新分页数据
   */
  private void updatePageData(Page<CheckGroup> page) {
    setCheckGroupList(page.getRecords());
    setCurrentPage((int) page.getCurrent());
    setPageSize((int) page.getSize());
    setTotalPages((int) page.getPages());
    setTotalRecords((int) page.getTotal());

    // 更新按钮状态
    updateButtonStates();
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    setEditButtonEnabled(selectedCheckGroup != null);
    setDeleteButtonEnabled(selectedCheckGroup != null);
  }

  // Getter和Setter方法

  public List<CheckGroup> getCheckGroupList() {
    return checkGroupList;
  }

  public void setCheckGroupList(List<CheckGroup> checkGroupList) {
    this.checkGroupList = checkGroupList;
    firePropertyChange("checkGroupList", null, checkGroupList);
  }

  public CheckGroup getSelectedCheckGroup() {
    return selectedCheckGroup;
  }

  public void setSelectedCheckGroup(CheckGroup selectedCheckGroup) {
    CheckGroup oldValue = this.selectedCheckGroup;
    this.selectedCheckGroup = selectedCheckGroup;
    firePropertyChange("selectedCheckGroup", oldValue, selectedCheckGroup);
    updateButtonStates();
  }

  public String getSearchName() {
    return searchName;
  }

  public void setSearchName(String searchName) {
    this.searchName = setProperty(this.searchName, searchName != null ? searchName : "", "searchName");
  }

  public String getSearchCode() {
    return searchCode;
  }

  public void setSearchCode(String searchCode) {
    this.searchCode = setProperty(this.searchCode, searchCode != null ? searchCode : "", "searchCode");
  }

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = setProperty(this.loading, loading, "loading");
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = setProperty(this.currentPage, currentPage, "currentPage");
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = setProperty(this.pageSize, pageSize, "pageSize");
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = setProperty(this.totalPages, totalPages, "totalPages");
  }

  public int getTotalRecords() {
    return totalRecords;
  }

  public void setTotalRecords(int totalRecords) {
    this.totalRecords = setProperty(this.totalRecords, totalRecords, "totalRecords");
  }

  public boolean isAddButtonEnabled() {
    return addButtonEnabled;
  }

  public void setAddButtonEnabled(boolean addButtonEnabled) {
    this.addButtonEnabled = setProperty(this.addButtonEnabled, addButtonEnabled, "addButtonEnabled");
  }

  public boolean isEditButtonEnabled() {
    return editButtonEnabled;
  }

  public void setEditButtonEnabled(boolean editButtonEnabled) {
    this.editButtonEnabled = setProperty(this.editButtonEnabled, editButtonEnabled, "editButtonEnabled");
  }

  public boolean isDeleteButtonEnabled() {
    return deleteButtonEnabled;
  }

  public void setDeleteButtonEnabled(boolean deleteButtonEnabled) {
    this.deleteButtonEnabled = setProperty(this.deleteButtonEnabled, deleteButtonEnabled, "deleteButtonEnabled");
  }

  public boolean isSearchButtonEnabled() {
    return searchButtonEnabled;
  }

  public void setSearchButtonEnabled(boolean searchButtonEnabled) {
    this.searchButtonEnabled = setProperty(this.searchButtonEnabled, searchButtonEnabled, "searchButtonEnabled");
  }

  public NotificationViewModel getNotificationViewModel() {
    return notificationViewModel;
  }
}