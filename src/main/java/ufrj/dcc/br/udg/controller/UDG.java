package ufrj.dcc.br.udg.controller;

import java.util.Map;

import ufrj.dcc.br.udg.model.ConnectedGraph;
import ufrj.dcc.br.udg.model.DefaultConstants;
import ufrj.dcc.br.udg.model.Node;

public class UDG {

	public String udgRecognition(ConnectedGraph graph){
		String result = DefaultConstants.TRIGRAPH_ONLY;
		double epsilon = DefaultConstants.INITIAL_EPSILON;
		
		while(result.equals(DefaultConstants.TRIGRAPH_ONLY)){
			result = hasDiscreteRealization(graph, epsilon);
			if(epsilon < DefaultConstants.MIN_EPSILON)
				return result;
		}
		return result;
	}
	
	public String hasDiscreteRealization(ConnectedGraph graph, double epsilon){
		Map<Integer, Node> nodes = graph.getNodes();
		int[] placedNodes = new int[nodes.size()];
		int[] breadthFirstPermutationNodes = new int[placedNodes.length];
		
		return "";
	}
	
	/*
	public int[] permuteBreathFirst(ConnectedGraph graph, int numberOfInsertedNodes, int[] result){
		if(result.length == numberOfInsertedNodes)
			return result;
		
		graph.
	}*/
}
