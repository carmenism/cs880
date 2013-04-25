package geo;

public class WgsPoint {
    public double latitude;
    public double longitude;
    public double altitude;

    private double scalar;

    public WgsPoint(double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public WgsPoint(float longitude, float latitude, float altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public double getScalar() {
        return scalar;
    }

    public void setScalar(double scalar) {
        this.scalar = scalar;
    }

    public EcefPoint toEcefPoint() {
        return GeoUtils.llhxyz(latitude, longitude, altitude);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Lat: ");
        sb.append(latitude);
        sb.append(", Lon: ");
        sb.append(longitude);
        sb.append(", Alt: ");
        sb.append(altitude);

        return sb.toString();
    }

    public static void main(String[] args) {
        double lat = -89.0;
        double lon = -179.0;
        double alt = -10.0;

        while (lat < 90.0) {
            while (lon < 180.0) {
                while (alt < 10.0) {
                    WgsPoint gp = new WgsPoint(lon, lat, alt);
                    EcefPoint p = gp.toEcefPoint();
                    WgsPoint gp2 = p.toWgsPoint();

                    double delta = 0.0002;
                    double diffLat = Math.abs(gp2.latitude - gp.latitude);
                    double diffLon = Math.abs(gp2.longitude - gp.longitude);
                    double diffAlt = Math.abs(gp2.altitude - gp.altitude);

                    if (diffLat > delta || diffLon > delta || diffAlt > delta) {
                        System.out.println(gp + " --> " + p + " --> " + gp2);
                    }
                }
                
                lon += 1.0;
                alt = -10.0;
            }

            lat += 1.0;
            lon = -180.0;
        }
    }
}
