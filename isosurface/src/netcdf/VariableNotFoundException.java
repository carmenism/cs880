package netcdf;

/**
 * Defines an exception for when a specific variable is not found within a
 * NetCDF file that is being processed.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class VariableNotFoundException extends Exception {
    private static final long serialVersionUID = 7690689084824793484L;

    public VariableNotFoundException() {
        super("Cannot find find variable in file.");
    }
}
