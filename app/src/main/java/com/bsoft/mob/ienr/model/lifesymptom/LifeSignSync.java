/**   
 * @Title: SyncBoolean.java 
 * @Package com.bsoft.mob.ienr.model.lifesymptom 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-1-18 上午10:34:17 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.model.lifesymptom;

import com.bsoft.mob.ienr.model.SelectResult;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: SyncBoolean
 * @Description: 是否要同步
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-1-18 上午10:34:17
 * 
 */
public class LifeSignSync implements Serializable {

	private static final long serialVersionUID = -6347697370571502639L;
	public String TeamID;
	public String JLBH;
	public Boolean IsSync = false;
	public SelectResult SyncData;
	//add by louis  存放保存的体征李列表 2017年6月6日13:39:34
	public List<LifeSignRealSaveDataItem> mLifeSignRealSaveDataItemList;
}
