package app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class SatisGirisDialogController {
    @FXML private TextField tfSatisFiyati;
    @FXML private TextField tfSatisAdet;
    @FXML private DatePicker dpSatisTarihi;
    @FXML private Button btnKaydet;
    @FXML private Button btnIptal;

    private Double satisFiyati;
    private Integer satisAdet;
    private LocalDate satisTarihi;
    private boolean kaydedildi = false;

    public boolean isKaydedildi() { return kaydedildi; }
    public Double getSatisFiyati() { return satisFiyati; }
    public Integer getSatisAdet() { return satisAdet; }
    public LocalDate getSatisTarihi() { return satisTarihi; }
    private double maxAdet = 0; // Dışardan ayarlanacak!
    public void setMaxAdet(double adet) { this.maxAdet = adet; }
    
    @FXML
    private void initialize() {
        btnKaydet.setOnAction(e -> kaydetTik());
        btnIptal.setOnAction(e -> kapat());
    }

    private void kaydetTik() {
        try {
            satisFiyati = Double.parseDouble(tfSatisFiyati.getText());
            satisAdet = Integer.parseInt(tfSatisAdet.getText());
            satisTarihi = dpSatisTarihi.getValue();
            if (satisAdet > maxAdet) {
                new Alert(Alert.AlertType.ERROR, "Kalan adetten fazla satış yapılamaz!").showAndWait();
                return;
            }
            kaydedildi = true;
            kapat();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Tüm alanları doğru doldurun!").showAndWait();
        }
    }

    private void kapat() {
        Stage stage = (Stage) btnIptal.getScene().getWindow();
        stage.close();
    }
}