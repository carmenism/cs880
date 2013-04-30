package head;

import vtk.*;

public class Actor extends vtkActor {
    private final LookupTable lookupTable;
    private final ContourFilter contour;
    
    private final vtkImageMapToColors imtc;
    private final vtkProbeFilter probe;
    private final vtkPolyDataMapper mapper;
            
    public Actor(vtkStructuredPointsReader reader, LookupTable lookupTable, ContourFilter contour) {
        super();
        
        this.lookupTable = lookupTable;
        this.contour = contour;
        
        imtc = new vtkImageMapToColors();
        imtc.SetLookupTable(this.lookupTable);
        imtc.SetInput(reader.GetOutput());

        probe = new vtkProbeFilter();
        probe.SetInput(this.contour.GetOutput());
        probe.SetSource(imtc.GetOutput());

        mapper = new vtkPolyDataMapper();
        mapper.SetInput((vtkPolyData) probe.GetOutput());
        mapper.SetLookupTable(this.lookupTable);
        mapper.SetColorModeToMapScalars();

        super.SetMapper(mapper);
    }
    
    public void display() {
        lookupTable.display();
        contour.display();        

        //imtc.SetInput(reader.GetOutput());
        
        //probe.SetInput(this.contour.GetOutput());
        //probe.SetSource(imtc.GetOutput());
    }
    
    public boolean isVisible() {
        return super.GetVisibility() == 1;
    }

    public void flipVisible() {
        setVisible(!isVisible());
    }

    public void setVisible(boolean visible) {
        if (visible) {
            super.SetVisibility(1);
        } else {
            super.SetVisibility(0);
        }
    }

    public void setVisible() {
        super.SetVisibility(1);
    }
    
    public LookupTable getLookupTable() {
        return lookupTable;
    }

    public ContourFilter getContour() {
        return contour;
    }

    public vtkImageMapToColors getImtc() {
        return imtc;
    }

    public vtkProbeFilter getProbe() {
        return probe;
    }

    public vtkPolyDataMapper getMapper() {
        return mapper;
    }
}
