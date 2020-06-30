package com.bsoft.mob.ienr.model.announce;

/**
 * 宣教表单index
 * 
 * @author hy
 * 
 */
public class AnnnouceSecondIdx {

	/**
	 * 一级宣教序号
	 */
	public String YSBS;

	/**
	 * 二级宣教序号
	 */
	public String LBBS;

	/**
	 * 宣教名称
	 */
	public String XMMC;

	@Override
	public boolean equals(Object o) {

		if (o == null) {
			return super.equals(o);
		}

		if (o instanceof AnnnouceSecondIdx) {

			return YSBS.equals(((AnnnouceSecondIdx) o).YSBS)
					&& LBBS.equals(((AnnnouceSecondIdx) o).LBBS);

		} else {
			return false;
		}

	}
}
