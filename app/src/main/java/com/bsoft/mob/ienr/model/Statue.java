package com.bsoft.mob.ienr.model;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-16 下午3:20:49
 * @类说明 请求状态
 */
public class Statue {
	// 特殊情况 不同情况显示不同的提示信息
	public static final int Special = -1;
	// 网络错误
	public static final int NET_ERROR = 0;
	// 返回成功
	public static final int SUCCESS = 1;
	// 请求失败
	public static final int ERROR = 2;
	// 解析失败parser
	public static final int PARSER_ERROR = 3;
	// 无数据
	public static final int NO_DATA = 4;
	// 没有选中
	public static final int NO_Chose = 5;

	//
	/*
        升级编号【56010053】============================================= start
        多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
        ================= Classichu 2017/11/14 16:25

        */
	//show list selector
	public static final int Show_List_Selector_handleExecute= 6;
	public static final int Show_List_Selector_scanExecute= 7;
	public static final int SHOW_MSG = 8;
	public static final int NO_OP = 9;
	public static final int SHOW_CORE = 10;
	/* =============================================================== end */
}
