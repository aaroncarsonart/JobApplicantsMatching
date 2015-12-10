/*
 
my code directory:
------------------
cd ~/"code/eclipse luna/workspace/MTH 354 - Applicant Job Matching/bin"


example programs:
----------------
java Main "A B C" "1 2 3" "A,1,1 A,2,2 A,3,3 B,1,4 B,2,5 B,3,6 C,1,7 C,2,8 C,3,9"
java Main "Aaron Jon Sam" "Writer Coder Prover" "Aaron,Coder,5 Aaron,Writer,2 Aaron,Prover,1 Jon,Writer,4 Jon,Prover,6 Jon,Coder,3 Sam,Writer,6 Sam,Prover,4 Sam,Coder,1"
java Main "April Bob Carl Dan" "Cashier Janitor Manager Engineer" "April,Cashier,1 April,Manager,3 April,Engineer,10 Bob,Cashier,4 Bob,Janitor,10 Bob,Manager,6 Carl,Cashier,10 Carl,Janitor,3 Carl,Manager,1 Carl,Engineer,5 Dan,Manager,10 Dan,Engineer,2"
java Main "April Bob Carl Dan Emily" "Cashier Janitor Manager Engineer Teacher" "April,Cashier,1 April,Manager,2 April,Engineer,5 Bob,Cashier,4 Bob,Janitor,2 Bob,Manager,1 Bob,Teacher,3 Carl,Cashier,10 Carl,Janitor,9 Carl,Manager,8 Carl,Engineer,7 Dan,Manager,8 Dan,Engineer,10 Emily,Cashier,1 Emily,Janitor,2 Emily,Manager,3 Emily,Engineer,5 Emily,Teacher,10"
java Main "April Bob Carl Dan Emily Zach" "Cashier Janitor Manager Engineer Teacher Officer" "April,Officer,2 April,Cashier,1 April,Manager,2 April,Engineer,5 Bob,Cashier,4 Bob,Janitor,2 Bob,Manager,1 Bob,Teacher,3 Carl,Cashier,10 Carl,Janitor,9 Carl,Manager,8 Carl,Engineer,7 Dan,Manager,8 Dan,Engineer,10 Emily,Cashier,1 Emily,Janitor,2 Emily,Manager,3 Emily,Engineer,5 Emily,Teacher,10 Zach,Cashier,2 Zach,Officer,7"
*/
//--------------------------------------------------------------------------

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Our java code for the Final Project of MTH 354.
 * 
 * @author Aaron Carson, Sam Wetzel, Jonathan Elliot
 * @version Dec 4, 2015
 */
public class Main
{
	// ========================================================================
	// Data Structures for Algorithm.
	// ========================================================================
	public static int		CAPACITY	= 1;		// capacity of each edge
	public static String	SOURCE		= "SOURCE"; // source name
	public static String	SINK		= "SINK";	// sink name.
	public static int		COUNT		= 0;		// for counting permutations
	
	public static String    INDENT      = "    ";
	public static boolean	DEBUG       = false;
	
	
	/**
	 * Vertices hold all info needed for our marking.
	 * 
	 * @version Dec 2, 2015
	 */
	public static class Vertex
	{
		String	name;
		
		// used for Network flow Algorithm.
		Vertex	predecessor	= null;
		int		value		= 0;
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	/**
	 * Edges hold all info needed for the marking algorithm.
	 * 
	 * @version Dec 2, 2015
	 */
	public static class Edge
	{	
		Vertex	tailVertex = null;
		Vertex	headVertex = null; 
		
		// marking for network flow algorithm. each edge has capacity of 1.
		int		flow	= 0;
		int		weight	= 0;
	}
	
	/**
	 * MatchingData allows for easy passing of data to recursive functions.
	 * 
	 * @version Dec 9, 2015
	 */
	public static class MatchingData
	{
		// unchanging data
		Vertex source;		// super-source
		Vertex sink; 		// super-sink
		Map<Vertex, List<Edge>> adj;    // adjacency lists
		List<Edge> edges;				// all edges used
		List<Vertex> vertices;			// all vertices used
		
