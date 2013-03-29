import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import vtk.*;

/**
 * An application that displays a 3D cone. A button allows you to close the
 * application.
 */
public class SimpleVTK extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private vtkPanel renWin;
    private JButton exitButton;

   /* static{
        try{    
            System.loadLibrary("vtkCommonJava");
            System.loadLibrary("vtkFilteringJava");
            System.loadLibrary("vtkImagingJava");
            System.loadLibrary("vtkGraphicsJava");
            System.loadLibrary("vtkRenderingJava");     
            System.loadLibrary("vtkIOJava");
            System.loadLibrary("vtkHybridJava");
            System.loadLibrary("vtkParallelJava");
        }catch(Throwable e){
            System.out.println("Error in loading VTK libraries");
        }
    }*/
    
    // -----------------------------------------------------------------
    // Load VTK library and print which library was not properly loaded
    
    static {
        if (!vtkNativeLibrary.LoadAllNativeLibraries()) {
            for (vtkNativeLibrary lib : vtkNativeLibrary.values()) {
                if (!lib.IsLoaded()) {
                    System.out.println(lib.GetLibraryName() + " not loaded");
                } else {
                    System.out.println(lib.GetLibraryName() + " loaded!!!!!!!!!!!!!!");
                }
            }
        }
        vtkNativeLibrary.DisableOutputWindow(null);
    }

    public SimpleVTK() {
        super(new BorderLayout());

        // build VTK Pipeline
        vtkConeSource cone = new vtkConeSource();
        cone.SetResolution(8);

        vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
        coneMapper.SetInputConnection(cone.GetOutputPort());

        vtkActor coneActor = new vtkActor();
        coneActor.SetMapper(coneMapper);

        renWin = new vtkPanel();
        renWin.GetRenderer().AddActor(coneActor);

        // Add Java UI components
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        add(renWin, BorderLayout.CENTER);
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
                JFrame frame = new JFrame("SimpleVTK");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(new SimpleVTK(), BorderLayout.CENTER);
                frame.setSize(400, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
