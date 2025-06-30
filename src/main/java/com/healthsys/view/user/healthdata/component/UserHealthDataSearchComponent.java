package com.healthsys.view.user.healthdata.component;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.Appointment;
import com.healthsys.service.IAppointmentService;
import com.healthsys.service.impl.AppointmentServiceImpl;
import com.healthsys.viewmodel.user.healthdata.UserHealthDataViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * 用户健康数据搜索组件
 * 负责搜索条件输入和搜索操作，包含预约选择功能
 * 
 * @author AI Assistant
 */
public class UserHealthDataSearchComponent extends JPanel {

  private static final Logger logger = LoggerFactory.getLogger(UserHealthDataSearchComponent.class);

  private final UserHealthDataViewModel viewModel;
  private final IAppointmentService appointmentService;

  // 搜索组件
  private JTextField searchField;
  private JButton searchButton;
  private JButton clearButton;
  private JComboBox<String> dateRangeCombo;
  private JComboBox<AppointmentItem> appointmentCombo;

  // 回调接口
  public interface SearchCallback {
    void onSearch(String keyword);

    void onClear();

    void onAppointmentSelected(Appointment appointment);
  }

  private SearchCallback searchCallback;

  public UserHealthDataSearchComponent(UserHealthDataViewModel viewModel) {
    this.viewModel = viewModel;
    this.appointmentService = new AppointmentServiceImpl();
    initializeComponents();
    setupLayout();
    bindEvents();
    loadAppointments();
  }

