import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class LakeFramePropertyControls extends LakePropertyControls {
    private JRadioButton radioDisplayOn, radioDisplayOff;
    
    public LakeFramePropertyControls(RenderLake render, Actor actor, String title) {
        super(render, actor, title);
        
        this.remove(panelEdges);
        
        JPanel panelDisplay = makeDisplayPanel();        

        super.add(panelDisplay, 0);
    }
    
    public void updateActor() {        
        if (radioRepPoints.isSelected()) {
            currentActor.GetProperty().SetRepresentationToPoints();
        } else if (radioRepWireframe.isSelected()) {
            currentActor.GetProperty().SetRepresentationToWireframe();
        } else if (radioRepSurface.isSelected()) {
            currentActor.GetProperty().SetRepresentationToSurface();
        }
                
        currentActor.GetProperty().SetLineWidth(sliderLineWidth.getValue());
        currentActor.GetProperty().SetPointSize(sliderPointSize.getValue());
        
        double opacity = sliderOpacity.getValue() / RES;            
        currentActor.GetProperty().SetOpacity(opacity);
        
        renderLake.display();
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
    
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioDisplayOn) {
            renderLake.drawFrameOn();
        } else if (source == radioDisplayOff) {
            renderLake.drawFrameOff();
        }
        
        super.actionPerformed(e);
    }
}
