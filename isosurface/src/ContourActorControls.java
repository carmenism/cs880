import java.awt.GridLayout;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;


public class ContourActorControls extends FullActorControls {
    /**
     * 
     */
    private static final long serialVersionUID = -4360527297291555077L;
    
    private JSlider sliderValue;
    
    protected ContourActor currentActor;

    public ContourActorControls(RenderLake render, ContourActor actor, String title) {
        super(render, actor, title);
        
        this.currentActor = actor;
        
        JPanel valuePanel = makeValuePanel();
        
        super.add(valuePanel, -1);
    }
    
    public void updateActor() {
        if (sliderValue != null) {
            double value = sliderValue.getValue() / RES;            
            currentActor.getContourFilter().SetValue(0, value);
        }
        
        super.updateActor();
    }
    
    private JPanel makeValuePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        int min = (int) (renderLake.getScalarMin() * RES);
        int max = (int) (renderLake.getScalarMax() * RES);
        int init = (int) (currentActor.getContourFilter().GetValue(0) * RES);
                
        double mid = (renderLake.getScalarMin() + renderLake.getScalarMax()) / 2.0; 
        double minMid = (renderLake.getScalarMin() + mid) / 2.0;
        double midMax = (mid + renderLake.getScalarMax()) / 2.0;
        
        int midIndex = (min + max) / 2;
        int minMidIndex = (min + midIndex) / 2;
        int midMaxIndex = (midIndex + max) / 2;
        
        String sMin = String.format("%.2f", renderLake.getScalarMin());
        String sMinMid = String.format("%.2f", minMid);
        String sMid = String.format("%.2f", mid);
        String sMidMax = String.format("%.2f", midMax);
        String sMax = String.format("%.2f", renderLake.getScalarMax());
        
        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(min, new JLabel(sMin));
        dict.put(minMidIndex, new JLabel(sMinMid));
        dict.put(midIndex, new JLabel(sMid));
        dict.put(midMaxIndex, new JLabel(sMidMax));
        dict.put(max, new JLabel(sMax));
        
        sliderValue = new JSlider(JSlider.HORIZONTAL, min, max, init);
        sliderValue.setLabelTable(dict);
        sliderValue.setMajorTickSpacing((max - min) / 4);
        sliderValue.setMinorTickSpacing((max - min) / 16);
        sliderValue.setPaintLabels(true);
        sliderValue.setPaintTicks(true);

        panel.add(sliderValue);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Contour Value"));

        sliderValue.addChangeListener(this);

        return panel;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (sliderValue != null && source == sliderValue) {
            double value = sliderValue.getValue() / RES;
            
            currentActor.getContourFilter().SetValue(0, value);
        }

        super.stateChanged(e);
        
        renderLake.display();
    }
}
