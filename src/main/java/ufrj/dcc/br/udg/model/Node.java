package ufrj.dcc.br.udg.model;

import java.util.HashSet;
import java.util.Set;

public class Node {
	
	public Integer index;
	public Set<Integer> neighbors;
	public boolean visited;
	
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

	@Override
	public String toString() {
		return "Node [index=" + index + ", neighborns=" + neighbors + "]";
	}
}