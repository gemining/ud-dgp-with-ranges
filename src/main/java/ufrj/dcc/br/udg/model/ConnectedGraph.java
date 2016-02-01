package ufrj.dcc.br.udg.model;

import java.util.HashMap;
import java.util.Map;

public class ConnectedGraph {
	
	private Map<Integer, Node> nodes;
	
	public ConnectedGraph(int numberOfNodes){
		super();
		nodes = new HashMap<Integer, Node>(numberOfNodes);
	}

	public void addNode(Node node){
		nodes.put(node.getIndex(), node);
	}
	
	public Map<Integer, Node> getNodes() {
		return nodes;
	}
	
	public Node getNode(Integer id){
		return nodes.get(id);
	}

	public void setGraph(Map<Integer, Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		return "ConnectedGraph [graph=" + nodes + "]";
	}
}