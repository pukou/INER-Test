/**   
 * @Title: CheckItem.java 
 * @Package com.bsoft.mob.ienr.model.evaluate 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-17 下午3:09:42 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @ClassName: CheckItem
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-17 下午3:09:42
 * 
 */
public class CheckItem {
	public int XMID;

	public String XMMC;

	public String TXBZ;

	@JsonProperty(value = "ItemChild")
	public List<ItemChild> ITEM;
}
