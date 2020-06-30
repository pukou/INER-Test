package com.bsoft.mob.ienr.model.kernel;

import org.json.JSONException;
import org.json.JSONObject;

import com.bsoft.mob.ienr.model.BaseVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 病区RFID
 */
public class RFIDStatueVo extends BaseVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 设备类型名称
	 */
	public String DeviceTypeName;
	/**
	 * 总数
	 */
	public int TotalCount;
	/**
	 * 损坏数量
	 */
	public int BrokenCount;
	/**
	 * 报修数量
	 */
	public int RepairCount;
	/**
	 * 废弃数量
	 */
	public int DiscardCount;
	/**
	 * 使用中数量
	 */
	public int UsingCount;
	/**
	 * 空闲数量
	 */
	public int FreeCount;

	public void parser(JSONObject ob) throws JSONException {
		if (!ob.isNull("TotalCount")) {
			this.TotalCount = ob.getInt("TotalCount");
		}
		if (!ob.isNull("BrokenCount")) {
			this.BrokenCount = ob.getInt("BrokenCount");
		}
		if (!ob.isNull("RepairCount")) {
			this.RepairCount = ob.getInt("RepairCount");
		}
		if (!ob.isNull("DiscardCount")) {
			this.DiscardCount = ob.getInt("DiscardCount");
		}
		if (!ob.isNull("UsingCount")) {
			this.UsingCount = ob.getInt("UsingCount");
		}
		if (!ob.isNull("FreeCount")) {
			this.FreeCount = ob.getInt("FreeCount");
		}
	}
}
