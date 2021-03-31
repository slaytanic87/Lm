package org.lm.frontend.code

import org.lm.frontend.syntaxtree.Constant
import org.lm.frontend.syntaxtree.Expr
import org.lm.frontend.syntaxtree.Singleton
import org.lm.frontend.syntaxtree.Temp

class ParamCallCode: FuncCode {

    var param: Expr;

    constructor(id: Singleton, param: Expr): super(id) {
        this.param = param;
    }

    public override fun toString(): String {
        if (param is Temp || param is Constant) {
            return "param " + param.toString();
        }
        return "refparam " + param.toString();
    }
}