package com.bsoft.mob.ienr.model.advice;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 拒绝理由
 */
public class AdviceRefuseReasonVo extends BaseVo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 拒绝理由id
	 */
	public String DYXH;
	/**
	 * 拒绝理由描述
	 */
	public String DYMS;
	
	public AdviceRefuseReasonVo() {
	}


	public AdviceRefuseReasonVo(String DYXH) {
		this.DYXH = DYXH;
	}

	@Override
	public boolean equals(Object o) {
		return DYXH.equals(((AdviceRefuseReasonVo) o).DYXH);
	}

}
