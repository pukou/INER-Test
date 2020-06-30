package com.bsoft.mob.ienr.model.trad;

import java.util.List;

/**
 * Created by Classichu on 2018/1/9.
 */

public class TradBean {

    public String code;
    public String name;
    public String name2;
    public String name3;
    public List<TradChild> tradChildList;


    public  static  class TradChild {
        public String name;
        public String name2;
        public String name3;
        public String name4;
        public String name5;
        public String name6;
    }

}
