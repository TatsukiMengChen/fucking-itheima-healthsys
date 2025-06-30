package com.healthsys.viewmodel.admin.checkitem;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.impl.CheckItemServiceImpl;
import com.healthsys.viewmodel.base.BaseViewModel;
import com.healthsys.viewmodel.common.NotificationViewModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 检查项管理ViewModel
 * 管理检查项列表、搜索条件、分页信息和相关命令
 * 
 * @author HealthSys Team
 */
public class CheckItemManagementViewModel extends BaseViewModel {

  // 服务层
  private ICheckItemService checkItemService;

  // 通知ViewModel
  private NotificationViewModel notificationViewModel;

  // 数据属性
  private List<CheckItem> checkItemList = new ArrayList<>();
  private CheckItem selectedCheckItem;
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
  public CheckItemManagementViewModel() {
    super();
    this.checkItemService = new CheckItemServiceImpl();
    this.notificationViewModel = new NotificationViewModel();

    // 初始化加载数据
    loadCheckItems();
  }

  /**
   * 加载检查项列表
   */
  public void loadCheckItems() {
    if (loading) {
      return; // 防止重复加载
    }

    setLoading(true);
    setSearchButtonEnabled(false);

    // 使用CompletableFuture在后台线程执行
    CompletableFuture.supplyAsync(() -> {
      try {
        Page<CheckItem> page = checkItemService.queryCheckItems(
            searchName.trim().isEmpty() ? null : searchName.trim(),
            searchCode.trim().isEmpty() ? null : searchCode.trim(),
            currentPage,
            pageSize);
        return page;
      } catch (Exception e) {
        throw new RuntimeException("加载检查项列表失败: " + e.getMessage(), e);
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
        notificationViewModel.showError("加载检查项列表失败: " + throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 搜索检查项
   */
  public void searchCheckItems() {
    setCurrentPage(1); // 重置到第一页
    loadCheckItems();
  }

  /**
   * 清空搜索条件
   */
  public void clearSearch() {
    setSearchName("");
    setSearchCode("");
    setCurrentPage(1);
    loadCheckItems();
  }

  /**
   * 添加检查项
   */
  public void addCheckItem() {
    // 权限检查
    if (!AppContext.isAdmin()) {
      notificationViewModel.showError("您没有权限执行此操作");
      return;
    }

    // 通过事件通知视图层打开添加对话框
    firePropertyChange("addCheckItemRequested", false, true);
  }

  /**
   * 编辑检查项
   */
  public void editCheckItem() {
    // 权限检查
    if (!AppContext.isAdmin()) {
      notificationViewModel.showError("您没有权限执行此操作");
      return;
    }

    if (selectedCheckItem == null) {
      notificationViewModel.showWarning("请先选择要编辑的检查项");
      return;
    }

    // 通过事件通知视图层打开编辑对话框
    firePropertyChange("editCheckItemRequested", null, selectedCheckItem);
  }

  /**
   * 删除检查项
   */
  public void deleteCheckItem() {
    // 权限检查
    if (!AppContext.isAdmin()) {
      notificationViewModel.showError("您没有权限执行此操作");
      return;
    }

    if (selectedCheckItem == null) {
      notificationViewModel.showWarning("请先选择要删除的检查项");
      return;
    }

    // 确认删除
    int result = JOptionPane.showConfirmDialog(
        null,
        "确定要删除检查项 \"" + selectedCheckItem.getItemName() + "\" 吗？\n" +
            "删除后不可恢复！",
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
        return checkItemService.deleteCheckItem(selectedCheckItem.getItemId());
      } catch (Exception e) {
        throw new RuntimeException("删除检查项失败: " + e.getMessage(), e);
      }
    }).thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        if (success) {
          notificationViewModel.showSuccess("删除检查项成功");
          setSelectedCheckItem(null); // 清空选择
          loadCheckItems(); // 重新加载列表
        } else {
          notificationViewModel.showError("删除检查项失败");
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        notificationViewModel.showError("删除检查项失败: " + throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 批量删除检查项
   */
  public void deleteSelectedCheckItems(List<CheckItem> selectedItems) {
    if (selectedItems == null || selectedItems.isEmpty()) {
      notificationViewModel.showWarning("请先选择要删除的检查项");
      return;
    }

    // 确认删除
    int result = JOptionPane.showConfirmDialog(
        null,
        "确定要删除选中的 " + selectedItems.size() + " 个检查项吗？\n" +
            "删除后不可恢复！",
        "确认批量删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (result != JOptionPane.YES_OPTION) {
      return;
    }

    setLoading(true);

    // 在后台线程执行批量删除操作
    List<Integer> itemIds = new ArrayList<>();
    for (CheckItem item : selectedItems) {
      itemIds.add(item.getItemId());
    }

    CompletableFuture.supplyAsync(() -> {
      try {
        return checkItemService.deleteCheckItems(itemIds);
      } catch (Exception e) {
        throw new RuntimeException("批量删除检查项失败: " + e.getMessage(), e);
      }
    }).thenAccept(successCount -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        if (successCount > 0) {
          notificationViewModel.showSuccess("成功删除 " + successCount + " 个检查项");
          setSelectedCheckItem(null); // 清空选择
          loadCheckItems(); // 重新加载列表
        } else {
          notificationViewModel.showError("删除检查项失败");
        }
      });
    }).exceptionally(throwable -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        notificationViewModel.showError("批量删除检查项失败: " + throwable.getMessage());
      });
      return null;
    });
  }

  /**
   * 刷新列表
   */
  public void refreshList() {
    loadCheckItems();
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    loadCheckItems();
  }

  /**
   * 删除指定ID的检查项
   */
  public void deleteCheckItem(Integer itemId) {
    if (itemId == null) {
      return;
    }

    setLoading(true);

    // 在后台线程执行删除操作
    CompletableFuture.supplyAsync(() -> {
      try {
        return checkItemService.deleteCheckItem(itemId);
      } catch (Exception e) {
        throw new RuntimeException("删除检查项失败: " + e.getMessage(), e);
      }
    }).thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        setLoading(false);
        if (success) {
          firePropertyChange("operationSuccess", false, true);
          setSelectedCheckItem(null); // 清空选择
          loadCheckItems(); // 重新加载列表
        } else {
          firePropertyChange("operationError", null, "删除检查项失败");
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
   * 页码改变
   */
  public void onPageChanged(int newPage, int newPageSize) {
    if (newPage != currentPage || newPageSize != pageSize) {
      setCurrentPage(newPage);
      setPageSize(newPageSize);
      loadCheckItems();
    }
  }

  /**
   * 更新分页数据
   */
  private void updatePageData(Page<CheckItem> page) {
    setCheckItemList(page.getRecords());
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
    setEditButtonEnabled(selectedCheckItem != null);
    setDeleteButtonEnabled(selectedCheckItem != null);
  }

  // Getter和Setter方法

  public List<CheckItem> getCheckItemList() {
    return checkItemList;
  }

  public void setCheckItemList(List<CheckItem> checkItemList) {
    this.checkItemList = checkItemList;
    firePropertyChange("checkItemList", null, checkItemList);
  }

  public CheckItem getSelectedCheckItem() {
    return selectedCheckItem;
  }

  public void setSelectedCheckItem(CheckItem selectedCheckItem) {
    CheckItem oldValue = this.selectedCheckItem;
    this.selectedCheckItem = selectedCheckItem;
    firePropertyChange("selectedCheckItem", oldValue, selectedCheckItem);
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