好的，遵照您的要求，这里是一份根据您提供的《健康管理系统项目架构文档》编写的、极其详细的、便于AI逐步执行的项目搭建TODOLIST。

此TODOLIST将严格遵循文档中定义的 **MVVM架构**、**组件化设计**、**技术栈** 和 **目录结构**。每一步都将是具体、可执行的任务。

---

## **健康管理系统 - AI项目执行TODOLIST**

### **阶段一：项目初始化与环境搭建 (Phase 1: Project Initialization & Environment Setup)**

**目标**: 搭建项目骨架，配置构建工具和核心依赖，确保基础环境就绪。

* **任务 1.1: 初始化Gradle项目**
    * [x] 创建一个新的Java项目。
    * [x] 初始化为Gradle项目，生成 `build.gradle` 和 `settings.gradle` 文件。
    * [x] 在 `build.gradle` 中设置 `group 'com.healthsys'` 和 `version '1.0-SNAPSHOT'`。

* **任务 1.2: 配置 `build.gradle` 依赖**
    * [x] 添加 `mavenCentral()` 作为仓库。
    * [x] 参照文档第7节 (技术栈) 和 `build.gradle` 示例，添加以下核心依赖：
        * [x] **GUI**: `com.formdev:flatlaf` (现代外观库)
        * [x] **数据库**: `org.postgresql:postgresql` (PostgreSQL JDBC驱动)
        * [x] **ORM**: `com.baomidou:mybatis-plus-core`, `mybatis-plus-extension`, `mybatis-plus-annotation` (或对应非SpringBoot版本依赖), `org.mybatis:mybatis`
        * [x] **工具库**: `cn.hutool:hutool-all`
        * [x] **日志**: `org.slf4j:slf4j-api` 和 `ch.qos.logback:logback-classic`
        * [x] **邮件**: `com.sun.mail:jakarta.mail`
        * [x] **测试**: `org.junit.jupiter:junit-jupiter-api` 和 `junit-jupiter-engine`
    * [x] 配置 `jar` 任务，指定主类 `com.healthsys.HealthApp` 并设置打包策略，如示例所示。

* **任务 1.3: 创建项目目录结构**
    * [x] 严格按照文档第8节定义的目录结构，在 `src/main/java/com/healthsys/` 下创建所有包 (package)，例如：`config`, `model`, `dao`, `service`, `view`, `viewmodel`, `util` 等。
    * [x] 在 `src/main/resources` 下创建 `mapper`, `images` 目录，并创建 `application.properties` 和 `mybatis-config.xml` 文件。

* **任务 1.4: 数据库环境准备**
    * [x] 启动PostgreSQL数据库服务。
    * [x] 创建一个名为 `health_management_system` (或自定义) 的新数据库。
    * [x] 参照文档第6节，依次执行SQL `CREATE TABLE` 语句，创建以下所有表：
        * [x] `users`
        * [x] `check_items`
        * [x] `check_groups`
        * [x] `group_check_item`
        * [x] `appointments`
        * [x] `examination_results`
        * [x] `medical_history`
    * [x] 确保所有字段、类型、约束（主键、外键、非空）与文档完全一致。

* **任务 1.5: 应用入口与基础配置**
    * [x] 在 `com.healthsys.config` 包中创建 `DatabaseConfig.java` 和 `MybatisPlusConfig.java`。
        * [x] `DatabaseConfig`: 负责创建数据源 (DataSource)，连接信息从 `application.properties` 读取。
        * [x] `MybatisPlusConfig`: 配置Mybatis-Plus，如扫描Mapper接口、分页插件等。
    * [x] 在 `application.properties` 文件中配置数据库连接信息（URL, username, password）和邮件服务信息。
    * [x] 在 `com.healthsys` 包中创建主入口类 `HealthApp.java`。
    * [x] 在 `HealthApp.main` 方法中，按照文档末尾的示例，添加代码以初始化并设置 **FlatLaf** 外观 (`UIManager.setLookAndFeel(new FlatDarkLaf());`)。
    * [x] 在 `com.healthsys.view` 包中创建主窗口类 `MainFrame.java` (继承自 `JFrame`)。
    * [x] 在 `HealthApp.main` 方法的 `SwingUtilities.invokeLater` 中，实例化并显示 `MainFrame`。

### **阶段二：模型与数据访问层开发 (Phase 2: Model & DAO Layer)**

**目标**: 创建与数据库表对应的实体类和Mybatis-Plus Mapper接口。

* **任务 2.1: 创建实体类 (POJOs)**
    * [x] 在 `com.healthsys.model.entity` 包中，为每一个数据库表创建一个Java实体类。
    * [x] `User.java`
    * [x] `CheckItem.java`
    * [x] `CheckGroup.java`
    * [x] `Appointment.java`
    * [x] `ExaminationResult.java`
    * [x] `MedicalHistory.java`
    * [x] 使用Mybatis-Plus注解 (`@TableName`, `@TableId`, `@TableField`) 映射类和字段到数据库表和列。