		// changes each permutation.
		List<Vertex> applicantOrdering; // current applicant ordering. 
		List<Vertex> jobOrdering;		// current job ordering
		List<Vertex> sinkOrdering;      // a list of only the SINK
		int applicantCount;         // total number of checked applicants
		int jobCount;               // total number of checked jobs

		// current maximal data.
		List<Edge>   maximalEdges;  // the edges for the maximal matching.
		int          preferenceSum; // the sum of weights of maximal matching
		int          count;         // how many permutations were run
		int          permutation;   // what permutation is max matching from
	}
	

	
	// ========================================================================
	// Maximal Matching Algorithm
	// ========================================================================
	
	/**
	 * Our main algorithm.
	 * 
	 * This requires a string array of length 3, with members of the form {
	 * "applicants", "jobs", "edges"}.
	 * 
	 * "applicants" and "jobs" contain the name of each applicant or job
	 * represented as a single word, delimited by spaces.
	 * 
	 * "edges" contains each preference as an edge. Each edge is of the form
	 * "applicant,job,preference" (delimited by commas). This is a directed edge
	 * from the applicant to the job with a weight equal to the preference. each
	 * edge is delimited by spaces in the "edges" string.
	 * 
	 * An example input would be: "April Bob Carl Dan Emily"
	 * "Cashier Janitor Manager Engineer Teacher"
	 * "April,Cashier,1 April,Engineer,5 Bob,Janitor,2 Emily,Teacher,7"
	 * 
	 * The algorithm uses the order that vertices are passed for choosing which
	 * vertex to select next.
	 * 
	 * @param args Args directly from main.
	 */
	public static void findMaximalMatching(String[] args) {
		
		System.out.println("================================================");
		System.out.println("Network Matching Algorithm");
		System.out.println("================================================");
		System.out.println("By Aaron Carson, Jon Elliot, and Sam Weiss.");
		System.out.println();
		System.out.println("MTH 354, Fall Term 2015 (WOU)");
		System.out.println("================================================");
		System.out.println();
		System.out.println();
		
		// ---------------------------------------------
		// Error checking arguments, need exactly 3.
		// ---------------------------------------------
		if (args.length != 3)
		{ 
			System.out.println("Need 3 arguments.\n\n");
			return;
		}
		
		long time = System.currentTimeMillis();
		// parse the inputs.
		String applicantsString = args[0];
		String jobsString = args[1];
		String edgesString = args[2];
		
		// ==================================================================
		// Step 1: Get Vertices and Edges from input, and add to collections.
		// ==================================================================

		// map for efficient lookup of applicant and job names.
		Map<String, Vertex> applicantMap = new HashMap<>();
		Map<String, Vertex> jobMap = new HashMap<>();
		
		// collection of all vertices and edges.
		List<Vertex> allVertices = new ArrayList<>();
		List<Edge> allEdges = new ArrayList<>();
		
		// adjacency lists.
		Map<Vertex, List<Edge>> adj = new HashMap<>();
		
		// ----------------
		// 1. super source
		// ----------------
		Vertex source = new Vertex();
		source.name = SOURCE;
		source.value = Integer.MAX_VALUE;
		adj.put(source, new ArrayList<>());
		allVertices.add(source);

		// --------------------------------------
		// create applicants.
		// --------------------------------------
		for (String a : applicantsString.split(" ")) {
			Vertex applicant = new Vertex();
			
			applicant.name = a;
			applicantMap.put(applicant.name, applicant);
			adj.put(applicant, new ArrayList<>());
			allVertices.add(applicant);
		}
		
		// --------------------------------------
		// create jobs.
		// --------------------------------------
		for (String j : jobsString.split(" ")) {
			Vertex job = new Vertex();
			
			job.name = j;
			jobMap.put(job.name, job);
			adj.put(job, new ArrayList<>());
			allVertices.add(job);
		}
		
		// ----------------
		// super sink
		// ----------------
		Vertex sink = new Vertex();
		sink.name = SINK;
		sink.value = 0;
		adj.put(sink, new ArrayList<>());		
		allVertices.add(sink);

		
		// Sets to determine which Vertices to use.
		Set<Vertex> incidentApplicants = new HashSet<>();
		Set<Vertex> incidentJobs = new HashSet<>();
				
		// --------------------------------------
		// create edges from Applicants to Jobs.
		// --------------------------------------
		for (String e : edgesString.split(" ")) {
			// assume "A,1,5" is edge from A to 1 with weight 5.
			String[] parts = e.split(",");
			
			// skip if string is malformed.
			if (parts.length < 3) continue;
			
			//get needed values from parts.
			String applicantString = parts[0];
			String jobString = parts[1];
			int weight = Integer.parseInt(parts[2]);

			// get vertices.
			Vertex applicant = applicantMap.get(applicantString);
			Vertex job = jobMap.get(jobString);

			// create the edge.
			Edge edge = new Edge();
			edge.tailVertex = applicant;
			edge.headVertex = job;
			edge.weight = weight;
			
			// add the edge to the adjacency list of the applicant
			adj.get(applicant).add(edge);
			allEdges.add(edge);
			
			// put both vertices of edge into available sets.
			incidentApplicants.add(applicant);
			incidentJobs.add(job);
		}
		
		// ==============================================================
		// Step 2:  Prepare to run network matching permutations.
		// ==============================================================
		
		// matching data holds all persistent info needed for recursion.
		MatchingData m = new MatchingData();
		
		// persistent data during entire algorithm
		m.source = source;
		m.sink = sink;
		m.adj = adj;	
		m.vertices = allVertices;
		m.edges = allEdges;
		
		//-----------------------------------
		// Get applicants that have an edge.
		//-----------------------------------
		m.applicantOrdering = new ArrayList<>();
		for (Vertex applicant : incidentApplicants){
			m.applicantOrdering.add(applicant);

			// add an edge from source to applicant.
			Edge edge = new Edge();
			edge.tailVertex = source;
			edge.headVertex = applicant;
			adj.get(source).add(edge);
			allEdges.add(edge);
		}
		m.applicantCount = m.applicantOrdering.size();
		
		//-----------------------------------
		// Get jobs that have an edge.
		//-----------------------------------
		m.jobOrdering = new ArrayList<>();
		for (Vertex job : incidentJobs){
			m.jobOrdering.add(job);
			
			// add edge from job to sink.
			Edge edge = new Edge();
			edge.tailVertex = job;
			edge.headVertex = sink;
			adj.get(job).add(edge);
			allEdges.add(edge);
		}
		m.jobCount = m.jobOrdering.size();
		
		// sink ordering, for simpler algorithm design.
		m.sinkOrdering = new ArrayList<>();
		m.sinkOrdering.add(m.sink);
		
		// fields for storing our current maximal matching.
		m.maximalEdges = new ArrayList<>();
		m.preferenceSum = 0;
		m.count = 0;
		
		if(DEBUG)
		{
			System.out.println("All Vertices and Edges:");
			System.out.println("-----------------------");
		}
		printVertices(allVertices);
		printEdges(allEdges);
		
		if(DEBUG)
		{
			System.out.println("\n\nVertices to be used:");
			System.out.println("--------------------");
			printVertices(m.applicantOrdering, "Applicants: ");
			printVertices(m.jobOrdering      , "Jobs:       ");
			System.out.println();
			System.out.println();
		}
		
		// ==============================================================
		// Step 3:  Check matchings of each permutation.
		// ==============================================================
		
		// ------------
		// run once.
		// ------------
		// maximalMatchingFor(m);
		
		// ---------------------------
		// run on all permutations.
		// ---------------------------
		permuteApplicants(m, 0);
		time = System.currentTimeMillis() - time;
		
		// report results.
		System.out.print("\n\n");
		System.out.println("----------------------------------");
		System.out.println("Finished algorithm.");
		System.out.println("----------------------------------");
		System.out.printf("total permutations: %d\n", m.count);
		System.out.printf("running time:       %.3f s\n", time / 1000.0);
		printMatching(m);
	}
	

