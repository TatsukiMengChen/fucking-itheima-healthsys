## 健康管理系统项目架构文档

**课程名称**: 软件开发实训
**题 目**: 健康管理系统
[cite\_start]**专 业**: 软件工程 [cite: 1]
[cite\_start]**班 级**: 2024级 [cite: 1]
[cite\_start]**完成人数**: 1 [cite: 1]
[cite\_start]**起讫日期**: 2025.06.23 - 2025.07.05 [cite: 1]
[cite\_start]**完成时间**: 2025.07.05 [cite: 1]

### 1\. 项目概述

[cite\_start]通过本次软件开发实训，旨在指导学生使用 Java 语言基于 Java Swing 框架和数据库编程实现一个具有一定规模和复杂度的软件开发项目，以提高学生编写、调试、测试程序、运行维护及文档撰写等软件开发能力 [cite: 1][cite\_start]。本项目聚焦于“健康管理系统”的开发 [cite: 1][cite\_start]，旨在深入理解结构化和面向对象软件开发过程全生命周期的基本设计、开发方法和技术 [cite: 1]。

[cite\_start]该系统将显著优化医疗资源配置，大幅节省人力、物力及时间成本 [cite: 2][cite\_start]。它不仅为医护人员提供高效的健康数据管理平台，实现检查项目的精准追踪和患者信息的动态管理 [cite: 2][cite\_start]，更为普通用户带来便捷的健康自检服务 [cite: 2][cite\_start]。通过可视化数据面板，系统使健康监测从传统的被动诊疗转变为主动预防，真正实现“让健康管理触手可及”的服务理念，全面提升医疗服务的可及性和精准性 [cite: 2]。

### 2\. 训练目的与要求

**训练目的**:
[cite\_start]通过指导学生使用Java语言基于Java Swing框架和MySQL数据库编程实现一个具有一定规模和复杂度的软件开发项目，提高学生编写程序、调试程序、软件测试、运行维护及文档撰写等软件开发能力 [cite: 1][cite\_start]。使学生掌握结构化和面向对象软件开发过程全生命周期的基本设计、开发方法和技术，了解影响设计目标和技术方案的各种因素，能够选择与使用恰当的结构化和面向对象软件开发过程中需用到的软件工程工具、信息资源，主要针对桌面应用和C/S结构系统中的复杂软件工程问题，进行复杂软件系统的分析、设计、实现、验证、应用和维护 [cite: 1]。

**训练内容和要求**:
[cite\_start]要求学生根据老师给定的选题和需求描述，1人1组，独自完成需求分析，程序模块设计以及程序的编写、调试和测试 [cite: 1][cite\_start]。要求使用Java语言基于Java Swing框架实现一个桌面应用程序 [cite: 1]。

### 3\. 需求分析

[cite\_start]根据大作业任务书和项目初期描述，本健康管理系统需满足以下核心需求[cite: 3]:

1.  [cite\_start]**统一友好的操作界面，具有良好的用户体验。** [cite: 3]
2.  [cite\_start]**登录注册功能**[cite: 1]:
    * 实现用户登录功能。
    * 实现用户注册功能。
    * 拓展：结合物联网技术，实现刷卡登录；读取 dll 文件（**注：此拓展需求超出了本次文档范围，将在系统核心功能稳定后再评估集成可行性，本次设计暂不考虑**）。
3.  [cite\_start]**检查项管理**[cite: 1, 3]:
    * 查询所有检查项: 分页查询、编号搜索查询。
    * 创建检查项 (例如：血红蛋白、白细胞等)。
    * 删除检查项目。
    * 修改检查项信息。
4.  [cite\_start]**检查组管理**[cite: 1]:
    * 检查组查询: 关联查询三种机制、搜索查询。
    * 创建检查组: 勾选多个检查项形成检查组。
    * 删除检查组。
    * 检查组信息修改。
5.  [cite\_start]**预约与跟踪**[cite: 1]:
    * 预约体检。
    * 体检方式选择。
    * 体检结果分析。
    * 用户病史对比与跟踪。
6.  [cite\_start]**设计后台管理，用于管理系统的各项基本数据，包括类别管理、书籍管理、用户管理。** [cite: 3]
7.  [cite\_start]**权限分配功能（管理员、普通用户）** [cite: 3]：本系统将简化为普通用户、管理员、超级管理员三级权限。
8.  [cite\_start]**用户信息的注册、验证、登录功能。** [cite: 3]
9.  [cite\_start]**检查项的增删改查。** [cite: 3]
10. [cite\_start]**检查组的增删改查。** [cite: 3]
11. [cite\_start]**系统运行安全稳定且响应及时。** [cite: 3]
12. [cite\_start]**高可用架构：MySQL 事务保障数据一致性，连接池优化确保秒级响应。** [cite: 3] (**注：根据之前约定，数据库将采用 PostgreSQL。** )

### 4\. 架构模式：MVVM（Model-View-ViewModel）深化与组件化

本项目将深化 MVVM 架构模式的应用，并引入**组件化设计**理念。每个页面被视为一个主视图，而页面内部的独立功能区域或可复用的 UI 元素将被拆分为独立的 UI 组件。每个 UI 组件（`JPanel`）将拥有其对应的 ViewModel，以实现更细粒度的职责分离、更高的可复用性、更强的可测试性，并便于团队协作开发。

* **Model（模型）**: 业务数据和核心业务逻辑。包含 POJO（Plain Old Java Objects）实体类和负责数据操作的服务层。
* **View（视图）**: Swing UI 组件，负责界面的渲染和用户交互的捕获。每个独立的功能区域或可复用 UI 元素都是一个 `JPanel` 视图组件。
* **ViewModel（视图模型）**: 作为 View 和 Model 之间的桥梁，持有 View 所需的数据和命令。每个 View 组件都将对应一个 ViewModel。ViewModel 负责处理 View 的显示逻辑、数据绑定和用户输入，并将这些操作转发给 Model 层。ViewModel 应该尽可能地与特定的 View 类型解耦，以便重用。

**组件化设计优势：**

