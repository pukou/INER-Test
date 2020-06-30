/**   
* @Title: AllowSave.java 
* @Package com.bsoft.mob.ienr.model.evaluate 
* @Description: 必填项未填写是否允许保存
* @author 吕自聪  lvzc@bsoft.com.cn
* @date 2015-12-29 下午3:18:50 
* @version V1.0   
*/ 
package com.bsoft.mob.ienr.model.evaluate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: EvaluateSaveRespose
 * @Description:
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-29 下午3:18:50
 *
 */
public class EvaluateSaveRespose implements Serializable {
	private static final long serialVersionUID = 564751681705080572L;

	//表单总项
	public SaveForm saveForm;

	//表单保存数据列表
	public List<SaveForm> lists = new ArrayList<>();

	public String bqdm;
	public String jgid;
	//add 2018-5-7 13:36:07
	public String isZKNotCheckBQ;
}
