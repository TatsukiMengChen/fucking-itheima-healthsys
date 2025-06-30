package com.healthsys.view.admin.checkitem.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.healthsys.viewmodel.admin.checkitem.CheckItemEditViewModel;

/**
 * 检查项编辑表单组件
 * 用于输入检查项信息，与CheckItemEditViewModel绑定
 * 
 * @author HealthSys Team
 */
public class CheckItemEditFormComponent extends JPanel {

  private CheckItemEditViewModel viewModel;

  // 表单字段
  private JTextField itemCodeField;
  private JTextField itemNameField;
  private JTextField referenceValField;
  private JTextField unitField;

  // 错误标签
  private JLabel itemCodeErrorLabel;
  private JLabel itemNameErrorLabel;

  // 按钮
  private JButton submitButton;
  private JButton cancelButton;

  // 状态组件
  private JProgressBar progressBar;
  private JLabel statusLabel;

  /**
   * 构造函数
   */
  public CheckItemEditFormComponent(CheckItemEditViewModel viewModel) {
    this.viewModel = viewModel;
    initializeComponents();
    setupLayout();
    bindViewModel();
    setupEventListeners();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 表单字段
    itemCodeField = new JTextField(20);
    itemCodeField.setToolTipText("输入检查项代码，只能包含字母、数字和下划线");

    itemNameField = new JTextField(20);
    itemNameField.setToolTipText("输入检查项名称");

    referenceValField = new JTextField(20);
    referenceValField.setToolTipText("输入参考值（可选）");

    unitField = new JTextField(20);
    unitField.setToolTipText("输入单位（可选）");

    // 错误标签
    itemCodeErrorLabel = new JLabel(" ");
    itemCodeErrorLabel.setForeground(Color.RED);
    itemCodeErrorLabel.setFont(itemCodeErrorLabel.getFont().deriveFont(10f));

    itemNameErrorLabel = new JLabel(" ");
    itemNameErrorLabel.setForeground(Color.RED);
    itemNameErrorLabel.setFont(itemNameErrorLabel.getFont().deriveFont(10f));

    // 按钮
    submitButton = new JButton(viewModel.isEditMode() ? "更新" : "添加");
    submitButton.setPreferredSize(new Dimension(80, 30));

    cancelButton = new JButton("取消");
    cancelButton.setPreferredSize(new Dimension(80, 30));

    // 状态组件
    progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);

