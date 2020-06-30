package com.bsoft.mob.ienr.dynamicui.evaluate;

import java.util.List;

import com.bsoft.mob.ienr.model.evaluate.AllowSave;
import com.bsoft.mob.ienr.model.evaluate.CheckForm;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Form {

	public String ID;

	public String NText;

	public String IsScored;

	public String Score;

	public String YSXH;

	public String TXGH;
	//add 2018-5-9 14:57:16
	public String TXSJ;

	public String YSLX;

	public String QMGH;

	public String SYZT;
	
	public String Dzlx;
	
	public String Dzbd;
	
	public String Dzxm;
	
	public String Dzbdlx;
	
	public String Btbz;
	
	/**
	 * 0:内部，1：emr（外部）
	 */
	public String LYBS;

	@JsonProperty(value = "Classification")
	public List<Classification> clazzs;

	/**
	 * 本地属性
	 */
	public boolean globalSign;


	@JsonProperty(value = "AllowSave")
	public AllowSave save;

	public CheckForm checkForm;

	public boolean modFlag = false;
}
