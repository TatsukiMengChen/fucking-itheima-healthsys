package com.healthsys.view.admin.appointment;

import com.healthsys.model.entity.Appointment;
import com.healthsys.view.admin.appointment.component.AdminAppointmentTableComponent;
import com.healthsys.view.common.NotificationComponent;
import com.healthsys.viewmodel.admin.appointment.AdminAppointmentViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 管理员预约管理主面板
 * 整合预约管理的各个组件，提供完整的预约管理界面
 * 
 * @author AI Assistant
 */
public class AdminAppointmentManagementPanel extends JPanel implements PropertyChangeListener {

  private final AdminAppointmentViewModel viewModel;
  private final NotificationComponent notificationComponent;

  // UI组件
  private AdminAppointmentTableComponent appointmentTableComponent;
  private JPanel detailPanel;
  private JLabel detailTitleLabel;
  private JTextArea detailTextArea;
  private JScrollPane detailScrollPane;

  public AdminAppointmentManagementPanel() {
    this.viewModel = new AdminAppointmentViewModel();
    this.notificationComponent = new NotificationComponent();
    this.viewModel.addPropertyChangeListener(this);

    initializeComponents();
    setupLayout();
    setupEventListeners();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 创建预约表格组件
    appointmentTableComponent = new AdminAppointmentTableComponent(viewModel);

    // 创建详情面板
    createDetailPanel();
  }

  /**
   * 创建详情面板
   */
  private void createDetailPanel() {
    detailPanel = new JPanel(new BorderLayout());
    detailPanel.setBorder(BorderFactory.createTitledBorder("预约详情"));
    detailPanel.setPreferredSize(new Dimension(300, 0));

    detailTitleLabel = new JLabel("请选择一个预约查看详情");
    detailTitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
    detailTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    detailTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    detailTextArea = new JTextArea();
    detailTextArea.setEditable(false);
    detailTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    detailTextArea.setBackground(getBackground());
    detailTextArea.setMargin(new Insets(10, 10, 10, 10));

    detailScrollPane = new JScrollPane(detailTextArea);
    detailScrollPane.setBorder(null);
    detailScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    detailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    detailPanel.add(detailTitleLabel, BorderLayout.NORTH);
    detailPanel.add(detailScrollPane, BorderLayout.CENTER);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 顶部通知组件
    add(notificationComponent, BorderLayout.NORTH);

    // 创建主内容面板
    JPanel mainContentPanel = new JPanel(new BorderLayout());

    // 左侧预约表格
    mainContentPanel.add(appointmentTableComponent, BorderLayout.CENTER);

    // 右侧详情面板
    mainContentPanel.add(detailPanel, BorderLayout.EAST);

    add(mainContentPanel, BorderLayout.CENTER);

    // 底部状态面板（可选）
    JPanel statusPanel = createStatusPanel();
    add(statusPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建状态面板
   */
  private JPanel createStatusPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    JLabel statusLabel = new JLabel("预约管理 - 管理员可以查看和管理所有用户的预约记录");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
    statusLabel.setForeground(Color.GRAY);

    panel.add(statusLabel);
    return panel;
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 监听表格选择变化，更新详情面板
    SwingUtilities.invokeLater(() -> {
      JTable table = findTableInComponent(appointmentTableComponent);
      if (table != null) {
        table.getSelectionModel().addListSelectionListener(e -> {
          if (!e.getValueIsAdjusting()) {
            updateDetailPanel();
          }
        });
      }
    });

    // 设置ViewModel的事件监听
    viewModel.addPropertyChangeListener("statusMessage", evt -> {
      String message = (String) evt.getNewValue();
      if (message != null && !message.equals("就绪")) {
        // 显示状态消息
        if (message.contains("成功")) {
          SwingUtilities.invokeLater(() -> notificationComponent.showSuccess(message));
        } else if (message.contains("失败") || message.contains("错误")) {
          SwingUtilities.invokeLater(() -> notificationComponent.showError(message));
        } else {
          SwingUtilities.invokeLater(() -> notificationComponent.showInfo(message));
        }
      }
    });
  }

  /**
   * 在组件中查找JTable
   */
  private JTable findTableInComponent(Container container) {
    for (Component component : container.getComponents()) {
      if (component instanceof JTable) {
        return (JTable) component;
      } else if (component instanceof Container) {
        JTable table = findTableInComponent((Container) component);
        if (table != null) {
          return table;
        }
      }
    }
    return null;
  }

  /**
   * 更新详情面板
   */
  private void updateDetailPanel() {
    Appointment selectedAppointment = appointmentTableComponent.getSelectedAppointment();

    if (selectedAppointment == null) {
      detailTitleLabel.setText("请选择一个预约查看详情");
      detailTextArea.setText("");
      return;
    }

    detailTitleLabel.setText("预约ID: " + selectedAppointment.getAppointmentId());

    StringBuilder detail = new StringBuilder();
    detail.append("基本信息：\n");
    detail.append("• 预约ID：").append(selectedAppointment.getAppointmentId()).append("\n");
    detail.append("• 用户：").append(viewModel.getUserName(selectedAppointment.getUserId())).append("\n");
    detail.append("• 检查组：").append(viewModel.getCheckGroupName(selectedAppointment.getGroupId())).append("\n");
    detail.append("• 预约日期：").append(selectedAppointment.getAppointmentDate()).append("\n");
    detail.append("• 预约时间：").append(selectedAppointment.getAppointmentTime()).append("\n");
    detail.append("• 体检方式：").append(selectedAppointment.getExaminationMethod()).append("\n");
    detail.append("• 当前状态：").append(selectedAppointment.getStatus()).append("\n\n");

    detail.append("时间信息：\n");
    detail.append("• 创建时间：").append(selectedAppointment.getCreatedAt()).append("\n");
    if (selectedAppointment.getUpdatedAt() != null) {
      detail.append("• 更新时间：").append(selectedAppointment.getUpdatedAt()).append("\n");
    }

    // 根据状态显示操作提示
    detail.append("\n\n操作提示：\n");
    String status = selectedAppointment.getStatus();
    switch (status) {
      case "待确认":
        detail.append("• 可以确认预约或取消预约");
        break;
      case "已确认":
        detail.append("• 可以标记为已完成或取消预约");
        break;
      case "已完成":
        detail.append("• 体检已完成，可以添加健康数据");
        break;
      case "已取消":
        detail.append("• 预约已取消");
        break;
      default:
        detail.append("• 无可用操作");
        break;
    }

    detailTextArea.setText(detail.toString());
    detailTextArea.setCaretPosition(0); // 滚动到顶部
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    viewModel.refreshData();
  }

  /**
   * 获取选中的预约
   */
  public Appointment getSelectedAppointment() {
    return appointmentTableComponent.getSelectedAppointment();
  }

  /**
   * 获取ViewModel
   */
  public AdminAppointmentViewModel getViewModel() {
    return viewModel;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();

      switch (propertyName) {
        case "selectedAppointment":
          updateDetailPanel();
          break;
        case "appointmentList":
          // 预约列表更新时，清空详情面板
          detailTitleLabel.setText("请选择一个预约查看详情");
          detailTextArea.setText("");
          break;
      }
    });
  }
}