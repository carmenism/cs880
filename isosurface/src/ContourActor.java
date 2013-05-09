import vtk.vtkContourFilter;
import vtk.vtkPolyDataMapper;
import vtk.vtkStructuredGrid;

public class ContourActor extends Actor {
    private vtkContourFilter contourFilter;

    public ContourActor(vtkStructuredGrid sGrid, LookupTable lut, double scalarMin, double scalarMax, double scalarInit) {
        super(lut);
        
        contourFilter = new vtkContourFilter();
        contourFilter.SetInput(sGrid);
        contourFilter.SetValue(0, scalarInit);
        contourFilter.ComputeNormalsOff();
        
        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(contourFilter.GetOutput());
        mapper.ScalarVisibilityOn();
        mapper.SetLookupTable(lut);
        mapper.SetScalarRange(scalarMin, scalarMax);
        
        super.SetMapper(mapper);
    }

    public vtkContourFilter getContourFilter() {
        return contourFilter;
    }

}
