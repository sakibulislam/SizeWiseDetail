package mnj.ont.model.bean;

public class InseamBean {
    public InseamBean() {
        super();
    }
    protected String inseamPk;
    protected String inseam;
    protected String inseamQty;

    public void setInseamPk(String inseamPk) {
        this.inseamPk = inseamPk;
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
}
