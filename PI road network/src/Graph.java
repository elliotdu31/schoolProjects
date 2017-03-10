import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/* we use adjacency lists to implement the network
 * 
 */

class Graph {
	int nEdges = 0;
	Vector<Long> verticesid;
	HashMap<Long, Vertex> vertices;
	HashMap<Vertex,LinkedList<Edge>> edges;   //  outgoing edges
	HashMap<Vertex, LinkedList<Edge>> edgesin;//  incoming edges

	public Graph() {
		this.verticesid = new Vector<Long>();
		this.vertices = new HashMap<Long, Vertex>();
		this.edges = new HashMap<Vertex, LinkedList<Edge>>();
		this.edgesin = new HashMap<Vertex, LinkedList<Edge>>();
		this.nEdges = 0;
	}

	public void addVertex(Vertex v) {
		vertices.put(v.id, v);
		verticesid.add(v.id);
	}

	public void addVertex2(Vertex v) {
		vertices.put(v.id, v);
	}
	public void removeVertex(Vertex v){
		vertices.remove(v.id);
		edges.remove(v);
		edgesin.remove(v);
	}
	public void addEdge(Edge e) {
		LinkedList<Edge> l = edges.get(vertices.get(e.id_start));
		if (l == null) {
			l = new LinkedList<Edge>();
			l.add(e);
			edges.put(vertices.get(e.id_start), l);
		} else
			l.add(e);
		LinkedList<Edge> lin = edgesin.get(vertices.get(e.id_end));
		if (lin == null) {
			lin = new LinkedList<Edge>();
			lin.add(e);
			edgesin.put(vertices.get(e.id_end), lin);
		} else
			lin.add(e);
		nEdges++;
	}

	public void removeEdge(Edge e){
		Edge edge=new Edge(e.id_end,e.id_start,e.t);
		if (edges.get(vertices.get(edge.id_start))!=null) edges.get(vertices.get(edge.id_start)).remove(edge);
		if (edgesin.get(vertices.get(edge.id_end))!=null) edgesin.get(vertices.get(edge.id_end)).remove(edge);
		edges.get(vertices.get(e.id_start)).remove(e);
		edgesin.get(vertices.get(e.id_end)).remove(e);
		nEdges--;
	}
	
	LinkedList<Vertex> successors(Vertex p) {
		List<Edge> l = edges.get(p);
		if (l == null)
			l = new LinkedList<Edge>();
		LinkedList<Vertex> s = new LinkedList<Vertex>();
		for(Edge e : l) 
			s.add(this.vertices.get(e.id_end));
		
		return s;
	}

	// freeId is used to add new vertices to the graph
	private long freeId=-1;
	public long getFreeId(){
		return --freeId;
	}
}
