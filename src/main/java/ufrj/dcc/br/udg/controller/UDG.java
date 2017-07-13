package ufrj.dcc.br.udg.controller;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
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
				return DefaultConstants.TRIGRAPH_ONLY;
			epsilon = refineGranularity(epsilon);
		}
		
		return result;
	}
	
	public String hasDiscreteRealization(ConnectedGraph graph, double epsilon){
		Map<Integer, Node> nodes = graph.getNodes();
		ArrayList<Pair<Integer,Position>> placedNodes = new ArrayList<Pair<Integer,Position>>(nodes.size());
		int[] breadthFirstPermutationIds = permuteBreathFirst(graph, 0);
		
		return placeNextNode(graph, epsilon, epsilon*DefaultConstants.SQUARED_TWO, breadthFirstPermutationIds, 0, placedNodes);
	}
	
	public String placeNextNode(ConnectedGraph graph, double epsilon, double epsilonSquared2, int[] permutedArray, int nextNodeIndex, ArrayList<Pair<Integer,Position>> placedNodes){
		String result;
		HashSet<Position> possiblePositions = new HashSet<Position>();
		HashSet<Position> excludedPositions = new HashSet<Position>();
		
		// Setting up possible positions
		for(int k=0; k<nextNodeIndex; k++){
			Node kNode = graph.getNode(permutedArray[k]);
			if(kNode.isNeighboor(nextNodeIndex)){
				possiblePositions.addAll(findPositionsOnMeshInsideCircumference(true, 1+epsilonSquared2, placedNodes.get(k).getValue(), epsilon));
			} else {
				excludedPositions.addAll(findPositionsOnMeshInsideCircumference(false, 1-epsilonSquared2, placedNodes.get(k).getValue(), epsilon));
			}
		}
		
		possiblePositions.removeAll(excludedPositions);
		
		// Special rules for initial nodes
		if(nextNodeIndex == 0){
			possiblePositions.add(new Position(0, 0));
		} else if(nextNodeIndex == 1){
			for (Position position : (HashSet<Position>) possiblePositions.clone()) {
				if(!(position.getX() > 0 && position.getY() == 0))
					possiblePositions.remove(position);
			}
		} else if(nextNodeIndex == 2){
			for (Position position : (HashSet<Position>) possiblePositions.clone()) {
				if(!(position.getY() >= 0))
					possiblePositions.remove(position);
			}
		}
		
		boolean foundTrigrah = false;
		for (Position pos : possiblePositions) {
			placedNodes.add(new Pair<Integer,Position>(Integer.valueOf(permutedArray[nextNodeIndex]), pos));
			if(nextNodeIndex == graph.getNodes().size()-1){
				foundTrigrah = true;
				if(isUDGrealization(graph, epsilonSquared2, placedNodes)){
					for(Pair<Integer,Position> position : placedNodes) {
						System.out.println("Index: " + position.getKey() + " Position: " + position.getValue());
					}
					return DefaultConstants.CONFIRMED_UDG;
				}
			} else {
				result = placeNextNode(graph, epsilon, epsilonSquared2, permutedArray, nextNodeIndex+1, placedNodes);
				if(result.equals(DefaultConstants.CONFIRMED_UDG))
					return DefaultConstants.CONFIRMED_UDG;
				if(result.equals(DefaultConstants.TRIGRAPH_ONLY))
					foundTrigrah = true;
			}
			placedNodes.remove(nextNodeIndex);
		}
		if(!foundTrigrah)
			return DefaultConstants.NOT_UDG;
		return DefaultConstants.TRIGRAPH_ONLY;
	}
	
	// Auxiliar Functions
	public double refineGranularity(double epsilon) {
		return epsilon/2;
	}
	
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
	
	public HashSet<Position> findPositionsOnMeshInsideCircumference(boolean isNeighboor, double radius, Position center, double epsilon){
		HashSet<Position> result = new HashSet<Position>();
		if(epsilon > radius){
			result.add(center);
			return result;
		}
		double outerBoudingBoxdelta = epsilon - (radius % epsilon);
		double innerBoudingBoxDelta = (radius * DefaultConstants.SQUARED_TWO)/2;
		double squaredRadius = Math.pow(radius, 2);
		
		// Outer bounding-box
		for(double x = center.getX() - radius - outerBoudingBoxdelta; x <= center.getX() + radius + outerBoudingBoxdelta; x+=epsilon){
			for(double y = center.getY() - radius - outerBoudingBoxdelta; y <= center.getY() + radius + outerBoudingBoxdelta; y+=epsilon){
				// Inner bounding-box + distance check
				double squaredDistance = getSquaredDistance(center, x, y);
				if(isInsideBox(center.getX() - innerBoudingBoxDelta, center.getX() + innerBoudingBoxDelta,
							   center.getY() - innerBoudingBoxDelta, center.getY() + innerBoudingBoxDelta, x,y)){
					if(!(isNeighboor && squaredDistance == squaredRadius)){
						result.add(new Position(x, y));
					}
				} else if(squaredDistance <= squaredRadius){
					if(!(isNeighboor && squaredDistance == squaredRadius)){
						result.add(new Position(x, y));
					}
				}
			}
		}
		
		return result;
	}
	
	public boolean isInsideBox(double xMin, double xMax, double yMin, double yMax, double currX, double currY){
		return currX >= xMin && currX <= xMax && currY >= yMin && currY <= yMax;
	}
	
	public double getSquaredDistance(Position pivotPos, double x, double  y){
		return (double) Math.pow(pivotPos.getX() - x, 2) + Math.pow(pivotPos.getY() - y, 2);
	}
	
	public boolean isUDGrealization(ConnectedGraph graph, double epsilonSquared2, ArrayList<Pair<Integer,Position>> placedNodes){
		//double lowerTreshold = Math.pow(1 - epsilonSquared2, 2);
		double higherTreshold = Math.pow(1 + epsilonSquared2, 2);
		
		for(int i=0; i<placedNodes.size(); i++){
			Position curPos = placedNodes.get(i).getValue();
			for(int k=i+1; k<placedNodes.size(); k++){
				double squaredDistance = getSquaredDistance(curPos, placedNodes.get(k).getValue().getX(), placedNodes.get(k).getValue().getY());
				if(squaredDistance < higherTreshold){
					if(!graph.getNode(placedNodes.get(i).getKey()).isNeighboor(placedNodes.get(k).getKey()))
						return false;
				}
			}
		}
		return true;
	}
}
