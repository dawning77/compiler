package backend.mips.instr.jtype;

import backend.mips.instr.*;

public abstract class JType implements Instr{
	public String label;

	public JType(String label){
		this.label = label;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName().toLowerCase() + " " + label;
	}
}
