package middle.ir.calc.binary;

import middle.ir.*;
import middle.ir.calc.*;
import middle.operand.Operand;
import middle.operand.symbol.*;

import java.util.*;

public abstract class Binary extends ICode implements Calc{
	public Operand opd0;
	public Operand opd1;
	public Symbol res;

	public Binary(Operand opd0, Operand opd1, Symbol res){
		super();
		this.opd0 = opd0;
		this.opd1 = opd1;
		this.res = res;
		genUseDef();
	}

	@Override
	public void changeDef(Symbol newDef){
		res = newDef;
		genUseDef();
	}

	@Override
	public void changeUse(Symbol oldUse, Operand newUse){
		if(opd0 instanceof Symbol && opd0.equals(oldUse)) opd0 = newUse;
		if(opd1 instanceof Symbol && opd1.equals(oldUse)) opd1 = newUse;
		genUseDef();
	}

	private void genUseDef(){
		use = new HashSet<>();
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		if(opd1 instanceof Symbol) use.add((Symbol)opd1);
		def = res;
	}

	public abstract Integer calc();

	@Override
	public String toString(){
		return res + " = " + opd0 + " " + this.getClass().getSimpleName() + " " + opd1;
	}
}
