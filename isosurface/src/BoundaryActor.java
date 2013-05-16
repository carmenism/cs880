import vtk.vtkStructuredGrid;


public class BoundaryActor extends FullActor {

    public BoundaryActor(vtkStructuredGrid sGrid, LookupTable lut, double scalarMin, double scalarMax) {
        super(sGrid, lut, scalarMin, scalarMax);
        
        super.GetProperty().SetRepresentationToWireframe(); 
    }

}
