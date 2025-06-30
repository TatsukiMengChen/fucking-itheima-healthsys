package com.healthsys.view.admin.appointment.component;

import com.healthsys.model.entity.Appointment;
import com.healthsys.viewmodel.admin.appointment.AdminAppointmentViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 管理员预约管理表格组件
 * 显示所有用户的预约记录，支持搜索、筛选和状态管理
 * 
 * @author AI Assistant
 */
public class AdminAppointmentTableComponent extends JPanel implements PropertyChangeListener {

  private AdminAppointmentViewModel viewModel;

  // UI组件
  private JTable appointmentTable;
  private DefaultTableModel tableModel;
  private JScrollPane scrollPane;

  // 搜索和筛选组件
  private JTextField searchField;
  private JButton searchButton;
  private JButton clearButton;
  private JComboBox<String> statusFilterCombo;

  // 操作按钮
  private JButton refreshButton;
  private JButton confirmButton;
  private JButton completeButton;
  private JButton cancelButton;
  private JButton dataEntryButton;

  // 状态标签
  private JLabel statusLabel;

  // 表格列名
  private final String[] columnNames = {
      "预约ID", "用户", "检查组", "预约日期", "预约时间", "体检方式", "状态", "创建时间"
  };

  // 日期时间格式化器
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public AdminAppointmentTableComponent(AdminAppointmentViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);
    initializeComponents();
    setupLayout();
    bindEvents();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 创建表格模型
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // 表格不可编辑
      }
    };

    // 创建表格
    appointmentTable = new JTable(tableModel);
    appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    appointmentTable.setRowHeight(25);
    appointmentTable.getTableHeader().setReorderingAllowed(false);

    // 设置列宽
    setupColumnWidths();

    // 创建滚动面板
    scrollPane = new JScrollPane(appointmentTable);
    scrollPane.setPreferredSize(new Dimension(900, 400));

    // 搜索组件
    searchField = new JTextField(15);
    searchField.setToolTipText("搜索预约ID、用户名或检查组");

    searchButton = new JButton("搜索");
    searchButton.setPreferredSize(new Dimension(80, 30));

    clearButton = new JButton("清空");
    clearButton.setPreferredSize(new Dimension(80, 30));

    // 状态筛选
    statusFilterCombo = new JComboBox<>(viewModel.getAppointmentStatuses());
    statusFilterCombo.setPreferredSize(new Dimension(120, 30));

    // 操作按钮
    refreshButton = new JButton("刷新");
    refreshButton.setPreferredSize(new Dimension(80, 30));

    confirmButton = new JButton("确认预约");
    confirmButton.setPreferredSize(new Dimension(100, 30));
    confirmButton.setEnabled(false);

    completeButton = new JButton("完成体检");
    completeButton.setPreferredSize(new Dimension(100, 30));
    completeButton.setEnabled(false);

    cancelButton = new JButton("取消预约");
    cancelButton.setPreferredSize(new Dimension(100, 30));
    cancelButton.setEnabled(false);

    dataEntryButton = new JButton("录入体检数据");
    dataEntryButton.setPreferredSize(new Dimension(120, 30));
    dataEntryButton.setBackground(new Color(255, 193, 7));
    dataEntryButton.setForeground(Color.BLACK);
    dataEntryButton.setEnabled(false);

    // 状态标签
    statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置表格列宽
   */
  private void setupColumnWidths() {
    TableColumnModel columnModel = appointmentTable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(80); // 预约ID
    columnModel.getColumn(1).setPreferredWidth(150); // 用户
    columnModel.getColumn(2).setPreferredWidth(150); // 检查组
    columnModel.getColumn(3).setPreferredWidth(100); // 预约日期
    columnModel.getColumn(4).setPreferredWidth(80); // 预约时间
    columnModel.getColumn(5).setPreferredWidth(100); // 体检方式
    columnModel.getColumn(6).setPreferredWidth(80); // 状态
    columnModel.getColumn(7).setPreferredWidth(130); // 创建时间
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 创建顶部工具栏
    JPanel toolbarPanel = createToolbarPanel();
    add(toolbarPanel, BorderLayout.NORTH);

    // 添加表格
    add(scrollPane, BorderLayout.CENTER);

    // 创建底部状态栏
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusPanel.add(statusLabel);
    add(statusPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建工具栏面板
   */
  private JPanel createToolbarPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

    // 标题面板
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel titleLabel = new JLabel("预约管理");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
    titleLabel.setForeground(new Color(51, 51, 51));
    titlePanel.add(titleLabel);

    // 搜索面板
    JPanel searchPanel = createSearchPanel();

    // 操作按钮面板
    JPanel buttonPanel = createButtonPanel();

    panel.add(titlePanel, BorderLayout.WEST);
    panel.add(searchPanel, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.EAST);

    return panel;
  }

  /**
   * 创建搜索面板
   */
  private JPanel createSearchPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    panel.add(new JLabel("搜索:"));
    panel.add(searchField);
    panel.add(searchButton);
    panel.add(clearButton);

    panel.add(Box.createHorizontalStrut(20));

    panel.add(new JLabel("状态:"));
    panel.add(statusFilterCombo);

    return panel;
  }

  /**
   * 创建操作按钮面板
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    panel.add(refreshButton);
    panel.add(Box.createHorizontalStrut(10));
    panel.add(confirmButton);
    panel.add(completeButton);
    panel.add(cancelButton);
    panel.add(Box.createHorizontalStrut(10));
    panel.add(dataEntryButton);

    return panel;
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 搜索按钮事件
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String keyword = searchField.getText().trim();
        viewModel.searchAppointmentsCommand(keyword);
      }
    });

    // 清空按钮事件
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        viewModel.clearSearchConditions();
      }
    });

    // 状态筛选事件
    statusFilterCombo.addActionListener(e -> {
      String selectedStatus = (String) statusFilterCombo.getSelectedItem();
      if (selectedStatus != null) {
        viewModel.setSelectedStatus(selectedStatus);
      }
    });

    // 刷新按钮事件
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        viewModel.refreshData();
      }
    });

    // 确认预约按钮事件
    confirmButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateSelectedAppointmentStatus("已确认");
      }
    });

    // 完成体检按钮事件
    completeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateSelectedAppointmentStatus("已完成");
      }
    });

    // 取消预约按钮事件
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateSelectedAppointmentStatus("已取消");
      }
    });

    // 录入体检数据按钮事件
    dataEntryButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        navigateToHealthDataEntry();
      }
    });

    // 表格选择事件
    appointmentTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        updateButtonStates();
      }
    });

    // 回车键搜索
    searchField.addActionListener(e -> {
      String keyword = searchField.getText().trim();
      viewModel.searchAppointmentsCommand(keyword);
    });
  }

  /**
   * 更新选中预约的状态
   */
  private void updateSelectedAppointmentStatus(String newStatus) {
    int selectedRow = appointmentTable.getSelectedRow();
    if (selectedRow < 0) {
      return;
    }

    Integer appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
    String currentStatus = (String) tableModel.getValueAt(selectedRow, 6);

    // 确认操作
    int result = JOptionPane.showConfirmDialog(
        this,
        "确定要将预约ID为 " + appointmentId + " 的状态从 \"" + currentStatus + "\" 更改为 \"" + newStatus + "\" 吗？",
        "确认状态更改",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (result == JOptionPane.YES_OPTION) {
      viewModel.updateAppointmentStatusCommand(appointmentId, newStatus);
    }
  }

  /**
   * 更新表格数据
   */
  private void updateTableData() {
    // 清空现有数据
    tableModel.setRowCount(0);

    // 添加新数据
    List<Appointment> appointments = viewModel.getAppointmentList();
    if (appointments != null) {
      for (Appointment appointment : appointments) {
        Object[] rowData = {
            appointment.getAppointmentId(),
            viewModel.getUserName(appointment.getUserId()),
            viewModel.getCheckGroupName(appointment.getGroupId()),
            appointment.getAppointmentDate() != null ? appointment.getAppointmentDate().format(dateFormatter) : "",
            appointment.getAppointmentTime() != null ? appointment.getAppointmentTime().format(timeFormatter) : "",
            appointment.getExaminationMethod(),
            appointment.getStatus(),
            appointment.getCreatedAt() != null ? appointment.getCreatedAt().format(dateTimeFormatter) : ""
        };
        tableModel.addRow(rowData);
      }
    }

    updateButtonStates();
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    int selectedRow = appointmentTable.getSelectedRow();
    boolean hasSelection = selectedRow >= 0;

    if (hasSelection) {
      String status = (String) tableModel.getValueAt(selectedRow, 6);

      // 根据当前状态决定哪些操作可用
      confirmButton.setEnabled("待确认".equals(status));
      completeButton.setEnabled("已确认".equals(status));
      cancelButton.setEnabled("待确认".equals(status) || "已确认".equals(status));
      dataEntryButton.setEnabled("已完成".equals(status));
    } else {
      confirmButton.setEnabled(false);
      completeButton.setEnabled(false);
      cancelButton.setEnabled(false);
      dataEntryButton.setEnabled(false);
    }
  }

  /**
   * 获取选中的预约
   */
  public Appointment getSelectedAppointment() {
    int selectedRow = appointmentTable.getSelectedRow();
    if (selectedRow < 0) {
      return null;
    }

    List<Appointment> appointments = viewModel.getAppointmentList();
    if (appointments != null && selectedRow < appointments.size()) {
      return appointments.get(selectedRow);
    }

    return null;
  }

  /**
   * 跳转到健康数据录入页面
   */
  private void navigateToHealthDataEntry() {
    Appointment selectedAppointment = getSelectedAppointment();
    if (selectedAppointment == null) {
      JOptionPane.showMessageDialog(this,
          "请先选择一个预约记录",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    // 调用ViewModel的跳转命令
    viewModel.navigateToHealthDataEntryCommand(selectedAppointment);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();

      switch (propertyName) {
        case "appointmentList":
          updateTableData();
          break;
        case "searchKeyword":
          searchField.setText(viewModel.getSearchKeyword());
          break;
        case "selectedStatus":
          statusFilterCombo.setSelectedItem(viewModel.getSelectedStatus());
          break;
        case "loading":
          boolean isLoading = viewModel.isLoading();
          setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());

          // 禁用/启用操作按钮
          searchButton.setEnabled(!isLoading);
          refreshButton.setEnabled(!isLoading);
          if (!isLoading) {
            updateButtonStates();
          } else {
            confirmButton.setEnabled(false);
            completeButton.setEnabled(false);
            cancelButton.setEnabled(false);
            dataEntryButton.setEnabled(false);
          }
          break;
        case "statusMessage":
          String message = viewModel.getStatusMessage();
          statusLabel.setText(message != null ? message : "就绪");
          break;
      }
    });
  }
}