* **职责单一**: 每个组件及其 ViewModel 只关注特定功能，代码更清晰。
* **高度复用**: 独立的 UI 组件可以在不同的主视图或页面中重复使用，减少代码冗余。
* **便于管理**: 对某个小功能或 UI 区域的修改，只需聚焦于其对应的组件代码，降低修改影响范围。
* **独立测试**: 组件的 ViewModel 可以独立于 UI 进行单元测试。
* **降低复杂性**: 将大页面分解为小的、可管理的单元，降低整体复杂度。

### 5\. 核心模块与组件拆分

系统将逻辑和 UI 功能划分为以下核心模块，并进一步细化为可复用的组件：

* **认证模块**: 处理用户注册、登录和角色权限。
    * **AuthPanel**: 主认证视图。
        * **LoginFormComponent**: 登录表单组件（用户名、密码输入，登录按钮）。
        * **RegistrationFormComponent**: 注册表单组件（邮箱、验证码、用户名、密码输入，注册按钮）。
* **检查项管理模块**: 管理健康检查项。
    * **CheckItemManagementPanel**: 管理员的检查项管理主视图。
        * **CheckItemTableComponent**: 显示检查项列表的表格组件（包含搜索/过滤栏）。
        * **CheckItemEditFormComponent**: 检查项添加/编辑的表单组件。
* **检查组管理模块**: 管理检查组。
    * **CheckGroupManagementPanel**: 管理员的检查组管理主视图。
        * **CheckGroupTableComponent**: 显示检查组列表的表格组件（包含搜索/过滤栏）。
        * **CheckGroupEditFormComponent**: 检查组添加/编辑的表单组件。
        * **CheckItemSelectorComponent**: 检查组创建/修改时用于勾选检查项的组件 (可复用)。
* **用户健康数据管理模块**: 管理用户健康数据。
    * **UserHealthDataPanel**: 普通用户的健康数据管理主视图。
        * **UserHealthDataTableComponent**: 显示用户健康数据列表的表格组件（包含搜索/过滤栏）。
        * **UserHealthDataEditFormComponent**: 用户健康数据添加/编辑的表单组件。
    * **AdminHealthDataPanel**: 管理员的所有用户健康数据管理主视图。
        * **AdminHealthDataTableComponent**: 显示所有用户健康数据列表的表格组件（包含用户选择器和搜索/过滤栏）。
        * **UserHealthDataEditFormComponent**: 用户健康数据添加/编辑的表单组件 (复用)。
* **预约与跟踪模块**: 处理体检预约、结果分析和病史跟踪。
    * **AppointmentPanel**: 用户预约体检主视图。
        * **AppointmentFormComponent**: 预约表单组件（选择检查组、体检日期、方式）。
        * **AppointmentHistoryComponent**: 显示预约历史记录的表格组件。
    * **HealthTrackingPanel**: 用户病史对比与跟踪主视图。
        * **HealthComparisonChartComponent**: 用于显示数据对比的图表组件。
        * **MedicalHistoryListComponent**: 显示用户历史病史的列表组件。
    * **ResultAnalysisPanel**: 体检结果分析主视图。
        * **ResultDisplayComponent**: 显示体检结果详情的组件。
        * **AnalysisSuggestionComponent**: 基于结果给出分析和建议的组件。
* **用户管理模块**: 允许管理员（特别是超级管理员）管理用户帐户和分配角色。
    * **UserManagementPanel**: 管理员的用户管理主视图。
        * **UserTableComponent**: 显示用户列表的表格组件（包含搜索/过滤栏）。
        * **UserEditFormComponent**: 用户添加/编辑的表单组件。
* **设置模块**:
    * **SettingsPanel**: 系统设置主视图。
        * **LogoutButtonComponent**: 退出登录按钮组件。
        * **SystemSettingsFormComponent**: 系统配置表单组件 (仅管理员可见)。
* **通用模块**:
    * **HeaderComponent**: 应用程序顶部统一的标题/菜单栏组件。
    * **SidebarComponent**: 左侧导航栏组件。
    * **NotificationComponent**: 通用的消息/通知显示组件（例如，成功/失败提示）。
    * **PagingComponent**: 通用的分页组件。

### 6\. 数据库设计 (PostgreSQL)

本系统将使用 PostgreSQL 数据库，并简化权限管理。**（注：任务书要求使用 MySQL，但考虑到 PostgreSQL 的现代化特性和我们之前的讨论，本项目将采用 PostgreSQL。在项目交付时将对此差异进行说明。）**

#### 6.1 `users` 表

`users` 表用于保存系统用户信息（管理员、医生、护士、病人）。

| 字段名     | 类型         | 可空性 | 主键 | 说明                                   |
| :--------- | :----------- | :----- | :--- | :------------------------------------- |
| user\_id    | SERIAL       | 否     | 是   | 用户 ID（自增）                          |
| username   | VARCHAR(50)  | 否     | 否   | 唯一用户名                               |
| password   | VARCHAR(255) | 否     | 否   | 哈希密码                                 |
| email      | VARCHAR(100) | 否     | 否   | 用户电子邮件（用于注册/恢复）            |
| uname      | VARCHAR(100) | 是     | 否   | 姓名                                   |
| tel        | VARCHAR(20)  | 是     | 否   | 电话号码                               |
| sex        | VARCHAR(10)  | 是     | 否   | 性别（例如：“Male”、“Female”）        |
| bir        | DATE         | 是     | 否   | 出生日期                               |
| idcard     | VARCHAR(18)  | 是     | 否   | 身份证号码                             |
| address    | VARCHAR(255) | 是     | 否   | 家庭住址                               |
| dep        | VARCHAR(50)  | 是     | 否   | 科室                                   |
| lev        | VARCHAR(50)  | 是     | 否   | 级别/职称                              |
| avatar     | VARCHAR(255) | 是     | 否   | 用户头像路径                           |
| role       | VARCHAR(20)  | 否     | 否   | 用户角色：“NORMAL\_USER”、“ADMIN”、“SUPER\_ADMIN” |
| created\_at | TIMESTAMP    | 否     | 否   | 记录创建时间戳                         |
| updated\_at | TIMESTAMP    | 否     | 否   | 最后更新时间戳                         |

#### 6.2 `check_items` 表

