package ufrj.dcc.br.udg.controller;

import ufrj.dcc.br.udg.model.ConnectedGraph;
import ufrj.dcc.br.udg.model.Node;

public class GraphCreator {
	
	private static GraphCreator instance;
	private ConnectedGraph connectedGraph;
	
	private GraphCreator() {
		
	}
	
	public static synchronized GraphCreator getInstance() { 
		if (instance == null) 
			instance = new GraphCreator(); 
		return instance; 
	}

	public boolean createGraph(int[][] graph){
		if(graph.length < 1 || graph.length != graph[0].length){
			return false;
		}
		
		connectedGraph = new ConnectedGraph(graph.length);
		for(int i=0; i< graph.length - 1; i++){
			insertLineToGraph(i, graph[i]);
		}

		return true;
	}
	
	public void insertLineToGraph(int index, int[] line){
		Node currentNode = new Node();
		currentNode.setIndex(index);
		for(int i=0; i< line.length - 1; i++){
			if(line[i] == 1) {
				currentNode.addNeighbor(i);
			}
		}
		connectedGraph.addNode(new Node());
	}

}
