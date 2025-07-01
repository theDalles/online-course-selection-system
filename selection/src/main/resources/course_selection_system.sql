/*
 Navicat Premium Dump SQL

 Source Server         : Mysql
 Source Server Type    : MySQL
 Source Server Version : 80404 (8.4.4)
 Source Host           : localhost:3306
 Source Schema         : course_selection_system

 Target Server Type    : MySQL
 Target Server Version : 80404 (8.4.4)
 File Encoding         : 65001

 Date: 16/06/2025 20:31:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `cno` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程号',
  `cname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `credit` decimal(3, 1) NOT NULL COMMENT '学分',
  `tno_fk` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '授课教师号 (外键)',
  PRIMARY KEY (`cno`) USING BTREE,
  INDEX `fk_course_teacher`(`tno_fk` ASC) USING BTREE,
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`tno_fk`) REFERENCES `teacher` (`tno`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES ('CS101', '数据库原理', 3.0, 'T001');
INSERT INTO `course` VALUES ('CS102', '操作系统', 4.0, 'T001');
INSERT INTO `course` VALUES ('SE201', 'Java程序设计', 3.5, 'T002');

-- ----------------------------
-- Table structure for selection
-- ----------------------------
DROP TABLE IF EXISTS `selection`;
CREATE TABLE `selection`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `sno_fk` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选课学生学号 (外键)',
  `cno_fk` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所选课程号 (外键)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_selection`(`sno_fk` ASC, `cno_fk` ASC) USING BTREE,
  INDEX `fk_selection_course`(`cno_fk` ASC) USING BTREE,
  CONSTRAINT `fk_selection_course` FOREIGN KEY (`cno_fk`) REFERENCES `course` (`cno`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_selection_student` FOREIGN KEY (`sno_fk`) REFERENCES `student` (`sno`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生选课记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of selection
-- ----------------------------
INSERT INTO `selection` VALUES (1, 'S2024001', 'CS101');
INSERT INTO `selection` VALUES (2, 'S2024001', 'SE201');
INSERT INTO `selection` VALUES (3, 'S2024002', 'CS101');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `sno` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `sname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学生姓名',
  `sage` int NULL DEFAULT NULL COMMENT '年龄',
  `sgender` enum('男','女','未知') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '未知' COMMENT '性别',
  `sdept` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属院系',
  PRIMARY KEY (`sno`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('2206050114', '蒋海泽', 22, '男', '计算机');
INSERT INTO `student` VALUES ('S2024001', '李明', 20, '男', '计算机科学系');
INSERT INTO `student` VALUES ('S2024002', '王红', 19, '女', '软件工程系');
INSERT INTO `student` VALUES ('S2024003', '赵强', 21, '男', '计算机科学系');

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `tno` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师号',
  `tname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '教师姓名',
  `tdept` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属院系',
  PRIMARY KEY (`tno`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教师信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES ('12333', '王二', '计算机');
INSERT INTO `teacher` VALUES ('T001', '张海峰', '计算机科学系');
INSERT INTO `teacher` VALUES ('T002', '林雪', '软件工程系');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码 (警告: 生产环境必须加密存储!)',
  `role` enum('admin','student','teacher') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_UNIQUE`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户登录认证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin123', 'admin');
INSERT INTO `user` VALUES (2, 'liming', 'pass123', 'student');
INSERT INTO `user` VALUES (3, 'wanghong', 'pass456', 'student');
INSERT INTO `user` VALUES (4, 'zhanghf', 'teacherpass', 'teacher');
INSERT INTO `user` VALUES (5, '2206050114', '123123', 'student');
INSERT INTO `user` VALUES (6, '12333', '123123', 'teacher');

SET FOREIGN_KEY_CHECKS = 1;
