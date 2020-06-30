package com.bsoft.mob.ienr.model.expense;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */
public class ExpenseRespose implements Serializable {
    private static final long serialVersionUID = -3565086075982701116L;

    public ExpenseTotal Table1;
    public List<ExpenseVo> Table2;
    public List<ExpenseDaysDetail> Table3;
}
