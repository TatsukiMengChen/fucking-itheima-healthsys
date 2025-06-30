package com.healthsys.view.admin.usermanagement.component;

import com.healthsys.viewmodel.admin.usermanagement.UserEditViewModel;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 用户个人信息表单组件
 * 包含姓名、电话、性别、生日、身份证、地址等个人信息字段。
 *
 * @author 梦辰
 */
public class UserPersonalInfoFormComponent extends JPanel implements PropertyChangeListener {

  private final UserEditViewModel viewModel;

  // UI 组件
  private JTextField unameField;
  private JTextField telField;
  private JComboBox<String> sexComboBox;
  private JTextField birField;
  private JTextField idcardField;
  private JTextArea addressArea;

  // 标签
  private JLabel unameLabel;
  private JLabel telLabel;
  private JLabel sexLabel;
  private JLabel birLabel;
  private JLabel idcardLabel;
  private JLabel addressLabel;

  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * 构造函数
   *
   * @param viewModel 用户编辑ViewModel
   */
  public UserPersonalInfoFormComponent(UserEditViewModel viewModel) {
    this.viewModel = viewModel;

    initializeComponents();
    setupLayout();
    setupEventListeners();
    setupDataBinding();
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建标签
    unameLabel = new JLabel("姓名:");
    telLabel = new JLabel("电话:");
    sexLabel = new JLabel("性别:");
    birLabel = new JLabel("出生日期:");
    idcardLabel = new JLabel("身份证号:");
    addressLabel = new JLabel("地址:");

    // 创建输入框
    unameField = new JTextField(15);
    telField = new JTextField(15);

    // 性别下拉框
    sexComboBox = new JComboBox<>(new String[] { "", "男", "女", "其他" });

    birField = new JTextField(15);
    idcardField = new JTextField(15);
    addressArea = new JTextArea(3, 15);

    // 设置提示文本
    unameField.setToolTipText("请输入真实姓名");
    telField.setToolTipText("请输入手机号码");
    birField.setToolTipText("请输入出生日期，格式：yyyy-MM-dd");
    idcardField.setToolTipText("请输入18位身份证号码");
    addressArea.setToolTipText("请输入详细地址");

    // 设置地址文本域
    addressArea.setLineWrap(true);
    addressArea.setWrapStyleWord(true);

    // 设置标签样式
    setupLabelStyles();
  }

  /**
   * 设置标签样式
   */
  private void setupLabelStyles() {
    Font labelFont = new Font("微软雅黑", Font.PLAIN, 12);

    unameLabel.setFont(labelFont);
    telLabel.setFont(labelFont);
    sexLabel.setFont(labelFont);
    birLabel.setFont(labelFont);
    idcardLabel.setFont(labelFont);
    addressLabel.setFont(labelFont);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), "个人信息"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 第一行：姓名和电话
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(unameLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;
    add(unameField, gbc);

    gbc.gridx = 2;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    add(telLabel, gbc);
    gbc.gridx = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;
    add(telField, gbc);

    // 第二行：性别和出生日期
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(sexLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;
    add(sexComboBox, gbc);

    gbc.gridx = 2;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    add(birLabel, gbc);
    gbc.gridx = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.5;
    add(birField, gbc);

    // 第三行：身份证号（跨两列）
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    add(idcardLabel, gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    add(idcardField, gbc);

    // 第四行：地址（跨两列）
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    add(addressLabel, gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    JScrollPane addressScrollPane = new JScrollPane(addressArea);
    addressScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    addressScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(addressScrollPane, gbc);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 姓名输入监听
    unameField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setUname(unameField.getText());
    }));

    // 电话输入监听
    telField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setTel(telField.getText());
    }));

    // 性别选择监听
    sexComboBox.addActionListener(e -> {
      String selectedSex = (String) sexComboBox.getSelectedItem();
      viewModel.setSex(selectedSex);
    });

    // 出生日期输入监听
    birField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      String dateText = birField.getText().trim();
      if (StrUtil.isNotBlank(dateText)) {
        try {
          LocalDate date = LocalDate.parse(dateText, dateFormatter);
          viewModel.setBir(date);
        } catch (DateTimeParseException e) {
          // 输入格式错误时不更新ViewModel
        }
      } else {
        viewModel.setBir(null);
      }
    }));

    // 身份证输入监听
    idcardField.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setIdcard(idcardField.getText());
    }));

    // 地址输入监听
    addressArea.getDocument().addDocumentListener(new SimpleDocumentListener(() -> {
      viewModel.setAddress(addressArea.getText());
    }));
  }

  /**
   * 设置数据绑定
   */
  private void setupDataBinding() {
    // 监听ViewModel属性变化
    viewModel.addPropertyChangeListener(this);

    // 初始化数据
    updateFromViewModel();
  }

  /**
   * 从ViewModel更新UI
   */
  private void updateFromViewModel() {
    unameField.setText(viewModel.getUname());
    telField.setText(viewModel.getTel());
    sexComboBox.setSelectedItem(viewModel.getSex());

    // 处理出生日期
    LocalDate bir = viewModel.getBir();
    if (bir != null) {
      birField.setText(bir.format(dateFormatter));
    } else {
      birField.setText("");
    }

    idcardField.setText(viewModel.getIdcard());
    addressArea.setText(viewModel.getAddress());
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(this::updateFromViewModel);
  }

  /**
   * 简单的文档监听器
   */
  private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
    private final Runnable action;

    public SimpleDocumentListener(Runnable action) {
      this.action = action;
    }

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
      SwingUtilities.invokeLater(action);
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
      SwingUtilities.invokeLater(action);
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
      SwingUtilities.invokeLater(action);
    }
  }
}