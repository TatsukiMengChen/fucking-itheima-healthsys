package com.healthsys.view.user.appointment.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckGroup;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.viewmodel.user.appointment.AppointmentViewModel;

/**
 * 预约历史组件
 * 显示用户的预约历史记录
 * 
 * @author 梦辰
 */
public class AppointmentHistoryComponent extends JPanel {

  private AppointmentViewModel viewModel;
  private ICheckGroupService checkGroupService;

  // UI组件
  private JTable historyTable;
  private DefaultTableModel tableModel;
  private JScrollPane scrollPane;
  private JButton refreshButton;
  private JButton cancelButton;
  private JLabel statusLabel;

  // 表格列名
  private final String[] columnNames = {
      "预约ID", "检查组", "预约日期", "预约时间", "体检方式", "状态", "创建时间"
  };

  public AppointmentHistoryComponent(AppointmentViewModel viewModel) {
    this.viewModel = viewModel;
    this.checkGroupService = new CheckGroupServiceImpl();
    initializeComponents();
    setupLayout();
    bindEvents();
    loadData();
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
    historyTable = new JTable(tableModel);
    historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    historyTable.setRowHeight(25);
    historyTable.getTableHeader().setReorderingAllowed(false);

    // 设置列宽
    setupColumnWidths();

    // 创建滚动面板
    scrollPane = new JScrollPane(historyTable);
    scrollPane.setPreferredSize(new Dimension(700, 300));

    // 创建按钮
    refreshButton = new JButton("刷新");
    refreshButton.setPreferredSize(new Dimension(80, 30));

    cancelButton = new JButton("取消预约");
    cancelButton.setPreferredSize(new Dimension(100, 30));
    cancelButton.setEnabled(false);

    // 状态标签
    statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置表格列宽
   */
  private void setupColumnWidths() {
    TableColumnModel columnModel = historyTable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(80); // 预约ID
    columnModel.getColumn(1).setPreferredWidth(120); // 检查组
    columnModel.getColumn(2).setPreferredWidth(100); // 预约日期
    columnModel.getColumn(3).setPreferredWidth(80); // 预约时间
    columnModel.getColumn(4).setPreferredWidth(100); // 体检方式
    columnModel.getColumn(5).setPreferredWidth(80); // 状态
    columnModel.getColumn(6).setPreferredWidth(120); // 创建时间
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
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

    panel.add(new JLabel("预约历史记录"));
    panel.add(Box.createHorizontalStrut(20));
    panel.add(refreshButton);
    panel.add(Box.createHorizontalStrut(10));
    panel.add(cancelButton);

    return panel;
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 刷新按钮事件
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadData();
      }
    });

    // 取消预约按钮事件
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelSelectedAppointment();
      }
    });

    // 表格选择事件
    historyTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        updateButtonStates();
      }
    });

    // 监听ViewModel数据变化
    viewModel.addPropertyChangeListener("appointmentHistory", evt -> {
      SwingUtilities.invokeLater(() -> {
        updateTableData();
      });
    });

    // 监听状态消息变化
    viewModel.addPropertyChangeListener("statusMessage", evt -> {
      SwingUtilities.invokeLater(() -> {
        String message = (String) evt.getNewValue();
        statusLabel.setText(message != null ? message : "就绪");
      });
    });
  }

  /**
   * 加载数据
   */
  private void loadData() {
    viewModel.loadAppointmentHistoryCommand();
  }

  /**
   * 获取检查组名称
   */
  private String getCheckGroupName(Integer groupId) {
    if (groupId == null) {
      return "未知检查组";
    }

    try {
      CheckGroup checkGroup = checkGroupService.getCheckGroupById(groupId);
      if (checkGroup != null) {
        return checkGroup.getGroupName() + " (" + checkGroup.getGroupCode() + ")";
      } else {
        return "检查组_" + groupId + " (已删除)";
      }
    } catch (Exception e) {
      return "检查组_" + groupId + " (加载失败)";
    }
  }

  /**
   * 更新表格数据
   */
  private void updateTableData() {
    // 清空现有数据
    tableModel.setRowCount(0);

    // 添加新数据
    List<Appointment> appointments = viewModel.getAppointmentHistory();
    if (appointments != null) {
      for (Appointment appointment : appointments) {
        Object[] rowData = {
            appointment.getAppointmentId(),
            getCheckGroupName(appointment.getGroupId()), // 显示实际检查组名称
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime(),
            appointment.getExaminationMethod(),
            appointment.getStatus(),
            appointment.getCreatedAt()
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
    int selectedRow = historyTable.getSelectedRow();
    boolean hasSelection = selectedRow >= 0;

    if (hasSelection) {
      // 检查状态是否可以取消
      String status = (String) tableModel.getValueAt(selectedRow, 5);
      boolean canCancel = "待确认".equals(status) || "已确认".equals(status);
      cancelButton.setEnabled(canCancel);
    } else {
      cancelButton.setEnabled(false);
    }
  }

  /**
   * 取消选中的预约
   */
  private void cancelSelectedAppointment() {
    int selectedRow = historyTable.getSelectedRow();
    if (selectedRow < 0) {
      return;
    }

    Integer appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
    String status = (String) tableModel.getValueAt(selectedRow, 5);

    // 确认取消
    int result = JOptionPane.showConfirmDialog(
        this,
        "确定要取消预约ID为 " + appointmentId + " 的预约吗？",
        "确认取消",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (result == JOptionPane.YES_OPTION) {
      viewModel.cancelAppointmentCommand(appointmentId);
    }
  }

  /**
   * 获取选中的预约
   */
  public Appointment getSelectedAppointment() {
    int selectedRow = historyTable.getSelectedRow();
    if (selectedRow < 0) {
      return null;
    }

    List<Appointment> appointments = viewModel.getAppointmentHistory();
    if (appointments != null && selectedRow < appointments.size()) {
      return appointments.get(selectedRow);
    }

    return null;
  }
}