* **任务 2.2: 创建枚举类**
    * [x] 在 `com.healthsys.model.enums` 包中创建 `UserRoleEnum.java`，包含 `NORMAL_USER`, `ADMIN`, `SUPER_ADMIN`。

* **任务 2.3: 创建数据访问接口 (Mappers)**
    * [x] 在 `com.healthsys.dao` 包中，为每个实体类创建一个Mapper接口，并继承 `BaseMapper<EntityType>`。
    * [x] `UserMapper.java`
    * [x] `CheckItemMapper.java`
    * [x] `CheckGroupMapper.java`
    * [x] `AppointmentMapper.java`
    * [x] `ExaminationResultMapper.java`
    * [x] `MedicalHistoryMapper.java`
    * [x] 在 `MybatisPlusConfig` 中或使用 `@MapperScan` 注解确保这些Mapper被扫描到。

* **任务 2.4: 创建工具类**
    * [x] 在 `com.healthsys.util` 包中创建以下工具类：
        * [x] `PasswordUtil.java`: 封装Hutool的 `SecureUtil`，提供密码哈希和验证的方法。
        * [x] `ValidationUtil.java`: 封装Hutool的 `Validator`，提供常用的数据校验方法（如邮箱、非空）。
        * [x] `GuiUtil.java`: Swing界面相关工具方法。
        * [x] `DateUtil.java`: 日期处理工具（基于Hutool）。
        * [x] `CommonUtil.java`: 通用工具方法。

### **阶段三：认证模块开发 (Phase 3: Authentication Module)**

**目标**: 实现一个完整的用户注册和登录流程，作为项目第一个端到端的垂直功能切片。

* **任务 3.1: 认证服务层 (Service)**
    * [ ] 在 `com.healthsys.service` 包创建 `IUserService.java` 接口，定义 `register`, `login` 等方法。
    * [ ] 创建 `EmailService.java` 接口，定义 `sendVerificationCode` 方法。
    * [ ] 在 `com.healthsys.service.impl` 包创建 `UserServiceImpl.java` 和 `EmailServiceImpl.java`。
        * [ ] `UserServiceImpl`: 实现 `register` (包含密码加密) 和 `login` (包含密码验证) 逻辑，与 `UserMapper` 交互。
        * [ ] `EmailServiceImpl`: 使用Hutool的 `MailUtil` 实现邮件发送逻辑，配置从 `application.properties` 读取。

* **任务 3.2: 认证ViewModel层**
    * [ ] 在 `com.healthsys.viewmodel.auth` 包创建以下ViewModel:
        * [ ] `LoginViewModel.java`: 包含 `username`, `password` 的可观察属性和 `loginCommand`。`loginCommand` 调用 `IUserService.login()`。
        * [ ] `RegistrationViewModel.java`: 包含 `email`, `verificationCode`, `username`, `password` 等可观察属性，以及 `sendCodeCommand` 和 `registerCommand`。分别调用 `EmailService` 和 `IUserService`。
        * [ ] `AuthViewModel.java`: 用于协调 `LoginViewModel` 和 `RegistrationViewModel` 的状态，控制UI显示哪个组件。

* **任务 3.3: 认证视图层 (View)**
    * [ ] 在 `com.healthsys.view.auth.component` 包创建组件：
        * [ ] `LoginFormComponent.java` (`JPanel`): 包含用户名/密码输入框和登录按钮。将UI事件绑定到 `LoginViewModel` 的命令和属性。
        * [ ] `RegistrationFormComponent.java` (`JPanel`): 包含所有注册所需UI元素。将UI事件绑定到 `RegistrationViewModel` 的命令和属性。实现发送验证码按钮的倒计时功能。
    * [ ] 在 `com.healthsys.view.auth` 包创建主视图：
        * [ ] `AuthPanel.java` (`JPanel`): 作为容器，根据 `AuthViewModel` 的状态，动态添加和显示 `LoginFormComponent` 或 `RegistrationFormComponent`。
    * [ ] 在 `MainFrame` 中，初始时将 `AuthPanel` 设置为其内容面板。

### **阶段四：后台管理模块开发 (Phase 4: Admin Modules)**

**目标**: 为管理员构建检查项、检查组和用户管理的核心CRUD功能。

* **任务 4.1: 通用组件开发**
    * [ ] 在 `com.healthsys.view.common` 创建通用UI组件：
        * [ ] `NotificationComponent.java`: 用于显示全局成功/失败/信息提示。
        * [ ] `PagingComponent.java`: 用于表格的分页功能。
    * [ ] 实现其对应的ViewModel（如果需要）。

