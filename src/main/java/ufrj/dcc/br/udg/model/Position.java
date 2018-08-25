package ufrj.dcc.br.udg.model;

public class Position {
	private double x;
	private double y;
	
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Position)) {
            return false;
        }

        Position pos = (Position) o;
        return this.x == pos.x && this.y == pos.y;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (x * 100000);
        result = 27 * result + (int) (y * 100000000);
        return result;
    }

	@Override
	public String toString() {
		return "(x,y) = (" + x + ", " + y + ")";
	}
}
