import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
    byte[][] arrMask;
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
            arrMask = converter.get2dFloatAsByte(varNameMask, reverse);
            arrSigma = converter.get1dFloat(varNameSigma);

            int nz = arrSigma.length;

            float[][][] z = getZ(arrDepth, arrSigma);
            float[][][] lat = make3d(arrLat, nz);
            float[][][] lon = make3d(arrLon, nz);
            
            /*float[][][] lat = new float[4][5][6];
            float[][][] lon = new float[4][5][6];
            float[][][] z = new float[4][5][6];
            float[][][] temp = new float[4][5][6];

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    for (int k = 0; k < 5; k++) {
                        lat[i][j][k] = 10 + i * 1;
                        lon[i][j][k] = 20 + j * 2; 
                        lon[i][j][k] = k * 30;
                        temp[i][j][k] = k * 2;
                    }    
                }
            }*/
            
            vtkStructuredGrid sGrid = getVtkStructuredGrid(temp, lat, lon, z);
            
            vtkDataSetMapper mapper = new vtkDataSetMapper();
            mapper.SetInput(sGrid);
            
            /*vtkLookupTable lut = new vtkLookupTable();
            lut.SetNumberOfColors(256);
            lut.SetTableRange(0.0, 12.0);
            
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

            System.err.println(actor.Print());
            
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
        vtkPoints points = getVtkPoints(lon, lat, z);
        
        sGrid.SetPoints(points);
        points.Delete();
        sGrid.GetPointData().SetVectors(scalars);
        scalars.Delete();
                
        return sGrid;
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
                    vArray.InsertNextValue(array[i][j][k]);
                    //index++;
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
