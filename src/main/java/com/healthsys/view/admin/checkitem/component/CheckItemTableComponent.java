package com.healthsys.view.admin.checkitem.component;

import com.healthsys.model.entity.CheckItem;
import com.healthsys.view.common.PagingComponent;
import com.healthsys.viewmodel.admin.checkitem.CheckItemManagementViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查项表格组件
 * 使用JTable显示数据，包含搜索框和操作按钮
 * 
 * @author HealthSys Team
 */
public class CheckItemTableComponent extends JPanel {

  private CheckItemManagementViewModel viewModel;

  // 搜索区域组件
  private JTextField searchNameField;
  private JTextField searchCodeField;
  private JButton searchButton;
  private JButton clearButton;

  // 表格组件
  private JTable checkItemTable;
  private CheckItemTableModel tableModel;
  private JScrollPane tableScrollPane;

  // 操作按钮
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;

  // 分页组件
  private PagingComponent pagingComponent;

  /**
   * 构造函数
   */
  public CheckItemTableComponent(CheckItemManagementViewModel viewModel) {
    this.viewModel = viewModel;
    initializeComponents();
    setupLayout();
    bindViewModel();
    setupEventListeners();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 搜索区域
    searchNameField = new JTextField(15);
    searchCodeField = new JTextField(15);
    searchButton = new JButton("搜索");
    clearButton = new JButton("清空");

    // 表格
    tableModel = new CheckItemTableModel();
    checkItemTable = new JTable(tableModel);
    checkItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    checkItemTable.setRowHeight(25);

    tableScrollPane = new JScrollPane(checkItemTable);
    tableScrollPane.setPreferredSize(new Dimension(800, 400));

    // 操作按钮
    addButton = new JButton("添加");
    editButton = new JButton("编辑");
    deleteButton = new JButton("删除");

    // 分页组件
    pagingComponent = new PagingComponent();
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(0, 10));
    setBorder(new EmptyBorder(10, 10, 10, 10));

    // 顶部搜索面板
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.add(new JLabel("名称:"));
    searchPanel.add(searchNameField);
    searchPanel.add(new JLabel("代码:"));
    searchPanel.add(searchCodeField);
    searchPanel.add(searchButton);
    searchPanel.add(clearButton);

    add(searchPanel, BorderLayout.NORTH);

    // 中间表格面板
    JPanel tablePanel = new JPanel(new BorderLayout());

    // 工具栏
    JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    toolbarPanel.add(addButton);
    toolbarPanel.add(editButton);
    toolbarPanel.add(deleteButton);

    tablePanel.add(toolbarPanel, BorderLayout.NORTH);
    tablePanel.add(tableScrollPane, BorderLayout.CENTER);

    add(tablePanel, BorderLayout.CENTER);

