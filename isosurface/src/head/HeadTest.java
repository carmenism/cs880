package head;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import vtk.*;

public class HeadTest extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    // private vtkPanel renWin;
    private JButton exitButton;

    // -----------------------------------------------------------------
    // Load VTK library and print which library was not properly loaded

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

    public HeadTest() {
        super(new BorderLayout());
        
        vtkStructuredPointsReader reader = new vtkStructuredPointsReader();
        reader.SetFileName("head.120.vtk");

        double[] range = {70.0, 80.0};
        int num = 20;
        
        vtkLookupTable lutBone = new vtkLookupTable();

        lutBone.SetNumberOfColors(num);
        /*lutBone.SetHueRange(0.25, 0.5);
        lutBone.SetSaturationRange(0.75, 1.0);
        lutBone.SetValueRange(0.75, 1.0);
        lutBone.SetAlphaRange(1.0, 1.0);*/
        lutBone.SetTableRange(range);
        lutBone.SetNumberOfTableValues(num);
        lutBone.Build();
                
        vtkImageMapToColors imtcBone = new vtkImageMapToColors();
        imtcBone.SetLookupTable(lutBone);
        imtcBone.SetInput(reader.GetOutput());
                
        vtkContourFilter contourBone = new vtkContourFilter();
        contourBone.SetInput(reader.GetOutput());
        contourBone.GenerateValues(num, range);
        
        vtkProbeFilter probeBone = new vtkProbeFilter();
        probeBone.SetInput(contourBone.GetOutput());
        probeBone.SetSource(imtcBone.GetOutput());
        
        vtkPolyDataMapper mapperBone = new vtkPolyDataMapper();
        mapperBone.SetInput((vtkPolyData) probeBone.GetOutput());
        mapperBone.SetLookupTable(lutBone);
        mapperBone.SetColorModeToMapScalars();
        mapperBone.SetScalarRange(range);
        
        vtkActor actorBone = new vtkActor();
        actorBone.SetMapper(mapperBone);
                
        vtkPanel panel = new vtkPanel();
        vtkRenderer ren = panel.GetRenderer();
        
        ren.SetBackground(0.5, 0.5, 0.5);
        ren.AddActor(actorBone);
        ren.ResetCamera();
        
        // Add Java UI components
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        add(panel, BorderLayout.CENTER);
        add(exitButton, BorderLayout.SOUTH); 
    }

    /** An ActionListener that listens to the button. */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitButton)) {
            System.exit(0);
        }
    }

    public static void main(String s[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Head");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(new HeadTest(), BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                System.out.println("Working Directory = " +
                        System.getProperty("user.dir"));
            }
        });
    }
}