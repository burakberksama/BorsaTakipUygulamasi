package app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;


public class GuncelleYatirimDialogController {

	 @FXML private TextField tfHisseKodu;
	    @FXML private TextField tfAlisFiyati;
	    @FXML private TextField tfAdet;
	    @FXML private DatePicker dpAlisTarihi;
	    @FXML private TextField tfNot;
	    @FXML private Button btnGuncelle;
	    @FXML private Button btnIptal;

	    private HisseYatirimi guncellenmisYatirim;

	    public void setYatirim(HisseYatirimi y) {
	        // Var olan veriyi inputlara doldur!
	        tfHisseKodu.setText(y.getHisseKodu());
	        tfAlisFiyati.setText(String.valueOf(y.getAlisFiyati()));
	        tfAdet.setText(String.valueOf(y.getAdet()));
	        dpAlisTarihi.setValue(y.getAlisTarihi());
	        tfNot.setText(y.getNot());
	    }

	    public HisseYatirimi getGuncellenmisYatirim() {
	        return guncellenmisYatirim;
	    }

	    @FXML
	    private void initialize() {
	        btnGuncelle.setOnAction(e -> guncelleTik());
	        btnIptal.setOnAction(e -> iptalTik());
	    }

	    private void guncelleTik() {
	        String kod = tfHisseKodu.getText();
	        double fiyat;
	        int adet;
	        try {
	            fiyat = Double.parseDouble(tfAlisFiyati.getText());
	            adet = Integer.parseInt(tfAdet.getText());
	        } catch (NumberFormatException ex) {
	            new Alert(Alert.AlertType.ERROR, "Fiyat ve adet sayı olmalı!").showAndWait();
	            return;
	        }
	        LocalDate tarih = dpAlisTarihi.getValue();
	        String not = tfNot.getText();

	        guncellenmisYatirim = new HisseYatirimi(kod, fiyat, adet, tarih, not);

	        // Dialog kapansın
	        ((Stage) btnGuncelle.getScene().getWindow()).close();
	    }

	    private void iptalTik() {
	        guncellenmisYatirim = null;
	        ((Stage) btnIptal.getScene().getWindow()).close();
	    }
}
