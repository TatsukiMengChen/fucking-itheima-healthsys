plugins {
    id("java")
    id("application")
}

group = "com.healthsys"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // FlatLaf 外观库 - 现代化UI
    implementation("com.formdev:flatlaf:3.4")
    
    // PostgreSQL JDBC 驱动
    implementation("org.postgresql:postgresql:42.7.3")
    
    // Mybatis-Plus 及其相关依赖
    implementation("com.baomidou:mybatis-plus-core:3.5.7")
    implementation("com.baomidou:mybatis-plus-extension:3.5.7")
    implementation("com.baomidou:mybatis-plus-annotation:3.5.7")
    implementation("org.mybatis:mybatis:3.5.16")
    implementation("org.mybatis:mybatis-spring:3.0.3")
    
    // Hutool 工具库
    implementation("cn.hutool:hutool-all:5.8.27")
    
    // 日志
    implementation("org.slf4j:slf4j-api:2.0.13")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.6")
    
    // JavaMail API (邮件发送)
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    
    // 连接池 - HikariCP (for database connection pooling)
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Lombok - 简化实体类代码
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // JUnit for testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
}

// 配置应用程序主类
application {
    mainClass.set("com.healthsys.HealthApp")
}

// 配置测试
tasks.test {
    useJUnitPlatform()
}

// 配置JAR打包，包含所有依赖
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.healthsys.HealthApp"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}