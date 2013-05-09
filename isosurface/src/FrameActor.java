import vtk.vtkLookupTable;
import vtk.vtkStructuredGrid;


public class FrameActor extends FullActor {

    public FrameActor(vtkStructuredGrid sGrid, vtkLookupTable lut, double scalarMin, double scalarMax) {
        super(sGrid, lut, scalarMin, scalarMax);
        
        super.GetProperty().SetRepresentationToWireframe();
        super.GetProperty().SetOpacity(0.1);  
    }

}
