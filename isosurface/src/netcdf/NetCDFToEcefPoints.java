package netcdf;

import geo.EcefPoint;
import geo.WgsPoint;

import java.io.IOException;

import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;

public class NetCDFToEcefPoints {
    private NetCDFConfiguration config;
    private float[][][] scalar;
    private float[] sigma;
    private float[][] zeta;
    private float[][] lat;
    private float[][] lon;
    private float[][] depth;
    private float[][] mask;

    public NetCDFToEcefPoints(NetCDFConfiguration config, String fileName,
            String scalarName, int time) {
        this.config = config;

        NetcdfFile file = null;

        try {
            file = NetcdfFile.open(fileName, null);

            NetCDFConverter converter = new NetCDFConverter(file);

            boolean reverse = false;

            scalar = converter.get4dFloatAtTime(scalarName, reverse, time);

            zeta = converter.get3dFloatAtTime(config.zeta, reverse, time);
            lat = converter.get2dFloat(config.latitude, reverse);
            lon = converter.get2dFloat(config.longitude, reverse);
            depth = converter.get2dFloat(config.depth, reverse);
            mask = converter.get2dFloat(config.mask, reverse);
            sigma = converter.get1dFloat(config.sigma);
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

    public EcefPoint[][][] convert(float verticalScale) {
        EcefPoint[][][] points = null;

        int dimZ = scalar.length - 1;
        int dimY = scalar[0].length;
        int dimX = scalar[0][0].length;

        float[][][] alt = new float[dimZ][dimY][dimX];

        points = new EcefPoint[dimZ][dimY][dimX];

        for (int z = 0; z < dimZ; z++) {
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    alt[z][y][x] = getZ(depth[y][x], zeta[y][x], sigma[z], verticalScale);

                    WgsPoint gp = new WgsPoint(lon[y][x], lat[y][x],
                            alt[z][y][x] / 1000.0);

                    points[z][y][x] = gp.toEcefPoint();
                    points[z][y][x].setScalar(scalar[z][y][x]);
                }
            }
        }

        return points;
    }

    private float getZ(float depth, float zeta, float sigma, float verticalScale) {
        if (depth == config.getMissingValue()
                || zeta == config.getMissingValue()
                || sigma == config.getMissingValue()) {
            return 0;
        }

        return ((zeta + (-1 * depth)) * verticalScale * sigma);
    }
}
