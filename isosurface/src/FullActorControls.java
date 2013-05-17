import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Defines controls for transforming and manipulating the full surface actor.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class FullActorControls extends ActorControls {
    private static final long serialVersionUID = 1L;

    private JRadioButton radioEdgesOn, radioEdgesOff;
    private JPanel panelEdges;

    public FullActorControls(RenderLake render, Actor actor) {
        super(render, actor);

        panelEdges = makeEdgesRadioPanel();

        super.add(panelEdges, 1);
    }

    /**
     * Makes the panel that turns edges on and off.
     * 
     * @return A panel with controls for the edges feature.
     */
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

    @Override
    public void updateActor() {
        if (radioEdgesOn.isSelected()) {
            currentActor.GetProperty().EdgeVisibilityOn();
        } else {
            currentActor.GetProperty().EdgeVisibilityOff();
        }

        super.updateActor();
    }

    @Override
    protected void representAsPoints() {
        super.representAsPoints();

        if (radioEdgesOn != null && radioEdgesOff != null) {
            radioEdgesOn.setEnabled(false);
            radioEdgesOff.setEnabled(false);
            panelEdges.setEnabled(false);
        }
    }

    @Override
    protected void representAsWireframe() {
        super.representAsWireframe();

        if (radioEdgesOn != null && radioEdgesOff != null) {
            radioEdgesOn.setEnabled(false);
            radioEdgesOff.setEnabled(false);
            panelEdges.setEnabled(false);
        }
    }

    @Override
    protected void representAsSolid() {
        super.representAsSolid();

        if (radioEdgesOn != null && radioEdgesOff != null) {
            radioEdgesOn.setEnabled(true);
            radioEdgesOff.setEnabled(true);
            panelEdges.setEnabled(true);
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