  /**
   * 预约选择项包装类
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
      if (appointment == null) {
        return "全部预约";
      }
      return String.format("预约%d - %s (%s)",
          appointment.getAppointmentId(),
          appointment.getAppointmentDate(),
          appointment.getStatus());
    }
  }

  /**
   * 初始化组件
   */
  private void initializeComponents() {
    // 搜索输入框
    searchField = new JTextField(15);
    searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    searchField.setToolTipText("输入关键词搜索检查项名称、测量值或备注");

    // 搜索按钮
    searchButton = new JButton("搜索");
    searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    searchButton.setPreferredSize(new Dimension(70, 28));

    // 清空按钮
    clearButton = new JButton("清空");
    clearButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    clearButton.setPreferredSize(new Dimension(70, 28));

    // 日期范围选择
    String[] dateRanges = { "全部", "近一周", "近一月", "近三月", "近半年", "近一年" };
    dateRangeCombo = new JComboBox<>(dateRanges);
    dateRangeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    dateRangeCombo.setPreferredSize(new Dimension(80, 28));

    // 预约选择下拉框
    appointmentCombo = new JComboBox<>();
    appointmentCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    appointmentCombo.setPreferredSize(new Dimension(200, 28));
    appointmentCombo.addItem(new AppointmentItem(null)); // 添加"全部预约"选项
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createTitledBorder("搜索和筛选条件"));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // 第一行：预约选择
    gbc.gridx = 0;
    gbc.gridy = 0;
    add(new JLabel("选择预约:"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 2;
    add(appointmentCombo, gbc);

    gbc.gridx = 3;
    gbc.gridwidth = 1;
    add(Box.createHorizontalStrut(20), gbc);

    // 第二行：关键词搜索和时间范围
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    add(new JLabel("关键词:"), gbc);

    gbc.gridx = 1;
    add(searchField, gbc);

    gbc.gridx = 2;
    add(searchButton, gbc);

    gbc.gridx = 3;
    add(clearButton, gbc);

    gbc.gridx = 4;
    add(new JLabel("时间:"), gbc);

    gbc.gridx = 5;
    add(dateRangeCombo, gbc);
  }

  /**
   * 绑定事件
   */
  private void bindEvents() {
    // 搜索按钮点击事件
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performSearch();
      }
    });

    // 清空按钮点击事件
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        performClear();
      }
    });

    // 搜索框回车事件
    searchField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          performSearch();
        }
      }
    });

    // 日期范围选择变化事件
    dateRangeCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selectedRange = (String) dateRangeCombo.getSelectedItem();
        viewModel.setSelectedDateRange(selectedRange);
        // 自动触发搜索
        performSearch();
      }
    });

    // 预约选择变化事件
    appointmentCombo.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        AppointmentItem selectedItem = (AppointmentItem) appointmentCombo.getSelectedItem();
        if (selectedItem != null && searchCallback != null) {
          searchCallback.onAppointmentSelected(selectedItem.getAppointment());
        }
      }
    });

    // 绑定ViewModel事件
    bindViewModelEvents();
  }

  /**
   * 绑定ViewModel事件
   */
  private void bindViewModelEvents() {
    viewModel.addPropertyChangeListener("searchKeyword", evt -> {
      SwingUtilities.invokeLater(() -> {
        String newKeyword = (String) evt.getNewValue();
        if (!searchField.getText().equals(newKeyword)) {
          searchField.setText(newKeyword);
        }
      });
    });

    viewModel.addPropertyChangeListener("selectedDateRange", evt -> {
      SwingUtilities.invokeLater(() -> {
        String newDateRange = (String) evt.getNewValue();
        if (!dateRangeCombo.getSelectedItem().equals(newDateRange)) {
          dateRangeCombo.setSelectedItem(newDateRange);
        }
      });
    });

    viewModel.addPropertyChangeListener("loading", evt -> {
      SwingUtilities.invokeLater(() -> {
        boolean isLoading = (Boolean) evt.getNewValue();
        setSearchEnabled(!isLoading);
      });
    });
  }

  /**
   * 加载用户的预约列表
   */
  private void loadAppointments() {
    SwingUtilities.invokeLater(() -> {
      try {
        // 获取当前用户的预约
        Integer currentUserId = AppContext.getCurrentUser().getUserId();
        List<Appointment> appointments = appointmentService.getAppointmentsByUserId(currentUserId);

        // 清空现有选项（保留"全部预约"）
        appointmentCombo.removeAllItems();
        appointmentCombo.addItem(new AppointmentItem(null));

        if (appointments != null && !appointments.isEmpty()) {
          for (Appointment appointment : appointments) {
            // 只显示已完成的预约，因为只有已完成的预约才有体检结果
            if ("已完成".equals(appointment.getStatus())) {
              appointmentCombo.addItem(new AppointmentItem(appointment));
            }
          }
        }

        logger.info("加载用户预约列表完成，共 {} 个预约", appointmentCombo.getItemCount() - 1);

      } catch (Exception e) {
        logger.error("加载用户预约列表失败", e);
        JOptionPane.showMessageDialog(this,
            "加载预约列表失败：" + e.getMessage(),
            "错误",
            JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  /**
   * 执行搜索
   */
  private void performSearch() {
    String keyword = searchField.getText().trim();
    logger.debug("执行搜索，关键词: {}", keyword);

    // 通过ViewModel执行搜索
    viewModel.searchHealthDataCommand(keyword);

    // 通知回调
    if (searchCallback != null) {
      searchCallback.onSearch(keyword);
    }
  }

  /**
   * 执行清空
   */
  private void performClear() {
    logger.debug("清空搜索条件");

    // 清空输入框
    searchField.setText("");
    dateRangeCombo.setSelectedIndex(0);
    appointmentCombo.setSelectedIndex(0);

    // 通过ViewModel清空搜索条件
    viewModel.clearSearchConditions();

    // 通知回调
    if (searchCallback != null) {
      searchCallback.onClear();
    }
  }

  /**
   * 设置搜索控件启用状态
   */
  private void setSearchEnabled(boolean enabled) {
    searchField.setEnabled(enabled);
    searchButton.setEnabled(enabled);
    clearButton.setEnabled(enabled);
    dateRangeCombo.setEnabled(enabled);
    appointmentCombo.setEnabled(enabled);
  }

  // Getter和Setter方法
  public String getSearchKeyword() {
    return searchField.getText().trim();
  }

  public void setSearchKeyword(String keyword) {
    searchField.setText(keyword != null ? keyword : "");
  }

  public String getSelectedDateRange() {
    return (String) dateRangeCombo.getSelectedItem();
  }

  public void setSelectedDateRange(String dateRange) {
    if (dateRange != null) {
      dateRangeCombo.setSelectedItem(dateRange);
    }
  }

  public Appointment getSelectedAppointment() {
    AppointmentItem selectedItem = (AppointmentItem) appointmentCombo.getSelectedItem();
    return selectedItem != null ? selectedItem.getAppointment() : null;
  }

  public void setSearchCallback(SearchCallback callback) {
    this.searchCallback = callback;
  }

  public void focusSearchField() {
    searchField.requestFocusInWindow();
  }

  /**
   * 刷新预约列表
   */
  public void refreshAppointments() {
    loadAppointments();
  }
}