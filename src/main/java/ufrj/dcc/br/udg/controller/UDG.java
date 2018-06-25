package ufrj.dcc.br.udg.controller;


import java.math.BigDecimal;
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
		double epsilon = getDoubleWithPrecisionScale(DefaultConstants.INITIAL_EPSILON);
		
		while(result.equals(DefaultConstants.TRIGRAPH_ONLY)){
			if(epsilon < DefaultConstants.MIN_EPSILON)
				return DefaultConstants.TRIGRAPH_ONLY;
			result = hasDiscreteRealization(graph, epsilon);
			epsilon = refineGranularity(epsilon);
		}
		
		return result;
	}
	
	private String hasDiscreteRealization(ConnectedGraph graph, double epsilon){
		Map<Integer, Node> nodes = graph.getNodes();
		ArrayList<Pair<Integer,Position>> placedNodes = new ArrayList<Pair<Integer,Position>>(nodes.size());
		int[] breadthFirstPermutationIds = permuteBreathFirst(graph, 0);

		return placeNextNode(graph, epsilon, epsilon*DefaultConstants.SQUARED_TWO, breadthFirstPermutationIds, 0, placedNodes);
	}
	
	private String placeNextNode(ConnectedGraph graph, double unRoundedEpsilon, double unRoundedEpsilonSquared2, int[] permutedArray, int nextNodeIndex, ArrayList<Pair<Integer,Position>> placedNodes){
		double epsilon = getDoubleWithPrecisionScale(unRoundedEpsilon);
        double epsilonSquared2 = getDoubleWithPrecisionScale(unRoundedEpsilonSquared2);
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
    private double refineGranularity(double epsilon) {
		return getDoubleWithPrecisionScale(epsilon/2);
	}

    private static double getDoubleWithPrecisionScale(double unRounded){
        return BigDecimal.valueOf(unRounded)
                .setScale(DefaultConstants.PRECISION_SCALE, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    private int[] permuteBreathFirst(ConnectedGraph graph, int root){
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
	
	private HashSet<Position> findPositionsOnMeshInsideCircumference(boolean isNeighboor, double unRoundedRadius, Position center, double unRoundedEpsilon){

	    HashSet<Position> result = new HashSet<Position>();
        double radius = getDoubleWithPrecisionScale(unRoundedRadius);
        double squaredRadius = getDoubleWithPrecisionScale(Math.pow(radius, 2));
        double epsilon = getDoubleWithPrecisionScale(unRoundedEpsilon);
        double outerBoudingBoxdelta = getDoubleWithPrecisionScale((radius % epsilon));

        // Outer bounding-box
        for (double x = 0 - radius + outerBoudingBoxdelta; x <= 0 + radius - outerBoudingBoxdelta; x += epsilon) {
            for (double y = 0 - radius + outerBoudingBoxdelta; y <= 0 + radius - outerBoudingBoxdelta; y += epsilon) {
                double curX = getDoubleWithPrecisionScale(x);
                double curY = getDoubleWithPrecisionScale(y);

                // Inner bounding-box + distance check
                double squaredDistance = getDoubleWithPrecisionScale(getSquaredDistance(center, curX, curY));

                if (isNeighboor) {
                    if(squaredDistance < squaredRadius){
                        result.add(new Position(curX, curY));
                    }
                } else {
                    if(squaredDistance <= squaredRadius){
                        result.add(new Position(curX, curY));
                    }
                }
            }
        }

	    return result;
	}

    private double getSquaredDistance(Position pivotPos, double x, double  y){
		return Math.pow(pivotPos.getX() - x, 2) + Math.pow(pivotPos.getY() - y, 2);
	}

    private boolean isUDGrealization(ConnectedGraph graph, double epsilonSquared2, ArrayList<Pair<Integer,Position>> placedNodes){
	    // checar distancia entre nao vizinhos - distancia tem que ser maior do que 1
        // checar distancia entre vizinhos a distancia tem que ser menor ou igual a 1
        // se todo mundo que esta na area cinza for do mesmo tipo(vizinho ou nao vizinho), eu ja achei tb

        double mandatoryTreshold = Math.pow(1 - epsilonSquared2, 2);
		double forbiddenTreshold = Math.pow(1 + epsilonSquared2, 2);
		boolean grayAreaNeighboor = false;
		boolean grayAreaNotNeighboor = false;

        for(int i=0; i<placedNodes.size()-1; i++){
            Position curPos = placedNodes.get(i).getValue();
            Node curNode = graph.getNode(placedNodes.get(i).getKey());

            for(int k=i+1; k<placedNodes.size(); k++){
				Position nextPos = placedNodes.get(k).getValue();
				Integer nextNodeId = placedNodes.get(k).getKey();
            	double squaredDistance = getSquaredDistance(curPos, nextPos.getX(), nextPos.getY());

            	if(squaredDistance <= mandatoryTreshold){
            		 if(!curNode.isNeighboor(nextNodeId)){
            		 	return false;
					 }
				} else if(squaredDistance >= forbiddenTreshold){
					if(curNode.isNeighboor(nextNodeId)){
						return false;
					}
				} else {
					if(curNode.isNeighboor(nextNodeId) && !grayAreaNotNeighboor){
						grayAreaNeighboor = true;
					} else if (!curNode.isNeighboor(nextNodeId) && !grayAreaNeighboor){
						grayAreaNotNeighboor = true;
					} else {
						return false;
					}
				}
            }
        }

		return true;
	}
}
