package cafeshopmanagementsystem;

import cafeshopmanagementsystem.database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cafeshopmanagementsystem.productData;



public class UpdateFormController {

    @FXML private TextField idprod;
    @FXML private TextField nameprod;
    @FXML private ComboBox<String> type;
    @FXML private TextField stockprod;
    @FXML private TextField prixprod;
    @FXML private ComboBox<String> statusid;
    @FXML private ImageView imageid;
    @FXML private Button importBtn;
    @FXML private Button saveBtn;
    @FXML private Button ignoreBtn;

    private productData currentProduct;
    private Connection connect;
    private PreparedStatement prepare;

    private String imagePath;  // chemin de l'image importée ou existante

    private final String[] typeList = {"Meals", "Drinks"};
    private final String[] statusList = {"Available", "Unavailable"};

    public void initialize() {
        type.setItems(FXCollections.observableArrayList(typeList));
        statusid.setItems(FXCollections.observableArrayList(statusList));
    }

    // Remplit le formulaire avec les données du produit sélectionné
    public void setProductData(productData product) {
        this.currentProduct = product;

        idprod.setText(product.getProductId());
        idprod.setEditable(false);

        nameprod.setText(product.getProductName());
        type.setValue(product.getType());
        stockprod.setText(String.valueOf(product.getStock()));
        prixprod.setText(String.valueOf(product.getPrice()));
        statusid.setValue(product.getStatus());

        imagePath = product.getImage();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            Image image = new Image(new File(imagePath).toURI().toString());
            imageid.setImage(image);
        }
    }

    @FXML
    private void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(importBtn.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageid.setImage(image);
        }
    }

    @FXML
    private void saveUpdate() {
        // Vérification des champs obligatoires
        if (nameprod.getText().trim().isEmpty() ||
                type.getSelectionModel().getSelectedItem() == null ||
                stockprod.getText().trim().isEmpty() ||
                prixprod.getText().trim().isEmpty() ||
                statusid.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Validation des formats numériques
        int stock;
        double price;
        try {
            stock = Integer.parseInt(stockprod.getText().trim());
            price = Double.parseDouble(prixprod.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Le stock doit être un entier et le prix un nombre décimal valide.");
            return;
        }

        try {
            connect = database.connectDB();

            String sql = "UPDATE product SET prod_name=?, prod_type=?, stock=?, price=?, status=?, image=? WHERE prod_id=?";
            prepare = connect.prepareStatement(sql);

            prepare.setString(1, nameprod.getText().trim());
            prepare.setString(2, type.getSelectionModel().getSelectedItem());
            prepare.setInt(3, stock);
            prepare.setDouble(4, price);
            prepare.setString(5, statusid.getSelectionModel().getSelectedItem());
            prepare.setString(6, imagePath);
            prepare.setString(7, idprod.getText());

            int result = prepare.executeUpdate();
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit mis à jour avec succès !");
                // Fermer la fenêtre
                Stage stage = (Stage) saveBtn.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La mise à jour a échoué.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        } finally {
            try {
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void ignoreUpdate() {
        // Fermer la fenêtre sans enregistrer
        Stage stage = (Stage) ignoreBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
