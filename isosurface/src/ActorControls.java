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

/**
 * Defines a set of general controls for manipulating and transforming an actor
 * in the lake rendering scene.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class ActorControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 7136812677209108849L;
    protected final double RES = 100;

    protected JRadioButton radioRepPoints, radioRepWireframe, radioRepSolid;
    protected JSlider sliderLineWidth, sliderPointSize, sliderOpacity;
    protected JPanel paneType, panelPoint, panelLine, panelOpacity;

    protected RenderLake renderLake;
    protected Actor currentActor;

    /**
     * Creates controls for adjusting and transforming a specific actor.
     * 
     * @param render
     *            A reference back to the RenderLake main class.
     * @param actor
     *            The actor to be controlled by this set of controls.
     */
    public ActorControls(RenderLake render, Actor actor) {
        super(new GridLayout(5, 1));

        currentActor = actor;
        renderLake = render;

        panelPoint = makePointSizePanel();
        panelLine = makeLineWidthPanel();
        panelOpacity = makeOpacityPanel();
        paneType = makeRepresentationRadioPanel();

        super.add(paneType, 0);
        super.add(panelPoint, 1);
        super.add(panelLine, 2);
        super.add(panelOpacity, 3);
    }

    /**
     * Makes the panel that controls the representation type of the actor.
     * 
     * @return A panel that contains representation type controls.
     */
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
        radioRepSolid = new JRadioButton("Solid", solid);

        if (points) {
            representAsPoints();
        } else if (wireframe) {
            representAsWireframe();
        } else {
            representAsSolid();
        }

        ButtonGroup buttonGroupDisplay = new ButtonGroup();
        buttonGroupDisplay.add(radioRepPoints);
        buttonGroupDisplay.add(radioRepWireframe);
        buttonGroupDisplay.add(radioRepSolid);

        panel.add(radioRepPoints);
        panel.add(radioRepWireframe);
        panel.add(radioRepSolid);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Representation"));

        radioRepPoints.addActionListener(this);
        radioRepWireframe.addActionListener(this);
        radioRepSolid.addActionListener(this);

        return panel;
    }

    /**
     * Makes the panel that controls the size of points.
     * 
     * @return A panel that contains point size controls.
     */
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

    /**
     * Makes the panel that controls the width of lines.
     * 
     * @return A panel that contains line width controls.
     */
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

    /**
     * Makes the panel that controls the opacity of the actor.
     * 
     * @return A panel that contains opacity controls.
     */
    private JPanel makeOpacityPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));

        int min = 0;
        int max = (int) (1.0 * RES);
        int init = (int) (currentActor.getLookupTable().getAlphaForAllColors() * RES);

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

    /**
     * Updates controls according to the points rendering mode.
     */
    protected void representAsPoints() {
        panelPoint.setEnabled(true);
        sliderPointSize.setEnabled(true);

        panelLine.setEnabled(false);
        sliderLineWidth.setEnabled(false);
    }

    /**
     * Updates controls according to the wireframe rendering mode.
     */
    protected void representAsWireframe() {
        panelPoint.setEnabled(false);
        sliderPointSize.setEnabled(false);

        panelLine.setEnabled(true);
        sliderLineWidth.setEnabled(true);
    }

    /**
     * Updates controls according to the solid rendering mode.
     */
    protected void representAsSolid() {
        panelPoint.setEnabled(false);
        sliderPointSize.setEnabled(false);

        panelLine.setEnabled(false);
        sliderLineWidth.setEnabled(false);
    }

    /**
     * Updates the actor so it is rendering according to the controls (e.g.,
     * sets the representation type, opacity, etc).
     */
    public void updateActor() {
        if (radioRepPoints.isSelected()) {
            currentActor.GetProperty().SetRepresentationToPoints();
        } else if (radioRepWireframe.isSelected()) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (radioRepSolid.isSelected()) {
            currentActor.GetProperty().SetRepresentationToSurface();
        }

        currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        currentActor.GetProperty().SetPointSize(sliderPointSize.getValue());

        double opacity = sliderOpacity.getValue() / RES;
        currentActor.getLookupTable().setAlphaForAllColors(opacity);

        renderLake.display();
    }

    public Actor getCurrentActor() {
        return currentActor;
    }

    public void setCurrentActor(Actor actor) {
        currentActor = actor;

        updateActor();
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
        } else if (source == radioRepSolid) {
            currentActor.GetProperty().SetRepresentationToSurface();
            representAsSolid();
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

            currentActor.getLookupTable().setAlphaForAllColors(value);
        }

        renderLake.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
    }
}
