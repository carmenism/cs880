import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
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

    private JSlider sliderLineWidth, sliderPointSize, sliderOpacity;
    
    protected Actor currentActor;
    
    protected RenderLake renderLake;
        
    protected final double RES = 100;
    
    protected JPanel paneType, panelEdges, panelPoint, panelLine, panelOpacity;
    
    public LakePropertyControls(RenderLake render, Actor actor, String title) {
        super(new GridLayout(6, 1));

        currentActor = actor;
        renderLake = render;
        
        panelEdges = makeEdgesRadioPanel();
        panelPoint = makePointSizePanel();
        panelLine = makeLineWidthPanel();
        panelOpacity = makeOpacityPanel();
        paneType = makeRepresentationRadioPanel();
        
        super.add(paneType, 0);
        super.add(panelEdges, 1);
        super.add(panelPoint, 2);
        super.add(panelLine, 3);
        super.add(panelOpacity, 4);
        
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
        
        renderLake.display();
    }
    
    private JPanel makeRepresentationRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));

        String rep = currentActor.GetProperty().GetRepresentationAsString();
                
        boolean points = false, wireframe = false, surface = false;
        
        if (rep.equals("Surface")) {
            surface = true;
            wireframe = false;
            points = false;
            representAsSurface();
        } else if (rep.equals("Wireframe")) {
            surface = false;
            wireframe = true;
            points = false;
            representAsWireframe();
        } else {
            surface = false;
            wireframe = false;
            points = true;
            representAsPoints();
        }
        
        radioRepPoints = new JRadioButton("Points", points);
        radioRepWireframe = new JRadioButton("Wireframe", wireframe);
        radioRepSurface = new JRadioButton("Surface", surface);

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
        
        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(min, new JLabel("0"));
        dict.put((min + max) / 2, new JLabel("0.5"));
        dict.put(max, new JLabel("1.0"));
        
        sliderOpacity = new JSlider(JSlider.HORIZONTAL, min, max, init);
        sliderOpacity.setLabelTable(dict);
        sliderOpacity.setMajorTickSpacing(25);
        sliderOpacity.setMinorTickSpacing(5);
        sliderOpacity.setPaintLabels(true);
        sliderOpacity.setPaintTicks(true);

        panel.add(sliderOpacity);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Opacity"));

        sliderOpacity.addChangeListener(this);

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
    
    private void representAsPoints() {
        sliderPointSize.setEnabled(true);
        sliderLineWidth.setEnabled(false);
        radioEdgesOn.setEnabled(false);
        radioEdgesOff.setEnabled(false);
    }
    
    private void representAsWireframe() {
        sliderPointSize.setEnabled(false);
        sliderLineWidth.setEnabled(true);
        radioEdgesOn.setEnabled(false);
        radioEdgesOff.setEnabled(false);
    }
    
    private void representAsSurface() {
        sliderPointSize.setEnabled(false);
        sliderLineWidth.setEnabled(false);
        radioEdgesOn.setEnabled(true);
        radioEdgesOff.setEnabled(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioRepPoints) {
            currentActor.GetProperty().SetRepresentationToPoints();
            representAsPoints();
        } else if (source == radioRepWireframe) {
            currentActor.GetProperty().SetRepresentationToWireframe();
            representAsWireframe();
        } else if (source == radioRepSurface) {
            currentActor.GetProperty().SetRepresentationToSurface();
            representAsSurface();
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
        }

        renderLake.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub        
    }
}
