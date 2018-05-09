package ufrj.dcc.br.udg;

import ufrj.dcc.br.udg.model.DefaultConstants;
import ufrj.dcc.br.udg.model.Position;
import java.math.BigDecimal;
import java.util.HashSet;

public class FastTest {

    public static void main(String[] args) {
        HashSet<Position> result = new HashSet<Position>();
        boolean isNeighboor = false;
        double radius = getDoubleWithBigDecimal(2.4);
        double squaredRadius = getDoubleWithBigDecimal(Math.pow(radius, 2));
        double episolon = getDoubleWithBigDecimal(0.3);
        double outerBoudingBoxdelta = BigDecimal.valueOf((radius % episolon))
                                                .setScale(5, BigDecimal.ROUND_HALF_UP)
                                                .doubleValue();

        // Outer bounding-box
        for (double x = 0 - radius + outerBoudingBoxdelta; x <= 0 + radius - outerBoudingBoxdelta; x += episolon) {
            for (double y = 0 - radius + outerBoudingBoxdelta; y <= 0 + radius - outerBoudingBoxdelta; y += episolon) {
                double curX = getDoubleWithBigDecimal(x);
                double curY = getDoubleWithBigDecimal(y);


                Position center = new Position(0,0);
                // Inner bounding-box + distance check
                double squaredDistance = getDoubleWithBigDecimal(getSquaredDistance(center, curX, curY));

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

        //DEBUG
        for (Position pos :result ) {
            System.out.println(pos);
        }

        System.out.println(radius);
        System.out.println(episolon);

    }

    public static double getDoubleWithBigDecimal(double unRounded){
        return BigDecimal.valueOf(unRounded)
                .setScale(DefaultConstants.PRECISION_SCALE, BigDecimal.ROUND_HALF_DOWN)
                .doubleValue();
    }

    public static double getSquaredDistance(Position pivotPos, double x, double  y){
        return Math.pow(pivotPos.getX() - x, 2) + Math.pow(pivotPos.getY() - y, 2);
    }

}
