package netcdf;

import geo.EcefPoint;
import geo.WgsPoint;

import java.io.IOException;
import java.util.Properties;

import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;

/**
 * Reads in a NetCDF file and converts the data points to ECEF according to a
 * vertical exaggeration component.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class NetCDFToEcefPoints {
    private float[][][] scalar;
    private float[] sigma;
    private float[][] zeta;
    private float[][] lat;
    private float[][] lon;
    private float[][] depth;

    private float missingValue;

    /**
     * Loads in data from a NetCDF file at a specific time index into memory as
     * floats.
     * 
     * @param prop
     *            The Properties object indicating the names of the variables
     *            for the NetCDF file being processed.
     * @param fileName
     *            The file path to the NetCDF file to be processed.
     * @param time
     *            The time index to be read in the file.
     */
    public NetCDFToEcefPoints(Properties prop, String fileName, int time) {
        NetcdfFile file = null;

        try {
            file = NetcdfFile.open(fileName, null);

            NetCDFConverter converter = new NetCDFConverter(file);

            boolean reverse = false;

            scalar = converter.get4dFloatAtTime(
                    prop.getProperty("temperature"), reverse, time);

            zeta = converter.get3dFloatAtTime(prop.getProperty("zeta"),
                    reverse, time);
            lat = converter.get2dFloat(prop.getProperty("latitude"), reverse);
            lon = converter.get2dFloat(prop.getProperty("longitude"), reverse);
            depth = converter.get2dFloat(prop.getProperty("depth"), reverse);
            sigma = converter.get1dFloat(prop.getProperty("sigma"));

            missingValue = Float.parseFloat(prop.getProperty("missingValue"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (VariableNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRangeException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * Converts the points found in the NetCDF file (which are assumed to be WGS
     * 1984 longitude, latitude, and altitude) to ECEF (Earth-centered,
     * Earth-fixed) points according to the specified vertical exaggeration.
     * 
     * @param verticalExaggeration
     *            The factor for exaggerating the vertical component (a positive
     *            integer; for no exaggeration, use 1).
     * @return The points converted into ECEF according to the vertical
     *         exaggeration.
     */
    public EcefPoint[][][] convert(float verticalExaggeration) {
        EcefPoint[][][] points = null;

        int dimZ = scalar.length - 1;
        int dimY = scalar[0].length;
        int dimX = scalar[0][0].length;

        float[][][] altitudes = new float[dimZ][dimY][dimX];

        points = new EcefPoint[dimZ][dimY][dimX];

        for (int z = 0; z < dimZ; z++) {
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    altitudes[z][y][x] = getAltitude(depth[y][x], zeta[y][x],
                            sigma[z], verticalExaggeration);

                    WgsPoint gp = new WgsPoint(lon[y][x], lat[y][x],
                            altitudes[z][y][x] / 1000.0);

                    points[z][y][x] = gp.toEcefPoint();
                    points[z][y][x].setScalar(scalar[z][y][x]);
                }
            }
        }

        return points;
    }

    /**
     * Calculates the altitude for a given point in meters.
     * 
     * @param depth
     *            The maximum depth (measured in meters; positive down) of the
     *            point.
     * @param zeta
     *            The free surface (measured in meters; positive up) of the
     *            point.
     * @param sigma
     *            The sigma layer this point is found in (percentage down the
     *            water column; 100% is ocean/lake bottom).
     * @param verticalExaggeration
     *            The factor for exaggerating the vertical component (a positive
     *            integer; for no exaggeration, use 1).
     * @return The altitude at the point (measured in meters; positive up).
     */
    private float getAltitude(float depth, float zeta, float sigma,
            float verticalExaggeration) {
        if (depth == missingValue || zeta == missingValue
                || sigma == missingValue) {
            return 0;
        }

        return ((zeta + (-1 * depth)) * verticalExaggeration * sigma);
    }
}
