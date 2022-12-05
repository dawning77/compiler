package middle.ir.mem;

import middle.ir.*;
import middle.operand.*;
import middle.operand.symbol.*;

public abstract class AccessMem extends ICode{
	public Symbol sym;
	public Operand idx;
	public Operand val;

	public AccessMem(Symbol sym, Operand idx, Operand val){
		super();
		this.sym = sym;
		this.idx = idx;
		this.val = val;
	}
}
