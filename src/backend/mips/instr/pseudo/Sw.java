package backend.mips.instr.pseudo;

import backend.mips.instr.*;
import backend.mips.reg.*;

public class Sw implements Instr{
	public Reg reg;
	public String label;
	public int valOffset;
	public Reg regOffset;

	public Sw(Reg reg, String label, int valOffset){
		this.reg = reg;
		this.label = label;
		this.valOffset = valOffset;
		this.regOffset = null;
	}

	public Sw(Reg reg, String label, Reg regOffset){
		this.reg = reg;
		this.label = label;
		this.valOffset = -1;
		this.regOffset = regOffset;
	}

	@Override
	public String toString(){
		if(regOffset == null)
			return "sw " + reg + ", " + label + "+" + valOffset;
		else return "sw " + reg + ", " + label + "(" + regOffset + ")";
	}
}
