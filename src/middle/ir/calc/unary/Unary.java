package middle.ir.calc.unary;

import middle.ir.*;
import middle.operand.*;

public abstract class Unary implements ICode{
	public Operand opd0;
	public Operand res;

	public Unary(Operand opd0, Operand res){
		this.opd0 = opd0;
		this.res = res;
	}

	@Override
	public String toString(){ return res + " = " + opd0 + " " + this.getClass().getSimpleName(); }
}