import vtk.*;

public class Test {
    public static void main(String [] args) {
        vtkNetCDFCFReader reader = new vtkNetCDFCFReader();
        
        reader.SetFileName("/home/csg/crr8/spring2013/cs880/flowvis/glofs.lsofs.fields.forecast.20130301.t00z.nc");
        reader.Update();
    //    reader.re
        
       // NetCDFCFReader r;
    }
}
