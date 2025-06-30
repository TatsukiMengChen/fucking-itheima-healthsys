package com.healthsys.service;

/**
 * 邮件服务接口。
 * 定义邮件发送与验证码相关的业务操作。
 * 
 * @author 梦辰
 */
public interface IEmailService {

  /**
   * 发送验证码到指定邮箱
   * 
   * @param email            目标邮箱地址
   * @param verificationCode 验证码
   * @return 发送结果，true表示成功，false表示失败
   */
  boolean sendVerificationCode(String email, String verificationCode);

  /**
   * 生成6位数字验证码
   * 
   * @return 验证码字符串
   */
  String generateVerificationCode();

  /**
   * 验证验证码是否正确且未过期
   * 
   * @param email     邮箱地址
   * @param inputCode 用户输入的验证码
   * @return true表示验证成功，false表示验证失败
   */
  boolean verifyCode(String email, String inputCode);

  /**
   * 清除指定邮箱的验证码缓存
   * 
   * @param email 邮箱地址
   */
  void clearVerificationCode(String email);
}