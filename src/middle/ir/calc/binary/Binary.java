package middle.ir.calc.binary;

import middle.ir.*;
import middle.operand.Operand;
import middle.operand.symbol.*;

public abstract class Binary extends ICode{
	public Operand opd0;
	public Operand opd1;
	public Operand res;

	public Binary(Operand opd0, Operand opd1, Operand res){
		super();
		this.opd0 = opd0;
		this.opd1 = opd1;
		this.res = res;
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		if(opd1 instanceof Symbol) use.add((Symbol)opd1);
		def.add((Symbol)res);
	}

	@Override
	public String toString(){
		return res + " = " + opd0 + " " + this.getClass().getSimpleName() + " " + opd1;
	}
}