	/**
	 * Helper function to permute the applicants.
	 * This is the 1st stage of permutation.
	 * 
	 * @param m The MatchingData for the algorithm.
	 * @param i The index permuting through the Applicants.
	 */
	public static void permuteApplicants(MatchingData m, int i) {
		if(i < m.applicantCount) 
		{
			for (int j = i; j < m.applicantCount; j++) 
			{
				swap(m.applicantOrdering, i, j);
				permuteApplicants(m, i + 1);
				swap(m.applicantOrdering, i, j);
			}	
		}
		else // when an applicant permutation is complete, permute the jobs.
		{
			permuteJobs(m, 0);
		}
	}
	
	/**
	 * Helper function to permute the jobs. 
	 * This is the 2nd stage of permutation.
	 *  
	 * @param m The MatchingData for the algorithm.
	 * @param i The index permuting through the Jobs.
	 */
	public static void permuteJobs(MatchingData m, int i){
		if(i < m.jobCount) 
		{
			for (int j = i; j < m.jobCount; j++) 
			{
				swap(m.jobOrdering, i, j);
				permuteJobs(m, i + 1);
				swap(m.jobOrdering, i, j);
			}	
		}
		else 
		{
			//------------------------------------------
			// when a job permutation is complete, 
			// find a matching of the permutation.
			//------------------------------------------
			maximalMatchingFor(m);			
		}
	}
	
