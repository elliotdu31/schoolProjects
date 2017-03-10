import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

public class Main {

	/*
	 * Open vis/vis.html to visualize.
	 */

	// static String file = "data/malta.in";
	// static String file = "data/idf.in";
	 static String file = "data/france.in";
	// static String file = "data/man.in";

	// A vertex at the Arc de Triomphe in the france.in file: id:368323
	// lon:2295147 lat:48873166

	public static void main(String[] args) {
		System.out.println("Graph loading...");
		Graph graph = read();
		System.out.println("Graph loaded");

		/*
		 //Question 1.1 and 1.2 
		 double t1=1; 
		 LinkedList<Vertex> l = Djkistra(t1,graph, graph.vertices.get(new Long(368323)));
		 System.out.println(l.size()); if (l.size() == 0) System.out.println(
		 "Aucun sommet. Dommage..."); else write(l, graph.vertices.get(new
		 Long(368323)));
		 */

		
		 //Question 1.3 
		 double t1=1; 
		 double t2=2; 
		 LinkedList<Vertex> l = DjkistraInterm(t1,t2, graph, graph.vertices.get(new Long(368323)),"out"); 
		 System.out.println(l.size()); 
		 if (l.size() == 0) System.out.println("Aucun sommet. Dommage..."); 
		 else write(l,graph.vertices.get(new Long(368323)));
		 
		/*
		 //Question B.6 
		 reachRandom(graph, 1000);
		 */
		System.out.println("Done");
	}

	// the following finds all the points (vertices or points on an edge) at distance t
	// (following quickest path) from source using Djkistra's algorithm.
	static LinkedList<Vertex> Djkistra(double t, Graph graph, Vertex source) { // t
																				// in
																				// hours
		t *= 60 * 60 * 1000;

		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		HashSet<Vertex> visited = new HashSet<Vertex>();
		HashMap<Vertex, Double> distance = new HashMap<Vertex, Double>();
		LinkedList<Node> listeDesPointsADistanceUnPeuSupérieureAt = new LinkedList<Node>();
		LinkedList<Vertex> listeDesPointsADistanceExactementEgaleAt = new LinkedList<Vertex>();

		distance.put(source, 0.);
		queue.add(new Node(source, 0));
		double t1 = 0;
		while (!queue.isEmpty() && t1 < t + 60000) {
			Node n = queue.poll();
			t1 = n.dist;
			if (t1 >= t && n.ancestor.dist < t)
				listeDesPointsADistanceUnPeuSupérieureAt.add(n);
			if (visited.contains(n.node))
				continue;
			visited.add(n.node);
			if (!graph.edges.containsKey(n.node))
				continue;
			for (Edge e : graph.edges.get(n.node)) {
				Vertex v = graph.vertices.get(e.id_end);
				double d = t1 + e.t;
				if (!distance.containsKey(v) || d < distance.get(v)) {
					distance.put(v, d);
					queue.add(new Node(v, d, n));
				}
			}
		}
		int i = 0;

		for (Node n : listeDesPointsADistanceUnPeuSupérieureAt) {
			double q = (t - n.ancestor.dist) / (n.dist - n.ancestor.dist);
			listeDesPointsADistanceExactementEgaleAt.add(new Vertex(i, (1 - q) * n.ancestor.node.lon + q * n.node.lon,
					(1 - q) * n.ancestor.node.lat + q * n.node.lat));
		}
		return listeDesPointsADistanceExactementEgaleAt;
	}

	static LinkedList<Edge> createdEdges = new LinkedList<Edge>();
	static LinkedList<Vertex> createdVertices = new LinkedList<Vertex>();

