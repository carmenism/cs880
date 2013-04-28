import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import netcdf.NetCDFConverter;
import netcdf.VariableNotFoundException;

import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import vtk.*;

public class GreatLake extends JPanel implements ActionListener {
    String varNameTemp = "temp";
    String varNameLat = "lat";
    String varNameLon = "lon";
    String varNameSigma = "sigma";
    String varNameDepth = "depth";
    String varNameZeta = "zeta";
    String varNameMask = "mask";

    float[][][] temp;
    float[][] arrZeta;
    float[][] arrLat;
    float[][] arrLon;
    float[][] arrDepth;
    float[][] arrMask;
    float[] arrSigma;

    vtkIdList ids;

    private JButton exitButton;
    private vtkPanel panel;
    private vtkRenderer ren;

    public GreatLake(String fileName) {
        super(new BorderLayout());

        NetcdfFile file = null;

        try {
            file = NetcdfFile.open(fileName, null);

            NetCDFConverter converter = new NetCDFConverter(file);

            int time = 0;

            boolean reverse = true;
            
            temp = converter.get4dFloatAtTime(varNameTemp, reverse, time);
            arrZeta = converter.get3dFloatAtTime(varNameZeta, reverse, time);
            arrLat = converter.get2dFloat(varNameLat, reverse);
            arrLon = converter.get2dFloat(varNameLon, reverse);
            arrDepth = converter.get2dFloat(varNameDepth, reverse);
            arrMask = converter.get2dFloat(varNameMask, reverse);
            arrSigma = converter.get1dFloat(varNameSigma);

            int nz = arrSigma.length;

            float[][][] z = getZ(arrDepth, arrSigma);
            float[][][] lat = make3d(arrLat, nz);
            float[][][] lon = make3d(arrLon, nz);
            float[][][] mask = make3d(arrMask, nz);
            
            vtkStructuredGrid sGrid = getVtkStructuredGrid(temp, lat, lon, z);

            vtkLookupTable lut = new vtkLookupTable();
            //lut.MapScalars(sGrid, 1, 1);
            lut.SetNumberOfColors(12);
            lut.SetTableRange(0.0, 12.0);
            lut.SetNanColor(0.0, 0.0, 0.0, 0.0);           
            lut.Build();
                        
            vtkDataSetMapper mapper = new vtkDataSetMapper();
            mapper.SetInput(sGrid);
            mapper.SetScalarRange(0.0, 12.0);
            mapper.SetLookupTable(lut);
            
            /*
            vtkContourFilter contour = new vtkContourFilter();
            contour.SetInput(sGrid);
            contour.GenerateValues(5,  0.0, 12.0);
            
            vtkImageMapToColors imtc = new vtkImageMapToColors();
            imtc.SetLookupTable(lut);
            imtc.SetInput(sGrid);

            vtkProbeFilter probe = new vtkProbeFilter();
            probe.SetInput(contour.GetOutput());
            probe.SetSource(imtc.GetOutput());

            vtkPolyDataMapper mapper = new vtkPolyDataMapper();
            mapper.SetInput((vtkPolyData) probe.GetOutput());
            mapper.SetLookupTable(lut);
            mapper.SetColorModeToMapScalars();*/
                        
            vtkActor actor = new vtkActor();
            actor.SetMapper(mapper);
            
            
            panel = new vtkPanel();
            ren = panel.GetRenderer();
            
            ren.SetBackground(0.5, 0.5, 0.5);
            ren.AddActor(actor);
            ren.ResetCamera();
            // Add Java UI components
            exitButton = new JButton("Exit");
            exitButton.addActionListener(this);
            
            add(panel, BorderLayout.CENTER);
            add(exitButton, BorderLayout.SOUTH);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidRangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (VariableNotFoundException e) {
            // TODO Auto-generated catch block
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
    
    public vtkStructuredGrid getVtkStructuredGrid(float[][][] scalar, float[][][] lat, float[][][] lon, float[][][] z) {
        final int nx = scalar.length;
        final int ny = scalar[0].length;
        final int nz = scalar[0][0].length;
        
        vtkStructuredGrid sGrid = new vtkStructuredGrid();
        sGrid.SetDimensions(nx, ny, nz);
        
        vtkFloatArray scalars = getVtkFloatArray(scalar);
        vtkPoints points = convertLatLon(lat, lon, z);//getVtkPoints(lon, lat, z);
        sGrid.SetPoints(points);
        points.Delete();
        sGrid.GetPointData().SetScalars(scalars);
        scalars.Delete();
                
        return sGrid;
    }

    private vtkPoints convertLatLon(float[][][] lat, float [][][] lon, float[][][] z) {
        vtkDoubleArray vLat = new vtkDoubleArray();
        vLat.SetName("latitude");
        
        vtkDoubleArray vLon = new vtkDoubleArray();
        vLon.SetName("longitude");
        
        final int nx = lat.length;
        final int ny = lat[0].length;
        final int nz = lat[0][0].length;
        
        vtkMutableDirectedGraph g = new vtkMutableDirectedGraph();
        
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) {
                    g.AddVertex();
                    vLat.InsertNextValue(lat[i][j][k]);
                    vLon.InsertNextValue(lon[i][j][k]);
                }
            }
        }
        g.GetVertexData().AddArray(vLat);
        g.GetVertexData().AddArray(vLon);

        vtkGeoAssignCoordinates assign = new vtkGeoAssignCoordinates();
        assign.SetInput(g);
        assign.SetLatitudeArrayName("latitude");
        assign.SetLongitudeArrayName("longitude");
        assign.SetCoordinatesInArrays(true);
        assign.Update();       

        System.err.println(assign.Print());
        
        //vtkStructuredGrid out = geo.GetStructuredGridOutput();
        vtkMutableDirectedGraph data = (vtkMutableDirectedGraph) assign.GetOutput();
        //System.err.println(data.Print());
        //data.
        vtkPoints points = data.GetPoints();
        vtkPoints newPoints = new vtkPoints();
                
        int count = 0;
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) {
                    double[] p = points.GetPoint(count);
                    double xval = p[0];
                    double yval = p[1];
                    double zval = p[2] - 100* z[i][j][k];
                                        
                    newPoints.InsertNextPoint(xval, yval, zval);
                    count++;
                }
            }
        }
        
        return newPoints;
    }
    
    private float[][][] make3d(float[][] array, int nz) {
        final int nx = array.length;
        final int ny = array[0].length;

        float[][][] array3d = new float[nx][ny][nz];

        for (int x = 0; x < nx; x++) {
            for (int y = 0; y < ny; y++) {
                for (int z = 0; z < nz; z++) {
                    array3d[x][y][z] = array[x][y];
                }
            }
        }

        return array3d;
    }
    
    private vtkPoints getVtkPoints(float[][][] x, float[][][] y, float[][][] z) {
        final int nx = x.length;
        final int ny = x[0].length;
        final int nz = x[0][0].length;

        vtkPoints vArray = new vtkPoints();//nx * ny * nz);

        //int index = 0;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) { 
                    double [] point = new double[3];
                    point[0] = x[i][j][k];
                    point[1] = y[i][j][k];
                    point[2] = z[i][j][k];
                    
                    vArray.InsertNextPoint(point);
                    //index++;
                }
            }
        }

        return vArray;
    }

    private vtkFloatArray getVtkFloatArray(float[][][] array) {
        final int nx = array.length;
        final int ny = array[0].length;
        final int nz = array[0][0].length;

        vtkFloatArray vArray = new vtkFloatArray();//nx * ny * nz);

        //int index = 0;
        
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) {
                    if (array[i][j][k] == -99999.0) {
                    vArray.InsertNextValue(Float.NaN);
                    System.out.println("inserting NaN");
                    } else {
                    vArray.InsertNextValue(array[i][j][k]);
                    //index++;
                    }
                }
            }
        }

        return vArray;
    }

    private float[][][] getZ(float[][] depth, float[] sigma) {
        int zLength = sigma.length;
        int xLength = depth.length;
        int yLength = depth[0].length;

        float[][][] zArr = new float[xLength][yLength][zLength];

        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                for (int z = 0; z < zLength; z++) {
                    zArr[x][y][z] = depth[x][y] * sigma[z];
                }
            }
        }

        return zArr;
    }

    static {
        if (!vtkNativeLibrary.LoadAllNativeLibraries()) {
            for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
                if (!lib.IsLoaded()) {
                    System.out.println(lib.GetLibraryName() + " not loaded");
                }
            }
        }
        vtkNativeLibrary.DisableOutputWindow(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitButton)) {
            System.exit(0);
        }
    }
    
    public static void main(String s[]) {        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GreatLake gl = new GreatLake("glofs.lsofs.fields.forecast.20130301.t00z.nc");

                JFrame frame = new JFrame("GL");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(gl, BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                System.out.println("Working Directory = "
                        + System.getProperty("user.dir"));
            }
        });
    }
}
