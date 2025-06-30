package com.healthsys.view.admin.checkitem;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.healthsys.model.entity.CheckItem;
import com.healthsys.view.admin.checkitem.component.CheckItemEditFormComponent;
import com.healthsys.view.admin.checkitem.component.CheckItemTableComponent;
import com.healthsys.view.common.NotificationComponent;
import com.healthsys.viewmodel.admin.checkitem.CheckItemEditViewModel;
import com.healthsys.viewmodel.admin.checkitem.CheckItemManagementViewModel;
import com.healthsys.viewmodel.common.NotificationViewModel;

/**
 * 检查项管理主面板
 * 组合表格组件和表单组件，实现完整的检查项管理功能
 * 
 * @author HealthSys Team
 */
public class CheckItemManagementPanel extends JPanel {

  private CheckItemManagementViewModel managementViewModel;
  private CheckItemEditViewModel editViewModel;
  private NotificationViewModel notificationViewModel;

  // UI组件
  private CheckItemTableComponent tableComponent;
  private NotificationComponent notificationComponent;

  // 编辑对话框
  private JDialog editDialog;
  private CheckItemEditFormComponent editFormComponent;

  /**
   * 构造函数
   */
  public CheckItemManagementPanel() {
    initializeViewModels();
    initializeComponents();
    setupLayout();
    bindViewModels();
    setupEventListeners();

    // 初始加载数据
    managementViewModel.refreshData();
  }

  /**
   * 初始化ViewModel
   */
  private void initializeViewModels() {
    managementViewModel = new CheckItemManagementViewModel();
    editViewModel = new CheckItemEditViewModel();
    notificationViewModel = new NotificationViewModel();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 表格组件
    tableComponent = new CheckItemTableComponent(managementViewModel);

    // 通知组件
    notificationComponent = new NotificationComponent(notificationViewModel);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // 顶部通知区域
    add(notificationComponent, BorderLayout.NORTH);

    // 中央表格区域
    JScrollPane tableScrollPane = new JScrollPane(tableComponent);
    tableScrollPane.setPreferredSize(new Dimension(800, 400));
    tableScrollPane.setBorder(BorderFactory.createTitledBorder("检查项列表"));
    add(tableScrollPane, BorderLayout.CENTER);
  }

  /**
   * 绑定ViewModel
   */
  private void bindViewModels() {
    // 监听管理ViewModel的变化
    managementViewModel.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> handleManagementViewModelChange(evt));
      }
    });

    // 监听编辑ViewModel的变化
    editViewModel.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> handleEditViewModelChange(evt));
      }
    });

    // 监听管理ViewModel的编辑请求
    managementViewModel.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
          String propertyName = evt.getPropertyName();
          switch (propertyName) {
            case "addCheckItemRequested":
              showAddDialog();
              break;
            case "editCheckItemRequested":
              showEditDialog();
              break;
          }
        });
      }
    });
  }

  /**
   * 处理管理ViewModel变化
   */
  private void handleManagementViewModelChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    switch (propertyName) {
      case "loading":
        updateLoadingState();
        break;
      case "operationSuccess":
        if ((Boolean) evt.getNewValue()) {
          notificationViewModel.showSuccess("操作成功完成");
          managementViewModel.refreshData();
        }
        break;
      case "operationError":
        String errorMessage = (String) evt.getNewValue();
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
          notificationViewModel.showError("操作失败: " + errorMessage);
        }
        break;
    }
  }

  /**
   * 处理编辑ViewModel变化
   */
  private void handleEditViewModelChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    switch (propertyName) {
      case "submitSuccess":
        if ((Boolean) evt.getNewValue()) {
          closeEditDialog();
          notificationViewModel.showSuccess(editViewModel.isEditMode() ? "检查项更新成功" : "检查项添加成功");
          managementViewModel.refreshData();
        }
        break;
      case "submitError":
        String errorMessage = (String) evt.getNewValue();
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
          notificationViewModel.showError("保存失败: " + errorMessage);
        }
        break;
      case "cancelled":
        if ((Boolean) evt.getNewValue()) {
          closeEditDialog();
        }
        break;
    }
  }

  /**
   * 更新加载状态
   */
  private void updateLoadingState() {
    boolean loading = managementViewModel.isLoading();
    // 加载状态更新由表格组件内部处理
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 事件监听由表格组件内部处理，这里暂时保留为空方法
  }

  /**
   * 显示添加对话框
   */
  private void showAddDialog() {
    editViewModel.setEditMode(false);
    editViewModel.resetForm();
    showEditFormDialog("添加检查项");
  }

  /**
   * 显示编辑对话框
   */
  private void showEditDialog() {
    CheckItem selectedItem = tableComponent.getSelectedCheckItem();
    if (selectedItem == null) {
      notificationViewModel.showWarning("请先选择要编辑的检查项");
      return;
    }

    editViewModel.setEditMode(true);
    editViewModel.loadCheckItem(selectedItem);
    showEditFormDialog("编辑检查项");
  }

  /**
   * 显示编辑表单对话框
   */
  private void showEditFormDialog(String title) {
    if (editDialog != null) {
      editDialog.dispose();
    }

    // 创建对话框
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    editDialog = new JDialog((Dialog) null, title, true);
    editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    // 创建表单组件
    editFormComponent = new CheckItemEditFormComponent(editViewModel);

    // 监听表单事件
    editFormComponent.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if ("submitSuccess".equals(evt.getPropertyName()) || "cancelled".equals(evt.getPropertyName())) {
          closeEditDialog();
        }
      }
    });

    // 设置对话框内容
    editDialog.setContentPane(editFormComponent);
    editDialog.pack();
    editDialog.setLocationRelativeTo(this);

    // 显示对话框
    SwingUtilities.invokeLater(() -> {
      editDialog.setVisible(true);
      editFormComponent.requestFocusOnFirstField();
    });
  }

  /**
   * 关闭编辑对话框
   */
  private void closeEditDialog() {
    if (editDialog != null) {
      editDialog.dispose();
      editDialog = null;
      editFormComponent = null;
    }
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    // 按钮状态更新由表格组件内部处理
  }

  /**
   * 获取选中的检查项
   */
  public CheckItem getSelectedCheckItem() {
    return tableComponent.getSelectedCheckItem();
  }

  /**
   * 设置选中的检查项
   */
  public void setSelectedCheckItem(CheckItem checkItem) {
    tableComponent.setSelectedCheckItem(checkItem);
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    managementViewModel.refreshData();
  }
}