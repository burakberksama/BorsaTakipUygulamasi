package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddYatirimDialogController {
		
    @FXML private TextField tfHisseKodu;
    @FXML private TextField tfAlisFiyati;
    @FXML private TextField tfAdet;
    @FXML private DatePicker dpAlisTarihi;
    @FXML private TextField tfNot;
    @FXML private Button btnEkle;
    @FXML private Button btnIptal;

    private HisseYatirimi yeniYatirim;

    public HisseYatirimi getYeniYatirim() {
        return yeniYatirim;
    }

    @FXML
    private void initialize() {
        btnEkle.setOnAction(e -> ekleTik());
        btnIptal.setOnAction(e -> iptalTik());
    }

    private void ekleTik() {
        // Basit veri kontrolü/parse işlemleri
        String kod = tfHisseKodu.getText();
        double fiyat;
        double adet;
        try {
            fiyat = Double.parseDouble(tfAlisFiyati.getText());
            adet = Double.parseDouble(tfAdet.getText());   // BURADA double olarak okundu!
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Fiyat ve adet ondalıklı veya tam sayı olmalı!").showAndWait();
            return;
        }
        LocalDate tarihs = dpAlisTarihi.getValue();
        String not = tfNot.getText();

        yeniYatirim = new HisseYatirimi(kod, fiyat, adet, tarihs, not);

        // İlgili Stage'i kapat
        ((Stage) btnEkle.getScene().getWindow()).close();
    }

    private void iptalTik() {
        yeniYatirim = null;
        ((Stage) btnIptal.getScene().getWindow()).close();
    }
}