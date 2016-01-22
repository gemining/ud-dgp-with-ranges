package ufrj.dcc.br.udg.model;

import java.util.HashSet;
import java.util.Set;

public class ConnectedGraph {
	
	public Set<Node> graph;
	
	public ConnectedGraph(int numberOfNodes){
		super();
		graph = new HashSet<Node>(numberOfNodes);
	}

	public void addNode(Node node){
		graph.add(node);
	}
	
	@Override
	public String toString() {
		return "ConnectedGraph [graph=" + graph + "]";
	}
	
}