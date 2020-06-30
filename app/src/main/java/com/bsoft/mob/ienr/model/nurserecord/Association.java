/**   
 * @Title: Association.java 
 * @Package com.bsoft.mob.ienr.model.nurserecord 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-1-5 下午5:08:26 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.nurserecord;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @ClassName: Association
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-1-5 下午5:08:26
 * 
 */
public class Association {
	@JsonProperty(value = "values")
	public List<RefrenceValue> RefrenceValue;
	@JsonProperty(value = "pageIndex")
	public int PageIndex;
	@JsonProperty(value = "pageSize")
	public int PageSize;
}
