package middle.ir.calc.binary;

import middle.ir.*;
import middle.operand.Operand;

public abstract class Binary implements ICode{
	public Operand opd0;
	public Operand opd1;
	public Operand res;

	public Binary(Operand opd0, Operand opd1, Operand res){
		this.opd0 = opd0;
		this.opd1 = opd1;
		this.res = res;
	}

	@Override
	public String toString(){
		return res + " = " + opd0 + " " + this.getClass().getSimpleName() + " " + opd1;
	}
}
