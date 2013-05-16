import java.util.ArrayList;
import java.util.Scanner;

import vtk.vtkLookupTable;

public class LookupTable extends vtkLookupTable {
    public LookupTable() {
        super();
    }
    
    public LookupTable(ArrayList<String> lines, double opacity) {
        super();
        
        changeColors(lines);
    }
    
    public void setOpacityForAllColors(double opacity) {
        int numberColors = super.GetNumberOfColors();

        for (int i = 0; i < numberColors; i++) {
            double[] color = super.GetTableValue(i);

            super.SetTableValue(i, color[0], color[1], color[2], opacity);
        }
    }

    public double getOpacityForAllColors() {
        return super.GetTableValue(0)[3];
    }

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

    private void changeColors(ArrayList<String> lines) {
        double opacity = getOpacityForAllColors();
        
        changeColors(lines, opacity);
    }
    
    public void changeColors(ArrayList<String> lines, double opacity) {
        int numColors = lines.size();

        super.SetNumberOfColors(numColors);
        super.SetNanColor(0.0, 0.0, 0.0, 0.0);

        for (int i = 0; i < lines.size(); i++) {
            Scanner scanLine = new Scanner(lines.get(i));// numColors - i - 1));

            double r = (double) scanLine.nextInt() / 255;
            double g = (double) scanLine.nextInt() / 255;
            double b = (double) scanLine.nextInt() / 255;

            super.SetTableValue(i, r, g, b, opacity);

            scanLine.close();
        }
    }
}
