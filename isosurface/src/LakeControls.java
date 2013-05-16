import gui.RangeSlider;

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
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LakeControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    protected final double RES = 100;

    private JRadioButton radioActorFull, radioActorSingleContour,
            radioActorDoubleContour;
    private JCheckBox checkColorReverse, checkDepthPeel;
    private JSlider sliderVertExag, sliderBgR, sliderBgG, sliderBgB;
    private JButton buttonColorChange, buttonSnapToData;
    private JFileChooser fileChooser;
    private RangeSlider rangeSlider;
    private JPanel panelIso;
    private JTabbedPane tabbedPane;

    private RenderLake render;
    
    private FullActorControls panelFull;
    private BoundaryActorControls panelBoundary;
    private IsosurfaceActorControls panelIsosurfaceA, panelIsosurfaceB;    

    public LakeControls(RenderLake render) {
        super();

        this.render = render;
        
        tabbedPane = new JTabbedPane();

        JPanel panelMain = makePanel();
        tabbedPane.addTab("Main", panelMain);        
        
        panelFull = new FullActorControls(render, render.getFullActor());        
        tabbedPane.addTab("Full Surface", panelFull);        
        
        panelIso = new JPanel(new GridLayout(1, 2));      
        panelIsosurfaceA = new IsosurfaceActorControls(render,
                render.getIsosurfaceActorA(), "Isosurface A");
        panelIsosurfaceB = new IsosurfaceActorControls(render,                
                render.getIsosurfaceActorB(), "Isosurface B");
        panelIso.add(panelIsosurfaceA);
        panelIso.add(panelIsosurfaceB);
        tabbedPane.addTab("Isosurfaces", panelIso);     
        
        panelBoundary = new BoundaryActorControls(render, render.getBoundaryActor());        
        tabbedPane.addTab("Boundary", panelBoundary);
        
        tabbedPane.setEnabledAt(1, true);
        tabbedPane.setEnabledAt(2, false);
        
        add(tabbedPane);
    }

    private JPanel makeBackgroundColorPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        
        int min = 0;
        int max = (int) (1.0 * RES);
        
        int initR = (int) (render.getBackgroundRed() * RES);
        int initG = (int) (render.getBackgroundGreen() * RES);
        int initB = (int) (render.getBackgroundBlue() * RES);
        
        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(min, new JLabel("0"));
        dict.put((min + max) / 2, new JLabel("0.5"));
        dict.put(max, new JLabel("1.0"));
                
        sliderBgR = new JSlider(JSlider.HORIZONTAL, min, max, initR);
        sliderBgR.setLabelTable(dict);
        sliderBgR.setMajorTickSpacing(25);
        sliderBgR.setMinorTickSpacing(5);
        sliderBgR.setPaintLabels(true);
        sliderBgR.setPaintTicks(true);
        
        sliderBgG = new JSlider(JSlider.HORIZONTAL, min, max, initG);
        sliderBgG.setLabelTable(dict);
        sliderBgG.setMajorTickSpacing(25);
        sliderBgG.setMinorTickSpacing(5);
        sliderBgG.setPaintLabels(true);
        sliderBgG.setPaintTicks(true);
        
        sliderBgB = new JSlider(JSlider.HORIZONTAL, min, max, initB);
        sliderBgB.setLabelTable(dict);
        sliderBgB.setMajorTickSpacing(25);
        sliderBgB.setMinorTickSpacing(5);
        sliderBgB.setPaintLabels(true);
        sliderBgB.setPaintTicks(true);
        
        JPanel panelR = new JPanel(new GridLayout(1, 1));
        JPanel panelG = new JPanel(new GridLayout(1, 1));
        JPanel panelB = new JPanel(new GridLayout(1, 1));
        
        panelR.add(sliderBgR);
        panelG.add(sliderBgG);
        panelB.add(sliderBgB);         

        panelR.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Red"));
        panelG.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Green"));
        panelB.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Blue"));
        
        panel.add(panelR); 
        panel.add(panelG); 
        panel.add(panelB);         

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Background Color"));

        sliderBgR.addChangeListener(this);
        sliderBgG.addChangeListener(this);
        sliderBgB.addChangeListener(this);   
        
        return panel;
    }
    
    private JPanel makePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new GridLayout(5, 1));
        JPanel rightPanel = new JPanel(new GridLayout(1, 1));
        
        JPanel actorPanel = makeActorPanel();
        JPanel depthPanel = makeVerticalExaggeration();
        JPanel colorPanel = makeColorPanel();
        JPanel valueRangePanel = makeValueRangePanel();
        JPanel depthPeelPanel = makeDepthPeelPanel();
                
        leftPanel.add(actorPanel);
        leftPanel.add(depthPanel);
        leftPanel.add(colorPanel);
        leftPanel.add(valueRangePanel);
        leftPanel.add(depthPeelPanel);
        
        JPanel bgColorPanel = makeBackgroundColorPanel();

        rightPanel.add(bgColorPanel);
        
        panel.add(leftPanel);
        panel.add(rightPanel);

        
        return panel;
    }

    private JPanel makeValueRangePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        
        int min = 0;
        int max = (int) (38.0 * RES);
        int initMin = (int) (render.getScalarMin() * RES);
        int initMax = (int) (render.getScalarMax() * RES);

        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(min, new JLabel("0°C"));
        dict.put((min + max) / 2, new JLabel("19°C"));
        dict.put(max, new JLabel("38°C"));
        
        rangeSlider = new RangeSlider(min, max);   
        rangeSlider.setValue(initMin);
        rangeSlider.setUpperValue(initMax);
        rangeSlider.setLabelTable(dict);
        rangeSlider.setMajorTickSpacing((min + max) / 4);
        rangeSlider.setMinorTickSpacing((min + max) / 8);
        rangeSlider.setPaintLabels(true);
        rangeSlider.setPaintTicks(true);
        
        buttonSnapToData = new JButton("Snap to Data");
        
        panel.add(rangeSlider);  
        panel.add(buttonSnapToData);         

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Value Range for Color Mapping"));
        
        rangeSlider.addChangeListener(this);
        buttonSnapToData.addActionListener(this);
        
        return panel;
    }
    
    private JPanel makeDepthPeelPanel() {
        JPanel panel = new JPanel();
        
        checkDepthPeel = new JCheckBox("Enabled");
        checkDepthPeel.setSelected(false);   

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Depth Peeling"));

        panel.add(checkDepthPeel);
        
        checkDepthPeel.addItemListener(this);
        
        return panel;
    }
    
    private JPanel makeColorPanel() {
        JPanel panel = new JPanel();

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

    private JPanel makeVerticalExaggeration() {
        JPanel panel = new JPanel(new GridLayout(1, 1));

        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(1, new JLabel("1"));
        dict.put(250, new JLabel("250"));
        dict.put(500, new JLabel("500"));
        dict.put(750, new JLabel("750"));
        dict.put(1000, new JLabel("1000"));

        sliderVertExag = new JSlider(JSlider.HORIZONTAL, 1, 1000, 200);
        sliderVertExag.setLabelTable(dict);
        sliderVertExag.setMajorTickSpacing(125);
        sliderVertExag.setMinorTickSpacing(25);
        sliderVertExag.setPaintLabels(true);
        sliderVertExag.setPaintTicks(true);

        panel.add(sliderVertExag);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Vertical Exaggeration"));

        sliderVertExag.addChangeListener(this);

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
        } else if (source == buttonSnapToData) {
            rangeSlider.setValue(rangeSlider.getMinimum());
            rangeSlider.setUpperValue(rangeSlider.getMaximum());
            
            int min = (int) (render.getDataScalarMin() * RES);
            int max = (int) (render.getDataScalarMax() * RES);
            
            rangeSlider.setValue(min);
            rangeSlider.setUpperValue(max);
        }

        render.display();
    }

    private void renderFull() {
        render.renderFull();

        tabbedPane.setEnabledAt(1, true);
        tabbedPane.setEnabledAt(2, false);
    }

    private void renderSingleContour() {
        render.renderSingleContour();

        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, true);
        panelIsosurfaceB.setVisible(false);
    }

    private void renderDoubleContour() {
        render.renderDoubleContour();

        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, true);
        panelIsosurfaceB.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == sliderVertExag) {
            render.changeZScale(sliderVertExag.getValue());

            resetActors();            
        } else if (source == sliderBgR) {
            double value = sliderBgR.getValue() / RES;
            
            render.setBackgroundRed(value);
        } else if (source == sliderBgG) {
            double value = sliderBgG.getValue() / RES;
            
            render.setBackgroundGreen(value);
        } else if (source == sliderBgB) {
            double value = sliderBgB.getValue() / RES;
            
            render.setBackgroundBlue(value);
        } else if (source == rangeSlider) {
            double minVal = rangeSlider.getValue() / RES;
            double maxVal = rangeSlider.getUpperValue() / RES;
            
            render.setScalarMin(minVal);
            render.setScalarMax(maxVal);
            
            render.changeColor();
            resetActors();
        }

        render.display();
    }

    private void resetActors() {
        panelFull.setCurrentActor(render.getFullActor());
        panelIsosurfaceA.setCurrentActor(render.getIsosurfaceActorA());
        panelIsosurfaceB.setCurrentActor(render.getIsosurfaceActorB());
        panelBoundary.setCurrentActor(render.getBoundaryActor());
        
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
