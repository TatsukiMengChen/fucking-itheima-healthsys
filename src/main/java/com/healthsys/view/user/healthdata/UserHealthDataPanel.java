package com.healthsys.view.user.healthdata;

import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.view.base.BasePanel;
import com.healthsys.view.user.healthdata.component.SingleExaminationDataEntryDialog;
import com.healthsys.view.user.healthdata.component.UserHealthDataTableComponent;
import com.healthsys.view.user.healthdata.component.UserHealthDataSearchComponent;
import com.healthsys.view.user.healthdata.component.UserHealthDataToolbarComponent;
import com.healthsys.viewmodel.user.healthdata.UserHealthDataViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 用户健康数据管理面板
 * 重构后的简化版本，使用组件化设计
 * 
 * @author AI Assistant
 */
public class UserHealthDataPanel extends BasePanel {

  private static final Logger logger = LoggerFactory.getLogger(UserHealthDataPanel.class);

  private UserHealthDataViewModel viewModel;

  // 子组件
  private UserHealthDataSearchComponent searchComponent;
  private UserHealthDataTableComponent tableComponent;
  private UserHealthDataToolbarComponent toolbarComponent;

  // 管理员录入模式相关
  private boolean isAdminEntryMode = false;
  private com.healthsys.model.entity.Appointment currentAppointment;

