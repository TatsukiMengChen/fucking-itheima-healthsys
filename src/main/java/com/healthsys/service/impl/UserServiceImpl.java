package com.healthsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthsys.dao.UserMapper;
import com.healthsys.model.entity.User;
import com.healthsys.model.enums.UserRoleEnum;
import com.healthsys.service.IEmailService;
import com.healthsys.service.IUserService;
import com.healthsys.util.PasswordUtil;
import com.healthsys.util.ValidationUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 提供用户注册、登录、管理等业务逻辑实现
 */
public class UserServiceImpl implements IUserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserMapper userMapper;
  private final IEmailService emailService;

  /**
   * 构造函数注入依赖
   */
  public UserServiceImpl(UserMapper userMapper, IEmailService emailService) {
    this.userMapper = userMapper;
    this.emailService = emailService;
  }

  @Override
  public boolean register(String username, String password, String email, String verificationCode) {
    logger.info("开始注册用户，用户名: {}, 邮箱: {}", username, email);

    // 参数校验
    if (StrUtil.hasBlank(username, password, email, verificationCode)) {
      throw new RuntimeException("用户名、密码、邮箱和验证码不能为空");
    }

    // 校验邮箱格式
    if (!ValidationUtil.isValidEmail(email)) {
      throw new RuntimeException("邮箱格式不正确");
    }

    // 校验用户名和邮箱是否已存在
    if (isUsernameExists(username)) {
      throw new RuntimeException("用户名已存在");
    }

    if (isEmailExists(email)) {
      throw new RuntimeException("邮箱已被注册");
    }

    // 验证验证码
    if (!emailService.verifyCode(email, verificationCode)) {
      throw new RuntimeException("验证码错误或已过期");
    }

    try {
      // 创建用户对象
      User user = new User();
      user.setUsername(username);
      user.setPassword(PasswordUtil.encryptPassword(password)); // 密码加密
      user.setEmail(email);
      user.setRole(UserRoleEnum.NORMAL_USER.name()); // 默认为普通用户
      user.setCreatedAt(LocalDateTime.now());
      user.setUpdatedAt(LocalDateTime.now());

      // 保存到数据库
      int result = userMapper.insert(user);

      if (result > 0) {
        // 清除验证码缓存
        emailService.clearVerificationCode(email);
        logger.info("用户注册成功，用户名: {}", username);
        return true;
      } else {
        throw new RuntimeException("注册失败，请稍后重试");
      }

    } catch (Exception e) {
      logger.error("用户注册失败，用户名: {}, 错误: {}", username, e.getMessage());
      throw new RuntimeException("注册失败：" + e.getMessage());
    }
  }

  @Override
  public User login(String username, String password) {
    logger.info("用户尝试登录，用户名: {}", username);

    // 参数校验
    if (StrUtil.hasBlank(username, password)) {
      return null;
    }

    try {
      // 根据用户名查询用户
      User user = getUserByUsername(username);
      if (user == null) {
        logger.info("登录失败：用户不存在，用户名: {}", username);
        return null;
      }

      // 验证密码
      if (PasswordUtil.verifyPassword(password, user.getPassword())) {
        logger.info("用户登录成功，用户名: {}", username);
        return user;
      } else {
        logger.info("登录失败：密码错误，用户名: {}", username);
        return null;
      }

    } catch (Exception e) {
      logger.error("用户登录异常，用户名: {}, 错误: {}", username, e.getMessage());
      return null;
    }
  }

  @Override
  public User getUserById(Integer userId) {
    if (userId == null) {
      return null;
    }

    try {
      return userMapper.selectById(userId);
    } catch (Exception e) {
      logger.error("根据用户ID查询用户失败，用户ID: {}, 错误: {}", userId, e.getMessage());
      return null;
    }
  }

  @Override
  public User getUserByUsername(String username) {
    if (StrUtil.isBlank(username)) {
      return null;
    }

    try {
      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(User::getUsername, username);
      return userMapper.selectOne(queryWrapper);
    } catch (Exception e) {
      logger.error("根据用户名查询用户失败，用户名: {}, 错误: {}", username, e.getMessage());
      return null;
    }
  }

  @Override
  public boolean isUsernameExists(String username) {
    if (StrUtil.isBlank(username)) {
      return false;
    }

    try {
      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(User::getUsername, username);
      return userMapper.selectCount(queryWrapper) > 0;
    } catch (Exception e) {
      logger.error("检查用户名是否存在失败，用户名: {}, 错误: {}", username, e.getMessage());
      return false;
    }
  }

  @Override
  public boolean isEmailExists(String email) {
    if (StrUtil.isBlank(email)) {
      return false;
    }

    try {
      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(User::getEmail, email);
      return userMapper.selectCount(queryWrapper) > 0;
    } catch (Exception e) {
      logger.error("检查邮箱是否存在失败，邮箱: {}, 错误: {}", email, e.getMessage());
      return false;
    }
  }

  @Override
  public boolean addOrUpdateUser(User user) {
    if (user == null) {
      return false;
    }

    try {
      user.setUpdatedAt(LocalDateTime.now());

      if (user.getUserId() == null) {
        // 新增用户
        if (StrUtil.isNotBlank(user.getPassword())) {
          user.setPassword(PasswordUtil.encryptPassword(user.getPassword()));
        }
        user.setCreatedAt(LocalDateTime.now());
        return userMapper.insert(user) > 0;
      } else {
        // 更新用户
        if (StrUtil.isNotBlank(user.getPassword())) {
          user.setPassword(PasswordUtil.encryptPassword(user.getPassword()));
        }
        return userMapper.updateById(user) > 0;
      }
    } catch (Exception e) {
      logger.error("添加或更新用户失败，错误: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public boolean deleteUser(Integer userId) {
    if (userId == null) {
      return false;
    }

    try {
      return userMapper.deleteById(userId) > 0;
    } catch (Exception e) {
      logger.error("删除用户失败，用户ID: {}, 错误: {}", userId, e.getMessage());
      return false;
    }
  }

  @Override
  public List<User> queryUsers(String searchKeyword, int page, int size) {
    try {
      LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

      if (StrUtil.isNotBlank(searchKeyword)) {
        queryWrapper.like(User::getUsername, searchKeyword)
            .or()
            .like(User::getEmail, searchKeyword)
            .or()
            .like(User::getUname, searchKeyword);
      }

      queryWrapper.orderByDesc(User::getCreatedAt);

      Page<User> pageObj = new Page<>(page, size);
      Page<User> resultPage = userMapper.selectPage(pageObj, queryWrapper);

      return resultPage.getRecords();
    } catch (Exception e) {
      logger.error("分页查询用户失败，错误: {}", e.getMessage());
      return null;
    }
  }
}
