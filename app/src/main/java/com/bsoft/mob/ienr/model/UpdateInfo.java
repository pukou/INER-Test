package com.bsoft.mob.ienr.model;

/**
 * @author Tank   E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 更新信息
 */
public class UpdateInfo extends BaseVo {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 更新描述
     */
    public String Description;

    /**
     * 版本号
     */
    public int VersionCode;

    /**
     * 版本名称
     */
    public String VersionName;

    /**
     * 文件名称，包括后缀
     */
    public String FileName;

    /**
     * 更新地址
     */
    public String Url;
}