	/*
	 * the following finds all the points you can be at tMid when you are travelling from
	 * source to a point further that tEnd following quickest path if s="out and
	 * finds all the points from which you can come if you are at v at Mid
	 * following a quickest path to a point at least tEnd away
	 */
	static LinkedList<Vertex> DjkistraInterm(double tMid, double tEnd, Graph graph, Vertex source, String s) { // tMid,tEnd
																												// in
																												// hours
		tMid *= 60 * 60 * 1000;
		tEnd *= 60 * 60 * 1000;

		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		HashSet<Node> visited = new HashSet<Node>();
		HashMap<Vertex, Double> distance = new HashMap<Vertex, Double>();
		LinkedList<Node> listeDesPointsADistanceSupérieureOuEgaleAtEnd = new LinkedList<Node>();

		distance.put(source, 0.);
		queue.add(new Node(source, 0));
		double t1 = 0;
		while (!queue.isEmpty() && t1 <= tEnd + 60000) {
			Node n = queue.poll();
			t1 = n.dist;
			if (t1 >= tEnd)
				listeDesPointsADistanceSupérieureOuEgaleAtEnd.add(n);

			if (visited.contains(n))
				continue;
			visited.add(n);
			if (s.equals("out")) {
				if (!graph.edges.containsKey(n.node))
					continue;
				for (Edge e : graph.edges.get(n.node)) {
					Vertex v = graph.vertices.get(e.id_end);
					double d = t1 + e.t;
					if (!distance.containsKey(v) || d < distance.get(v)) {
						distance.put(v, d);
						queue.add(new Node(v, d, n));
					}
				}
			} else if (s.equals("in")) {
				if (!graph.edgesin.containsKey(n.node))
					continue;
				for (Edge e : graph.edgesin.get(n.node)) {
					Vertex v = graph.vertices.get(e.id_start);
					double d = t1 + e.t;
					if (!distance.containsKey(v) || d < distance.get(v)) {
						distance.put(v, d);
						queue.add(new Node(v, d, n));
					}
				}
			}
		}

		LinkedList<Vertex> listeDesPointsSolutions = new LinkedList<Vertex>();
		// the list we return

		HashSet<Long> vu = new HashSet<Long>();
		// used to store the points we meet and avoid treating them twice

		HashSet<Long> idDictionnary = new HashSet<Long>();
		// used to store the ids of the points we added to our list of solutions

		// we go back from tEnd points to tMid points
		for (Node n : listeDesPointsADistanceSupérieureOuEgaleAtEnd) {
			Node node = n;
			while (node.ancestor != null && node.ancestor.dist > tMid && !vu.contains(node.ancestor.node.id)) {
				if (node.node != null)
					vu.add(node.node.id);
				node = node.ancestor;
			}
			if (node.ancestor != null && node.ancestor.dist > tMid)
				continue;
			if (tMid == node.dist) {
				if (!idDictionnary.contains(node.node.id)) {
					listeDesPointsSolutions.add(node.node);
					idDictionnary.add(node.node.id);
				}
			} else if (tMid == node.ancestor.dist) {
				if (!idDictionnary.contains(node.ancestor.node.id)) {
					listeDesPointsSolutions.add(node.ancestor.node);
					idDictionnary.add(node.ancestor.node.id);
				}
			}
			// the following creates an intermediate vertex
			else {
				double q = (tMid - node.ancestor.dist) / (node.dist - node.ancestor.dist);
				Vertex newVertex = new Vertex(graph.getFreeId(), (1 - q) * node.ancestor.node.lon + q * node.node.lon,
						(1 - q) * node.ancestor.node.lat + q * node.node.lat);
				idDictionnary.add(newVertex.id);

				// we add the new points and new edges to the graph for question
				// B.4
				graph.addVertex2(newVertex);
				createdVertices.add(newVertex);

				if (s.equals("out")) {
					graph.addEdge(new Edge(node.ancestor.node.id, newVertex.id, (tMid - node.ancestor.dist)));
					graph.addEdge(new Edge(newVertex.id, node.node.id, (node.dist - tMid)));
					createdEdges.add(new Edge(node.ancestor.node.id, newVertex.id, (tMid - node.ancestor.dist)));
					createdEdges.add(new Edge(newVertex.id, node.node.id, (node.dist - tMid)));
					if (graph.successors(node.node).contains(node.ancestor.node)) {
						graph.addEdge(new Edge(newVertex.id, node.ancestor.node.id, (tMid - node.ancestor.dist)));
						graph.addEdge(new Edge(node.node.id, newVertex.id, (node.dist - tMid)));
						createdEdges.add(new Edge(newVertex.id, node.ancestor.node.id, (tMid - node.ancestor.dist)));
						createdEdges.add(new Edge(node.node.id, newVertex.id, (node.dist - tMid)));
					}
				} else if (s.equals("in")) {
					graph.addEdge(new Edge(newVertex.id, node.ancestor.node.id, (tMid - node.ancestor.dist)));
					graph.addEdge(new Edge(node.node.id, newVertex.id, (node.dist - tMid)));
					createdEdges.add(new Edge(newVertex.id, node.ancestor.node.id, (tMid - node.ancestor.dist)));
					createdEdges.add(new Edge(node.node.id, newVertex.id, (node.dist - tMid)));
					if (graph.successors(node.ancestor.node).contains(node.node)) {
						graph.addEdge(new Edge(node.ancestor.node.id, newVertex.id, (tMid - node.ancestor.dist)));
						graph.addEdge(new Edge(newVertex.id, node.node.id, (node.dist - tMid)));
						createdEdges.add(new Edge(node.ancestor.node.id, newVertex.id, (tMid - node.ancestor.dist)));
						createdEdges.add(new Edge(newVertex.id, node.node.id, (node.dist - tMid)));
					}
				}
				Node intermediate = new Node(newVertex, (int) tMid, node.ancestor);
				node.ancestor = intermediate;
				listeDesPointsSolutions.add(newVertex);
			}
		}
		return listeDesPointsSolutions;
	}

