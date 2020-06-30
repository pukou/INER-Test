package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.bloodsugar.PersonBloodSugar;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;

import java.util.ArrayList;

/**
 * 用于批量操作的病人列表 adapter
 *
 * @author hy
 */
public class PersonAdapter extends BaseAdapter {

    private ArrayList<SickPersonVo> list;

    private Context mContext;

    public PersonAdapter(Context context, ArrayList<SickPersonVo> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public SickPersonVo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_list_text_two_vert_secondary_selected, parent, false);
            vHolder = new ViewHolder();
            vHolder.tv_brxm = (TextView) convertView
                    .findViewById(R.id.id_tv_one);
            vHolder.tv_brch = (TextView) convertView
                    .findViewById(R.id.id_tv_two);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        SickPersonVo vo = list.get(position);
        if (vo.dcjCount > 0) {
            vHolder.tv_brxm.setText(vo.BRXM + "(" + vo.dcjCount + ")");
        } else {
            vHolder.tv_brxm.setText(vo.BRXM);
        }
        vHolder.tv_brch.setText(vo.BRCH);

        boolean isZC = false;
        if (vo.personalBloodSugarList != null) {
            for (PersonBloodSugar personBloodSugar : vo.personalBloodSugarList) {
                if (!TextUtils.isEmpty(personBloodSugar.YSZT)&&personBloodSugar.YSZT.contains("自测")) {
                    isZC = true;
                    break;
                }
            }
        }
        if (isZC) {
            vHolder.tv_brxm.setTextColor(Color.RED);
            vHolder.tv_brch.setTextColor(Color.RED);
        } else {
            vHolder.tv_brxm.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
            vHolder.tv_brch.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
        }


        return convertView;
    }

    class ViewHolder {
        public TextView tv_brxm;
        public TextView tv_brch;
    }

    /**
     * @param zYH 病人唯一住院号
     * @return
     */
    public int getPersonPostion(String zYH) {

        if (list == null) {
            return -1;
        }
        int i = 0;
        for (SickPersonVo person : list) {

            if (person.ZYH.equals(zYH)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
