package ufrj.dcc.br.udg.model;

import java.util.HashSet;
import java.util.Set;

public class ConnectedGraph {
	
	public Set<Node> nodes;
	
	public ConnectedGraph(int numberOfNodes){
		super();
		nodes = new HashSet<Node>(numberOfNodes);
	}

	public void addNode(Node node){
		nodes.add(node);
	}
	
	public Set<Node> getNodes() {
		return nodes;
	}

	public void setGraph(Set<Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		return "ConnectedGraph [graph=" + nodes + "]";
	}
}