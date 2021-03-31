package org.lm.backend.codegenerator.nasm

import org.lm.frontend.code.*

class NasmCodeGen {

    private val threeAddrCode: ArrayList<ThreeAddrCode>;

    constructor(threeAddrCode: ArrayList<ThreeAddrCode>) {
        this.threeAddrCode = threeAddrCode;
    }

    public fun process() {
        for (codeLine: ThreeAddrCode in threeAddrCode) {
            when (codeLine) {
                is Arith1OpCode -> {};
                is Arith2OpCode -> {};
                is ArrayAssignCode -> {};
                is ArrayRefCode -> {};
                is AssignCode -> {};
                is CondJumpCodeId -> {};
                is CondJumpCodeRel -> {};
                is FuncBeginCode -> {};
                is ParamCallCode -> {};
                is FuncCallCode -> {};
                is ReturnCode -> {};
                is UncondJumpCode -> {};
                else -> {
                    throw Error("Unsupported code class " + codeLine::javaClass);
                }
            }
        }
    }

}