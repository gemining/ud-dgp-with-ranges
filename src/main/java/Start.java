import ufrj.dcc.br.udg.controller.GraphManager;
import ufrj.dcc.br.udg.controller.UDG;

public class Start {
	public static void main(String[] args) {
		GraphManager graphManager = GraphManager.getInstance();
		graphManager.loadGraphFromFile("src/main/resources/graph.txt");
		graphManager.printGraph();
		System.out.println("*************************************");
		
		UDG udg = UDG.getInstance();
		System.out.println("Result: " + udg.udgRecognition(graphManager.getGraph()));
	}
}
