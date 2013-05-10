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

public class FrameActorControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 7770450634313817843L;
    protected final double RES = 100;
    private JRadioButton radioDisplayOn, radioDisplayOff;
    protected JRadioButton radioRepWireframe, radioRepSolid;
    protected JSlider sliderLineWidth, sliderOpacity;
    protected Actor currentActor;
    protected RenderLake renderLake;
    protected JPanel paneType, panelDisplay, panelLine, panelOpacity;

    public FrameActorControls(RenderLake render, FrameActor actor, String title) {
        super(new GridLayout(4, 1));

        currentActor = actor;
        renderLake = render;

        panelDisplay = makeDisplayPanel();
        panelLine = makeLineWidthPanel();
        panelOpacity = makeOpacityPanel();
        paneType = makeRepresentationRadioPanel();

        super.add(panelDisplay, 0);
        super.add(paneType, 1);
        super.add(panelLine, 2);
        super.add(panelOpacity, 3);

        paneType.setEnabled(false);
        panelLine.setEnabled(false);
        panelOpacity.setEnabled(false);

        super.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title));
        
        update();
    }

    protected void update() {
        if (radioDisplayOn.isSelected()) {
            paneType.setEnabled(true);
            radioRepWireframe.setEnabled(true);
            radioRepSolid.setEnabled(true);

            if (radioRepWireframe.isSelected()) {
                panelLine.setEnabled(true);
                sliderLineWidth.setEnabled(true);
            } else {
                panelLine.setEnabled(false);
                sliderLineWidth.setEnabled(false);
            }

            panelOpacity.setEnabled(true);
            sliderOpacity.setEnabled(true);
        } else {
            paneType.setEnabled(false);
            radioRepWireframe.setEnabled(false);
            radioRepSolid.setEnabled(false);

            panelLine.setEnabled(false);
            sliderLineWidth.setEnabled(false);

            panelOpacity.setEnabled(false);
            sliderOpacity.setEnabled(false);
        }
    }

    public Actor getCurrentActor() {
        return currentActor;
    }

    public void setCurrentActor(Actor actor) {
        currentActor = actor;

        updateActor();
    }

    public void updateActor() {
        if (radioRepWireframe.isSelected()) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (radioRepSolid.isSelected()) {
            currentActor.GetProperty().SetRepresentationToSurface();
        }

        currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());

        double opacity = sliderOpacity.getValue() / RES;
        currentActor.getLookupTable().setOpacityForAllColors(opacity);

        renderLake.display();
    }

    private JPanel makeRepresentationRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));

        String rep = currentActor.GetProperty().GetRepresentationAsString();

        boolean wireframe = false, surface = false;

        if (rep.equals("Surface")) {
            surface = true;
            wireframe = false;
        } else {
            surface = false;
            wireframe = true;
        }

        radioRepWireframe = new JRadioButton("Wireframe", wireframe);
        radioRepSolid = new JRadioButton("Solid", surface);

        ButtonGroup buttonGroupDisplay = new ButtonGroup();
        buttonGroupDisplay.add(radioRepWireframe);
        buttonGroupDisplay.add(radioRepSolid);

        panel.add(radioRepWireframe);
        panel.add(radioRepSolid);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Representation"));

        radioRepWireframe.addActionListener(this);
        radioRepSolid.addActionListener(this);

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
        int init = (int) (currentActor.getLookupTable().getOpacityForAllColors() * RES);

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

    private JPanel makeDisplayPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        radioDisplayOn = new JRadioButton("On", false);
        radioDisplayOff = new JRadioButton("Off", true);

        ButtonGroup buttonGroupActor = new ButtonGroup();
        buttonGroupActor.add(radioDisplayOn);
        buttonGroupActor.add(radioDisplayOff);

        panel.add(radioDisplayOn);
        panel.add(radioDisplayOff);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Display"));

        radioDisplayOn.addActionListener(this);
        radioDisplayOff.addActionListener(this);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioRepWireframe) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (source == radioRepSolid) {
            currentActor.GetProperty().SetRepresentationToSurface();
        } else if (source == radioDisplayOn) {
            renderLake.drawFrameOn();
        } else if (source == radioDisplayOff) {
            renderLake.drawFrameOff();
        }

        update();
        renderLake.display();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == sliderLineWidth) {
            currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        } else if (source == sliderOpacity) {
            double value = sliderOpacity.getValue() / RES;

            currentActor.getLookupTable().setOpacityForAllColors(value);
        }

        renderLake.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
    }
}
