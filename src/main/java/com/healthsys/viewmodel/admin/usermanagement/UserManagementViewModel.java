package com.healthsys.viewmodel.admin.usermanagement;

import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.service.IUserService;
import com.healthsys.viewmodel.base.BaseViewModel;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 用户管理ViewModel
 * 负责管理用户列表、搜索、权限控制等功能
 */
public class UserManagementViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(UserManagementViewModel.class);

  private final IUserService userService;
  private final ExecutorService executorService;

  // 当前登录用户信息
  private User currentUser;

  // 用户列表数据
  private List<User> userList;
  private List<User> filteredUserList;

  // 搜索条件
  private String searchKeyword = "";

  // 分页信息
  private int currentPage = 1;
  private int pageSize = 20;

  // 事件监听器
  private Consumer<List<User>> onUserListChanged;
  private Consumer<String> onError;
  private Consumer<String> onSuccess;
  private Consumer<Boolean> onLoading;

  /**
   * 构造函数
   *
   * @param userService 用户服务
   * @param currentUser 当前登录用户
   */
  public UserManagementViewModel(IUserService userService, User currentUser) {
    this.userService = userService;
    this.currentUser = currentUser;
    this.userList = new ArrayList<>();
    this.filteredUserList = new ArrayList<>();
    this.executorService = Executors.newCachedThreadPool();
  }

  /**
   * 加载用户列表
   */
  public void loadUserList() {
    if (onLoading != null) {
      onLoading.accept(true);
    }

    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          List<User> users = userService.queryUsers(searchKeyword, currentPage, pageSize);
          if (users != null) {
            userList = users;
            filteredUserList = new ArrayList<>(users);
          } else {
            userList = new ArrayList<>();
            filteredUserList = new ArrayList<>();
          }
        } catch (Exception e) {
          logger.error("加载用户列表失败", e);
          userList = new ArrayList<>();
          filteredUserList = new ArrayList<>();
        }
        return null;
      }

      @Override
      protected void done() {
        if (onLoading != null) {
          onLoading.accept(false);
        }
        if (onUserListChanged != null) {
          onUserListChanged.accept(filteredUserList);
        }
      }
    };

    worker.execute();
  }

  /**
   * 搜索用户
   *
   * @param keyword 搜索关键词
   */
  public void searchUsers(String keyword) {
    this.searchKeyword = keyword != null ? keyword.trim() : "";
    this.currentPage = 1; // 搜索时重置页码
    loadUserList();
  }

  /**
   * 刷新用户列表
   */
  public void refreshUserList() {
    loadUserList();
  }

  /**
   * 删除用户
   *
   * @param userId 用户ID
   */
  public void deleteUser(Integer userId) {
    if (userId == null) {
      if (onError != null) {
        onError.accept("用户ID不能为空");
      }
      return;
    }

    // 权限检查 - 只有管理员和超级管理员可以删除用户
    if (!canDeleteUser()) {
      if (onError != null) {
        onError.accept("权限不足，无法删除用户");
      }
      return;
    }

    // 不能删除自己
    if (userId.equals(currentUser.getUserId())) {
      if (onError != null) {
        onError.accept("不能删除当前登录用户");
      }
      return;
    }

    if (onLoading != null) {
      onLoading.accept(true);
    }

    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        return userService.deleteUser(userId);
      }

      @Override
      protected void done() {
        if (onLoading != null) {
          onLoading.accept(false);
        }
        try {
          Boolean result = get();
          if (result != null && result) {
            if (onSuccess != null) {
              onSuccess.accept("删除用户成功");
            }
            // 刷新列表
            loadUserList();
          } else {
            if (onError != null) {
              onError.accept("删除用户失败");
            }
          }
        } catch (Exception e) {
          logger.error("删除用户失败", e);
          if (onError != null) {
            onError.accept("删除用户失败：" + e.getMessage());
          }
        }
      }
    };

    worker.execute();
  }

  /**
   * 检查当前用户是否可以添加用户
   *
   * @return true表示可以添加
   */
  public boolean canAddUser() {
    if (currentUser == null) {
      return false;
    }
    UserRoleEnum role = UserRoleEnum.valueOf(currentUser.getRole());
    return role == UserRoleEnum.ADMIN || role == UserRoleEnum.SUPER_ADMIN;
  }

  /**
   * 检查当前用户是否可以编辑用户
   *
   * @param targetUser 目标用户
   * @return true表示可以编辑
   */
  public boolean canEditUser(User targetUser) {
    if (currentUser == null || targetUser == null) {
      return false;
    }

    UserRoleEnum currentRole = UserRoleEnum.valueOf(currentUser.getRole());
    UserRoleEnum targetRole = UserRoleEnum.valueOf(targetUser.getRole());

    // 超级管理员可以编辑所有用户
    if (currentRole == UserRoleEnum.SUPER_ADMIN) {
      return true;
    }

    // 管理员可以编辑普通用户，但不能编辑其他管理员和超级管理员
    if (currentRole == UserRoleEnum.ADMIN) {
      return targetRole == UserRoleEnum.NORMAL_USER;
    }

    return false;
  }

  /**
   * 检查当前用户是否可以删除用户
   *
   * @return true表示可以删除
   */
  public boolean canDeleteUser() {
    if (currentUser == null) {
      return false;
    }
    UserRoleEnum role = UserRoleEnum.valueOf(currentUser.getRole());
    return role == UserRoleEnum.ADMIN || role == UserRoleEnum.SUPER_ADMIN;
  }

  /**
   * 检查当前用户是否可以修改目标用户的角色
   *
   * @param targetUser 目标用户
   * @return true表示可以修改角色
   */
  public boolean canModifyUserRole(User targetUser) {
    if (currentUser == null || targetUser == null) {
      return false;
    }

    UserRoleEnum currentRole = UserRoleEnum.valueOf(currentUser.getRole());

    // 只有超级管理员可以修改用户角色
    return currentRole == UserRoleEnum.SUPER_ADMIN;
  }

  /**
   * 获取当前用户可以分配的角色列表
   *
   * @return 角色列表
   */
  public List<UserRoleEnum> getAvailableRoles() {
    List<UserRoleEnum> roles = new ArrayList<>();

    if (currentUser == null) {
      return roles;
    }

    UserRoleEnum currentRole = UserRoleEnum.valueOf(currentUser.getRole());

    if (currentRole == UserRoleEnum.SUPER_ADMIN) {
      // 超级管理员可以分配所有角色
      roles.add(UserRoleEnum.NORMAL_USER);
      roles.add(UserRoleEnum.ADMIN);
      roles.add(UserRoleEnum.SUPER_ADMIN);
    } else if (currentRole == UserRoleEnum.ADMIN) {
      // 管理员只能分配普通用户角色
      roles.add(UserRoleEnum.NORMAL_USER);
    }

    return roles;
  }

  // Getters and Setters

  public List<User> getUserList() {
    return filteredUserList != null ? new ArrayList<>(filteredUserList) : new ArrayList<>();
  }

  public String getSearchKeyword() {
    return searchKeyword;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  // Event Listeners

  public void setOnUserListChanged(Consumer<List<User>> onUserListChanged) {
    this.onUserListChanged = onUserListChanged;
  }

  public void setOnError(Consumer<String> onError) {
    this.onError = onError;
  }

  public void setOnSuccess(Consumer<String> onSuccess) {
    this.onSuccess = onSuccess;
  }

  public void setOnLoading(Consumer<Boolean> onLoading) {
    this.onLoading = onLoading;
  }

  /**
   * 释放资源
   */
  public void dispose() {
    if (executorService != null && !executorService.isShutdown()) {
      executorService.shutdown();
    }
  }
}