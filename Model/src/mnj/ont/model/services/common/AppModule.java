package mnj.ont.model.services.common;

import oracle.jbo.ApplicationModule;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Sep 03 11:26:05 BDT 2015
// ---------------------------------------------------------------------
public interface AppModule extends ApplicationModule {
    String callSplitSizes();

    String callMatrix();

    String uploadDCPOs();

    void lineValidation();

    void setSessionValues(String orgId, String userId, String respId,
                          String respAppl);
}
