package com.lwb.yupao.service;

import com.lwb.yupao.model.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.req.TeamReq;
import com.lwb.yupao.model.vo.TeamUserVO;

import java.util.List;

/**
* @author 路文斌
* &#064;description  针对表【team(队伍)】的数据库操作Service
* &#064;createDate  2024-07-25 19:33:52
 */
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     */
    Long createTeam(Team team, User loginUser);
    List<TeamUserVO> listTeams(TeamReq teamReq,boolean isAdmin);
}
