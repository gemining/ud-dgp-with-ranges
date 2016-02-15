import ufrj.dcc.br.udg.controller.GraphManager;
import ufrj.dcc.br.udg.controller.UDG;

public class Start {
	public static void main(String[] args) {
		GraphManager graphManager = GraphManager.getInstance();
		graphManager.loadGraphFromFile("src/main/resources/graph.txt");
		graphManager.printGraph();
		System.out.println("*************************************");
		
		long startTime = System.currentTimeMillis();
		UDG udg = UDG.getInstance();
		System.out.println("Result: " + udg.udgRecognition(graphManager.getGraph()));
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Total Time : " + elapsedTime + " miliseconds");
	}
}