  public UserHealthDataPanel() {
    initializeViewModel();
    initializeComponents();
    setupLayout();
    bindEvents();

    logger.info("用户健康数据管理面板初始化完成");
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
    // 创建子组件
    searchComponent = new UserHealthDataSearchComponent(viewModel);
    tableComponent = new UserHealthDataTableComponent(viewModel);
    toolbarComponent = new UserHealthDataToolbarComponent();
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));

    // 顶部：搜索组件
    add(searchComponent, BorderLayout.NORTH);

    // 中间：表格组件
    add(tableComponent, BorderLayout.CENTER);

    // 底部：工具栏组件
    add(toolbarComponent, BorderLayout.SOUTH);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 设置表格选择回调
    tableComponent.setSelectionCallback(new UserHealthDataTableComponent.SelectionCallback() {
      @Override
      public void onSelectionChanged(ExaminationResult selectedResult) {
        toolbarComponent.updateButtonStates(selectedResult != null);
      }

      @Override
      public void onDoubleClick(ExaminationResult selectedResult) {
        showResultDetail(selectedResult);
      }
    });

    // 设置搜索组件回调
    searchComponent.setSearchCallback(new UserHealthDataSearchComponent.SearchCallback() {
      @Override
      public void onSearch(String keyword) {
        logger.debug("搜索健康数据，关键词: {}", keyword);
        // 搜索逻辑已在SearchComponent中通过ViewModel处理
      }

      @Override
      public void onClear() {
        logger.debug("清空搜索条件");
        // 清空逻辑已在SearchComponent中通过ViewModel处理
      }

      @Override
      public void onAppointmentSelected(com.healthsys.model.entity.Appointment appointment) {
        logger.debug("选择预约: {}", appointment != null ? appointment.getAppointmentId() : "全部");
        // 通过ViewModel设置选中的预约ID
        viewModel.setSelectedAppointmentId(appointment != null ? appointment.getAppointmentId() : null);
      }
    });

    // 设置工具栏回调
    toolbarComponent.setToolbarCallback(new UserHealthDataToolbarComponent.ToolbarCallback() {
      @Override
      public void onAddData() {
        if (isAdminEntryMode) {
          showAddDataDialog();
        } else {
          ExaminationResult selected = tableComponent.getSelectedResult();
          if (selected != null) {
            showResultDetail(selected);
          }
        }
      }

      @Override
      public void onDeleteData() {
        ExaminationResult selected = tableComponent.getSelectedResult();
        if (selected != null) {
          deleteHealthData(selected);
        }
      }

      @Override
      public void onRefreshData() {
        viewModel.refreshData();
        // 同时刷新预约列表
        searchComponent.refreshAppointments();
      }

      @Override
      public void onExportData() {
        exportHealthData();
      }

      @Override
      public void onPrintReport() {
        printHealthReport();
      }
    });

    // 绑定ViewModel事件
    bindViewModelEvents();
  }

  /**
   * 绑定ViewModel事件
   */
  private void bindViewModelEvents() {
    viewModel.addPropertyChangeListener("loading", evt -> {
      SwingUtilities.invokeLater(() -> {
        boolean isLoading = (Boolean) evt.getNewValue();
        toolbarComponent.setLoading(isLoading);
      });
    });

    viewModel.addPropertyChangeListener("statusMessage", evt -> {
      SwingUtilities.invokeLater(() -> {
        String message = (String) evt.getNewValue();
        toolbarComponent.setStatusMessage(message);
      });
    });
  }

  /**
   * 显示体检结果详情
   */
  private void showResultDetail(ExaminationResult result) {
    if (result == null) {
      JOptionPane.showMessageDialog(
          this,
          "请先选择要查看的体检记录",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    StringBuilder details = new StringBuilder();
    details.append("体检结果详情\n");
    details.append("=================\n\n");
    details.append("记录ID: ").append(result.getResultId()).append("\n");
    details.append("预约ID: ").append(result.getAppointmentId()).append("\n");
    details.append("检查组: ").append(viewModel.getCheckGroupName(result.getGroupId())).append("\n");
    details.append("检查项: ").append(viewModel.getCheckItemName(result.getItemId())).append("\n");
    details.append("测量值: ").append(result.getMeasuredValue()).append("\n");
    details.append("参考值: ").append(viewModel.getCheckItemReferenceValue(result.getItemId())).append("\n");
    details.append("记录时间: ").append(result.getRecordedAt()).append("\n");

    if (result.getResultNotes() != null && !result.getResultNotes().trim().isEmpty()) {
      details.append("备注: ").append(result.getResultNotes()).append("\n");
    }

    JTextArea textArea = new JTextArea(details.toString());
    textArea.setEditable(false);
    textArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));

    JOptionPane.showMessageDialog(
        this,
        scrollPane,
        "体检结果详情",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * 删除健康数据
   */
  private void deleteHealthData(ExaminationResult result) {
    int option = JOptionPane.showConfirmDialog(
        this,
        "确定要删除这条体检记录吗？\n\n" +
            "检查项: " + viewModel.getCheckItemName(result.getItemId()) + "\n" +
            "测量值: " + result.getMeasuredValue() + "\n" +
            "记录时间: " + result.getRecordedAt(),
        "确认删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (option == JOptionPane.YES_OPTION) {
      viewModel.deleteHealthDataCommand(result.getResultId());
    }
  }

  /**
   * 导出健康数据
   */
  private void exportHealthData() {
    JOptionPane.showMessageDialog(
        this,
        "导出功能正在开发中...\n" +
            "将支持导出为Excel、PDF等格式",
        "功能提示",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * 打印健康报告
   */
  private void printHealthReport() {
    JOptionPane.showMessageDialog(
        this,
        "打印功能正在开发中...\n" +
            "将支持生成标准体检报告并打印",
        "功能提示",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * 获取ViewModel
   */
  public UserHealthDataViewModel getViewModel() {
    return viewModel;
  }

  /**
   * 进入管理员录入模式
   * 用于管理员为指定预约录入体检数据
   * 
   * @param appointment 要录入数据的预约
   */
  public void enterBatchEntryMode(com.healthsys.model.entity.Appointment appointment) {
    this.isAdminEntryMode = true;
    this.currentAppointment = appointment;

    // 切换工具栏到管理员录入模式
    String appointmentInfo = "预约ID: " + appointment.getAppointmentId() +
        " | 检查组ID: " + appointment.getGroupId();
    toolbarComponent.enterAdminEntryMode(appointmentInfo);

    logger.info("进入管理员录入模式: {}", appointmentInfo);
  }

  /**
   * 显示添加数据对话框
   */
  private void showAddDataDialog() {
    if (currentAppointment == null) {
      JOptionPane.showMessageDialog(this,
          "当前没有关联的预约信息",
          "错误",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    // 创建单项数据录入对话框
    Frame parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
    SingleExaminationDataEntryDialog dialog = new SingleExaminationDataEntryDialog(
        parentFrame, currentAppointment);

    // 设置保存成功回调
    dialog.setOnSaveCallback(() -> {
      SwingUtilities.invokeLater(() -> {
        // 刷新数据
        viewModel.refreshData();

        JOptionPane.showMessageDialog(this,
            "体检数据添加成功！",
            "添加成功",
            JOptionPane.INFORMATION_MESSAGE);
      });
    });

    dialog.setVisible(true);
  }

  /**
   * 退出管理员录入模式
   */
  public void exitAdminEntryMode() {
    this.isAdminEntryMode = false;
    this.currentAppointment = null;

    // 退出工具栏管理员录入模式
    toolbarComponent.exitAdminEntryMode();

    logger.info("退出管理员录入模式");
  }
}