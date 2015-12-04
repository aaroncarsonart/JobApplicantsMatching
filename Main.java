import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Our java code for the Final Project of MTH 354.
 * @author Aaron Carson, Sam Wetzel, Jonathan Elliot
 * @version Dec 4, 2015
 */
public class Main
{	
	// assume input of "A B C D" "1 2 3 4" "A,1,5 A,2,20"
	// edges are input as <vertex 1><vertex 2>,<edge weight>
	// multiple elements are delimited by spaces.
	
	
	/**
	 * Vertices hold all info needed for our marking.
	 * @version Dec 2, 2015
	 */
	public static class Vertex
	{
		String name;
		
		// used for Network flow Algorithm.
		Vertex predecessor = null;
		int value = 0;
		
		@Override
		public int hashCode()
		{
			return name.hashCode();
		}
	}
	
	/**
	 * Edges hold all info needed for the marking algorithm.
	 * @version Dec 2, 2015
	 */
	public static class Edge
	{
		// the vertex the edge is incident to.
		Vertex vertex = null;
		
		// marking for network flow algorithm. each edge has max flow of 1.
		boolean visited = false;
		int weight = 0;
	}
	
	
	/**
	 * Our Main Algorithm.
	 * @param args Args directly from main.
	 */
	public static void findMaximalMatching(String[] args)
	{
		if (args.length != 3) System.out.println("Need 3 arguments.");

		Map<String, Vertex> applicants = new HashMap<>();
		Map<String, Vertex> jobs = new HashMap<>();
		
		// data structure for graph
		Map<Vertex, List<Edge>> adj = new HashMap<>();
		
		// super source
		Vertex source = new Vertex();
		source.name = "source";
		source.value = Integer.MAX_VALUE;
		adj.put(source, new ArrayList<>());
		
		// super sink
		Vertex sink = new Vertex();
		sink.name = "sink";
		sink.value = 0;
		adj.put(sink, new ArrayList<>());
		
		// create applicants.
		for(String a : args[0].split(" ")) {
			Vertex applicant = new Vertex();
			applicant.name = a;
			applicants.put(applicant.name, applicant);
			adj.put(applicant, new ArrayList<>());
			
			// add edge from source to applicant.
			Edge edge = new Edge();
			edge.vertex = applicant;
			adj.get(source).add(edge);

		}

		// create jobs.
		for(String j : args[1].split(" ")) {
			Vertex job = new Vertex();
			job.name = j;
			jobs.put(job.name, job);
			adj.put(job, new ArrayList<>());
			
			// add edge from job to sink.
			Edge edge = new Edge();
			edge.vertex = sink;
			adj.get(job).add(edge);
		}
			
		// create edges from Applicants to Jobs.
		for(String e : args[2].split(" ")) {
			// assume "A,1,5" is edge from A to 1 with weight 5.
			System.out.println("e.length " + e.length());
			String[] parts = e.split(",");
			if (parts.length < 3) continue;
			System.out.println("parts.length " + parts.length);
			String applicantString = parts[0];
			String jobString       = parts[1];
			String weightString    = parts[2];
			
			// create the edge.
			Edge edge = new Edge();	
			edge.vertex    = jobs.get(jobString);
			edge.weight = Integer.parseInt(weightString);
			
			// add the edge to the adj list of the applicant
			adj.get(applicants.get(applicantString)).add(edge);			
		}
		
		for(Vertex v : adj.keySet()) {
			System.out.print("Vertex: " + v.name + " edges: {");
			for(Edge e : adj.get(v)) {
				System.out.print("(" + e.vertex.name + "," + e.weight + "), ");		
			}
			System.out.println("}");
		}

		
		
				
		
	}
	
	// ********************************************************************
	// MAIN
	// ********************************************************************
	
	/**
	 * Entry point of program, run findMaximalMatching().
	 * @param args
	 */
	public static void main(String[] args)
	{
		// build a list of Jobs, and Applicants.
		// build our Graph as Map<String, List<String>>

		// 2 classes for vertices and edges
		
		// vertices need name, and marking: predecessor(prev) and value.
		// edges need from, to vertices, and marking: max and current flow.
		
		// 2 conditions for a new matching to be the best
		//   1. | v1 |  >=  prev | v1 | 
		//   2. if the flow > the previous flow.
		
		findMaximalMatching(args);
	}
}
