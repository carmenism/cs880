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
        
    NetCDFToEcefPoints ncToPts;
    NetCDFConfiguration config;
    
    private double[][] colors;
    
    private vtkStructuredGrid currentSGrid;
    private boolean drawFrame = false;
    
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
        
        File file = new File("rgb.256");
        setColorTable(file);
        
        ncToPts = new NetCDFToEcefPoints(config, fileName, scalarName, time);
        
        EcefPoint[][][] points = ncToPts.convert(200.0f);
        
        if (points != null) {
            currentSGrid = getVtkStructuredGrid(points, config.getMissingValue());
            
            gridsAtZScales.put(200.0f, currentSGrid);
            
            System.out.println("Min: " + scalarMin);
            System.out.println("Max: " + scalarMax);
            
            buildFullActor();
            buildContourActorA();
            buildContourActorB();
            buildFrameActor();
            
            panel = new vtkPanel();
            ren = panel.GetRenderer();
            /*
            //panel.SetAlphaBitPlanes(true);
            //ren.SetUseDepthPeeling(true);
            ren.UseDepthPeelingOn();
            ren.SetMaximumNumberOfPeels(0);
            // - Set the occlusion ratio (initial value is 0.0, exact image):
            ren.SetOcclusionRatio(0.0);*/
            
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
    
    public void changeZScale(float zScale) {
        ren.RemoveAllViewProps();
        
        currentSGrid = gridsAtZScales.get(zScale);
        
        if (currentSGrid == null) {
            EcefPoint[][][] points = ncToPts.convert(zScale);        
            currentSGrid = getVtkStructuredGrid(points, config.getMissingValue());
            gridsAtZScales.put(zScale, currentSGrid);
        }
        
        buildFullActor();
        buildContourActorA();
        buildContourActorB();
        buildFrameActor();  
    }
    
    public void changeColor() {
        ren.RemoveAllViewProps();
        
        
        buildFullActor();
        buildContourActorA();
        buildContourActorB();
        buildFrameActor();        
    }
    
    private void buildFullActor() {        
        LookupTable lut = getColorTable(1.0);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);         
        lut.Build();
        
        actorFull = new FullActor(currentSGrid, lut, scalarMin, scalarMax);
    }
    
    private void buildFrameActor() {    
        LookupTable lut = new LookupTable();
        lut.SetTableRange(scalarMin, scalarMax);
        //lut.SetSaturationRange(0.0, 0.0);
        lut.SetValueRange(0.0, 0.0);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);      
        lut.SetAlphaRange(0.1, 0.1);
        lut.Build();                 
       
        actorFrame = new FrameActor(currentSGrid, lut, scalarMin, scalarMax);   
    }
    
    private void buildContourActorA() {      
        LookupTable lut = getColorTable(0.6);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);    
        lut.Build();
                
        actorContourA = new ContourActor(currentSGrid, lut, scalarMin, scalarMax, 2 * (scalarMin + scalarMax) / 3);
    }
   
    private void buildContourActorB() {      
        LookupTable lut = getColorTable(0.4);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);      
        lut.Build();
                
        actorContourB = new ContourActor(currentSGrid, lut, scalarMin, scalarMax, (scalarMin + scalarMax) / 3);
    }
    
    public void setColorTable(File file) {
        Scanner scanFile;
        ArrayList<String> lines = null;

        try {
            scanFile = new Scanner(file);
            lines = new ArrayList<String>();

            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();

                if (!line.isEmpty()) {
                    Scanner scanLine = new Scanner(line);
                    int count = 0;

                    while (scanLine.hasNextInt()) {
                        scanLine.nextInt();
                        count++;
                    }

                    if (count < 3) {
                        scanLine.close();
                        scanFile.close();

                        throw new Exception(
                                "There must be at least three integer values per line.");
                    }

                    scanLine.close();
                    lines.add(line);
                }
            }

            scanFile.close();
            
            colors = new double[lines.size()][3];
            
            for (int i = 0; i < lines.size(); i++) {
                Scanner scanLine = new Scanner(lines.get(i));

                colors[i][0] = (double) scanLine.nextInt() / 255;
                colors[i][1] = (double) scanLine.nextInt() / 255;
                colors[i][2] = (double) scanLine.nextInt() / 255;

                scanLine.close();
            }
        } catch (FileNotFoundException e1) {
            System.err.println(e1.getMessage());
            e1.printStackTrace();
            lines = null;
        } catch (Exception e1) {
            System.err.println(e1.getMessage());
            e1.printStackTrace();
            lines = null;
        }
    }
    
    private LookupTable getColorTable(double opacity) {
        LookupTable lut = null;
        lut = new LookupTable();
        
        lut.Allocate(colors.length, colors.length);
        lut.SetNumberOfTableValues(colors.length);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        
        for (int i = 0; i < colors.length; i++) {            
            lut.SetTableValue(i, colors[i][0], colors[i][1], colors[i][2], opacity);
        }
        
        return lut;
        //String filename = "rgb.256";
        
        //File file = new File(filename);
        
        /*try {
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
                
        return lut;*/
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
