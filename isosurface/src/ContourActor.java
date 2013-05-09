import vtk.vtkContourFilter;
import vtk.vtkLookupTable;
import vtk.vtkPolyDataMapper;
import vtk.vtkStructuredGrid;


public class ContourActor extends Actor {
    private vtkContourFilter contourFilter;

    public ContourActor(vtkStructuredGrid sGrid, vtkLookupTable lut, double scalarMin, double scalarMax, double scalarInit, double opacity) {
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
        
        super.GetProperty().SetOpacity(opacity);
    }

    public vtkContourFilter getContourFilter() {
        return contourFilter;
    }

}
