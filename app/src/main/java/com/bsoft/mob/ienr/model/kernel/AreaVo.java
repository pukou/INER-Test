package com.bsoft.mob.ienr.model.kernel;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank   E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明	病区
 */
public class AreaVo extends BaseVo{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 员工代码
	 */
	public String YGDM;
	/**
	 * 科室代码
	 */
	public String KSDM;
	/**
	 * 是否选择
	 */
	public int MRBZ;
	/**
	 * 科室名称
	 */
	public String KSMC;
	
	public AreaVo(){
	}
	
	public AreaVo(String KSDM){
		this.KSDM=KSDM;
	}
	
	
	@Override
	public boolean equals(Object o) {
		return KSDM.equals(((AreaVo)o).KSDM);
	}
}
