package com.healthsys.view.user.healthdata.component;

import com.healthsys.model.entity.CheckItem;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * 检查项数据录入行组件
 * 显示单个检查项的录入界面：检查项名称、测量值输入、参考值显示、单位显示、备注输入
 * 
 * @author 梦辰
 */
public class ExaminationItemInputRowComponent extends JPanel {

  private CheckItem checkItem;

  // UI组件
  private JLabel nameLabel;
  private JTextField valueField;
  private JLabel referenceLabel;
  private JLabel unitLabel;
  private JTextField notesField;

  // 验证状态
  private boolean isValid = true;

  public ExaminationItemInputRowComponent(CheckItem checkItem) {
    this.checkItem = checkItem;

    initializeComponents();
    setupLayout();
    bindEvents();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 检查项名称标签
    nameLabel = new JLabel(checkItem.getItemName());
    nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    nameLabel.setPreferredSize(new Dimension(150, 30));

    // 测量值输入框
    valueField = new JTextField();
    valueField.setPreferredSize(new Dimension(120, 30));
    valueField.setToolTipText("请输入测量值");

    // 参考值标签
    referenceLabel = new JLabel(checkItem.getReferenceVal() != null ? checkItem.getReferenceVal() : "-");
    referenceLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    referenceLabel.setForeground(Color.GRAY);
    referenceLabel.setPreferredSize(new Dimension(120, 30));

    // 单位标签
    unitLabel = new JLabel(checkItem.getUnit() != null ? checkItem.getUnit() : "-");
    unitLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    unitLabel.setForeground(Color.GRAY);
    unitLabel.setPreferredSize(new Dimension(80, 30));

    // 备注输入框
    notesField = new JTextField();
    notesField.setPreferredSize(new Dimension(150, 30));
    notesField.setToolTipText("可选：添加备注信息");
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new GridLayout(1, 5, 10, 0));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    setBackground(Color.WHITE);

    add(nameLabel);
    add(valueField);
    add(referenceLabel);
    add(unitLabel);
    add(notesField);

    // 设置最大高度
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 测量值输入验证
    valueField.addFocusListener(new java.awt.event.FocusAdapter() {
      @Override
      public void focusLost(java.awt.event.FocusEvent e) {
        validateInput();
      }
    });

    // 实时验证
    valueField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        SwingUtilities.invokeLater(() -> validateInput());
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        SwingUtilities.invokeLater(() -> validateInput());
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        SwingUtilities.invokeLater(() -> validateInput());
      }
    });
  }

  /**
   * 验证输入
   */
  public boolean validateInput() {
    String value = valueField.getText().trim();

    // 如果没有输入值，则认为是有效的（可选填）
    if (value.isEmpty()) {
      clearValidationError();
      isValid = true;
      return true;
    }

    // 验证数值格式（如果看起来像数字）
    if (value.matches("^\\d*\\.?\\d*$") && !value.equals(".")) {
      try {
        Double.parseDouble(value);
        clearValidationError();
        isValid = true;
        return true;
      } catch (NumberFormatException e) {
        showValidationError("请输入有效的数值");
        isValid = false;
        return false;
      }
    }

    // 对于非数值类型，只要不为空即可
    if (value.length() > 255) {
      showValidationError("测量值不能超过255个字符");
      isValid = false;
      return false;
    }

    clearValidationError();
    isValid = true;
    return true;
  }

  /**
   * 显示验证错误
   */
  private void showValidationError(String message) {
    valueField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    valueField.setToolTipText(message);
    setBackground(new Color(255, 245, 245));
  }

  /**
   * 清除验证错误
   */
  private void clearValidationError() {
    valueField.setBorder(UIManager.getBorder("TextField.border"));
    valueField.setToolTipText("请输入测量值");
    setBackground(Color.WHITE);
  }

  /**
   * 获取体检结果对象
   */
  public ExaminationResult getExaminationResult() {
    String value = valueField.getText().trim();

    // 如果没有输入值，返回null
    if (value.isEmpty()) {
      return null;
    }

    ExaminationResult result = new ExaminationResult();
    result.setItemId(checkItem.getItemId());
    result.setMeasuredValue(value);
    result.setResultNotes(notesField.getText().trim());
    result.setRecordedAt(LocalDateTime.now());

    return result;
  }

  /**
   * 设置测量值
   */
  public void setValue(String value) {
    valueField.setText(value);
    validateInput();
  }

  /**
   * 获取测量值
   */
  public String getValue() {
    return valueField.getText().trim();
  }

  /**
   * 设置备注
   */
  public void setNotes(String notes) {
    notesField.setText(notes);
  }

  /**
   * 获取备注
   */
  public String getNotes() {
    return notesField.getText().trim();
  }

  /**
   * 获取检查项
   */
  public CheckItem getCheckItem() {
    return checkItem;
  }

  /**
   * 是否有效
   */
  public boolean isValid() {
    return isValid;
  }

  /**
   * 是否有输入值
   */
  public boolean hasValue() {
    return !valueField.getText().trim().isEmpty();
  }

  /**
   * 清空输入
   */
  public void clearInput() {
    valueField.setText("");
    notesField.setText("");
    clearValidationError();
    isValid = true;
  }

  /**
   * 设置是否可编辑
   */
  public void setEditable(boolean editable) {
    valueField.setEditable(editable);
    notesField.setEditable(editable);

    if (!editable) {
      valueField.setBackground(new Color(245, 245, 245));
      notesField.setBackground(new Color(245, 245, 245));
    } else {
      valueField.setBackground(Color.WHITE);
      notesField.setBackground(Color.WHITE);
    }
  }
}