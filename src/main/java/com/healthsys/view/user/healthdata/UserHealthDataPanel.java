package com.healthsys.view.user.healthdata;

import com.healthsys.view.base.BasePanel;
import com.healthsys.viewmodel.user.healthdata.UserHealthDataViewModel;
import com.healthsys.model.entity.ExaminationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 用户健康数据管理主面板
 * 管理用户自主录入的健康数据
 * 
 * @author AI Assistant
 */
public class UserHealthDataPanel extends BasePanel {

  private UserHealthDataViewModel viewModel;

  // UI组件
  private JTable dataTable;
  private DefaultTableModel tableModel;
  private JScrollPane scrollPane;

  // 搜索和筛选组件
  private JTextField searchField;
  private JButton searchButton;
  private JButton clearButton;
  private JComboBox<String> dateRangeCombo;

  // 操作按钮
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JButton refreshButton;

  // 状态标签
  private JLabel statusLabel;

  // 表格列名
  private final String[] columnNames = {
      "记录ID", "检查项", "测量值", "参考值", "备注", "记录时间"
  };

  public UserHealthDataPanel() {
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
    this.viewModel = new UserHealthDataViewModel();
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
    dataTable = new JTable(tableModel);
    dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.setRowHeight(25);
    dataTable.getTableHeader().setReorderingAllowed(false);

    // 设置列宽
    setupColumnWidths();

    // 创建滚动面板
    scrollPane = new JScrollPane(dataTable);
    scrollPane.setPreferredSize(new Dimension(800, 400));

    // 搜索组件
    searchField = new JTextField(15);
    searchField.setToolTipText("搜索测量值或备注");

    searchButton = new JButton("搜索");
    searchButton.setPreferredSize(new Dimension(80, 30));

    clearButton = new JButton("清空");
    clearButton.setPreferredSize(new Dimension(80, 30));

    // 日期范围筛选
    dateRangeCombo = new JComboBox<>(new String[] {
        "全部", "最近7天", "最近30天", "最近3个月", "最近半年"
    });
    dateRangeCombo.setPreferredSize(new Dimension(120, 30));

    // 操作按钮
    addButton = new JButton("添加数据");
    addButton.setPreferredSize(new Dimension(100, 30));

    editButton = new JButton("编辑");
    editButton.setPreferredSize(new Dimension(80, 30));
    editButton.setEnabled(false);

    deleteButton = new JButton("删除");
    deleteButton.setPreferredSize(new Dimension(80, 30));
    deleteButton.setEnabled(false);

    refreshButton = new JButton("刷新");
    refreshButton.setPreferredSize(new Dimension(80, 30));

    // 状态标签
    statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置表格列宽
   */
  private void setupColumnWidths() {
    TableColumnModel columnModel = dataTable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(80); // 记录ID
    columnModel.getColumn(1).setPreferredWidth(120); // 检查项
    columnModel.getColumn(2).setPreferredWidth(100); // 测量值
    columnModel.getColumn(3).setPreferredWidth(100); // 参考值
    columnModel.getColumn(4).setPreferredWidth(200); // 备注
    columnModel.getColumn(5).setPreferredWidth(150); // 记录时间
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
    JLabel titleLabel = new JLabel("健康数据管理");
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
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    panel.add(new JLabel("搜索:"));
    panel.add(searchField);
    panel.add(searchButton);
    panel.add(clearButton);

    panel.add(Box.createHorizontalStrut(20));

    panel.add(new JLabel("时间范围:"));
    panel.add(dateRangeCombo);

    return panel;
  }

  /**
   * 创建按钮面板
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    panel.add(addButton);
    panel.add(Box.createHorizontalStrut(5));
    panel.add(editButton);
    panel.add(Box.createHorizontalStrut(5));
    panel.add(deleteButton);
    panel.add(Box.createHorizontalStrut(5));
    panel.add(refreshButton);

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
        viewModel.searchHealthDataCommand(keyword);
      }
    });

    // 清空按钮事件
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchField.setText("");
        dateRangeCombo.setSelectedIndex(0);
        viewModel.clearSearchConditions();
      }
    });

    // 刷新按钮事件
    refreshButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        viewModel.refreshData();
      }
    });

    // 删除按钮事件
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        deleteSelectedData();
      }
    });

    // 表格选择事件
    dataTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        updateButtonStates();
      }
    });

    // 回车键搜索
    searchField.addActionListener(e -> {
      String keyword = searchField.getText().trim();
      viewModel.searchHealthDataCommand(keyword);
    });

    // 监听ViewModel数据变化
    bindViewModelEvents();
  }

  /**
   * 绑定ViewModel事件
   */
  private void bindViewModelEvents() {
    // 监听数据列表变化
    viewModel.addPropertyChangeListener("healthDataList", evt -> {
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

    // 监听加载状态变化
    viewModel.addPropertyChangeListener("loading", evt -> {
      boolean isLoading = (Boolean) evt.getNewValue();
      SwingUtilities.invokeLater(() -> {
        setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());

        // 禁用/启用操作按钮
        searchButton.setEnabled(!isLoading);
        refreshButton.setEnabled(!isLoading);
        addButton.setEnabled(!isLoading);
        if (!isLoading) {
          updateButtonStates();
        } else {
          editButton.setEnabled(false);
          deleteButton.setEnabled(false);
        }
      });
    });
  }

  /**
   * 更新表格数据
   */
  private void updateTableData() {
    // 清空现有数据
    tableModel.setRowCount(0);

    // 添加新数据
    List<ExaminationResult> dataList = viewModel.getHealthDataList();
    if (dataList != null) {
      for (ExaminationResult result : dataList) {
        Object[] rowData = {
            result.getResultId(),
            "检查项_" + result.getItemId(), // TODO: 显示实际检查项名称
            result.getMeasuredValue(),
            "参考值", // TODO: 从CheckItem获取参考值
            result.getResultNotes(),
            result.getRecordedAt()
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
    int selectedRow = dataTable.getSelectedRow();
    boolean hasSelection = selectedRow >= 0;

    editButton.setEnabled(hasSelection);
    deleteButton.setEnabled(hasSelection);
  }

  /**
   * 删除选中的数据
   */
  private void deleteSelectedData() {
    int selectedRow = dataTable.getSelectedRow();
    if (selectedRow < 0) {
      return;
    }

    Integer resultId = (Integer) tableModel.getValueAt(selectedRow, 0);

    // 确认删除
    int result = JOptionPane.showConfirmDialog(
        this,
        "确定要删除选中的健康数据吗？",
        "确认删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (result == JOptionPane.YES_OPTION) {
      viewModel.deleteHealthDataCommand(resultId);
    }
  }

  /**
   * 获取ViewModel
   */
  public UserHealthDataViewModel getViewModel() {
    return viewModel;
  }
}