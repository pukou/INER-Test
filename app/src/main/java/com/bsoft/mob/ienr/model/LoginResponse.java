package com.bsoft.mob.ienr.model;


import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.model.kernel.UserConfig;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class LoginResponse implements Serializable{

    public LoginUser LonginUser;
    public TimeVo TimeVo;
    public List<AreaVo> Areas;
    public String SessionId;
    public UserConfig userConfig;
}
