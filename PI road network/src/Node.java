// this class is used in the Dijkstra algorithm to be able to save the way we came from

public class Node implements Comparable<Node> {
	final Vertex node;
	final double dist;
	Node ancestor;
	
	public Node(Vertex node, int dist) {
		this.node = node;
		this.dist = dist;
		this.ancestor = null;
	}
	
	public Node(Vertex node, double dist, Node ancestor) {
		this.node = node;
		this.dist = dist;
		this.ancestor = ancestor;
	}
	
	public int compareTo(Node n) {
		return (int)(this.dist - n.dist);
	}

	
}