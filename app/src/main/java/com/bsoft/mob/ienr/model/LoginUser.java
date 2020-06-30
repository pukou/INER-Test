package com.bsoft.mob.ienr.model;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:46:16
 * @类说明 登录用户信息
 */
public class LoginUser {

	/**
	 * 员工id
	 */
	public String YHID;
	/**
	 * 员工姓名
	 */
	public String YHXM;

	/**
	 * 当前默认病区
	 */
	public int MRBZ;

	/**
	 * 机构ID
	 */
	public String JGID;

	/**
	 * 登录账号
	 */
	public String YHDM;

	/**
	 * 用户胸卡
	 */
	public String YHXK;
	
	@Override
	public String toString() {
		return "YHID is " + YHID + "YHXM is" + YHXM + " JGID is" + JGID;
	}

}
