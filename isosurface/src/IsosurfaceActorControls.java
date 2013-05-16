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

public class IsosurfaceActorControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = -4360527297291555077L;

    protected final double RES = 100;

    protected JRadioButton radioEdgesOn, radioEdgesOff;
    private JSlider sliderValue;

    protected IsosurfaceActor currentActor;
    protected JPanel paneType, panelPoint, panelLine, panelOpacity, panelEdges, panelValue;
    protected JRadioButton radioRepPoints, radioRepWireframe, radioRepSurface;
    protected JSlider sliderLineWidth, sliderPointSize, sliderOpacity;

    protected RenderLake renderLake;

    public IsosurfaceActorControls(RenderLake render, IsosurfaceActor actor,
            String title) {
        super(new GridLayout(6, 1));

        this.currentActor = actor;
        this.renderLake = render;

        panelEdges = makeEdgesRadioPanel();
        panelValue = makeValuePanel();

        panelPoint = makePointSizePanel();
        panelLine = makeLineWidthPanel();
        panelOpacity = makeOpacityPanel();
        paneType = makeRepresentationRadioPanel();

        super.add(panelValue, 0);
        super.add(paneType, 1);
        super.add(panelEdges, 2);
        super.add(panelPoint, 3);
        super.add(panelLine, 4);
        super.add(panelOpacity, 5);

        super.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title));
    }

    public void disableAll() {
        super.setEnabled(false);
        
        paneType.setEnabled(false);
        sliderValue.setEnabled(false);
        sliderOpacity.setEnabled(false);   
        panelOpacity.setEnabled(false);        
        radioRepSurface.setEnabled(false);
        radioRepPoints.setEnabled(false);
        radioRepWireframe.setEnabled(false);
        panelValue.setEnabled(false);
        
        radioEdgesOn.setEnabled(false);
        radioEdgesOff.setEnabled(false);
        panelEdges.setEnabled(false);        

        sliderLineWidth.setEnabled(false);
        panelLine.setEnabled(false);

        sliderPointSize.setEnabled(false);
        panelPoint.setEnabled(false);
    }
    
    public void enableAll() {
        super.setEnabled(true);
        
        paneType.setEnabled(true);
        sliderValue.setEnabled(true);
        panelOpacity.setEnabled(true);  
        sliderOpacity.setEnabled(true);      
        radioRepSurface.setEnabled(true);
        radioRepPoints.setEnabled(true);
        radioRepWireframe.setEnabled(true);
        panelValue.setEnabled(true);
        
        boolean solid = false;
        boolean point = false;
        boolean lines = false;
                
        if (radioRepSurface.isSelected()) {
            solid = true;
        } else if (radioRepPoints.isSelected()) {
            point = true;
        } else {
            lines = true;
        }
        
        radioEdgesOn.setEnabled(solid);
        radioEdgesOff.setEnabled(solid);
        panelEdges.setEnabled(solid);        

        sliderLineWidth.setEnabled(lines);
        panelLine.setEnabled(lines);

        sliderPointSize.setEnabled(point);
        panelPoint.setEnabled(point);
    }

    private JPanel makeRepresentationRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));

        String rep = currentActor.GetProperty().GetRepresentationAsString();

        boolean points = false, wireframe = false, solid = false;

        if (rep.equals("Surface")) {
            solid = true;
            wireframe = false;
            points = false;
        } else if (rep.equals("Wireframe")) {
            solid = false;
            wireframe = true;
            points = false;
        } else {
            solid = false;
            wireframe = false;
            points = true;
        }

        radioRepPoints = new JRadioButton("Points", points);
        radioRepWireframe = new JRadioButton("Wireframe", wireframe);
        radioRepSurface = new JRadioButton("Solid", solid);

        if (points) {
            representAsPoints();
        } else if (wireframe) {
            representAsWireframe();
        } else {
            representAsSurface();
        }

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

    protected void representAsPoints() {
        panelPoint.setEnabled(true);
        sliderPointSize.setEnabled(true);

        panelLine.setEnabled(false);
        sliderLineWidth.setEnabled(false);

        radioEdgesOn.setEnabled(false);
        radioEdgesOff.setEnabled(false);
        panelEdges.setEnabled(false);
    }

    protected void representAsWireframe() {
        panelPoint.setEnabled(false);
        sliderPointSize.setEnabled(false);

        panelLine.setEnabled(true);
        sliderLineWidth.setEnabled(true);

        radioEdgesOn.setEnabled(false);
        radioEdgesOff.setEnabled(false);
        panelEdges.setEnabled(false);
    }

    protected void representAsSurface() {
        panelPoint.setEnabled(false);
        sliderPointSize.setEnabled(false);

        panelLine.setEnabled(false);
        sliderLineWidth.setEnabled(false);

        radioEdgesOn.setEnabled(true);
        radioEdgesOff.setEnabled(true);
        panelEdges.setEnabled(true);
    }

    public Actor getCurrentActor() {
        return currentActor;
    }

    public void setCurrentActor(IsosurfaceActor actor) {
        currentActor = actor;

        updateActor();
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

    public void updateActor() {
        if (sliderValue != null) {
            double value = sliderValue.getValue() / RES;
            currentActor.getContourFilter().SetValue(0, value);
        }

        if (radioRepPoints.isSelected()) {
            currentActor.GetProperty().SetRepresentationToPoints();
        } else if (radioRepWireframe.isSelected()) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (radioRepSurface.isSelected()) {
            currentActor.GetProperty().SetRepresentationToSurface();
        }

        currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        currentActor.GetProperty().SetPointSize(sliderPointSize.getValue());

        double opacity = sliderOpacity.getValue() / RES;
        currentActor.getLookupTable().setOpacityForAllColors(opacity);

        renderLake.display();
    }

    private JPanel makeValuePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));

        int min = (int) (renderLake.getScalarMin() * RES);
        int max = (int) (renderLake.getScalarMax() * RES);
        int init = (int) (currentActor.getContourFilter().GetValue(0) * RES);

        double mid = (renderLake.getScalarMin() + renderLake.getScalarMax()) / 2.0;
        double minMid = (renderLake.getScalarMin() + mid) / 2.0;
        double midMax = (mid + renderLake.getScalarMax()) / 2.0;

        int midIndex = (min + max) / 2;
        int minMidIndex = (min + midIndex) / 2;
        int midMaxIndex = (midIndex + max) / 2;

        String sMin = String.format("%.2f", renderLake.getScalarMin());
        String sMinMid = String.format("%.2f", minMid);
        String sMid = String.format("%.2f", mid);
        String sMidMax = String.format("%.2f", midMax);
        String sMax = String.format("%.2f", renderLake.getScalarMax());

        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(min, new JLabel(sMin));
        dict.put(minMidIndex, new JLabel(sMinMid));
        dict.put(midIndex, new JLabel(sMid));
        dict.put(midMaxIndex, new JLabel(sMidMax));
        dict.put(max, new JLabel(sMax));

        sliderValue = new JSlider(JSlider.HORIZONTAL, min, max, init);
        sliderValue.setLabelTable(dict);
        sliderValue.setMajorTickSpacing((max - min) / 4);
        sliderValue.setMinorTickSpacing((max - min) / 16);
        sliderValue.setPaintLabels(true);
        sliderValue.setPaintTicks(true);

        panel.add(sliderValue);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Isovalue"));

        sliderValue.addChangeListener(this);

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
        int init = (int) (currentActor.getLookupTable()
                .getOpacityForAllColors() * RES);// GetProperty().GetOpacity() *
                                                 // RES);

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

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (sliderValue != null && source == sliderValue) {
            double value = sliderValue.getValue() / RES;

            currentActor.getContourFilter().SetValue(0, value);
        } else if (source == sliderLineWidth) {
            currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        } else if (source == sliderPointSize) {
            currentActor.GetProperty().SetPointSize(sliderPointSize.getValue());
        } else if (source == sliderOpacity) {
            double value = sliderOpacity.getValue() / RES;

            currentActor.getLookupTable().setOpacityForAllColors(value);// .GetProperty().SetOpacity(value);
        }

        renderLake.display();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioEdgesOn) {
            currentActor.GetProperty().EdgeVisibilityOn();
        } else if (source == radioEdgesOff) {
            currentActor.GetProperty().EdgeVisibilityOff();
        } else if (source == radioRepPoints) {
            currentActor.GetProperty().SetRepresentationToPoints();
            representAsPoints();
        } else if (source == radioRepWireframe) {
            currentActor.GetProperty().SetRepresentationToWireframe();
            representAsWireframe();
        } else if (source == radioRepSurface) {
            currentActor.GetProperty().SetRepresentationToSurface();
            representAsSurface();
        }

        renderLake.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
    }
}
