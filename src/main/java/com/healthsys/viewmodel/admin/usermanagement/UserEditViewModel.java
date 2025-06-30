package com.healthsys.viewmodel.admin.usermanagement;

import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.service.IUserService;
import com.healthsys.util.ValidationUtil;
import com.healthsys.viewmodel.base.BaseViewModel;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 用户编辑ViewModel
 * 负责管理单个用户实体的编辑功能，包括权限控制和数据验证
 */
public class UserEditViewModel extends BaseViewModel {

  private static final Logger logger = LoggerFactory.getLogger(UserEditViewModel.class);

  private final IUserService userService;

  // 当前登录用户信息
  private User currentUser;

  // 正在编辑的用户对象
  private User editingUser;

  // 是否为新增模式
  private boolean isAddMode;

  // 用户属性
  private String username = "";
  private String password = "";
  private String confirmPassword = "";
  private String email = "";
  private String uname = "";
  private String tel = "";
  private String sex = "";
  private LocalDate bir;
  private String idcard = "";
  private String address = "";
  private String dep = "";
  private String lev = "";
  private String avatar = "";
  private UserRoleEnum role = UserRoleEnum.NORMAL_USER;

  // 可用角色列表
  private List<UserRoleEnum> availableRoles;

  // 事件监听器
  private Consumer<String> onError;
  private Consumer<String> onSuccess;
  private Consumer<Boolean> onLoading;
  private Runnable onSaveCompleted;

  /**
   * 构造函数
   *
   * @param userService 用户服务
   * @param currentUser 当前登录用户
   */
  public UserEditViewModel(IUserService userService, User currentUser) {
    this.userService = userService;
    this.currentUser = currentUser;
    this.availableRoles = getAvailableRolesInternal();
    this.isAddMode = true;
    this.editingUser = new User();
  }

  /**
   * 设置编辑用户（编辑模式）
   *
   * @param user 要编辑的用户
   */
  public void setEditingUser(User user) {
    if (user == null) {
      // 新增模式
      this.isAddMode = true;
      this.editingUser = new User();
      clearForm();
    } else {
      // 编辑模式
      this.isAddMode = false;
      this.editingUser = user;
      loadUserData(user);
    }
    updateAvailableRoles();
  }

  /**
   * 从用户对象加载数据到表单
   *
   * @param user 用户对象
   */
  private void loadUserData(User user) {
    setUsername(user.getUsername() != null ? user.getUsername() : "");
    setEmail(user.getEmail() != null ? user.getEmail() : "");
    setUname(user.getUname() != null ? user.getUname() : "");
    setTel(user.getTel() != null ? user.getTel() : "");
    setSex(user.getSex() != null ? user.getSex() : "");
    setBir(user.getBir());
    setIdcard(user.getIdcard() != null ? user.getIdcard() : "");
    setAddress(user.getAddress() != null ? user.getAddress() : "");
    setDep(user.getDep() != null ? user.getDep() : "");
    setLev(user.getLev() != null ? user.getLev() : "");
    setAvatar(user.getAvatar() != null ? user.getAvatar() : "");

    if (user.getRole() != null) {
      try {
        setRole(UserRoleEnum.valueOf(user.getRole()));
      } catch (IllegalArgumentException e) {
        setRole(UserRoleEnum.NORMAL_USER);
      }
    } else {
      setRole(UserRoleEnum.NORMAL_USER);
    }

    // 编辑模式下清空密码字段
    setPassword("");
    setConfirmPassword("");
  }

  /**
   * 清空表单
   */
  private void clearForm() {
    setUsername("");
    setPassword("");
    setConfirmPassword("");
    setEmail("");
    setUname("");
    setTel("");
    setSex("");
    setBir(null);
    setIdcard("");
    setAddress("");
    setDep("");
    setLev("");
    setAvatar("");
    setRole(UserRoleEnum.NORMAL_USER);
  }

  /**
   * 更新可用角色列表
   */
  private void updateAvailableRoles() {
    this.availableRoles = getAvailableRolesInternal();
  }

