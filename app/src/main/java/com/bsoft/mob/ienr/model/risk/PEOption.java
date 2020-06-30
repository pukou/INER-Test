package com.bsoft.mob.ienr.model.risk;

import java.io.Serializable;

/**
 * Description: 疼痛评估选项
 * User: 苏泽雄
 * Date: 16/12/12
 * Time: 16:16:00
 */
public class PEOption implements Serializable {
   private static final long serialVersionUID = 8619862659039846058L;
   // 选项序号
   public String XXXH;
   // 项目序号(疼痛评估项目主键)
   public String XMXH;
   // 选项名称
   public String XXMC;
   // 修改标志  1 允许修改  0 不允许修改
   public String XGBZ;
   // 记录项目(疼痛项目记录主键)
   public String JLXM;
   // 项目取值(疼痛评估记录中记录的XXMC，主要针对可修改的选项)
   public String XMQZ;
   // 是否选中
   public Boolean SELECT = false;
}