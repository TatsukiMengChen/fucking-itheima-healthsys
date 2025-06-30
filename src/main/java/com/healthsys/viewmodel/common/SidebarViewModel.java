package com.healthsys.viewmodel.common;

import com.healthsys.config.AppContext;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.viewmodel.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 侧边栏视图模型。
 * 管理侧边栏导航按钮和状态。
 * 
 * @author 梦辰
 * @since 1.0
 */
public class SidebarViewModel extends BaseViewModel {

  /**
   * 导航项数据类
   */
  public static class NavigationItem {
    private String text;
    private String action;
    private boolean isSection;

    public NavigationItem(String text, String action, boolean isSection) {
      this.text = text;
      this.action = action;
      this.isSection = isSection;
    }

    // Getters
    public String getText() {
      return text;
    }

    public String getAction() {
      return action;
    }

    public boolean isSection() {
      return isSection;
    }
  }

  private List<NavigationItem> navigationItems;
  private String selectedAction;

  /**
   * 构造函数
   */
  public SidebarViewModel() {
    navigationItems = new ArrayList<>();
    updateNavigationItems();
  }

  /**
   * 获取导航项列表
   */
  public List<NavigationItem> getNavigationItems() {
    updateNavigationItems();
    return new ArrayList<>(navigationItems);
  }

  /**
   * 更新导航项列表
   */
  public void updateNavigationItems() {
    navigationItems.clear();

    UserRoleEnum userRole = AppContext.getCurrentUserRole();

    if (userRole == null) {
      return;
    }

    // 根据用户角色添加导航项
    switch (userRole) {
      case NORMAL_USER:
        addUserNavigationItems();
        break;
      case ADMIN:
        addAdminNavigationItems();
        break;
      case SUPER_ADMIN:
        addSuperAdminNavigationItems();
        break;
    }

    // 添加通用导航项
    addCommonNavigationItems();
  }

  /**
   * 添加普通用户导航项
   */
  private void addUserNavigationItems() {
    navigationItems.add(new NavigationItem("预约管理", "appointment", false));
    navigationItems.add(new NavigationItem("健康跟踪", "tracking", false));
    navigationItems.add(new NavigationItem("体检结果", "analysis", false));
    navigationItems.add(new NavigationItem("健康数据", "userdata", false));
  }

  /**
   * 添加管理员导航项
   */
  private void addAdminNavigationItems() {
    // 管理员可以访问普通用户功能
    addUserNavigationItems();

    // 添加管理功能分组
    navigationItems.add(new NavigationItem("管理功能", "", true));
    navigationItems.add(new NavigationItem("检查项管理", "checkitem", false));
    navigationItems.add(new NavigationItem("检查组管理", "checkgroup", false));
    navigationItems.add(new NavigationItem("用户健康数据", "admindata", false));
  }

  /**
   * 添加超级管理员导航项
   */
  private void addSuperAdminNavigationItems() {
    // 超级管理员可以访问管理员功能
    addAdminNavigationItems();

    // 添加系统管理功能
    navigationItems.add(new NavigationItem("系统管理", "", true));
    navigationItems.add(new NavigationItem("用户管理", "usermanagement", false));
  }

  /**
   * 添加通用导航项
   */
  private void addCommonNavigationItems() {
    navigationItems.add(new NavigationItem("系统功能", "", true));
    navigationItems.add(new NavigationItem("系统设置", "settings", false));
    navigationItems.add(new NavigationItem("退出登录", "logout", false));
  }

  /**
   * 检查是否有权限访问指定功能
   */
  public boolean hasPermission(String action) {
    UserRoleEnum userRole = AppContext.getCurrentUserRole();

    if (userRole == null) {
      return false;
    }

    switch (action) {
      // 普通用户功能
      case "appointment":
      case "tracking":
      case "analysis":
      case "userdata":
        return true;

      // 管理员功能
      case "checkitem":
      case "checkgroup":
      case "admindata":
        return AppContext.isAdmin();

      // 超级管理员功能
      case "usermanagement":
        return AppContext.isSuperAdmin();

      // 通用功能
      case "settings":
      case "logout":
        return true;

      default:
        return false;
    }
  }

  /**
   * 设置选中的导航项
   */
  public void setSelectedAction(String action) {
    this.selectedAction = action;
  }

  /**
   * 获取选中的导航项
   */
  public String getSelectedAction() {
    return selectedAction;
  }

  /**
   * 检查指定导航项是否选中
   */
  public boolean isSelected(String action) {
    return action != null && action.equals(selectedAction);
  }

  /**
   * 清理资源
   */
  public void dispose() {
    if (navigationItems != null) {
      navigationItems.clear();
    }
  }
}