package com.bsoft.mob.ienr.dynamicui.evaluate;

import com.bsoft.mob.ienr.model.evaluate.AllowSave;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Root {

	@JsonProperty(value = "Form")
	public Form form;

	@JsonProperty(value = "AllowSave")
	public AllowSave save;
}
