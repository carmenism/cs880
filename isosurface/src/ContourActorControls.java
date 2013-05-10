import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ContourActorControls extends JPanel implements ChangeListener {
    /**
     * 
     */
    private static final long serialVersionUID = -4360527297291555077L;
    protected final double RES = 100; 
    
    private JSlider sliderIsovalue;
    
    protected ContourActor currentActor;
    protected JSlider sliderOpacity;
    protected RenderLake renderLake;     
    protected JPanel panelOpacity, panelValue;

    public ContourActorControls(RenderLake render, ContourActor actor, String title) {
        super(new GridLayout(2, 1));
        
        this.currentActor = actor;
        this.renderLake = render;
        
        panelValue = makeValuePanel();
        panelOpacity = makeOpacityPanel();
        
        super.add(panelValue, 0);
        super.add(panelOpacity, 1);
        
        super.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title));
    }
    
    public ContourActor getCurrentContourActor() {
        return currentActor;
    }
    
    public void setCurrentContourActor(ContourActor actor) {
        currentActor = actor;
        
        updateActor();
    }
    
    public void updateActor() {
        if (sliderIsovalue != null) {
            double value = sliderIsovalue.getValue() / RES;            
            currentActor.getContourFilter().SetValue(0, value);
        }
        
        double opacity = sliderOpacity.getValue() / RES;            
        currentActor.getLookupTable().setOpacityForAllColors(opacity);
        
        renderLake.display();
    }
    
    private JPanel makeOpacityPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        
        int min = 0;
        int max = (int) (1.0 * RES);
        int init = (int) (currentActor.getLookupTable().getOpacityForAllColors() * RES);//GetProperty().GetOpacity() * RES);
        
        Dictionary<Integer, JLabel> dict = new Hashtable<Integer, JLabel>();
        dict.put(min, new JLabel("0"));
        dict.put((min + max) / 2, new JLabel("0.5"));
        dict.put(max, new JLabel("1.0"));
        
        sliderOpacity = new JSlider(JSlider.HORIZONTAL, min, max, init);
        sliderOpacity.setLabelTable(dict);
        sliderOpacity.setMajorTickSpacing(25);
        sliderOpacity.setMinorTickSpacing(5);
        sliderOpacity.setPaintLabels(true);
        sliderOpacity.setPaintTicks(true);

        panel.add(sliderOpacity);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Opacity"));

        sliderOpacity.addChangeListener(this);

        return panel;
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
        
        sliderIsovalue = new JSlider(JSlider.HORIZONTAL, min, max, init);
        sliderIsovalue.setLabelTable(dict);
        sliderIsovalue.setMajorTickSpacing((max - min) / 4);
        sliderIsovalue.setMinorTickSpacing((max - min) / 16);
        sliderIsovalue.setPaintLabels(true);
        sliderIsovalue.setPaintTicks(true);

        panel.add(sliderIsovalue);        

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Temperature Isovalue"));

        sliderIsovalue.addChangeListener(this);

        return panel;
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (sliderIsovalue != null && source == sliderIsovalue) {
            double value = sliderIsovalue.getValue() / RES;
            
            currentActor.getContourFilter().SetValue(0, value);
        } else if (source == sliderOpacity) {
            double value = sliderOpacity.getValue() / RES;
            
            currentActor.getLookupTable().setOpacityForAllColors(value);//.GetProperty().SetOpacity(value);
        }
        
        renderLake.display();
    }
}
