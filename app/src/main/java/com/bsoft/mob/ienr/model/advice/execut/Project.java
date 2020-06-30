package com.bsoft.mob.ienr.model.advice.execut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Describtion:同步的项目
 * Created: dragon
 * Date： 2017/1/3.
 */
public class Project implements Serializable{
    public Project(){}
    public Project(String key ,String vlaue){
        this.key = key;
        this.value = vlaue;
    }
    public String key;
    public String value;

    public List<Project> saveProjects = new ArrayList<>();
}
