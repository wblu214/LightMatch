-- 用户表
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    imageUrl     varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别0女性1男性',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '手机',
    email        varchar(512)                       null comment '邮箱',
    status       tinyint  default 0                 not null comment '0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除（1为已删除0为未删除）',
    userRole     tinyint  default 0                 not null comment '角色 0-普通用户 1-管理员',
    tags         varchar(1024)                      null comment '标签列表',
    code         varchar(512)                       null comment '编号',
    profile      varchar(512)                       null comment '个人简介'
)
    comment '用户表';

INSERT INTO yupao.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code) VALUES ('张三丰', 1, '7788455', '', 0, '123456', '14574587458', '4848494@qq.com', 0, '2024-07-04 06:04:20', '2024-07-04 07:33:45', 0, 0, null, null);
INSERT INTO yupao.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code) VALUES ('张三丰', 2, '7788455', '', 0, '123456', '14574587458', '4848494@qq.com', 0, '2024-07-04 06:14:23', '2024-07-04 07:33:45', 0, 0, null, null);
INSERT INTO yupao.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code) VALUES (null, 5, 'lwb123', null, null, '86c1a2b4821efaed608f7586c7aa7d4b', null, null, 0, '2024-07-04 06:40:13', '2024-07-04 21:14:59', 0, 1, null, null);
INSERT INTO yupao.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code) VALUES (null, 6, 'zhangsan', null, null, '777ff18d62ecfe3b287ec54dd4dece73', null, null, 0, '2024-07-04 08:10:48', '2024-07-04 08:10:48', 0, 0, null, null);
INSERT INTO yupao.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code) VALUES (null, 7, 'lwb456', null, null, '7d5bcede30691b3ee44ff820a44138e5', null, null, 0, '2024-07-04 10:34:46', '2024-07-04 10:34:46', 0, 0, null, null);
INSERT INTO yupao.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code) VALUES ('张三丰', 8, '7788455', '', 0, '123456', '14574587458', '4848494@qq.com', 0, '2024-07-05 18:08:53', '2024-07-05 18:08:53', 0, 0, null, null);


-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)       not null comment '队伍名称',
    description varchar(1024) null comment '描述',
    maxNum      int      default 1 not null comment '最大人数',
    expireTime  datetime null comment '过期时间',
    userId      bigint comment '用户id（队长 id）',
    status      int      default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512) null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0 not null comment '是否删除'
) comment '队伍';

-- 用户队伍关系
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除'
) comment '用户队伍关系';
