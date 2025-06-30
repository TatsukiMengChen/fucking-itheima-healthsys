package com.healthsys.view.user.healthdata.component;

import com.healthsys.model.entity.CheckItem;
import com.healthsys.model.entity.ExaminationResult;
import com.healthsys.service.ICheckItemService;
import com.healthsys.service.impl.CheckItemServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 健康数据编辑表单组件
 * 用于添加或编辑健康数据记录
 * 
 * @author AI Assistant
 */
public class UserHealthDataEditFormComponent extends JDialog {

  private ICheckItemService checkItemService;

  // 编辑模式相关
  private boolean isEditMode = false;
  private ExaminationResult editingResult;

  // UI组件
  private JComboBox<CheckItem> checkItemCombo;
  private JTextField measuredValueField;
  private JTextField referenceValueField;
  private JTextArea notesArea;
  private JLabel recordTimeLabel;
  private JButton saveButton;
  private JButton cancelButton;

  // 回调函数
  private Runnable onSaveCallback;
  private Runnable onCancelCallback;

  // 数据缓存
  private List<CheckItem> checkItemList;

  /**
   * 构造函数 - 添加模式
   */
  public UserHealthDataEditFormComponent(Frame parent) {
    super(parent, "添加健康数据", true);
    this.isEditMode = false;
    this.checkItemService = new CheckItemServiceImpl();
    initializeComponents();
    setupLayout();
    bindEvents();
    loadCheckItems();
  }