    statusLabel = new JLabel(" ");
    statusLabel.setFont(statusLabel.getFont().deriveFont(12f));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(15, 15, 15, 15));

    // 表单面板
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    int row = 0;

    // 检查项代码
    gbc.gridx = 0;
    gbc.gridy = row;
    formPanel.add(new JLabel("代码 *:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    formPanel.add(itemCodeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = ++row;
    gbc.gridwidth = 2;
    formPanel.add(itemCodeErrorLabel, gbc);

    // 检查项名称
    gbc.gridx = 0;
    gbc.gridy = ++row;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    formPanel.add(new JLabel("名称 *:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    formPanel.add(itemNameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = ++row;
    gbc.gridwidth = 2;
    formPanel.add(itemNameErrorLabel, gbc);

    // 参考值
    gbc.gridx = 0;
    gbc.gridy = ++row;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    formPanel.add(new JLabel("参考值:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    formPanel.add(referenceValField, gbc);

    // 单位
    gbc.gridx = 0;
    gbc.gridy = ++row;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    formPanel.add(new JLabel("单位:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    formPanel.add(unitField, gbc);

    // 状态
    gbc.gridx = 0;
    gbc.gridy = ++row;
    gbc.gridwidth = 2;
    formPanel.add(statusLabel, gbc);

    gbc.gridx = 0;
    gbc.gridy = ++row;
    formPanel.add(progressBar, gbc);

    add(formPanel, BorderLayout.CENTER);

    // 按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(submitButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * 绑定ViewModel
   */
  private void bindViewModel() {
    // 初始化字段值
    itemCodeField.setText(viewModel.getItemCode());
    itemNameField.setText(viewModel.getItemName());
    referenceValField.setText(viewModel.getReferenceVal());
    unitField.setText(viewModel.getUnit());

    updateErrorLabels();
    updateButtonStates();
    updateSubmittingState();

    // 监听ViewModel变化
    viewModel.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> handleViewModelChange(evt));
      }
    });
  }

  /**
   * 处理ViewModel变化
   */
  private void handleViewModelChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    switch (propertyName) {
      case "itemCodeError":
      case "itemNameError":
        updateErrorLabels();
        break;
      case "submitButtonEnabled":
      case "cancelButtonEnabled":
        updateButtonStates();
        break;
      case "submitting":
        updateSubmittingState();
        break;
      case "submitSuccess":
        firePropertyChange("submitSuccess", false, true);
        break;
      case "cancelled":
        firePropertyChange("cancelled", false, true);
        break;
    }
  }

  /**
   * 更新错误标签
   */
  private void updateErrorLabels() {
    String codeError = viewModel.getItemCodeError();
    if (codeError != null && !codeError.trim().isEmpty()) {
      itemCodeErrorLabel.setText(codeError);
      itemCodeField.setBorder(BorderFactory.createLineBorder(Color.RED));
    } else {
      itemCodeErrorLabel.setText(" ");
      itemCodeField.setBorder(UIManager.getBorder("TextField.border"));
    }

    String nameError = viewModel.getItemNameError();
    if (nameError != null && !nameError.trim().isEmpty()) {
      itemNameErrorLabel.setText(nameError);
      itemNameField.setBorder(BorderFactory.createLineBorder(Color.RED));
    } else {
      itemNameErrorLabel.setText(" ");
      itemNameField.setBorder(UIManager.getBorder("TextField.border"));
    }
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    submitButton.setEnabled(viewModel.isSubmitButtonEnabled());
    cancelButton.setEnabled(viewModel.isCancelButtonEnabled());
  }

  /**
   * 更新提交状态
   */
  private void updateSubmittingState() {
    boolean submitting = viewModel.isSubmitting();

    if (submitting) {
      statusLabel.setText("正在保存...");
      progressBar.setVisible(true);
    } else {
      statusLabel.setText(" ");
      progressBar.setVisible(false);
    }

    // 禁用/启用表单字段
    itemCodeField.setEnabled(!submitting);
    itemNameField.setEnabled(!submitting);
    referenceValField.setEnabled(!submitting);
    unitField.setEnabled(!submitting);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 表单字段变化监听
    itemCodeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateItemCode();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateItemCode();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateItemCode();
      }
    });

    itemNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateItemName();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateItemName();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateItemName();
      }
    });

    referenceValField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateReferenceVal();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateReferenceVal();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateReferenceVal();
      }
    });

    unitField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateUnit();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateUnit();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateUnit();
      }
    });

    // 按钮事件
    submitButton.addActionListener(e -> viewModel.submit());
    cancelButton.addActionListener(e -> viewModel.cancel());

    // 代码字段失焦验证
    itemCodeField.addFocusListener(new java.awt.event.FocusAdapter() {
      @Override
      public void focusLost(java.awt.event.FocusEvent e) {
        viewModel.validateItemCodeAsync();
      }
    });
  }

  private void updateItemCode() {
    SwingUtilities.invokeLater(() -> {
      String text = itemCodeField.getText();
      if (!text.equals(viewModel.getItemCode())) {
        viewModel.setItemCode(text);
      }
    });
  }

  private void updateItemName() {
    SwingUtilities.invokeLater(() -> {
      String text = itemNameField.getText();
      if (!text.equals(viewModel.getItemName())) {
        viewModel.setItemName(text);
      }
    });
  }

  private void updateReferenceVal() {
    SwingUtilities.invokeLater(() -> {
      String text = referenceValField.getText();
      if (!text.equals(viewModel.getReferenceVal())) {
        viewModel.setReferenceVal(text);
      }
    });
  }

  private void updateUnit() {
    SwingUtilities.invokeLater(() -> {
      String text = unitField.getText();
      if (!text.equals(viewModel.getUnit())) {
        viewModel.setUnit(text);
      }
    });
  }

  /**
   * 请求焦点到第一个字段
   */
  public void requestFocusOnFirstField() {
    SwingUtilities.invokeLater(() -> itemCodeField.requestFocusInWindow());
  }

  /**
   * 重置表单
   */
  public void resetForm() {
    viewModel.resetForm();
  }
}