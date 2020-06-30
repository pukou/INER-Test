/**   
 * @Title: CheckCls.java 
 * @Package com.bsoft.mob.ienr.model.evaluate 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-17 下午3:10:40 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @ClassName: CheckCls
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-17 下午3:10:40
 * 
 */
public class CheckCls {
	public int FLID;

	@JsonProperty(value = "CheckItem")
	public List<CheckItem> ITEMS;
}
