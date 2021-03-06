import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Defines controls for transforming and manipulating the boundary actor.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 *
 */
public class BoundaryActorControls extends ActorControls {
    private static final long serialVersionUID = 7770450634313817843L;
    private JRadioButton radioDisplayOn, radioDisplayOff;

    public BoundaryActorControls(RenderLake render, BoundaryActor actor) {
        super(render, actor);

        JPanel panelDisplay = makeDisplayPanel();

        super.add(panelDisplay, 0);

        update();
    }

    /**
     * Makes the panel which has the controls for whether or not the actor is
     * currently being displayed.
     * 
     * @return A panel with display controls.
     */
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

    /**
     * Updates the display by enabling or disabling the appropriate components.
     */
    protected void update() {
        if (radioDisplayOn.isSelected()) {
            paneType.setEnabled(true);
            radioRepWireframe.setEnabled(true);
            radioRepSolid.setEnabled(true);
            radioRepPoints.setEnabled(true);

            if (radioRepWireframe.isSelected()) {
                panelLine.setEnabled(true);
                sliderLineWidth.setEnabled(true);
            } else if (radioRepPoints.isSelected()) {
                panelPoint.setEnabled(true);
                sliderPointSize.setEnabled(true);
            } else {
                panelLine.setEnabled(false);
                sliderLineWidth.setEnabled(false);
            }

            panelOpacity.setEnabled(true);
            sliderOpacity.setEnabled(true);
        } else {
            paneType.setEnabled(false);
            radioRepWireframe.setEnabled(false);
            radioRepSolid.setEnabled(false);
            radioRepPoints.setEnabled(false);

            panelPoint.setEnabled(false);
            sliderPointSize.setEnabled(false);

            panelLine.setEnabled(false);
            sliderLineWidth.setEnabled(false);

            panelOpacity.setEnabled(false);
            sliderOpacity.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == radioDisplayOn) {
            renderLake.drawBoundaryOn();
            panelLine.setEnabled(false);
        } else if (source == radioDisplayOff) {
            renderLake.drawBoundaryOff();
        }

        super.actionPerformed(e);

        update();
    }
}