* **任务 4.2: 检查项管理模块 (`CheckItemManagement`)**
    * [ ] **Service**: 在 `ICheckItemService` 和 `CheckItemServiceImpl` 中实现 `add`, `delete`, `update`, `query` (支持分页和搜索) 方法。
    * [ ] **ViewModel**:
        * [ ] `CheckItemManagementViewModel.java`: 管理检查项列表 (`ObservableList`)、搜索条件、分页信息，并包含 `search`, `add`, `edit`, `delete` 命令。
        * [ ] `CheckItemEditViewModel.java`: 管理单个检查项实体，用于新建或编辑，并包含 `submit` 命令。
    * [ ] **View**:
        * [ ] `CheckItemTableComponent.java`: 使用 `JTable` 显示数据，包含搜索框和操作按钮。与 `CheckItemManagementViewModel` 绑定。
        * [ ] `CheckItemEditFormComponent.java`: 一个表单 `JPanel`，用于输入检查项信息。与 `CheckItemEditViewModel` 绑定。
        * [ ] `CheckItemManagementPanel.java`: 组合 `CheckItemTableComponent` 和（在对话框中弹出的）`CheckItemEditFormComponent`，构成完整页面。

* **任务 4.3: 检查组管理模块 (`CheckGroupManagement`)**
    * [ ] **Service**: 在 `ICheckGroupService` 和 `CheckGroupServiceImpl` 中实现 `add`, `delete`, `update`, `query` 方法。注意处理与 `group_check_item` 关联表的事务性操作。
    * [ ] **ViewModel**:
        * [ ] `CheckGroupManagementViewModel.java`: 类似于检查项的管理VM。
        * [ ] `CheckGroupEditViewModel.java`: 管理检查组实体和其关联的检查项ID列表。
    * [ ] **View**:
        * [ ] `CheckGroupTableComponent.java`: 显示检查组列表。
        * [ ] `CheckItemSelectorComponent.java` (可复用): 一个带有复选框的检查项列表，用于在创建/编辑检查组时选择检查项。
        * [ ] `CheckGroupEditFormComponent.java`: 包含基本信息输入和 `CheckItemSelectorComponent`。
        * [ ] `CheckGroupManagementPanel.java`: 组合以上组件。

* **任务 4.4: 用户管理模块 (`UserManagement`)**
    * [ ] **Service**: 在 `IUserService` 和 `UserServiceImpl` 中添加 `addOrUpdateUser`, `deleteUser`, `queryUsers` (支持分页和搜索) 方法。
    * [ ] **ViewModel**:
        * [ ] `UserManagementViewModel.java`: 管理用户列表和搜索/删除操作。需要加入权限判断逻辑，确定按钮是否可用。
        * [ ] `UserEditViewModel.java`: 管理单个用户实体。逻辑需要判断当前登录用户是否有权修改角色，并动态生成可选角色列表。
    * [ ] **View**:
        * [ ] `UserTableComponent.java`: 显示用户列表。
        * [ ] `UserEditFormComponent.java`: 包含用户所有字段的输入，特别是角色选择的 `JComboBox`。根据VM的权限判断结果，启用/禁用角色选择框。
        * [ ] `UserManagementPanel.java`: 组合以上组件。

### **阶段五：普通用户功能模块开发 (Phase 5: User-Facing Modules)**

**目标**: 实现普通用户交互的核心功能，如预约、结果查看和健康跟踪。

* **任务 5.1: 预约与跟踪模块 (`Appointment & Tracking`)**
    * [ ] **Service**: 创建 `IAppointmentService`, `IMedicalHistoryService` 及其实现，定义创建预约、查询历史、查询病史等方法。
    * [ ] **ViewModel**:
        * [ ] `AppointmentViewModel.java`: 管理预约表单数据、预约历史列表，并提供 `submitAppointment` 和 `loadHistory` 命令。
        * [ ] `HealthTrackingViewModel.java`: 管理用于图表的数据、病史列表，并提供加载命令。
    * [ ] **View**:
        * [ ] `AppointmentPanel.java`: 组合以下组件。
        * [ ] `AppointmentFormComponent.java`: 包含检查组下拉框、日期选择器等，用于提交新预约。
        * [ ] `AppointmentHistoryComponent.java`: 使用 `JTable` 显示用户的预约历史。
        * [ ] `HealthTrackingPanel.java`: 组合以下组件。
        * [ ] `HealthComparisonChartComponent.java`: 集成一个图表库（如JFreeChart），根据VM提供的数据动态生成并显示健康数据趋势图。
        * [ ] `MedicalHistoryListComponent.java`: 显示用户的病史记录。

