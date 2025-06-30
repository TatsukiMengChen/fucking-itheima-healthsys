package com.healthsys.view.user.healthdata.component;

import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.viewmodel.user.healthdata.BatchExaminationDataEntryViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 批量检查项数据录入组件
 * 显示检查组包含的所有检查项，允许批量录入体检数据
 * 
 * @author 梦辰
 */
public class BatchExaminationDataEntryComponent extends JPanel {

  private BatchExaminationDataEntryViewModel viewModel;
  private Appointment appointment;

  // UI组件
  private JPanel headerPanel;
  private JPanel contentPanel;
  private JScrollPane scrollPane;
  private JPanel buttonPanel;

  // 按钮
  private JButton saveButton;
  private JButton cancelButton;
  private JButton backButton;

  // 检查项录入行组件列表
  private List<ExaminationItemInputRowComponent> inputRowComponents;

  // 回调接口
  private Runnable onSaveCallback;
  private Runnable onCancelCallback;

  public BatchExaminationDataEntryComponent(Appointment appointment) {
    this.appointment = appointment;
    this.viewModel = new BatchExaminationDataEntryViewModel(appointment);
    this.inputRowComponents = new ArrayList<>();

    initializeComponents();
    setupLayout();
    bindEvents();

    // 加载检查项数据
    viewModel.loadCheckItemsCommand();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 创建头部信息面板
    createHeaderPanel();

    // 创建内容面板
    createContentPanel();

    // 创建按钮面板
    createButtonPanel();
  }

  /**
   * 创建头部信息面板
   */
  private void createHeaderPanel() {
    headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(BorderFactory.createTitledBorder("预约信息"));
    headerPanel.setPreferredSize(new Dimension(0, 100));

    // 创建预约信息显示
    JPanel infoPanel = new JPanel(new GridLayout(2, 3, 10, 5));
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    infoPanel.add(new JLabel("预约ID: " + appointment.getAppointmentId()));
    infoPanel.add(new JLabel("预约日期: " + appointment.getAppointmentDate()));
    infoPanel.add(new JLabel("状态: " + appointment.getStatus()));
    infoPanel.add(new JLabel("用户ID: " + appointment.getUserId()));
    infoPanel.add(new JLabel("检查组ID: " + appointment.getGroupId()));
    infoPanel.add(new JLabel("体检方式: " + appointment.getExaminationMethod()));

    headerPanel.add(infoPanel, BorderLayout.CENTER);
  }

