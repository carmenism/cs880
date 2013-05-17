import vtk.vtkContourFilter;
import vtk.vtkPolyDataMapper;
import vtk.vtkStructuredGrid;

/**
 * Renders an isosurface of the lake.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class IsosurfaceActor extends Actor {
    private final vtkContourFilter contourFilter;
    
    /**
     * Creates an isosurface actor.
     * 
     * @param sGrid
     *            The structured grid to be the input of the actor.
     * @param lut
     *            The color look up table that
     * @param tempMin
     *            The minimum temperature value to use for the color mapping.
     * @param tempMax
     *            The maximum temperature value to use for the color mapping.
     */
    public IsosurfaceActor(vtkStructuredGrid sGrid, LookupTable lut,
            double tempMin, double tempMax, double scalarInit) {
        super(lut);

        contourFilter = new vtkContourFilter();
        contourFilter.SetInput(sGrid);
        contourFilter.SetValue(0, scalarInit);
        contourFilter.ComputeNormalsOff();

        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(contourFilter.GetOutput());
        mapper.ScalarVisibilityOn();
        mapper.SetLookupTable(lut);
        mapper.SetScalarRange(tempMin, tempMax);

        super.SetMapper(mapper);
    }

    public vtkContourFilter getContourFilter() {
        return contourFilter;
    }

}
