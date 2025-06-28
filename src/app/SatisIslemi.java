package app;

import java.time.LocalDate;

public class SatisIslemi {
    private double adet;
    private double fiyat;
    private LocalDate tarih;

    public SatisIslemi(double adet, double fiyat, LocalDate tarih) {
        this.adet = adet;
        this.fiyat = fiyat;
        this.tarih = tarih;
    }
    public double getAdet() { return adet; }
    public double getFiyat() { return fiyat; }
    public LocalDate getTarih() { return tarih; }
}
