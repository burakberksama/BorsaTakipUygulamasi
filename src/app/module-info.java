module BorsaTakipUygulamasi {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.github.librepdf.openpdf;
	requires javafx.graphics;
    
    
    opens app to javafx.fxml, com.google.gson;
    exports app;
}