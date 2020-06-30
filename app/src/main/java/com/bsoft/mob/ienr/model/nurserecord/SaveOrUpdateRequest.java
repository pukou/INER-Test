package com.bsoft.mob.ienr.model.nurserecord;

import java.util.List;

public class SaveOrUpdateRequest {

	/**
	 * 住院号
	 */
	public String ZYH;

	/**
	 * 换行标志
	 */
	public String HHBZ;

	/**
	 * 书写时间
	 */
	public String SXSJ;

	/**
	 * 记录编号
	 */
	public String JLBH;

	/**
	 * 结构编号
	 */
	public String JGBH;
	/**
	 * 记录时间
	 */
	public String JLSJ;

	/**
	 * 机构ID
	 */
	public String JGID;

	/**
	 * 用户ID
	 */
	public String YHID;

	/**
	 * 
	 */
	public List<SaveOrUpdateItem> ItemList;

}
