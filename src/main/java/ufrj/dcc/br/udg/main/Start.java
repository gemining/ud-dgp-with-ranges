package ufrj.dcc.br.udg.main;

import ufrj.dcc.br.udg.controller.GraphManager;
import ufrj.dcc.br.udg.controller.UDG;
import ufrj.dcc.br.udg.model.Position;
import ufrj.dcc.br.udg.model.UDGResult;
import javafx.util.Pair;

public class Start {
	
	
	public static void main(String[] args) {
        boolean concurrency = true;
        int cores = Runtime.getRuntime().availableProcessors();
		GraphManager graphManager = GraphManager.getInstance();
		graphManager.loadGraphFromFile("src/main/resources/c4.txt");

		graphManager.printGraph();
		System.out.println("*************************************");
		System.out.println("Procesing...");

        UDG udg = UDG.getInstance();
		long startTime = System.currentTimeMillis();
        UDGResult result = udg.udgRecognition(graphManager.getGraph(), concurrency, cores);
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;

	    System.out.println("Total Time: " + elapsedTime + " miliseconds");
        System.out.println("Result: Is UDG? " + result.getResult());
        System.out.println("Epsilon: " + result.getEpsilon());
        if(result.getPlacedNodes() != null){
            for(Pair<Integer,Position> position : result.getPlacedNodes()) {
            	System.out.println("Index: " + position.getKey() + " Position: " + position.getValue());
            }
        }
	}
}
