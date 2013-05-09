import vtk.vtkActor;
import vtk.vtkLookupTable;

public class Actor extends vtkActor {
    private vtkLookupTable lookupTable;
    
    public Actor(vtkLookupTable lut) {
        super();
        
        lookupTable = lut;
    }

    public vtkLookupTable getLookupTable() {
        return lookupTable;
    }
}
