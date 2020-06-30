package com.bsoft.mob.ienr.model.risk;

import java.io.Serializable;

public class DERecordPostData implements Serializable {

	private static final long serialVersionUID = -4882488176033668194L;

	// 保存的风险记录数据
	public RiskRecord DERecord;

	// 住院号
	public String ZYH;

	// 病区id
	public String BQID;

	// 机构id
	public String JGID;
}
