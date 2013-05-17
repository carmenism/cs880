import geo.EcefPoint;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import netcdf.NetCDFToEcefPoints;

import vtk.vtkFloatArray;
import vtk.vtkNativeLibrary;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkRenderer;
import vtk.vtkStructuredGrid;

/**
 * Renders a NetCDF Great Lake Operational Forecast file.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class RenderLake extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JButton exitButton;
    private Properties prop;

    private vtkPanel panel;
    private vtkRenderer ren;
    private vtkStructuredGrid currentSGrid;

    private FullActor actorFull;
    private IsosurfaceActor actorIsosurfaceA, actorIsosurfaceB;
    private BoundaryActor actorBoundary;
    private DrawColorTable colorLegend;
    private NetCDFToEcefPoints ncToPts;

    private final double dataScalarMin;
    private final double dataScalarMax;
    private double scalarMin = Double.MAX_VALUE;
    private double scalarMax = -1 * Double.MAX_VALUE;
    private int[][] colors;
    private float missingValue;
    private boolean drawBoundary = false;

    private HashMap<Float, vtkStructuredGrid> gridsAtZScales = new HashMap<Float, vtkStructuredGrid>();

    /**
     * Loads the VTK libraries and prints out the ones which fail to load.
     */
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

    /**
     * Sets up the window that renders the lake.
     * 
     * @param fileName
     *            The path to the NetCDF input file.
     * @param colorFilename
     *            The path to the color table file.
     * @param config
     *            The path to the properties configuration file.
     * @param time
     *            The time index within the file to be used.
     */
    public RenderLake(String fileName, String colorFilename, String config,
            int time) {
        super(new BorderLayout());

        File file = new File(colorFilename);
        setColorTable(file);

        prop = new Properties();

        try {
            prop.load(new FileInputStream(config));
        } catch (IOException ex) {
            System.err
                    .println("Error opening Java properties configuration file");
            System.exit(1);
        }

        float initVertExag = 200.0f;
        missingValue = Float.parseFloat(prop.getProperty("missingValue"));
        ncToPts = new NetCDFToEcefPoints(prop, fileName, time);
        EcefPoint[][][] points = ncToPts.convert(initVertExag);

        if (points != null) {
            currentSGrid = getVtkStructuredGrid(points, missingValue);

            gridsAtZScales.put(initVertExag, currentSGrid);

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

            exitButton = new JButton("Exit");
            exitButton.addActionListener(this);

            colorLegend = new DrawColorTable(colors, this);
            colorLegend.setSize(768, 100);

            add(colorLegend, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);
            add(exitButton, BorderLayout.SOUTH);
        }

        dataScalarMin = scalarMin;
        dataScalarMax = scalarMax;
    }

    /**
     * Turns the depth peeling feature on.
     */
    public void depthPeelOn() {
        ren.UseDepthPeelingOn();
    }

    /**
     * Turns the depth peeling feature off.
     */
    public void depthPeelOff() {
        ren.UseDepthPeelingOff();
    }

    /**
     * Displays the panel. Acts somewhat like a repaint. Should be called
     * whenever the actors have been changed.
     */
    public void display() {
        panel.Render();
    }

    /**
     * Renders the full surface actor.
     */
    public void renderFull() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorFull);

        if (drawBoundary) {
            ren.AddActor(actorBoundary);
        }
    }

    /**
     * Renders the single isosurface actors.
     */
    public void renderSingleIsosurface() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorIsosurfaceA);

        if (drawBoundary) {
            ren.AddActor(actorBoundary);
        }
    }

    /**
     * Renders both of the isosurface actors.
     */
    public void renderDoubleIsosurface() {
        ren.RemoveAllViewProps();
        ren.AddActor(actorIsosurfaceA);
        ren.AddActor(actorIsosurfaceB);

        if (drawBoundary) {
            ren.AddActor(actorBoundary);
        }
    }

    /**
     * Makes it so the boundary actor is drawn.
     */
    public void drawBoundaryOn() {
        drawBoundary = true;

        ren.AddActor(actorBoundary);
    }

    /**
     * Makes it so the boundary actor is no longer drawn.
     */
    public void drawBoundaryOff() {
        drawBoundary = false;

        ren.RemoveActor(actorBoundary);
    }

    /**
     * Changes the vertical exaggeration from all actors.
     * 
     * @param verticalExaggeration
     *            The factor to indicate how much the vertical component should
     *            be exaggerated.
     */
    public void changeVerticalExaggeration(float verticalExaggeration) {
        ren.RemoveAllViewProps();

        currentSGrid = gridsAtZScales.get(verticalExaggeration);

        if (currentSGrid == null) {
            EcefPoint[][][] points = ncToPts.convert(verticalExaggeration);
            currentSGrid = getVtkStructuredGrid(points, missingValue);
            gridsAtZScales.put(verticalExaggeration, currentSGrid);
        }

        buildFullActor();
        buildIsosurfaceActorA();
        buildIsosurfaceActorB();
        buildBoundaryActor();
    }

    /**
     * Changes the colors for all actors and the color legend.
     */
    public void changeColor() {
        ren.RemoveAllViewProps();

        buildFullActor();
        buildIsosurfaceActorA();
        buildIsosurfaceActorB();
        buildBoundaryActor();

        colorLegend.resetColors(colors);
        colorLegend.repaint();
    }

    /**
     * Reverses the color tables for all actors.
     */
    public void reverseColor() {
        colorLegend.reverseColors();
        colorLegend.repaint();

        actorFull.getLookupTable().reverseTableColors();
        actorIsosurfaceA.getLookupTable().reverseTableColors();
        actorIsosurfaceB.getLookupTable().reverseTableColors();
    }

    /**
     * Builds the full surface vtkActor.
     */
    private void buildFullActor() {
        LookupTable lut = getColorTable(1.0);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.Build();

        actorFull = new FullActor(currentSGrid, lut, scalarMin, scalarMax);
    }

    /**
     * Builds the boundary vtkActor.
     */
    private void buildBoundaryActor() {
        LookupTable lut = new LookupTable();
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetValueRange(0.0, 0.0);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.SetAlphaRange(0.1, 0.1);
        lut.Build();

        actorBoundary = new BoundaryActor(currentSGrid, lut, scalarMin,
                scalarMax);
    }

    /**
     * Builds the first isosurface vtkActor.
     */
    private void buildIsosurfaceActorA() {
        LookupTable lut = getColorTable(1.0);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.Build();

        actorIsosurfaceA = new IsosurfaceActor(currentSGrid, lut, scalarMin,
                scalarMax, 2 * (scalarMin + scalarMax) / 3);
    }

    /**
     * Builds the second isosurface vtkActor.
     */
    private void buildIsosurfaceActorB() {
        LookupTable lut = getColorTable(0.6);
        lut.SetTableRange(scalarMin, scalarMax);
        lut.SetNanColor(0.0, 0.0, 0.0, 0.0);
        lut.Build();

        actorIsosurfaceB = new IsosurfaceActor(currentSGrid, lut, scalarMin,
                scalarMax, (scalarMin + scalarMax) / 3);
    }

    /**
     * Attempts to set the current colors according to the file passed in. If
     * there is a problem with this file, then the current color values will
     * remain.
     * 
     * @param file
     *            The file containing colors.
     */
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
                                "Must be at least three integer values per line.");
                    }

                    scanLine.close();
                    lines.add(line);
                }
            }

            scanFile.close();

            if (lines.size() == 256) {
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

    /**
     * Gets a look up table from the current R, G, B values that are stored
     * using the specified opacity value.
     * 
     * @param opacity
     *            An opacity value from 0.0 to 1.0 where 0.0 is completely
     *            transparent.
     * @return A vtkLookupTable object.
     */
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

    /**
     * Creates a structured grid given a 3D array of points.
     * 
     * @param points
     *            The points that will form the grid.
     * @param missingValue
     *            The value that represents the missing value.
     * @return A vtkStructuredGrid made from the points.
     */
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

    public double getDataScalarMin() {
        return dataScalarMin;
    }

    public double getDataScalarMax() {
        return dataScalarMax;
    }

    public void setScalarMin(double scalarMin) {
        this.scalarMin = scalarMin;
    }

    public void setScalarMax(double scalarMax) {
        this.scalarMax = scalarMax;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitButton)) {
            System.exit(0);
        }
    }

    /**
     * Describes the usage of this program.
     */
    public static void usage() {
        System.err.println("Four command line parameters are required:");
        System.err.println("\tlakefile colorfile configfile timeindex");
        System.err.println();
        System.err.println("\tlakefile -- ");
        System.err.println("The path of the NetCDF input file to be rendered");
        System.err.println();
        System.err.println("\tcolorfile -- ");
        System.err
                .println("The path of the color table file (must contain 256 colors, one color per line, RGB values from 0 to 255, any delimiter)");
        System.err.println();
        System.err.println("\tconfigfile -- ");
        System.err
                .println("The path to a Java properties configuration describing the variable names of the NetCDF input");
        System.err.println();
        System.err.println("\ttimeindex -- ");
        System.err
                .println("The time index to be used in the NetCDF file - e.g., 0");
        System.exit(1);
    }

    public static void main(final String[] args) {
        if (args.length != 4) {
            usage();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String filename = args[0];
                String colorFilename = args[1];
                String configFilename = args[2];

                RenderLake lake = new RenderLake(filename, colorFilename,
                        configFilename, 0);

                JFrame frame = new JFrame("Isosurface Renderer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(lake, BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                LakeControls lc = new LakeControls(lake);
                JScrollPane jsp = new JScrollPane(lc);

                JFrame frame2 = new JFrame("Isosurface Controls");
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.getContentPane().setLayout(new BorderLayout());
                frame2.getContentPane().add(jsp, BorderLayout.CENTER);
                frame2.setSize(700, 550);
                frame2.setLocationRelativeTo(frame);
                frame2.setVisible(true);

                System.out.println("Working Directory = "
                        + System.getProperty("user.dir"));
            }
        });
    }
}
