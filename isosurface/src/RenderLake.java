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
import vtk.vtkContourFilter;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkImageMapToColors;
import vtk.vtkLookupTable;
import vtk.vtkMarchingCubes;
import vtk.vtkNativeLibrary;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProbeFilter;
import vtk.vtkProperty;
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
    private Actor actorFull, actorContourSelectionA, actorContourSelectionB;
    
    private double scalarMin = Double.MAX_VALUE;
    private double scalarMax = -1 * Double.MAX_VALUE;
    
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
            
    public double getScalarMin() {
        return scalarMin;
    }

    public double getScalarMax() {
        return scalarMax;
    }

    public Actor getFullActor() {
        return actorFull;
    }
    
    public Actor getContourSelectionActorA() {
        return actorContourSelectionA;
    }
    
    public Actor getContourSelectionActorB() {
        return actorContourSelectionB;
    }
    
    public void display() {
        panel.Render();
    }
    
    public void renderFull() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorFull);
    }
    
    public void renderSingleContour() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorContourSelectionA);
    }
    public void renderDoubleContour() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorContourSelectionA);
        ren.AddActor(actorContourSelectionB);
    }
    public RenderLake(NetCDFConfiguration config, String fileName, String scalarName, int time) {
        super(new BorderLayout());
        
        EcefPoint[][][] points = NetCDFToEcefPoints.convert(config, fileName, scalarName, time);
        
        if (points != null) {
            vtkStructuredGrid sGrid = getVtkStructuredGrid(points);
            
            buildFullActor(sGrid);
            buildContourActorA(sGrid);
            buildContourActorB(sGrid);
            
            panel = new vtkPanel();
            ren = panel.GetRenderer();
            
            ren.SetBackground(0.45, 0.5, 0.55);
            ren.AddActor(actorFull);
            ren.TwoSidedLightingOn();
            ren.ResetCamera();
            
            // Add Java UI components
            exitButton = new JButton("Exit");
            exitButton.addActionListener(this);
            
            add(panel, BorderLayout.CENTER);
            add(exitButton, BorderLayout.SOUTH);
        }
    }
    
    private void buildFullActor(vtkStructuredGrid sGrid) {        
        vtkLookupTable lut = new vtkLookupTable();
        lut.SetNumberOfColors(256);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.SetAlphaRange(1.0, 1.0);          
        lut.Build();
                    
        vtkDataSetMapper mapper = new vtkDataSetMapper();
        mapper.SetInput(sGrid);
        mapper.SetScalarRange(scalarMin, scalarMax);
        mapper.SetLookupTable(lut);           
       
        actorFull = new Actor(null, lut);
        actorFull.SetMapper(mapper);  
    }
    
    private void buildContourActorA(vtkStructuredGrid sGrid) {        
        vtkLookupTable lut = new vtkLookupTable();
        lut.SetNumberOfColors(256);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);     
        lut.SetAlphaRange(0.6, 0.6);
        lut.Build();

        vtkContourFilter iso = new vtkContourFilter();
        iso.SetInput(sGrid);
        iso.SetValue(0, 2 * (scalarMin + scalarMax) / 3);
        iso.ComputeNormalsOff();
        
        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(iso.GetOutput());
        mapper.ScalarVisibilityOn();
        mapper.SetLookupTable(lut);
        mapper.SetScalarRange(scalarMin, scalarMax);
        
        actorContourSelectionA = new Actor(iso, lut);
        actorContourSelectionA.SetMapper(mapper);     
    }
   
    private void buildContourActorB(vtkStructuredGrid sGrid) {        
        vtkLookupTable lut = new vtkLookupTable();
        lut.SetNumberOfColors(256);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);     
        lut.SetAlphaRange(0.4, 0.4);
        lut.Build();

        vtkContourFilter iso = new vtkContourFilter();
        iso.SetInput(sGrid);
        iso.SetValue(0, (scalarMin + scalarMax) / 3);
        iso.ComputeNormalsOff();
        
        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(iso.GetOutput());
        mapper.ScalarVisibilityOn();
        mapper.SetLookupTable(lut);
        mapper.SetScalarRange(scalarMin, scalarMax);
        
        actorContourSelectionB = new Actor(iso, lut);
        actorContourSelectionB.SetMapper(mapper); 
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
                    
                    double scalarValue = points[z][y][x].getScalar();
                    
                    if (scalarValue == -99999.0 || points[z][y][x].z == 0) {
                        vfArray.InsertNextValue(Float.NaN);
                    } else {
                        vfArray.InsertNextValue(scalarValue);
                        
                        if (scalarValue > scalarMax) {
                            scalarMax = scalarValue;
                        }
                        if (scalarValue < scalarMin) {
                            scalarMin = scalarValue;
                        }
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
                
                String filename = "glofs.lsofs.fields.forecast.20130301.t00z.nc";
                //String filename = "glofs.leofs.fields.nowcast.20130425.t01z.nc";
                
                RenderLake lake = new RenderLake(config, filename, "temp", 0);

                JFrame frame = new JFrame("Lake Renderer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(lake, BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                JFrame frame2 = new JFrame("Lake Controls");
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.getContentPane().setLayout(new BorderLayout());
                frame2.getContentPane().add(new LakeControls(lake),
                        BorderLayout.CENTER);
                frame2.setSize(400, 600);
                frame2.setLocationRelativeTo(frame);
                frame2.setVisible(true);
                
                System.out.println("Working Directory = "
                        + System.getProperty("user.dir"));
            }
        });
    }
}
