package app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HisseYatirimi {
    private String hisseKodu;
    private double alisFiyati;
    private double adet;
    private LocalDate alisTarihi;
    private String not; // isteğe bağlı
    private Double guncelFiyat;
    
    // Satış işlemi istersek:
    private Double satisFiyati;
    private Integer satisAdet;
    private LocalDate satisTarihi;

    // --- Constructor ---
    public HisseYatirimi(String hisseKodu, double alisFiyati, double adet, LocalDate alisTarihi, String not) {
        this.hisseKodu = hisseKodu;
        this.alisFiyati = alisFiyati;
        this.adet = adet;
        this.alisTarihi = alisTarihi;
        this.not = not;
     // satış alanları başta boş
        this.satisFiyati = null;
        this.satisAdet = null;
        this.satisTarihi = null;
    }
    
    private List<SatisIslemi> satislar = new ArrayList<>();
    public List<SatisIslemi> getSatislar() {
        if (satislar == null) satislar = new ArrayList<>();
        return satislar;
    }
    public void satisEkle(SatisIslemi satis) { satislar.add(satis); }

    // --- Getters & Setters ---

    public String getHisseKodu() {
        return hisseKodu;
    }

    public void setHisseKodu(String hisseKodu) {
        this.hisseKodu = hisseKodu;
    }

    public double getAlisFiyati() {
        return alisFiyati;
    }

    public void setAlisFiyati(double alisFiyati) {
        this.alisFiyati = alisFiyati;
    }

    public double getAdet() {
        return adet;
    }

    public void setAdet(double adet) {
        this.adet = adet;
    }

    public LocalDate getAlisTarihi() {
        return alisTarihi;
    }

    public void setAlisTarihi(LocalDate alisTarihi) {
        this.alisTarihi = alisTarihi;
    }

    public String getNot() {
        return not;
    }

    public void setNot(String not) {
        this.not = not;
    }
    public Double getSatisFiyati() { 
    	return satisFiyati; 
    	}
    public void setSatisFiyati(Double satisFiyati) { 
    	this.satisFiyati = satisFiyati; 
    	}

    public Integer getSatisAdet() { 
    	return satisAdet; 
    	}
    public void setSatisAdet(Integer satisAdet) { 
    	this.satisAdet = satisAdet; 
    	}

    public LocalDate getSatisTarihi() { 
    	return satisTarihi; 
    	}
    public void setSatisTarihi(LocalDate satisTarihi) { 
    	this.satisTarihi = satisTarihi; 
    	}
    public Double getGuncelFiyat() { 
    	return guncelFiyat; 
    	}
    public void setGuncelFiyat(Double guncelFiyat) { 
    	this.guncelFiyat = guncelFiyat; 
    	}
    
}


