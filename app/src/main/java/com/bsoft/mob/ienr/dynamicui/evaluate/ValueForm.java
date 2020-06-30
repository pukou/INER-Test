/**   
 * @Title: ValueForm.java 
 * @Package com.bsoft.mob.ienr.dynamicui.evaluate 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-28 下午5:01:09 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.dynamicui.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName: ValueForm
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-28 下午5:01:09
 * 
 */
public class ValueForm {
	@JsonProperty(value = "Root")
	public ValueRoot root = new ValueRoot();
}