	/*
	 * the following computes the approximated reach of nombreDeSommets vertices, randomly
	 * chosen in the graph. Might take a few hours to compute on a standard
	 * computer if nombreDeSommets>100.
	 */
	static double[] reachRandom(Graph g, int nombreDeSommets) {
		double[] reaches = new double[nombreDeSommets];

		// ints is an array of nombreDeSommets distinct integers chosen at
		// random between 0 and g.nEdges-1
		int[] ints = new Random().ints(0, g.verticesid.size()).distinct().limit(nombreDeSommets).toArray();
		Vertex v;
		for (int i = 0; i < nombreDeSommets; i++) {
			v = g.vertices.get(g.verticesid.get(ints[i]));
			reaches[i] = reach(minEdge(g, v), g, v);
			System.out.println(reaches[i]);
		}
		return reaches;
	}

	// the following calculates the minimum length of edges coming from or going to v
	static double minEdge(Graph g, Vertex v) {
		double min = Double.MAX_VALUE;
		if (g.edges.get(v)==null || g.edgesin.get(v)==null) return 0;
		for (Edge e : g.edges.get(v)) {
			min = Math.min(min, e.t);
		}
		for (Edge e : g.edgesin.get(v)) {
			min = Math.min(min, e.t);
		}
		return min;
	}
	
	// the following calculates the approximate reach of v using the algorithm described in the report
	static double reach(double tstart, Graph g, Vertex v) {
		double r = tstart;
		LinkedList<Vertex> Tout;
		LinkedList<Vertex> Sin;
		if (r==0) return 0; // in case reach(v)==0
		while (true) {
			for (Edge e : createdEdges)
				g.removeEdge(e);
			for (Vertex w : createdVertices)
				g.removeVertex(w);
			createdEdges.clear();
			createdVertices.clear();
			Tout = DjkistraInterm(r / (2 * 3600 * 1000), r / (3600 * 1000), g, v, "out");
			Sin = DjkistraInterm(r / (2 * 3600 * 1000), r / (3600 * 1000), g, v, "in");
			if (!dist(g, Sin, Tout, r)) {
				return r / (3600 * 1000);
			} else {
				r *= 2;
			}
		}
	}

	static boolean dist(Graph g, LinkedList<Vertex> Sin, LinkedList<Vertex> Tout, double r) {
		for (Vertex s : Sin) {
			if (dist(g, s, Tout, r))
				return true;
		}
		return false;
	}

