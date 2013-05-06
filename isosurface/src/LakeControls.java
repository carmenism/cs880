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

public class LakeControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private JRadioButton radioActorFull, radioActorSingleContour, radioActorDoubleContour;
    
    private JSlider depthScale;

    private RenderLake render;
    private LakePropertyControls panelFull;
    private LakeFramePropertyControls panelFrame;
    private LakeContourPropertyControls panelContourA, panelContourB;
            
    public LakeControls(RenderLake render) {
        super();

        this.render = render;                
        
        JPanel panel = makePanel();
        
        panelFull = new LakePropertyControls(render, render.getFullActor(), "Full Lake");
        panelFrame = new LakeFramePropertyControls(render, render.getFrameActor(), "Lake Outline");
        panelContourA = new LakeContourPropertyControls(render, render.getContourSelectionActorA(), "Contour Level A");
        panelContourB = new LakeContourPropertyControls(render, render.getContourSelectionActorB(), "Contour Level B");

        panelContourA.setVisible(false);
        panelContourB.setVisible(false);
        
        add(panel, 0);
        add(panelFrame, 1);
        add(panelFull, 2);
        add(panelContourA, 3);
        add(panelContourB, 4);
    }
    
    private JPanel makePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        
        JPanel actorPanel = makeActorPanel();
        JPanel depthPanel = makeDepthScale();
        
        panel.add(actorPanel);
        panel.add(depthPanel);
        
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
            panelContourA.setCurrentActor(render.getContourSelectionActorA());
            panelContourB.setCurrentActor(render.getContourSelectionActorB());
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
        // TODO Auto-generated method stub
    }
}
