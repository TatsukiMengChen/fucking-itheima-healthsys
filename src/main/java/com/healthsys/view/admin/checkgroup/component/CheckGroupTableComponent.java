package com.healthsys.view.admin.checkgroup.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.healthsys.model.entity.CheckGroup;
import com.healthsys.view.common.PagingComponent;
import com.healthsys.viewmodel.admin.checkgroup.CheckGroupManagementViewModel;

/**
 * 检查组表格组件
 * 显示检查组列表，包含搜索框和操作按钮
 * 
 * @author HealthSys Team
 */
public class CheckGroupTableComponent extends JPanel implements PropertyChangeListener {

  private CheckGroupManagementViewModel viewModel;

  // UI组件
  private JTextField searchNameField;
  private JTextField searchCodeField;
  private JButton searchButton;
  private JButton clearButton;
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JButton refreshButton;
  private JTable checkGroupTable;
  private CheckGroupTableModel tableModel;
  private PagingComponent pagingComponent;
  private JLabel statusLabel;

  /**
   * 构造函数
   */
  public CheckGroupTableComponent() {
    initializeComponents();
    setupLayout();
    setupEventHandlers();
  }

  /**
   * 设置ViewModel
   */
  public void setViewModel(CheckGroupManagementViewModel viewModel) {
    if (this.viewModel != null) {
      this.viewModel.removePropertyChangeListener(this);
    }

    this.viewModel = viewModel;

    if (this.viewModel != null) {
      this.viewModel.addPropertyChangeListener(this);
      updateFromViewModel();
    }
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 搜索组件
    searchNameField = new JTextField(15);
    searchNameField.setToolTipText("搜索检查组名称");

    searchCodeField = new JTextField(15);
    searchCodeField.setToolTipText("搜索检查组代码");

    searchButton = new JButton("搜索");
    clearButton = new JButton("清空");

    // 操作按钮
    addButton = new JButton("新增");
    editButton = new JButton("编辑");
    deleteButton = new JButton("删除");
    refreshButton = new JButton("刷新");

    // 设置按钮颜色
    addButton.setBackground(new Color(46, 204, 113));
    addButton.setForeground(Color.WHITE);
    editButton.setBackground(new Color(52, 152, 219));
    editButton.setForeground(Color.WHITE);
    deleteButton.setBackground(new Color(231, 76, 60));
    deleteButton.setForeground(Color.WHITE);
    refreshButton.setBackground(new Color(149, 165, 166));
    refreshButton.setForeground(Color.WHITE);

    // 表格
    tableModel = new CheckGroupTableModel();
    checkGroupTable = new JTable(tableModel);
    checkGroupTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    checkGroupTable.setRowHeight(30);
    checkGroupTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

    // 设置列宽
    setupTableColumns();

    // 分页组件
    pagingComponent = new PagingComponent();

    // 状态标签
    statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置表格列
   */
  private void setupTableColumns() {
    TableColumnModel columnModel = checkGroupTable.getColumnModel();

    // 设置列宽
    columnModel.getColumn(0).setPreferredWidth(60); // ID
    columnModel.getColumn(1).setPreferredWidth(120); // 代码
    columnModel.getColumn(2).setPreferredWidth(150); // 名称
    columnModel.getColumn(3).setPreferredWidth(200); // 描述
    columnModel.getColumn(4).setPreferredWidth(80); // 状态
    columnModel.getColumn(5).setPreferredWidth(120); // 创建时间

    // 居中显示ID和状态列
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(JLabel.CENTER);
    columnModel.getColumn(0).setCellRenderer(centerRenderer);
    columnModel.getColumn(4).setCellRenderer(centerRenderer);

    // 自定义状态列渲染器
    columnModel.getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(JLabel.CENTER);

        if (value != null) {
          Boolean isActive = (Boolean) value;
          if (isActive) {
            setText("启用");
            setForeground(isSelected ? Color.WHITE : new Color(46, 204, 113));
          } else {
            setText("禁用");
            setForeground(isSelected ? Color.WHITE : new Color(231, 76, 60));
          }
        }

        return this;
      }
    });
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 顶部面板容器
    JPanel topPanel = new JPanel(new BorderLayout(10, 10));

    // 搜索面板（顶部左侧）
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    searchPanel.add(new JLabel("名称:"));
    searchPanel.add(searchNameField);
    searchPanel.add(new JLabel("代码:"));
    searchPanel.add(searchCodeField);
    searchPanel.add(searchButton);
    searchPanel.add(clearButton);

    // 操作按钮工具栏（顶部右侧）
    JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    toolbarPanel.add(addButton);
    toolbarPanel.add(editButton);
    toolbarPanel.add(deleteButton);
    toolbarPanel.add(refreshButton);

    topPanel.add(searchPanel, BorderLayout.WEST);
    topPanel.add(toolbarPanel, BorderLayout.EAST);

    // 中间表格面板
    JScrollPane scrollPane = new JScrollPane(checkGroupTable);
    scrollPane.setBorder(BorderFactory.createTitledBorder("检查组列表"));

    // 底部面板
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(pagingComponent, BorderLayout.CENTER);
    bottomPanel.add(statusLabel, BorderLayout.EAST);

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

    // 清空按钮事件
    clearButton.addActionListener(e -> clearSearch());

    // 操作按钮事件
    addButton.addActionListener(e -> {
      if (viewModel != null) {
        viewModel.addCheckGroup();
      }
    });

    editButton.addActionListener(e -> {
      if (viewModel != null) {
        viewModel.editCheckGroup();
      }
    });

    deleteButton.addActionListener(e -> {
      if (viewModel != null) {
        viewModel.deleteCheckGroup();
      }
    });

    refreshButton.addActionListener(e -> {
      if (viewModel != null) {
        viewModel.refreshData();
      }
    });

    // 表格选择事件
    checkGroupTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        int selectedRow = checkGroupTable.getSelectedRow();
        if (selectedRow >= 0 && viewModel != null) {
          CheckGroup selectedGroup = tableModel.getCheckGroupAt(selectedRow);
          viewModel.setSelectedCheckGroup(selectedGroup);
        }
      }
    });

    // 回车键搜索
    ActionListener searchAction = e -> performSearch();
    searchNameField.addActionListener(searchAction);
    searchCodeField.addActionListener(searchAction);

    // 分页组件事件
    pagingComponent.setPagingListener(new PagingComponent.PagingListener() {
      @Override
      public void onPageChanged(int newPage, int newPageSize) {
        if (viewModel != null) {
          viewModel.onPageChanged(newPage, newPageSize);
        }
      }
    });
  }

  /**
   * 执行搜索
   */
  private void performSearch() {
    if (viewModel != null) {
      viewModel.setSearchName(searchNameField.getText());
      viewModel.setSearchCode(searchCodeField.getText());
      viewModel.searchCheckGroups();
    }
  }

  /**
   * 清空搜索
   */
  private void clearSearch() {
    searchNameField.setText("");
    searchCodeField.setText("");
    if (viewModel != null) {
      viewModel.clearSearch();
    }
  }

  /**
   * 从ViewModel更新UI
   */
  private void updateFromViewModel() {
    if (viewModel == null)
      return;

    // 更新搜索字段
    searchNameField.setText(viewModel.getSearchName());
    searchCodeField.setText(viewModel.getSearchCode());

    // 更新表格数据
    tableModel.setCheckGroups(viewModel.getCheckGroupList());

    // 更新按钮状态
    updateButtonStates();

    // 更新分页信息
    updatePagingInfo();

    // 更新状态
    updateStatus();
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    if (viewModel == null)
      return;

    addButton.setEnabled(viewModel.isAddButtonEnabled());
    editButton.setEnabled(viewModel.isEditButtonEnabled());
    deleteButton.setEnabled(viewModel.isDeleteButtonEnabled());
    searchButton.setEnabled(viewModel.isSearchButtonEnabled());
  }

  /**
   * 更新分页信息
   */
  private void updatePagingInfo() {
    if (viewModel == null)
      return;

    pagingComponent.setPagingData(
        viewModel.getTotalRecords(),
        viewModel.getCurrentPage(),
        viewModel.getPageSize());
  }

  /**
   * 更新状态信息
   */
  private void updateStatus() {
    if (viewModel == null)
      return;

    if (viewModel.isLoading()) {
      statusLabel.setText("加载中...");
    } else {
      int totalRecords = viewModel.getTotalRecords();
      statusLabel.setText("共 " + totalRecords + " 条记录");
    }
  }

  /**
   * 获取选中的检查组
   */
  public CheckGroup getSelectedCheckGroup() {
    int selectedRow = checkGroupTable.getSelectedRow();
    if (selectedRow >= 0) {
      return tableModel.getCheckGroupAt(selectedRow);
    }
    return null;
  }

  /**
   * 设置选中的检查组
   */
  public void setSelectedCheckGroup(CheckGroup checkGroup) {
    if (checkGroup == null) {
      checkGroupTable.clearSelection();
      return;
    }

    for (int i = 0; i < tableModel.getRowCount(); i++) {
      CheckGroup group = tableModel.getCheckGroupAt(i);
      if (group != null && group.getGroupId().equals(checkGroup.getGroupId())) {
        checkGroupTable.setRowSelectionInterval(i, i);
        break;
      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();

      switch (propertyName) {
        case "checkGroupList":
          tableModel.setCheckGroups(viewModel.getCheckGroupList());
          break;
        case "searchName":
          searchNameField.setText(viewModel.getSearchName());
          break;
        case "searchCode":
          searchCodeField.setText(viewModel.getSearchCode());
          break;
        case "loading":
        case "addButtonEnabled":
        case "editButtonEnabled":
        case "deleteButtonEnabled":
        case "searchButtonEnabled":
          updateButtonStates();
          updateStatus();
          break;
        case "currentPage":
        case "pageSize":
        case "totalPages":
        case "totalRecords":
          updatePagingInfo();
          updateStatus();
          break;
        case "selectedCheckGroup":
          setSelectedCheckGroup(viewModel.getSelectedCheckGroup());
          break;
      }
    });
  }

  /**
   * 检查组表格模型
   */
  private static class CheckGroupTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
        "ID", "代码", "名称", "描述", "状态", "创建时间"
    };

    private List<CheckGroup> checkGroups = new ArrayList<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setCheckGroups(List<CheckGroup> checkGroups) {
      this.checkGroups = checkGroups != null ? checkGroups : new ArrayList<>();
      fireTableDataChanged();
    }

    public CheckGroup getCheckGroupAt(int rowIndex) {
      if (rowIndex >= 0 && rowIndex < checkGroups.size()) {
        return checkGroups.get(rowIndex);
      }
      return null;
    }

    @Override
    public int getRowCount() {
      return checkGroups.size();
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
          return Integer.class;
        case 4:
          return Boolean.class;
        default:
          return String.class;
      }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= checkGroups.size()) {
        return null;
      }

      CheckGroup group = checkGroups.get(rowIndex);
      if (group == null) {
        return null;
      }

      switch (columnIndex) {
        case 0:
          return group.getGroupId();
        case 1:
          return group.getGroupCode();
        case 2:
          return group.getGroupName();
        case 3:
          return group.getDescription() != null ? group.getDescription() : "";
        case 4:
          return group.getIsActive();
        case 5:
          return group.getCreatedAt() != null ? group.getCreatedAt().format(dateFormatter) : "";
        default:
          return null;
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }
  }
}