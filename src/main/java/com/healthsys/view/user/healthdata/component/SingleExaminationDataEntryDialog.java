package com.healthsys.view.user.healthdata.component;

import com.healthsys.model.entity.Appointment;
import com.healthsys.model.entity.CheckItem;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.service.ICheckGroupService;
import com.healthsys.service.IExaminationResultService;
import com.healthsys.service.impl.CheckGroupServiceImpl;
import com.healthsys.service.impl.ExaminationResultServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 单项体检数据录入对话框
 * 允许管理员选择当前预约检查组中的检查项并录入单条数据记录
 * 
 * @author AI Assistant
 */
public class SingleExaminationDataEntryDialog extends JDialog {

  private final Appointment appointment;
  private final ICheckGroupService checkGroupService;
  private final IExaminationResultService examinationResultService;

  // UI组件
  private JPanel contentPanel;
  private JComboBox<CheckItemWrapper> checkItemComboBox;
  private JTextField valueField;
  private JLabel referenceLabel;
  private JLabel unitLabel;
  private JTextArea notesArea;
  private JButton saveButton;
  private JButton cancelButton;

  // 数据
  private List<CheckItem> availableCheckItems;

  // 回调
  private Runnable onSaveCallback;

  public SingleExaminationDataEntryDialog(Frame parent, Appointment appointment) {
    super(parent, "录入体检数据", true);
    this.appointment = appointment;
    this.checkGroupService = new CheckGroupServiceImpl();
    this.examinationResultService = new ExaminationResultServiceImpl();

    initializeComponents();
    setupLayout();
    bindEvents();
    loadCheckItems();

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setSize(500, 400);
    setLocationRelativeTo(parent);
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    contentPanel = new JPanel();

    // 检查项选择下拉框
    checkItemComboBox = new JComboBox<>();
    checkItemComboBox.setPreferredSize(new Dimension(300, 30));

    // 测量值输入框
    valueField = new JTextField();
    valueField.setPreferredSize(new Dimension(300, 30));

    // 参考值标签
    referenceLabel = new JLabel("-");
    referenceLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    referenceLabel.setForeground(Color.GRAY);

    // 单位标签
    unitLabel = new JLabel("-");
    unitLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    unitLabel.setForeground(Color.GRAY);

    // 备注文本域
    notesArea = new JTextArea(3, 20);
    notesArea.setLineWrap(true);
    notesArea.setWrapStyleWord(true);
    notesArea.setBorder(BorderFactory.createLoweredBevelBorder());

    // 按钮
    saveButton = new JButton("保存");
    saveButton.setPreferredSize(new Dimension(80, 35));
    saveButton.setBackground(new Color(40, 167, 69));
    saveButton.setForeground(Color.WHITE);

    cancelButton = new JButton("取消");
    cancelButton.setPreferredSize(new Dimension(80, 35));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());

