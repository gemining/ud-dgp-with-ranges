import ufrj.dcc.br.udg.controller.GraphManager;

public class Start {
	public static void main(String[] args) {
		GraphManager graphManager = GraphManager.getInstance();
		graphManager.loadGraphFromFile("src/main/resources/graph.txt");
		graphManager.printGraph();
	}
}
