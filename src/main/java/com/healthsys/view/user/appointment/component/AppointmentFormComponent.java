package com.healthsys.view.user.appointment.component;

import com.healthsys.viewmodel.user.appointment.AppointmentViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 预约表单组件
 * 包含检查组下拉框、日期选择器等，用于提交新预约
 * 
 * @author AI Assistant
 */
public class AppointmentFormComponent extends JPanel {

  private AppointmentViewModel viewModel;

  // 表单组件
  private JComboBox<String> checkGroupComboBox;
  private JTextField dateField;
  private JTextField timeField;
  private JComboBox<String> methodComboBox;
  private JButton submitButton;
  private JButton resetButton;

  public AppointmentFormComponent(AppointmentViewModel viewModel) {
    this.viewModel = viewModel;
    initializeComponents();
    setupLayout();
    bindEvents();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 检查组选择
    checkGroupComboBox = new JComboBox<>();
    checkGroupComboBox.setPreferredSize(new Dimension(200, 30));

    // 日期输入
    dateField = new JTextField();
    dateField.setPreferredSize(new Dimension(150, 30));
    dateField.setToolTipText("格式：yyyy-MM-dd");

    // 时间输入
    timeField = new JTextField();
    timeField.setPreferredSize(new Dimension(100, 30));
    timeField.setToolTipText("格式：HH:mm");

    // 体检方式选择
    methodComboBox = new JComboBox<>(viewModel.getExaminationMethods());
    methodComboBox.setPreferredSize(new Dimension(120, 30));

    // 按钮
    submitButton = new JButton("提交预约");
    submitButton.setPreferredSize(new Dimension(100, 35));

    resetButton = new JButton("重置");
    resetButton.setPreferredSize(new Dimension(80, 35));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    // 检查组选择
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(new JLabel("检查组:"), gbc);
    gbc.gridx = 1;
    add(checkGroupComboBox, gbc);

    // 预约日期
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(new JLabel("预约日期:"), gbc);
    gbc.gridx = 1;
    add(dateField, gbc);

    // 预约时间
    gbc.gridx = 0;
    gbc.gridy = 2;
    add(new JLabel("预约时间:"), gbc);
    gbc.gridx = 1;
    add(timeField, gbc);

    // 体检方式
    gbc.gridx = 0;
    gbc.gridy = 3;
    add(new JLabel("体检方式:"), gbc);
    gbc.gridx = 1;
    add(methodComboBox, gbc);

    // 按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.add(submitButton);
    buttonPanel.add(resetButton);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(buttonPanel, gbc);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 提交按钮事件
    submitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        submitAppointment();
      }
    });

    // 重置按钮事件
    resetButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resetForm();
      }
    });
  }

  /**
   * 提交预约
   */
  private void submitAppointment() {
    // 获取表单数据并调用ViewModel提交
    String selectedGroup = (String) checkGroupComboBox.getSelectedItem();
    String date = dateField.getText().trim();
    String time = timeField.getText().trim();
    String method = (String) methodComboBox.getSelectedItem();

    // 简单验证
    if (selectedGroup == null || date.isEmpty() || time.isEmpty()) {
      JOptionPane.showMessageDialog(this, "请填写完整的预约信息", "提示", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // TODO: 设置ViewModel数据并提交
    viewModel.submitAppointmentCommand();
  }

  /**
   * 重置表单
   */
  private void resetForm() {
    checkGroupComboBox.setSelectedIndex(0);
    dateField.setText("");
    timeField.setText("");
    methodComboBox.setSelectedIndex(0);
  }
}