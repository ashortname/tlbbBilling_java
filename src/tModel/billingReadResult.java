package tModel;

public class billingReadResult {
    public billingData bresult;
    public billingData.BillingDataCode code;
    public int pLength;

    public billingReadResult(billingData br, billingData.BillingDataCode c, int pl)
    {
        bresult = br;
        code = c;
        pLength = pl;
    }
}
