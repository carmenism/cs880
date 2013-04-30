package head;

public class Interval {
    protected double min;
    protected double max;
    
    public Interval(double min, double max) {
        this.min = min;
        this.max = max;
        
        assert(min <= max);
        //assert(min >= 0.0);
        //assert(max <= 1.0);
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
        
        assert(min <= max);
        //assert(min >= 0.0);
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;

        assert(min <= max);
        //assert(max <= 1.0);
    }
    
    public double[] toArray() {
        double [] array = new double[2];
        
        array[0] = min;
        array[1] = max;
        
        return array;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("[");
        sb.append(min);
        sb.append(", ");
        sb.append(max);
        sb.append("]");
        
        return sb.toString();
    }
}
