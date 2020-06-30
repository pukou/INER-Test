package com.bsoft.mob.ienr.dynamicui.evaluate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ItemNode {

	@JsonProperty(value = "CheckBox")
	public List<CheckBox> cbs;

	@JsonProperty(value = "RadioBox")
	public List<RadioBox> rbs;

	@JsonProperty(value = "Numeric")
	public List<Numeric> numbers;

	@JsonProperty(value = "Label")
	public List<Label> labels;

	@JsonProperty(value = "Input")
	public List<Input> inputs;

	@JsonProperty(value = "DateTime")
	public List<DateTime> datetimes;

	@JsonProperty(value = "SpinnerDataInfo")
	public List<SpinnerDataInfo> spinners;

	@JsonProperty(value = "SpecSpinnerDataInfo")
	public List<SpecSpinnerDataInfo> specSpinners;

	@JsonProperty(value = "ChildViewModel")
	public List<ChildViewModel> childViewModelLists=new ArrayList<>();

	public int ID;

	public String NText;

	public String IsScored;

	public String Score;

	public String Dzlx;

	public String Dzbd;

	public String Dzxm;

	public String Dzbdlx;

	public String Btbz;

}