  /**
   * 创建内容面板
   */
  private void createContentPanel() {
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 创建滚动面板
    scrollPane = new JScrollPane(contentPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
  }

  /**
   * 创建按钮面板
   */
  private void createButtonPanel() {
    buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    backButton = new JButton("返回");
    backButton.setPreferredSize(new Dimension(80, 35));

    cancelButton = new JButton("取消");
    cancelButton.setPreferredSize(new Dimension(80, 35));

    saveButton = new JButton("保存数据");
    saveButton.setPreferredSize(new Dimension(100, 35));
    saveButton.setBackground(new Color(40, 167, 69));
    saveButton.setForeground(Color.WHITE);

    buttonPanel.add(backButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(headerPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 保存按钮事件
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveExaminationData();
      }
    });

    // 取消按钮事件
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (onCancelCallback != null) {
          onCancelCallback.run();
        }
      }
    });

    // 返回按钮事件
    backButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (onCancelCallback != null) {
          onCancelCallback.run();
        }
      }
    });

    // 监听ViewModel的检查项数据变化
    viewModel.addPropertyChangeListener("checkItems", evt -> {
      SwingUtilities.invokeLater(() -> updateCheckItemsDisplay());
    });

    // 监听加载状态变化
    viewModel.addPropertyChangeListener("loading", evt -> {
      boolean loading = (Boolean) evt.getNewValue();
      SwingUtilities.invokeLater(() -> {
        saveButton.setEnabled(!loading);
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
      });
    });
  }

  /**
   * 更新检查项显示
   */
  private void updateCheckItemsDisplay() {
    // 清空现有组件
    contentPanel.removeAll();
    inputRowComponents.clear();

    List<CheckItem> checkItems = viewModel.getCheckItems();
    if (checkItems == null || checkItems.isEmpty()) {
      JLabel noDataLabel = new JLabel("暂无检查项数据", JLabel.CENTER);
      noDataLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
      noDataLabel.setForeground(Color.GRAY);
      contentPanel.add(noDataLabel);
    } else {
      // 添加表头
      JPanel headerRow = createTableHeader();
      contentPanel.add(headerRow);
      contentPanel.add(Box.createVerticalStrut(10));

      // 为每个检查项创建录入行
      for (CheckItem checkItem : checkItems) {
        ExaminationItemInputRowComponent rowComponent = new ExaminationItemInputRowComponent(checkItem);
        inputRowComponents.add(rowComponent);
        contentPanel.add(rowComponent);
        contentPanel.add(Box.createVerticalStrut(5));
      }
    }

    // 重新验证和绘制
    contentPanel.revalidate();
    contentPanel.repaint();
  }

  /**
   * 创建表头
   */
  private JPanel createTableHeader() {
    JPanel header = new JPanel(new GridLayout(1, 5, 10, 0));
    header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    header.setBackground(new Color(248, 249, 250));

    JLabel nameLabel = new JLabel("检查项名称");
    nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

    JLabel valueLabel = new JLabel("测量值");
    valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

    JLabel referenceLabel = new JLabel("参考值");
    referenceLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

    JLabel unitLabel = new JLabel("单位");
    unitLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

    JLabel notesLabel = new JLabel("备注");
    notesLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

    header.add(nameLabel);
    header.add(valueLabel);
    header.add(referenceLabel);
    header.add(unitLabel);
    header.add(notesLabel);

    return header;
  }

  /**
   * 保存体检数据
   */
  private void saveExaminationData() {
    // 验证输入数据
    if (!validateInputData()) {
      return;
    }

    // 收集所有录入的数据
    List<ExaminationResult> results = collectExaminationResults();

    if (results.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "请至少录入一项体检数据",
          "提示",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    // 确认保存
    int option = JOptionPane.showConfirmDialog(this,
        "确定要保存 " + results.size() + " 项体检数据吗？",
        "确认保存",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (option == JOptionPane.YES_OPTION) {
      // 调用ViewModel保存数据
      viewModel.saveExaminationResultsCommand(results).thenRun(() -> {
        SwingUtilities.invokeLater(() -> {
          JOptionPane.showMessageDialog(this,
              "体检数据保存成功！",
              "保存成功",
              JOptionPane.INFORMATION_MESSAGE);

          if (onSaveCallback != null) {
            onSaveCallback.run();
          }
        });
      }).exceptionally(throwable -> {
        SwingUtilities.invokeLater(() -> {
          JOptionPane.showMessageDialog(this,
              "保存失败：" + throwable.getMessage(),
              "保存失败",
              JOptionPane.ERROR_MESSAGE);
        });
        return null;
      });
    }
  }

  /**
   * 验证输入数据
   */
  private boolean validateInputData() {
    for (ExaminationItemInputRowComponent rowComponent : inputRowComponents) {
      if (!rowComponent.validateInput()) {
        return false;
      }
    }
    return true;
  }

  /**
   * 收集体检结果数据
   */
  private List<ExaminationResult> collectExaminationResults() {
    List<ExaminationResult> results = new ArrayList<>();

    for (ExaminationItemInputRowComponent rowComponent : inputRowComponents) {
      ExaminationResult result = rowComponent.getExaminationResult();
      if (result != null && result.getMeasuredValue() != null && !result.getMeasuredValue().trim().isEmpty()) {
        // 设置预约和用户信息
        result.setAppointmentId(appointment.getAppointmentId());
        result.setUserId(appointment.getUserId());
        results.add(result);
      }
    }

    return results;
  }

  /**
   * 设置保存回调
   */
  public void setOnSaveCallback(Runnable callback) {
    this.onSaveCallback = callback;
  }

  /**
   * 设置取消回调
   */
  public void setOnCancelCallback(Runnable callback) {
    this.onCancelCallback = callback;
  }

  /**
   * 获取ViewModel
   */
  public BatchExaminationDataEntryViewModel getViewModel() {
    return viewModel;
  }
}