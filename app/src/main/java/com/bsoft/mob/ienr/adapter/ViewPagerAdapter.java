/**   
 * @Title: ViewPagerAdapter.java 
 * @Description: Viewpager适配器类文件
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-13 上午9:09:12 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @ClassName: ViewPagerAdapter
 * @Description: Viewpager适配器
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-11-13 上午9:09:12
 * 
 */
@Deprecated
public class ViewPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	public ViewPagerAdapter(List<View> mListViews) {
		super();
		this.mListViews = mListViews;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		container.addView(mListViews.get(position), 0);// 添加页卡
		return mListViews.get(position);
	}

	@Override
	public int getCount() {
		return mListViews.size();// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

}
