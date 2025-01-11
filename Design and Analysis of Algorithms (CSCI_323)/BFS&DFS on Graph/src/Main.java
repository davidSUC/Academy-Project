import java.util.Iterator;
import java.util.LinkedList;

public class Main {

	public static void main(String[] args) {
		
		// Create myGraph. 0==a, 1==b, 2==c, 3==d, 4==e, 5==f.
		LinkedList<Integer>[] myGraph = new LinkedList[6];
		for(int i=0; i<6; i++) {
			myGraph[i] = new LinkedList<Integer>();
		}
        //Vertices a
        myGraph[0].add(1);
        myGraph[0].add(5);
        //Vertices b
        myGraph[1].add(0);
        myGraph[1].add(2);
        myGraph[1].add(4);
        //Vertices c
        myGraph[2].add(3);
        myGraph[2].add(4);
        //Vertices d
        myGraph[3].add(4);
        //Vertices e
        myGraph[4].add(2);
        //Vertices f
        myGraph[5].add(4);
        myGraph[5].add(0);

        // Reverse version of myGraph
        LinkedList<Integer>[] myGraphR = new LinkedList[6];
        for(int i = 0; i<6; i++) {
        	myGraphR[i] = new LinkedList<Integer>();
        }
        //vertices a
        myGraphR[0].add(1);
        myGraphR[0].add(5);
        //vertices b
        myGraphR[1].add(0);
        //vertices c
        myGraphR[2].add(1);
        myGraphR[2].add(4);
        //vertices d
        myGraphR[3].add(2);
        //vertices e
        myGraphR[4].add(1);
        myGraphR[4].add(2);
        myGraphR[4].add(3);
        myGraphR[4].add(5);
        //vertices f
        myGraphR[5].add(0);


        LinkedList<Integer>[] queueBFS_a = BFS(myGraph, 0);
        LinkedList<Integer> queueBFS_a1 = mergeList(queueBFS_a);
        //System.out.println("Path from a with BFS: "+ queueBFS_a1);
        LinkedList<Integer> queueDFS_a = DFS(myGraphR,0);
        //System.out.println("Path to a with DFS: " + queueDFS_a);
        getStrongCompenent(queueBFS_a1, queueDFS_a);
        
        LinkedList<Integer>[] queueBFS_b = BFS(myGraph, 1);
        LinkedList<Integer> queueBFS_b1 = mergeList(queueBFS_b);
        //System.out.println("Path from b with BFS: "+ queueBFS_b1);
        LinkedList<Integer> queueDFS_b = DFS(myGraphR,1);
        //System.out.println("Path to b with DFS: " + queueDFS_b);
        getStrongCompenent(queueBFS_b1, queueDFS_b);        
        
        LinkedList<Integer>[] queueBFS_c = BFS(myGraph, 2);
        LinkedList<Integer> queueBFS_c1 = mergeList(queueBFS_c);
        //System.out.println("Path from c with BFS: "+ queueBFS_c1);
        LinkedList<Integer> queueDFS_c = DFS(myGraphR,2);
        //System.out.println("Path to c with DFS: " + queueDFS_c);
        getStrongCompenent(queueBFS_c1, queueDFS_c);
        
        LinkedList<Integer>[] queueBFS_d = BFS(myGraph, 3);
        LinkedList<Integer> queueBFS_d1 = mergeList(queueBFS_d);
        //System.out.println("Path from d with BFS: "+ queueBFS_d1);
        LinkedList<Integer> queueDFS_d = DFS(myGraphR,3);
        //System.out.println("Path to d with DFS: " + queueDFS_d);
        getStrongCompenent(queueBFS_d1, queueDFS_d);
        
        LinkedList<Integer>[] queueBFS_e = BFS(myGraph, 4);
        LinkedList<Integer> queueBFS_e1 = mergeList(queueBFS_e);
        //System.out.println("Path from e with BFS: "+ queueBFS_e1);
        LinkedList<Integer> queueDFS_e = DFS(myGraphR,4);
        //System.out.println("Path to e with DFS: " + queueDFS_e);
        getStrongCompenent(queueBFS_e1, queueDFS_e);
        
        LinkedList<Integer>[] queueBFS_f = BFS(myGraph, 5);
        LinkedList<Integer> queueBFS_f1 = mergeList(queueBFS_f);
        //System.out.println("Path from f with BFS: "+ queueBFS_f1);
        LinkedList<Integer> queueDFS_f = DFS(myGraphR,5);
        //System.out.println("Path to f with DFS: " + queueDFS_f);
        getStrongCompenent(queueBFS_f1, queueDFS_f);
        
}//end main