`checkitem` 表存储健康检查项目标准。

| 字段名        | 类型         | 可空性 | 主键 | 说明                                   |
| :------------ | :----------- | :----- | :--- | :------------------------------------- |
| item\_id       | SERIAL       | 否     | 是   | 检查项 ID（自增）                      |
| item\_code     | VARCHAR(50)  | 否     | 否   | 检查项的唯一代码                       |
| item\_name     | VARCHAR(100) | 否     | 否   | 检查项名称                             |
| reference\_val | VARCHAR(255) | 是     | 否   | 项的参考值                             |
| unit          | VARCHAR(50)  | 是     | 否   | 测量单位                               |
| created\_by    | INTEGER      | 是     | 否   | 创建者用户 ID (FK to users.user\_id)      |
| created\_at    | TIMESTAMP    | 否     | 否   | 创建时间戳                             |
| updated\_at    | TIMESTAMP    | 否     | 否   | 最后更新时间戳                         |
| is\_active     | BOOLEAN      | 否     | 否   | 状态 (true 为活动，false 为非活动/已删除) |

#### 6.3 `check_groups` 表

`check_groups` 表存储健康检查组信息。

| 字段名        | 类型         | 可空性 | 主键 | 说明                                   |
| :------------ | :----------- | :----- | :--- | :------------------------------------- |
| group\_id      | SERIAL       | 否     | 是   | 检查组 ID（自增）                      |
| group\_code    | VARCHAR(50)  | 否     | 否   | 检查组唯一代码                         |
| group\_name    | VARCHAR(100) | 否     | 否   | 检查组名称                             |
| description   | TEXT         | 是     | 否   | 检查组描述                             |
| created\_by    | INTEGER      | 是     | 否   | 创建者用户 ID (FK to users.user\_id)      |
| created\_at    | TIMESTAMP    | 否     | 否   | 创建时间戳                             |
| updated\_at    | TIMESTAMP    | 否     | 否   | 最后更新时间戳                         |
| is\_active     | BOOLEAN      | 否     | 否   | 状态 (true 为活动，false 为非活动/已删除) |

#### 6.4 `group_check_item` 关联表

`group_check_item` 表存储检查组与检查项的关联关系 (多对多)。

| 字段名   | 类型    | 可空性 | 主键 | 说明                  |
| :------- | :------ | :----- | :--- | :-------------------- |
| group\_id | INTEGER | 否     | 是   | 检查组 ID (FK to check\_groups.group\_id) |
| item\_id  | INTEGER | 否     | 是   | 检查项 ID (FK to check\_items.item\_id) |

#### 6.5 `appointments` 表

`appointments` 表存储用户的体检预约信息。

| 字段名        | 类型         | 可空性 | 主键 | 说明                                   |
| :------------ | :----------- | :----- | :--- | :------------------------------------- |
| appointment\_id| SERIAL       | 否     | 是   | 预约 ID（自增）                        |
| user\_id       | INTEGER      | 否     | 否   | 预约用户 ID (FK to users.user\_id)        |
| group\_id      | INTEGER      | 否     | 否   | 预约检查组 ID (FK to check\_groups.group\_id) |
| appointment\_date | DATE      | 否     | 否   | 预约体检日期                           |
| appointment\_time | TIME      | 是     | 否   | 预约体检时间                           |
| examination\_method | VARCHAR(50) | 是     | 否   | 体检方式选择 (例如：上门、诊所)         |
| status        | VARCHAR(20)  | 否     | 否   | 预约状态 (例如：待确认、已确认、已完成、已取消) |
| created\_at    | TIMESTAMP    | 否     | 否   | 创建时间戳                             |
| updated\_at    | TIMESTAMP    | 否     | 否   | 最后更新时间戳                         |

#### 6.6 `examination_results` 表

`examination_results` 表存储体检结果。

| 字段名        | 类型         | 可空性 | 主键 | 说明                                   |
| :------------ | :----------- | :----- | :--- | :------------------------------------- |
| result\_id     | SERIAL       | 否     | 是   | 结果 ID（自增）                        |
| appointment\_id| INTEGER      | 否     | 否   | 对应预约 ID (FK to appointments.appointment\_id) |
| user\_id       | INTEGER      | 否     | 否   | 用户 ID (FK to users.user\_id)            |
| item\_id       | INTEGER      | 否     | 否   | 检查项 ID (FK to check\_items.item\_id)    |
| measured\_value| VARCHAR(255) | 否     | 否   | 实际测量值                             |
| result\_notes  | TEXT         | 是     | 否   | 结果备注/医生建议                      |
| recorded\_at   | TIMESTAMP    | 否     | 否   | 结果记录时间                           |

#### 6.7 `medical_history` 表

`medical_history` 表存储用户病史记录。

| 字段名        | 类型         | 可空性 | 主键 | 说明                                   |
| :------------ | :----------- | :----- | :--- | :------------------------------------- |
| history\_id    | SERIAL       | 否     | 是   | 病史 ID（自增）                        |
| user\_id       | INTEGER      | 否     | 否   | 用户 ID (FK to users.user\_id)            |
| diagnosis     | VARCHAR(255) | 否     | 否   | 诊断结果                               |
| doctor\_name   | VARCHAR(100) | 是     | 否   | 医生姓名                               |
| diagnosis\_date| DATE         | 否     | 否   | 诊断日期                               |
| treatment     | TEXT         | 是     | 否   | 治疗方案                               |
| notes         | TEXT         | 是     | 否   | 备注                                   |
| created\_at    | TIMESTAMP    | 否     | 否   | 创建时间戳                             |
| updated\_at    | TIMESTAMP    | 否     | 否   | 最后更新时间戳                         |

### 7\. 技术栈

* **编程语言**: Java 8+
* **GUI 框架**: Java Swing
* **外观库 (Look and Feel)**: **FlatLaf** (提供现代、扁平化的 UI 风格)
* **数据库**: PostgreSQL
* **ORM 框架**: Mybatis-Plus
* **构建工具**: **Gradle**
* **常用工具库**: **Hutool** (用于字符串处理、日期时间、加密、校验、邮件发送等常用功能)
* **依赖注入**: (可选，但推荐) Google Guice 或 Spring Framework 的轻量级 IoC 容器，用于管理 ViewModels 和 Services 的生命周期与依赖关系，进一步解耦。
* **日志**: SLF4J with Logback
* **邮件发送**: JavaMail API (作为 EmailService 的底层实现，Hutool 会封装其使用)