  /**
   * 构造函数 - 编辑模式
   */
  public UserHealthDataEditFormComponent(Frame parent, ExaminationResult result) {
    super(parent, "编辑健康数据", true);
    this.isEditMode = true;
    this.editingResult = result;
    this.checkItemService = new CheckItemServiceImpl();
    initializeComponents();
    setupLayout();
    bindEvents();
    loadCheckItems();
    populateFields();
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 检查项下拉框
    checkItemCombo = new JComboBox<>();
    checkItemCombo.setPreferredSize(new Dimension(200, 30));
    checkItemCombo.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof CheckItem) {
          CheckItem item = (CheckItem) value;
          setText(item.getItemName() + " (" + item.getItemCode() + ")");
        }
        return this;
      }
    });

    // 测量值字段
    measuredValueField = new JTextField(15);
    measuredValueField.setPreferredSize(new Dimension(200, 30));

    // 参考值字段
    referenceValueField = new JTextField(15);
    referenceValueField.setPreferredSize(new Dimension(200, 30));
    referenceValueField.setEditable(false); // 只读，从检查项中获取

    // 备注区域
    notesArea = new JTextArea(4, 15);
    notesArea.setLineWrap(true);
    notesArea.setWrapStyleWord(true);

    // 记录时间标签
    recordTimeLabel = new JLabel();
    recordTimeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    recordTimeLabel.setForeground(Color.GRAY);

    // 按钮
    saveButton = new JButton(isEditMode ? "更新" : "保存");
    saveButton.setPreferredSize(new Dimension(80, 30));
    saveButton.setBackground(new Color(0, 123, 255));
    saveButton.setForeground(Color.WHITE);
    saveButton.setFocusPainted(false);

    cancelButton = new JButton("取消");
    cancelButton.setPreferredSize(new Dimension(80, 30));
    cancelButton.setFocusPainted(false);

    // 设置记录时间
    if (isEditMode && editingResult != null) {
      recordTimeLabel.setText("记录时间: " + editingResult.getRecordedAt());
    } else {
      recordTimeLabel
          .setText("记录时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout());
    setSize(400, 500);
    setLocationRelativeTo(getParent());
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    // 创建主面板
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 标题
    JLabel titleLabel = new JLabel(isEditMode ? "编辑健康数据" : "添加健康数据");
    titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    mainPanel.add(titleLabel, gbc);

    // 检查项
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 1;
    mainPanel.add(new JLabel("检查项:"), gbc);
    gbc.gridx = 1;
    mainPanel.add(checkItemCombo, gbc);

    // 测量值
    gbc.gridx = 0;
    gbc.gridy = 2;
    mainPanel.add(new JLabel("测量值:"), gbc);
    gbc.gridx = 1;
    mainPanel.add(measuredValueField, gbc);

    // 参考值
    gbc.gridx = 0;
    gbc.gridy = 3;
    mainPanel.add(new JLabel("参考值:"), gbc);
    gbc.gridx = 1;
    mainPanel.add(referenceValueField, gbc);

    // 备注
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    mainPanel.add(new JLabel("备注:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    JScrollPane notesScrollPane = new JScrollPane(notesArea);
    mainPanel.add(notesScrollPane, gbc);

    // 记录时间
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(recordTimeLabel, gbc);

    add(mainPanel, BorderLayout.CENTER);

    // 按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    buttonPanel.add(saveButton);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 检查项选择事件
    checkItemCombo.addActionListener(e -> {
      CheckItem selected = (CheckItem) checkItemCombo.getSelectedItem();
      if (selected != null) {
        // 设置参考值
        String referenceValue = selected.getReferenceVal();
        if (referenceValue != null && !referenceValue.trim().isEmpty()) {
          referenceValueField.setText(referenceValue + " " + (selected.getUnit() != null ? selected.getUnit() : ""));
        } else {
          referenceValueField.setText("未设置");
        }
      }
    });

    // 保存按钮事件
    saveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveData();
      }
    });

    // 取消按钮事件
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
        if (onCancelCallback != null) {
          onCancelCallback.run();
        }
      }
    });
  }

  /**
   * 加载检查项数据
   */
  private void loadCheckItems() {
    CompletableFuture.supplyAsync(() -> {
      try {
        return checkItemService.getAllActiveCheckItems();
      } catch (Exception e) {
        SwingUtilities.invokeLater(() -> {
          JOptionPane.showMessageDialog(this,
              "加载检查项失败: " + e.getMessage(),
              "错误",
              JOptionPane.ERROR_MESSAGE);
        });
        return null;
      }
    }).thenAccept(items -> {
      SwingUtilities.invokeLater(() -> {
        if (items != null) {
          this.checkItemList = items;
          checkItemCombo.removeAllItems();
          for (CheckItem item : items) {
            checkItemCombo.addItem(item);
          }

          // 如果是编辑模式，设置选中的检查项
          if (isEditMode && editingResult != null) {
            setSelectedCheckItem(editingResult.getItemId());
          }
        }
      });
    });
  }

  /**
   * 设置选中的检查项
   */
  private void setSelectedCheckItem(Integer itemId) {
    if (itemId != null && checkItemList != null) {
      for (CheckItem item : checkItemList) {
        if (item.getItemId().equals(itemId)) {
          checkItemCombo.setSelectedItem(item);
          break;
        }
      }
    }
  }

  /**
   * 填充编辑数据
   */
  private void populateFields() {
    if (editingResult != null) {
      measuredValueField.setText(editingResult.getMeasuredValue());
      if (editingResult.getResultNotes() != null) {
        notesArea.setText(editingResult.getResultNotes());
      }
    }
  }

  /**
   * 保存数据
   */
  private void saveData() {
    // 验证输入
    if (!validateInput()) {
      return;
    }

    // 准备数据
    CheckItem selectedItem = (CheckItem) checkItemCombo.getSelectedItem();
    String measuredValue = measuredValueField.getText().trim();
    String notes = notesArea.getText().trim();

    // 创建或更新ExaminationResult对象
    ExaminationResult result;
    if (isEditMode) {
      result = editingResult;
      result.setMeasuredValue(measuredValue);
      result.setResultNotes(notes.isEmpty() ? null : notes);
    } else {
      result = new ExaminationResult();
      result.setItemId(selectedItem.getItemId());
      result.setMeasuredValue(measuredValue);
      result.setResultNotes(notes.isEmpty() ? null : notes);
      result.setRecordedAt(LocalDateTime.now());
    }

    // 关闭对话框
    dispose();

    // 调用回调函数
    if (onSaveCallback != null) {
      onSaveCallback.run();
    }
  }

  /**
   * 验证输入
   */
  private boolean validateInput() {
    // 检查检查项
    if (checkItemCombo.getSelectedItem() == null) {
      JOptionPane.showMessageDialog(this,
          "请选择检查项",
          "验证错误",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }

    // 检查测量值
    String measuredValue = measuredValueField.getText().trim();
    if (measuredValue.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "请输入测量值",
          "验证错误",
          JOptionPane.WARNING_MESSAGE);
      measuredValueField.requestFocus();
      return false;
    }

    // 验证测量值格式（尝试解析为数字，但不强制要求）
    try {
      Double.parseDouble(measuredValue);
    } catch (NumberFormatException e) {
      // 如果不是数字，询问用户是否继续
      int result = JOptionPane.showConfirmDialog(this,
          "测量值不是数字格式，是否继续保存？",
          "确认",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
      if (result != JOptionPane.YES_OPTION) {
        measuredValueField.requestFocus();
        return false;
      }
    }

    return true;
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
   * 获取编辑结果
   */
  public ExaminationResult getEditingResult() {
    return editingResult;
  }

  /**
   * 创建新的结果对象（用于添加模式）
   */
  public ExaminationResult createNewResult() {
    if (isEditMode) {
      return editingResult;
    }

    CheckItem selectedItem = (CheckItem) checkItemCombo.getSelectedItem();
    if (selectedItem == null) {
      return null;
    }

    ExaminationResult result = new ExaminationResult();
    result.setItemId(selectedItem.getItemId());
    result.setMeasuredValue(measuredValueField.getText().trim());
    String notes = notesArea.getText().trim();
    result.setResultNotes(notes.isEmpty() ? null : notes);
    result.setRecordedAt(LocalDateTime.now());

    return result;
  }
}