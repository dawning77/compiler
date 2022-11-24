package backend.mips.instr;

public class Label implements Instr{
	public String label;

	public Label(String label){
		this.label = label;
	}

	@Override
	public String toString(){ return label + ':'; }
}
