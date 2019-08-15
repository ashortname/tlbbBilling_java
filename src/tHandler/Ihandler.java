package tHandler;

import com.mysql.jdbc.MySQLConnection;
import tModel.billingData;

public interface Ihandler{
    public billingData getResponse(billingData bData, MySQLConnection connection);
    public String getType();
}
