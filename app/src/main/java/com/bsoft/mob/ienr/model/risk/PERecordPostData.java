package com.bsoft.mob.ienr.model.risk;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 保存疼痛项目记录时，从Android端接受的数据，包含PEOption,ZYH,JGID,PGXH
 * User: 苏泽雄
 * Date: 16/12/13
 * Time: 15:51:56
 */
public class PERecordPostData implements Serializable {
   private static final long serialVersionUID = 1530822743755645379L;

   public List<PEOption> RECORDS;

   public String JGID;

   public String ZYH;

   public String PGXH;
}