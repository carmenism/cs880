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

public class LakeControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private JRadioButton radioActorFull, radioActorSingleContour, radioActorDoubleContour;
    
    private JSlider depthScale;

    private RenderLake render;
    private LakePropertyControls panelFull, panelContourA, panelContourB;
            
    public LakeControls(RenderLake render) {
        super();

        this.render = render;
                
        JPanel actorPanel = makeActorPanel();
        JPanel depthPanel = makeDepthScale();
        
        panelFull = new LakePropertyControls(render, render.getFullActor(), "Full Lake");
        panelContourA = new LakePropertyControls(render, render.getContourSelectionActorA(), "Contour Level A");
        panelContourB = new LakePropertyControls(render, render.getContourSelectionActorB(), "Contour Level B");
        
        panelContourA.setVisible(false);
        panelContourB.setVisible(false);
        
        add(actorPanel, "0, 0");
        add(depthPanel, "0, 1");
        add(panelFull, "0, 2");
        add(panelContourA, "0, 3");
        add(panelContourB, "0, 4");
    }

    private JPanel makeDepthScale() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        depthScale =  new JSlider(JSlider.HORIZONTAL, 1, 500, 200);
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

        radioActorFull = new JRadioButton("Full Isosurface", true);
        radioActorSingleContour = new JRadioButton("Single Contour", false);
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
