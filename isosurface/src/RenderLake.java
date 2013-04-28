import geo.EcefPoint;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import netcdf.NetCDFConfiguration;
import netcdf.NetCDFToEcefPoints;

import vtk.vtkActor;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkLookupTable;
import vtk.vtkNativeLibrary;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkRenderer;
import vtk.vtkStructuredGrid;


public class RenderLake extends JPanel implements ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JButton exitButton;
    private vtkPanel panel;
    private vtkRenderer ren;
    
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
    
    public RenderLake(NetCDFConfiguration config, String fileName, String scalarName, int time) {
        super(new BorderLayout());
        
        EcefPoint[][][] points = NetCDFToEcefPoints.convert(config, fileName, scalarName, time);
        
        if (points != null) {
            vtkStructuredGrid sGrid = getVtkStructuredGrid(points);
            
            vtkLookupTable lut = new vtkLookupTable();
            //lut.MapScalars(sGrid, 1, 1);
            lut.SetNumberOfColors(12);
            lut.SetTableRange(0.0, 30.0);
            lut.SetNanColor(0.0, 0.0, 0.0, 0.0);           
            lut.Build();
                        
            vtkDataSetMapper mapper = new vtkDataSetMapper();
            mapper.SetInput(sGrid);
            mapper.SetScalarRange(0.0, 30.0);
            mapper.SetLookupTable(lut);            
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
        }
    }
    
    private vtkStructuredGrid getVtkStructuredGrid(EcefPoint[][][] points) {
        final int nz = points.length;
        final int ny = points[0].length;
        final int nx = points[0][0].length;
        
        vtkStructuredGrid sGrid = new vtkStructuredGrid();
        sGrid.SetDimensions(nx, ny, nz);
        
        vtkPoints vpArray = new vtkPoints();
        vtkFloatArray vfArray = new vtkFloatArray();

        for (int z = 0; z < nz; z++) {
            for (int y = 0; y < ny; y++) {
                for (int x = 0; x < nx; x++) { 
                    double [] point = new double[3];
                    point[0] = points[z][y][x].x;
                    point[1] = points[z][y][x].y;
                    point[2] = points[z][y][x].z;
                    
                    vpArray.InsertNextPoint(point);
                    
                    if (points[z][y][x].getScalar() == -99999.0) {
                        vfArray.InsertNextValue(Float.NaN);
                    } else {
                        vfArray.InsertNextValue(points[z][y][x].getScalar());
                    }
                }
            }
        }        
        
        sGrid.SetPoints(vpArray);
        vpArray.Delete();
        sGrid.GetPointData().SetScalars(vfArray);
        vfArray.Delete();
        
        return sGrid;
    }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitButton)) {
            System.exit(0);
        }
    }
    
    public static void main(String [] args) {        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NetCDFConfiguration config = new NetCDFConfiguration();
                
                config.setDepth("depth");
                config.setZeta("zeta");
                config.setMask("mask");
                config.setLatitude("lat");
                config.setLongitude("lon");
                config.setSigma("sigma");
                
                //String filename = "glofs.lsofs.fields.forecast.20130301.t00z.nc";
                String filename = "glofs.leofs.fields.nowcast.20130425.t01z.nc";

                
                RenderLake lake = new RenderLake(config, filename, "temp", 0);

                JFrame frame = new JFrame("GL");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(lake, BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                System.out.println("Working Directory = "
                        + System.getProperty("user.dir"));
            }
        });
    }
}
