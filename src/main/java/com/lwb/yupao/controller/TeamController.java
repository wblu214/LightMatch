package com.lwb.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwb.yupao.common.BaseResult;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.model.Team;
import com.lwb.yupao.model.req.TeamReq;
import com.lwb.yupao.service.TeamService;
import com.lwb.yupao.utils.ResultUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
@RequestMapping("team")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @PostMapping("/add")
    public BaseResult<Long> addTeam(@RequestBody Team team) {
        if(team == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR,"team不能为空");
        }
        boolean addResult = teamService.save(team);
        if(!addResult){
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"创建失败");
        }
        return ResultUtil.success(team.getId());
    }
    @PostMapping("/delete")
    public BaseResult<Boolean> deleteTeam(@RequestBody long id) {
        if(id <= 0){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"id不能为空");
        }
        boolean deleteResult = teamService.removeById(id);
        if(!deleteResult) {
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtil.success(true);
    }
    @PostMapping("/update")
    public BaseResult<Boolean> updateTeam(@RequestBody Team team) {
        if(team == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR,"team不能为空");
        }
        boolean updateResult = teamService.updateById(team);

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
    public BaseResult<List<Team>> listTeams(TeamReq teamReq) {
        if(teamReq == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamReq);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        List<Team> teamList = teamService.list(queryWrapper);
        return ResultUtil.success(teamList);
    }
}
