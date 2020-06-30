package com.bsoft.mob.ienr.dynamicui.evaluate;

import java.io.Serializable;

public class ChildViewModel implements Serializable {
	public static final String CheckBox="CheckBox";
	public static final String RadioBox="RadioBox";
	public static final String Numeric="Numeric";
	public static final String Label="Label";
	public static final String Input="Input";
	public static final String DateTime="DateTime";
	public static final String Spinner="SpinnerDataInfo";
	public static final String SpecSpinner="SpecSpinnerDataInfo";

	private String childViewType;
	private Object childViewObj;

	public String getChildViewType() {
		return childViewType;
	}

	public Object getChildViewObj() {
		return childViewObj;
	}
	public ChildViewModel() {
	}

	public ChildViewModel(String childViewType, Object childViewObj) {
		this.childViewType = childViewType;
		this.childViewObj = childViewObj;
	}

}