	/**
	 * Find the maximal matching for the given permutation, and if it is
	 * better than the previous maximum, set it as the maximum.
	 * 
	 * A maximal matching should be selected if:
	 * 1. It uses as many or more applicants than the previous maximum.
	 * 2. It has a greater sum of edge weights.
	 * 
	 * @param m The MatchingData to use.
	 */
	public static void maximalMatchingFor(MatchingData m)
	{		
		// count the permutation.
		m.count++;
		
		// print some labels for debugging.
		if(DEBUG){
		System.out.println("=============================================");
		System.out.printf( "Permutation: %d\n", m.count);
		System.out.println("=============================================");
		printVertices(m.applicantOrdering, "Applicant ordering: ");
		printVertices(m.jobOrdering,       "Job ordering:       ");
		System.out.println("Vertices:");
		for(Vertex v : m.vertices){
			System.out.printf("%8s:(%s,%s)\n", v.name, v.predecessor, v.name == SOURCE ? "∞" : "" + v.value);
		}
		System.out.println("Edges:");
		for(Edge e : m.edges){
			System.out.printf("%8s --> %-8s (flow: %d, weight: %d)\n", e.tailVertex, e.headVertex, e.flow, e.weight);
		}
		}
		// -----------------------------------------------
		// 1. Visit each vertex with the network flow 
		//    algorithm until the sink cannot be reached.
		// -----------------------------------------------
		Vertex result = null;
		while (result != m.source) 
		{
			if(DEBUG) System.out.println("visit();");
			// always returns the sink for a successful pass.
			result = visit(m.source, m, 0);
		}
	
		// -----------------------------------------------
		// 2. Compare the current matching with the 
		//    current maximal matching.
		// -----------------------------------------------

		// count marked edges and weights.
		int edgeCount = 0;
		int weightSum = 0;
		for(Edge e : m.edges) {
			if(DEBUG) 
				if (e.flow > 0) 
					System.out.printf("%d. %8s ---(flow: %d weight: %2d)---> %s\n", m.count, e.tailVertex, e.flow, e.weight, e.headVertex);
			
			if(e.weight > 0) {
				edgeCount += e.flow;
				if (e.flow > 0) weightSum += e.weight;
			}
			
		}
		if(DEBUG) {
			System.out.println(m.count + " edgeCount: " + edgeCount);
			System.out.println(m.count + " weightSum: " + weightSum);
		}
		
		
		// --------------------------------------------------
		// 3. If at least as many edges as before,
		//    AND the current sum is greater:
		//    found a better one!
		// --------------------------------------------------
		if(m.maximalEdges.size() <= edgeCount && m.preferenceSum < weightSum) 
		{
			List<Edge> newMax = new ArrayList<>();
			
			// add edges that were part of the flow and have a weight.
			for(Edge e :m.edges)
				if(e.flow != 0 && e.weight != 0)
					newMax.add(e);
			
			// put new maximal matching values into MatchingData.
			m.preferenceSum = weightSum;
			m.maximalEdges = newMax;
			m.permutation = m.count;
			
			// report for debugging.
			if(DEBUG) 
			{
				System.out.println("Found new matching.");
				System.out.printf("Permutation: %d\n", m.count);
				printVertices(m.applicantOrdering, "    Applicant ordering: ");
				printVertices(m.jobOrdering,       "    Job ordering:       ");
				printMatching(m);
				System.out.println("\n");
			}	
		}
		
		// ---------------------------------------------
		// 3. Reset the marking of edges and vertices.
		// ---------------------------------------------
		for(Edge e : m.edges) 
		{
			e.flow = 0;
		}
		for(Vertex v : m.vertices)
		{
			v.value = 0;
			v.predecessor = null;
		}
		
		

	}
	
