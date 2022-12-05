package middle.ir;

import backend.mips.reg.*;

public class Label extends ICode{
	public String val;

	public Label(String val){
		super();
		this.val = val;
	}

	@Override
	public String toString(){ return val + ":"; }

	@Override
	public void genInstr(RegManager regManager){
		instrs.add(new backend.mips.instr.Label(val));
	}
}
