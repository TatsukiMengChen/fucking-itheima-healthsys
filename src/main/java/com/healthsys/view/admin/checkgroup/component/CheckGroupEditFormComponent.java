package com.healthsys.view.admin.checkgroup.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.healthsys.viewmodel.admin.checkgroup.CheckGroupEditViewModel;

/**
 * 检查组编辑表单组件。
 * 包含基本信息输入和检查项选择器。
 * 
 * @author 梦辰
 */
public class CheckGroupEditFormComponent extends JPanel implements PropertyChangeListener {

  private CheckGroupEditViewModel viewModel;

  // UI组件
  private JTextField groupCodeField;
  private JTextField groupNameField;
  private JTextArea descriptionArea;
  private CheckItemSelectorComponent checkItemSelector;
  private JButton submitButton;
  private JButton cancelButton;

  // 错误标签
  private JLabel groupCodeErrorLabel;
  private JLabel groupNameErrorLabel;

  // 事件监听器
  private FormEventListener formEventListener;

  /**
   * 构造函数
   */
  public CheckGroupEditFormComponent() {
    initializeComponents();
    setupLayout();
    setupEventHandlers();
  }

  /**
   * 设置ViewModel
   */
  public void setViewModel(CheckGroupEditViewModel viewModel) {
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
    // 输入字段
    groupCodeField = new JTextField(20);
    groupCodeField.setToolTipText("输入检查组代码，只能包含字母、数字和下划线");

    groupNameField = new JTextField(20);
    groupNameField.setToolTipText("输入检查组名称");

    descriptionArea = new JTextArea(3, 20);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    descriptionArea.setToolTipText("输入检查组描述（可选）");

    // 检查项选择器
    checkItemSelector = new CheckItemSelectorComponent();

    // 按钮
    submitButton = new JButton("提交");
    cancelButton = new JButton("取消");

    // 设置按钮颜色
    submitButton.setBackground(new Color(46, 204, 113));
    submitButton.setForeground(Color.WHITE);
    cancelButton.setBackground(new Color(149, 165, 166));
    cancelButton.setForeground(Color.WHITE);

    // 错误标签
    groupCodeErrorLabel = new JLabel();
    groupCodeErrorLabel.setForeground(Color.RED);
    groupCodeErrorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

    groupNameErrorLabel = new JLabel();
    groupNameErrorLabel.setForeground(Color.RED);
    groupNameErrorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // 顶部基本信息面板
    JPanel basicInfoPanel = createBasicInfoPanel();

    // 中间检查项选择面板
    JPanel checkItemPanel = new JPanel(new BorderLayout());
    checkItemPanel.setBorder(BorderFactory.createTitledBorder("关联检查项"));
    checkItemPanel.add(checkItemSelector, BorderLayout.CENTER);

    // 底部按钮面板
    JPanel buttonPanel = createButtonPanel();

    add(basicInfoPanel, BorderLayout.NORTH);
    add(checkItemPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * 创建基本信息面板
   */
  private JPanel createBasicInfoPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("基本信息"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 检查组代码
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("检查组代码:*"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(groupCodeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    panel.add(groupCodeErrorLabel, gbc);

    // 检查组名称
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    panel.add(new JLabel("检查组名称:*"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    panel.add(groupNameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    panel.add(groupNameErrorLabel, gbc);

    // 描述
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    panel.add(new JLabel("描述:"), gbc);

    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    JScrollPane descScrollPane = new JScrollPane(descriptionArea);
    descScrollPane.setPreferredSize(new Dimension(300, 80));
    panel.add(descScrollPane, gbc);

    return panel;
  }

  /**
   * 创建按钮面板
   */
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    panel.add(submitButton);
    panel.add(cancelButton);
    return panel;
  }

  /**
   * 设置事件处理器
   */
  private void setupEventHandlers() {
    // 输入字段事件
    groupCodeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateGroupCode();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateGroupCode();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateGroupCode();
      }
    });

    groupNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateGroupName();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateGroupName();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateGroupName();
      }
    });

    descriptionArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        updateDescription();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        updateDescription();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        updateDescription();
      }
    });

    // 检查项选择事件
    checkItemSelector.setSelectionChangeListener(selectedIds -> {
      if (viewModel != null) {
        viewModel.setSelectedCheckItemIds(selectedIds);
      }
    });

    // 按钮事件
    submitButton.addActionListener(e -> submit());
    cancelButton.addActionListener(e -> cancel());

    // 回车键提交
    groupCodeField.addActionListener(e -> submit());
    groupNameField.addActionListener(e -> submit());
  }

  /**
   * 更新检查组代码
   */
  private void updateGroupCode() {
    if (viewModel != null) {
      viewModel.setGroupCode(groupCodeField.getText());
    }
  }

  /**
   * 更新检查组名称
   */
  private void updateGroupName() {
    if (viewModel != null) {
      viewModel.setGroupName(groupNameField.getText());
    }
  }

  /**
   * 更新描述
   */
  private void updateDescription() {
    if (viewModel != null) {
      viewModel.setDescription(descriptionArea.getText());
    }
  }

  /**
   * 提交表单
   */
  private void submit() {
    if (viewModel != null) {
      viewModel.submit();
    }
  }

  /**
   * 取消操作
   */
  private void cancel() {
    if (viewModel != null) {
      viewModel.cancel();
    }
  }

  /**
   * 从ViewModel更新UI
   */
  private void updateFromViewModel() {
    if (viewModel == null)
      return;

    // 更新输入字段
    groupCodeField.setText(viewModel.getGroupCode());
    groupNameField.setText(viewModel.getGroupName());
    descriptionArea.setText(viewModel.getDescription());

    // 更新检查项选择器
    checkItemSelector.setAllCheckItems(viewModel.getAllCheckItems());
    checkItemSelector.setSelectedItemIds(viewModel.getSelectedCheckItemIds());

    // 更新错误信息
    updateValidationErrors();

    // 更新按钮状态
    updateButtonStates();
  }

  /**
   * 更新验证错误
   */
  private void updateValidationErrors() {
    if (viewModel == null)
      return;

    groupCodeErrorLabel.setText(viewModel.getGroupCodeError());
    groupNameErrorLabel.setText(viewModel.getGroupNameError());

    // 设置字段边框颜色
    groupCodeField.setBorder(viewModel.isGroupCodeValid() ? UIManager.getBorder("TextField.border")
        : BorderFactory.createLineBorder(Color.RED));

    groupNameField.setBorder(viewModel.isGroupNameValid() ? UIManager.getBorder("TextField.border")
        : BorderFactory.createLineBorder(Color.RED));
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    if (viewModel == null)
      return;

    submitButton.setEnabled(viewModel.isSubmitButtonEnabled());
    cancelButton.setEnabled(viewModel.isCancelButtonEnabled());

    // 更新提交按钮文本
    if (viewModel.isSubmitting()) {
      submitButton.setText("提交中...");
    } else {
      submitButton.setText("提交");
    }
  }

  /**
   * 设置表单事件监听器
   */
  public void setFormEventListener(FormEventListener listener) {
    this.formEventListener = listener;
  }

  /**
   * 重置表单
   */
  public void resetForm() {
    groupCodeField.setText("");
    groupNameField.setText("");
    descriptionArea.setText("");
    checkItemSelector.setSelectedItemIds(null);
    groupCodeErrorLabel.setText("");
    groupNameErrorLabel.setText("");

    // 重置字段边框
    groupCodeField.setBorder(UIManager.getBorder("TextField.border"));
    groupNameField.setBorder(UIManager.getBorder("TextField.border"));
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(() -> {
      String propertyName = evt.getPropertyName();

      switch (propertyName) {
        case "groupCode":
          if (!groupCodeField.getText().equals(viewModel.getGroupCode())) {
            groupCodeField.setText(viewModel.getGroupCode());
          }
          break;
        case "groupName":
          if (!groupNameField.getText().equals(viewModel.getGroupName())) {
            groupNameField.setText(viewModel.getGroupName());
          }
          break;
        case "description":
          if (!descriptionArea.getText().equals(viewModel.getDescription())) {
            descriptionArea.setText(viewModel.getDescription());
          }
          break;
        case "allCheckItems":
          checkItemSelector.setAllCheckItems(viewModel.getAllCheckItems());
          break;
        case "selectedCheckItemIds":
          checkItemSelector.setSelectedItemIds(viewModel.getSelectedCheckItemIds());
          break;
        case "groupCodeError":
        case "groupNameError":
        case "groupCodeValid":
        case "groupNameValid":
          updateValidationErrors();
          break;
        case "submitting":
        case "submitButtonEnabled":
        case "cancelButtonEnabled":
          updateButtonStates();
          break;
        case "submitSuccess":
          if (formEventListener != null) {
            formEventListener.onSubmitSuccess();
          }
          break;
        case "submitError":
          if (formEventListener != null) {
            formEventListener.onSubmitError((String) evt.getNewValue());
          }
          break;
        case "cancelled":
          if (formEventListener != null) {
            formEventListener.onCancelled();
          }
          break;
      }
    });
  }

  /**
   * 表单事件监听器接口
   */
  public interface FormEventListener {
    void onSubmitSuccess();

    void onSubmitError(String errorMessage);

    void onCancelled();
  }
}