	// the following says if distance from s to any point in Tout(r/2,r) following quickest
	// path is at least r
	static boolean dist(Graph g, Vertex s, LinkedList<Vertex> Tout, double t) {
		HashSet<Vertex> h = new HashSet<Vertex>();
		h.addAll(Tout);
		PriorityQueue<Node> queue = new PriorityQueue<Node>();
		HashSet<Vertex> visited = new HashSet<Vertex>();
		HashMap<Vertex, Double> distance = new HashMap<Vertex, Double>();
		distance.put(s, 0.);
		queue.add(new Node(s, 0));
		double t1 = 0;
		while (!queue.isEmpty() && t1 < t + 6000) {
			Node n = queue.poll();
			if (visited.contains(n.node))
				continue;
			t1 = n.dist;
			if (h.contains(n.node) && t1 >= t - 1)
				return true;
			visited.add(n.node);
			if (!g.edges.containsKey(n.node))
				continue;
			for (Edge e : g.edges.get(n.node)) {
				Vertex v = g.vertices.get(e.id_end);
				double d = t1 + e.t;
				if (!distance.containsKey(v) || d < distance.get(v)) {
					distance.put(v, d);
					queue.add(new Node(v, d, n));
				}
			}
		}
		return false;
	}

	// reads in file and fills up Graph
	static Graph read() {
		Graph graph = new Graph();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String CurrentLine;
			while ((CurrentLine = br.readLine()) != null) {
				String[] words = CurrentLine.split(" ");
				if (words[0].equals("v"))
					graph.addVertex(new Vertex(Long.parseLong(words[1]), Double.parseDouble(words[2]),
							Double.parseDouble(words[3])));
				else if (words[0].equals("a"))
					graph.addEdge(
							new Edge(Long.parseLong(words[1]), Long.parseLong(words[2]), Integer.parseInt(words[3])));

			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return graph;
	}

	// writes the vertexes of a list l into the file points.js. Open vis.htlm
	// with a web browser to visualize the points.
	static void write(LinkedList<Vertex> list) {
		try {
			PrintWriter writer = new PrintWriter("./vis/points.js", "UTF-8");
			writer.println(
					"// A sequence of points to be plotted, in standard [latitude, longitude] coordinates, expressed in degrees.");
			writer.println();
			writer.println("var plottedPoints = [");
			double latmean = 0;
			double lonmean = 0;
			Vertex[] t = new Vertex[list.size()];
			int k = 0;
			for (Vertex v : list)
				t[k++] = v;
			for (int i = 0; i < list.size() - 1; i++) {
				writer.println(t[i] + ",");
				latmean += t[i].lat * Math.pow(10, -6);
				lonmean += t[i].lon * Math.pow(10, -6);
			}
			writer.println(t[list.size() - 1]);
			latmean += t[list.size() - 1].lat * Math.pow(10, -6);
			lonmean += t[list.size() - 1].lon * Math.pow(10, -6);
			writer.println("];");
			writer.println();
			writer.println("var centralMarker =");
			writer.println("[" + latmean / (double) list.size() + "," + lonmean / (double) list.size() + "]");
			writer.println(";");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void write(LinkedList<Vertex> list, Vertex centralMarker) {
		try {
			PrintWriter writer = new PrintWriter("./vis/points.js", "UTF-8");
			writer.println(
					"// A sequence of points to be plotted, in standard [latitude, longitude] coordinates, expressed in degrees.");
			writer.println();
			writer.println("var plottedPoints = [");
			Vertex[] t = new Vertex[list.size()];
			int k = 0;
			for (Vertex v : list)
				t[k++] = v;
			for (int i = 0; i < list.size() - 1; i++)
				writer.println(t[i] + ",");
			writer.println(t[list.size() - 1]);
			writer.println("];");
			writer.println();
			writer.println("var centralMarker =");
			writer.println(centralMarker);
			writer.println(";");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
