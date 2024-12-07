CREATE
    DATABASE IF NOT EXISTS programio_question;
USE
    programio_question;

-- 创建question_info表
CREATE TABLE IF NOT EXISTS question_info
(
    id             INT AUTO_INCREMENT PRIMARY KEY, -- 用户ID（这里可能是问题ID），自动递增并作为主键
    question_id    VARCHAR(6) UNIQUE,              -- 问题ID，唯一标识
    difficulty     VARCHAR(10)  NOT NULL,          -- 难度
    question       VARCHAR(200) NOT NULL UNIQUE,   -- 问题信息，不能为空且唯一
    time_request   INT DEFAULT 5000,               -- 时间要求
    memory_request INT DEFAULT 5000                -- 内存要求
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 插入测试数据到question_info表
INSERT INTO question_info (question_id, difficulty, question)
VALUES ('1', 'easy', '两数之和'),
       ('2', 'middle', '两数之差'),
       ('3', 'hard', '数组长度');

-- 创建question_info表
CREATE TABLE IF NOT EXISTS question_answer
(
    id          INT AUTO_INCREMENT PRIMARY KEY, -- 用户ID，自动递增并作为主键
    question_id INT(4),
    input_case  VARCHAR(100),                   -- 难度
    answer      VARCHAR(100)
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;


INSERT INTO question_answer (question_id, input_case, answer)
VALUES ('1', '2 4', '6'),
       ('1', '3 5', '8'),
       ('2', '10 4', '6'),
       ('3', '[1, 2, 3, 4]', '4'),
       ('1', '3 5', '8'),
       ('1', '3 5', '8'),
       ('1', '3 5', '8'),
       ('1', '3 5', '8');

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS programio_user;
USE programio_user;

-- 创建user_info表
CREATE TABLE IF NOT EXISTS user_info
(
    id           INT AUTO_INCREMENT PRIMARY KEY,                                   -- 用户ID，自动递增并作为主键
    user_name    VARCHAR(50)  NOT NULL UNIQUE,                                     -- 用户名，唯一且不为空
    user_account INT(6)       NOT NULL UNIQUE,
    email        VARCHAR(100) NOT NULL UNIQUE,                                     -- 电子邮件，唯一且不为空
    password     VARCHAR(255) NOT NULL,                                            -- 密码哈希值，不为空（存储哈希值而非明文密码）
    permission   VARCHAR(10) DEFAULT '0',                                          -- 用户权限，不为空，默认值为'0'
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,                            -- 记录创建时间
    updated_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录更新时间
) CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 插入测试数据到user_info表，并设置permission字段的值为'0'（或您希望的其他默认值）
INSERT INTO user_info (user_name, user_account, email, password, permission, created_at, updated_at)
VALUES ('john_doe', 111111, CONCAT('john.', SUBSTRING(MD5(RAND()), 1, 8), '@example.com'), MD5('securePassword123'),
        '0',
        NOW(), NOW()),
       ('jane_smith', 222222, CONCAT('jane.', SUBSTRING(MD5(RAND()), 1, 8), '@test.com'), MD5('anotherSecurePwd'), '0',
        NOW(), NOW()),
       ('alice_jones', 333333, CONCAT('alice.', SUBSTRING(MD5(RAND()), 1, 8), '@sample.org'), MD5('mySuperSecretPwd'),
        '0', NOW(), NOW()),
       ('bob_brown', 444444, CONCAT('bob.', SUBSTRING(MD5(RAND()), 1, 8), '@domain.net'), MD5('pwd4Life'), '0', NOW(),
        NOW()),
       ('charlie_black', 555555, CONCAT('charlie.', SUBSTRING(MD5(RAND()), 1, 8), '@webmail.com'),
        MD5('easyToRememberPwd'), '0', NOW(), NOW());

CREATE TABLE user_token
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_account INT(6)       NOT NULL,
    permission   VARCHAR(10)  NOT NULL,
    token        VARCHAR(500) NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at   TIMESTAMP,
    FOREIGN KEY (user_account) REFERENCES user_info (user_account) -- 保证 user_id 指向有效的用户
) CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


CREATE DATABASE IF NOT EXISTS programio_submit;
USE programio_submit;
-- 创建question_info表
CREATE TABLE IF NOT EXISTS submit_info
(
    id            INT AUTO_INCREMENT PRIMARY KEY,                                 -- 用户ID（这里可能是问题ID），自动递增并作为主键
    user_account  INT(6)        NOT NULL,
    question_id   VARCHAR(4)    NOT NULL,                                         -- 问题ID，唯一标识
    language_type VARCHAR(10)   NOT NULL,
    code          VARCHAR(1000) NOT NULL,                                         -- 难度
    accuracy      FLOAT         NOT NULL,
    running_time  FLOAT         NOT NULL,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录更新时间
) CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

