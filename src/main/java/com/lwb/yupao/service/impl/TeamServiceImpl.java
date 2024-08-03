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
import com.lwb.yupao.model.req.TeamJoinReq;
import com.lwb.yupao.model.req.TeamQuitReq;
import com.lwb.yupao.model.req.TeamReq;
import com.lwb.yupao.model.req.TeamUpdateReq;
import com.lwb.yupao.model.vo.TeamUserVO;
import com.lwb.yupao.model.vo.UserVO;
import com.lwb.yupao.service.TeamService;
import com.lwb.yupao.mapper.TeamMapper;
import com.lwb.yupao.service.UserService;
import com.lwb.yupao.service.UserTeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService{
    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;
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
    @Transactional
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
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateReq.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(teamUpdateReq.getPassword()))
                throw new BusinessesException(ErrorCode.NULL_ERROR,"私密队伍必须设置密码");
        }
         BeanUtils.copyProperties(teamUpdateReq,oldTeam);
         return this.updateById(oldTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinReq teamJoinReq, HttpServletRequest request) {
        if (teamJoinReq == null){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        long userId = loginUser.getId();
        Long teamId = teamJoinReq.getTeamId();
        if (teamId == null || teamId < 1){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"队伍不存在");
        }
        if (team.getExpireTime() != null && new Date().after(team.getExpireTime())){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"不能加入私有队伍");
        }
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)){
            if(StringUtils.isBlank(teamJoinReq.getPassword()) || !teamJoinReq.getPassword().equals(team.getPassword())){
                throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍密码不正确");
            }
        }
        //该用户已加入的队伍数量
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasJoinNum = userTeamService.count(queryWrapper);
        if (hasJoinNum >= 5){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"一个用户最多加入5个队伍");
        }
        //不能重复加入同一个队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        queryWrapper.eq("userId",userId);
        long hasUserJoinNum = userTeamService.count(queryWrapper);
        if (hasUserJoinNum > 0){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"用户已加入该队伍");
        }
        //该队伍已经拥有的人数
        long teamJoinNum = getJoinNum(teamId);
        if (teamJoinNum >= team.getMaxNum()){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"队伍已满,无法加入");
        }
        //新增用户队伍关系
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }


    @Override
    @Transactional
    public boolean quitTeam(TeamQuitReq teamQuitReq, HttpServletRequest request) {
        if (teamQuitReq == null){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitReq.getTeamId();
        Team team = hasOrNotTeam(teamId);
        User loginUser = userService.getCurrentUser(request);
        long userId = loginUser.getId();
        //判断当前用户是否加入队伍
        UserTeam queryTeam = new UserTeam();
        queryTeam.setUserId(userId);
        queryTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryTeam);
        long isNotJoin = userTeamService.count(queryWrapper);
        if (isNotJoin < 1){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"未加入该队伍");
        }
        long teamJoinNum = getJoinNum(teamId);
        //当前队伍只剩1人
        if (teamJoinNum == 1){
            //删除队伍
            this.removeById(teamId);
        }else{
            //当前队伍至少为2人
            //退出的用户是否为队长
            if(team.getUserId() == userId){
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeams = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeams) || userTeams.size() < 2){
                    throw new BusinessesException(ErrorCode.SYSTEM_ERROR);
                }
                //获取第二个用户作为新的队长
                UserTeam nextTeamUser = userTeams.get(1);
                long nextTeamLeaderId = nextTeamUser.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean updateResult = this.updateById(updateTeam);
                if (!updateResult){
                    throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"转让队长失败");
                }
            }
        }
        //移除用户队伍关系
        QueryWrapper<UserTeam> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("teamId", teamId);
        deleteWrapper.eq("userId", userId);
        try {
            return userTeamService.remove(deleteWrapper);
        } catch (BusinessesException e){
            log.error("退出队伍失败,{}",e.getMessage());
           throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"退出队伍失败");
        }
    }


    @Override
    @Transactional
    public boolean deleteTeam(long teamId,HttpServletRequest request) {
        //校验队伍是否存在
        Team team = hasOrNotTeam(teamId);
        //校验是否为队长
        User loginUser = userService.getCurrentUser(request);
        if (loginUser != null){
            if (!loginUser.getId().equals(team.getUserId())){
                throw new BusinessesException(ErrorCode.FORBIDDEN,"无权限删除队伍");
            }
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        //删除队伍关系
        boolean delRes = userTeamService.remove(queryWrapper);
        if (!delRes){
            throw new BusinessesException(ErrorCode.DATABASE_ERROR,"删除队伍关系失败");
        }
        try {
            //删除队伍
            return this.removeById(team);
        } catch (Exception e) {
            log.error("解散队伍失败,{}",e.getMessage());
            throw new BusinessesException(ErrorCode.DATABASE_ERROR,"解散队伍失败");
        }
    }
    //获取当前队伍已加入的人数
    private long getJoinNum(Long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        return userTeamService.count(queryWrapper);
    }
    //校验队伍是否存在（存在则获取队伍）
    private Team hasOrNotTeam(Long teamId) {
        if (teamId == null || teamId < 1){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"队伍不存在");
        }
        return team;
    }
}




