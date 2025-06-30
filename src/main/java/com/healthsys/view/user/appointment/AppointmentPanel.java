package com.healthsys.view.user.appointment;

import com.healthsys.view.base.BasePanel;
import com.healthsys.view.user.appointment.component.AppointmentFormComponent;
import com.healthsys.view.user.appointment.component.AppointmentHistoryComponent;
import com.healthsys.viewmodel.user.appointment.AppointmentViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * 用户预约面板。
 * 展示和管理用户预约信息。
 * 
 * @author 梦辰
 */
public class AppointmentPanel extends BasePanel {

  private AppointmentViewModel viewModel;
  private AppointmentFormComponent appointmentFormComponent;
  private AppointmentHistoryComponent appointmentHistoryComponent;

  // UI组件
  private JTabbedPane tabbedPane;
  private JPanel formPanel;
  private JPanel historyPanel;

  public AppointmentPanel() {
    initializeViewModel();
    initializeComponents();
    setupLayout();
    bindEvents();

    // 初始化数据
    viewModel.initialize();
  }

  /**
   * 初始化ViewModel
   */
  private void initializeViewModel() {
    this.viewModel = new AppointmentViewModel();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 创建选项卡面板
    tabbedPane = new JTabbedPane();

    // 创建子组件
    appointmentFormComponent = new AppointmentFormComponent(viewModel);
    appointmentHistoryComponent = new AppointmentHistoryComponent(viewModel);

    // 创建面板容器
    formPanel = new JPanel(new BorderLayout());
    historyPanel = new JPanel(new BorderLayout());

    // 添加组件到容器
    formPanel.add(appointmentFormComponent, BorderLayout.CENTER);
    historyPanel.add(appointmentHistoryComponent, BorderLayout.CENTER);

    // 添加选项卡
    tabbedPane.addTab("预约体检", createTabIcon("appointment"), formPanel, "预约新的体检");
    tabbedPane.addTab("预约历史", createTabIcon("history"), historyPanel, "查看预约历史记录");
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());

    // 创建标题面板
    JPanel titlePanel = createTitlePanel();
    add(titlePanel, BorderLayout.NORTH);

    // 添加选项卡面板
    add(tabbedPane, BorderLayout.CENTER);

    // 创建状态栏
    JPanel statusPanel = createStatusPanel();
    add(statusPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建标题面板
   */
  private JPanel createTitlePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    JLabel titleLabel = new JLabel("体检预约管理");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
    titleLabel.setForeground(new Color(51, 51, 51));

    JLabel descLabel = new JLabel("预约体检服务，查看预约历史");
    descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    descLabel.setForeground(new Color(102, 102, 102));

    panel.add(titleLabel);
    panel.add(Box.createHorizontalStrut(15));
    panel.add(descLabel);

    return panel;
  }

  /**
   * 创建状态栏
   */
  private JPanel createStatusPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

    JLabel statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
    statusLabel.setForeground(new Color(102, 102, 102));

    panel.add(statusLabel, BorderLayout.WEST);

    // 监听状态消息变化
    viewModel.addPropertyChangeListener("statusMessage", evt -> {
      SwingUtilities.invokeLater(() -> {
        String message = (String) evt.getNewValue();
        statusLabel.setText(message != null ? message : "就绪");
      });
    });

    return panel;
  }

  /**
   * 创建选项卡图标
   */
  private Icon createTabIcon(String iconName) {
    // 这里可以加载实际的图标文件
    // 暂时返回null，使用文字标签
    return null;
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 监听选项卡切换
    tabbedPane.addChangeListener(e -> {
      int selectedIndex = tabbedPane.getSelectedIndex();
      if (selectedIndex == 1) { // 切换到预约历史选项卡时刷新数据
        viewModel.loadAppointmentHistoryCommand();
      }
    });

    // 监听ViewModel状态变化
    viewModel.addPropertyChangeListener("loading", evt -> {
      boolean isLoading = (Boolean) evt.getNewValue();
      SwingUtilities.invokeLater(() -> {
        setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
      });
    });

    // 监听预约提交成功事件
    viewModel.addPropertyChangeListener("appointmentSubmitted", evt -> {
      boolean submitted = (Boolean) evt.getNewValue();
      if (submitted) {
        SwingUtilities.invokeLater(() -> {
          // 切换到预约历史选项卡
          tabbedPane.setSelectedIndex(1);

          // 显示成功提示
          JOptionPane.showMessageDialog(
              this,
              "预约提交成功！",
              "预约成功",
              JOptionPane.INFORMATION_MESSAGE);
        });
      }
    });
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    viewModel.initialize();
  }

  /**
   * 获取ViewModel
   */
  public AppointmentViewModel getViewModel() {
    return viewModel;
  }

  /**
   * 设置选中的选项卡
   */
  public void setSelectedTab(int index) {
    if (index >= 0 && index < tabbedPane.getTabCount()) {
      tabbedPane.setSelectedIndex(index);
    }
  }

  /**
   * 切换到预约表单选项卡
   */
  public void showAppointmentForm() {
    setSelectedTab(0);
  }

  /**
   * 切换到预约历史选项卡
   */
  public void showAppointmentHistory() {
    setSelectedTab(1);
  }
}