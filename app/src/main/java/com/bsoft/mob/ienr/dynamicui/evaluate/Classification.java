package com.bsoft.mob.ienr.dynamicui.evaluate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Classification {

	public int ID;

	public String NText;

	public String IsScored;

	public String Score;

	public String HSQM1;

	public String HSQM2;

	public String Dzlx;

	public String Dzbd;

	public String Dzxm;

	public String Dzbdlx;

	public String Btbz;

	/**
	 * 独立标志，0或“”混合签，1独立签名
	 */
	public String DLBZ;

	/**
	 * 1 双签，非1 单签
	 */
	public String QMBZ;

	@JsonProperty(value = "ItemNode")
	public List<ItemNode> itemNodes;

	public String FLLX ="0";
	public String XSFLLX = "0";
	public boolean modFlag = false;

}
