import vtk.vtkLookupTable;

public class LookupTable extends vtkLookupTable {
    public void setOpacityForAllColors(double opacity) {
        int numberColors = super.GetNumberOfColors();
        
        for (int i = 0; i < numberColors; i++) {
            double[] color = super.GetTableValue(i);
            
            super.SetTableValue(i, color[0], color[1], color[2], opacity);
        }
    }
    
    public double getOpacityForAllColors() {
        return super.GetTableValue(0)[3];
    }
    
    public void reverseTableColors() {
        int numberColors = super.GetNumberOfColors();
        int end = numberColors - 1;
        
        for (int i = 0; i < numberColors / 2; i++) {
            double[] forward = super.GetTableValue(i);
            double[] backward = super.GetTableValue(end - i);
            
            super.SetTableValue(i, backward);
            super.SetTableValue(end - i, forward);
        }
    }
}
