package middle.ir.calc.unary;

import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public abstract class Unary extends ICode{
	public Operand opd0;
	public Symbol res;

	public Unary(Operand opd0, Symbol res){
		super();
		this.opd0 = opd0;
		this.res = res;
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		def = res;
	}

	@Override
	public String toString(){ return res + " = " + opd0 + " " + this.getClass().getSimpleName(); }
}