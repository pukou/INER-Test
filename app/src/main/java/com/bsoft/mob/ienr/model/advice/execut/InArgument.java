package com.bsoft.mob.ienr.model.advice.execut;

import com.bsoft.mob.ienr.model.Sheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Describtion:同步入参
 * Created: dragon
 * Date： 2017/1/3.
 */
public class InArgument implements Serializable{
    private static final long serialVersionUID = 6263172453306730308L;
    /**
     * 住院号
     */
    public String zyh;
    /**
     * 机构ID
     */
    public String jgid;
    /**
     * 病区代码
     */
    public String bqdm;
    /**
     * 护士工号
     */
    public String hsgh;
    /**
     * 记录时间
     */
    public String jlsj;
    /**
     * 表单类型
     1	护理评估  IENR_BDYS                      (表单类型)
     2	风险评估  IENR_FXPGD                    (表单类型)
     3	健康宣教  IENR_XMGL                      (分类类型)
     4	护理计划  IENR_HLJHWT                 (分类类型)
     5	生命体征                                             (项目类型)
     6	护理记录  ENR_JG01                         (表单类型)
     7  风险措施  IENR_FXCSD                     (分类类型 )
     8  护理焦点
     9  医嘱执行
     */
    public String bdlx;
    /**
     * 来源表单
     */
    public String lybd;
    /**
     * 状态标识   0 新增 1 修改 2 删除
     */
    public String flag;
    /**
     * 来源记录序号
     */
    public String jlxh;
    /**
     * 需同步的项目列表
     */
    public List<Project> projects = new ArrayList<>();
    /**
     * 来源明细类型(护理焦点和护理计划之外的类型置为 0)
     */
    public String lymxlx;
    /**
     * 来源明细(护理焦点和护理计划之外的类型置为 0)
     */
    public String lymx;
    /**
     * 用户选择表单后调用
     */
    public Boolean isUserConfirmOper = false;
    /**
     * 默认的目标表单（在同步规则是多表单，且选择默认表单策略失效的情况下，由调用者提供目标表单）
     */
    public List<Sheet> selectSheets = new ArrayList<>();

    @Override
    public String toString() {
        return "住院号：" + zyh + "-记录序号："+ jlxh +"-记录时间：" + jlsj +"-来源表单类型：" + bdlx + "-来源表单编号：" + lybd;
    }
    //
    public String JHSJ4TB;
    public String YZZH4TB;
}
