package com.healthsys.view.user.healthdata.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 用户健康数据工具栏组件
 * 负责操作按钮和状态显示
 * 
 * @author AI Assistant
 */
public class UserHealthDataToolbarComponent extends JPanel {

  private static final Logger logger = LoggerFactory.getLogger(UserHealthDataToolbarComponent.class);

  // 操作按钮
  private JButton addButton;
  private JButton deleteButton;
  private JButton refreshButton;
  private JButton exportButton;
  private JButton printButton;

  // 状态标签
  private JLabel statusLabel;

  // 是否管理员录入模式
  private boolean isAdminEntryMode = false;

  // 回调接口
  public interface ToolbarCallback {
    void onAddData();

    void onDeleteData();

    void onRefreshData();

    void onExportData();

    void onPrintReport();
  }

  private ToolbarCallback toolbarCallback;

  public UserHealthDataToolbarComponent() {
    initializeComponents();
    setupLayout();
    bindEvents();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 添加/查看按钮
    addButton = new JButton("查看详情");
    addButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    addButton.setPreferredSize(new Dimension(100, 30));
    addButton.setEnabled(false);

    // 删除按钮
    deleteButton = new JButton("删除记录");
    deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    deleteButton.setPreferredSize(new Dimension(100, 30));
    deleteButton.setEnabled(false);
    deleteButton.setVisible(false); // 默认隐藏，只有管理员可见

    // 刷新按钮
    refreshButton = new JButton("刷新");
    refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    refreshButton.setPreferredSize(new Dimension(80, 30));

    // 导出按钮
    exportButton = new JButton("导出数据");
    exportButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    exportButton.setPreferredSize(new Dimension(100, 30));

    // 打印按钮
    printButton = new JButton("打印报告");
    printButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    printButton.setPreferredSize(new Dimension(100, 30));

    // 状态标签
    statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("操作工具"));

    // 按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(Box.createHorizontalStrut(20)); // 间距
    buttonPanel.add(refreshButton);
    buttonPanel.add(exportButton);
    buttonPanel.add(printButton);

    // 状态面板
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusPanel.add(new JLabel("状态: "));
    statusPanel.add(statusLabel);

    add(buttonPanel, BorderLayout.CENTER);
    add(statusPanel, BorderLayout.SOUTH);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 添加数据按钮
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (toolbarCallback != null) {
          toolbarCallback.onAddData();
        }
      }
    });

    // 删除按钮
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (toolbarCallback != null) {
          toolbarCallback.onDeleteData();
        }
      }
    });

    // 刷新按钮
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (toolbarCallback != null) {
          toolbarCallback.onRefreshData();
        }
      }
    });

    // 导出按钮
    exportButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (toolbarCallback != null) {
          toolbarCallback.onExportData();
        }
      }
    });

    // 打印按钮
    printButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (toolbarCallback != null) {
          toolbarCallback.onPrintReport();
        }
      }
    });
  }

  /**
   * 进入管理员录入模式
   */
  public void enterAdminEntryMode(String appointmentInfo) {
    this.isAdminEntryMode = true;

    // 切换按钮文本和功能
    addButton.setText("添加数据");
    addButton.setEnabled(true);

    // 显示删除按钮（管理员权限）
    deleteButton.setVisible(true);

    // 更新状态
    setStatusMessage("管理员录入模式 - " + appointmentInfo);
    statusLabel.setForeground(new Color(220, 53, 69)); // 红色提示

    logger.info("进入管理员录入模式: {}", appointmentInfo);
  }

  /**
   * 退出管理员录入模式
   */
  public void exitAdminEntryMode() {
    this.isAdminEntryMode = false;

    // 恢复按钮文本和功能
    addButton.setText("查看详情");
    addButton.setEnabled(false);

    // 隐藏删除按钮
    deleteButton.setVisible(false);

    // 恢复状态
    setStatusMessage("就绪");
    statusLabel.setForeground(Color.GRAY);

    logger.info("退出管理员录入模式");
  }

  /**
   * 更新按钮状态
   */
  public void updateButtonStates(boolean hasSelection) {
    if (isAdminEntryMode) {
      // 管理员录入模式下，添加按钮始终可用
      addButton.setEnabled(true);
      deleteButton.setEnabled(hasSelection);
    } else {
      // 普通模式下，查看详情需要选中记录
      addButton.setEnabled(hasSelection);
      deleteButton.setEnabled(false);
    }
  }

  /**
   * 设置状态消息
   */
  public void setStatusMessage(String message) {
    if (statusLabel != null) {
      statusLabel.setText(message != null ? message : "就绪");
    }
  }

  /**
   * 设置加载状态
   */
  public void setLoading(boolean loading) {
    // 在加载时禁用所有按钮
    addButton.setEnabled(!loading);
    deleteButton.setEnabled(!loading && isAdminEntryMode);
    refreshButton.setEnabled(!loading);
    exportButton.setEnabled(!loading);
    printButton.setEnabled(!loading);

    if (loading) {
      setStatusMessage("正在加载...");
    }
  }

  /**
   * 设置工具栏回调
   */
  public void setToolbarCallback(ToolbarCallback callback) {
    this.toolbarCallback = callback;
  }

  /**
   * 获取添加按钮
   */
  public JButton getAddButton() {
    return addButton;
  }

  /**
   * 获取删除按钮
   */
  public JButton getDeleteButton() {
    return deleteButton;
  }

  /**
   * 获取刷新按钮
   */
  public JButton getRefreshButton() {
    return refreshButton;
  }

  /**
   * 获取导出按钮
   */
  public JButton getExportButton() {
    return exportButton;
  }

  /**
   * 获取打印按钮
   */
  public JButton getPrintButton() {
    return printButton;
  }

  /**
   * 获取状态标签
   */
  public JLabel getStatusLabel() {
    return statusLabel;
  }

  /**
   * 是否为管理员录入模式
   */
  public boolean isAdminEntryMode() {
    return isAdminEntryMode;
  }
}