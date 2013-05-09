import vtk.vtkStructuredGrid;


public class FrameActor extends FullActor {

    public FrameActor(vtkStructuredGrid sGrid, LookupTable lut, double scalarMin, double scalarMax) {
        super(sGrid, lut, scalarMin, scalarMax);
        
        super.GetProperty().SetRepresentationToWireframe(); 
    }

}
