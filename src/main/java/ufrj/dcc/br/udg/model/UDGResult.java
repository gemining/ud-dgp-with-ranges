package ufrj.dcc.br.udg.model;

import javafx.util.Pair;

import java.util.ArrayList;

public class UDGResult {

    private String result;
    private ArrayList<Pair<Integer,Position>> placedNodes;
    private double epsilon;

    public UDGResult(String result, ArrayList<Pair<Integer,Position>> placedNodes, double epsilon) {
        super();
        this.result = result;
        this.placedNodes = placedNodes;
        this.epsilon = epsilon;
    }

    public String getResult() {
        return result;
    }

    public ArrayList<Pair<Integer, Position>> getPlacedNodes() {
        return placedNodes;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setPlacedNodes(ArrayList<Pair<Integer, Position>> placedNodes) {
        this.placedNodes = placedNodes;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public String toString() {
        return "UDGResult [result=" + result + ", placedNodes=" + placedNodes.toString() + ", epsilon=" + epsilon + "]";
    }
}
