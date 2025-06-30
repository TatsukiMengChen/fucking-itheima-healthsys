# 邮箱配置说明

## 环境变量配置

项目使用QQ邮箱SMTP服务发送验证码邮件。需要在项目根目录创建 `.env` 文件来配置邮箱信息。

### 第一步：创建.env文件

在项目根目录（与build.gradle.kts同级）创建 `.env` 文件：

```properties
# QQ邮箱地址
SPRING_MAIL_USERNAME=your_qq_email@qq.com

# QQ邮箱授权码
SPRING_MAIL_PASSWORD=your_qq_email_auth_code
```

### 第二步：获取QQ邮箱授权码

1. 登录QQ邮箱网页版 (https://mail.qq.com)
2. 点击**设置** -> **账户**
3. 找到**POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务**
4. 点击**开启SMTP服务**
5. 按照提示发送短信验证
6. 获得16位授权码（类似：abcdefghijklmnop）

### 第三步：填写配置

将.env文件中的配置替换为您的实际信息：

```properties
# 示例配置
SPRING_MAIL_USERNAME=zhangsan@qq.com
SPRING_MAIL_PASSWORD=abcdefghijklmnop
```

### 注意事项

- **授权码不是QQ密码**：必须使用QQ邮箱生成的16位授权码
- **保护隐私**：.env文件已添加到.gitignore，不会被提交到版本控制
- **SMTP设置**：项目已配置QQ邮箱SMTP服务器(smtp.qq.com:587)

### application.properties配置

项目中的邮箱配置已更新为：

```properties
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=${SPRING_MAIL_USERNAME:your_qq_email@qq.com}
spring.mail.password=${SPRING_MAIL_PASSWORD:your_qq_email_auth_code}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.default-encoding=UTF-8
```

### 故障排除

1. **发送失败**：检查授权码是否正确，确保是16位授权码而不是QQ密码
2. **连接超时**：检查网络连接，QQ邮箱SMTP服务器可能被防火墙阻止
3. **认证失败**：确保QQ邮箱已开启SMTP服务

### 配置文件说明

由于这是纯Java项目，环境变量的配置有以下几种方式：

#### 方式1：创建.env文件（推荐）
在项目根目录创建.env文件，然后在IDE中配置环境变量：

```properties
SPRING_MAIL_USERNAME=your_qq_email@qq.com
SPRING_MAIL_PASSWORD=your_qq_email_auth_code
```

#### 方式2：直接修改application.properties
如果不想使用环境变量，可以直接在application.properties中配置：

```properties
spring.mail.username=your_qq_email@qq.com
spring.mail.password=your_qq_email_auth_code
```

#### 方式3：IDE运行配置
在IDE中设置VM options或环境变量：
- IntelliJ IDEA: Run -> Edit Configurations -> Environment Variables
- Eclipse: Run Configurations -> Environment

### 快速配置方法

项目提供了多种配置方式，选择最适合您的：

#### 方法1：使用示例配置文件
```bash
# 复制示例文件
cp example.env .env

# 编辑.env文件，填入您的配置
notepad .env  # Windows
nano .env     # Linux/Mac
```

#### 方法2：使用启动脚本
```bash
# Windows
编辑 run_with_env.bat 文件，填入配置后双击运行

# Linux/Mac
编辑 run_with_env.sh 文件，填入配置后运行：
./run_with_env.sh
```

#### 方法3：IDE配置
在IDE的运行配置中设置环境变量：
- IntelliJ IDEA: Run -> Edit Configurations -> Environment Variables
- Eclipse: Run Configurations -> Environment

### 测试配置

1. **自动检查**：启动应用时会自动检查配置并显示结果
2. **查看日志**：控制台会显示详细的配置信息和错误提示
3. **测试发送**：在注册页面测试验证码发送功能 