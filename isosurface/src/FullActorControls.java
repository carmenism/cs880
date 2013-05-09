import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class FullActorControls extends ActorControls {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected JRadioButton radioEdgesOn, radioEdgesOff;
            
    protected final double RES = 100;
    
    protected JPanel panelEdges;
    
    public FullActorControls(RenderLake render, Actor actor, String title) {
        super(render, actor, title);
        
        panelEdges = makeEdgesRadioPanel();
        
        super.add(panelEdges, 1);
    }
        
    public void updateActor() {
        if (radioEdgesOn.isSelected()) {
            currentActor.GetProperty().EdgeVisibilityOn();
        } else {
            currentActor.GetProperty().EdgeVisibilityOff();            
        }
        
        super.updateActor();
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
    
    protected void representAsPoints() {
        super.representAsPoints();
        
        if (radioEdgesOn != null && radioEdgesOff != null) {
            radioEdgesOn.setEnabled(false);
            radioEdgesOff.setEnabled(false);
        }
    }
    
    protected void representAsWireframe() {
        super.representAsWireframe();

        if (radioEdgesOn != null && radioEdgesOff != null) {
            radioEdgesOn.setEnabled(false);
            radioEdgesOff.setEnabled(false);
        }
    }
    
    protected void representAsSurface() {
        super.representAsSurface();

        if (radioEdgesOn != null && radioEdgesOff != null) {
            radioEdgesOn.setEnabled(true);
            radioEdgesOff.setEnabled(true);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
       
        if (source == radioEdgesOn) {
            currentActor.GetProperty().EdgeVisibilityOn();
        } else if (source == radioEdgesOff) {
            currentActor.GetProperty().EdgeVisibilityOff();            
        }
        
        super.actionPerformed(e);
    }
}
