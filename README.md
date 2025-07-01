# 🎓 在线选课系统 (Online Course Selection System)

这是一个基于 **Spring Boot + Thymeleaf** 技术栈实现的简洁高效的在线选课系统。项目模拟了大学选课场景，包含了管理员、教师和学生三种核心角色，实现了课程管理、选课管理等核心功能。

## ✨ 项目简介

本系统旨在为高校提供一个简单、易用的在线选课平台。通过该平台：
*   **学生** 可以方便地浏览课程信息、在线选择和退选课程并查看自己的选课结果。
*   **教师** 可以创建和管理自己所授课程，并查看选修该课程的学生名单。
*   **管理员** 拥有最高权限，负责维护系统的基础数据，包括管理所有用户信息和课程信息。

## 🚀 主要功能

系统根据不同角色划分了不同的功能模块：

### 1. 管理员 (Administrator)
-   **登录/注销**: 安全地登录和退出系统。
-   **用户管理**:
    -   添加、删除、修改、查询 **教师** 信息。
    -   添加、删除、修改、查询 **学生** 信息。
-   **课程管理**:
    -   发布新课程，并指定授课教师。
    -   修改、删除现有课程。
    -   查看所有课程列表及其详情。

### 2. 教师 (Teacher)
-   **登录/注销**: 安全地登录和退出系统。
-   **课程管理**: 创建并发布本人教授课程。
-   **课程查看**: 查看个人教授的所有课程列表。
-   **学生名单**: 查看选修了自己课程的学生名单。
-   **个人信息**: 查看和修改个人信息。

### 3. 学生 (Student)
-   **登录/注销**: 安全地登录和退出系统。
-   **课程浏览**: 浏览开设的所有课程列表。
-   **在线选课**: 在可选课程列表中选择自己想修的课程。
-   **退选课程**: 对已选课程进行退选操作。
-   **我的课程**: 查看自己已经成功选择的课程列表。
-   **个人信息**: 查看和修改个人信息。

## 🛠️ 技术栈

-   **核心框架**: Spring Boot
-   **视图模板**: Thymeleaf
-   **数据持久层**: MyBatis 
-   **数据库**: MySQL
-   **构建工具**: Maven
-   **前端**: HTML, CSS

## 📦 环境搭建与启动

请按照以下步骤在本地运行此项目。

### 1. 环境要求
-   JDK 1.8 或更高版本
-   Maven 3.6 或更高版本
-   MySQL 5.7 或更高版本

### 2. 克隆项目
```bash
git clone https://github.com/theDalles/online-course-selection-system
cd your-repo-name
```

### 3. 数据库配置
1.  在 MySQL 中创建一个新的数据库，例如 `db_course_selection`。
    ```sql
    CREATE DATABASE db_course_selection CHARACTER SET utf8mb4;
    ```
2.  找到项目resources目录下的 SQL 脚本文件course_selection_system，并将其导入到刚创建的数据库中，以初始化表结构和基础数据。
3.  修改配置文件 `application.properties` ，更新数据库连接信息。

    ```properties
    # application.properties
    spring.datasource.url=jdbc:mysql://localhost:3306/course_selection_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    spring.datasource.username=root(your_mysql_username)
    spring.datasource.password=000000(your_mysql_password)
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```

### 4. 运行项目
您可以通过以下两种方式启动项目：

-   **使用 IDE**: 直接在 IntelliJ IDEA 或 Eclipse 中打开项目，找到主启动类 `com/wangshangxuankexitong/WangshangxuankexitongApplication.java` ，右键点击并选择 "Run" 即可。
-   **使用 Maven 命令**: 在项目根目录下打开终端，执行以下命令：
    ```bash
    mvn spring-boot:run
    ```

### 5. 访问系统
项目启动成功后，在浏览器中访问 `http://localhost:8080`。
