package geo;

/**
 * Represents an Earth-centered, Earth-fixed (ECEF) x, y, z point. Each
 * coordinate is measured in kilometers. In ECEF, (0, 0, 0) is the center of the
 * Earth. Also can store a scalar value (such as temperature) with the point.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class EcefPoint {
    public double x;
    public double y;
    public double z;

    private double scalar;

    /**
     * Creates a new ECEF point.
     * 
     * @param x
     *            The x coordinate in kilometers.
     * @param y
     *            The y coordinate in kilometers.
     * @param z
     *            The z coordinate in kilometers.
     */
    public EcefPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Converts to a WGS 1984 point (longitude, latitude, and altitude).
     * 
     * @return The point in WGS 1984.
     */
    public WgsPoint toWgsPoint() {
        return GeoUtils.xyzllh(x, y, z);
    }

    public double getScalar() {
        return scalar;
    }

    public void setScalar(double scalar) {
        this.scalar = scalar;
    }

    @Override
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
}
