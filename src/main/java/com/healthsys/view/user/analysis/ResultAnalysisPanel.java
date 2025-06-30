package com.healthsys.view.user.analysis;

import com.healthsys.view.base.BasePanel;
import com.healthsys.viewmodel.user.analysis.ResultAnalysisViewModel;
import com.healthsys.model.entity.Appointment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 体检结果分析面板。
 * 展示用户体检结果的分析信息。
 * 
 * @author 梦辰
 */
public class ResultAnalysisPanel extends BasePanel {

  private ResultAnalysisViewModel viewModel;

  // UI组件
  private JComboBox<AppointmentItem> appointmentComboBox;
  private JButton analyzeButton;
  private JTextArea resultDisplayArea;
  private JTextArea analysisArea;
  private JTextArea suggestionArea;
  private JLabel healthScoreLabel;
  private JLabel riskLevelLabel;
  private JLabel statusLabel;

  public ResultAnalysisPanel() {
    initializeViewModel();
    initializeComponents();
    setupLayout();
    bindEvents();

    // 初始化数据
    viewModel.loadUserAppointments();
  }

  /**
   * 初始化ViewModel
   */
  private void initializeViewModel() {
    this.viewModel = new ResultAnalysisViewModel();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 预约选择下拉框
    appointmentComboBox = new JComboBox<>();
    appointmentComboBox.setPreferredSize(new Dimension(300, 30));

    // 分析按钮
    analyzeButton = new JButton("分析结果");
    analyzeButton.setPreferredSize(new Dimension(100, 30));
    analyzeButton.setEnabled(false);

    // 结果显示区域
    resultDisplayArea = new JTextArea();
    resultDisplayArea.setEditable(false);
    resultDisplayArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    resultDisplayArea.setBorder(BorderFactory.createTitledBorder("体检结果详情"));

    // 分析区域
    analysisArea = new JTextArea();
    analysisArea.setEditable(false);
    analysisArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    analysisArea.setBorder(BorderFactory.createTitledBorder("结果分析"));

    // 建议区域
    suggestionArea = new JTextArea();
    suggestionArea.setEditable(false);
    suggestionArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    suggestionArea.setBorder(BorderFactory.createTitledBorder("健康建议"));

    // 健康评分标签
    healthScoreLabel = new JLabel("健康评分: --");
    healthScoreLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

    // 风险等级标签
    riskLevelLabel = new JLabel("风险等级: --");
    riskLevelLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

    // 状态标签
    statusLabel = new JLabel("就绪");
    statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
    statusLabel.setForeground(Color.GRAY);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 创建顶部控制面板
    JPanel controlPanel = createControlPanel();
    add(controlPanel, BorderLayout.NORTH);

    // 创建主内容面板
    JPanel contentPanel = createContentPanel();
    add(contentPanel, BorderLayout.CENTER);

    // 创建底部状态面板
    JPanel statusPanel = createStatusPanel();
    add(statusPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建控制面板
   */
  private JPanel createControlPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

    // 标题
    JLabel titleLabel = new JLabel("体检结果分析");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
    titleLabel.setForeground(new Color(51, 51, 51));

    panel.add(titleLabel);
    panel.add(Box.createHorizontalStrut(30));

    // 预约选择
    panel.add(new JLabel("选择预约:"));
    panel.add(appointmentComboBox);
    panel.add(Box.createHorizontalStrut(10));
    panel.add(analyzeButton);

    return panel;
  }

  /**
   * 创建内容面板
   */
  private JPanel createContentPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // 创建分割面板
    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setDividerLocation(400);

    // 左侧：结果显示和评分
    JPanel leftPanel = createLeftPanel();
    mainSplitPane.setLeftComponent(leftPanel);

    // 右侧：分析和建议
    JPanel rightPanel = createRightPanel();
    mainSplitPane.setRightComponent(rightPanel);

    panel.add(mainSplitPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * 创建左侧面板
   */
  private JPanel createLeftPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // 健康评分面板
    JPanel scorePanel = createScorePanel();
    panel.add(scorePanel, BorderLayout.NORTH);

    // 结果显示区域
    JScrollPane resultScrollPane = new JScrollPane(resultDisplayArea);
    resultScrollPane.setPreferredSize(new Dimension(380, 300));
    panel.add(resultScrollPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * 创建右侧面板
   */
  private JPanel createRightPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    // 创建分析和建议的分割面板
    JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    rightSplitPane.setDividerLocation(200);

    // 分析区域
    JScrollPane analysisScrollPane = new JScrollPane(analysisArea);
    rightSplitPane.setTopComponent(analysisScrollPane);

    // 建议区域
    JScrollPane suggestionScrollPane = new JScrollPane(suggestionArea);
    rightSplitPane.setBottomComponent(suggestionScrollPane);

    panel.add(rightSplitPane, BorderLayout.CENTER);

    return panel;
  }

  /**
   * 创建评分面板
   */
  private JPanel createScorePanel() {
    JPanel panel = new JPanel(new GridLayout(2, 1));
    panel.setBorder(BorderFactory.createTitledBorder("健康评估"));
    panel.setPreferredSize(new Dimension(380, 80));

    panel.add(healthScoreLabel);
    panel.add(riskLevelLabel);

    return panel;
  }

  /**
   * 创建状态面板
   */
  private JPanel createStatusPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

    panel.add(statusLabel);

    return panel;
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 分析按钮事件
    analyzeButton.addActionListener(e -> {
      AppointmentItem selectedItem = (AppointmentItem) appointmentComboBox.getSelectedItem();
      if (selectedItem != null) {
        viewModel.loadResultDetailCommand(selectedItem.getAppointment().getAppointmentId());
      }
    });

    // 预约选择事件
    appointmentComboBox.addActionListener(e -> {
      analyzeButton.setEnabled(appointmentComboBox.getSelectedItem() != null);
    });

    // 监听ViewModel数据变化
    bindViewModelEvents();
  }

  /**
   * 绑定ViewModel事件
   */
  private void bindViewModelEvents() {
    // 监听预约列表变化
    viewModel.addPropertyChangeListener("userAppointments", evt -> {
      SwingUtilities.invokeLater(() -> {
        updateAppointmentComboBox();
      });
    });

    // 监听分析结果变化
    viewModel.addPropertyChangeListener("analysisText", evt -> {
      SwingUtilities.invokeLater(() -> {
        analysisArea.setText((String) evt.getNewValue());
      });
    });

    // 监听建议变化
    viewModel.addPropertyChangeListener("suggestionText", evt -> {
      SwingUtilities.invokeLater(() -> {
        suggestionArea.setText((String) evt.getNewValue());
      });
    });

    // 监听健康评分变化
    viewModel.addPropertyChangeListener("healthScore", evt -> {
      SwingUtilities.invokeLater(() -> {
        String score = (String) evt.getNewValue();
        healthScoreLabel.setText("健康评分: " + (score != null ? score : "--"));
      });
    });

    // 监听风险等级变化
    viewModel.addPropertyChangeListener("riskLevel", evt -> {
      SwingUtilities.invokeLater(() -> {
        String risk = (String) evt.getNewValue();
        riskLevelLabel.setText("风险等级: " + (risk != null ? risk : "--"));

        // 根据风险等级设置颜色
        if (risk != null) {
          if (risk.contains("低风险")) {
            riskLevelLabel.setForeground(Color.GREEN);
          } else if (risk.contains("中等风险")) {
            riskLevelLabel.setForeground(Color.ORANGE);
          } else if (risk.contains("高风险")) {
            riskLevelLabel.setForeground(Color.RED);
          }
        }
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
        analyzeButton.setEnabled(!isLoading && appointmentComboBox.getSelectedItem() != null);
        setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
      });
    });
  }

  /**
   * 更新预约下拉框
   */
  private void updateAppointmentComboBox() {
    appointmentComboBox.removeAllItems();

    List<Appointment> appointments = viewModel.getUserAppointments();
    if (appointments != null) {
      for (Appointment appointment : appointments) {
        appointmentComboBox.addItem(new AppointmentItem(appointment));
      }
    }

    analyzeButton.setEnabled(appointmentComboBox.getItemCount() > 0);
  }

  /**
   * 预约下拉框显示项
   */
  private static class AppointmentItem {
    private final Appointment appointment;

    public AppointmentItem(Appointment appointment) {
      this.appointment = appointment;
    }

    public Appointment getAppointment() {
      return appointment;
    }

    @Override
    public String toString() {
      return String.format("预约%d - %s - 检查组%d",
          appointment.getAppointmentId(),
          appointment.getAppointmentDate(),
          appointment.getGroupId());
    }
  }

  /**
   * 获取ViewModel
   */
  public ResultAnalysisViewModel getViewModel() {
    return viewModel;
  }
}