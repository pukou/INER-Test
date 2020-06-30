package com.bsoft.mob.ienr.model.bloodglucose;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 保存记录时Android端传输的数据，包含ZYH,JGID,BRBQ,JHRQ,XMLX,XMXH,DETAILS
 * User: 苏泽雄
 * Date: 16/12/29
 * Time: 9:58:42
 */
public class BGSavePostData implements Serializable {

	private static final long serialVersionUID = 3360427930822915576L;

	public String ZYH;

	public String JGID;

	public String BRBQ;

	public String JHRQ;

	public String XMLX;

	public String XMXH;

	public List<BloodGlucoseDetail> DETAILS;
}
