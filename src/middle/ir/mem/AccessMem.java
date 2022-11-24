package middle.ir.mem;

import middle.operand.*;
import middle.operand.symbol.*;

public abstract class AccessMem{
	public Symbol sym;
	public Operand idx;
	public Operand val;

	public AccessMem(Symbol sym, Operand idx, Operand val){
		this.sym = sym;
		this.idx = idx;
		this.val = val;
	}
}
