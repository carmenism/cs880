import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LakeControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private JRadioButton radioActorFull, radioActorSingleContour,
            radioActorDoubleContour;
    private JCheckBox checkColorReverse, checkDepthPeel;

    private JSlider depthScale;

    private RenderLake render;
    private FullActorControls panelFull;
    private FrameActorControls panelFrame;
    private ContourActorControls panelContourA, panelContourB;
    private JButton buttonColorChange;

    private JFileChooser fileChooser;

    public LakeControls(RenderLake render) {
        super();

        this.render = render;

        JPanel panel = makePanel();

        panelFull = new FullActorControls(render, render.getFullActor(),
                "Full Surface");
        panelFrame = new FrameActorControls(render, render.getBoundaryActor(),
                "Lake Boundary");
        panelContourA = new ContourActorControls(render,
                render.getIsosurfaceActorA(), "Isosurface A");
        panelContourB = new ContourActorControls(render,
                render.getIsosurfaceActorB(), "Isosurface B");

        panelContourA.setVisible(false);
        panelContourB.setVisible(false);

        add(panel, 0);
        add(panelFull, 1);
        add(panelContourA, 2);
        add(panelContourB, 3);
        add(panelFrame, 4);
    }

    private JPanel makePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));

        JPanel actorPanel = makeActorPanel();
        JPanel depthPanel = makeDepthScale();
        JPanel colorPanel = makeColorPanel();

        checkDepthPeel = new JCheckBox("Depth Peel");
        checkDepthPeel.setSelected(false);   
        
        panel.add(checkDepthPeel);
        
        panel.add(actorPanel);
        panel.add(depthPanel);
        panel.add(colorPanel);

        checkDepthPeel.addItemListener(this);
        
        return panel;
    }

    private JPanel makeColorPanel() {
        JPanel panel = new JPanel();//new GridLayout(1, 2));

        checkColorReverse = new JCheckBox("Reverse");
        checkColorReverse.setSelected(false);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        buttonColorChange = new JButton("Change Source");
        
        panel.add(buttonColorChange);        
        panel.add(checkColorReverse);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Color Scale"));

        checkColorReverse.addItemListener(this);
        buttonColorChange.addActionListener(this);

        return panel;
    }

    private JPanel makeDepthScale() {
        JPanel panel = new JPanel(new GridLayout(1, 1));

        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(1, new JLabel("1"));
        dict.put(250, new JLabel("250"));
        dict.put(500, new JLabel("500"));
        dict.put(750, new JLabel("750"));
        dict.put(1000, new JLabel("1000"));

        depthScale = new JSlider(JSlider.HORIZONTAL, 1, 1000, 200);
        depthScale.setLabelTable(dict);
        depthScale.setMajorTickSpacing(125);
        depthScale.setMinorTickSpacing(25);
        depthScale.setPaintLabels(true);
        depthScale.setPaintTicks(true);

        panel.add(depthScale);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Vertical Exaggeration"));

        depthScale.addChangeListener(this);

        return panel;
    }

    private JPanel makeActorPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        radioActorFull = new JRadioButton("Full Surface", true);
        radioActorSingleContour = new JRadioButton("One Isosurface", false);
        radioActorDoubleContour = new JRadioButton("Two Isosurfaces", false);

        ButtonGroup buttonGroupActor = new ButtonGroup();
        buttonGroupActor.add(radioActorFull);
        buttonGroupActor.add(radioActorSingleContour);
        buttonGroupActor.add(radioActorDoubleContour);

        panel.add(radioActorFull);
        panel.add(radioActorSingleContour);
        panel.add(radioActorDoubleContour);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Current Object"));

        radioActorFull.addActionListener(this);
        radioActorSingleContour.addActionListener(this);
        radioActorDoubleContour.addActionListener(this);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioActorFull) {
            renderFull();
        } else if (source == radioActorSingleContour) {
            renderSingleContour();
        } else if (source == radioActorDoubleContour) {
            renderDoubleContour();
        } else if (source == buttonColorChange) {
            int returnVal = fileChooser.showOpenDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                render.setColorTable(file);
                render.changeColor();

                resetActors();
            }
        }

        render.display();
    }

    private void renderFull() {
        render.renderFull();

        panelFull.setVisible(true);
        panelContourA.setVisible(false);
        panelContourB.setVisible(false);
    }

    private void renderSingleContour() {
        render.renderSingleContour();

        panelFull.setVisible(false);
        panelContourA.setVisible(true);
        panelContourB.setVisible(false);
    }

    private void renderDoubleContour() {
        render.renderDoubleContour();

        panelFull.setVisible(false);
        panelContourA.setVisible(true);
        panelContourB.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == depthScale) {
            render.changeZScale(depthScale.getValue());

            resetActors();
            
        }

        render.display();
    }

    private void resetActors() {
        panelFull.setCurrentActor(render.getFullActor());
        panelContourA.setCurrentActor(render.getIsosurfaceActorA());
        panelContourB.setCurrentActor(render.getIsosurfaceActorB());
        panelFrame.setCurrentActor(render.getBoundaryActor());
        
        if (radioActorFull.isSelected()) {
            renderFull();
        } else if (radioActorSingleContour.isSelected()) {
            renderSingleContour();
        } else {
            renderDoubleContour();
        }
        
        if (checkColorReverse.isSelected()) {
            render.reverseColor();
        }
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == checkColorReverse) {
            render.reverseColor();
        } else if (source == checkDepthPeel) {
            if (checkDepthPeel.isSelected()) {
                render.depthPeelOn();
            } else {
                render.depthPeelOff();
            }
        }

        render.display();
    }
}
