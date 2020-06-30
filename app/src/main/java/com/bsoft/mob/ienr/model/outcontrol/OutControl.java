package com.bsoft.mob.ienr.model.outcontrol;

public class OutControl {

	/**
	 * 记录序号
	 */
	public String JLXH;
	/**
	 * 外出登记时间
	 */
	public String WCDJSJ;

	/**
	 * 外出登记护士
	 */
	public String WCDJHS;

	/**
	 * 预计回床时间
	 */
	public String YJHCSJ;

	/**
	 * 批准医生
	 */
	public String PZYS;

	public String WCYY;

	/**
	 * 回床登记时间
	 */
	public String HCDJSJ;

	/**
	 * 回床登记护士
	 */
	public String HCDJHS;

	/**
	 * 0无 1有 陪同人员
	 */
	public String PTRY;
	/*升级编号【56010038】============================================= start
                外出管理PDA上只有登记功能，查询需要找到具体的人再查询，不太方便，最好能有一个查询整个病区外出病人的列表
            ================= classichu 2018/3/7 19:49
            */
	//zyh
	public String ZYH;
	public String BRXM;
	public String BRCH;
	/* =============================================================== end */

}
