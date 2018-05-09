package ufrj.dcc.br.udg.model;

import java.util.HashSet;
import java.util.Set;

public class Node {
	
	private Integer index;
	private Set<Integer> neighbors;
	private boolean visited;
	
	public Node() {
		super();
		neighbors = new HashSet<Integer>();
		visited = false;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Set<Integer> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Set<Integer> neighbors) {
		this.neighbors = neighbors;
	}
	
	public void addNeighbor(Integer neighbor){
		this.neighbors.add(neighbor);
	}

	public boolean wasVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public boolean isNeighboor(int neighboorId){
		return neighbors.contains(neighboorId);
	}

	@Override
	public String toString() {
		return "Node [index=" + index + ", neighbors=" + neighbors + "]";
	}
}
