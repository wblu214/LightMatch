package com.lwb.yupao.service;

import com.lwb.yupao.model.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.req.TeamJoinReq;
import com.lwb.yupao.model.req.TeamQueryReq;
import com.lwb.yupao.model.req.TeamQuitReq;
import com.lwb.yupao.model.req.TeamUpdateReq;
import com.lwb.yupao.model.vo.TeamUserVO;
import jakarta.servlet.http.HttpServletRequest;

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
    /**
     * 查询队伍
     */
    List<TeamUserVO> listTeams(TeamQueryReq teamReq, boolean isAdmin);
    /**
     * 更新队伍
     */
    boolean updateTeam(TeamUpdateReq teamUpdateReq, HttpServletRequest request);
    /**
     * 加入队伍
     */
    boolean joinTeam(TeamJoinReq teamJoinReq,HttpServletRequest request);
    /**
     * 退出队伍
     */
    boolean quitTeam(TeamQuitReq teamQuitReq, HttpServletRequest request);
    /**
     * 解散队伍
     */
    boolean deleteTeam(long teamId,HttpServletRequest request);
}
