package com.bsoft.mob.ienr.model.healthguid;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by TXM on 2015-11-30.
 */
public class HealthGuidOper implements Serializable{

    /*
    序号
     */
    public String XH;

    /*
    描述
     */
    public String MS;

    /*
    可操作项目
     */
    public ArrayList<HealthGuidOperItem> HealthGuidOperItems;


    public HealthGuidOper DeepClone() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(HealthGuidOper.this);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in =new ObjectInputStream(byteIn);
        HealthGuidOper dest = (HealthGuidOper)in.readObject();
        return dest;
    }
}
