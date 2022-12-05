package middle.ir.calc.unary;

import backend.mips.reg.*;
import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public abstract class Unary extends ICode{
	public Operand opd0;
	public Operand res;

	public Unary(Operand opd0, Operand res){
		super();
		this.opd0 = opd0;
		this.res = res;
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		def.add((Symbol)res);
	}

	@Override
	public String toString(){ return res + " = " + opd0 + " " + this.getClass().getSimpleName(); }

	@Override
	public void genInstr(RegManager regManager){

	}
}