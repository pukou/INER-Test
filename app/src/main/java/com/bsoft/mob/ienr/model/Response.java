package com.bsoft.mob.ienr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {

	/**
	 * 非0为异常
	 */
	public int ReType;

	/**
	 * 消息
	 */
	public String Msg;

	/**
	 * 数据
	 */
	public T Data;

}
