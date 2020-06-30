package com.bsoft.mob.ienr.model.nurserecord;

import java.util.List;

public class HelpTree {

	/**
	 * 助手内容
	 */
	public String ZSNR;

	/**
	 * 目录编号
	 */
	public String MLBH;

	/**
	 * 父目录编号
	 */
	public String FLBH;

	/**
	 * 目录编码
	 */
	public String MLBM;

	/**
	 * 目录类别
	 */
	public String MLLB;

	/**
	 * 目录名称
	 */
	public String MLMC;

	/**
	 * 子目录项目为内容对象本身(即为多层)
	 */
	public List<HelpTree> Items;
	//todo 待优化 客户端可以用起来 不需要再去取
	public List<HelpLeaf> helpLeafList;

}
