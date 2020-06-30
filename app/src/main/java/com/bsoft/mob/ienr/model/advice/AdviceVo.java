package com.bsoft.mob.ienr.model.advice;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 医嘱
 */
public class AdviceVo extends BaseVo {
	/**
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 记录序号（医嘱序号）
	 */
	public String JLXH;
	/**
	 * 药房识别
	 */
	public String YFSB;
	/**
	 * 医嘱名称
	 */
	public String YZMC;
	/**
	 * 药品序号
	 */
	public String YPXH;
	/**
	 * 开始时间
	 */
	public String KZSJ;
	/**
	 * 停嘱时间
	 */
	public String TZSJ;
	/**
	 * 药品单价
	 */
	public String YPDJ;
	/**
	 * 备注信息
	 */
	public String BZXX;
	/**
	 *医嘱组号
	 */
	public String YZZH;
	/**
	 * 临时医嘱
	 */
	public int LSYZ;
	/**
	 * 项目类型
	 */
	public String XMLX;
	/**
	 * 药品类型
	 */
	public String YPLX;
	/**
	 * 数量单位
	 */
	public String SLDW;
	/**
	 * 剂量单位
	 */
	public String JLDW;
	/**
	 * 使用频次名称
	 */
	public String SYPCMC;
	/**
	 * 药品用法名称
	 */
	public String YPYFMC;
	/**
	 * 开嘱医生名称
	 */
	public String KZYSMC;
	/**
	 * 停嘱医生名称
	 */
	public String TZYSMC;
	/**
	 * 医嘱大类
	 */
	public String YZDL;
	/**
	 * 一次剂量描述
	 */
	public String YCJLMS;
	/**
	 * 一次数量描述
	 */
	public String YCSLMS;
	/**
	 * 文本颜色
	 */
	public String TEXTCOLOR;
	/**
	 * 无效标志
	 */
	public String WXBZ;
}
