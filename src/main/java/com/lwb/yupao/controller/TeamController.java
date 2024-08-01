package com.lwb.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwb.yupao.common.BaseResult;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.model.Team;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.req.TeamCreateReq;
import com.lwb.yupao.model.req.TeamReq;
import com.lwb.yupao.model.req.TeamUpdateReq;
import com.lwb.yupao.model.vo.TeamUserVO;
import com.lwb.yupao.service.TeamService;
import com.lwb.yupao.service.UserService;
import com.lwb.yupao.utils.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("team")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService  userService;
    @PostMapping("/create")
    public BaseResult<Long> createTeam(@RequestBody TeamCreateReq teamCreateReq, HttpServletRequest request) {
        if(teamCreateReq == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getCurrentUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamCreateReq,team);
        Long result = teamService.createTeam(team, loginUser);
        return ResultUtil.success(result);
    }
    @PostMapping("/delete")
    public BaseResult<Boolean> deleteTeam(@RequestParam long id) {
        if(id <= 0){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"id为空或id错误");
        }
        boolean deleteResult = teamService.removeById(id);
        if(!deleteResult) {
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST);
        }
        return ResultUtil.success(true);
    }
    @PostMapping("/update")
    public BaseResult<Boolean> updateTeam(@RequestBody TeamUpdateReq teamUpdateReq,HttpServletRequest request) {
        if(teamUpdateReq == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR,"team不能为空");
        }
        boolean updateResult = teamService.updateTeam(teamUpdateReq, request);
        if(!updateResult) {
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtil.success(true);
    }
    @GetMapping("/get")
    public BaseResult<Team> getTeam(@RequestParam long id) {
        if(id <= 0){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"id不能为空");
        }

        Team team = teamService.getById(id);
        if (team == null) {
            throw  new BusinessesException(ErrorCode.USER_NOT_EXIST);
        }
        return ResultUtil.success(team);
    }
    @GetMapping("/list")
    public BaseResult<List<TeamUserVO>> getListTeams(TeamReq teamReq, HttpServletRequest request) {
        if(teamReq == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamReq,isAdmin);
        return ResultUtil.success(teamList);
    }
    @GetMapping("/list/page")
    public BaseResult<Page<Team>> getPageTeams(TeamReq teamReq) {
        if(teamReq == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamReq,team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = new Page<>(teamReq.getPageNum(), teamReq.getPageSize());
        Page<Team> teamPage = teamService.page(page,queryWrapper);
        return ResultUtil.success(teamPage);
    }
}
