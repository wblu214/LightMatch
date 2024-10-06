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
-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 3                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             null comment '用户id（队长 id）',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    imageUrl    varchar(512)                       null comment '队伍头像',
    currentNum  int      default 0                 null comment '已加入人数',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';

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


INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('马兵', 1, 'mb123', 'https://wx.zsxq.com/dweb2/assets/images/emoji/%E8%89%B2.png', 1, 'c660e7ad46c2ae91feb06b65dc89c68f', '14574587458', '4848494@qq.com', 0, '2024-07-04 06:04:20', '2024-07-27 21:06:45', 0, 0, '["python","c++"]', '124', '精神小伙一枚');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('母洪福', 2, 'mhf123', 'https://wx.zsxq.com/dweb2/assets/images/emoji/%E8%89%B2.png', 1, '76c821b613270edae43180d8b1740eea', '14574587458', '4848494@qq.com', 0, '2024-07-04 06:14:23', '2024-07-27 21:14:41', 0, 0, '["java","c++"]', '3', '念慈嫡女并改变他人');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('路文斌', 3, 'lwb123', 'https://wx.zsxq.com/dweb2/assets/images/emoji/%E5%98%BF%E5%93%88.png', 0, '86c1a2b4821efaed608f7586c7aa7d4b', '18995157457', 'verve@qq.com', 0, '2024-07-04 06:40:13', '2024-08-09 00:42:39', 0, 1, '["java","c++"]', '3', '华山掌门');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('童辉', 4, 'th123', 'https://wx.zsxq.com/dweb2/assets/images/emoji/%E5%98%BF%E5%93%88.png', 0, 'a1d3b00f347da9d2f17f4dda239c0c99', '15425478457', null, 0, '2024-07-04 08:10:48', '2024-08-08 15:16:07', 0, 0, '["java","python"]', '67', '精神小伙二枚');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('杨明', 5, 'ym123', 'https://wx.zsxq.com/dweb2/assets/images/emoji/%E5%87%8B%E8%B0%A2.png', 0, 'a7054bf90308aa88778e0094c6c14758', null, null, 0, '2024-07-04 10:34:46', '2024-08-07 16:22:20', 0, 0, '["c++"]', '76', '念慈嫡女并改变他人');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('李艳', 8, 'ly123', 'https://wx.zsxq.com/dweb2/assets/images/emoji/%E5%87%8B%E8%B0%A2.png', 0, 'da3712c84c828f72bc3a6f9baf0ff9bf', '14574587458', '4848494@qq.com', 0, '2024-07-05 18:08:53', '2024-07-27 21:14:03', 0, 1, '["golang","c++"]', '45', '念慈嫡女并改变他人');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('朱棣', 9, 'zd123', '', 1, '32dc98e4519a58503c5298b2cc354be9', '12547458745', '45@qq.com', 0, '2024-07-12 02:23:25', '2024-07-27 21:08:30', 0, 0, '[]', '5', '永乐大帝');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('朱棣', 10, 'zdddd1', '', 1, '123456', '12547458745', '45@qq.com', 0, '2024-07-12 02:23:25', '2024-07-12 02:23:25', 0, 0, '[]', '6', '永乐大帝');
INSERT INTO light.user (username, id, userAccount, imageUrl, gender, userPassword, phone, email, status, createTime, updateTime, isDelete, userRole, tags, code, profile) VALUES ('朱棣', 11, 'zdddd2', '', 1, '123456', '12547458745', '45@qq.com', 0, '2024-07-12 02:23:25', '2024-07-12 02:23:25', 0, 0, '[]', '7', '永乐大帝');