package org.jruby.ir.instructions;

import org.jruby.ir.IRVisitor;
import org.jruby.ir.Operation;
import org.jruby.ir.operands.Operand;
import org.jruby.ir.operands.Variable;
import org.jruby.ir.runtime.IRRuntimeHelpers;
import org.jruby.ir.transformations.inlining.CloneInfo;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

// Represents a splat value in Ruby code: *array
public class BuildSplatInstr extends Instr implements ResultInstr {
    public BuildSplatInstr(Variable result, Operand array) {
        super(Operation.BUILD_SPLAT, result, new Operand[] { array });
    }

    public Operand getArray() {
        return operands[0];
    }

    @Override
    public Instr clone(CloneInfo ii) {
        return new BuildSplatInstr(ii.getRenamedVariable(result), getArray().cloneForInlining(ii));
    }

    @Override
    public Object interpret(ThreadContext context, StaticScope currScope, DynamicScope currDynScope, IRubyObject self, Object[] temp) {
        IRubyObject arrayVal = (IRubyObject) getArray().retrieve(context, self, currScope, currDynScope, temp);
        return IRRuntimeHelpers.irSplat(context, arrayVal);
    }

    @Override
    public void visit(IRVisitor visitor) {
        visitor.BuildSplatInstr(this);
    }
}
