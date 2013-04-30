package head;

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
    private vtkPanel panel;
    private vtkRenderer ren;
    private JButton exitButton;    
    
    private Actor bone, skin;

    private double[] background = { 0.5, 0.5, 0.5 };    

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

        Interval hueBone = new Interval( 0.0, 0.02 );
        Interval satBone = new Interval( 0.0, 0.02 );
        Interval valueBone = new Interval( 0.0, 1.0 );
        Interval alphaBone = new Interval( 1.0, 1.0 );        

        Interval hueSkin = new Interval( 0.75, 1.0 );
        Interval satSkin = new Interval( 0.0, 1.0 );
        Interval valueSkin = new Interval( 0.0, 1.0 );    
        Interval alphaSkin = new Interval( 0.5, 0.6 );

        LookupTable lutBone = new LookupTable(hueBone, satBone, valueBone, alphaBone);
        LookupTable lutSkin = new LookupTable(hueSkin, satSkin, valueSkin, alphaSkin);

        Interval rangeBone = new Interval(75.0, 76.0);
        Interval rangeSkin = new Interval(25.0, 26.0);
        
        ContourFilter contourBone = new ContourFilter(reader, 4, rangeBone);
        ContourFilter contourSkin = new ContourFilter(reader, 4, rangeSkin);

        bone = new Actor(reader, lutBone, contourBone);
        skin = new Actor(reader, lutSkin, contourSkin);

        panel = new vtkPanel();
        ren = panel.GetRenderer();

        ren.SetBackground(0.5, 0.5, 0.5);
        ren.AddActor(bone);
        ren.AddActor(skin);
        ren.ResetCamera();

        // Add Java UI components
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        add(panel, BorderLayout.CENTER);
        add(exitButton, BorderLayout.SOUTH);
    }
      
    public void setBackground(double r, double g, double b) {
        background[0] = r;
        background[1] = g;
        background[2] = b;
    }
    
    public double getBackgroundR() {
        return background[0];
    }
    
    public double getBackgroundG() {
        return background[1];
    }
    
    public double getBackgroundB() {
        return background[2];
    }
    
    public Actor getBone() {
        return bone;
    }

    public Actor getSkin() {
        return skin;
    }

    public void display() {
        ren.SetBackground(background);
    

        panel.Render();
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
                Head head = new Head();

                JFrame frame = new JFrame("Head");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(head, BorderLayout.CENTER);
                frame.setSize(800, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                JFrame frame2 = new JFrame("Head Controls");
                frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame2.getContentPane().setLayout(new BorderLayout());
                frame2.getContentPane().add(new HeadControls(head),
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
