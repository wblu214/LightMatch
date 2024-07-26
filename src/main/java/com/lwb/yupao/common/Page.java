package com.lwb.yupao.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *  通用分页请求参数
 * @author 路文斌
 */
@Data
public class Page implements Serializable {

    @Serial
    private static final long serialVersionUID = 7763867929178698882L;
    /**
     * 页面大小
     */
    protected int pageSize;
    /**
     * 当前页码
     */
    protected int pageNum;
}
