package com.bsoft.mob.ienr.barcode;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 条码业务类
 *
 * @author hy
 */
public class BarcodeEntity implements Parcelable {

    /**
     * 条码业务类型[一级]: 1 病人腕带；2 归属类型；3 医护胸牌；4 床头卡；5 RFID腕带；6 输血（客户化）；7 标本采集; 8 皮试 9 血袋号
     */
    public int TMFL;

    /**
     * 条码业务类型[二级]，例如当TMFL=2时，此时secodeType：1.护理治疗 ；2. 标本采集；3.口服,4.输液,5.注射
     */
    public int FLBS;

    /*
    * 设定号
    */
    public String SDH;

    /*
    * 来源标志
    */
    public String LYBZ;

    /*
    * 条码前缀
    */
    public String TMQZ;

    /*
    * 作废标志
    */
    public String ZFBZ;

    /*
    * 条码规则
    * 1.前缀 2.长度 3.日期 4.校验位
    */
    public String TMGZ;

    /*
    * 规则内容
    */
    public String GZNR;

    /*
    * 条码内容
    */
    public String TMNR;

    /*
    * 条码源码
    */
    public String source;

    /*
    * 机构id
    */
    public String JGID;

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(source);
        dest.writeString(TMQZ);
        dest.writeString(TMNR);
        dest.writeInt(TMFL);
        dest.writeInt(FLBS);
    }

    public static final Parcelable.Creator<BarcodeEntity> CREATOR = new Creator<BarcodeEntity>() {
        @Override
        public BarcodeEntity createFromParcel(Parcel source) {

            BarcodeEntity item = new BarcodeEntity();
            item.source = source.readString();
            item.TMQZ = source.readString();
            item.TMNR = source.readString();
            item.TMFL = source.readInt();
            item.FLBS = source.readInt();
            return item;
        }

        @Override
        public BarcodeEntity[] newArray(int size) {
            return new BarcodeEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}
