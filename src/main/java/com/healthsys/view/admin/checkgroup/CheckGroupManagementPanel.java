package com.healthsys.view.admin.checkgroup;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.service.impl.CheckItemServiceImpl;
import com.healthsys.view.admin.checkgroup.component.CheckGroupEditFormComponent;
import com.healthsys.view.admin.checkgroup.component.CheckGroupTableComponent;
import com.healthsys.view.common.NotificationComponent;
import com.healthsys.viewmodel.admin.checkgroup.CheckGroupEditViewModel;
import com.healthsys.viewmodel.admin.checkgroup.CheckGroupManagementViewModel;
import com.healthsys.viewmodel.common.NotificationViewModel;

/**
 * 检查组管理主面板
 * 组合表格组件和表单组件，提供完整的检查组管理功能
 * 
 * @author HealthSys Team
 */
public class CheckGroupManagementPanel extends JPanel implements PropertyChangeListener {

  // ViewModels
  private CheckGroupManagementViewModel managementViewModel;
  private CheckGroupEditViewModel editViewModel;
  private NotificationViewModel notificationViewModel;

  // Services
  private ICheckGroupService checkGroupService;
  private ICheckItemService checkItemService;

  // UI组件
  private CheckGroupTableComponent tableComponent;
  private NotificationComponent notificationComponent;

  // 对话框
  private JDialog editDialog;
  private CheckGroupEditFormComponent editFormComponent;

  /**
   * 构造函数
   */
  public CheckGroupManagementPanel() {
    initializeServices();
    initializeViewModels();
    initializeComponents();
    setupLayout();
    setupEventHandlers();

    // 初始加载数据
    loadInitialData();
  }

  /**
   * 初始化服务
   */
  private void initializeServices() {
    checkGroupService = new CheckGroupServiceImpl();
    checkItemService = new CheckItemServiceImpl();
  }

  /**
   * 初始化ViewModels
   */
  private void initializeViewModels() {
    // 通知ViewModel
    notificationViewModel = new NotificationViewModel();

    // 管理ViewModel
    managementViewModel = new CheckGroupManagementViewModel();
    managementViewModel.addPropertyChangeListener(this);

    // 编辑ViewModel
    editViewModel = new CheckGroupEditViewModel();
    editViewModel.addPropertyChangeListener(this);
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 通知组件
    notificationComponent = new NotificationComponent(notificationViewModel);

    // 表格组件
    tableComponent = new CheckGroupTableComponent();
    tableComponent.setViewModel(managementViewModel);

    // 编辑表单组件
    editFormComponent = new CheckGroupEditFormComponent();
    editFormComponent.setViewModel(editViewModel);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 顶部通知面板
    add(notificationComponent, BorderLayout.NORTH);

    // 中间表格面板
    add(tableComponent, BorderLayout.CENTER);

    // 创建编辑对话框
    createEditDialog();
  }

  /**
   * 创建编辑对话框
   */
  private void createEditDialog() {
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    if (parentWindow instanceof Frame) {
      editDialog = new JDialog((Frame) parentWindow, "检查组编辑", true);
    } else if (parentWindow instanceof Dialog) {
      editDialog = new JDialog((Dialog) parentWindow, "检查组编辑", true);
    } else {
      editDialog = new JDialog((Frame) null, "检查组编辑", true);
    }

    editDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    editDialog.setLayout(new BorderLayout());
    editDialog.add(editFormComponent, BorderLayout.CENTER);
    editDialog.setSize(800, 600);
    editDialog.setLocationRelativeTo(this);
  }

