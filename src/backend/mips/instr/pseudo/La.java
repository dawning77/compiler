package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;

public class La implements Instr{
	public Reg res;
	public String label;

	public La(Reg res, String label){
		this.res = res;
		this.label = label;
	}

	@Override
	public String toString(){ return "la " + res + ", " + label; }
}