### 8\. 详细目录结构 (突出组件化)

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── healthsys
│   │   │           ├── HealthApp.java           // 主程序入口点
│   │   │           ├── config                   // 应用配置类，例如数据库连接池配置、Mybatis-Plus配置
│   │   │           │   └── DatabaseConfig.java
│   │   │           │   └── MybatisPlusConfig.java
│   │   │           ├── model                    // 模型层
│   │   │           │   ├── entity               // 数据库实体类 (POJOs)
│   │   │           │   │   ├── User.java
│   │   │           │   │   ├── CheckItem.java
│   │   │           │   │   ├── CheckGroup.java
│   │   │           │   │   ├── Appointment.java
│   │   │           │   │   ├── ExaminationResult.java
│   │   │           │   │   └── MedicalHistory.java
│   │   │           │   ├── enums                // 枚举类，例如 UserRoleEnum, CheckItemStatusEnum
│   │   │           │   │   └── UserRoleEnum.java
│   │   │           │   └── vo                   // View Object，用于封装 View 层需要展示的复杂数据
│   │   │           │       ├── UserHealthDataVO.java
│   │   │           │       └── CheckGroupVO.java // 包含关联检查项信息
│   │   │           ├── dao                      // 数据访问层 (Mybatis-Plus Mapper 接口)
│   │   │           │   ├── UserMapper.java
│   │   │           │   ├── CheckItemMapper.java
│   │   │           │   ├── CheckGroupMapper.java
│   │   │           │   ├── AppointmentMapper.java
│   │   │           │   ├── ExaminationResultMapper.java
│   │   │           │   └── MedicalHistoryMapper.java
│   │   │           ├── service                  // 业务逻辑层接口及实现
│   │   │           │   ├── IUserService.java
│   │   │           │   ├── impl
│   │   │           │   │   └── UserServiceImpl.java
│   │   │           │   ├── ICheckItemService.java
│   │   │           │   ├── impl
│   │   │           │   │   └── CheckItemServiceImpl.java
│   │   │           │   ├── ICheckGroupService.java
│   │   │           │   ├── impl
│   │   │           │   │   └── CheckGroupServiceImpl.java
│   │   │           │   ├── IAppointmentService.java
│   │   │           │   ├── impl
│   │   │           │   │   └── AppointmentServiceImpl.java
│   │   │           │   ├── IExaminationResultService.java
│   │   │           │   ├── impl
│   │   │           │   │   └── ExaminationResultServiceImpl.java
│   │   │           │   ├── IMedicalHistoryService.java
│   │   │           │   ├── impl
│   │   │           │   │   └── MedicalHistoryServiceImpl.java
│   │   │           │   └── EmailService.java    // 邮件发送服务，底层使用 JavaMail + Hutool封装
│   │   │           ├── view                     // Swing UI 视图层 (主页面和组件)
│   │   │           │   ├── MainFrame.java       // 主应用窗口，作为容器
│   │   │           │   ├── base                 // 基础视图类或通用抽象类
│   │   │           │   │   └── BasePanel.java
│   │   │           │   ├── common               // 通用UI组件
│   │   │           │   │   ├── HeaderComponent.java
│   │   │           │   │   ├── SidebarComponent.java
│   │   │           │   │   ├── NotificationComponent.java
│   │   │           │   │   └── PagingComponent.java
│   │   │           │   ├── auth                 // 认证相关视图
│   │   │           │   │   ├── AuthPanel.java         // 登录/注册主页面
│   │   │           │   │   ├── component
│   │   │           │   │   │   ├── LoginFormComponent.java    // 登录表单组件
│   │   │           │   │   │   └── RegistrationFormComponent.java // 注册表单组件
│   │   │           │   ├── admin                // 管理员功能视图
│   │   │           │   │   ├── checkitem            // 检查项管理相关视图
│   │   │           │   │   │   ├── CheckItemManagementPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── CheckItemTableComponent.java
│   │   │           │   │   │       └── CheckItemEditFormComponent.java
│   │   │           │   │   ├── checkgroup           // 检查组管理相关视图
│   │   │           │   │   │   ├── CheckGroupManagementPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── CheckGroupTableComponent.java
│   │   │           │   │   │       ├── CheckGroupEditFormComponent.java
│   │   │           │   │   │       └── CheckItemSelectorComponent.java
│   │   │           │   │   ├── healthdata           // 管理员健康数据管理相关视图
│   │   │           │   │   │   ├── AdminHealthDataPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       └── AdminHealthDataTableComponent.java
│   │   │           │   │   ├── usermanagement       // 用户管理相关视图
│   │   │           │   │   │   ├── UserManagementPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── UserTableComponent.java
│   │   │           │   │   │       └── UserEditFormComponent.java
│   │   │           │   ├── user                 // 普通用户功能视图
│   │   │           │   │   ├── healthdata           // 用户健康数据管理相关视图
│   │   │           │   │   │   ├── UserHealthDataPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── UserHealthDataTableComponent.java
│   │   │           │   │   │       └── UserHealthDataEditFormComponent.java
│   │   │           │   │   ├── appointment          // 预约体检相关视图
│   │   │           │   │   │   ├── AppointmentPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── AppointmentFormComponent.java
│   │   │           │   │   │       └── AppointmentHistoryComponent.java
│   │   │           │   │   ├── tracking             // 健康跟踪相关视图
│   │   │           │   │   │   ├── HealthTrackingPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── HealthComparisonChartComponent.java
│   │   │           │   │   │       └── MedicalHistoryListComponent.java
│   │   │           │   │   ├── analysis             // 体检结果分析相关视图
│   │   │           │   │   │   ├── ResultAnalysisPanel.java
│   │   │           │   │   │   └── component
│   │   │           │   │   │       ├── ResultDisplayComponent.java
│   │   │           │   │   │       └── AnalysisSuggestionComponent.java
│   │   │           │   ├── settings             // 设置页面视图
│   │   │           │   │   ├── SettingsPanel.java
│   │   │           │   │   └── component
│   │   │           │   │       └── LogoutButtonComponent.java
│   │   │           │   │       └── SystemSettingsFormComponent.java
│   │   │           ├── viewmodel                // ViewModel 层
│   │   │           │   ├── base                 // 基础 ViewModel 类或通用抽象类
│   │   │           │   │   └── BaseViewModel.java
│   │   │           │   ├── common               // 通用组件的 ViewModel
│   │   │           │   │   ├── HeaderViewModel.java
│   │   │           │   │   ├── SidebarViewModel.java
│   │   │           │   │   └── NotificationViewModel.java
│   │   │           │   ├── auth                 // 认证相关 ViewModel
│   │   │           │   │   ├── AuthViewModel.java
│   │   │           │   │   ├── LoginViewModel.java
│   │   │           │   │   └── RegistrationViewModel.java
│   │   │           │   ├── admin                // 管理员功能 ViewModel
│   │   │           │   │   ├── checkitem            // 检查项管理 ViewModel
│   │   │           │   │   │   ├── CheckItemManagementViewModel.java
│   │   │           │   │   │   └── CheckItemEditViewModel.java
│   │   │           │   │   ├── checkgroup           // 检查组管理 ViewModel
│   │   │           │   │   │   ├── CheckGroupManagementViewModel.java
│   │   │           │   │   │   └── CheckGroupEditViewModel.java
│   │   │           │   │   ├── healthdata           // 管理员健康数据管理 ViewModel
│   │   │           │   │   │   └── AdminHealthDataViewModel.java
│   │   │           │   │   ├── usermanagement       // 用户管理 ViewModel
│   │   │           │   │   │   ├── UserManagementViewModel.java
│   │   │           │   │   │   └── UserEditViewModel.java
│   │   │           │   ├── user                 // 普通用户功能 ViewModel
│   │   │           │   │   ├── healthdata           // 用户健康数据管理 ViewModel
│   │   │           │   │   │   └── UserHealthDataViewModel.java
│   │   │           │   │   ├── appointment          // 预约体检 ViewModel
│   │   │           │   │   │   └── AppointmentViewModel.java
│   │   │           │   │   ├── tracking             // 健康跟踪 ViewModel
│   │   │           │   │   │   └── HealthTrackingViewModel.java
│   │   │           │   │   ├── analysis             // 体检结果分析 ViewModel
│   │   │           │   │   │   └── ResultAnalysisViewModel.java
│   │   │           │   ├── settings             // 设置页面 ViewModel
│   │   │           │   │   └── SettingsViewModel.java
│   │   │           ├── util                     // 工具类
│   │   │           │   ├── PasswordUtil.java
│   │   │           │   ├── ValidationUtil.java
│   │   │           │   ├── GuiUtil.java
│   │   │           │   ├── DateUtil.java
│   │   │           │   └── CommonUtil.java
│   │   └── resources
│   │       ├── mybatis-config.xml
│   │       ├── application.properties           // 数据库连接、电子邮件服务配置等
│   │       ├── mapper                           // Mybatis-Plus Mapper XML 文件
│   │       │   ├── UserMapper.xml
│   │   │   │   ├── CheckItemMapper.xml
│   │   │   │   ├── CheckGroupMapper.xml
│   │   │   │   ├── AppointmentMapper.xml
│   │   │   │   ├── ExaminationResultMapper.xml
│   │   │   │   └── MedicalHistoryMapper.xml
│   │       └── images                           // UI 图片资源
│   └── test
│       └── java
│           └── com
│               └── healthsys
│                   └── ...                      // 单元测试
├── build.gradle                                 // Gradle 构建文件
└── settings.gradle                              // Gradle 项目设置文件 (如果未来拆分为多模块项目)
```

### 9\. 关键页面设计与交互 (详细组件拆分与交互流程)

#### 9.1 登录与注册页面 (`AuthPanel`)

**职责**: 作为登录和注册功能的入口。
**组件构成**:

* `LoginFormComponent` (View):

    * **UI 元素**: 用户名输入框 (`JTextField`)、密码输入框 (`JPasswordField`)、登录按钮 (`JButton`)。
    * **交互**: 用户输入用户名和密码。点击“登录”按钮。将输入传递给其 ViewModel。
    * **ViewModel**: `LoginViewModel`。
        * **属性**: `username` (ObservableString)、`password` (ObservableString)。
        * **命令**: `loginCommand` (绑定到登录按钮)。
        * **逻辑**: 调用 `UserService.login()` 方法。根据结果，通过事件或回调通知 `AuthPanel` 登录成功或失败，并显示提示。
        * **错误处理**: 验证用户名/密码格式，密码加密（使用 `PasswordUtil` 封装 Hutool `SecureUtil`），登录失败时返回错误信息。

* `RegistrationFormComponent` (View):

    * **UI 元素**: 邮箱输入框 (`JTextField`)、验证码输入框 (`JTextField`)、发送验证码按钮 (`JButton`)、用户名输入框 (`JTextField`)、密码输入框 (`JPasswordField`)、确认密码输入框 (`JPasswordField`)、注册按钮 (`JButton`)。
    * **交互**: 用户输入邮箱，点击“发送验证码”，启动倒计时。收到验证码后输入。输入用户名、密码、确认密码。点击“注册”按钮。将所有输入传递给其 ViewModel。
    * **ViewModel**: `RegistrationViewModel`。
        * **属性**: `email`、`verificationCode`、`username`、`password`、`confirmPassword`。
        * **命令**: `sendCodeCommand` (绑定到发送验证码按钮)、`registerCommand` (绑定到注册按钮)。
        * **逻辑**: `sendCodeCommand` 调用 `EmailService` 发送验证码（`EmailService` 内部使用 Hutool `MailUtil`）。`registerCommand` 调用 `UserService.register()` 进行注册。
        * **错误处理**: 校验邮箱格式（Hutool `Validator`），验证码是否正确，两次密码是否一致，用户名是否重复等。
    * **现代化交互**: 倒计时显示，输入校验实时反馈。

* **`AuthPanel` (主视图)**:

    * **UI 元素**: 容器面板，用于动态切换显示 `LoginFormComponent` 和 `RegistrationFormComponent`。
    * **交互**: 接收来自组件的事件（例如，登录成功、注册成功），并根据这些事件进行页面跳转或显示全局通知。
    * **ViewModel**: `AuthViewModel` (协调 `LoginViewModel` 和 `RegistrationViewModel` 的状态，控制组件的显示)。

#### 9.2 检查项管理页面 (`CheckItemManagementPanel`)

**职责**: 管理员对健康检查项进行 CRUD 操作。
**组件构成**:

* `CheckItemTableComponent` (View):
    * **UI 元素**: 检查项表格 (`JTable`)、名称搜索框、代号搜索框、查询按钮、添加/编辑/删除按钮。
    * **交互**:
        * 用户输入搜索关键词（名称或代号）。
        * [cite\_start]点击查询按钮更新表格 [cite: 73]。
        * 选择表格行。
        * [cite\_start]点击“添加”、“编辑”、“删除”按钮 [cite: 109, 110]。
    * **ViewModel**: `CheckItemManagementViewModel`。
        * **属性**: `checkItemList` (ObservableList\<CheckItem\>)、`searchName`、`searchCode`。
        * **命令**: `searchCommand`、`addCommand`、`editCommand`、`deleteCommand`。
        * [cite\_start]**逻辑**: `searchCommand` 调用 `CheckItemService.queryAllCheckItem()` 方法进行单条件或多条件查询 [cite: 86, 87][cite\_start]。`addCommand`、`editCommand`、`deleteCommand` 调用 `CheckItemService` 进行相应操作 [cite: 100, 119, 140]。
        * **错误处理**: 校验输入合法性，操作失败时提示。
* `CheckItemEditFormComponent` (View):
    * [cite\_start]**UI 元素**: 代号、名称、参考值、单位输入框，提交按钮 [cite: 99, 100]。
    * **交互**:
        * 用户填写或修改检查项信息。
        * [cite\_start]点击“提交”按钮 [cite: 99]。
    * **ViewModel**: `CheckItemEditViewModel`。
        * **属性**: `checkItem` (ObservableCheckItem)。
        * **命令**: `submitCommand`。
        * [cite\_start]**逻辑**: `submitCommand` 判断是新增还是修改。新增时调用 `CheckItemService.addCheckItem()`，需检查代号是否重复 [cite: 112, 131, 132][cite\_start]。修改时调用 `CheckItemService.updateCheckItem()` [cite: 125, 136]。
        * [cite\_start]**校验**: 字段非空和长度校验（Hutool `Validator` 辅助或 `SystemVerifier` 模式） [cite: 104]。

#### 9.3 检查组管理页面 (`CheckGroupManagementPanel`)

**职责**: 管理员对健康检查组进行 CRUD 操作，并管理检查组与检查项的关联。
**组件构成**:

* `CheckGroupTableComponent` (View):
    * **UI 元素**: 检查组表格 (`JTable`)、名称搜索框、代号搜索框、查询按钮、添加/编辑/删除按钮。
    * **交互**: 类似检查项表格组件。
    * **ViewModel**: `CheckGroupManagementViewModel`。
        * **属性**: `checkGroupList` (ObservableList\<CheckGroupVO\>)、`searchName`、`searchCode`。
        * **命令**: `searchCommand`、`addCommand`、`editCommand`、`deleteCommand`。
        * **逻辑**: 调用 `CheckGroupService` 进行查询、添加、编辑、删除。
* `CheckGroupEditFormComponent` (View):
    * **UI 元素**: 检查组代号、名称、描述输入框，提交按钮。
    * **交互**: 填写检查组基本信息。
    * **ViewModel**: `CheckGroupEditViewModel`。
        * **属性**: `checkGroup` (ObservableCheckGroup)。
        * **命令**: `submitCommand`。
        * **逻辑**: 调用 `CheckGroupService.addOrUpdateCheckGroup()`。
* `CheckItemSelectorComponent` (View): (复用组件)
    * **UI 元素**: 可选的检查项列表（`JList` 或 `JTable`），复选框 (`JCheckBox`)，用于选择要包含在检查组中的检查项。
    * **交互**: 用户勾选或取消勾选检查项。
    * **ViewModel**: `CheckItemSelectorViewModel`。
        * **属性**: `allCheckItems` (ObservableList\<CheckItem\>)、`selectedCheckItemIds` (ObservableSet\<Integer\>)。
        * **命令**: `loadCheckItemsCommand`。
        * **逻辑**: 提供可供选择的检查项列表，并管理当前已选的检查项。

#### 9.4 普通用户健康数据管理页面 (`UserHealthDataPanel`)

**职责**: 显示并允许普通用户管理自己的健康数据。
**组件构成**:

* `UserHealthDataTableComponent` (View):
    * **UI 元素**: 健康数据表格 (`JTable`)、搜索框 (`JTextField`)、筛选按钮 (`JButton`)、添加/编辑/删除按钮 (`JButton`)。
    * **交互**: 用户输入搜索关键词（例如检查项名称）。点击筛选按钮更新表格。选择表格行。点击“添加”、“编辑”、“删除”按钮。
    * **ViewModel**: `UserHealthDataViewModel`。
        * **属性**: `healthDataList` (ObservableList\<UserHealthDataVO\>)、`searchKeyword`。
        * **命令**: `searchCommand`、`addCommand`、`editCommand`、`deleteCommand`。
        * **逻辑**: `searchCommand` 调用 `UserHealthDataService.getHealthDataByUserIdAndKeyword()`。`addCommand`、`editCommand`、`deleteCommand` 调用 `UserHealthDataService` 进行相应操作。
        * **数据转换**: 将 `UserHealthData` 实体转换为 `UserHealthDataVO`，以便在表格中显示用户友好的信息（如检查项名称、用户姓名）。
* `UserHealthDataEditFormComponent` (View): (复用组件)
    * **UI 元素**: 检查项下拉框 (`JComboBox`，填充 `check_items` 中的名称)、值输入框、记录日期选择器、备注文本域、提交按钮。
    * **交互**: 用户填写或修改数据。点击“提交”按钮。
    * **ViewModel**: `UserHealthDataEditViewModel`。
        * **属性**: `selectedCheckItem`、`value`、`recordDate`、`notes`。
        * **命令**: `submitCommand`。
        * **逻辑**: `submitCommand` 调用 `UserHealthDataService.addOrUpdateHealthData()`。
        * **校验**: 确保输入值符合检查项的类型要求（例如，数值型）。

#### 9.5 管理员健康数据管理页面 (`AdminHealthDataPanel`)

**职责**: 允许管理员查看和管理所有用户的健康数据。
**组件构成**:

* `AdminHealthDataTableComponent` (View):
    * **UI 元素**: 类似 `UserHealthDataTableComponent`，但增加了用户选择器 (`JComboBox` 或 `JTextField` with auto-complete) 用于筛选特定用户的数据。
    * **交互**: 管理员选择用户，输入搜索关键词，进行筛选。其他 CRUD 操作与普通用户类似。
    * **ViewModel**: `AdminHealthDataViewModel`。
        * **属性**: `allHealthDataList`、`selectedUser`、`searchKeyword`。
        * **命令**: `searchCommand`、`addCommand`、`editCommand`、`deleteCommand`。
        * **逻辑**: 相比普通用户，查询时不再局限于当前登录用户ID，而是可以根据 `selectedUser` 查询。
* `UserHealthDataEditFormComponent` (View): (复用组件)
    * **UI 元素**: 与普通用户健康数据编辑组件相同。
    * **交互**: 相同。
    * **ViewModel**: `UserHealthDataEditViewModel` (复用)。

#### 9.6 预约体检页面 (`AppointmentPanel`)

**职责**: 用户预约体检，查看预约历史。
**组件构成**:

* `AppointmentFormComponent` (View):
    * **UI 元素**: 检查组选择器 (`JComboBox`)、体检日期选择器、体检时间选择器、体检方式选择（单选按钮或下拉框）、提交按钮。
    * **交互**: 用户选择检查组、日期、时间、方式，点击提交。
    * **ViewModel**: `AppointmentViewModel` (处理预约提交)。
        * **属性**: `selectedCheckGroup`、`appointmentDate`、`appointmentTime`、`examinationMethod`。
        * **命令**: `submitAppointmentCommand`。
        * **逻辑**: 调用 `AppointmentService.createAppointment()`。
* `AppointmentHistoryComponent` (View):
    * **UI 元素**: 预约历史表格 (`JTable`)，显示预约 ID、检查组、日期、时间、方式、状态等。
    * **交互**: 用户可以查看自己的预约历史。
    * **ViewModel**: `AppointmentViewModel` (处理历史数据加载)。
        * **属性**: `appointmentHistoryList` (ObservableList\<Appointment\>)。
        * **命令**: `loadAppointmentHistoryCommand`。
        * **逻辑**: 调用 `AppointmentService.getAppointmentsByUserId()`。

#### 9.7 体检结果分析与病史跟踪页面 (`HealthTrackingPanel` 和 `ResultAnalysisPanel`)

**职责**: 显示体检结果详情、提供结果分析和建议、进行用户病史对比与跟踪。
**组件构成**:

* `HealthComparisonChartComponent` (View):
    * **UI 元素**: 图表组件 (例如，使用 JFreeChart 库或自定义绘制)，用于可视化对比不同时间点的健康数据（如血糖、血压趋势）。
    * **交互**: 用户选择要对比的检查项和时间范围。
    * **ViewModel**: `HealthTrackingViewModel`。
        * **属性**: `selectedComparisonItem`、`comparisonData` (用于图表的数据)。
        * **命令**: `loadComparisonDataCommand`。
        * **逻辑**: 从 `ExaminationResultService` 获取历史数据并进行处理。
* `MedicalHistoryListComponent` (View):
    * **UI 元素**: 病史列表 (`JList` 或 `JTable`)，显示诊断日期、诊断结果、医生等。
    * **交互**: 用户查看自己的病史详情。
    * **ViewModel**: `HealthTrackingViewModel`。
        * **属性**: `medicalHistoryList` (ObservableList\<MedicalHistory\>)。
        * **命令**: `loadMedicalHistoryCommand`。
        * **逻辑**: 调用 `MedicalHistoryService.getMedicalHistoryByUserId()`。
* `ResultDisplayComponent` (View):
    * **UI 元素**: 用于显示单次体检结果详情的面板，包括检查项列表、测量值、参考值、医生建议等。
    * **交互**: 用户选择某个体检记录后，显示其详细结果。
    * **ViewModel**: `ResultAnalysisViewModel`。
        * **属性**: `selectedResult` (ObservableExaminationResult)。
        * **命令**: `loadResultDetailCommand`。
        * **逻辑**: 调用 `ExaminationResultService.getExaminationResultById()`。
* `AnalysisSuggestionComponent` (View):
    * **UI 元素**: 文本区域或标签，用于显示基于体检结果的健康分析和个性化建议。
    * **交互**: 自动根据 `ResultDisplayComponent` 中显示的结果进行分析和更新。
    * **ViewModel**: `ResultAnalysisViewModel` (可能通过分析 `selectedResult` 数据在内部生成建议)。
        * **属性**: `analysisText` (ObservableString)、`suggestionText` (ObservableString)。
        * **逻辑**: 根据 `ExaminationResult` 数据进行简单的健康评估逻辑，提供建议。

#### 9.8 管理员用户管理页面 (`UserManagementPanel`)

**职责**: 允许管理员对用户帐户执行基本 CRUD 操作，并允许超级管理员分配角色。
**组件构成**:

* `UserTableComponent` (View):
    * **UI 元素**: 用户列表表格 (`JTable`)、搜索框 (`JTextField`)、筛选按钮 (`JButton`)、添加/编辑/删除用户按钮 (`JButton`)。
    * **交互**:
        * 管理员输入搜索关键词（例如用户名、邮箱、角色）。
        * 点击筛选按钮更新表格。
        * 选择表格行。
        * 点击“添加”、“编辑”、“删除”按钮。
    * **ViewModel**: `UserManagementViewModel`。
        * **属性**: `userList` (ObservableList\<User\>)、`searchKeyword`。
        * **命令**: `searchCommand`、`addCommand`、`editCommand`、`deleteCommand`。
        * **逻辑**: 调用 `UserService` 进行用户查询、添加、编辑、删除。
        * **权限控制**: 在 `ViewModel` 层面判断当前登录用户角色，决定是否启用添加/编辑/删除用户按钮。
* `UserEditFormComponent` (View):
    * **UI 元素**: 用户名、密码、姓名、电话、性别、出生日期、身份证、地址、科室、职位、头像路径输入框，以及**角色选择下拉框** (`JComboBox`)。提交按钮。
    * **交互**:
        * 管理员填写或修改用户信息。
        * **角色选择框**: 根据当前登录用户角色，动态显示可用角色（管理员可设为普通用户/管理员，超级管理员可设为普通用户/管理员/超级管理员）。若当前用户不是超级管理员，角色选择框可能被禁用或隐藏。
        * 点击“提交”按钮。
    * **ViewModel**: `UserEditViewModel`。
        * **属性**: `user` (ObservableUser)、`availableRoles` (ObservableList\<String\>)。
        * **命令**: `submitCommand`。
        * **逻辑**: 调用 `UserService.addOrUpdateUser()`。
        * **权限校验**: 在保存时再次校验角色修改的权限，确保非超级管理员不能提升其他用户的权限或修改超级管理员的角色。密码加密使用 `PasswordUtil`。

### 10\. 交互设计原则

* **组件独立性**: 每个组件 (`JPanel`) 及其 ViewModel 应该尽可能独立，不直接引用其他组件的内部状态，而是通过事件或回调机制进行通信。
* **数据绑定**: ViewModel 的可观察属性 (`ObservableString`, `ObservableList` 等) 通过监听器直接驱动 View 的更新，避免手动同步 UI 状态。
* **命令模式**: 用户操作（如按钮点击）通过命令绑定到 ViewModel 中的方法，将 UI 行为与业务逻辑解耦。
* **清晰的反馈**: 所有操作，无论是成功、失败、加载中，都应通过 `NotificationComponent` 或组件内部的视觉效果提供明确的反馈。
* **表单验证**: 在表单组件 (`LoginFormComponent`, `RegistrationFormComponent` 等) 中集成前端验证，利用 Hutool `Validator` 提高用户体验。在 ViewModel 中进行二次业务验证。
* **错误处理**: 捕获所有潜在的运行时异常，并转化为用户可理解的错误消息，通过 `NotificationComponent` 显示。
* **导航**: 主 `MainFrame` 负责管理不同主视图(`JPanel`)之间的切换，ViewModel 负责发出导航请求。
* **现代化外观**: 通过集成 FlatLaf，系统将拥有统一、专业的扁平化设计，极大地提升用户体验。结合自定义图标、间距和排版，实现更具吸引力的界面。

### 11\. 提交要求与考核方法

**递交时间**:
[cite\_start]在设计完成之后书写设计说明书（实验报告），按规定报告的格式书写，在2025年7月5日前与设计相关文档（包括源代码）一并上交 [cite: 1]。

**考核方法**:
[cite\_start]项目演练实验（40%）+真实项目开发大作业报告和答辩（60%） [cite: 1]。

-----

**Gradle 构建文件 (`build.gradle` 示例):**

```gradle
plugins {
    id 'java'
}