  /**
   * 设置事件处理器
   */
  private void setupEventHandlers() {
    // 表格组件事件 - 通过属性监听来处理
    // CheckGroupTableComponent内部处理按钮点击事件，通过ViewModel通信

    // 编辑表单事件
    editFormComponent.setFormEventListener(new CheckGroupEditFormComponent.FormEventListener() {
      @Override
      public void onSubmitSuccess() {
        editDialog.setVisible(false);
        refreshData();
        notificationViewModel.showSuccess("检查组保存成功！");
      }

      @Override
      public void onSubmitError(String errorMessage) {
        notificationViewModel.showError("保存失败：" + errorMessage);
      }

      @Override
      public void onCancelled() {
        editDialog.setVisible(false);
      }
    });
  }

  /**
   * 加载初始数据
   */
  private void loadInitialData() {
    SwingUtilities.invokeLater(() -> {
      managementViewModel.refreshData();
    });
  }

  /**
   * 显示添加对话框
   */
  private void showAddDialog() {
    editDialog.setTitle("添加检查组");
    editViewModel.setEditMode(false);
    editViewModel.loadCheckGroup(null);
    editFormComponent.resetForm();
    editDialog.setVisible(true);
  }

  /**
   * 显示编辑对话框
   */
  private void showEditDialog() {
    var selectedGroup = tableComponent.getSelectedCheckGroup();
    if (selectedGroup == null) {
      notificationViewModel.showWarning("请选择要编辑的检查组！");
      return;
    }

    editDialog.setTitle("编辑检查组");
    editViewModel.setEditMode(true);
    editViewModel.loadCheckGroup(selectedGroup);
    editDialog.setVisible(true);
  }

  /**
   * 确认并删除
   */
  private void confirmAndDelete() {
    var selectedGroup = tableComponent.getSelectedCheckGroup();
    if (selectedGroup == null) {
      notificationViewModel.showWarning("请选择要删除的检查组！");
      return;
    }

    int result = JOptionPane.showConfirmDialog(
        this,
        "确定要删除检查组 \"" + selectedGroup.getGroupName() + "\" 吗？\n此操作不可撤销！",
        "确认删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (result == JOptionPane.YES_OPTION) {
      managementViewModel.deleteCheckGroup(selectedGroup.getGroupId());
    }
  }

  /**
   * 刷新数据
   */
  private void refreshData() {
    managementViewModel.refreshData();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();
      Object source = evt.getSource();

      if (source == managementViewModel) {
        handleManagementViewModelChange(propertyName, evt.getNewValue());
      } else if (source == editViewModel) {
        handleEditViewModelChange(propertyName, evt.getNewValue());
      }
    });
  }

  /**
   * 处理管理ViewModel变更
   */
  private void handleManagementViewModelChange(String propertyName, Object newValue) {
    switch (propertyName) {
      case "loading":
        // 可以在这里显示加载状态
        break;
      case "operationError":
        if (newValue != null) {
          notificationViewModel.showError("操作失败：" + newValue);
        }
        break;
      case "operationSuccess":
        notificationViewModel.showSuccess("操作成功！");
        break;
      case "addCheckGroupRequested":
        showAddDialog();
        break;
      case "editCheckGroupRequested":
        showEditDialog();
        break;
    }
  }

  /**
   * 处理编辑ViewModel变更
   */
  private void handleEditViewModelChange(String propertyName, Object newValue) {
    switch (propertyName) {
      case "loadError":
        if (newValue != null) {
          notificationViewModel.showError("加载数据失败：" + newValue);
          editDialog.setVisible(false);
        }
        break;
    }
  }

  /**
   * 获取面板标题
   */
  public String getPanelTitle() {
    return "检查组管理";
  }

  /**
   * 获取面板描述
   */
  public String getPanelDescription() {
    return "管理系统中的检查组，包括添加、编辑、删除检查组，以及管理检查组与检查项的关联关系";
  }

  /**
   * 清理资源
   */
  public void dispose() {
    if (managementViewModel != null) {
      managementViewModel.removePropertyChangeListener(this);
    }
    if (editViewModel != null) {
      editViewModel.removePropertyChangeListener(this);
    }
    if (editDialog != null) {
      editDialog.dispose();
    }
  }
}