  /**
   * 获取当前用户可以分配的角色列表（私有方法）
   *
   * @return 角色列表
   */
  private List<UserRoleEnum> getAvailableRolesInternal() {
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

  /**
   * 检查当前用户是否可以修改角色
   *
   * @return true表示可以修改角色
   */
  public boolean canModifyRole() {
    if (currentUser == null) {
      return false;
    }

    UserRoleEnum currentRole = UserRoleEnum.valueOf(currentUser.getRole());

    // 超级管理员可以修改所有角色
    if (currentRole == UserRoleEnum.SUPER_ADMIN) {
      return true;
    }

    // 管理员在新增用户时可以分配普通用户角色
    if (currentRole == UserRoleEnum.ADMIN && isAddMode) {
      return true;
    }

    return false;
  }

  /**
   * 验证表单数据
   *
   * @return 错误信息，如果验证通过则返回null
   */
  private String validateForm() {
    // 用户名验证
    if (StrUtil.isBlank(username)) {
      return "用户名不能为空";
    }
    if (username.length() < 3 || username.length() > 50) {
      return "用户名长度必须在3-50个字符之间";
    }

    // 新增模式下密码必填
    if (isAddMode && StrUtil.isBlank(password)) {
      return "密码不能为空";
    }

    // 密码验证（如果输入了密码）
    if (StrUtil.isNotBlank(password)) {
      if (password.length() < 6) {
        return "密码长度不能少于6位";
      }
      if (!password.equals(confirmPassword)) {
        return "两次输入的密码不一致";
      }
    }

    // 邮箱验证
    if (StrUtil.isBlank(email)) {
      return "邮箱不能为空";
    }
    if (!ValidationUtil.isValidEmail(email)) {
      return "邮箱格式不正确";
    }

    // 身份证验证（如果填写了）
    if (StrUtil.isNotBlank(idcard) && !ValidationUtil.isValidIdCard(idcard)) {
      return "身份证号码格式不正确";
    }

    return null;
  }

  /**
   * 提交表单
   */
  public void submitForm() {
    // 验证表单
    String errorMsg = validateForm();
    if (errorMsg != null) {
      if (onError != null) {
        onError.accept(errorMsg);
      }
      return;
    }

    if (onLoading != null) {
      onLoading.accept(true);
    }

    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        try {
          // 构建用户对象
          User user = buildUserFromForm();

          // 保存用户
          return userService.addOrUpdateUser(user);
        } catch (Exception e) {
          logger.error("保存用户失败", e);
          throw e;
        }
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
              onSuccess.accept(isAddMode ? "添加用户成功" : "更新用户成功");
            }
            if (onSaveCompleted != null) {
              onSaveCompleted.run();
            }
          } else {
            if (onError != null) {
              onError.accept(isAddMode ? "添加用户失败" : "更新用户失败");
            }
          }
        } catch (Exception e) {
          logger.error("保存用户失败", e);
          if (onError != null) {
            onError.accept("保存用户失败：" + e.getMessage());
          }
        }
      }
    };

    worker.execute();
  }

  /**
   * 从表单构建用户对象
   *
   * @return 用户对象
   */
  private User buildUserFromForm() {
    User user = isAddMode ? new User() : new User();

    if (!isAddMode && editingUser != null) {
      user.setUserId(editingUser.getUserId());
      user.setCreatedAt(editingUser.getCreatedAt());
    }

    user.setUsername(username.trim());
    user.setEmail(email.trim());
    user.setUname(StrUtil.isNotBlank(uname) ? uname.trim() : null);
    user.setTel(StrUtil.isNotBlank(tel) ? tel.trim() : null);
    user.setSex(StrUtil.isNotBlank(sex) ? sex.trim() : null);
    user.setBir(bir);
    user.setIdcard(StrUtil.isNotBlank(idcard) ? idcard.trim() : null);
    user.setAddress(StrUtil.isNotBlank(address) ? address.trim() : null);
    user.setDep(StrUtil.isNotBlank(dep) ? dep.trim() : null);
    user.setLev(StrUtil.isNotBlank(lev) ? lev.trim() : null);
    user.setAvatar(StrUtil.isNotBlank(avatar) ? avatar.trim() : null);
    user.setRole(role.name());

    // 只有输入了密码才设置密码
    if (StrUtil.isNotBlank(password)) {
      user.setPassword(password);
    }

    user.setUpdatedAt(LocalDateTime.now());
    if (isAddMode) {
      user.setCreatedAt(LocalDateTime.now());
    }

    return user;
  }

  // Getters and Setters with Property Change Support

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = setProperty(this.username, username != null ? username : "", "username");
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = setProperty(this.password, password != null ? password : "", "password");
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = setProperty(this.confirmPassword, confirmPassword != null ? confirmPassword : "",
        "confirmPassword");
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = setProperty(this.email, email != null ? email : "", "email");
  }

  public String getUname() {
    return uname;
  }

  public void setUname(String uname) {
    this.uname = setProperty(this.uname, uname != null ? uname : "", "uname");
  }

  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = setProperty(this.tel, tel != null ? tel : "", "tel");
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = setProperty(this.sex, sex != null ? sex : "", "sex");
  }

  public LocalDate getBir() {
    return bir;
  }

  public void setBir(LocalDate bir) {
    this.bir = setProperty(this.bir, bir, "bir");
  }

  public String getIdcard() {
    return idcard;
  }

  public void setIdcard(String idcard) {
    this.idcard = setProperty(this.idcard, idcard != null ? idcard : "", "idcard");
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = setProperty(this.address, address != null ? address : "", "address");
  }

  public String getDep() {
    return dep;
  }

  public void setDep(String dep) {
    this.dep = setProperty(this.dep, dep != null ? dep : "", "dep");
  }

  public String getLev() {
    return lev;
  }

  public void setLev(String lev) {
    this.lev = setProperty(this.lev, lev != null ? lev : "", "lev");
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = setProperty(this.avatar, avatar != null ? avatar : "", "avatar");
  }

  public UserRoleEnum getRole() {
    return role;
  }

  public void setRole(UserRoleEnum role) {
    this.role = setProperty(this.role, role != null ? role : UserRoleEnum.NORMAL_USER, "role");
  }

  public List<UserRoleEnum> getAvailableRoles() {
    return new ArrayList<>(availableRoles);
  }

  public boolean isAddMode() {
    return isAddMode;
  }

  public User getEditingUser() {
    return editingUser;
  }

  // Event Listeners

  public void setOnError(Consumer<String> onError) {
    this.onError = onError;
  }

  public void setOnSuccess(Consumer<String> onSuccess) {
    this.onSuccess = onSuccess;
  }

  public void setOnLoading(Consumer<Boolean> onLoading) {
    this.onLoading = onLoading;
  }

  public void setOnSaveCompleted(Runnable onSaveCompleted) {
    this.onSaveCompleted = onSaveCompleted;
  }
}