	//DFS function
	public static LinkedList<Integer> DFS(LinkedList<Integer>[] graph, int source){
	    if(graph.length==0) return null;
	    int vNum = graph.length;
	    boolean visited[] = new boolean[vNum];
	    LinkedList<Integer> queue = new LinkedList<Integer>();
	    DFSExplore(graph, source, visited, queue);
	    return queue;
	}
	 public static void DFSExplore(LinkedList<Integer>[] graph, int source, boolean[] visited, LinkedList<Integer> queue){
	    visited[source] = true;
	    queue.add(source);
	
	    Iterator<Integer> i = graph[source].listIterator();
	    while(i.hasNext()){
	    	int n = i.next();
	        if(!visited[n]) {
	            DFSExplore(graph, n, visited, queue);
	        }
	    }
	 }
	 
	 //BFS function
	 public static LinkedList<Integer>[] BFS(LinkedList<Integer>[] graph, int source){
		    if(graph.length==0) return null;
		    int vNum = graph.length;
		    boolean visited[] = new boolean[vNum];
		    LinkedList<Integer>[] queue = new LinkedList[6]; //6 vertices, possible max layer is 6.
	        for(int i = 0; i<6; i++) {
	        	queue[i] = new LinkedList<Integer>();
	        }
		    visited[source] = true;
		    int layer = 0;
		    queue[layer++].add(source); //Set source as root.
		    Iterator<Integer> i = graph[source].listIterator();
		    while(i.hasNext()) {
		    	int n = i.next();
		    	if(!visited[n]) {
		    		queue[layer].add(n);
		    		visited[n] = true;
		    	}
		    }//above code if for setting up the root and layer 1.
		    	BFSExplore(graph, queue, layer, visited);   		
		    return queue;
	 }
	 public static void BFSExplore(LinkedList<Integer>[] graph, LinkedList<Integer>[] queue, int layer, boolean[] visited) {
		 if(queue[layer].isEmpty()==true)return;
		 Iterator<Integer> k = queue[layer].iterator();
		 if(++layer > 5) return;
		 while(k.hasNext()) {
			 int source = k.next();
			 Iterator<Integer> i = graph[source].listIterator();
			 while(i.hasNext()) {
				 int n = i.next();
				 if(!visited[n]) {
					 queue[layer].add(n);
					 visited[n] = true;
				 }
			 }
		 }
		 BFSExplore(graph,queue,layer,visited); 
	 }
	 
	 //Merge BFS array LinkedList all layer into one LinkedList.
	 public static LinkedList<Integer> mergeList(LinkedList<Integer>[] queueBFS) {
	        LinkedList<Integer> queue = new LinkedList();
	        for(int i = 0; i<6; i++) {
	        	if(queueBFS[i].isEmpty()) break;
	        	Iterator<Integer> k = queueBFS[i].iterator();
	        	while(k.hasNext()) {
	        		queue.add(k.next());
	        	}
	        }
		return queue;
		 
	 }
	 
	 //Get strong component. (the union BFS_path_from_vertice & DFS_path_to_vertice)
	 public static void getStrongCompenent(LinkedList<Integer> queueBFS, LinkedList<Integer> queueDFS) {
		 if(queueBFS.size()>=queueDFS.size()) {
			 if(queueBFS.containsAll(queueDFS)) {
				 System.out.println("Strong component: " + queueDFS);
			 }
			 else
			 	System.out.println("No strong component.");
		 }
		 else 
			 if(queueDFS.containsAll(queueBFS)) {
		 		System.out.println("Strong component: " + queueBFS);
		 	 }
		 	 else
		 		System.out.println("No strong component.");
	 }
}
