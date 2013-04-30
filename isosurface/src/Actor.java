import vtk.vtkActor;
import vtk.vtkContourFilter;
import vtk.vtkLookupTable;

public class Actor extends vtkActor {
    private vtkContourFilter contourFilter;
    private vtkLookupTable lookupTable;
    
    public Actor(vtkContourFilter cf, vtkLookupTable lut) {
        super();
        
        contourFilter = cf;
        lookupTable = lut;
    }

    public vtkContourFilter getContourFilter() {
        return contourFilter;
    }

    public vtkLookupTable getLookupTable() {
        return lookupTable;
    }
}
