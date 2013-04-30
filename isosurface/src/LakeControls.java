import gui.TableLayout;

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

    private static final int ROW_H = 30;

    private JRadioButton radioActorFull, radioActorSingleContour, radioActorDoubleContour;
    private JRadioButton radioBfCullingOn, radioBfCullingOff;
    private JRadioButton radioFfCullingOn, radioFfCullingOff;

    private RenderLake render;
    private Actor currentActor;

    private LakePropertyControls panelFull, panelContourA, panelContourB;
            
    public LakeControls(RenderLake render) {
        super();

        this.render = render;
        this.currentActor = render.getFullActor();
                
        JPanel actorPanel = makeActorPanel();
        
        panelFull = new LakePropertyControls(render, currentActor, "Full Lake");
        panelContourA = new LakePropertyControls(render, render.getContourSelectionActorA(), "Contour Level A");
        panelContourB = new LakePropertyControls(render, render.getContourSelectionActorB(), "Contour Level B");
        
        panelContourA.setVisible(false);
        panelContourB.setVisible(false);
        
        JPanel cullingPanel = makeCullingPanel();

        add(actorPanel, "0, 0");
        add(panelFull, "0, 1");
        add(panelContourA, "0, 2");
        add(panelContourB, "0, 3");
        add(cullingPanel, "0, 4");
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
    
    private JPanel makeCullingPanel() {
        double[][] size = { { 0.50, 0.50 }, { 2 * ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        JPanel frontfacePanel = makeFrontfaceCullingRadioPanel();
        JPanel backfacePanel = makeBackfaceCullingRadioPanel();

        panel.add(frontfacePanel, "0, 0");
        panel.add(backfacePanel, "1, 0");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Culling"));

        return panel;
    }

    private JPanel makeFrontfaceCullingRadioPanel() {
        double[][] size = { { 0.50, 0.50 }, { ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        radioFfCullingOn = new JRadioButton("On", false);
        radioFfCullingOff = new JRadioButton("Off", true);

        ButtonGroup buttonGroupBfCulling = new ButtonGroup();
        buttonGroupBfCulling.add(radioFfCullingOn);
        buttonGroupBfCulling.add(radioFfCullingOff);

        panel.add(radioFfCullingOn, "0, 0");
        panel.add(radioFfCullingOff, "1, 0");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Frontface"));

        radioFfCullingOn.addActionListener(this);
        radioFfCullingOff.addActionListener(this);

        return panel;
    }

    private JPanel makeBackfaceCullingRadioPanel() {
        double[][] size = { { 0.50, 0.50 }, { ROW_H } };

        JPanel panel = new JPanel(new TableLayout(size));

        radioBfCullingOn = new JRadioButton("On", false);
        radioBfCullingOff = new JRadioButton("Off", true);

        ButtonGroup buttonGroupBfCulling = new ButtonGroup();
        buttonGroupBfCulling.add(radioBfCullingOn);
        buttonGroupBfCulling.add(radioBfCullingOff);

        panel.add(radioBfCullingOn, "0, 0");
        panel.add(radioBfCullingOff, "1, 0");

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Backface"));

        radioBfCullingOn.addActionListener(this);
        radioBfCullingOff.addActionListener(this);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioBfCullingOn) {
            currentActor.GetProperty().BackfaceCullingOn();
        } else if (source == radioBfCullingOff) {
            currentActor.GetProperty().BackfaceCullingOff();
        } else if (source == radioFfCullingOn) {
            currentActor.GetProperty().FrontfaceCullingOn();
        } else if (source == radioFfCullingOff) {
            currentActor.GetProperty().FrontfaceCullingOff();
        } else if (source == radioActorFull) {          
            render.renderFull();
            
            currentActor = render.getFullActor();
            
            panelFull.setVisible(true);
            panelContourA.setVisible(false);
            panelContourB.setVisible(false);
        } else if (source == radioActorSingleContour) {
            render.renderSingleContour();
            
            currentActor = render.getContourSelectionActorA();

            panelFull.setVisible(false);
            panelContourA.setVisible(true);
            panelContourB.setVisible(false);
        } else if (source == radioActorDoubleContour) {
            render.renderDoubleContour();
            
            currentActor = render.getContourSelectionActorA();

            panelFull.setVisible(false);
            panelContourA.setVisible(true);
            panelContourB.setVisible(true);        
        }
        
        render.display();
    }

    @Override
    public void stateChanged(ChangeEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
    }
}