	/**
	 * Recursive method to visit vertices.  Each completion of this method call
	 * creates a single path through the network from SOURCE to SINK.  
	 * 
	 * Each successful pass of this function returns the SINK vertex.  Calling
	 * this function until SOURCE is returned guarantees a maximal matching
	 * has been found for the given ordering.
	 * 
	 * @param current The vertex to visit.
	 * @param adj The Map of Vertices to Edge Lists.
	 * @param applicantOrdering The ordering to use for applicants.
	 * @param jobOrdering THe ordering to use for jobs.
	 * @param sink The terminal vertex.
	 * @param depth The current depth of recursive calls.
	 * @return Either the current vertex, or Sink if it is reached.
	 */
	public static Vertex visit(Vertex current, MatchingData m, int depth) 
	{
		List<Vertex> ordering;
		switch (depth) {
		case 0:  ordering = m.applicantOrdering; break;
		case 1:  ordering = m.jobOrdering;       break;
		case 2:  ordering = m.sinkOrdering;      break;
		default: ordering = new ArrayList<>();   break;
		}
		
		// edges for this recursive step.
		List<Edge> edges = m.adj.get(current);
		
		// using the given ordering,
		for (int i = 0; i < ordering.size(); i++) {
			Vertex v = ordering.get(i);			
			// -----------------------------------------------------
			// visit each edge, mark it and see if it is the sink.
			// -----------------------------------------------------
			for (Edge e : edges) {
				
				if(DEBUG) { 
					for (int j = 0; j < depth; j++) System.out.print(INDENT);
					System.out.printf("%s ---> %-8s", e.tailVertex, e.headVertex);
				}

				
				// if the edge isn't full (hasn't been visited)
				if (v == e.headVertex && e.flow < CAPACITY) {
					if(DEBUG) System.out.println();
					e.flow = 1;                     // mark flow.
					e.headVertex.predecessor = current; // mark previous vertex
					e.headVertex.value = 1;             // mark vertex's content.
					Vertex next = visit(e.headVertex, m, depth + 1);
					
					// case 1: if it is the sink, then done!
					if (next == m.sink) {
						//for (int j = 0; j < depth; j++) System.out.print(indent);
						if(DEBUG) {
							for (int j = 0; j < depth; j++) System.out.print(INDENT);
							System.out.print(v.name);
							System.out.println(" Done!");
						}
						return m.sink;  // finished.
					}
					
					// otherwise, remove marking and visit the next vertex.
					else {
						e.flow = 0;
						e.headVertex.predecessor = null;
						e.headVertex.value = 0;
					}
				}
				else{
					if(DEBUG) System.out.println(" Skip ...");
					continue;

				}
			}
		}
		
		// after all edges have been visited, return current.
		return current;
	}
		
