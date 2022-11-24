package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;
import middle.*;

public class Br implements Instr{
	public Reg r0;
	public Reg r1;
	public Rel rel;
	public String label;

	public Br(Reg r0, Reg r1, Rel rel, String label){
		this.r0 = r0;
		this.r1 = r1;
		this.rel = rel;
		this.label = label;
	}

	@Override
	public String toString(){ return "b" + rel + " " + r0 + ", " + r1 + ", " + label; }
}
