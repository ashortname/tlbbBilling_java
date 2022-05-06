package net;

public class PacketOpCodes {
    //Empty
    public static final int NONE = -1;
    //OpCodes
    public static final int CloseReq = 0x00;
    public static final int ConnectReq = 0xA0;
    public static final int ConvertPointReq = 0xE1;
    public static final int CostLogReq = 0xC5;
    public static final int EnterGameReq = 0XA3;
    public static final int KeepAliveNotify = 0xA6;
    public static final int KickNotify = 0xA9;
    public static final int LoginReq = 0xA2;
    public static final int LogOutReq = 0xA4;
    public static final int PingNotify = 0xA1;
    public static final int QueryPointReq = 0xE2;
    public static final int UserRegisterReq = 0xF1;
}
