import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkActor;


public class LakePropertyControls extends JPanel implements ItemListener, ChangeListener, ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JRadioButton radioRepPoints, radioRepWireframe, radioRepSurface;
    private JRadioButton radioEdgesOn, radioEdgesOff;

    private JSlider sliderLineWidth, sliderPointSize, sliderOpacity, sliderValue;
    
    private Actor currentActor;
    
    private RenderLake renderLake;
        
    private double RES = 100;
    
    public LakePropertyControls(RenderLake render, Actor actor, String title) {
        super(new GridLayout(6, 1));

        currentActor = actor;
        renderLake = render;
        
        JPanel typePanel = makeRepresentationRadioPanel();
        JPanel edgesPanel = makeEdgesRadioPanel();
        JPanel pointSizePanel = makePointSizePanel();
        JPanel lineWidthPanel = makeLineWidthPanel();
        JPanel opacityPanel = makeOpacityPanel();
        

        super.add(typePanel, "0, 0");
        super.add(edgesPanel, "0, 1");
        super.add(pointSizePanel, "0, 2");
        super.add(lineWidthPanel, "0, 3");
        super.add(opacityPanel, "0, 4");
        

        if (actor.getContourFilter() != null) {
            JPanel valuePanel = makeValuePanel();
            super.add(valuePanel, "0, 5");
        }
        
        super.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title));
    }
    
    public Actor getCurrentActor() {
        return currentActor;
    }
    
    public void setCurrentActor(Actor actor) {
        currentActor = actor;
        
        updateActor();
    }
    
    public void updateActor() {
        if (radioRepPoints.isSelected()) {
            currentActor.GetProperty().SetRepresentationToPoints();
        } else if (radioRepWireframe.isSelected()) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (radioRepSurface.isSelected()) {
            currentActor.GetProperty().SetRepresentationToSurface();
        }
        
        if (radioEdgesOn.isSelected()) {
            currentActor.GetProperty().EdgeVisibilityOn();
        } else {
            currentActor.GetProperty().EdgeVisibilityOff();            
        }
        
        currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        currentActor.GetProperty().SetPointSize(sliderPointSize.getValue());
        
        double opacity = sliderOpacity.getValue() / RES;            
        currentActor.GetProperty().SetOpacity(opacity);
        
        if (sliderValue != null) {
            double value = sliderValue.getValue() / RES;            
            currentActor.getContourFilter().SetValue(0, value);
        }
        
        renderLake.display();
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
                BorderFactory.createEtchedBorder(), "Type"));

        radioRepPoints.addActionListener(this);
        radioRepWireframe.addActionListener(this);
        radioRepSurface.addActionListener(this);

        return panel;
    }
    
    private JPanel makePointSizePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));

        sliderPointSize = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        sliderPointSize.setMajorTickSpacing(1);
        sliderPointSize.setPaintLabels(true);

        panel.add(sliderPointSize);  

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Point Size"));

        sliderPointSize.addChangeListener(this);

        return panel;
    }
    
    private JPanel makeLineWidthPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        sliderLineWidth = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
        sliderLineWidth.setMajorTickSpacing(1);
        sliderLineWidth.setPaintLabels(true);

        panel.add(sliderLineWidth);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Line Width"));

        sliderLineWidth.addChangeListener(this);

        return panel;
    }
    
    private JPanel makeOpacityPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        int min = 0;
        int max = (int) (1.0 * RES);
        int init = (int) (currentActor.GetProperty().GetOpacity() * RES);
        
        sliderOpacity = new JSlider(JSlider.HORIZONTAL, min, max, init);

        panel.add(sliderOpacity);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Opacity"));

        sliderOpacity.addChangeListener(this);

        return panel;
    }
    
    private JPanel makeValuePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        int min = (int) (renderLake.getScalarMin() * RES);
        int max = (int) (renderLake.getScalarMax() * RES);
        int init = (int) (currentActor.getContourFilter().GetValue(0) * RES);
        
        sliderValue = new JSlider(JSlider.HORIZONTAL, min, max, init);

        panel.add(sliderValue);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Contour Value"));

        sliderValue.addChangeListener(this);

        return panel;
    }
    
    private JPanel makeEdgesRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        radioEdgesOn = new JRadioButton("On", false);
        radioEdgesOff = new JRadioButton("Off", true);
        
        ButtonGroup buttonGroupEdges = new ButtonGroup();
        buttonGroupEdges.add(radioEdgesOn);
        buttonGroupEdges.add(radioEdgesOff);

        panel.add(radioEdgesOn);
        panel.add(radioEdgesOff);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Edges"));

        radioEdgesOn.addActionListener(this);
        radioEdgesOff.addActionListener(this);

        return panel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioRepPoints) {
            currentActor.GetProperty().SetRepresentationToPoints();
        } else if (source == radioRepWireframe) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (source == radioRepSurface) {
            currentActor.GetProperty().SetRepresentationToSurface();
        } else if (source == radioEdgesOn) {
            currentActor.GetProperty().EdgeVisibilityOn();
        } else if (source == radioEdgesOff) {
            currentActor.GetProperty().EdgeVisibilityOff();            
        }
        
        renderLake.display();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == sliderLineWidth) {
            currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        } else if (source == sliderPointSize) {
            currentActor.GetProperty().SetPointSize(sliderPointSize.getValue());
        } else if (source == sliderOpacity) {
            double value = sliderOpacity.getValue() / RES;
            
            currentActor.GetProperty().SetOpacity(value);
        } else if (sliderValue != null && source == sliderValue) {
            double value = sliderValue.getValue() / RES;
            
            currentActor.getContourFilter().SetValue(0, value);
        }

        renderLake.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub        
    }
}
