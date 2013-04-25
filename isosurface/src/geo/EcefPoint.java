package geo;

public class EcefPoint {     
    public double x;
    public double y;
    public double z;

    private double scalar;

    public EcefPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getScalar() {
        return scalar;
    }

    public void setScalar(double scalar) {
        this.scalar = scalar;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("X: ");
        sb.append(x);
        sb.append(", Y: ");
        sb.append(y);
        sb.append(", Z: ");
        sb.append(z);

        return sb.toString();
    }

    public WgsPoint toWgsPoint() {
        return GeoUtils.xyzllh(x, y, z);
    }    
}
