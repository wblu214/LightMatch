package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.enums.TeamStatusEnum;
import com.lwb.yupao.mapper.UserMapper;
import com.lwb.yupao.mapper.UserTeamMapper;
import com.lwb.yupao.model.Team;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.UserTeam;
import com.lwb.yupao.model.req.TeamReq;
import com.lwb.yupao.model.req.TeamUpdateReq;
import com.lwb.yupao.model.vo.TeamUserVO;
import com.lwb.yupao.model.vo.UserVO;
import com.lwb.yupao.service.TeamService;
import com.lwb.yupao.mapper.TeamMapper;
import com.lwb.yupao.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author 路文斌
* &#064;description  针对表【team(队伍)】的数据库操作Service实现
* &#064;createDate  2024-07-25 19:33:52
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService{
    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Override
    @Transactional
    public Long createTeam(Team team, User loginUser) {
        //1.校验是否登录，未登录不允许创建
        if(loginUser == null){
            throw new BusinessesException(ErrorCode.USER_NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        //2.校验参数是否为空
        if(team == null){
            throw  new BusinessesException(ErrorCode.NULL_ERROR);
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
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"过期时间不能小于当前时间");
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

    @Override
    public List<TeamUserVO> listTeams(TeamReq teamReq,boolean isAdmin) {
        //组合查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //已过期的不展示
        queryWrapper.and(qw ->qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        if(teamReq != null){
            Long id = teamReq.getId();
            if(id != null && id > 0){
                queryWrapper.eq("id",id);
            }
            String searchText = teamReq.getSearchText();
            if (StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw -> qw.like("name",searchText).or().like("description",searchText));
            }
            String name = teamReq.getName();
            if (StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamReq.getDescription();
            if (StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }
            Integer maxNum = teamReq.getMaxNum();
            if (maxNum != null && maxNum > 0){
                queryWrapper.eq("maxNum",maxNum);
            }
            Long userId = teamReq.getUserId();
            if( userId != null && userId > 0){
                queryWrapper.eq("userId",userId);
            }
            Integer status = teamReq.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);

            if (statusEnum == null){
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !TeamStatusEnum.PUBLIC.equals(statusEnum)){
                throw new BusinessesException(ErrorCode.FORBIDDEN);
            }
            if (status != null && status > -1){
                queryWrapper.eq("status",statusEnum.getCode());
            }
        }
        List<Team> teamList = this.list(queryWrapper);
        //关联查询用户信息
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOS = new ArrayList<>();
        for (Team team : teamList){
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userMapper.selectById(userId);
            //脱敏
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            if (user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOS.add(teamUserVO);
        }
        return teamUserVOS;
    }

    @Override
    public boolean updateTeam(TeamUpdateReq teamUpdateReq, HttpServletRequest request) {
        if (teamUpdateReq == null){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateReq.getId();
        if (id == null || id < 1){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"队伍不存在");
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"队伍不存在");
        }
        User loginUser = userService.getCurrentUser(request);
        //只有管理员和本人才能修改
        if(!userService.isAdmin(request) && !oldTeam.getUserId().equals(loginUser.getId())){
            throw new BusinessesException(ErrorCode.FORBIDDEN);
        }
         BeanUtils.copyProperties(teamUpdateReq,oldTeam);
         return this.updateById(oldTeam);
    }
}




