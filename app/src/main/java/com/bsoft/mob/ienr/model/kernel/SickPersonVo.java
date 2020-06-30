package com.bsoft.mob.ienr.model.kernel;

import com.bsoft.mob.ienr.model.BaseVo;
import com.bsoft.mob.ienr.model.bloodsugar.PersonBloodSugar;
import com.bsoft.mob.ienr.model.inspection.SpecimenVo;
import com.bsoft.mob.ienr.model.risk.RecondBean;
import com.bsoft.mob.ienr.model.risk.ZKBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午4:27:25
 * @类说明 病人信息对象
 */
public class SickPersonVo extends BaseVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 住院号
	 */
	public String ZYH;
	/**
	 * 住院号码
	 */
	public String ZYHM;
	/**
	 * 病人姓名
	 */
	public String BRXM;
	/**
	 * 病人性别
	 */
	public int BRXB;
	/**
	 * 出生年月
	 */
	public String CSNY;
	/**
	 * 病人床号
	 */
	public String BRCH;
	/**
	 * 护理级别,默认没有级别
	 */
	public int HLJB = -1;
	/**
	 * 病人年龄
	 */
	public String BRNL;
	/**
	 * 
	 */
	public String RFID;

	/**
	 * 入院日期
	 */
	public String RYRQ;

	/******* 标本采集病人列表特有 ********/
	/**
	 * 病人病区
	 */
	public String BRBQ;

	/**
	 * 发放状态 0 没有发放记录 1 全部已发放 2 部分发放部分未发放
	 */
	public int FFZT;

	/**
	 * 机构ID
	 */
	public String JGID;

	/******* 体征采集病人列表特有 ********/
	/**
	 * 体征项目,格式7,5,6
	 */
	public String TZXM;

	/**
	 * 病人状态
	 */
	public String BRZT;

	/*
	升级编号【56010014】============================================= start
	今日出院的病人，系统会提早出院，但是该病人当天的药品需要执行完，目前PDA无法处理该类病人的医嘱执行。（需要将当日出院的病人也显示在病人类别中，并标识）
	================= Classichu 2017/10/11 16:32
	6.
	*/
	/**
	 * 出院判别
	 */
	public String CYPB;
	public String BRXX;
	/**
	 * 主治医生
	 */
	public String ZZYS;
	public String BRZDMC;
	public String BRZDLB;
	public String LCLJ;
	/* =============================================================== end */
	public ArrayList<State> state;
	//
	public List<RecondBean> recondBeanList4Sicker;
	public List<ZKBean> zKbeanList4Sicker;
	public int dcjCount;//待采集
	public boolean hasGMYP;//过敏药品
	public List<PersonBloodSugar> personalBloodSugarList;//病人血糖列表


	@Override
	public boolean equals(Object o) {
		return this.ZYH.equals(((SickPersonVo) o).ZYH);
	}

}
