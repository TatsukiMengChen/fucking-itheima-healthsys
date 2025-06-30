package com.healthsys.view.user.tracking;

import com.healthsys.view.base.BasePanel;
import com.healthsys.viewmodel.user.tracking.HealthTrackingViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * 健康跟踪主面板
 * 组合图表组件和病史列表组件
 * 
 * @author AI Assistant
 */
public class HealthTrackingPanel extends BasePanel {

  private HealthTrackingViewModel viewModel;

  // UI组件
  private JPanel historyPanel;
  private JPanel filterPanel;

  public HealthTrackingPanel() {
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
    this.viewModel = new HealthTrackingViewModel();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 只创建病史记录面板，不再使用选项卡
    historyPanel = createHistoryPanel();

    // 创建筛选面板
    filterPanel = createFilterPanel();
  }

  /**
   * 创建病史记录面板
   */
  private JPanel createHistoryPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 创建病史表格
    String[] columnNames = { "诊断日期", "诊断结果", "医生姓名", "治疗方案", "备注" };
    Object[][] data = {}; // 空数据，将通过ViewModel加载

    JTable historyTable = new JTable(data, columnNames);
    historyTable.setRowHeight(25);
    historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(historyTable);
    scrollPane.setPreferredSize(new Dimension(700, 400));

    // 创建病史控制面板
    JPanel historyControlPanel = createHistoryControlPanel();

    panel.add(historyControlPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * 创建病史控制面板
   */
  private JPanel createHistoryControlPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

    // 搜索框
    panel.add(new JLabel("搜索诊断:"));
    JTextField searchField = new JTextField(15);
    panel.add(searchField);

    // 搜索按钮
    JButton searchButton = new JButton("搜索");
    searchButton.addActionListener(e -> {
      String keyword = searchField.getText().trim();
      viewModel.searchMedicalHistoryCommand(keyword);
    });
    panel.add(searchButton);

    panel.add(Box.createHorizontalStrut(20));

    // 刷新按钮
    JButton refreshButton = new JButton("刷新");
    refreshButton.addActionListener(e -> viewModel.loadMedicalHistoryCommand());
    panel.add(refreshButton);

    panel.add(Box.createHorizontalStrut(20));

    // 显示最近记录按钮
    JButton recentButton = new JButton("最近10条");
    recentButton.addActionListener(e -> viewModel.loadRecentMedicalHistoryCommand(10));
    panel.add(recentButton);

    return panel;
  }

  /**
   * 创建筛选面板
   */
  private JPanel createFilterPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createTitledBorder("筛选条件"));

    // 日期范围筛选
    panel.add(new JLabel("开始日期:"));
    JTextField startDateField = new JTextField(10);
    startDateField.setToolTipText("格式：yyyy-MM-dd");
    panel.add(startDateField);

    panel.add(new JLabel("结束日期:"));
    JTextField endDateField = new JTextField(10);
    endDateField.setToolTipText("格式：yyyy-MM-dd");
    panel.add(endDateField);

    // 应用筛选按钮
    JButton applyFilterButton = new JButton("应用筛选");
    applyFilterButton.addActionListener(e -> {
      // TODO: 实现日期筛选逻辑
      String startDate = startDateField.getText().trim();
      String endDate = endDateField.getText().trim();
      if (!startDate.isEmpty() && !endDate.isEmpty()) {
        // viewModel.filterByDateRangeCommand(startDate, endDate);
      }
    });
    panel.add(applyFilterButton);

    // 清空筛选按钮
    JButton clearFilterButton = new JButton("清空筛选");
    clearFilterButton.addActionListener(e -> {
      startDateField.setText("");
      endDateField.setText("");
      viewModel.clearFilters();
    });
    panel.add(clearFilterButton);

    return panel;
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());

    // 创建标题面板
    JPanel titlePanel = createTitlePanel();
    add(titlePanel, BorderLayout.NORTH);

    // 直接添加病史记录面板
    add(historyPanel, BorderLayout.CENTER);

    // 添加筛选面板
    add(filterPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建标题面板
   */
  private JPanel createTitlePanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    JLabel titleLabel = new JLabel("病史记录管理");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
    titleLabel.setForeground(new Color(51, 51, 51));

    JLabel descLabel = new JLabel("查看和管理个人病史记录");
    descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    descLabel.setForeground(new Color(102, 102, 102));

    panel.add(titleLabel);
    panel.add(Box.createHorizontalStrut(15));
    panel.add(descLabel);

    return panel;
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 监听ViewModel状态变化
    viewModel.addPropertyChangeListener("loading", evt -> {
      boolean isLoading = (Boolean) evt.getNewValue();
      SwingUtilities.invokeLater(() -> {
        setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
      });
    });

    // 自动加载病史记录
    SwingUtilities.invokeLater(() -> {
      viewModel.loadMedicalHistoryCommand();
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
  public HealthTrackingViewModel getViewModel() {
    return viewModel;
  }
}