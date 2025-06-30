package com.healthsys.view.admin.usermanagement.component;

import com.healthsys.config.AppContext;
import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.viewmodel.admin.usermanagement.UserManagementViewModel;
import com.healthsys.util.GuiUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 用户表格组件
 * 展示用户列表，支持搜索、增删改等操作。
 * 
 * @author 梦辰
 */
public class UserTableComponent extends JPanel {

  private final UserManagementViewModel viewModel;

  // UI 组件
  private JTextField searchField;
  private JButton searchButton;
  private JButton refreshButton;
  private JButton addButton;
  private JButton editButton;
  private JButton deleteButton;
  private JTable userTable;
  private UserTableModel tableModel;
  private JScrollPane scrollPane;

  // 事件监听器
  private Consumer<User> onEditUser;
  private Runnable onAddUser;

  /**
   * 构造函数
   *
   * @param viewModel 用户管理ViewModel
   */
  public UserTableComponent(UserManagementViewModel viewModel) {
    this.viewModel = viewModel;
    this.tableModel = new UserTableModel();

    initializeComponents();
    setupLayout();
    setupEventListeners();
    setupViewModelBindings();

    // 初始加载数据
    viewModel.loadUserList();
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 搜索组件
    searchField = new JTextField(20);
    searchField.setToolTipText("输入用户名、邮箱或姓名进行搜索");
    searchButton = new JButton("搜索");
    refreshButton = new JButton("刷新");

    // 操作按钮
    addButton = new JButton("添加用户");
    editButton = new JButton("编辑用户");
    deleteButton = new JButton("删除用户");

    // 设置按钮样式
    setButtonStyle(searchButton, new Color(52, 152, 219));
    setButtonStyle(refreshButton, new Color(149, 165, 166));
    setButtonStyle(addButton, new Color(46, 204, 113));
    setButtonStyle(editButton, new Color(241, 196, 15));
    setButtonStyle(deleteButton, new Color(231, 76, 60));

    // 用户表格
    userTable = new JTable(tableModel);
    setupTable();
    scrollPane = new JScrollPane(userTable);
    scrollPane.setPreferredSize(new Dimension(800, 400));

    // 初始状态设置
    updateButtonStates();
  }

  /**
   * 设置按钮样式
   *
   * @param button 按钮
   * @param color  背景颜色
   */
  private void setButtonStyle(JButton button, Color color) {
    button.setBackground(color);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setOpaque(true);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
  }

  /**
   * 设置表格样式和属性
   */
  private void setupTable() {
    userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userTable.setRowHeight(30);
    userTable.setGridColor(new Color(230, 230, 230));
    userTable.setShowGrid(true);
    userTable.setIntercellSpacing(new Dimension(1, 1));

    // 设置列宽
    TableColumnModel columnModel = userTable.getColumnModel();
    columnModel.getColumn(0).setPreferredWidth(60); // ID
    columnModel.getColumn(1).setPreferredWidth(120); // 用户名
    columnModel.getColumn(2).setPreferredWidth(100); // 姓名
    columnModel.getColumn(3).setPreferredWidth(180); // 邮箱
    columnModel.getColumn(4).setPreferredWidth(100); // 电话
    columnModel.getColumn(5).setPreferredWidth(80); // 角色
    columnModel.getColumn(6).setPreferredWidth(120); // 创建时间

    // 设置角色列的渲染器
    columnModel.getColumn(5).setCellRenderer(new RoleCellRenderer());

    // 设置表格头样式
    userTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
    userTable.getTableHeader().setBackground(new Color(52, 152, 219));
    userTable.getTableHeader().setForeground(Color.WHITE);
  }

  /**
   * 设置布局
   */
  private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // 顶部搜索面板
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    topPanel.add(new JLabel("搜索:"));
    topPanel.add(searchField);
    topPanel.add(searchButton);
    topPanel.add(refreshButton);

