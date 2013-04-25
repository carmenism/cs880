package geo;

// http://www.oc.nps.edu/oc2902w/coord/geodesy.js

public class GeoUtils {
    public static double wgs84a = 6378.137;
    public static double wgs84f = 1.0 / 298.257223563;
    public static double wgs84b = wgs84a * (1.0 - wgs84f);
    public static double eccsq = 1 - (wgs84b * wgs84b) / (wgs84a * wgs84a);
    public static double ecc = Math.sqrt(eccsq);

    public static double rearth(double lat) {
        double[] rrnrm = radcur(lat);
        double r = rrnrm[0];

        return r;

    }

    public static double[] radcur(double lat) {

        /*
         * compute the radii at the geodetic latitude lat (in degrees)
         * 
         * input: lat geodetic latitude in degrees output: rrnrm an array 3 long
         * r, rn, rm in km
         */
        double[] rrnrm = new double[3];

        double dtr = Math.PI / 180.0;

        // -------------------------------------

        double a = wgs84a;
        double b = wgs84b;

        double asq = a * a;
        double bsq = b * b;
        double eccsq = 1 - bsq / asq;

        double clat = Math.cos(dtr * lat);
        double slat = Math.sin(dtr * lat);

        double dsq = 1.0 - eccsq * slat * slat;
        double d = Math.sqrt(dsq);

        double rn = a / d;
        double rm = rn * (1.0 - eccsq) / dsq;

        double rho = rn * clat;
        double z = (1.0 - eccsq) * rn * slat;
        double rsq = rho * rho + z * z;
        double r = Math.sqrt(rsq);

        rrnrm[0] = r;
        rrnrm[1] = rn;
        rrnrm[2] = rm;

        return rrnrm;
    }

    public static double gc2gd(double flatgc, double altkm) {
        /*
         * geocentric latitude to geodetic latitude
         * 
         * Input: flatgc geocentric latitude deg. altkm altitide in km ouput:
         * flatgd geodetic latitude in deg
         */

        double dtr = Math.PI / 180.0;
        double rtd = 1 / dtr;

        double esq = ecc * ecc;

        // approximation by stages
        // 1st use gc-lat as if is gd, then correct alt dependence

        double altnow = altkm;

        double[] rrnrm = radcur(flatgc);
        double rn = rrnrm[1];

        double ratio = 1 - esq * rn / (rn + altnow);

        double tlat = Math.tan(dtr * flatgc) / ratio;
        double flatgd = rtd * Math.atan(tlat);

        // now use this approximation for gd-lat to get rn etc.

        rrnrm = radcur(flatgd);
        rn = rrnrm[1];

        ratio = 1 - esq * rn / (rn + altnow);
        tlat = Math.tan(dtr * flatgc) / ratio;
        flatgd = rtd * Math.atan(tlat);

        return flatgd;
    }

    public static EcefPoint llhxyz(double flat, double flon, double altkm) {
        /*
         * lat,lon,height to xyz vector
         * 
         * input: flat geodetic latitude in deg flon longitude in deg altkm
         * altitude in km output: returns vector x 3 long ECEF in km
         */
        double dtr = Math.PI / 180.0;

        double clat = Math.cos(dtr * flat);
        double slat = Math.sin(dtr * flat);
        double clon = Math.cos(dtr * flon);
        double slon = Math.sin(dtr * flon);

        double[] rrnrm = radcur(flat);
        double rn = rrnrm[1];

        double esq = ecc * ecc;

        double xkm = (rn + altkm) * clat * clon;
        double ykm = (rn + altkm) * clat * slon;
        double zkm = ((1 - esq) * rn + altkm) * slat;
        
        return new EcefPoint(xkm, ykm, zkm);
    }

    public static WgsPoint xyzllh(double x, double y, double z) {

        /*
         * xyz vector to lat,lon,height
         * 
         * input: xvec[3] xyz ECEF location output:
         * 
         * llhvec[3] with components
         * 
         * flat geodetic latitude in deg flon longitude in deg altkm altitude in
         * km
         */

        double dtr = Math.PI / 180.0;

        double rp = Math.sqrt(x * x + y * y + z * z);

        double flatgc = Math.asin(z / rp) / dtr;

        double testval = Math.abs(x) + Math.abs(y);
        double flon;

        if (testval < 1.0e-10) {
            flon = 0.0;
        } else {
            flon = Math.atan2(y, x) / dtr;
        }
        if (flon < 0.0) {
            flon = flon + 360.0;
        }

        double p = Math.sqrt(x * x + y * y);

        // on pole special case

        if (p < 1.0e-10) {
            double flat = 90.0;
            if (z < 0.0) {
                flat = -90.0;
            }

            double altkm = rp - rearth(flat);

            return new WgsPoint(flon, flat, altkm);
        }

        // first iteration, use flatgc to get altitude
        // and alt needed to convert gc to gd lat.

        double rnow = rearth(flatgc);
        double altkm = rp - rnow;
        double flat = gc2gd(flatgc, altkm);

        double[] rrnrm = radcur(flat);
        double rn = rrnrm[1];

        for (int kount = 0; kount < 5; kount++) {
            double slat = Math.sin(dtr * flat);
            double tangd = (z + rn * eccsq * slat) / p;
            double flatn = Math.atan(tangd) / dtr;

            double dlat = flatn - flat;
            flat = flatn;
            double clat = Math.cos(dtr * flat);

            rrnrm = radcur(flat);
            rn = rrnrm[1];

            altkm = (p / clat) - rn;

            if (Math.abs(dlat) < 1.0e-12) {
                break;
            }

        }

        if (flon > 180) {
            flon = flon - 360;
        }
        
        return new WgsPoint(flon, flat, altkm);
    }

}
