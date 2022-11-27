package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;

public class Mul implements Instr{
	public Reg t1;
	public Reg t2;

	public Mul(Reg t1, Reg t2){
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String toString(){ return "mult " + t1 + ", " + t2; }
}
