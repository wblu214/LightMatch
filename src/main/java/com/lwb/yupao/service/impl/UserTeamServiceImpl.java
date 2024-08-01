package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwb.yupao.model.UserTeam;
import com.lwb.yupao.service.UserTeamService;
import com.lwb.yupao.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 路文斌
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-07-25 19:36:54
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




