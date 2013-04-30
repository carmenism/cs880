package head;

import vtk.*;

public class LookupTable extends vtkLookupTable {
    private Interval hue;  
    private Interval sat;  
    private Interval value;
    private Interval alpha;

    public LookupTable(Interval hue, Interval sat, Interval value, Interval alpha) {
        super();
        
        this.hue = hue;
        
        this.sat = sat;
        this.value = value;
        this.alpha = alpha;
        
        super.SetNumberOfColors(256);
        super.SetTableRange(0, 255);
        
        super.SetHueRange(hue.toArray());
        super.SetSaturationRange(sat.toArray());
        super.SetValueRange(value.toArray());
        super.SetAlphaRange(alpha.toArray());
        
        super.Build();
    }

    public void display() {
        super.SetHueRange(hue.toArray());
        super.SetSaturationRange(sat.toArray());
        super.SetValueRange(value.toArray());
        super.SetAlphaRange(alpha.toArray());
    }
    
    public Interval getHue() {
        return hue;
    }

    public void setHue(Interval hue) {
        this.hue = hue;
    }

    public Interval getSat() {
        return sat;
    }

    public void setSat(Interval sat) {
        this.sat = sat;
    }

    public Interval getValue() {
        return value;
    }

    public void setValue(Interval value) {
        this.value = value;
    }

    public Interval getAlpha() {
        return alpha;
    }

    public void setAlpha(Interval alpha) {
        this.alpha = alpha;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("H: ");
        sb.append(hue);
        sb.append("\nS: ");
        sb.append(sat);
        sb.append("\nV: ");
        sb.append(value);
        sb.append("\nA: ");
        sb.append(alpha);
        
        return sb.toString();
    }
}
