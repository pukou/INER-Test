package com.bsoft.mob.ienr.model;

import java.io.Serializable;
import java.util.List;

/**
 * Describtion:同步用户选择的表单
 * Created: dragon
 * Date： 2017/1/19.
 */
public class SelectResult implements Serializable {
    private static final long serialVersionUID = 7435697301518610625L;

    // 标识符
    public String UUID;

    // 选择的表单列表
    public List<Sheet> sheets;

}
