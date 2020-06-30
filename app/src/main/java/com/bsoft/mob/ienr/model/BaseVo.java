package com.bsoft.mob.ienr.model;

import java.io.Serializable;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-4 上午3:50:42
 * @类说明
 */
public class BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String itemName;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

}
