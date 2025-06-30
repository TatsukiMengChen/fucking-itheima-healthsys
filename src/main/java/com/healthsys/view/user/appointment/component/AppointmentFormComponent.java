package com.healthsys.view.user.appointment.component;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.healthsys.model.entity.CheckGroup;
import com.healthsys.viewmodel.user.appointment.AppointmentViewModel;

/**
 * 预约表单组件
 * 包含检查组下拉框、日期选择器等，用于提交新预约
 * 
 * @author AI Assistant
 */
public class AppointmentFormComponent extends JPanel implements PropertyChangeListener {

  private AppointmentViewModel viewModel;

  // 表单组件
  private JComboBox<CheckGroupItem> checkGroupComboBox;
  private JTextField dateField;
  private JTextField timeField;
  private JComboBox<String> methodComboBox;
  private JButton submitButton;
  private JButton resetButton;

  // 内部类：检查组下拉框项
  private static class CheckGroupItem {
    private final CheckGroup checkGroup;

    public CheckGroupItem(CheckGroup checkGroup) {
      this.checkGroup = checkGroup;
    }

    public CheckGroup getCheckGroup() {
      return checkGroup;
    }

    @Override
    public String toString() {
      return checkGroup.getGroupName() + " (" + checkGroup.getGroupCode() + ")";
    }
  }

  public AppointmentFormComponent(AppointmentViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);
    initializeComponents();
    setupLayout();
    bindEvents();
    updateFromViewModel();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 检查组选择
    checkGroupComboBox = new JComboBox<>();
    checkGroupComboBox.setPreferredSize(new Dimension(250, 30));

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
    // 检查组选择事件
    checkGroupComboBox.addActionListener(e -> {
      CheckGroupItem selectedItem = (CheckGroupItem) checkGroupComboBox.getSelectedItem();
      if (selectedItem != null) {
        CheckGroup checkGroup = selectedItem.getCheckGroup();
        viewModel.setSelectedCheckGroupId(checkGroup.getGroupId());
        viewModel.setSelectedCheckGroupName(checkGroup.getGroupName());
      }
    });

    // 日期输入事件
    dateField.addActionListener(e -> updateDateFromField());
    dateField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        updateDateFromField();
      }
    });

    // 时间输入事件
    timeField.addActionListener(e -> updateTimeFromField());
    timeField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        updateTimeFromField();
      }
    });

    // 体检方式选择事件
    methodComboBox.addActionListener(e -> {
      String selectedMethod = (String) methodComboBox.getSelectedItem();
      if (selectedMethod != null) {
        viewModel.setExaminationMethod(selectedMethod);
      }
    });

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
   * 从日期字段更新ViewModel
   */
  private void updateDateFromField() {
    String dateText = dateField.getText().trim();
    if (!dateText.isEmpty()) {
      try {
        LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        viewModel.setAppointmentDate(date);
      } catch (DateTimeParseException e) {
        // 日期格式错误，不更新
      }
    }
  }

  /**
   * 从时间字段更新ViewModel
   */
  private void updateTimeFromField() {
    String timeText = timeField.getText().trim();
    if (!timeText.isEmpty()) {
      try {
        LocalTime time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm"));
        viewModel.setAppointmentTime(time);
      } catch (DateTimeParseException e) {
        // 时间格式错误，不更新
      }
    }
  }

  /**
   * 提交预约
   */
  private void submitAppointment() {
    // 先更新ViewModel中的数据
    updateDateFromField();
    updateTimeFromField();

    // 验证基本信息
    if (checkGroupComboBox.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this, "请选择检查组", "提示", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (dateField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "请输入预约日期", "提示", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (timeField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "请输入预约时间", "提示", JOptionPane.WARNING_MESSAGE);
      return;
    }

    // 提交预约
    viewModel.submitAppointmentCommand().thenAccept(success -> {
      SwingUtilities.invokeLater(() -> {
        if (success) {
          JOptionPane.showMessageDialog(this, "预约提交成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
          resetForm();
        } else {
          JOptionPane.showMessageDialog(this, "预约提交失败：" + viewModel.getStatusMessage(),
              "失败", JOptionPane.ERROR_MESSAGE);
        }
      });
    });
  }

  /**
   * 重置表单
   */
  private void resetForm() {
    if (checkGroupComboBox.getItemCount() > 0) {
      checkGroupComboBox.setSelectedIndex(0);
    }
    dateField.setText("");
    timeField.setText("");
    if (methodComboBox.getItemCount() > 0) {
      methodComboBox.setSelectedIndex(0);
    }
  }

  /**
   * 更新检查组下拉框数据
   */
  private void updateCheckGroupComboBox() {
    List<CheckGroup> checkGroups = viewModel.getAvailableCheckGroups();
    DefaultComboBoxModel<CheckGroupItem> model = new DefaultComboBoxModel<>();

    if (checkGroups != null && !checkGroups.isEmpty()) {
      for (CheckGroup checkGroup : checkGroups) {
        model.addElement(new CheckGroupItem(checkGroup));
      }

      checkGroupComboBox.setModel(model);

      // 自动选择第一个检查组作为默认值
      if (model.getSize() > 0) {
        checkGroupComboBox.setSelectedIndex(0);
        CheckGroupItem firstItem = (CheckGroupItem) model.getElementAt(0);
        if (firstItem != null) {
          CheckGroup firstCheckGroup = firstItem.getCheckGroup();
          // 更新ViewModel中的选择状态
          viewModel.setSelectedCheckGroupId(firstCheckGroup.getGroupId());
          viewModel.setSelectedCheckGroupName(firstCheckGroup.getGroupName());
        }
      }
    } else {
      checkGroupComboBox.setModel(model);
      // 如果没有检查组，清空ViewModel中的选择
      viewModel.setSelectedCheckGroupId(null);
      viewModel.setSelectedCheckGroupName(null);
    }
  }

  /**
   * 从ViewModel更新UI
   */
  private void updateFromViewModel() {
    // 更新检查组下拉框
    updateCheckGroupComboBox();

    // 更新日期
    if (viewModel.getAppointmentDate() != null) {
      dateField.setText(viewModel.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    // 更新时间
    if (viewModel.getAppointmentTime() != null) {
      timeField.setText(viewModel.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    // 更新体检方式
    if (viewModel.getExaminationMethod() != null) {
      methodComboBox.setSelectedItem(viewModel.getExaminationMethod());
    }

    // 更新按钮状态
    submitButton.setEnabled(!viewModel.isLoading());
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();

      switch (propertyName) {
        case "availableCheckGroups":
          updateCheckGroupComboBox();
          break;
        case "appointmentDate":
          if (viewModel.getAppointmentDate() != null) {
            dateField.setText(viewModel.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
          }
          break;
        case "appointmentTime":
          if (viewModel.getAppointmentTime() != null) {
            timeField.setText(viewModel.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")));
          }
          break;
        case "examinationMethod":
          if (viewModel.getExaminationMethod() != null) {
            methodComboBox.setSelectedItem(viewModel.getExaminationMethod());
          }
          break;
        case "loading":
          submitButton.setEnabled(!viewModel.isLoading());
          break;
        case "appointmentSubmitted":
          // 预约提交成功后的处理已在submitAppointment方法中完成
          break;
      }
    });
  }
}