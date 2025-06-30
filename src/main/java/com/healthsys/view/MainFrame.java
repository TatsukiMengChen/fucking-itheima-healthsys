package com.healthsys.view;

import com.healthsys.config.AppContext;
import com.healthsys.dao.UserMapper;
import com.healthsys.service.IEmailService;
import com.healthsys.service.IUserService;
import com.healthsys.service.impl.EmailServiceImpl;
import com.healthsys.service.impl.UserServiceImpl;
import com.healthsys.view.admin.checkgroup.CheckGroupManagementPanel;
import com.healthsys.view.admin.checkitem.CheckItemManagementPanel;
import com.healthsys.view.admin.usermanagement.UserManagementPanel;
import com.healthsys.view.auth.AuthPanel;
import com.healthsys.view.common.HeaderComponent;
import com.healthsys.view.common.SidebarComponent;
import com.healthsys.view.settings.SystemSettingsPanel;
import com.healthsys.view.user.analysis.ResultAnalysisPanel;
import com.healthsys.view.user.appointment.AppointmentPanel;
import com.healthsys.view.user.healthdata.UserHealthDataPanel;
import com.healthsys.view.user.tracking.HealthTrackingPanel;
import com.healthsys.viewmodel.admin.usermanagement.UserManagementViewModel;
import com.healthsys.viewmodel.auth.AuthViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 应用程序主窗口
 * 作为所有UI组件的容器，负责整体界面布局和导航
 * 
 * @author AI健康管理系统开发团队
 */
public class MainFrame extends JFrame {

