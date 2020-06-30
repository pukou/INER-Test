package com.bsoft.mob.ienr.dynamicui.nurserecord;

import com.bsoft.mob.ienr.model.nurserecord.RefrenceValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlugIn {
	/*
	升级编号【56010022】============================================= start
	护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
	================= Classichu 2017/10/18 10:43
	*/
	public String FZYT;
	/* =============================================================== end */
	/**
	 * 控件号
	 */
	public int KJH;
	/**
	 * 元素编号
	 */
	public String YSBH;
	/**
	 * 元素类型
	 */
	public String YSLX;

	/**
	 * 显示名称
	 */
	public String XSMC;

	/**
	 * 数据类型1文本、2数字、3日期、4图片
	 */
	public int SJLX;

	/**
	 * 数据内容格式
	 */
	public String SJGS;

	/**
	 * 控件类型 1 Lable 2 TextBox 3 动态 4 下拉 5特殊控件
	 */

	// public UiType KJLX;

	public int KJLX;

	/**
	 * 控件内容
	 */
	public String KJNR;
	//add 2018-03-05 13:52:26
	public String KSLH;

	/**
	 * 正常值上限
	 */
	public String ZCZSX;

	/**
	 * 正常值下限
	 */
	public String ZCZXX;

	/**
	 * 有效值上限
	 */
	public String YSZSX;

	/**
	 * 有效值下限
	 */
	public String YSZXX;

	/**
	 * 是否显示
	 */
	public String SFXS;

	/**
	 * 是否必填
	 */
	public String SFBT;

	/**
	 * 是否多选
	 */
	public String SFDX;

	/**
	 * 多选分隔符
	 */
	public String DXFG;

	/**
	 * 保存体征项目号
	 */
	public String YSKZ;

	/**
	 * 换行间隔
	 */
	public String HHJG;

	public List<PouponItem> DropdownItem;

	/**
	 * 关联项目的值
	 */
	public List<RefrenceValue> RefrenceValue;

	/**
	 * 当前页
	 */
	public int PageIndex;

	/**
	 * 总页数
	 */
	public int PageSize;

	public static enum UiType {

		/**
		 * 
		 */
		TextView,
		/**
		 * 
		 */
		EditText,
		/**
		 * 动态是选择后，增加此类
		 */
		Daynamic,
		/**
		 * 
		 */
		Spinner,
		/**
		 * 特殊控件
		 */
		Flex;

	}

}
