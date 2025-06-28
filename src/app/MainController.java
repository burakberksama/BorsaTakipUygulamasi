package app;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class MainController {

    @FXML private TableView<HisseYatirimi> yatirimTable;
    @FXML private TableColumn<HisseYatirimi, String> hisseKoduColumn;
    @FXML private TableColumn<HisseYatirimi, Double> alisFiyatiColumn;
    @FXML private TableColumn<HisseYatirimi, Double> adetColumn;
    @FXML private TableColumn<HisseYatirimi, String> alisTarihiColumn;
    @FXML private TableColumn<HisseYatirimi, String> notColumn;

    @FXML private Button ekleButton;
    @FXML private Button guncelleButton;
    @FXML private Button silButton;
    @FXML private Button pdfKaydetButton;
    @FXML private Button satisGecmisiButton;

    @FXML private TableColumn<HisseYatirimi, Double> satisFiyatiColumn;
    @FXML private TableColumn<HisseYatirimi, String> satisTarihiColumn;
    @FXML private TableColumn<HisseYatirimi, String> karZararColumn;
    @FXML private TableColumn<HisseYatirimi, Double> kalanAdetColumn;
    @FXML private Button satisButton;
    @FXML private Button fiyatGuncelleButton;
    @FXML private TableColumn<HisseYatirimi, String> guncelFiyatColumn;
    @FXML private TableColumn<HisseYatirimi, String> potansiyelKarZararColumn;
    @FXML private PieChart karZararPieChart;
    @FXML private Button temaDegistirButton;
    @FXML private Button hakkindaButton;
    @FXML private Button manualFiyatButton;

    private ObservableList<HisseYatirimi> yatirimListesi = FXCollections.observableArrayList();
    private static final DecimalFormat df2 = new DecimalFormat("#.##");
    private boolean darkMode = false;


    public void initialize() {
        hisseKoduColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHisseKodu()));
        alisFiyatiColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAlisFiyati()));
        
        temaDegistirButton.setOnAction(e -> temaDegistir());

        manualFiyatButton.setOnAction(e -> manuelFiyatGir());

        // Adet artık double! Düzgün gösterim için string formatı tercih edebilirsin:
        adetColumn.setCellValueFactory(cellData ->
            new SimpleObjectProperty<>(cellData.getValue().getAdet())); // veya String.format("%.6f", ...)

        alisTarihiColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlisTarihi().toString()));
        notColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNot()));

        yatirimListesi.addAll(JsonDataManager.loadInvestments());
        yatirimTable.setItems(yatirimListesi);

        ekleButton.setOnAction(e -> ekleYatirim());
        silButton.setOnAction(e -> seciliYatirimiSil());
        guncelleButton.setOnAction(e -> seciliYatirimiGuncelle());
        satisButton.setOnAction(e -> satisGir());
        fiyatGuncelleButton.setOnAction(e -> fiyatlariGuncelle());
        
        Tooltip.install(ekleButton, new Tooltip("Yeni yatırım (hisse ya da kripto) ekle."));
        Tooltip.install(satisButton, new Tooltip("Satış/kısmi satış işlem bilgisi gir."));
        Tooltip.install(fiyatGuncelleButton, new Tooltip("Güncel fiyatları API'dan veya elle güncelle.")); 

        satisFiyatiColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSatisFiyati()));
        satisTarihiColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getSatisTarihi() != null ? cellData.getValue().getSatisTarihi().toString() : ""));

        karZararColumn.setCellValueFactory(cellData -> {
            Double kz = karZararHesapla(cellData.getValue());
            String prefix = cellData.getValue().getHisseKodu().toUpperCase().contains("USDT") ? "USDT " : "₺ ";
            String val = kz == null ? "" : prefix + df2.format(kz);
            return new SimpleStringProperty(val);
        });

        guncelFiyatColumn.setCellValueFactory(cellData -> {
            Double fiyat = cellData.getValue().getGuncelFiyat();
            String text = fiyat == null ? "" : df2.format(fiyat);
            return new SimpleStringProperty(text);
        });

        kalanAdetColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(kalanAdetHesapla(cellData.getValue())));

        potansiyelKarZararColumn.setCellValueFactory(cellData -> {
            HisseYatirimi y = cellData.getValue();
            double kalan = kalanAdetHesapla(y);
            if (y.getGuncelFiyat() != null && kalan > 0) {
                double karZarar = (y.getGuncelFiyat() - y.getAlisFiyati()) * kalan;
                String text = (y.getHisseKodu().toUpperCase().contains("USDT") ? "USDT " : "₺ ") + df2.format(karZarar);
                return new SimpleStringProperty(text);
            }
            return new SimpleStringProperty("");
        });
        guncelleGrafik(); // ilk yüklemede grafik güncelle
        
        pdfKaydetButton.setOnAction(e -> tabloyuPdfYaz());
        
        satisGecmisiButton.setOnAction(e -> {
            HisseYatirimi secili = yatirimTable.getSelectionModel().getSelectedItem();
            if (secili != null) satisGecmisiGoster(secili);
        });
        
        hakkindaButton.setOnAction(e -> hakkindaGoster());
    }

    private void ekleYatirim() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddYatirimDialog.fxml"));
            DialogPane pane = loader.load();
            AddYatirimDialogController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Yeni Yatırım Ekle");
            dialog.setResizable(false);

            dialog.showAndWait();

            HisseYatirimi yeni = controller.getYeniYatirim();
            if (yeni != null) {
                yatirimListesi.add(yeni);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        JsonDataManager.saveInvestments(new ArrayList<>(yatirimListesi));
        guncelleGrafik();
    }

    private void seciliYatirimiSil() {
        HisseYatirimi secili = yatirimTable.getSelectionModel().getSelectedItem();
        if (secili == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText(null);
        alert.setContentText("'" + secili.getHisseKodu() + "' kodlu yatırımı silmek istiyor musunuz?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            yatirimListesi.remove(secili);
        }
        JsonDataManager.saveInvestments(new ArrayList<>(yatirimListesi));
        guncelleGrafik();
    }

    private void seciliYatirimiGuncelle() {
        HisseYatirimi secili = yatirimTable.getSelectionModel().getSelectedItem();
        if (secili == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GuncelleYatirimDialog.fxml"));
            DialogPane pane = loader.load();
            GuncelleYatirimDialogController controller = loader.getController();
            controller.setYatirim(secili);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Yatırım Güncelle");
            dialog.setResizable(false);

            dialog.showAndWait();

            HisseYatirimi yeni = controller.getGuncellenmisYatirim();
            if (yeni != null) {
                secili.setHisseKodu(yeni.getHisseKodu());
                secili.setAlisFiyati(yeni.getAlisFiyati());
                secili.setAdet(yeni.getAdet());
                secili.setAlisTarihi(yeni.getAlisTarihi());
                secili.setNot(yeni.getNot());
                yatirimTable.refresh();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        JsonDataManager.saveInvestments(new ArrayList<>(yatirimListesi));
        guncelleGrafik();
    }

    private Double karZararHesapla(HisseYatirimi y) {
        double toplam = 0.0;
        for (SatisIslemi satis : y.getSatislar()) {
            toplam += (satis.getFiyat() - y.getAlisFiyati()) * satis.getAdet();
        }
        // Satış yoksa null döndür (veya ister 0 yap)
        return y.getSatislar().isEmpty() ? null : toplam;
    }

    private void satisGir() {
        HisseYatirimi secili = yatirimTable.getSelectionModel().getSelectedItem();
        if (secili == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SatisGirisDialog.fxml"));
            DialogPane pane = loader.load();
            SatisGirisDialogController controller = loader.getController();

            double kalan = kalanAdetHesapla(secili);
            controller.setMaxAdet(kalan);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle("Satış Gir");
            dialog.setResizable(false);
            dialog.showAndWait();

            if (controller.isKaydedildi()) {
                // SATIŞI MODELDEKİ ALANLARA DA YAZ!
                secili.setSatisFiyati(controller.getSatisFiyati());
                secili.setSatisTarihi(controller.getSatisTarihi());
                secili.setSatisAdet((int) controller.getSatisAdet());

                // Satış geçmişine ekle (mevcut halin)
                SatisIslemi satis = new SatisIslemi(controller.getSatisAdet(), controller.getSatisFiyati(), controller.getSatisTarihi());
                secili.satisEkle(satis);

                yatirimTable.refresh();
                JsonDataManager.saveInvestments(new ArrayList<>(yatirimListesi));
                guncelleGrafik();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void satisGecmisiGoster(HisseYatirimi varlik) {
        StringBuilder sb = new StringBuilder();
        sb.append("Satış Geçmişi: " + varlik.getHisseKodu() + "\n\n");
        int i = 1;
        for (SatisIslemi satis : varlik.getSatislar()) {
            sb.append(i++ + ". ");
            sb.append("Adet: " + satis.getAdet());
            sb.append(", Fiyat: " + satis.getFiyat());
            sb.append(", Tarih: " + satis.getTarih());
            sb.append("\n");
        }
        if (varlik.getSatislar().isEmpty()) {
            sb.append("Henüz satış yok.");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, sb.toString(), ButtonType.OK);
        alert.setTitle("Satış Geçmişi");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    private double toplamSatilanAdet(HisseYatirimi y) {
        return y.getSatislar().stream().mapToDouble(SatisIslemi::getAdet).sum();
    }

    private double kalanAdetHesapla(HisseYatirimi y) {
        return y.getAdet() - toplamSatilanAdet(y);
    }
    

    private void fiyatlariGuncelle() {
        for (HisseYatirimi y : yatirimListesi) {
            Double fiyat = FiyatServisi.getVarlikFiyati(y.getHisseKodu());
            y.setGuncelFiyat(fiyat);
        }
        yatirimTable.refresh();
        guncelleGrafik();
    }

    private void guncelleGrafik() {
        karZararPieChart.getData().clear();
        for (HisseYatirimi y : yatirimListesi) {
            String etiket = y.getHisseKodu();
            Double kz;
            // Satılmışsa gerçek K/Z, satılmamışsa potansiyel K/Z göster
            if (y.getSatisFiyati() != null && y.getSatisAdet() != null) {
                kz = (y.getSatisFiyati() - y.getAlisFiyati()) * y.getSatisAdet();
            } else if (y.getGuncelFiyat() != null) {
                kz = (y.getGuncelFiyat() - y.getAlisFiyati()) * y.getAdet();
            } else {
                kz = 0.0;
            }
            karZararPieChart.getData().add(new PieChart.Data(etiket, Math.abs(kz)));
        }
    }
    
    private void tabloyuPdfYaz() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("portfoy_raporu.pdf"));
            document.open();

            // Başlık
            document.add(new Paragraph("Portföy Raporu (" + java.time.LocalDate.now() + ")\n\n"));

            // Tablo: Kolon sayısı (örnek tablona göre)
            PdfPTable table = new PdfPTable(8); // 8 sütun

            // Sütun başlıkları
            table.addCell("Kodu");
            table.addCell("Alış Fiyatı");
            table.addCell("Adet");
            table.addCell("Alış Tarihi");
            table.addCell("Not");
            table.addCell("Güncel Fiyat");
            table.addCell("Kar/Zarar");
            table.addCell("Kalan Adet");

            // Satırlar
            for (HisseYatirimi y : yatirimListesi) {
                table.addCell(y.getHisseKodu());
                table.addCell(String.valueOf(y.getAlisFiyati()));
                table.addCell(String.format("%.6f", y.getAdet()));
                table.addCell(String.valueOf(y.getAlisTarihi()));
                table.addCell(y.getNot() != null ? y.getNot() : "");
                table.addCell(y.getGuncelFiyat() != null ? String.valueOf(y.getGuncelFiyat()) : "");
                Double kz = karZararHesapla(y);
                table.addCell(kz != null ? String.valueOf(kz) : "");
                table.addCell(String.valueOf(kalanAdetHesapla(y)));
            }

            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "PDF başarıyla oluşturuldu (program klasöründe):\nportfoy_raporu.pdf",
                    ButtonType.OK);
            alert.setHeaderText("Rapor Alındı!");
            alert.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "PDF oluşturulamadı: " + ex.getMessage(),
                    ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    private void temaDegistir() {
        Scene scene = temaDegistirButton.getScene();

        if (darkMode) {
            // Light mode'a geç
            scene.getStylesheets().remove(getClass().getResource("application-dark.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("application-light.css").toExternalForm());
            temaDegistirButton.setText("🌙 Koyu/Aydınlık");
            darkMode = false;
        } else {
            // Dark mode'a geç
            scene.getStylesheets().remove(getClass().getResource("application-light.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("application-dark.css").toExternalForm());
            temaDegistirButton.setText("☀️ Koyu/Aydınlık");
            darkMode = true;
        }
    }
    
    private void hakkindaGoster() {
        String metin = """
            Borsa/Crypto Hesap Takip Uygulaması
            ----------------------------------------
            Bu uygulama yatırım portföyünüzü kolayca yönetmeniz ve 
            hem hisse senedi hem de kripto varlık işlemlerini izlemeniz için geliştirildi.
            
            ----------------------------------------
            Borsa İstanbul Kurallarından dolayı hisse senetlerinde güncel fiyatları manuel olarak girmeniz gerekmekte. 
            Seçeceğiniz hisse senedinin güncel fiyatını girmeniz için butonlardan "Güncel Fiyat Gir" butonuna tıklamanız yeterlidir.
            
            ----------------------------------------

            - Geliştirici: Burak Berk Şama
            - İletişim/E-posta: burakberksama@gmail.com
            - Teknoloji: Java, JavaFX, OpenPDF, Gson, Finnhub API

            © 2025 Tüm hakları saklıdır.
            Tüm yazılı, görsel ve fonksiyonel içerik geliştiriciye aittir.
            Bu ürün, eğitim/test amaçlıdır ve finansal/danışmanlık tavsiyesi niteliği taşımaz.
            """;
        Alert info = new Alert(Alert.AlertType.INFORMATION, metin, ButtonType.OK);
        info.setHeaderText("Hakkında / Yardım");
        info.setTitle("Hakkında");
        info.showAndWait();
    }      
    
    private void manuelFiyatGir() {
        HisseYatirimi secili = yatirimTable.getSelectionModel().getSelectedItem();
        if (secili == null) {
            Alert uyar = new Alert(Alert.AlertType.WARNING, "Lütfen fiyatını değiştirmek istediğiniz satırı seçin!", ButtonType.OK);
            uyar.showAndWait();
            return;
        }

        TextInputDialog girdi = new TextInputDialog(
            secili.getGuncelFiyat() != null ? secili.getGuncelFiyat().toString() : ""
        );
        girdi.setTitle("Güncel Fiyatı Manuel Gir");
        girdi.setHeaderText("Fiyatı güncelle (" + secili.getHisseKodu() + ")");
        girdi.setContentText("Yeni fiyat (örn: 74.25):");

        // Simge eklemek istiyorsan:
        Stage stage = (Stage) girdi.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("app_icon.png")));

        girdi.showAndWait().ifPresent(input -> {
            try {
                double fiyat = Double.parseDouble(input.replace(",", "."));
                secili.setGuncelFiyat(fiyat);
                yatirimTable.refresh();
                guncelleGrafik();
            } catch (NumberFormatException ex) {
                Alert hata = new Alert(Alert.AlertType.ERROR, "Geçerli bir sayı giriniz.", ButtonType.OK);
                hata.showAndWait();
            }
        });
    }
}