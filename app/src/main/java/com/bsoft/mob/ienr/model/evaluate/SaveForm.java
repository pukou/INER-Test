package com.bsoft.mob.ienr.model.evaluate;

import com.bsoft.mob.ienr.dynamicui.evaluate.EvaluateViewFactory.VEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SaveForm {

	public String ID;

	public String YSXH;

	public String Score;

	public String TXGH;

	public String JLGH;

	public String TXSJ;

	public String JLSJ;

	public String ZYH;

	public String YSLX;

	public String DLBZ;

	public String QMBZ;

	public String HSQM1;

	public String HSQM2;

	public String LYBS;

	public String FLLX = "0";

	public String IsScored;

	public String XSFLLX;

	@JsonProperty(value = "Item")
	public VEntity[] entities;

}
