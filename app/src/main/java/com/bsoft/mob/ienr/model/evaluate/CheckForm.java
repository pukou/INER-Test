/**   
* @Title: CheckForm.java 
* @Package com.bsoft.mob.ienr.model.evaluate 
* @author 吕自聪  lvzc@bsoft.com.cn
* @date 2015-12-17 下午3:17:46 
* @version V1.0   
*/ 
package com.bsoft.mob.ienr.model.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** 
 * @ClassName: CheckForm 
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-17 下午3:17:46 
 *  
 */
public class CheckForm {
	@JsonProperty(value = "CheckCls")
	public List<CheckCls> CLS;
}
