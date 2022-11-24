package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;

public class Li implements Instr{
	public Reg reg;
	public int val;

	public Li(Reg reg, int val){
		this.reg = reg;
		this.val = val;
	}

	@Override
	public String toString(){ return "li " + reg + ", " + val; }
}
