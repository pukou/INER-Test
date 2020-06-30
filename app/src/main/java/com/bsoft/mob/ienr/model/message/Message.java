package com.bsoft.mob.ienr.model.message;

public class Message {

	/**
	 * 消息的唯一ID值
	 */
	public long MsgId;
	/**
	 * 消息的类型 危机值1 医嘱 2,工作 3, 系统 4
	 */
	public int MsgType;
	/**
	 * 业务ID
	 */
	public String BusinessId;
	/**
	 * 消息级别
	 */
	public String Level;
	/**
	 * 摘要
	 */
	public String Content;

	public String Time;

	public String PatientId;

	public String PatientWard;

	public String UserId;

	public String Agency;
	//类型名称
	public String LXMC;

	//主动提醒
	public String ZDTX;
}
