package com.healthsys.view.user.healthdata.component;

import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.viewmodel.user.healthdata.UserHealthDataViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 用户健康数据表格组件
 * 负责显示健康数据列表和处理表格相关操作
 * 
 * @author 梦辰
 */
public class UserHealthDataTableComponent extends JPanel {

  private static final Logger logger = LoggerFactory.getLogger(UserHealthDataTableComponent.class);

  private final UserHealthDataViewModel viewModel;

  // 表格组件
  private JTable dataTable;
  private DefaultTableModel tableModel;
  private JScrollPane scrollPane;

  // 表格列名
  private final String[] columnNames = {
      "记录ID", "记录日期", "检查组", "检查项", "测量值", "参考值", "备注", "记录时间"
  };

  // 回调接口
  public interface SelectionCallback {
    void onSelectionChanged(ExaminationResult selectedResult);

    void onDoubleClick(ExaminationResult selectedResult);
  }

  private SelectionCallback selectionCallback;

  public UserHealthDataTableComponent(UserHealthDataViewModel viewModel) {
    this.viewModel = viewModel;
    initializeComponents();
    setupLayout();
    bindEvents();
    bindViewModelEvents();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 创建表格模型
    tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // 所有单元格不可编辑
      }
    };

    // 创建表格
    dataTable = new JTable(tableModel);
    dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataTable.setRowHeight(30);
    dataTable.getTableHeader().setReorderingAllowed(false);
    dataTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    dataTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));

    // 设置列宽
    setupColumnWidths();

    // 创建滚动面板
    scrollPane = new JScrollPane(dataTable);
    scrollPane.setBorder(BorderFactory.createTitledBorder("健康数据记录"));
  }

  /**
   * 设置列宽
   */
  private void setupColumnWidths() {
    TableColumnModel columnModel = dataTable.getColumnModel();
    int[] columnWidths = { 80, 100, 120, 150, 100, 120, 150, 150 };

    for (int i = 0; i < columnWidths.length && i < columnModel.getColumnCount(); i++) {
      columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
    }
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 表格选择事件
    dataTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        int selectedRow = dataTable.getSelectedRow();
        ExaminationResult selectedResult = null;

        if (selectedRow >= 0) {
          Integer resultId = (Integer) tableModel.getValueAt(selectedRow, 0);
          selectedResult = findResultById(resultId);
        }

        if (selectionCallback != null) {
          selectionCallback.onSelectionChanged(selectedResult);
        }
      }
    });

    // 表格双击事件
    dataTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int selectedRow = dataTable.getSelectedRow();
          if (selectedRow >= 0) {
            Integer resultId = (Integer) tableModel.getValueAt(selectedRow, 0);
            ExaminationResult selectedResult = findResultById(resultId);

            if (selectionCallback != null && selectedResult != null) {
              selectionCallback.onDoubleClick(selectedResult);
            }
          }
        }
      }
    });
  }

  /**
   * 绑定ViewModel事件
   */
  private void bindViewModelEvents() {
    viewModel.addPropertyChangeListener("healthDataList", evt -> {
      SwingUtilities.invokeLater(this::updateTableData);
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
            result.getResultId(), // 记录ID
            result.getRecordedAt() != null ? result.getRecordedAt().toLocalDate().toString() : "-", // 记录日期
            viewModel.getCheckGroupName(result.getGroupId()), // 检查组名称
            viewModel.getCheckItemName(result.getItemId()), // 检查项名称
            result.getMeasuredValue(), // 测量值
            viewModel.getCheckItemReferenceValue(result.getItemId()), // 参考值
            result.getResultNotes() != null ? result.getResultNotes() : "-", // 备注
            result.getRecordedAt() != null ? result.getRecordedAt().toString() : "-" // 记录时间
        };
        tableModel.addRow(rowData);
      }
    }

    logger.debug("表格数据已更新，共 {} 条记录", dataList != null ? dataList.size() : 0);
  }

  /**
   * 根据ID查找结果对象
   */
  private ExaminationResult findResultById(Integer resultId) {
    List<ExaminationResult> dataList = viewModel.getHealthDataList();
    if (dataList != null) {
      for (ExaminationResult result : dataList) {
        if (result.getResultId().equals(resultId)) {
          return result;
        }
      }
    }
    return null;
  }

  /**
   * 获取选中的结果
   */
  public ExaminationResult getSelectedResult() {
    int selectedRow = dataTable.getSelectedRow();
    if (selectedRow >= 0) {
      Integer resultId = (Integer) tableModel.getValueAt(selectedRow, 0);
      return findResultById(resultId);
    }
    return null;
  }

  /**
   * 是否有选中的行
   */
  public boolean hasSelection() {
    return dataTable.getSelectedRow() >= 0;
  }

  /**
   * 清空选择
   */
  public void clearSelection() {
    dataTable.clearSelection();
  }

  /**
   * 设置选择回调
   */
  public void setSelectionCallback(SelectionCallback callback) {
    this.selectionCallback = callback;
  }

  /**
   * 刷新数据
   */
  public void refreshData() {
    updateTableData();
  }
}