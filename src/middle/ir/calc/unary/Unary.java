package middle.ir.calc.unary;

import middle.ir.*;
import middle.ir.calc.*;
import middle.operand.*;
import middle.operand.symbol.*;

import java.util.*;

public abstract class Unary extends ICode implements Calc{
	public Operand opd0;
	public Symbol res;

	public Unary(Operand opd0, Symbol res){
		super();
		this.opd0 = opd0;
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
		genUseDef();
	}

	private void genUseDef(){
		use = new HashSet<>();
		if(opd0 instanceof Symbol) use.add((Symbol)opd0);
		def = res;
	}

	public abstract Integer calc();

	@Override
	public String toString(){ return res + " = " + opd0 + " " + this.getClass().getSimpleName(); }
}