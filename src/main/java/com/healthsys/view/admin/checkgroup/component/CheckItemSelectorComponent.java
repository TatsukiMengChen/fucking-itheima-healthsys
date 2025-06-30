package com.healthsys.view.admin.checkgroup.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.healthsys.model.entity.CheckItem;

/**
 * 检查项选择器组件
 * 用于在创建/编辑检查组时选择检查项
 * 
 * @author HealthSys Team
 */
public class CheckItemSelectorComponent extends JPanel {

  // 数据
  private List<CheckItem> allCheckItems = new ArrayList<>();
  private Set<Integer> selectedItemIds = new HashSet<>();

  // UI组件
  private JTable checkItemTable;
  private CheckItemTableModel tableModel;
  private JLabel statusLabel;
  private JButton selectAllButton;
  private JButton selectNoneButton;
  private JTextField searchField;
  private JButton searchButton;

  // 事件监听器
  private SelectionChangeListener selectionChangeListener;

  /**
   * 构造函数
   */
  public CheckItemSelectorComponent() {
    initializeComponents();
    setupLayout();
    setupEventHandlers();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 表格
    tableModel = new CheckItemTableModel();
    checkItemTable = new JTable(tableModel);
    checkItemTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    checkItemTable.setRowHeight(25);
    checkItemTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    // 设置复选框列
    setupCheckBoxColumn();

    // 搜索组件
    searchField = new JTextField(15);
    searchField.setToolTipText("搜索检查项名称或代码");
    searchButton = new JButton("搜索");

    // 操作按钮
    selectAllButton = new JButton("全选");
    selectNoneButton = new JButton("全不选");

    // 状态标签
    statusLabel = new JLabel("已选择 0 项");
    statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置复选框列
   */
  private void setupCheckBoxColumn() {
    // 设置列宽
    checkItemTable.getColumnModel().getColumn(0).setPreferredWidth(50); // 选择
    checkItemTable.getColumnModel().getColumn(1).setPreferredWidth(60); // ID
    checkItemTable.getColumnModel().getColumn(2).setPreferredWidth(120); // 代码
    checkItemTable.getColumnModel().getColumn(3).setPreferredWidth(150); // 名称
    checkItemTable.getColumnModel().getColumn(4).setPreferredWidth(120); // 参考值
    checkItemTable.getColumnModel().getColumn(5).setPreferredWidth(80); // 单位

    // 复选框列渲染器和编辑器
    TableColumn selectColumn = checkItemTable.getColumnModel().getColumn(0);
    selectColumn.setCellRenderer(new CheckBoxRenderer());
    selectColumn.setCellEditor(new CheckBoxEditor());

    // 居中显示ID列
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    checkItemTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createTitledBorder("选择检查项"));

    // 顶部面板
    JPanel topPanel = new JPanel(new BorderLayout(10, 5));

    // 搜索面板
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    searchPanel.add(new JLabel("搜索:"));
    searchPanel.add(searchField);
    searchPanel.add(searchButton);

    // 操作按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    buttonPanel.add(selectAllButton);
    buttonPanel.add(selectNoneButton);

    topPanel.add(searchPanel, BorderLayout.WEST);
    topPanel.add(buttonPanel, BorderLayout.EAST);

    // 中间表格面板
    JScrollPane scrollPane = new JScrollPane(checkItemTable);
    scrollPane.setPreferredSize(new Dimension(600, 300));

    // 底部状态面板
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bottomPanel.add(statusLabel);

    add(topPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * 设置事件处理器
   */
  private void setupEventHandlers() {
    // 搜索按钮事件
    searchButton.addActionListener(e -> performSearch());

    // 回车键搜索
    searchField.addActionListener(e -> performSearch());

    // 全选按钮事件
    selectAllButton.addActionListener(e -> selectAll());

    // 全不选按钮事件
    selectNoneButton.addActionListener(e -> selectNone());
  }

  /**
   * 执行搜索
   */
  private void performSearch() {
    String keyword = searchField.getText().trim().toLowerCase();
    tableModel.setSearchKeyword(keyword);
    updateStatusLabel();
  }

  /**
   * 全选
   */
  private void selectAll() {
    selectedItemIds.clear();
    for (CheckItem item : tableModel.getFilteredItems()) {
      selectedItemIds.add(item.getItemId());
    }
    tableModel.fireTableDataChanged();
    updateStatusLabel();
    fireSelectionChanged();
  }

  /**
   * 全不选
   */
  private void selectNone() {
    selectedItemIds.clear();
    tableModel.fireTableDataChanged();
    updateStatusLabel();
    fireSelectionChanged();
  }

  /**
   * 更新状态标签
   */
  private void updateStatusLabel() {
    int selectedCount = selectedItemIds.size();
    statusLabel.setText("已选择 " + selectedCount + " 项");
  }

  /**
   * 设置所有检查项
   */
  public void setAllCheckItems(List<CheckItem> checkItems) {
    this.allCheckItems = checkItems != null ? checkItems : new ArrayList<>();
    tableModel.setCheckItems(this.allCheckItems);
    updateStatusLabel();
  }

  /**
   * 获取所有检查项
   */
  public List<CheckItem> getAllCheckItems() {
    return allCheckItems;
  }

  /**
   * 设置选中的检查项ID
   */
  public void setSelectedItemIds(Set<Integer> selectedIds) {
    this.selectedItemIds = selectedIds != null ? new HashSet<>(selectedIds) : new HashSet<>();
    tableModel.fireTableDataChanged();
    updateStatusLabel();
  }

  /**
   * 获取选中的检查项ID
   */
  public Set<Integer> getSelectedItemIds() {
    return new HashSet<>(selectedItemIds);
  }

  /**
   * 获取选中的检查项
   */
  public List<CheckItem> getSelectedCheckItems() {
    List<CheckItem> selectedItems = new ArrayList<>();
    for (CheckItem item : allCheckItems) {
      if (selectedItemIds.contains(item.getItemId())) {
        selectedItems.add(item);
      }
    }
    return selectedItems;
  }

  /**
   * 设置选择变更监听器
   */
  public void setSelectionChangeListener(SelectionChangeListener listener) {
    this.selectionChangeListener = listener;
  }

  /**
   * 触发选择变更事件
   */
  private void fireSelectionChanged() {
    if (selectionChangeListener != null) {
      selectionChangeListener.onSelectionChanged(getSelectedItemIds());
    }
  }

  /**
   * 选择变更监听器接口
   */
  public interface SelectionChangeListener {
    void onSelectionChanged(Set<Integer> selectedIds);
  }

  /**
   * 检查项表格模型
   */
  private class CheckItemTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
        "选择", "ID", "代码", "名称", "参考值", "单位"
    };

    private List<CheckItem> checkItems = new ArrayList<>();
    private List<CheckItem> filteredItems = new ArrayList<>();
    private String searchKeyword = "";

    public void setCheckItems(List<CheckItem> checkItems) {
      this.checkItems = checkItems != null ? checkItems : new ArrayList<>();
      filterItems();
    }

    public void setSearchKeyword(String keyword) {
      this.searchKeyword = keyword != null ? keyword.toLowerCase() : "";
      filterItems();
    }

    private void filterItems() {
      filteredItems.clear();

      if (searchKeyword.isEmpty()) {
        filteredItems.addAll(checkItems);
      } else {
        for (CheckItem item : checkItems) {
          if (item.getItemName().toLowerCase().contains(searchKeyword) ||
              item.getItemCode().toLowerCase().contains(searchKeyword)) {
            filteredItems.add(item);
          }
        }
      }

      fireTableDataChanged();
    }

    public List<CheckItem> getFilteredItems() {
      return filteredItems;
    }

    @Override
    public int getRowCount() {
      return filteredItems.size();
    }

    @Override
    public int getColumnCount() {
      return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
      return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
        case 0:
          return Boolean.class;
        case 1:
          return Integer.class;
        default:
          return String.class;
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex == 0; // 只有选择列可编辑
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= filteredItems.size()) {
        return null;
      }

      CheckItem item = filteredItems.get(rowIndex);
      if (item == null) {
        return null;
      }

      switch (columnIndex) {
        case 0:
          return selectedItemIds.contains(item.getItemId());
        case 1:
          return item.getItemId();
        case 2:
          return item.getItemCode();
        case 3:
          return item.getItemName();
        case 4:
          return item.getReferenceVal() != null ? item.getReferenceVal() : "";
        case 5:
          return item.getUnit() != null ? item.getUnit() : "";
        default:
          return null;
      }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      if (columnIndex == 0 && rowIndex < filteredItems.size()) {
        CheckItem item = filteredItems.get(rowIndex);
        Boolean selected = (Boolean) value;

        if (selected) {
          selectedItemIds.add(item.getItemId());
        } else {
          selectedItemIds.remove(item.getItemId());
        }

        updateStatusLabel();
        fireSelectionChanged();
        fireTableCellUpdated(rowIndex, columnIndex);
      }
    }
  }

  /**
   * 复选框渲染器
   */
  private static class CheckBoxRenderer extends JCheckBox implements javax.swing.table.TableCellRenderer {

    public CheckBoxRenderer() {
      setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      if (isSelected) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      } else {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
      }

      setSelected(value != null && (Boolean) value);
      return this;
    }
  }

  /**
   * 复选框编辑器
   */
  private static class CheckBoxEditor extends DefaultCellEditor {

    public CheckBoxEditor() {
      super(new JCheckBox());
      JCheckBox checkBox = (JCheckBox) getComponent();
      checkBox.setHorizontalAlignment(JLabel.CENTER);
    }
  }
}