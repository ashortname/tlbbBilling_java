package tModel;

public class QueryResult {
    public userAccount acc;
    public userAccount.QueryCode code;

    public QueryResult(userAccount ua, userAccount.QueryCode qc)
    {
        acc = ua;
        code = qc;
    }
}
