
class Edge {
	final long id_start, id_end;
	final double t; // en ms

	public Edge(long id_start, long id_end, double t) {
		super();
		this.id_start = id_start;
		this.id_end = id_end;
		this.t = t;
	}

	public String toString() {
		return "a " + id_start + " " + id_end + " " + t;
	}

	@Override
	public boolean equals(Object obj) {
		Edge e=(Edge)obj;
		return(e.t==t && e.id_start==id_start && e.id_end==id_end);
	}
	
	
}
