/**   
* @Title: CheckRoot.java 
* @Package com.bsoft.mob.ienr.model.evaluate 
* @author 吕自聪  lvzc@bsoft.com.cn
* @date 2015-12-17 下午3:29:55 
* @version V1.0   
*/ 
package com.bsoft.mob.ienr.model.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

/** 
 * @ClassName: CheckRoot 
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-17 下午3:29:55 
 *  
 */
public class CheckRoot {
	@JsonProperty(value = "root")
	public CheckForm form;
}
