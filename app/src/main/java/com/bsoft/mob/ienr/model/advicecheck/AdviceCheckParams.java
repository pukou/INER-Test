package com.bsoft.mob.ienr.model.advicecheck;

import java.io.Serializable;

public class AdviceCheckParams implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8056094356859010990L;


	public String IsDispensingCheck;// 是否启用摆药核对（0否1是）

	public String IsSimpleMode;// 是否为复杂核对模式（0否1是）

}
