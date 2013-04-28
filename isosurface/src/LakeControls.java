import gui.TableLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkActor;
import vtk.vtkProperty;


public class LakeControls extends JPanel implements ItemListener,
ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private static final int ROW_H = 30;
    
    private JRadioButton radioRepPoints, radioRepWireframe, radioRepSurface;
    private JRadioButton radioBfCullingOn, radioBfCullingOff;
    private JRadioButton radioFfCullingOn, radioFfCullingOff;
    
    private RenderLake render;
    private vtkActor lake;
    
    public LakeControls(RenderLake render) {
        super();
        
        this.render = render;
        this.lake = render.getFullLakeActor();
        
        JPanel repPanel = makeRepresentationRadioPanel();
        JPanel cullingPanel = makeCullingPanel();
        
        add(repPanel, "0, 0");
        add(cullingPanel, "0, 1");
    }
       
    private JPanel makeCullingPanel() {
        double[][] size = { { 0.50, 0.50 }, { 2 * ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));
        
        JPanel frontfacePanel = makeFrontfaceCullingRadioPanel();
        JPanel backfacePanel = makeBackfaceCullingRadioPanel();
        
        panel.add(frontfacePanel, "0, 0");
        panel.add(backfacePanel, "1, 0");
        
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Culling"));
        
        return panel;
    }
    
    private JPanel makeFrontfaceCullingRadioPanel() {
        double[][] size = { { 0.50, 0.50 }, { ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        radioFfCullingOn = new JRadioButton("On", false);
        radioFfCullingOff = new JRadioButton("Off", true);

        ButtonGroup buttonGroupBfCulling = new ButtonGroup();
        buttonGroupBfCulling.add(radioFfCullingOn);
        buttonGroupBfCulling.add(radioFfCullingOff);

        panel.add(radioFfCullingOn, "0, 0");
        panel.add(radioFfCullingOff, "1, 0");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Frontface"));

        radioFfCullingOn.addActionListener(this);
        radioFfCullingOff.addActionListener(this);

        return panel;
    }
    
    
    private JPanel makeBackfaceCullingRadioPanel() {
        double[][] size = { { 0.50, 0.50 }, { ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        radioBfCullingOn = new JRadioButton("On", false);
        radioBfCullingOff = new JRadioButton("Off", true);

        ButtonGroup buttonGroupBfCulling = new ButtonGroup();
        buttonGroupBfCulling.add(radioBfCullingOn);
        buttonGroupBfCulling.add(radioBfCullingOff);

        panel.add(radioBfCullingOn, "0, 0");
        panel.add(radioBfCullingOff, "1, 0");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Backface"));

        radioBfCullingOn.addActionListener(this);
        radioBfCullingOff.addActionListener(this);

        return panel;
    }
    
    private JPanel makeRepresentationRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));

        radioRepPoints = new JRadioButton("Points", false);
        radioRepWireframe = new JRadioButton("Wireframe", false);
        radioRepSurface = new JRadioButton("Surface", true);

        ButtonGroup buttonGroupDisplay = new ButtonGroup();
        buttonGroupDisplay.add(radioRepPoints);
        buttonGroupDisplay.add(radioRepWireframe);
        buttonGroupDisplay.add(radioRepSurface);

        panel.add(radioRepPoints);
        panel.add(radioRepWireframe);
        panel.add(radioRepSurface);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Representation"));

        radioRepPoints.addActionListener(this);
        radioRepWireframe.addActionListener(this);
        radioRepSurface.addActionListener(this);

        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
                
        if (source == radioRepPoints) {
            lake.GetProperty().SetRepresentationToPoints();
        } else if (source == radioRepWireframe) {
            lake.GetProperty().SetRepresentationToWireframe();
        } else if (source == radioRepSurface) {
            lake.GetProperty().SetRepresentationToSurface();            
        } else if (source == radioBfCullingOn) {
            lake.GetProperty().BackfaceCullingOn();  
        } else if (source == radioBfCullingOff) {
            lake.GetProperty().BackfaceCullingOff();            
        } else if (source == radioFfCullingOn) {
            lake.GetProperty().FrontfaceCullingOn();  
        } else if (source == radioFfCullingOff) {
            lake.GetProperty().FrontfaceCullingOff();            
        }
        
        render.display();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
        
    }

}
