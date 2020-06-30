package com.bsoft.mob.ienr.model.advice.execut;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-18 下午10:05:09
 * @类说明 口服药执行过程中出现停嘱医嘱将信息提示给用户确认，提示文案（本次口服用药中有停嘱;public
 *      String经色为停嘱请确认执行下列口服药）及提示红色字段内容。 确认后执行HandExecut方法;public
 *      StringcheckTime=true;public StringtansfuseBX=false
 */
public class MEModel extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String JHH;
	public String GSLX;
	public String YZXH;
	public String YZZH;
	public String JHSJ;
	public String YZMC;
	public int TZBZ;

}
