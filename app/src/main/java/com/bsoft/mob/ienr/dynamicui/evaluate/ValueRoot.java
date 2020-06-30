/**   
 * @Title: ValueRoot.java 
 * @Package com.bsoft.mob.ienr.dynamicui.evaluate 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-28 下午4:24:51 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.dynamicui.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ValueRoot
 * @Description:
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-28 下午4:24:51
 * 
 */
public class ValueRoot {
	@JsonProperty(value = "ValueEntity")
	public List<ValueEntity> values = new ArrayList<>();
}
