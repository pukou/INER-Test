package com.bsoft.mob.ienr.model.risk;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 疼痛评估项目
 * User: 苏泽雄
 * Date: 16/12/12
 * Time: 16:03:35
 */
public class PainEvaluate implements Serializable {
   private static final long serialVersionUID = 5535069828673403173L;
   // 项目序号
   public String XMXH;
   // 项目名称
   public String XMMC;
   // 项目类型  1 手工输入  2 单项选择  3 多项选择
   public String XMLX;
   // 疼痛评估选项
   public List<PEOption> PGXX;
}