group 'com.healthsys'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    // Java Swing 不需额外依赖，是 JDK 自带的

    // FlatLaf 外观库
    implementation 'com.formdev:flatlaf:3.4' // 请使用最新稳定版本
    // 如果需要 FlatLaf Extras (例如 FlatSVGIcon, FlatAnimatedIcon)
    // implementation 'com.formdev:flatlaf-extras:3.4'

    // PostgreSQL JDBC 驱动
    implementation 'org.postgresql:postgresql:42.7.3' // 请使用最新稳定版本

    // Mybatis-Plus 及其相关依赖
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.7' // 如果不使用SpringBoot，则需要引入mybatis-plus-core, mybatis-plus-extension, mybatis-plus-annotation
    implementation 'org.mybatis:mybatis:3.5.16'
    implementation 'org.mybatis:mybatis-spring:3.0.3'

    // Hutool 工具库
    implementation 'cn.hutool:hutool-all:5.8.27' // 请使用最新稳定版本

    // 日志
    implementation 'org.slf4j:slf4j-api:2.0.13' // 请使用最新稳定版本
    runtimeOnly 'ch.qos.logback:logback-classic:1.5.6' // 请使用最新稳定版本

    // JavaMail API (如果需要发送邮件)
    implementation 'com.sun.mail:jakarta.mail:2.0.1' // 请使用最新稳定版本

    // JUnit for testing
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.11.0' // 请使用最新稳定版本
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.11.0' // 请使用最新稳定版本

    // 可选：如果引入依赖注入框架 (例如 Guice)
    // implementation 'com.google.inject:guice:6.0.0'
}

test {
    useJUnitPlatform()
}

// 可选：配置 JAR 打包，包含所有依赖
jar {
    manifest {
        attributes 'Main-Class': 'com.healthsys.HealthApp' // 设置主类
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    // 防止重复文件
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```

**在 `HealthApp.java` (主入口类) 中初始化 FlatLaf：**

在应用程序启动时，即 `main` 方法中，需要设置 FlatLaf 作为默认的外观：

```java
package com.healthsys;

import com.formdev.flatlaf.FlatDarkLaf; // 或者 FlatLightLaf, FlatIntelliJLaf 等

import javax.swing.*;
import com.healthsys.view.MainFrame;

public class HealthApp {
    public static void main(String[] args) {
        // 设置 FlatLaf 外观
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf()); // 可以选择 FlatLightLaf, FlatIntelliJLaf 等
            // 或者：FlatLaf.setup(); // 默认是 FlatLightLaf
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.err.println("Failed to set FlatLaf LookAndFeel.");
        }

        // 确保 Swing UI 更新在事件调度线程中进行
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            // 可以在这里初始化并显示登录面板
            // 例如：mainFrame.setContent(new AuthPanel());
        });
    }
}
```