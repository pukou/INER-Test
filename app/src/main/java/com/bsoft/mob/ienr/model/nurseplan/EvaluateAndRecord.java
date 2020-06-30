/**   
 * @Title: EvaluateAndRecord.java 
 * @Package com.bsoft.mob.ienr.model.nurseform 
 * @Description: 计划评价 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-4 下午2:54:40 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.nurseplan;

import java.util.List;

import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.SyncRecord;

/**
 * @ClassName: EvaluateAndRecord
 * @Description: 计划评价及记录
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-4 下午2:54:40
 * 
 */
public class EvaluateAndRecord {
	public List<Evaluate> PJLS;

	public List<Evaluate> PJXM;

	public boolean IsSync = false;

	public SelectResult SyncData;
}
