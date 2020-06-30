/**   
 * @Title: RiskRecord.java 
 * @Package com.bsoft.mob.ienr.model.risk 
 * @Description:风险记录 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-10 下午1:38:32 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.risk;

import java.util.List;

import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.SyncRecord;

/**
 * @ClassName: RiskRecord
 * @Description: 风险记录
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-10 下午1:38:32
 * 
 */
public class RiskRecord {

	// 评估序号
	public String PGXH;

	// 评估单号
	public String PGDH;

	// 病人床号
	public String BRCH;

	// 病人姓名
	public String BRXM;

	// 表单名称
	public String BDMC;

	// 评估时间
	public String PGSJ;

	// 评估总分
	public String PGZF;

	// 评估工号 员工代码
	public String PGGH;

	// 评估护士 姓名
	public String PGHS;

	// 质控描述
	public String ZKMS;

	// 质控规则
	public List<QualityControl> ZKGZ;

	// 风险因子
	public List<RiskFactor> FXYZ;

	// 评估类型
	public String PGLX;

	// 护士长签名
	public String HSZQM;

	// 判断同步结果，是否需要处理
	public boolean IsSync = false;

	// 同步数据
	public SelectResult SyncData;

}
