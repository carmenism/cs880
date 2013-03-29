import vtk.*;

public class ContourFilter extends vtkContourFilter {
    private int numberContours;
    private Interval range;

    public ContourFilter(vtkStructuredPointsReader reader, int numberContours, Interval range) {
        super();
        
        this.numberContours = numberContours;
        this.range = range;
        
        super.SetInput(reader.GetOutput());
        super.GenerateValues(numberContours, range.toArray());        
    }

    /*public ContourFilter(vtkStructuredPointsReader reader, double value) {
        super();

        this.numberContours = 1;
        this.range = range;

        super.SetInput(reader.GetOutput());
        super.GenerateValues(numberContours, range.toArray());    
    }*/

    public void display() {
        super.GenerateValues(numberContours, range.toArray());    
    }

    public int getNumberContours() {
        return numberContours;
    }

    public void setNumberContours(int numberContours) {
        this.numberContours = numberContours;
    }

    public Interval getRange() {
        return range;
    }

    public void setRange(Interval range) {
        this.range = range;
    }
}
