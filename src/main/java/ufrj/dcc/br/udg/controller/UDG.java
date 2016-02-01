package ufrj.dcc.br.udg.controller;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ufrj.dcc.br.udg.model.ConnectedGraph;
import ufrj.dcc.br.udg.model.DefaultConstants;
import ufrj.dcc.br.udg.model.Node;
import ufrj.dcc.br.udg.model.Position;

public class UDG {
	// Singleton
	private static UDG instance;
	private UDG() {
		
	}
	public static synchronized UDG getInstance() { 
		if (instance == null) 
			instance = new UDG(); 
		return instance; 
	}

	// Algorithms
	public String udgRecognition(ConnectedGraph graph){
		String result = DefaultConstants.TRIGRAPH_ONLY;
		double epsilon = DefaultConstants.INITIAL_EPSILON;
		
		while(result.equals(DefaultConstants.TRIGRAPH_ONLY)){
			result = hasDiscreteRealization(graph, epsilon);
			if(epsilon < DefaultConstants.MIN_EPSILON)
				return result;
			epsilon = epsilon/2;
		}
		return result;
	}
	
	public String hasDiscreteRealization(ConnectedGraph graph, double epsilon){
		Map<Integer, Node> nodes = graph.getNodes();
		ArrayList<Position> placedNodes = new ArrayList<Position>(nodes.size());
		int[] breadthFirstPermutationIds = permuteBreathFirst(graph, 0);
		
		return placeNextNode(graph, epsilon, epsilon*DefaultConstants.SQUARED_TWO, breadthFirstPermutationIds, 0, placedNodes);
	}
	
	public String placeNextNode(ConnectedGraph graph, double epsilon, double epsilonSquared2, int[] permutedArray, int nextNodeIndex, ArrayList<Position> placedNodes){
		Node currNode = graph.getNode(permutedArray[nextNodeIndex]);
		HashSet<Position> possiblePositions = new HashSet<Position>();
		
		// Setting up possible positions
		for(int k=0; k<nextNodeIndex; k++){
			Node kNode = graph.getNode(permutedArray[nextNodeIndex]);
			if(kNode.isNeighboor(nextNodeIndex)){
				Position placedPos = placedNodes.get(k);
				
			} else {
				Position placedPos = placedNodes.get(k);
			}
		}
		
		// Special rules for initial nodes
		if(nextNodeIndex == 0){
			possiblePositions.add(new Position(0, 0));
		} else if(nextNodeIndex == 1){
			
		} else if(nextNodeIndex == 2){
			
		}
		
		boolean foundTrigrah = false;
		for (Position pos : possiblePositions) {
			
		}
		
		return "TRIGRAPH ONLY";
	}
	
	
	// Auxiliar Functions
	public int[] permuteBreathFirst(ConnectedGraph graph, int root){
		int[] result = new int[graph.getNodes().size()];
		int counter = 0;
		List<Node> supportList = new LinkedList<Node>();
		
		// Start from the root node
		Node rootNode = graph.getNode(root);
		rootNode.setVisited(true);
		supportList.add(rootNode);
		
		while(!supportList.isEmpty()){
			Node currNode = supportList.remove(0);
			for (Integer neighborId : currNode.getNeighbors()) {
				Node neighbor = graph.getNode(neighborId);
				if(!neighbor.wasVisited()){
					neighbor.setVisited(true);
					supportList.add(neighbor);
				}
			}
			result[counter] = currNode.getIndex();
			counter++;
		}
		return result;
	}
	
	/*
	public void findPositionOnMesh(boolean isNeighboor){
		if(isNeighboor){
			double x = pivotPos.getX() + incrementX;
			double y = pivotPos.getY();
			
			x = pivotPos.getX();
			y = pivotPos.getY() + incrementY;
			
			
			x = pivotPos.getX();
			y = pivotPos.getY() + incrementY;
			
			
		} else {
			
		}
	}
	
	public void findPositionsOnMesh(double epsilon, Position pivotPos, double deltaMax, boolean isNeighboor){
		HashSet<Position> result = new HashSet<Position>();
		double squaredDeltaMax = Math.pow(deltaMax, 2);
		
		if(isNeighboor){
			int i = 1;
			while(i > 0){
				double x = pivotPos.getX() + (i*epsilon);
				double y = pivotPos.getY();
				if(getSquaredDistance(pivotPos, x, y) < squaredDeltaMax){
					result.add(new Position(x, y));
					result.add(new Position(x*(-1), y));
					
					x = pivotPos.getX();
					y = pivotPos.getY() + (i*epsilon);
					result.add(new Position(x, y));
					result.add(new Position(x, y));
				} else {
					i = 0;
				}
			}
			
			
			i = 1;
			while(i > 0){
				double x = pivotPos.getX() + (i*epsilon);
				double y = pivotPos.getY();
				if(getSquaredDistance(pivotPos, x, y) < squaredDeltaMax){
					result.add(new Position(x, y));
					result.add(new Position(x*(-1), y));
				} else {
					i = 0;
				}
			}
		}
	}*/
	
	public double getSquaredDistance(Position pivotPos, double x, double  y){
		return (double) Math.pow(pivotPos.getX() - x, 2) +Math.pow(pivotPos.getY() - y, 2);
	}
}
