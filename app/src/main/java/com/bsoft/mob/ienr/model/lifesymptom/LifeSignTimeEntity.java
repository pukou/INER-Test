package com.bsoft.mob.ienr.model.lifesymptom;

public class LifeSignTimeEntity {

	/**
	 * 时刻，例如：00:00，08:00
	 */
	public String NAME;

	/**
	 * 时刻所对应的值
	 */
	public int VALUE;

	/**
	 * 重载toString方法，返回NAME值,用于ArrayAdapter显示
	 */
	@Override
	public String toString() {
		return NAME;
	}
}
