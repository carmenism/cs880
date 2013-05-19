Isosurface Rendering
Carmen St. Jean (crr8@unh.edu)
Spring 2013

To run: VTK must be installed.  VTK and NetCDF will have to be 
   added to your class path.

java RenderLake lakefile colorfile configfile timeindex

Example:
java RenderLake glofs.lsofs.fields.nowcast.20130430.t00z.nc 

lakefile --
    The path of the NetCDF input file to be rendered

colorfile --
    The path of the color table file (must contain 256 colors,
    one color per line, RGB values from 0 to 255, any delimiter)

configfile --
    The path to a Java properties configuration describing the 
    variable names of the NetCDF input

timeindex -- 
    The time index to be used in the NetCDF file - e.g., 0 for
    the first time index