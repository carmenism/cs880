import vtk.vtkDataSetMapper;
import vtk.vtkStructuredGrid;

/**
 * Represents the full surface rendering of the lake, which is the top-most and
 * bottom-most sigma layers. Not a true isosurface, but interesting to look at.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class FullActor extends Actor {
    /**
     * Creates a full surface actor.
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
    public FullActor(vtkStructuredGrid sGrid, LookupTable lut, double tempMin,
            double tempMax) {
        super(lut);

        vtkDataSetMapper mapper = new vtkDataSetMapper();
        mapper.SetInput(sGrid);
        mapper.SetScalarRange(tempMin, tempMax);
        mapper.SetLookupTable(lut);

        super.SetMapper(mapper);
    }
}
