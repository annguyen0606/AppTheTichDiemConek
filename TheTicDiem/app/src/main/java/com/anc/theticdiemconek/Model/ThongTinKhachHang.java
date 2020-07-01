package com.anc.theticdiemconek.Model;

import android.print.PrinterId;

public class ThongTinKhachHang {
    private String UIDKH;
    private String NameKH;
    private String AddressKH;
    private String PhoneKH;
    private int TichDiem;

    public ThongTinKhachHang(String UIDKH, String nameKH, String addressKH, String phoneKH, int tichDiem) {
        this.UIDKH = UIDKH;
        NameKH = nameKH;
        AddressKH = addressKH;
        PhoneKH = phoneKH;
        TichDiem = tichDiem;
    }

    public String getUIDKH() {
        return UIDKH;
    }

    public void setUIDKH(String UIDKH) {
        this.UIDKH = UIDKH;
    }

    public String getNameKH() {
        return NameKH;
    }

    public void setNameKH(String nameKH) {
        NameKH = nameKH;
    }

    public String getAddressKH() {
        return AddressKH;
    }

    public void setAddressKH(String addressKH) {
        AddressKH = addressKH;
    }

    public String getPhoneKH() {
        return PhoneKH;
    }

    public void setPhoneKH(String phoneKH) {
        PhoneKH = phoneKH;
    }

    public int getTichDiem() {
        return TichDiem;
    }

    public void setTichDiem(int tichDiem) {
        TichDiem = tichDiem;
    }
}