    // 底部分页面板
    add(pagingComponent, BorderLayout.SOUTH);
  }

  /**
   * 绑定ViewModel
   */
  private void bindViewModel() {
    // 绑定搜索字段
    searchNameField.setText(viewModel.getSearchName());
    searchCodeField.setText(viewModel.getSearchCode());

    // 绑定按钮状态
    addButton.setEnabled(viewModel.isAddButtonEnabled());
    editButton.setEnabled(viewModel.isEditButtonEnabled());
    deleteButton.setEnabled(viewModel.isDeleteButtonEnabled());

    // 绑定表格数据
    tableModel.setData(viewModel.getCheckItemList());

    // 绑定分页数据
    pagingComponent.setPagingData(
        viewModel.getTotalRecords(),
        viewModel.getCurrentPage(),
        viewModel.getPageSize());

    // 监听ViewModel属性变化
    viewModel.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> handleViewModelPropertyChange(evt));
      }
    });
  }

  /**
   * 处理ViewModel属性变化
   */
  private void handleViewModelPropertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    switch (propertyName) {
      case "checkItemList":
        tableModel.setData(viewModel.getCheckItemList());
        break;
      case "editButtonEnabled":
        editButton.setEnabled(viewModel.isEditButtonEnabled());
        break;
      case "deleteButtonEnabled":
        deleteButton.setEnabled(viewModel.isDeleteButtonEnabled());
        break;
      case "addCheckItemRequested":
        firePropertyChange("addCheckItemRequested", false, true);
        break;
      case "editCheckItemRequested":
        firePropertyChange("editCheckItemRequested", null, evt.getNewValue());
        break;
    }
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 搜索按钮事件
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performSearch();
      }
    });

    // 清空按钮事件
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearSearch();
      }
    });

    // 操作按钮事件
    addButton.addActionListener(e -> viewModel.addCheckItem());
    editButton.addActionListener(e -> viewModel.editCheckItem());
    deleteButton.addActionListener(e -> viewModel.deleteCheckItem());

    // 表格选择事件
    checkItemTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        updateSelectedItem();
      }
    });

    // 分页事件
    pagingComponent.setPagingListener(new PagingComponent.PagingListener() {
      @Override
      public void onPageChanged(int currentPage, int pageSize) {
        viewModel.onPageChanged(currentPage, pageSize);
      }
    });
  }

  /**
   * 执行搜索
   */
  private void performSearch() {
    viewModel.setSearchName(searchNameField.getText());
    viewModel.setSearchCode(searchCodeField.getText());
    viewModel.searchCheckItems();
  }

  /**
   * 清空搜索
   */
  private void clearSearch() {
    searchNameField.setText("");
    searchCodeField.setText("");
    viewModel.clearSearch();
  }

  /**
   * 更新选中项
   */
  private void updateSelectedItem() {
    int selectedRow = checkItemTable.getSelectedRow();
    if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
      CheckItem selectedItem = tableModel.getCheckItemAt(selectedRow);
      viewModel.setSelectedCheckItem(selectedItem);
      firePropertyChange("selectedCheckItem", null, selectedItem);
    } else {
      viewModel.setSelectedCheckItem(null);
      firePropertyChange("selectedCheckItem", null, null);
    }
  }

  /**
   * 获取选中的检查项
   */
  public CheckItem getSelectedCheckItem() {
    return viewModel.getSelectedCheckItem();
  }

  /**
   * 设置选中的检查项
   */
  public void setSelectedCheckItem(CheckItem checkItem) {
    viewModel.setSelectedCheckItem(checkItem);

    // 更新表格选择
    if (checkItem != null) {
      for (int i = 0; i < tableModel.getRowCount(); i++) {
        CheckItem item = tableModel.getCheckItemAt(i);
        if (item != null && item.getItemId().equals(checkItem.getItemId())) {
          checkItemTable.setRowSelectionInterval(i, i);
          break;
        }
      }
    } else {
      checkItemTable.clearSelection();
    }
  }

  /**
   * 检查项表格模型
   */
  private static class CheckItemTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "代码", "名称", "参考值", "单位", "状态", "创建时间"
    };

    private List<CheckItem> data = new ArrayList<>();

    public void setData(List<CheckItem> data) {
      this.data = data != null ? data : new ArrayList<>();
      fireTableDataChanged();
    }

    public CheckItem getCheckItemAt(int rowIndex) {
      if (rowIndex >= 0 && rowIndex < data.size()) {
        return data.get(rowIndex);
      }
      return null;
    }

    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
      return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= data.size()) {
        return null;
      }

      CheckItem item = data.get(rowIndex);
      switch (columnIndex) {
        case 0:
          return item.getItemId();
        case 1:
          return item.getItemCode();
        case 2:
          return item.getItemName();
        case 3:
          return item.getReferenceVal();
        case 4:
          return item.getUnit();
        case 5:
          return item.getIsActive() != null && item.getIsActive() ? "启用" : "禁用";
        case 6:
          return item.getCreatedAt() != null
              ? item.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
              : "";
        default:
          return null;
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false; // 表格只读
    }
  }
}