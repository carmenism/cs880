import geo.EcefPoint;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import netcdf.NetCDFConfiguration;
import netcdf.NetCDFToEcefPoints;

import vtk.vtkActor;
import vtk.vtkContourFilter;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkImageMapToColors;
import vtk.vtkLegendBoxActor;
import vtk.vtkLookupTable;
import vtk.vtkMarchingCubes;
import vtk.vtkNativeLibrary;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
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
    private FullActor actorFull;
    private ContourActor actorContourA, actorContourB;
    private FrameActor actorFrame;
    
    private double scalarMin = Double.MAX_VALUE;
    private double scalarMax = -1 * Double.MAX_VALUE;
    
    private boolean drawFrame;
    
    NetCDFToEcefPoints ncToPts;
    NetCDFConfiguration config;
    
    private HashMap<Float, vtkStructuredGrid> gridsAtZScales = new HashMap<Float, vtkStructuredGrid>();
    
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

    public FullActor getFullActor() {
        return actorFull;
    }
    
    public FrameActor getFrameActor() {
        return actorFrame;
    }
    
    public ContourActor getContourActorA() {
        return actorContourA;
    }
    
    public ContourActor getContourActorB() {
        return actorContourB;
    }
    
    public void display() {
        panel.Render();
    }
    
    public void renderFull() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorFull);
        
        if (drawFrame) {
            ren.AddActor(actorFrame);
        }
    }
    
    public void renderSingleContour() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorContourA);
        
        if (drawFrame) {
            ren.AddActor(actorFrame);
        }
    }
    
    public void renderDoubleContour() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorContourA);
        ren.AddActor(actorContourB);
        
        if (drawFrame) {
            ren.AddActor(actorFrame);
        }
    }
    
    public void drawFrameOn() {
        drawFrame = true;
        
        ren.AddActor(actorFrame);
    }
    
    public void drawFrameOff() {
        drawFrame = false;
        
        ren.RemoveActor(actorFrame);
    }
    
    public RenderLake(NetCDFConfiguration config, String fileName, String scalarName, int time) {
        super(new BorderLayout());
        
        this.config = config;
        
        ncToPts = new NetCDFToEcefPoints(config, fileName, scalarName, time);
        
        EcefPoint[][][] points = ncToPts.convert(200.0f);
        
        if (points != null) {
            vtkStructuredGrid sGrid = getVtkStructuredGrid(points, config.getMissingValue());
            
            gridsAtZScales.put(200.0f, sGrid);
            
            System.out.println("Min: " + scalarMin);
            System.out.println("Max: " + scalarMax);
            
            buildFullActor(sGrid);
            buildContourActorA(sGrid);
            buildContourActorB(sGrid);
            buildFrameActor(sGrid);
            
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
            
            vtkLegendBoxActor leg;
        }
    }
    
    public void changeZScale(float zScale) {
        ren.RemoveAllViewProps();
        
        vtkStructuredGrid sGrid = gridsAtZScales.get(zScale);
        
        if (sGrid == null) {
            EcefPoint[][][] points = ncToPts.convert(zScale);        
            sGrid = getVtkStructuredGrid(points, config.getMissingValue());
            gridsAtZScales.put(zScale, sGrid);
        }
        
        buildFullActor(sGrid);
        buildContourActorA(sGrid);
        buildContourActorB(sGrid);
        buildFrameActor(sGrid);
    }
    
    /*private Actor buildActor(vtkStructuredGrid sGrid, double opacity) {
        vtkLookupTable lut = getColorTable();
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);         
        lut.Build();
                    
        vtkDataSetMapper mapper = new vtkDataSetMapper();
        mapper.SetInput(sGrid);
        mapper.SetScalarRange(scalarMin, scalarMax);
        mapper.SetLookupTable(lut);           
       
        Actor actor = new Actor(null, lut);
        actor.SetMapper(mapper);        
        actor.GetProperty().SetOpacity(1.0);
        
        return actor;
    }*/
    
    private void buildFullActor(vtkStructuredGrid sGrid) {        
        LookupTable lut = getColorTable(1.0);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);         
        lut.Build();
        
        actorFull = new FullActor(sGrid, lut, scalarMin, scalarMax);
    }
    
    private void buildFrameActor(vtkStructuredGrid sGrid) {        
        LookupTable lut = new LookupTable();
        lut.SetTableRange(scalarMin, scalarMax);
        //lut.SetSaturationRange(0.0, 0.0);
        lut.SetValueRange(0.0, 0.0);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);      
        lut.SetAlphaRange(0.1, 0.1);
        lut.Build();                 
       
        actorFrame = new FrameActor(sGrid, lut, scalarMin, scalarMax);   
    }
    
    private void buildContourActorA(vtkStructuredGrid sGrid) {      
        LookupTable lut = getColorTable(0.6);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);    
        lut.Build();
                
        actorContourA = new ContourActor(sGrid, lut, scalarMin, scalarMax, 2 * (scalarMin + scalarMax) / 3);
    }
   
    private void buildContourActorB(vtkStructuredGrid sGrid) {        
        LookupTable lut = getColorTable(0.4);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);      
        lut.Build();
                
        actorContourB = new ContourActor(sGrid, lut, scalarMin, scalarMax, (scalarMin + scalarMax) / 3);
    }
    
    private LookupTable getColorTable(double opacity) {
        LookupTable lut = null;
        String filename = "rgb.256";
        
        File file = new File(filename);
        
        try {
            Scanner scanFile = new Scanner(file);
            ArrayList<String> lines = new ArrayList<String>();
            
            while (scanFile.hasNextLine()) {
                lines.add(scanFile.nextLine());
            }
            
            scanFile.close();
            
            int numColors = lines.size();
            
            lut = new LookupTable();
            lut.SetNumberOfColors(numColors);
            lut.SetNanColor(0.0, 0.0, 0.0, 0.0); 
            
            for (int i = 0; i < lines.size(); i++) {
                Scanner scanLine = new Scanner(lines.get(i));//numColors - i - 1));
                
                double r = (double) scanLine.nextInt() / 255;
                double g = (double) scanLine.nextInt() / 255;
                double b = (double) scanLine.nextInt() / 255;
                
                lut.SetTableValue(i, r, g, b, opacity);
                
                scanLine.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
                
        return lut;
    }
    
    private vtkStructuredGrid getVtkStructuredGrid(EcefPoint[][][] points, double missingValue) {
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
                    
                    if (scalarValue == missingValue || points[z][y][x].z == 0) {
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
                config.setMissingValue(-99999.0);
                
                //String filename = "glofs.lsofs.fields.nowcast.20130430.t00z.nc";
                String filename = "glofs.lsofs.fields.nowcast.20120701.t00z.nc";
                //String filename = "glofs.lsofs.fields.forecast.20130301.t00z.nc";
                //String filename = "glofs.leofs.fields.nowcast.20130425.t01z.nc";
                
                RenderLake lake = new RenderLake(config, filename, "temp", 0);
                
                JFrame frame = new JFrame("Lake Renderer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(lake, BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                LakeControls lc = new LakeControls(lake);
                JScrollPane jsp = new JScrollPane(lc);
                
                JFrame frame2 = new JFrame("Lake Controls");
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.getContentPane().setLayout(new BorderLayout());
                frame2.getContentPane().add(jsp, BorderLayout.CENTER);
                frame2.setSize(700, 500);
                frame2.setLocationRelativeTo(frame);
                frame2.setVisible(true);
                
                System.out.println("Working Directory = "
                        + System.getProperty("user.dir"));
            }
        });
    }
}