    // 操作按钮面板
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);

    // 顶部容器
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.add(topPanel, BorderLayout.NORTH);
    headerPanel.add(buttonPanel, BorderLayout.CENTER);

    add(headerPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * 设置事件监听器
   */
  private void setupEventListeners() {
    // 搜索按钮
    searchButton.addActionListener(e -> performSearch());

    // 搜索框回车事件
    searchField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          performSearch();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });

    // 刷新按钮
    refreshButton.addActionListener(e -> viewModel.refreshUserList());

    // 添加用户按钮
    addButton.addActionListener(e -> {
      if (onAddUser != null) {
        onAddUser.run();
      }
    });

    // 编辑用户按钮
    editButton.addActionListener(e -> {
      User selectedUser = getSelectedUser();
      if (selectedUser != null && onEditUser != null) {
        onEditUser.accept(selectedUser);
      }
    });

    // 删除用户按钮
    deleteButton.addActionListener(e -> {
      User selectedUser = getSelectedUser();
      if (selectedUser != null) {
        confirmAndDeleteUser(selectedUser);
      }
    });

    // 表格选择事件
    userTable.getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        updateButtonStates();
      }
    });

    // 双击编辑
    userTable.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
          User selectedUser = getSelectedUser();
          if (selectedUser != null && onEditUser != null) {
            onEditUser.accept(selectedUser);
          }
        }
      }
    });
  }

  /**
   * 设置ViewModel绑定
   */
  private void setupViewModelBindings() {
    // 监听用户列表变化
    viewModel.setOnUserListChanged(users -> {
      SwingUtilities.invokeLater(() -> {
        tableModel.setUsers(users);
        updateButtonStates();
      });
    });

    // 监听加载状态
    viewModel.setOnLoading(isLoading -> {
      SwingUtilities.invokeLater(() -> {
        setCursor(isLoading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        searchButton.setEnabled(!isLoading);
        refreshButton.setEnabled(!isLoading);
      });
    });
  }

  /**
   * 执行搜索
   */
  private void performSearch() {
    String keyword = searchField.getText().trim();
    viewModel.searchUsers(keyword);
  }

  /**
   * 获取选中的用户
   *
   * @return 选中的用户，未选中返回null
   */
  private User getSelectedUser() {
    int selectedRow = userTable.getSelectedRow();
    if (selectedRow >= 0) {
      return tableModel.getUserAt(selectedRow);
    }
    return null;
  }

  /**
   * 确认并删除用户
   *
   * @param user 要删除的用户
   */
  private void confirmAndDeleteUser(User user) {
    String message = String.format("确定要删除用户 '%s' 吗？\n此操作不可撤销！", user.getUsername());
    int result = JOptionPane.showConfirmDialog(
        this,
        message,
        "确认删除",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (result == JOptionPane.YES_OPTION) {
      viewModel.deleteUser(user.getUserId());
    }
  }

  /**
   * 更新按钮状态
   */
  private void updateButtonStates() {
    User selectedUser = getSelectedUser();
    boolean hasSelection = selectedUser != null;

    // 添加按钮状态
    addButton.setEnabled(viewModel.canAddUser());

    // 编辑按钮状态
    editButton.setEnabled(hasSelection && viewModel.canEditUser(selectedUser));

    // 删除按钮状态
    deleteButton.setEnabled(hasSelection && viewModel.canDeleteUser() &&
        !selectedUser.getUserId().equals(AppContext.getCurrentUser().getUserId()));
  }

  // 事件监听器设置方法

  public void setOnEditUser(Consumer<User> onEditUser) {
    this.onEditUser = onEditUser;
  }

  public void setOnAddUser(Runnable onAddUser) {
    this.onAddUser = onAddUser;
  }

  /**
   * 角色单元格渲染器
   */
  private static class RoleCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (value instanceof String) {
        String role = (String) value;
        try {
          UserRoleEnum roleEnum = UserRoleEnum.valueOf(role);
          switch (roleEnum) {
            case SUPER_ADMIN:
              setText("超级管理员");
              setBackground(isSelected ? table.getSelectionBackground() : new Color(231, 76, 60));
              setForeground(Color.WHITE);
              break;
            case ADMIN:
              setText("管理员");
              setBackground(isSelected ? table.getSelectionBackground() : new Color(241, 196, 15));
              setForeground(Color.WHITE);
              break;
            case NORMAL_USER:
              setText("普通用户");
              setBackground(isSelected ? table.getSelectionBackground() : new Color(149, 165, 166));
              setForeground(Color.WHITE);
              break;
            default:
              setText(role);
              setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
              setForeground(Color.BLACK);
          }
        } catch (IllegalArgumentException e) {
          setText(role);
          setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
          setForeground(Color.BLACK);
        }
      }

      setHorizontalAlignment(CENTER);
      return this;
    }
  }

  /**
   * 用户表格数据模型
   */
  private static class UserTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "用户名", "姓名", "邮箱", "电话", "角色", "创建时间"
    };

    private List<User> users = new ArrayList<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setUsers(List<User> users) {
      this.users = users != null ? new ArrayList<>(users) : new ArrayList<>();
      fireTableDataChanged();
    }

    public User getUserAt(int rowIndex) {
      if (rowIndex >= 0 && rowIndex < users.size()) {
        return users.get(rowIndex);
      }
      return null;
    }

    @Override
    public int getRowCount() {
      return users.size();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
      return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (rowIndex >= users.size()) {
        return null;
      }

      User user = users.get(rowIndex);
      switch (columnIndex) {
        case 0:
          return user.getUserId();
        case 1:
          return user.getUsername();
        case 2:
          return user.getUname() != null ? user.getUname() : "";
        case 3:
          return user.getEmail();
        case 4:
          return user.getTel() != null ? user.getTel() : "";
        case 5:
          return user.getRole();
        case 6:
          return user.getCreatedAt() != null ? user.getCreatedAt().format(dateFormatter) : "";
        default:
          return null;
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }
  }
}