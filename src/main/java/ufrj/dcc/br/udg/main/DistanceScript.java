package ufrj.dcc.br.udg.main;

public class DistanceScript {
    public static void main(String[] args) {
        double x1 = 2.45;
        double y1 = -1.05;
        double x2 = 1.75;
        double y2 = 0.35;

        double squaredDistance = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        System.out.println(squaredDistance);
    }
}
