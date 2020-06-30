package com.bsoft.mob.ienr.model.blood;

import java.io.Serializable;
import java.util.ArrayList;

public class BloodRecieveSaveData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2523098779923483723L;

	/**
	 * 护士工号
	 */
	public String HSGH;

	/**
	 * 护工工号
	 */
	public String HGGH;

	/**
	 * 标本ID列表
	 */
	public ArrayList<String> SampleId;

	/**
	 * 机构id
	 */
	public String JGID;
}
