package ufrj.dcc.br.udg.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import javafx.util.Pair;
import ufrj.dcc.br.udg.model.*;

public class UDG {
    // Singleton
    private static UDG instance;
    private CallableUDG callableUDG;

    private UDG() {
    }

    public static synchronized UDG getInstance() {
        if (instance == null)
            instance = new UDG();
        return instance;
    }

    // Algorithms
    public UDGResult udgRecognition(ConnectedGraph graph, boolean concurrency, int cores) {
        double epsilon = CallableUDG.getDoubleWithPrecisionScale(DefaultConstants.INITIAL_EPSILON);
        int[] breadthFirstPermutationIds = CallableUDG.permuteBreathFirst(graph, 0);
        UDGResult result = new UDGResult(DefaultConstants.TRIGRAPH_ONLY, null, 0);
        callableUDG = new CallableUDG(graph, epsilon, breadthFirstPermutationIds);

        if (!concurrency) {
            System.out.println("Concurrency - OFF");
            while (result.getResult().equals(DefaultConstants.TRIGRAPH_ONLY)) {
                if (epsilon < DefaultConstants.MIN_EPSILON)
                    return result;
                result = callableUDG.hasDiscreteRealization(graph, epsilon, breadthFirstPermutationIds);
                epsilon = CallableUDG.refineGranularity(epsilon);
            }
        } else {
            System.out.println("Concurrency - ON");
            System.out.println("Threads: " + (cores - 1));
            final ExecutorService pool = Executors.newFixedThreadPool(cores-1);
            final ExecutorCompletionService<UDGResult> completionService = new ExecutorCompletionService<>(pool);

            for (int i = 0; i < (cores - 1); ++i) {
                completionService.submit(new CallableUDG(graph, epsilon, breadthFirstPermutationIds));
                epsilon = CallableUDG.refineGranularity(epsilon);
            }

            for (int i = 0; i < (cores - 1); ++i) {
                try {
                    final Future<UDGResult> future = completionService.take();
                    final UDGResult content = future.get();
                    if(!content.getResult().equals(DefaultConstants.TRIGRAPH_ONLY)){
                        result = content;
                        break;
                    } else {
                        System.out.println("Granularity " + content.getEpsilon() + " ended with TRIGRAPH ONLY");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getCause());
                }
            }
            pool.shutdown();
        }

        return result;
    }
}

class CallableUDG implements Callable<UDGResult> {

    private double currentEpsilon;
    private ConnectedGraph currentGraph;
    private int[] currentBreadthFirstPermutationIds;

    CallableUDG(ConnectedGraph graph, double epsilon, int[] breadthFirstPermutationIds) {
        this.currentGraph = graph;
        this.currentEpsilon = epsilon;
        this.currentBreadthFirstPermutationIds = breadthFirstPermutationIds;
    }

    @Override
    public UDGResult call() {
        return hasDiscreteRealization(currentGraph, currentEpsilon, currentBreadthFirstPermutationIds);
    }

    public UDGResult hasDiscreteRealization(ConnectedGraph graph, double epsilon, int[] breadthFirstPermutationIds){
		Map<Integer, Node> nodes = graph.getNodes();
		ArrayList<Pair<Integer,Position>> placedNodes = new ArrayList<Pair<Integer,Position>>(nodes.size());

		return placeNextNode(graph, epsilon, epsilon*DefaultConstants.SQUARED_TWO, breadthFirstPermutationIds, 0, placedNodes);
	}
	
	private UDGResult placeNextNode(ConnectedGraph graph, double unRoundedEpsilon, double unRoundedEpsilonSquared2, int[] permutedArray, int nextNodeIndex, ArrayList<Pair<Integer,Position>> placedNodes){
		double epsilon = getDoubleWithPrecisionScale(unRoundedEpsilon);
        double epsilonSquared2 = getDoubleWithPrecisionScale(unRoundedEpsilonSquared2);
        UDGResult result;
		HashSet<Position> possiblePositions = new HashSet<Position>();
		HashSet<Position> excludedPositions = new HashSet<Position>();

		// Setting up possible positions
		for(int k=0; k<nextNodeIndex; k++){
			Node kNode = graph.getNode(permutedArray[k]);
			if(kNode.isNeighboor(permutedArray[nextNodeIndex])){
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
					return new UDGResult(DefaultConstants.CONFIRMED_UDG, placedNodes, epsilon);
				}
			} else {
				result = placeNextNode(graph, epsilon, epsilonSquared2, permutedArray, nextNodeIndex+1, placedNodes);
				if(result.getResult().equals(DefaultConstants.CONFIRMED_UDG))
					return result;
				if(result.getResult().equals(DefaultConstants.TRIGRAPH_ONLY))
					foundTrigrah = true;
			}
			placedNodes.remove(nextNodeIndex);
		}
		if(!foundTrigrah)
			return new UDGResult(DefaultConstants.NOT_UDG, null, epsilon);
		return new UDGResult(DefaultConstants.TRIGRAPH_ONLY, placedNodes, epsilon);
	}
	
	// Auxiliar Functions
    public static double refineGranularity(double epsilon) {
		return getDoubleWithPrecisionScale(epsilon/2);
	}

    public static double getDoubleWithPrecisionScale(double unRounded){
        return BigDecimal.valueOf(unRounded)
                .setScale(DefaultConstants.PRECISION_SCALE, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    public static int[] permuteBreathFirst(ConnectedGraph graph, int root){
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
        double outerBoudingBoxdelta = getDoubleWithPrecisionScale((squaredRadius % epsilon));
        double centerX = getDoubleWithPrecisionScale(center.getX());
		double centerY = getDoubleWithPrecisionScale(center.getY());


        // Outer bounding-box
        for (double x = centerX - squaredRadius + outerBoudingBoxdelta; x <= centerX + squaredRadius - outerBoudingBoxdelta; x += epsilon) {
            for (double y = centerY - squaredRadius + outerBoudingBoxdelta; y <= centerY + squaredRadius - outerBoudingBoxdelta; y += epsilon) {
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
