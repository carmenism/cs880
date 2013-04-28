package netcdf;


public class NetCDFConfiguration {
    protected String latitude = null;
    protected String longitude = null;
    protected String sigma = null;
    protected String depth = null;
    protected String zeta = null;
    protected String mask = null;
    
    public NetCDFConfiguration() {
        
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSigma() {
        return sigma;
    }

    public void setSigma(String sigma) {
        this.sigma = sigma;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getZeta() {
        return zeta;
    }

    public void setZeta(String zeta) {
        this.zeta = zeta;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
