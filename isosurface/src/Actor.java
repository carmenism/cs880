import vtk.vtkActor;

public class Actor extends vtkActor {
    private LookupTable lookupTable;
    
    public Actor(LookupTable lut) {
        super();
        
        lookupTable = lut;
    }

    public LookupTable getLookupTable() {
        return lookupTable;
    }
}
