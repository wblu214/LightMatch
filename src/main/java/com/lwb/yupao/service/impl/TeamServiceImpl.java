package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwb.yupao.model.Team;
import com.lwb.yupao.service.TeamService;
import com.lwb.yupao.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 路文斌
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-07-25 19:33:52
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




