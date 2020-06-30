package com.bsoft.mob.ienr.model.kernel;

import com.bsoft.mob.ienr.model.expense.ExpenseTotal;

import java.io.Serializable;
import java.util.List;

/**
 * Describtion: 病人详情接口返回对象
 * Created: dragon
 * Date： 2016/10/19.
 */
public class PatientDetailResponse implements Serializable{

    /**
     * 病人信息
     */
    public SickPersonDetailVo patient;

    /**
     * 费用信息
     */
    public ExpenseTotal expenseTotal;

    /**
     * 诊断信息
     */
    public String diagnose;

    public List<AllergicDrug> allergicDrugs;
    /**
     * 异常信息
     */
    public List<State> states;
}
