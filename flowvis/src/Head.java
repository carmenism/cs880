import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import vtk.*;

public class Head extends JPanel implements ActionListener {
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

    public Head() {
        super(new BorderLayout());
        
        vtkStructuredPointsReader reader = new vtkStructuredPointsReader();
        reader.SetFileName("head.120.vtk");
        
        vtkLookupTable lutBone = new vtkLookupTable();
        lutBone.SetNumberOfColors(256);
        lutBone.SetTableRange(0, 255);
        
        lutBone.SetHueRange(0.0, 1.0);
        lutBone.SetSaturationRange(0.0, 0.0);
        lutBone.SetValueRange(0.0, 1.0);
        lutBone.SetAlphaRange(1.0, 1.0);
        lutBone.Build();
        
        vtkLookupTable lutSkin = new vtkLookupTable();
        lutSkin.SetNumberOfColors(256);
        lutSkin.SetTableRange(0, 255);
        
        lutSkin.SetHueRange(0.0, 0.0);
        lutSkin.SetSaturationRange(0.0, 1.0);
        lutSkin.SetValueRange(0.0, 1.0);
        lutSkin.SetAlphaRange(0.5, 0.6);
        lutSkin.Build();
        
        vtkImageMapToColors imtcBone = new vtkImageMapToColors();
        imtcBone.SetLookupTable(lutBone);
        imtcBone.SetInput(reader.GetOutput());
        
        vtkImageMapToColors imtcSkin = new vtkImageMapToColors();
        imtcSkin.SetLookupTable(lutSkin);
        imtcSkin.SetInput(reader.GetOutput());
        
        vtkContourFilter contourBone = new vtkContourFilter();
        contourBone.SetInput(reader.GetOutput());
        contourBone.SetNumberOfContours(1);
        contourBone.SetValue(0, 75.0);
        
        vtkContourFilter contourSkin = new vtkContourFilter();
        contourSkin.SetInput(reader.GetOutput());
        contourSkin.SetNumberOfContours(1);
        contourSkin.SetValue(0, 25.0);
        
        vtkProbeFilter probeBone = new vtkProbeFilter();
        probeBone.SetInput(contourBone.GetOutput());
        probeBone.SetSource(imtcBone.GetOutput());
        
        vtkProbeFilter probeSkin = new vtkProbeFilter();
        probeSkin.SetInput(contourSkin.GetOutput());
        probeSkin.SetSource(imtcSkin.GetOutput());
        
        vtkPolyDataMapper mapperBone = new vtkPolyDataMapper();
        mapperBone.SetInput((vtkPolyData) probeBone.GetOutput());
        mapperBone.SetLookupTable(lutBone);
        mapperBone.SetColorModeToMapScalars();
        
        vtkPolyDataMapper mapperSkin = new vtkPolyDataMapper();
        mapperSkin.SetInput((vtkPolyData) probeSkin.GetOutput());
        mapperSkin.SetLookupTable(lutSkin);
        mapperSkin.SetColorModeToMapScalars();
        
        vtkActor actorBone = new vtkActor();
        actorBone.SetMapper(mapperBone);
        
        vtkActor actorSkin = new vtkActor();
        actorSkin.SetMapper(mapperSkin);
        
        vtkPanel panel = new vtkPanel();
        vtkRenderer ren =  panel.GetRenderer();
        
        ren.SetBackground(0.5, 0.5, 0.5);
        ren.AddActor(actorBone);
        ren.AddActor(actorSkin);
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
                frame.getContentPane().add(new Head(), BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                System.out.println("Working Directory = " +
                        System.getProperty("user.dir"));
            }
        });
    }
}
