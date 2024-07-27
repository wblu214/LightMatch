package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.enums.TeamStatusEnum;
import com.lwb.yupao.mapper.UserTeamMapper;
import com.lwb.yupao.model.Team;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.UserTeam;
import com.lwb.yupao.service.TeamService;
import com.lwb.yupao.mapper.TeamMapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
* @author 路文斌
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-07-25 19:33:52
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService{
    @Resource
       private UserTeamMapper userTeamMapper;
    @Override
    @Transactional
    public Long createTeam(Team team, User loginUser) {
        final long userId = loginUser.getId();
        //1.校验参数是否为空
        if(team == null){
            throw  new BusinessesException(ErrorCode.NULL_ERROR);
        }
        //2.校验是否登录，未登录不允许创建
        if(loginUser == null){
            throw new BusinessesException(ErrorCode.USER_NOT_LOGIN);
        }
        //3.校验信息
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum <= 0 || maxNum >= 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        String teamName = team.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
        String desc = team.getDescription();
        if (StringUtils.isNotBlank(desc) && desc.length() > 512){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if(teamStatusEnum == null){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        if(TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            String password = team.getPassword();
            if(StringUtils.isBlank(password) || password.length() > 32){
                throw new BusinessesException(ErrorCode.PARAMS_ERROR,"密码不满足要求");
            }
        }
        //4.校验过期时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"创建时间不能晚于当前时间");
        }
        // 5.校验一个用户只能创建5个队伍
        QueryWrapper<Team>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = count(queryWrapper);
        if (hasTeamNum >= 5){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"一个用户最多创建5个队伍");
        }
        //6.插入队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean teamResult = this.save(team);
        Long teamId = team.getId();
        if(!teamResult || teamId == null){
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
        UserTeam  userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        int userTeamResult = userTeamMapper.insert(userTeam);
        if (userTeamResult < 1){
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
        return 0L;
    }
}




