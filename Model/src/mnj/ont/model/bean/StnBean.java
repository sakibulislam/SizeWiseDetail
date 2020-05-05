package mnj.ont.model.bean;

public class StnBean {


    public StnBean() {
        super();
    }

    protected String stn;
    protected String inseamPk;
    protected String inseam;
    protected String inseamQty;
    protected String size;
    protected String sizeQty;

    public void setStn(String stn) {
        this.stn = stn;
    }

    public String getStn() {
        return stn;
    }

    public void setInseamPk(String iseamPk) {
        this.inseamPk = iseamPk;
    }

    public String getInseamPk() {
        return inseamPk;
    }

    public void setInseam(String inseam) {
        this.inseam = inseam;
    }

    public String getInseam() {
        return inseam;
    }

    public void setInseamQty(String inseamQty) {
        this.inseamQty = inseamQty;
    }

    public String getInseamQty() {
        return inseamQty;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setSizeQty(String sizeQty) {
        this.sizeQty = sizeQty;
    }

    public String getSizeQty() {
        return sizeQty;
    }
}
