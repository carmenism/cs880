import vtk.vtkDataSetMapper;
import vtk.vtkStructuredGrid;

public class FullActor extends Actor {

    public FullActor(vtkStructuredGrid sGrid, LookupTable lut, double scalarMin, double scalarMax) {
        super(lut);
              
        vtkDataSetMapper mapper = new vtkDataSetMapper();
        mapper.SetInput(sGrid);
        mapper.SetScalarRange(scalarMin, scalarMax);
        mapper.SetLookupTable(lut);           
       
        super.SetMapper(mapper);
    }

}
