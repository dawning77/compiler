package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;

public class Move implements Instr{
	public Reg src;
	public Reg dst;

	public Move(Reg dst, Reg src){
		this.src = src;
		this.dst = dst;
	}

	@Override
	public String toString(){ return "move " + dst + ", " + src; }
}
