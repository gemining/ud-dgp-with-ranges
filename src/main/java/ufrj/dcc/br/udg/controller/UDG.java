package ufrj.dcc.br.udg.controller;

import ufrj.dcc.br.udg.model.ConnectedGraph;
import ufrj.dcc.br.udg.model.DefaultConstants;

public class UDG {

	public String udgRecognition(ConnectedGraph graph){
		String result = DefaultConstants.TRIGRAPH_ONLY;
		double epsilon = DefaultConstants.INITIAL_EPSILON;
		
		while(result.equals(DefaultConstants.TRIGRAPH_ONLY)){
			//result = hasDiscreteRealization(graph, epsilon);
			if(epsilon < DefaultConstants.MIN_EPSILON)
				return result;
		}
		return result;
	}
	
}
