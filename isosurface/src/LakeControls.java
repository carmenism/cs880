import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LakeControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private JRadioButton radioActorFull, radioActorSingleContour, radioActorDoubleContour;
    private JCheckBox checkColorReverse;
    
    private JSlider depthScale;

    private RenderLake render;
    private FullActorControls panelFull;
    private FrameActorControls panelFrame;
    private ContourActorControls panelContourA, panelContourB;
            
    public LakeControls(RenderLake render) {
        super();

        this.render = render;                
        
        JPanel panel = makePanel();
        
        panelFull = new FullActorControls(render, render.getFullActor(), "Full Surface");
        panelFrame = new FrameActorControls(render, render.getFrameActor(), "Boundary");
        panelContourA = new ContourActorControls(render, render.getContourActorA(), "Isosurface A");
        panelContourB = new ContourActorControls(render, render.getContourActorB(), "Isosurface B");

        panelContourA.setVisible(false);
        panelContourB.setVisible(false);
        
        add(panel, 0);
        add(panelFull, 1);
        add(panelContourA, 2);
        add(panelContourB, 3);
        add(panelFrame, 4);
    }
    
    private JPanel makePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        
        JPanel actorPanel = makeActorPanel();
        JPanel depthPanel = makeDepthScale();
        JPanel colorPanel = makeColorPanel();
        
        panel.add(actorPanel);
        panel.add(depthPanel);
        panel.add(colorPanel);
        
        return panel;
    }
    
    private JPanel makeColorPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        checkColorReverse = new JCheckBox("Reverse Color Scale");
        checkColorReverse.setSelected(false);

        panel.add(checkColorReverse);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Color Scale"));

        checkColorReverse.addItemListener(this);

        return panel;
    }
    
    private JPanel makeDepthScale() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(1, new JLabel("1"));
        dict.put(100, new JLabel("100"));
        dict.put(200, new JLabel("200"));
        dict.put(300, new JLabel("300"));
        dict.put(400, new JLabel("400"));
        dict.put(500, new JLabel("500"));
        
        depthScale =  new JSlider(JSlider.HORIZONTAL, 1, 500, 200);
        depthScale.setLabelTable(dict);
        depthScale.setMajorTickSpacing(100);
        depthScale.setMinorTickSpacing(25);
        depthScale.setPaintLabels(true);
        depthScale.setPaintTicks(true);

        panel.add(depthScale);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Depth Scale"));
        
        depthScale.addChangeListener(this);
        
        return panel;
    }
    
    private JPanel makeActorPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        radioActorFull = new JRadioButton("Full Lake", true);
        radioActorSingleContour = new JRadioButton("One Contour", false);
        radioActorDoubleContour = new JRadioButton("Two Contours", false);
        
        ButtonGroup buttonGroupActor = new ButtonGroup();
        buttonGroupActor.add(radioActorFull);
        buttonGroupActor.add(radioActorSingleContour);
        buttonGroupActor.add(radioActorDoubleContour);

        panel.add(radioActorFull);
        panel.add(radioActorSingleContour);
        panel.add(radioActorDoubleContour);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Current Actor"));

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
        
        if (source == depthScale) {// && !depthScale.getValueIsAdjusting()) {            
            render.changeZScale(depthScale.getValue());
            
            panelFull.setCurrentActor(render.getFullActor());
            panelContourA.setCurrentContourActor(render.getContourActorA());
            panelContourB.setCurrentContourActor(render.getContourActorB());
            panelFrame.setCurrentActor(render.getFrameActor());
            
            if (radioActorFull.isSelected()) {  
                renderFull();
            } else if (radioActorSingleContour.isSelected()) {
                renderSingleContour();
            } else {
                renderDoubleContour();       
            }
        }
        
        render.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        
        if (source == checkColorReverse) {
            panelFull.getCurrentActor().getLookupTable().reverseTableColors();
            panelContourA.getCurrentContourActor().getLookupTable().reverseTableColors();
            panelContourB.getCurrentContourActor().getLookupTable().reverseTableColors();
            panelFrame.getCurrentActor().getLookupTable().reverseTableColors();
        }
       
        render.display();
    }
}
