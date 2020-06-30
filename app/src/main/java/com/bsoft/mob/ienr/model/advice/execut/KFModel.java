package com.bsoft.mob.ienr.model.advice.execut;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-18 下午10:05:09
 * @类说明 多包口服药的扫描执行， 需要扫描所有的药包后自动提交执行 提示文案（本次共有**包药需要执行，请全部扫描） 用户扫描所列项目的所有条码后 提交
 *      OralMedicationExecut
 */
public class KFModel extends BaseVo {

	public String EQXH;
	public String YZMC;
	public String KFMX;
	public String JLXX;
	public String SLXX;
	public String BZSL;
	public String BZJL;
	public String YDMS;
	public String TMBH;
	public String QRDH;
	//add 2018-4-25 18:47:59
	public String JHSJ;
	public String YZZH;
	public String JHH;

	public KFModel(){
		
	}
	
	public KFModel(String TMBH){
		this.TMBH=TMBH;
	}

	@Override
	public boolean equals(Object o) {
		return TMBH.equals(((KFModel) o).TMBH);
	}

}
