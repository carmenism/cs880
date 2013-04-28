package netcdf;

import geo.EcefPoint;
import geo.WgsPoint;

import java.io.IOException;

import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;

public class NetCDFToEcefPoints { 
    public static int DEPTH_SCALE = 100;
    
    public static EcefPoint[][][] convert(NetCDFConfiguration config, String fileName, String scalarName, int time) {
        NetcdfFile file = null;
        
        EcefPoint[][][] points = null;
        
        float[][][] scalar;
        float[][] zeta;
        float[][] lat;
        float[][] lon;
        float[][] depth;
        float[][] mask;
        float[] sigma;
        float[][][] alt;
        
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
            
            int dimZ = scalar.length - 1;
            int dimY = scalar[0].length;
            int dimX = scalar[0][0].length;

            alt = new float[dimZ][dimY][dimX];
            
            points = new EcefPoint[dimZ][dimY][dimX];
            
            for (int z = 0; z < dimZ; z++) {
                for (int y = 0; y < dimY; y++) {
                    for (int x = 0; x < dimX; x++) {
                        alt[z][y][x] = getZ(depth[y][x], zeta[y][x], sigma[z]);
                        
                        WgsPoint gp = new WgsPoint(lon[y][x], lat[y][x], alt[z][y][x] / 1000.0);
                        
                        points[z][y][x] = gp.toEcefPoint();
                        points[z][y][x].setScalar(scalar[z][y][x]);
                    }
                }
            }
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

        return points;
    }
    
    private static float getZ(float depth, float zeta, float sigma) {
        if (depth == -99999.0 || zeta == -99999.0 || sigma == -99999.f) {
            return 0;
        }
        
        return ((zeta + (-1 * depth)) * DEPTH_SCALE * sigma) ;
    }
}