* **任务 5.2: 体检结果分析模块 (`Result Analysis`)**
    * [ ] **Service**: 创建 `IExaminationResultService` 及其实现，定义查询用户详细体检结果的方法。
    * [ ] **ViewModel**:
        * [ ] `ResultAnalysisViewModel.java`: 管理选定的体检结果详情，并包含生成分析和建议的逻辑。
    * [ ] **View**:
        * [ ] `ResultAnalysisPanel.java`: 组合以下组件。
        * [ ] `ResultDisplayComponent.java`: 详细展示单次体检的所有项目、测量值、参考值和备注。
        * [ ] `AnalysisSuggestionComponent.java`: 显示由VM生成的健康分析和建议文本。

* **任务 5.3: 用户健康数据管理模块 (`UserHealthData`)**
    * [ ] **Service**: `IExaminationResultService` 中添加用户自主录入/修改/删除健康数据的方法。
    * [ ] **ViewModel**: `UserHealthDataViewModel`, `UserHealthDataEditViewModel`。
    * [ ] **View**: `UserHealthDataPanel`, `UserHealthDataTableComponent`, `UserHealthDataEditFormComponent` (可复用)。实现与管理员模块类似的CRUD界面，但仅限于当前登录用户的数据。

### **阶段六：系统整合与完善 (Phase 6: Integration & Refinement)**

**目标**: 将所有模块整合到主框架中，完善导航、权限控制和用户体验。

* **任务 6.1: 主框架与导航**
    * [ ] 在 `com.healthsys.view.common` 创建通用布局组件：
        * [ ] `HeaderComponent.java`: 顶部标题栏，可显示用户名。
        * [ ] `SidebarComponent.java`: 左侧导航栏，用按钮或列表显示可访问的模块。
    * [ ] 在 `MainFrame.java` 中，实现登录成功后的主界面布局（例如 `BorderLayout`），将 `HeaderComponent` 置于顶部，`SidebarComponent` 置于左侧，主内容面板置于中央。
    * [ ] 实现 `SidebarComponent` 的导航逻辑：点击不同按钮时，通知 `MainFrame` 在中央区域切换显示不同的主面板（如 `CheckItemManagementPanel`, `AppointmentPanel` 等）。

* **任务 6.2: 权限控制**
    * [ ] 在用户成功登录后，将用户信息（特别是角色 `UserRoleEnum`）保存在一个全局可访问的位置（例如一个单例的 `AppContext` 类）。
    * [ ] `SidebarComponent` 的ViewModel需要根据当前用户的角色，动态决定显示哪些导航项。
    * [ ] 在各个ViewModel中（尤其是管理模块），添加对用户角色的检查，以控制具体操作（如删除、修改角色）的可用性。

* **任务 6.3: UI/UX 优化**
    * [ ] 全面检查所有视图，确保布局、间距、字体、颜色符合 **FlatLaf** 的现代风格。
    * [ ] 为所有按钮和可交互元素添加图标（可以放在 `src/main/resources/images`）。
    * [ ] 确保所有耗时操作（数据库查询、网络请求）都在后台线程 (`SwingWorker` 或 `ExecutorService`) 中执行，避免UI冻结，并显示加载状态。
    * [ ] 完善所有表单的输入校验和友好的错误提示。

### **阶段七：测试、打包与交付 (Phase 7: Testing, Packaging & Delivery)**

**目标**: 确保项目质量，完成最终打包，准备答辩和交付。

* **任务 7.1: 单元测试**
    * [ ] 在 `src/test/java` 目录下，为关键业务逻辑编写JUnit单元测试。
    * [ ] 测试 `UserService` 的登录/注册逻辑。
    * [ ] 测试 `PasswordUtil` 的加密/验证功能。
    * [ ] 测试各个Service层的CRUD操作（可结合内存数据库如H2，或直接测试与开发库的交互）。
    * [ ] 测试各个ViewModel的逻辑。

* **任务 7.2: 集成与手动测试**
    * [ ] 运行整个应用程序，手动测试所有功能点。
    * [ ] 以**超级管理员**身份登录，测试所有管理功能，包括用户角色分配。
    * [ ] 以**管理员**身份登录，测试其权限范围内的所有功能。
    * [ ] 以**普通用户**身份登录，测试所有用户功能。
    * [ ] 重点测试多用户数据隔离、权限边界和所有交互流程的正确性。

* **任务 7.3: 项目打包**
    * [ ] 运行 Gradle 的 `jar` 或 `build` 任务，生成一个包含所有依赖的可执行JAR文件。
    * [ ] 测试该JAR包在未安装IDE的环境下是否能正常启动和运行。

* **任务 7.4: 文档撰写与提交**
    * [ ] 撰写最终的设计说明书（实验报告）。
    * [ ] 整理项目源代码，确保注释清晰，格式规范。
    * [ ] 按照要求，在 **2025年7月5日** 前，将源代码和所有相关文档一并上交。
    * [ ] 准备项目演练和答辩。

---