package ufrj.dcc.br.udg.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ufrj.dcc.br.udg.model.ConnectedGraph;
import ufrj.dcc.br.udg.model.Node;

public class GraphManager {
	
	private static GraphManager instance;
	private ConnectedGraph connectedGraph;
	
	private GraphManager() {
		
	}
	
	public static synchronized GraphManager getInstance() { 
		if (instance == null) 
			instance = new GraphManager(); 
		return instance; 
	}

	public void createGraph(int[][] graph){
		if(graph.length < 1 || graph.length != graph[0].length){
			return;
		}
		
		connectedGraph = new ConnectedGraph(graph.length);
		for(int i=0; i< graph.length - 1; i++){
			insertLineToGraph(i, graph[i]);
		}
	}
	
	public void insertLineToGraph(int index, int[] line){
		Node currentNode = new Node();
		currentNode.setIndex(index);
		for(int i=0; i <= line.length - 1; i++){
			if(line[i] == 1) {
				currentNode.addNeighbor(i);
			}
		}
		connectedGraph.addNode(currentNode);
	}
	
	public void loadGraphFromFile(String fileName){
		
		List<String> lines = new ArrayList<String>();
		File file = new File(fileName);
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    while ((text = reader.readLine()) != null) {
		    	lines.add(text);
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		
		connectedGraph = new ConnectedGraph(lines.size());
		for(int i=0; i <= lines.size() - 1; i++){
			char[] line = lines.get(i).toCharArray();
			int[] lineArray = new int[line.length];
			
			for(int j=0; j <= line.length - 1; j++){
				lineArray[j] = line[j] - '0';
			}
			insertLineToGraph(i, lineArray);
		}
	}
	
	public void printGraph(){
		System.out.println("Graph:");
		for (Node node : connectedGraph.getNodes().values()) {
			System.out.print("Index: " + node.getIndex() + " - Neighbors: ");
			for (Integer id : node.getNeighbors()) {
				System.out.print("{" + id + "}");
			}
			System.out.println("");
		}
	}
	
	public ConnectedGraph getGraph(){
		return connectedGraph;
	}
}
