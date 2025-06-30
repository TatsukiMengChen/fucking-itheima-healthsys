package com.healthsys.view.admin.checkitem;

import com.healthsys.model.entity.CheckItem;
import com.healthsys.view.admin.checkitem.component.CheckItemEditFormComponent;
import com.healthsys.view.admin.checkitem.component.CheckItemTableComponent;
import com.healthsys.view.common.NotificationComponent;
import com.healthsys.viewmodel.admin.checkitem.CheckItemEditViewModel;
import com.healthsys.viewmodel.admin.checkitem.CheckItemManagementViewModel;
import com.healthsys.viewmodel.common.NotificationViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

  // 主要组件
  private CheckItemTableComponent tableComponent;
  private NotificationComponent notificationComponent;

  // 编辑对话框
  private JDialog editDialog;
  private CheckItemEditFormComponent editFormComponent;

  // 工具栏按钮
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JButton refreshButton;

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

    // 工具栏按钮
    addButton = new JButton("添加");
    addButton.setPreferredSize(new Dimension(80, 30));
    addButton.setToolTipText("添加新的检查项");

    editButton = new JButton("编辑");
    editButton.setPreferredSize(new Dimension(80, 30));
    editButton.setToolTipText("编辑选中的检查项");
    editButton.setEnabled(false);

    deleteButton = new JButton("删除");
    deleteButton.setPreferredSize(new Dimension(80, 30));
    deleteButton.setToolTipText("删除选中的检查项");
    deleteButton.setEnabled(false);

    refreshButton = new JButton("刷新");
    refreshButton.setPreferredSize(new Dimension(80, 30));
    refreshButton.setToolTipText("刷新检查项列表");
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // 顶部通知区域
    add(notificationComponent, BorderLayout.NORTH);

    // 中央主要内容区域
    JPanel mainPanel = createMainPanel();
    add(mainPanel, BorderLayout.CENTER);
  }

  /**
   * 创建主面板
   */
  private JPanel createMainPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // 工具栏
    JPanel toolbarPanel = createToolbarPanel();
    panel.add(toolbarPanel, BorderLayout.NORTH);

    // 表格区域
    JScrollPane tableScrollPane = new JScrollPane(tableComponent);
    tableScrollPane.setPreferredSize(new Dimension(800, 400));
    tableScrollPane.setBorder(BorderFactory.createTitledBorder("检查项列表"));
    panel.add(tableScrollPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * 创建工具栏面板
   */
  private JPanel createToolbarPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    panel.setBorder(BorderFactory.createEtchedBorder());

    // 添加按钮
    panel.add(addButton);
    panel.add(editButton);
    panel.add(deleteButton);

    // 添加分隔符
    panel.add(Box.createHorizontalStrut(10));
    JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
    separator.setPreferredSize(new Dimension(1, 20));
    panel.add(separator);
    panel.add(Box.createHorizontalStrut(10));

    panel.add(refreshButton);

    return panel;
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

    // 监听表格选择变化
    tableComponent.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if ("selectedCheckItem".equals(evt.getPropertyName())) {
          updateButtonStates();
        }
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
   * 更新按钮状态
   */
  private void updateButtonStates() {
    CheckItem selectedItem = tableComponent.getSelectedCheckItem();
    boolean hasSelection = selectedItem != null;

    editButton.setEnabled(hasSelection);
    deleteButton.setEnabled(hasSelection);
  }

  /**
   * 更新加载状态
   */
  private void updateLoadingState() {
    boolean loading = managementViewModel.isLoading();

    // 禁用/启用操作按钮
    addButton.setEnabled(!loading);
    editButton.setEnabled(!loading && tableComponent.getSelectedCheckItem() != null);
    deleteButton.setEnabled(!loading && tableComponent.getSelectedCheckItem() != null);
    refreshButton.setEnabled(!loading);

    // 更新刷新按钮文本
    refreshButton.setText(loading ? "加载中..." : "刷新");
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 添加按钮
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showAddDialog();
      }
    });

    // 编辑按钮
    editButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showEditDialog();
      }
    });

    // 删除按钮
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        confirmAndDelete();
      }
    });

    // 刷新按钮
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        managementViewModel.refreshData();
      }
    });

    // 表格双击编辑
    tableComponent.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if ("doubleClicked".equals(evt.getPropertyName())) {
          showEditDialog();
        }
      }
    });
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
   * 确认并删除
   */
  private void confirmAndDelete() {
    CheckItem selectedItem = tableComponent.getSelectedCheckItem();
    if (selectedItem == null) {
      notificationViewModel.showWarning("请先选择要删除的检查项");
      return;
    }

    // 确认对话框
    String message = String.format("确定要删除检查项 \"%s\" 吗？\n\n注意：如果此检查项正在被使用，删除操作将失败。",
        selectedItem.getItemName());

    int result = JOptionPane.showConfirmDialog(
        this,
        message,
        "确认删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (result == JOptionPane.YES_OPTION) {
      managementViewModel.deleteCheckItem(selectedItem.getItemId());
    }
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