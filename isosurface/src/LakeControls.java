import gui.TableLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vtk.vtkActor;
import vtk.vtkLookupTable;
import vtk.vtkMapper;
import vtk.vtkProperty;
import vtk.vtkScalarsToColors;

public class LakeControls extends JPanel implements ItemListener,
        ChangeListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private static final int ROW_H = 30;

    private JRadioButton radioActorFull, radioActorSingleContour, radioActorDoubleContour;
    private JRadioButton radioBfCullingOn, radioBfCullingOff;
    private JRadioButton radioFfCullingOn, radioFfCullingOff;

    private JSlider sliderNumberColors;

    private RenderLake render;
    private vtkActor currentActor, otherActor;

    private LakePropertyControls primaryActorPanel, secondaryActorPanel;
    
    private boolean displayDouble = false;
    
    private int RES = 100;
    
    public LakeControls(RenderLake render) {
        super();

        this.render = render;
        this.currentActor = render.getFullActor();
        this.otherActor = render.getContourSelectionActorB();

        int min = (int) (render.getScalarMin() * RES);
        int max = (int) (render.getScalarMax() * RES);
                
        JPanel actorPanel = makeActorPanel();
        primaryActorPanel = new LakePropertyControls(render, currentActor, "Primary Actor");
        secondaryActorPanel = new LakePropertyControls(render, otherActor, "Secondary Actor");
        
        JPanel cullingPanel = makeCullingPanel();
        sliderNumberColors = new JSlider(JSlider.HORIZONTAL, min, max, (min + max) / 2);

        sliderNumberColors.addChangeListener(this);

        add(actorPanel, "0, 0");
        add(primaryActorPanel, "0, 1");
        add(secondaryActorPanel, "0, 2");
        add(cullingPanel, "0, 3");
        add(sliderNumberColors, "0, 4");
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
/*
    private JPanel makeRepresentationPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));

        JPanel typePanel = makeRepresentationRadioPanel();
        JPanel edgesPanel = makeEdgesRadioPanel();
        JPanel pointSizePanel = makePointSizePanel();
        JPanel lineWidthPanel = makeLineWidthPanel();

        panel.add(typePanel, "0, 0");
        panel.add(edgesPanel, "0, 1");
        panel.add(pointSizePanel, "0, 2");
        panel.add(lineWidthPanel, "0, 3");
        
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Representation"));

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

    private JPanel makeRepresentationRadioPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));

        radioRepPoints = new JRadioButton("Points", false);
        radioRepWireframe = new JRadioButton("Wireframe", false);
        radioRepSurface = new JRadioButton("Surface", true);

        ButtonGroup buttonGroupDisplay = new ButtonGroup();
        buttonGroupDisplay.add(radioRepPoints);
        buttonGroupDisplay.add(radioRepWireframe);
        buttonGroupDisplay.add(radioRepSurface);

        panel.add(radioRepPoints);
        panel.add(radioRepWireframe);
        panel.add(radioRepSurface);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Type"));

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
    }*/

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
            displayDouble = false;            
            render.renderFull();
            
            currentActor = render.getFullActor();
            
            primaryActorPanel.setCurrentActor(currentActor);
        } else if (source == radioActorSingleContour) {
            displayDouble = false;
            render.renderSingleContour();
            
            currentActor = render.getContourSelectionActorA();
            
            primaryActorPanel.setCurrentActor(currentActor);
        } else if (source == radioActorDoubleContour) {
            displayDouble = true;
            render.renderDoubleContour();
            
            currentActor = render.getContourSelectionActorA();
            
            primaryActorPanel.setCurrentActor(currentActor);            
        }
        
        render.display();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == sliderNumberColors) {
            double val = ((double) sliderNumberColors.getValue()) / RES;
            
            render.getIso().SetValue(0, val);
        }

        render.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub

    }
    
    /*private void updateActorToControls(vtkActor actor) {
        if (radioRepPoints.isSelected()) {
            actor.GetProperty().SetRepresentationToPoints();
        } else if (radioRepWireframe.isSelected()) {
            actor.GetProperty().SetRepresentationToWireframe();
        } else {
            actor.GetProperty().SetRepresentationToSurface();
        }
        
        if (radioBfCullingOn.isSelected()) {
            actor.GetProperty().BackfaceCullingOn();
        } else {
            actor.GetProperty().BackfaceCullingOff();
        }
        
        if (radioFfCullingOn.isSelected()) {
            actor.GetProperty().FrontfaceCullingOn();
        } else {
            actor.GetProperty().FrontfaceCullingOff();
        }
        
        if (radioEdgesOn.isSelected()) {
            actor.GetProperty().EdgeVisibilityOn();
        } else {
            actor.GetProperty().EdgeVisibilityOff();
        }
        
        actor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        actor.GetProperty().SetPointSize(sliderPointSize.getValue());
    }*/
}