  private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);

  // 窗口尺寸常量
  private static final int DEFAULT_WIDTH = 1200;
  private static final int DEFAULT_HEIGHT = 800;
  private static final int MIN_WIDTH = 1000;
  private static final int MIN_HEIGHT = 600;

  // UI组件
  private JPanel contentPanel;
  private JPanel currentViewPanel;
  private HeaderComponent headerComponent;
  private SidebarComponent sidebarComponent;
  private JPanel mainContentPanel;

  // 认证相关组件
  private AuthPanel authPanel;
  private AuthViewModel authViewModel;

  // 服务层组件
  private IUserService userService;
  private IEmailService emailService;

  // 页面面板缓存
  private AppointmentPanel appointmentPanel;
  private HealthTrackingPanel healthTrackingPanel;
  private ResultAnalysisPanel resultAnalysisPanel;
  private UserHealthDataPanel userHealthDataPanel;
  private CheckItemManagementPanel checkItemManagementPanel;
  private CheckGroupManagementPanel checkGroupManagementPanel;
  private UserManagementPanel userManagementPanel;
  private SystemSettingsPanel systemSettingsPanel;

  /**
   * 构造函数
   */
  public MainFrame() {
    initializeServices();
    initializeFrame();
    initializeComponents();
    layoutComponents();
    bindEvents();

    logger.info("主窗口初始化完成");
  }

  /**
   * 初始化服务层组件
   */
  private void initializeServices() {
    try {
      // 创建服务实例
      emailService = new EmailServiceImpl();

      // 初始化数据访问层
      com.healthsys.config.DataAccessManager dataAccessManager = com.healthsys.config.DataAccessManager.getInstance();

      // 获取UserMapper实例
      UserMapper userMapper = dataAccessManager.getUserMapper();

      // 创建用户服务
      userService = new UserServiceImpl(userMapper, emailService);

      logger.info("服务层组件初始化完成");
    } catch (Exception e) {
      logger.error("服务层组件初始化失败", e);
      JOptionPane.showMessageDialog(this,
          "系统初始化失败：" + e.getMessage(),
          "错误",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * 初始化窗口基本属性
   */
  private void initializeFrame() {
    setTitle("健康管理系统 v1.0");
    setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setLocationRelativeTo(null); // 居中显示

    // 设置窗口图标（如果有的话）
    try {
      // TODO: 添加应用图标
      // setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/app-icon.png")));
    } catch (Exception e) {
      logger.debug("未设置应用图标");
    }
  }

  /**
   * 初始化UI组件
   */
  private void initializeComponents() {
    // 创建内容面板
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // 创建当前视图面板（用于动态切换不同的功能面板）
    currentViewPanel = new JPanel(new BorderLayout());

    // 初始化认证相关组件
    initializeAuthComponents();

    // 初始显示认证面板
    showAuthView();
  }

  /**
   * 初始化认证相关组件
   */
  private void initializeAuthComponents() {
    try {
      // 创建认证ViewModel
      authViewModel = new AuthViewModel(userService, emailService);

      // 设置认证成功回调（包括登录和注册）
      authViewModel.setAuthSuccessCallback(user -> {
        logger.info("用户认证成功: {}", user.getUsername());
        showMainApplicationView(user);
      });

      // 设置认证错误回调
      authViewModel.setAuthErrorCallback(errorMsg -> {
        logger.error("认证失败: {}", errorMsg);
        JOptionPane.showMessageDialog(this,
            "认证失败：" + errorMsg,
            "错误",
            JOptionPane.ERROR_MESSAGE);
      });

      // 设置注册成功回调（在RegistrationViewModel中）
      authViewModel.getRegistrationViewModel().setRegisterSuccessCallback(() -> {
        logger.info("用户注册成功");
        JOptionPane.showMessageDialog(this,
            "注册成功！请使用新账号登录。",
            "注册成功",
            JOptionPane.INFORMATION_MESSAGE);
        authViewModel.switchToLogin();
      });

      // 创建认证面板
      authPanel = new AuthPanel(authViewModel);

      logger.info("认证组件初始化完成");
    } catch (Exception e) {
      logger.error("认证组件初始化失败", e);
      JOptionPane.showMessageDialog(this,
          "认证组件初始化失败：" + e.getMessage(),
          "错误",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * 布局组件
   */
  private void layoutComponents() {
    // 设置主内容面板
    setContentPane(contentPanel);

    // 添加主内容区域
    contentPanel.add(currentViewPanel, BorderLayout.CENTER);

    // 添加状态栏（暂时用标签代替）
    JLabel statusLabel = new JLabel("请先登录系统");
    statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    contentPanel.add(statusLabel, BorderLayout.SOUTH);
  }

  /**
   * 绑定事件处理器
   */
  private void bindEvents() {
    // 窗口关闭事件
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        handleWindowClosing();
      }
    });
  }

  /**
   * 显示认证视图
   */
  private void showAuthView() {
    if (authPanel != null) {
      setCurrentView(authPanel);
      setTitle("健康管理系统 - 用户认证");
    } else {
      // 回退到欢迎视图
      showWelcomeView();
    }
  }

  /**
   * 显示主应用视图（用户登录后）
   */
  private void showMainApplicationView(com.healthsys.model.entity.User user) {
    try {
      // 设置当前登录用户到全局上下文
      AppContext.setCurrentUser(user);

      // 创建主应用面板
      JPanel mainAppPanel = new JPanel(new BorderLayout());

      // 创建Header组件
      if (headerComponent == null) {
        headerComponent = new HeaderComponent();
      }
      headerComponent.updateUserInfo();

      // 创建Sidebar组件
      if (sidebarComponent == null) {
        sidebarComponent = new SidebarComponent();
        sidebarComponent.setNavigationListener(this::handleNavigation);
      }
      sidebarComponent.updateNavigationButtons();

      // 创建主内容面板
      if (mainContentPanel == null) {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      }

      // 显示默认的主页面
      showDefaultMainPage();

      // 布局主应用面板
      mainAppPanel.add(headerComponent, BorderLayout.NORTH);
      mainAppPanel.add(sidebarComponent, BorderLayout.WEST);
      mainAppPanel.add(mainContentPanel, BorderLayout.CENTER);

      // 设置当前视图
      setCurrentView(mainAppPanel);
      setTitle("健康管理系统 - " + user.getUsername());
      setStatusMessage("用户已登录: " + user.getUsername());

      logger.info("主应用界面已显示，用户: {}", user.getUsername());

    } catch (Exception e) {
      logger.error("显示主应用视图失败", e);
      JOptionPane.showMessageDialog(this,
          "主界面加载失败：" + e.getMessage(),
          "错误",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * 处理导航事件
   */
  private void handleNavigation(String targetPanel) {
    logger.info("导航到: {}", targetPanel);

    try {
      switch (targetPanel) {
        case "logout":
          logout();
          break;
        case "appointment":
          setMainContent(getAppointmentPanel());
          setTitle("健康管理系统 - 预约管理");
          break;
        case "tracking":
          setMainContent(getHealthTrackingPanel());
          setTitle("健康管理系统 - 健康跟踪");
          break;
        case "analysis":
          setMainContent(getResultAnalysisPanel());
          setTitle("健康管理系统 - 体检结果分析");
          break;
        case "userdata":
          setMainContent(getUserHealthDataPanel());
          setTitle("健康管理系统 - 健康数据管理");
          break;
        case "checkitem":
          setMainContent(getCheckItemManagementPanel());
          setTitle("健康管理系统 - 检查项管理");
          break;
        case "checkgroup":
          setMainContent(getCheckGroupManagementPanel());
          setTitle("健康管理系统 - 检查组管理");
          break;
        case "admindata":
          // 管理员查看用户健康数据，可以复用用户健康数据面板
          setMainContent(getUserHealthDataPanel());
          setTitle("健康管理系统 - 用户健康数据管理");
          break;
        case "usermanagement":
          setMainContent(getUserManagementPanel());
          setTitle("健康管理系统 - 用户管理");
          break;
        case "settings":
          setMainContent(getSystemSettingsPanel());
          setTitle("健康管理系统 - 系统设置");
          break;
        default:
          logger.warn("未知的导航目标: {}", targetPanel);
          showDefaultMainPage();
          break;
      }
    } catch (Exception e) {
      logger.error("处理导航失败: {}", targetPanel, e);
      JOptionPane.showMessageDialog(this,
          "页面切换失败：" + e.getMessage(),
          "错误",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * 显示默认主页面
   */
  private void showDefaultMainPage() {
    var currentUser = AppContext.getCurrentUser();
    if (currentUser != null) {
      JPanel welcomePanel = new JPanel(new BorderLayout());

      JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
          "<h2>欢迎, " + currentUser.getUsername() + "!</h2>" +
          "<p>用户角色: " + currentUser.getRole() + "</p>" +
          "<p>邮箱: " + currentUser.getEmail() + "</p>" +
          "<p>注册时间: " + currentUser.getCreatedAt() + "</p>" +
          "<hr>" +
          "<p>请从左侧菜单选择要使用的功能</p>" +
          "</div></html>", JLabel.CENTER);
      welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

      welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
      setMainContent(welcomePanel);
    }
  }

  /**
   * 显示临时功能页面（功能开发中）
   */
  private void showTempPanel(String title, String message) {
    JPanel tempPanel = new JPanel(new BorderLayout());

    JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" +
        "<h2>" + title + "</h2>" +
        "<p>" + message + "</p>" +
        "<p>此功能将在后续版本中实现</p>" +
        "</div></html>", JLabel.CENTER);
    titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

    tempPanel.add(titleLabel, BorderLayout.CENTER);
    setMainContent(tempPanel);
  }

  /**
   * 设置主内容面板的内容
   */
  private void setMainContent(JPanel content) {
    if (mainContentPanel != null) {
      mainContentPanel.removeAll();
      mainContentPanel.add(content, BorderLayout.CENTER);
      mainContentPanel.revalidate();
      mainContentPanel.repaint();
    }
  }

  /**
   * 用户退出登录
   */
  private void logout() {
    int option = JOptionPane.showConfirmDialog(
        this,
        "确定要退出登录吗？",
        "确认退出登录",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (option == JOptionPane.YES_OPTION) {
      logger.info("用户退出登录");

      // 清理全局用户上下文
      AppContext.logout();

      // 清理UI组件
      if (headerComponent != null) {
        headerComponent.dispose();
        headerComponent = null;
      }

      if (sidebarComponent != null) {
        sidebarComponent = null;
      }

      if (mainContentPanel != null) {
        mainContentPanel = null;
      }

      // 重置认证状态
      if (authPanel != null) {
        authPanel.reset();
      }

      // 显示认证面板
      showAuthView();
      setStatusMessage("请先登录系统");
    }
  }

  /**
   * 显示欢迎视图（备用）
   */
  private void showWelcomeView() {
    JPanel welcomePanel = new JPanel(new BorderLayout());

    // 欢迎信息
    JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
        "<h2>欢迎使用健康管理系统</h2>" +
        "<p>系统初始化遇到问题，请检查配置</p>" +
        "<p>或联系系统管理员</p>" +
        "</div></html>", JLabel.CENTER);
    welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));

    welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

    // 重试按钮
    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton retryButton = new JButton("重新初始化");
    retryButton.addActionListener(e -> {
      // 重新初始化认证组件
      initializeAuthComponents();
      showAuthView();
    });
    buttonPanel.add(retryButton);

    welcomePanel.add(buttonPanel, BorderLayout.SOUTH);

    setCurrentView(welcomePanel);
  }

  /**
   * 设置当前显示的视图
   * 
   * @param viewPanel 要显示的面板
   */
  public void setCurrentView(JPanel viewPanel) {
    currentViewPanel.removeAll();
    currentViewPanel.add(viewPanel, BorderLayout.CENTER);
    currentViewPanel.revalidate();
    currentViewPanel.repaint();

    logger.debug("切换到新视图: {}", viewPanel.getClass().getSimpleName());
  }

  /**
   * 处理窗口关闭事件
   */
  private void handleWindowClosing() {
    int option = JOptionPane.showConfirmDialog(
        this,
        "确定要退出健康管理系统吗？",
        "确认退出",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

    if (option == JOptionPane.YES_OPTION) {
      logger.info("用户确认退出系统");

      // 执行清理工作
      cleanup();

      System.exit(0);
    }
  }

  /**
   * 获取预约面板（懒加载）
   */
  private JPanel getAppointmentPanel() {
    if (appointmentPanel == null) {
      appointmentPanel = new AppointmentPanel();
    }
    return appointmentPanel;
  }

  /**
   * 获取健康跟踪面板（懒加载）
   */
  private JPanel getHealthTrackingPanel() {
    if (healthTrackingPanel == null) {
      healthTrackingPanel = new HealthTrackingPanel();
    }
    return healthTrackingPanel;
  }

  /**
   * 获取体检结果分析面板（懒加载）
   */
  private JPanel getResultAnalysisPanel() {
    if (resultAnalysisPanel == null) {
      resultAnalysisPanel = new ResultAnalysisPanel();
    }
    return resultAnalysisPanel;
  }

  /**
   * 获取用户健康数据面板（懒加载）
   */
  private JPanel getUserHealthDataPanel() {
    if (userHealthDataPanel == null) {
      userHealthDataPanel = new UserHealthDataPanel();
    }
    return userHealthDataPanel;
  }

  /**
   * 获取检查项管理面板（懒加载）
   */
  private JPanel getCheckItemManagementPanel() {
    if (checkItemManagementPanel == null) {
      checkItemManagementPanel = new CheckItemManagementPanel();
    }
    return checkItemManagementPanel;
  }

  /**
   * 获取检查组管理面板（懒加载）
   */
  private JPanel getCheckGroupManagementPanel() {
    if (checkGroupManagementPanel == null) {
      checkGroupManagementPanel = new CheckGroupManagementPanel();
    }
    return checkGroupManagementPanel;
  }

  /**
   * 获取用户管理面板（懒加载）
   */
  private JPanel getUserManagementPanel() {
    if (userManagementPanel == null) {
      UserManagementViewModel userManagementViewModel = new UserManagementViewModel(userService);
      userManagementPanel = new UserManagementPanel(userManagementViewModel, userService);
    }
    return userManagementPanel;
  }

  /**
   * 获取系统设置面板（懒加载）
   */
  private JPanel getSystemSettingsPanel() {
    if (systemSettingsPanel == null) {
      systemSettingsPanel = new SystemSettingsPanel();
    }
    return systemSettingsPanel;
  }

  /**
   * 清理资源
   */
  private void cleanup() {
    try {
      // 清理认证组件
      if (authPanel != null) {
        authPanel.dispose();
      }

      if (authViewModel != null) {
        authViewModel.dispose();
      }

      // 清理面板缓存
      appointmentPanel = null;
      healthTrackingPanel = null;
      resultAnalysisPanel = null;
      userHealthDataPanel = null;
      checkItemManagementPanel = null;
      checkGroupManagementPanel = null;
      userManagementPanel = null;
      systemSettingsPanel = null;

      // TODO: 关闭数据库连接
      // TODO: 保存用户设置
      // TODO: 清理临时文件

      logger.info("资源清理完成");
    } catch (Exception e) {
      logger.error("资源清理失败", e);
    }
  }

  /**
   * 显示状态消息
   * 
   * @param message 状态消息
   */
  public void setStatusMessage(String message) {
    // TODO: 更新状态栏消息
    logger.info("状态: {}", message);
  }
}