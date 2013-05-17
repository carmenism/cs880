import vtk.vtkActor;

/**
 * Extends the vtkActor class to give the ability to get an easy reference to
 * the color look-up table that is associated with the actor.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class Actor extends vtkActor {
    private final LookupTable lookupTable;

    public Actor(LookupTable lut) {
        super();

        lookupTable = lut;
    }

    public LookupTable getLookupTable() {
        return lookupTable;
    }
}
