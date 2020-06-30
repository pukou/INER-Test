package com.bsoft.mob.ienr.model.advicecheck;

import java.io.Serializable;
import java.util.List;

/**
 * Created by king on 2016/12/1.
 */
public class CheckDetail  implements Serializable {

    public List<AdviceFormDetail> adviceFormDetails;


    public String Msg;

    public String IsFalse;
}
