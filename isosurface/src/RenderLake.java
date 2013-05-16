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
import vtk.vtkFloatArray;
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
    private FullActor actorFull;
    private IsosurfaceActor actorIsosurfaceA, actorIsosurfaceB;
    private BoundaryActor actorBoundary;

    private double scalarMin = Double.MAX_VALUE;
    private double scalarMax = -1 * Double.MAX_VALUE;

    private NetCDFToEcefPoints ncToPts;
    private NetCDFConfiguration config;

    private int[][] colors;

    private vtkStructuredGrid currentSGrid;
    private boolean drawBoundary = false;
    private DrawColorTable canvas;

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

    public BoundaryActor getBoundaryActor() {
        return actorBoundary;
    }

    public IsosurfaceActor getIsosurfaceActorA() {
        return actorIsosurfaceA;
    }

    public IsosurfaceActor getIsosurfaceActorB() {
        return actorIsosurfaceB;
    }

    public void display() {
        panel.Render();
    }

    public void renderFull() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorFull);

        if (drawBoundary) {
            ren.AddActor(actorBoundary);
        }
    }

    public void renderSingleContour() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorIsosurfaceA);

        if (drawBoundary) {
            ren.AddActor(actorBoundary);
        }
    }

    public void renderDoubleContour() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorIsosurfaceA);
        ren.AddActor(actorIsosurfaceB);

        if (drawBoundary) {
            ren.AddActor(actorBoundary);
        }
    }

    public void drawBoundaryOn() {
        drawBoundary = true;

        ren.AddActor(actorBoundary);
    }

    public void drawBoundaryOff() {
        drawBoundary = false;

        ren.RemoveActor(actorBoundary);
    }

    public RenderLake(NetCDFConfiguration config, String fileName,
            String scalarName, int time) {
        super(new BorderLayout());

        this.config = config;

        File file = new File("rgb.256");
        setColorTable(file);

        ncToPts = new NetCDFToEcefPoints(config, fileName, scalarName, time);

        EcefPoint[][][] points = ncToPts.convert(200.0f);

        if (points != null) {
            currentSGrid = getVtkStructuredGrid(points,
                    config.getMissingValue());

            gridsAtZScales.put(200.0f, currentSGrid);

            System.out.println("Min: " + scalarMin);
            System.out.println("Max: " + scalarMax);

            buildFullActor();
            buildIsosurfaceActorA();
            buildIsosurfaceActorB();
            buildBoundaryActor();

            panel = new vtkPanel();
            ren = panel.GetRenderer();

            ren.SetBackground(0.45, 0.5, 0.55);
            ren.AddActor(actorFull);
            ren.TwoSidedLightingOn();
            ren.ResetCamera();

            // Add Java UI components
            exitButton = new JButton("Exit");
            exitButton.addActionListener(this);

            canvas = new DrawColorTable(colors);
            // canvas.setBackground(Color.blue);
            canvas.setSize(768, 100);

            add(canvas, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);
            add(exitButton, BorderLayout.SOUTH);

        }
    }
    
    public void setBackgroundRed(double r) {
        double[] bg = ren.GetBackground();
        
        ren.SetBackground(r, bg[1], bg[2]);
    }

    public void setBackgroundGreen(double g) {
        double[] bg = ren.GetBackground();
        
        ren.SetBackground(bg[0], g, bg[2]);
    }

    public void setBackgroundBlue(double b) {
        double[] bg = ren.GetBackground();
        
        ren.SetBackground(bg[0], bg[1], b);
    }
    
    public double getBackgroundRed() {
        return ren.GetBackground()[0];
    }

    public double getBackgroundGreen() {
        return ren.GetBackground()[1];
    }

    public double getBackgroundBlue() {
        return ren.GetBackground()[2];
    }

    public void depthPeelOn() {
        ren.UseDepthPeelingOn();
    }

    public void depthPeelOff() {
        ren.UseDepthPeelingOff();
    }

    public void changeZScale(float zScale) {
        ren.RemoveAllViewProps();

        currentSGrid = gridsAtZScales.get(zScale);

        if (currentSGrid == null) {
            EcefPoint[][][] points = ncToPts.convert(zScale);
            currentSGrid = getVtkStructuredGrid(points,
                    config.getMissingValue());
            gridsAtZScales.put(zScale, currentSGrid);
        }

        buildFullActor();
        buildIsosurfaceActorA();
        buildIsosurfaceActorB();
        buildBoundaryActor();
    }

    public void changeColor() {
        ren.RemoveAllViewProps();

        buildFullActor();
        buildIsosurfaceActorA();
        buildIsosurfaceActorB();
        buildBoundaryActor();

        canvas.resetColors(colors);
        canvas.repaint();
    }

    public void reverseColor() {
        canvas.reverseColors();
        canvas.repaint();   
        
        actorFull.getLookupTable().reverseTableColors();
        actorIsosurfaceA.getLookupTable().reverseTableColors();
        actorIsosurfaceB.getLookupTable().reverseTableColors();
    }
    
    private void buildFullActor() {
        LookupTable lut = getColorTable(1.0);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.Build();

        actorFull = new FullActor(currentSGrid, lut, scalarMin, scalarMax);
    }

    private void buildBoundaryActor() {
        LookupTable lut = new LookupTable();
        lut.SetTableRange(scalarMin, scalarMax);
        // lut.SetSaturationRange(0.0, 0.0);
        lut.SetValueRange(0.0, 0.0);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.SetAlphaRange(0.1, 0.1);
        lut.Build();

        actorBoundary = new BoundaryActor(currentSGrid, lut, scalarMin, scalarMax);
    }

    private void buildIsosurfaceActorA() {
        LookupTable lut = getColorTable(0.6);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.Build();

        actorIsosurfaceA = new IsosurfaceActor(currentSGrid, lut, scalarMin,
                scalarMax, 2 * (scalarMin + scalarMax) / 3);
    }

    private void buildIsosurfaceActorB() {
        LookupTable lut = getColorTable(0.4);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.Build();

        actorIsosurfaceB = new IsosurfaceActor(currentSGrid, lut, scalarMin,
                scalarMax, (scalarMin + scalarMax) / 3);
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

            if (lines.size() > 0 && lines.size() <= 256) {
                colors = new int[lines.size()][3];

                for (int i = 0; i < lines.size(); i++) {
                    Scanner scanLine = new Scanner(lines.get(i));

                    colors[i][0] = scanLine.nextInt();
                    colors[i][1] = scanLine.nextInt();
                    colors[i][2] = scanLine.nextInt();

                    scanLine.close();
                }
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
            double r = (double) colors[i][0] / 255;
            double g = (double) colors[i][1] / 255;
            double b = (double) colors[i][2] / 255;
            lut.SetTableValue(i, r, g, b, opacity);
        }

        return lut;
    }

    private vtkStructuredGrid getVtkStructuredGrid(EcefPoint[][][] points,
            double missingValue) {
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
                    double[] point = new double[3];
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

    public static void main(String[] args) {
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

                // String filename =
                // "glofs.lsofs.fields.nowcast.20130430.t00z.nc";
                // String filename =
                // "glofs.lsofs.fields.nowcast.20120701.t00z.nc";
                // String filename =
                // "glofs.lsofs.fields.forecast.20130301.t00z.nc";
                String filename = "glofs.leofs.fields.nowcast.20130425.t01z.nc";

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
