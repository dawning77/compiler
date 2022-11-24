package middle;

public enum Rel{

	ne{
		public boolean satisfied(int v0, int v1){ return v0 != v1; }

		public Rel inverse(){ return eq; }
	},
	eq{
		public boolean satisfied(int v0, int v1){ return v0 == v1; }

		public Rel inverse(){ return ne; }
	},
	ge{
		public boolean satisfied(int v0, int v1){ return v0 >= v1; }

		public Rel inverse(){ return lt; }
	},
	gt{
		public boolean satisfied(int v0, int v1){ return v0 > v1; }

		public Rel inverse(){ return le; }
	},
	le{
		public boolean satisfied(int v0, int v1){ return v0 <= v1; }

		public Rel inverse(){ return gt; }
	},
	lt{
		public boolean satisfied(int v0, int v1){ return v0 < v1; }

		public Rel inverse(){ return ge; }
	};

	public abstract boolean satisfied(int val0, int val1);

	public abstract Rel inverse();
}
