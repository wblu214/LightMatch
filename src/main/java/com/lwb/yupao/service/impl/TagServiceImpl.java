package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lwb.yupao.model.Tag;
import com.lwb.yupao.service.TagService;
import com.lwb.yupao.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 路文斌
* &#064;description  针对表【tag(标签表)】的数据库操作Service实现
* &#064;createDate  2024-07-05 23:33:23
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