    // 创建主面板
    contentPanel.setLayout(new GridBagLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 预约信息
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    JPanel infoPanel = createAppointmentInfoPanel();
    contentPanel.add(infoPanel, gbc);

    gbc.gridwidth = 1;

    // 检查项选择
    gbc.gridx = 0;
    gbc.gridy = 1;
    contentPanel.add(new JLabel("检查项目:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(checkItemComboBox, gbc);

    // 测量值
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    contentPanel.add(new JLabel("测量值:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add(valueField, gbc);

    // 参考值
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.NONE;
    contentPanel.add(new JLabel("参考值:"), gbc);
    gbc.gridx = 1;
    contentPanel.add(referenceLabel, gbc);

    // 单位
    gbc.gridx = 0;
    gbc.gridy = 4;
    contentPanel.add(new JLabel("单位:"), gbc);
    gbc.gridx = 1;
    contentPanel.add(unitLabel, gbc);

    // 备注
    gbc.gridx = 0;
    gbc.gridy = 5;
    contentPanel.add(new JLabel("备注:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPanel.add(new JScrollPane(notesArea), gbc);

    add(contentPanel, BorderLayout.CENTER);

    // 按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建预约信息面板
   */
  private JPanel createAppointmentInfoPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 5));
    panel.setBorder(BorderFactory.createTitledBorder("预约信息"));
    panel.setPreferredSize(new Dimension(400, 80));

    panel.add(new JLabel("预约ID: " + appointment.getAppointmentId()));
    panel.add(new JLabel("预约日期: " + appointment.getAppointmentDate()));
    panel.add(new JLabel("用户ID: " + appointment.getUserId()));
    panel.add(new JLabel("检查组ID: " + appointment.getGroupId()));

    return panel;
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 检查项选择变化事件
    checkItemComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateReferenceInfo();
      }
    });

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
        dispose();
      }
    });

    // 回车键保存
    valueField.addActionListener(e -> saveExaminationData());
  }

  /**
   * 加载检查项
   */
  private void loadCheckItems() {
    try {
      // 获取检查组包含的检查项
      availableCheckItems = checkGroupService.getCheckItemsByGroupId(appointment.getGroupId());

      // 清空并填充下拉框
      checkItemComboBox.removeAllItems();

      if (availableCheckItems != null && !availableCheckItems.isEmpty()) {
        for (CheckItem item : availableCheckItems) {
          checkItemComboBox.addItem(new CheckItemWrapper(item));
        }

        // 更新参考信息
        updateReferenceInfo();
      } else {
        JOptionPane.showMessageDialog(this,
            "该检查组暂无可用的检查项",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "加载检查项失败：" + e.getMessage(),
          "错误",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * 更新参考信息
   */
  private void updateReferenceInfo() {
    CheckItemWrapper selectedWrapper = (CheckItemWrapper) checkItemComboBox.getSelectedItem();
    if (selectedWrapper != null) {
      CheckItem item = selectedWrapper.getCheckItem();
      referenceLabel.setText(item.getReferenceVal() != null ? item.getReferenceVal() : "-");
      unitLabel.setText(item.getUnit() != null ? item.getUnit() : "-");
    } else {
      referenceLabel.setText("-");
      unitLabel.setText("-");
    }
  }

  /**
   * 保存体检数据
   */
  private void saveExaminationData() {
    // 验证输入
    if (!validateInput()) {
      return;
    }

    try {
      // 获取选中的检查项
      CheckItemWrapper selectedWrapper = (CheckItemWrapper) checkItemComboBox.getSelectedItem();
      CheckItem selectedItem = selectedWrapper.getCheckItem();

      // 创建体检结果
      ExaminationResult result = new ExaminationResult();
      result.setAppointmentId(appointment.getAppointmentId());
      result.setUserId(appointment.getUserId());
      result.setGroupId(appointment.getGroupId()); // 重要：设置检查组ID
      result.setItemId(selectedItem.getItemId());
      result.setMeasuredValue(valueField.getText().trim());
      result.setResultNotes(notesArea.getText().trim());
      result.setRecordedAt(LocalDateTime.now());

      // 保存数据
      boolean success = examinationResultService.addExaminationResult(result);

      if (success) {
        // 保存成功
        if (onSaveCallback != null) {
          onSaveCallback.run();
        }
        dispose();
      } else {
        JOptionPane.showMessageDialog(this,
            "保存体检数据失败",
            "保存失败",
            JOptionPane.ERROR_MESSAGE);
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "保存数据时发生错误：" + e.getMessage(),
          "错误",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * 验证输入
   */
  private boolean validateInput() {
    // 检查是否选择了检查项
    if (checkItemComboBox.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this,
          "请选择要录入数据的检查项",
          "输入验证",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }

    // 检查是否输入了测量值
    String value = valueField.getText().trim();
    if (value.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "请输入测量值",
          "输入验证",
          JOptionPane.WARNING_MESSAGE);
      valueField.requestFocus();
      return false;
    }

    // 检查测量值长度
    if (value.length() > 255) {
      JOptionPane.showMessageDialog(this,
          "测量值不能超过255个字符",
          "输入验证",
          JOptionPane.WARNING_MESSAGE);
      valueField.requestFocus();
      return false;
    }

    // 检查备注长度
    String notes = notesArea.getText().trim();
    if (notes.length() > 1000) {
      JOptionPane.showMessageDialog(this,
          "备注不能超过1000个字符",
          "输入验证",
          JOptionPane.WARNING_MESSAGE);
      notesArea.requestFocus();
      return false;
    }

    return true;
  }

  /**
   * 设置保存成功回调
   */
  public void setOnSaveCallback(Runnable callback) {
    this.onSaveCallback = callback;
  }

  /**
   * 检查项包装类，用于下拉框显示
   */
  private static class CheckItemWrapper {
    private final CheckItem checkItem;

    public CheckItemWrapper(CheckItem checkItem) {
      this.checkItem = checkItem;
    }

    public CheckItem getCheckItem() {
      return checkItem;
    }

    @Override
    public String toString() {
      return checkItem.getItemName();
    }
  }
}