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

    private JRadioButton radioRepPoints, radioRepWireframe, radioRepSurface;
    private JRadioButton radioBfCullingOn, radioBfCullingOff;
    private JRadioButton radioFfCullingOn, radioFfCullingOff;
    private JRadioButton radioEdgesOn, radioEdgesOff;

    private JSlider sliderLineWidth, sliderPointSize;
    private JSlider sliderNumberColors;

    private RenderLake render;
    private vtkActor lake;

    private int RES = 100;
    
    public LakeControls(RenderLake render) {
        super();

        this.render = render;
        this.lake = render.getFullLakeActor();

        JPanel repPanel = makeRepresentationPanel();
        JPanel cullingPanel = makeCullingPanel();
        sliderNumberColors = new JSlider(JSlider.HORIZONTAL, 1, 35, 15);

        sliderNumberColors.addChangeListener(this);
        
        add(repPanel, "0, 0");
        add(cullingPanel, "0, 1");
        add(sliderNumberColors, "0, 2");
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
        JPanel panel = new JPanel(new GridLayout(1, 3));

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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioRepPoints) {
            lake.GetProperty().SetRepresentationToPoints();
        } else if (source == radioRepWireframe) {
            lake.GetProperty().SetRepresentationToWireframe();
        } else if (source == radioRepSurface) {
            lake.GetProperty().SetRepresentationToSurface();
        } else if (source == radioBfCullingOn) {
            lake.GetProperty().BackfaceCullingOn();
        } else if (source == radioBfCullingOff) {
            lake.GetProperty().BackfaceCullingOff();
        } else if (source == radioFfCullingOn) {
            lake.GetProperty().FrontfaceCullingOn();
        } else if (source == radioFfCullingOff) {
            lake.GetProperty().FrontfaceCullingOff();
        } else if (source == radioEdgesOn) {
            lake.GetProperty().EdgeVisibilityOn();
        } else if (source == radioEdgesOff) {
            lake.GetProperty().EdgeVisibilityOff();            
        }
        
        render.display();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source == sliderLineWidth) {
            lake.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        } else if (source == sliderPointSize) {
            lake.GetProperty().SetPointSize(sliderPointSize.getValue());
        } else if (source == sliderNumberColors) {
            double max = sliderNumberColors.getValue();
            
            vtkMapper mapper = lake.GetMapper();
            vtkScalarsToColors colors = mapper.GetLookupTable();
            mapper.SetScalarRange(0, max);
            //colors.SetRange(0, max);
           
        }

        render.display();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub

    }

}