	/**
	 * Swap 2 elements in the given list.
	 * 
	 * @param elements The list of elements.
	 * @param i Index of the 1st element to swap.
	 * @param j Index of the 2nd element to swap
	 */
	public static void swap(List<Vertex> elements, int i, int j) {
		Vertex tmp = elements.get(i);
		elements.set(i, elements.get(j));
		elements.set(j, tmp);
	}
	
	// ========================================================================
	// Helper Methods
	// ========================================================================
	

	
	/**
	 * Print the matching for a List of edges.
	 * @param edges The list of edges to print.
	 */
	public static void printMatching(MatchingData m)
	{
		System.out.println("# of edges:       " + m.maximalEdges.size());
		System.out.println("sum of weights:   " + m.preferenceSum);
		System.out.println("from permutation: " + m.permutation);
		
		String f = "%-8s ---(%2d)--> %-8s\n";
		for (Edge e : m.maximalEdges) 
		{
			System.out.printf(f, e.tailVertex.name, e.weight, e.headVertex.name);
		}
	}
	
	
	public static int LINE_LIMIT = 9;
	
	/**
	 * Helper function to print a list of vertices.
	 * 
	 * @param elements The list.
	 */
	static void printVertices(List<Vertex> elements) {
		printVertices(elements, "Vertices: ");
	}
	
	/**
	 * Helper function to print a list of vertices.
	 * 
	 * @param elements The list.
	 * @param prefix A string to prefix it with.
	 */
	static void printVertices(List<Vertex> elements, String prefix) {
		// opening curly brace.
		int n = elements.size();
		boolean large = n >= LINE_LIMIT;
		if (large) System.out.printf("%s\n{\n", prefix);
		else System.out.printf("%s{ ", prefix);
		
		// print the contents.
		String delimiter = large ? ",\n" : ", ";
		String spacer    = large ? "    " : "";

		for (int i = 0; i < n; i++) {
			System.out.printf(spacer + "%s", elements.get(i));
			if (i != n - 1) System.out.print(delimiter);
		}
		
		// Closing curly brace.
		System.out.println((large ? "\n" : " ") + " }");
	}

	/**
	 * Helper function to print a list of vertices.
	 * 
	 * @param elements The list.
	 */
	static void printEdges(List<Edge> elements) {
		printEdges(elements, "Edges: ");
	}
	
	/**
	 * Helper function to print a list of vertices.
	 * 
	 * @param elements The list.
	 * @param prefix A string to prefix it with.
	 */
	static void printEdges(List<Edge> elements, String prefix) {
		int n = elements.size();
		boolean large = n >= LINE_LIMIT;
		if (large) System.out.printf("%s\n{\n", prefix);
		else System.out.printf("%s{ ", prefix);
		
		// print the contents.
		String delimiter = large ? ",\n" : ", ";
		String format    = large ? "    (%s, %s, %d)" : "(%s,%s,%d)";

		// print the contents.
		for (int i = 0; i < n; i++) {
			Edge e = elements.get(i);
			System.out.printf(format, 
					e.tailVertex, e.headVertex, e.weight);
			
			// add comma if not last item.
			if (i != n - 1) System.out.print(delimiter);
		}
		
		// Closing curly brace.
		System.out.println((large ? "\n" : " ") + " }");
	}

		
	// ========================================================================
	// MAIN
	// ========================================================================
	
	/**
	 * Entry point of program, run findMaximalMatching().
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		findMaximalMatching(args);
	}
}
