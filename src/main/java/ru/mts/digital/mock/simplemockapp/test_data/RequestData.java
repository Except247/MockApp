package ru.mts.digital.mock.simplemockapp.test_data;

public class RequestData {

    private final String msisdn;
    private final String name;
    private final String fullTaResp;
    private final String en;
    private final String reg;
    private final String hasStatus;

    public RequestData(String msisdn, String name, String fullTaResp, String en, String reg, String hasStatus)
    {
        this.msisdn = msisdn;
        this.name = name;
        this.fullTaResp = fullTaResp;
        this.en = en;
        this.reg = reg;
        this.hasStatus = hasStatus;
    }

    public String getEndpoint(){
        return msisdn + "/resp?full_ta_resp=" + fullTaResp + "&en=" + en + "&name=" + name;
    }

    public String getMsisdn(){
        return msisdn;
    }

    public String getName(){
        return name;
    }

    public String getFullTaResp(){
        return fullTaResp;
    }

    public String getEn(){
        return en;
    }

    public String getReg(){
        return reg;
    }

    public String getHasStatus(){
        return hasStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "msisdn=" + msisdn +
                ", name='" + name + '\'' +
                ", full_ta_resp='" + fullTaResp + '\'' +
                ", en='" + en + '\'' +
                ", reg='" + reg + '\'' +
                ", hasStatus='" + hasStatus + '\'' +
                '}';
    }
}
