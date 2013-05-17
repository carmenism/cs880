import vtk.vtkStructuredGrid;

/**
 * Represents the boundary rendering of the lake, which is the top-most and
 * bottom-most sigma layers rendered completely in black. The purpose of this
 * rendering is to give the user some context of the lake while viewing
 * isosurfaces.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class BoundaryActor extends FullActor {
    /**
     * Creates a boundary actor.
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
    public BoundaryActor(vtkStructuredGrid sGrid, LookupTable lut,
            double tempMin, double tempMax) {
        super(sGrid, lut, tempMin, tempMax);

        super.GetProperty().SetRepresentationToWireframe();
    }
}
