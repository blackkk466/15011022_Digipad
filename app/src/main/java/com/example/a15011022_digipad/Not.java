package com.example.a15011022_digipad;

import android.os.Parcel;
import android.os.Parcelable;


public class Not implements Parcelable {
    private String baslik;
    private String adres;
    private String icerik;
    private String renk;
    private int oncelik;
    private String tarih;
    private long id;

    public Not ( String baslik, String adres, String icerik, String renk, int oncelik, String  tarih ){
        this.baslik = baslik;
        this.adres = adres;
        this.icerik = icerik;
        this.renk = renk;
        this.oncelik = oncelik;
        this.tarih = tarih;
    }

    //constructor to be used when passing with bundle object
    protected Not(Parcel in) {
        baslik = in.readString();
        adres = in.readString();
        icerik = in.readString();
        renk = in.readString();
        oncelik = in.readInt();
        tarih = in.readString();
        id = in.readLong();
    }

    public static final Creator<Not> CREATOR = new Creator<Not>() {
        @Override
        public Not createFromParcel(Parcel in) {
            return new Not(in);
        }

        @Override
        public Not[] newArray(int size) {
            return new Not[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(baslik);
        dest.writeString(adres);
        dest.writeString(icerik);
        dest.writeString(renk);
        dest.writeInt(oncelik);
        dest.writeString(tarih);
        dest.writeLong(id);
    }


    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public String getRenk() {
        return renk;
    }

    public void setRenk(String renk) {
        this.renk = renk;
    }

    public int getOncelik() {
        return oncelik;
    }

    public void setOncelik(int oncelik) {
        this.oncelik = oncelik;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
