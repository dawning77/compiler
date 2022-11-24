package middle.operand.symbol;

import middle.operand.*;

// only used in passing param, do not add to symbol table
public class SubMat extends Arr{
	public Mat mat;
	public Operand idx;

	public SubMat(Mat mat, Operand idx){
		super("_subMat", mat.type, -1);
		this.mat = mat;
		this.idx = idx;
	}
}
