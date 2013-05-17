import vtk.vtkLookupTable;

/**
 * Extends the vktLookupTable class to give ability to easily reverse the color
 * values in the table. Also gives ability to adjust the opacities of all colors
 * at once.
 * 
 * @author Carmen St. Jean (crr8@unh.edu)
 * 
 */
public class LookupTable extends vtkLookupTable {
    public LookupTable() {
        super();
    }

    /**
     * Sets the alpha for every color in the color table to the specified value.
     * 
     * @param alpha
     *            The desired opacity; a value from 0.0 to 1.0, where 0.0 is
     *            completely transparent.
     */
    public void setAlphaForAllColors(double alpha) {
        int numberColors = super.GetNumberOfColors();

        for (int i = 0; i < numberColors; i++) {
            double[] color = super.GetTableValue(i);

            super.SetTableValue(i, color[0], color[1], color[2], alpha);
        }
    }

    /**
     * Gets the alpha that is used for every color.
     * 
     * @return The opacity of the colors in the table where; a value from 0.0 to
     *         1.0, where 0.0 is completely transparent.
     */
    public double getAlphaForAllColors() {
        return super.GetTableValue(0)[3];
    }

    /**
     * Reverses the ordering of the colors in the table.
     */
    public void reverseTableColors() {
        int numberColors = super.GetNumberOfColors();
        int end = numberColors - 1;

        for (int i = 0; i < numberColors / 2; i++) {
            double[] forward = super.GetTableValue(i);
            double[] backward = super.GetTableValue(end - i);

            super.SetTableValue(i, backward);
            super.SetTableValue(end - i, forward);
        }
    }
}
