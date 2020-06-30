package com.bsoft.mob.ienr.dynamicui.nurserecord;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.PopupWindowHelper;

import java.util.ArrayList;
import java.util.List;

public class PouponEditViewForMultiselect extends LinearLayout {

    public String dxfg;//对象分隔符
    public Boolean onlyShowQzwb;//只显示前置文本

    public PouponEditViewForMultiselect(Context context, String dxfgf) {
        super(context);
        dxfg = dxfgf;
        onlyShowQzwb = false;
        init(context);
    }

    //福建协和客户化：只显示前置文本
    public PouponEditViewForMultiselect(Context context, String dxfgf, Boolean onlyShowQzwb) {
        super(context);
        dxfg = dxfgf;
        this.onlyShowQzwb = onlyShowQzwb;
        init(context);
    }

    public View mainView;
    public LayoutInflater mInflater;
    public EditText edit;
    public ImageView down;
    public PullAdapter adapter;
    public ListView listView;
    public ArrayList<PouponItem> datas;
    private TextView title;

    private PopupWindow pop;

    private View listBox;
    private Button btnOk;
    private Button btnCancle;


    OnSelectListener selectListener;

    public void setOnSelectListener(OnSelectListener listener) {
        this.selectListener = listener;
    }

    public OnSelectListener getOnSelectListener() {
        return this.selectListener;
    }

    public void setEditHitText(String text) {
        edit.setHint(text);
    }

    public EditText getEditText() {
        return edit;
    }

    public String getEditTextText() {
        return edit.getText().toString();
    }

    public void setEditIsAdble(boolean flage) {
        edit.setEnabled(flage);
    }

    public void hidden() {
        if (null != pop) {
            if (pop.isShowing()) {
                pop.dismiss();
            }
        }
    }

    public void setDataList(List<PouponItem> d) {

        this.datas = (ArrayList<PouponItem>) d;
        adapter = new PullAdapter();
        listView.setAdapter(adapter);
        listView.setBackgroundColor(ContextCompat.getColor(listView.getContext(),R.color.windowBackground));
        down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == pop) {
                    pop = new PopupWindow(listBox, edit.getWidth(),
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    pop.setTouchable(true);
                    pop.setFocusable(true);
                    pop.setOutsideTouchable(true);
                    pop.setBackgroundDrawable(new BitmapDrawable(
                            getResources(), (Bitmap) null));
//                    pop.showAsDropDown(edit);
                    PopupWindowHelper.show(pop,edit);
                } else {
                    if (pop.isShowing()) {
                        pop.dismiss();
                    } else {
//                        pop.showAsDropDown(edit);
                        PopupWindowHelper.show(pop,edit);
                    }
                }
            }
        });

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = "";
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).ISCHECK) {
                        if (onlyShowQzwb) {
                            value += datas.get(i).QZWB + dxfg;
                        } else {
                            value += datas.get(i).XZNR + dxfg;
                        }
                        list.add(datas.get(i).XZH);
                    }
                }
                if (!"".equals(value)) {
                    value = value.substring(0, value.length() - dxfg.length());
                }
                edit.setText(value);
                edit.setSelection(value.length());
                if (null != selectListener) {
                    selectListener.doSelect(list);
                }
                pop.dismiss();
            }
        });

        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        addView(mainView, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mainView = mInflater.inflate(R.layout.pouponeditview, null, false);
        edit = (EditText) mainView.findViewById(R.id.edit);
        edit.setSingleLine(false);
        if (onlyShowQzwb) {
            edit.setEnabled(!onlyShowQzwb);
        }
        down = (ImageView) mainView.findViewById(R.id.down);
        title = (TextView) mainView.findViewById(R.id.text);
        listBox = mInflater.inflate(R.layout.popupwindow_box, null, false);
        listView = (ListView) listBox.findViewById(R.id.list);
        btnOk = (Button) listBox.findViewById(R.id.ok);
        btnCancle = (Button) listBox.findViewById(R.id.cancle);

    }

    public void setTitle(String titleStr) {
        if (title != null) {
            title.setText(titleStr);
        }
    }

    public void setEditText(String content) {
        if (edit != null) {
            edit.setText(content);
            //
            if (!TextUtils.isEmpty(content)) {
                String[] ss = content.split(dxfg);
                for (int i = 0; i < ss.length; i++) {
                    String key = ss[i];
                    for (PouponItem data : datas) {
                        if (key.equals(data.XZNR)) {
                            data.ISCHECK = true;
                            break;
                        }
                    }
                }
            }

        }
    }

    class PullAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater
                        .inflate(R.layout.item_list_bar_check_text_start_no_clickable, parent, false);
                holder.itemView = convertView;
                holder.text_row = (TextView) convertView
                        .findViewById(R.id.id_tv_name);
                holder.check = (CheckBox) convertView
                        .findViewById(R.id.id_cb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.check.setChecked(datas.get(position).ISCHECK);
            holder.check.setVisibility(View.VISIBLE);
            holder.text_row.setText(datas.get(position).XZNR);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    datas.get(position).ISCHECK = !datas.get(position).ISCHECK;
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            public TextView text_row;
            public CheckBox check;
            public View itemView;
        }
    }

    public interface OnSelectListener {
        public void doSelect(ArrayList<String> list);
    }

}
