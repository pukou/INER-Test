package com.bsoft.mob.ienr.model;

public class MemuVo {
	public String name;
	public int headIconId;
	public String tclass;

	public MemuVo(String name, int headIconId) {
		this.name = name;
		this.headIconId = headIconId;
	}

	public MemuVo(String name, int headIconId, String tclass) {
		this.name = name;
		this.headIconId = headIconId;
		this.tclass = tclass;
	}
}
