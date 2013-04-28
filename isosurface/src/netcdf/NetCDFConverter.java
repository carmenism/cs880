package netcdf;

import java.io.IOException;

import ucar.ma2.ArrayFloat;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class NetCDFConverter {
    NetcdfFile file;

    public NetCDFConverter(NetcdfFile file) {
        this.file = file;
    }

    public float[] get1dFloat(String name) throws IOException,
            InvalidRangeException, VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read1dFloat(variable);
    }

    public float[][] get2dFloat(String name, boolean reverseDimensions)
            throws IOException, InvalidRangeException,
            VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read2dFloat(variable, reverseDimensions);
    }

    public byte[][] get2dFloatAsByte(String name, boolean reverseDimensions)
            throws IOException, InvalidRangeException,
            VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read2dFloatAsByte(variable, reverseDimensions);
    }
    
    public float[] get2dFloatAtTime(String name, boolean reverseDimensions,
            int timeIndex) throws IOException, InvalidRangeException,
            VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read2dFloatAtTime(variable, reverseDimensions, timeIndex);
    }

    public float[][][] get3dFloat(String name, boolean reverseDimensions)
            throws IOException, InvalidRangeException,
            VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read3dFloat(variable, reverseDimensions);
    }

    public float[][] get3dFloatAtTime(String name, boolean reverseDimensions,
            int timeIndex) throws IOException, InvalidRangeException,
            VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read3dFloatAtTime(variable, reverseDimensions, timeIndex);
    }

    public float[][][][] get4dFloat(String name, boolean reverseDimensions)
            throws IOException, InvalidRangeException,
            VariableNotFoundException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read4dFloat(variable, reverseDimensions);
    }

    public float[][][] get4dFloatAtTime(String name, boolean reverseDimensions,
            int timeIndex) throws VariableNotFoundException, IOException,
            InvalidRangeException {
        Variable variable = file.findVariable(name);

        if (variable == null) {
            throw new VariableNotFoundException();
        }

        return read4dFloatAtTime(variable, reverseDimensions, timeIndex);
    }



    private float[][][][] read4dFloat(Variable var, boolean reverseDimensions)
            throws IOException, InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[43];
        float[][][][] array;

        int dimT = shape[0];
        int dimZ = shape[1];
        int dimY = shape[2];
        int dimX = shape[3];

        if (reverseDimensions) {
            array = new float[dimX][dimY][dimZ][dimT];
        } else {
            array = new float[dimT][dimZ][dimY][dimX];
        }

        ArrayFloat.D4 arrayFloat = (ArrayFloat.D4) var.read(origin, shape);

        for (int t = 0; t < dimT; t++) {
            for (int z = 0; z < dimZ; z++) {
                for (int y = 0; y < dimY; y++) {
                    for (int x = 0; x < dimX; x++) {
                        if (reverseDimensions) {
                            array[x][y][z][t] = arrayFloat.get(t, z, y, x);
                        } else {
                            array[t][z][y][x] = arrayFloat.get(t, z, y, x);
                        }
                    }
                }
            }
        }

        return array;
    }

    private float[][][] read4dFloatAtTime(Variable var,
            boolean reverseDimensions, int timeIndex) throws IOException,
            InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[4];
        float[][][] array;

        int dimZ = shape[1];
        int dimY = shape[2];
        int dimX = shape[3];

        if (reverseDimensions) {
            array = new float[dimX][dimY][dimZ];
        } else {
            array = new float[dimZ][dimY][dimX];
        }

        ArrayFloat.D4 arrayFloat = (ArrayFloat.D4) var.read(origin, shape);

        for (int z = 0; z < dimZ; z++) {
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    if (reverseDimensions) {
                        array[x][y][z] = arrayFloat.get(timeIndex, z, y, x);
                    } else {
                        array[z][y][x] = arrayFloat.get(timeIndex, z, y, x);
                    }
                }
            }
        }

        return array;
    }

    private float[][][] read3dFloat(Variable var, boolean reverseDimensions)
            throws IOException, InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[3];
        float[][][] array;

        int dimZ = shape[0];
        int dimY = shape[1];
        int dimX = shape[2];

        if (reverseDimensions) {
            array = new float[dimX][dimY][dimZ];
        } else {
            array = new float[dimZ][dimY][dimX];
        }

        ArrayFloat.D3 arrayFloat = (ArrayFloat.D3) var.read(origin, shape);

        for (int z = 0; z < dimZ; z++) {
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    if (reverseDimensions) {
                        array[x][y][z] = arrayFloat.get(z, y, x);
                    } else {
                        array[z][y][x] = arrayFloat.get(z, y, x);
                    }
                }
            }
        }

        return array;
    }

    private float[][] read3dFloatAtTime(Variable var,
            boolean reverseDimensions, int timeIndex) throws IOException,
            InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[3];
        float[][] array;

        int dimY = shape[1];
        int dimX = shape[2];

        if (reverseDimensions) {
            array = new float[dimX][dimY];
        } else {
            array = new float[dimY][dimX];
        }

        ArrayFloat.D3 arrayFloat = (ArrayFloat.D3) var.read(origin, shape);

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                if (reverseDimensions) {
                    array[x][y] = arrayFloat.get(timeIndex, y, x);
                } else {
                    array[y][x] = arrayFloat.get(timeIndex, y, x);
                }
            }
        }

        return array;
    }

    private float[][] read2dFloat(Variable var, boolean reverseDimensions)
            throws IOException, InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[2];
        float[][] array;

        int dimY = shape[0];
        int dimX = shape[1];

        if (reverseDimensions) {
            array = new float[dimX][dimY];
        } else {
            array = new float[dimY][dimX];
        }

        ArrayFloat.D2 arrayFloat = (ArrayFloat.D2) var.read(origin, shape);

        for (int j = 0; j < dimY; j++) {
            for (int i = 0; i < dimX; i++) {
                if (reverseDimensions) {
                    array[i][j] = arrayFloat.get(j, i);
                } else {
                    array[j][i] = arrayFloat.get(j, i);
                }
            }
        }

        return array;
    }

    private byte[][] read2dFloatAsByte(Variable var, boolean reverseDimensions)
            throws IOException, InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[2];
        byte[][] array;

        int dimY = shape[0];
        int dimX = shape[1];

        if (reverseDimensions) {
            array = new byte[dimX][dimY];
        } else {
            array = new byte[dimY][dimX];
        }

        ArrayFloat.D2 arrayFloat = (ArrayFloat.D2) var.read(origin, shape);

        for (int j = 0; j < dimY; j++) {
            for (int i = 0; i < dimX; i++) {
                if (reverseDimensions) {
                    array[i][j] = (byte) arrayFloat.get(j, i);
                } else {
                    array[j][i] = (byte) arrayFloat.get(j, i);
                }
            }
        }

        return array;
    }

    private float[] read2dFloatAtTime(Variable var, boolean reverseDimensions,
            int timeIndex) throws IOException, InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[2];

        int dim = shape[1];
        float[] array = new float[dim];

        ArrayFloat.D2 arrayFloat = (ArrayFloat.D2) var.read(origin, shape);

        for (int i = 0; i < dim; i++) {
            array[i] = arrayFloat.get(timeIndex, i);
        }

        return array;
    }

    private float[] read1dFloat(Variable var) throws IOException,
            InvalidRangeException {
        int[] shape = var.getShape();
        int[] origin = new int[1];

        int dim = shape[0];
        float[] array = new float[dim];

        ArrayFloat.D1 arrayFloat = (ArrayFloat.D1) var.read(origin, shape);

        for (int i = 0; i < dim; i++) {
            array[i] = arrayFloat.get(i);
        }

        return array;
    }

    /*
     * private byte[][] read2dByte(Variable var, int dimI, int dimJ) throws
     * IOException, InvalidRangeException { int[] shape = var.getShape(); int[]
     * origin = new int[2];
     * 
     * byte[][] array = new byte[dimI][dimJ];
     * 
     * ArrayFloat.D2 arrayFloat = (ArrayFloat.D2) var.read(origin, shape);
     * 
     * for (int i = 0; i < dimI; i++) { for (int j = 0; j < dimJ; j++) {
     * array[i][j] = (byte) arrayFloat.get(j, i); } }
     * 
     * return array; }
     */
}
