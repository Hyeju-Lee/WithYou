package smu.techtown.withyou;

import androidx.annotation.NonNull;

public class JSONData {
    private String storNm;
    private String ctprvnNm;
    private String signguNm;
    private String signguCode;
    private String rdnmadr;
    private String lnmadr;
    private String latitude;
    private String longitude;
    private String phoneNumber;
    private String cmptncPolcsttnNm;
    private String appnYear;
    private String useYn;
    private String referenceDate;
    private String insttCode;

    public String getCtprvnNm() {
        return ctprvnNm;
    }

    public String getCmptncPolcsttnNm() {
        return cmptncPolcsttnNm;
    }

    public void setCmptncPolcsttnNm(String cmptncPolcsttnNm) {
        this.cmptncPolcsttnNm = cmptncPolcsttnNm;
    }

    public String getAppnYear() {
        return appnYear;
    }

    public void setAppnYear(String appnYear) {
        this.appnYear = appnYear;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(String referenceDate) {
        this.referenceDate = referenceDate;
    }

    public String getInsttCode() {
        return insttCode;
    }

    public void setInsttCode(String insttCode) {
        this.insttCode = insttCode;
    }

    public void setCtprvnNm(String ctprvnNm) {
        this.ctprvnNm = ctprvnNm;
    }

    public String getSignguNm() {
        return signguNm;
    }

    public void setSignguNm(String signguNm) {
        this.signguNm = signguNm;
    }

    public String getSignguCode() {
        return signguCode;
    }

    public void setSignguCode(String signguCode) {
        this.signguCode = signguCode;
    }

    public String getLnmadr() {
        return lnmadr;
    }

    public void setLnmadr(String lnmadr) {
        this.lnmadr = lnmadr;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStorNm() {
        return storNm;
    }

    public void setStorNm(String storNm) {
        this.storNm = storNm;
    }

    public String getRdnmadr() {
        return rdnmadr;
    }

    public void setRdnmadr(String rdnmadr) {
        this.rdnmadr = rdnmadr;
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

    @NonNull
    @Override
    public String toString() {
        return "JSONData{\n"+
                "store name = "+storNm+'\n'+
                "address = "+rdnmadr+'\n'+
                "latitude = "+latitude+'\n'+
                "longitude = "+longitude+'\n'+
                "locat = "+ctprvnNm+'\n'
                +"}";
    }
}


