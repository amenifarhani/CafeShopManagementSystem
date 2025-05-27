package cafeshopmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {
    @FXML
    private Button importBtn;
    @FXML
    private Button addBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Button deletBtn;
    @FXML
    private TextField nameprod;

    @FXML
    private TextField prixprod;



    @FXML
    private TextField stockprod;
    @FXML
    private TextField idprod;

    @FXML
    private ImageView imageid;

    @FXML
    private Button updateBtn;
    @FXML
    private TableColumn<productData, String> colproductID;
    @FXML
    private AnchorPane mainform;


    @FXML
    private TableColumn<productData, String>coldate;

    @FXML
    private TableColumn<productData, String> colprice;


    @FXML
    private TableColumn<productData, String>colproductName;

    @FXML
    private TableColumn<productData, String> colstatus;

    @FXML
    private TableColumn<productData, String> colstock;

    @FXML
    private TableColumn<productData, String> coltype;

    @FXML
    private TableView<productData> tableView;



    @FXML
    private TextField priceid;

    @FXML
    private ComboBox<String> statusid;

    @FXML
    private TextField stokid;

    @FXML
    private ComboBox<String> type;

    @FXML
    private Label username;



    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;



    public void inventoryDeleteBtn() {
        productData selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer le produit sélectionné ?");

        Optional<ButtonType> option = alert.showAndWait();

        if (option.isPresent() && option.get().equals(ButtonType.OK)) {
            String deleteQuery = "DELETE FROM product WHERE prod_id = ?";

            connect = database.connectDB();

            try {
                prepare = connect.prepareStatement(deleteQuery);
                prepare.setString(1, selectedProduct.getProductId());
                int rowsDeleted = prepare.executeUpdate();

                if (rowsDeleted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès.");
                    inventoryShowData(); // Refresh la table
                    clearFields();       // Nettoie les champs
                } else {
                    showAlert(Alert.AlertType.ERROR, "Échec", "La suppression a échoué. Produit introuvable.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur est survenue : " + e.getMessage());
            } finally {
                try {
                    if (prepare != null) prepare.close();
                    if (connect != null) connect.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }





    public void inventoryAddBtn() {
        // Vérification des champs un par un avec messages spécifiques
        if (idprod.getText() == null || idprod.getText().trim().isEmpty()) {
            showAlert("Error Message", "Missing Product ID", "Please enter a valid Product ID.");
            return;
        }

        if (nameprod.getText() == null || nameprod.getText().trim().isEmpty()) {
            showAlert("Error Message", "Missing Product Name", "Please enter a valid Product Name.");
            return;
        }

        if (type.getSelectionModel().getSelectedItem() == null) {
            showAlert("Error Message", "Missing Product Type", "Please select a Product Type.");
            return;
        }

        if (stockprod.getText() == null || stockprod.getText().trim().isEmpty()) {
            showAlert("Error Message", "Missing Stock", "Please enter the Stock quantity.");
            return;
        }

        if (prixprod.getText() == null || prixprod.getText().trim().isEmpty()) {
            showAlert("Error Message", "Missing Price", "Please enter the Price.");
            return;
        }

        if (statusid.getSelectionModel().getSelectedItem() == null) {
            showAlert("Error Message", "Missing Status", "Please select a Status.");
            return;
        }
        if (data.path == null || data.path.trim().isEmpty()) {
            showAlert("Error Message", "Missing Image", "Please select an Image for the product.");
            return;
        }

        // Si tout est bon, passe à l'insertion dans la base de données
        try {
            String productId = idprod.getText().trim();
            String productName = nameprod.getText().trim();
            String productType = type.getSelectionModel().getSelectedItem().toString();
            int stock = Integer.parseInt(stockprod.getText().trim());
            double price = Double.parseDouble(prixprod.getText().trim());
            String status = statusid.getSelectionModel().getSelectedItem().toString();
            String imagePath = data.path.trim();

            String insertData = "INSERT INTO product (prod_id, prod_name, prod_type, stock, price, status, image, date) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            connect = database.connectDB();

            try (PreparedStatement insertStatement = connect.prepareStatement(insertData)) {
                insertStatement.setString(1, productId);
                insertStatement.setString(2, productName);
                insertStatement.setString(3, productType);
                insertStatement.setInt(4, stock);
                insertStatement.setDouble(5, price);
                insertStatement.setString(6, status);
                insertStatement.setString(7, imagePath);

                int rowsInserted = insertStatement.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Success", "Product Added", "The product has been successfully added.");
                    clearFields(); // Nettoyer les champs après ajout
                    inventoryShowData(); // Rafraîchir la table pour afficher le nouveau produit
                }}


        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Error Connecting to Database", "An error occurred while inserting the product.");
        } finally {
            try {
                if (connect != null) connect.close();
            }  catch (Exception e) {
            e.printStackTrace();  // Afficher les détails de l'erreur dans la console
            showAlert("Database Error", "Error Connecting to Database", "An error occurred while inserting the product.\n" + e.getMessage());
        }
            }
        }


    // Méthode pour afficher les alertes plus proprement
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour nettoyer les champs après ajout
    private void clearFields() {
        idprod.clear();
        nameprod.clear();
        type.getSelectionModel().clearSelection();
        stockprod.clear();
        prixprod.clear();
        statusid.getSelectionModel().clearSelection();
        data.path = null;
        imageid.setImage(null); // <-- Vider l'image après ajout
    }








    public ObservableList<productData> inventoryDataList() {
        ObservableList<productData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM product";
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                productData prodData = new productData(
                        result.getInt("id"),
                        result.getString("prod_id"),
                        result.getString("prod_name"),
                        result.getString("prod_type"), // <- correction ici : c'était "type"
                        result.getInt("stock"),
                        result.getDouble("price"),
                        result.getString("status"),
                        result.getString("image"),
                        result.getDate("date")
                );
                listData.add(prodData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits : " + e.getMessage());
        } finally {
            try {
                if (result != null) result.close();
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listData;
    }
    private String selectedProductId = null;

    public void inventoryUpdateBtn() {
        productData selectedProduct = tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateForm.fxml"));
            Parent root = loader.load();

            UpdateFormController updateController = loader.getController();
            updateController.setProductData(selectedProduct);

            Stage stage = new Stage();
            stage.setTitle("Modifier le produit");
            stage.setScene(new Scene(root));

            // Quand la fenêtre de modification se ferme, on rafraîchit la table principale
            stage.setOnHidden(event -> inventoryShowData());

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de modification.");
        }
    }


    private ObservableList<productData> invetoryListData;
    public void inventoryShowData(){
        invetoryListData = inventoryDataList();
        colproductID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colproductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        coltype.setCellValueFactory(new PropertyValueFactory<>("type"));
        colstock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colprice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colstatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.setItems(invetoryListData);
    }

    private String[] typeList = {"Meals", "Drinks"};

    public void inventoryTypeList() {
        List<String> typeL = new ArrayList<>();
        for (String data : typeList) {
            typeL.add(data);
        }
        ObservableList listData = FXCollections.observableArrayList(typeL);
        type.setItems(listData);
    }

    private String[] statusList = {"Available", "Unavailable"};

    public void inventoryStatusList() {
        List<String> statusL = new ArrayList<>();
        for (String data : statusList) {

            statusL.add(data);
        }


    ObservableList listData = FXCollections.observableArrayList(statusL);
        statusid.setItems(listData);
    // Affichage du nom d'utilisateur
}
public void displayUsername(String _username) {
        if (_username != null && !_username.trim().isEmpty()) {
            String user = _username.substring(0, 1).toUpperCase() + _username.substring(1).toLowerCase();
            username.setText(user); // Mettre à jour le label avec le nom de l'utilisateur
            System.out.println("Nom d'utilisateur affiché : " + user);
        }
    }

    // Fonction de déconnexion
    public void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent() && option.get().equals(ButtonType.OK)) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Cafe Shop Management System");
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Fermer la fenêtre actuelle (Main Form)
                    Stage currentStage = (Stage) mainform.getScene().getWindow();
                    currentStage.close();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la fenêtre : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la déconnexion.");
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour afficher les alertes
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void importImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importer une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
            );

            // Ouvrir la boîte de dialogue de sélection de fichier
            File selectedFile = fileChooser.showOpenDialog(importBtn.getScene().getWindow());

            if (selectedFile != null) {
                // Vérifier si le fichier existe avant de l'utiliser
                if (!selectedFile.exists()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier sélectionné n'existe pas.");
                    return;
                }

                // Charger l'image sélectionnée dans l'ImageView
                Image image = new Image(selectedFile.toURI().toString());
                imageid.setImage(image);

                // Assigner le chemin de l'image à 'data.path' (chemin absolu)
                data.path = selectedFile.getAbsolutePath();

                // Afficher le chemin de l'image
                System.out.println("Image importée : " + data.path);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'importer l'image : " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inventoryTypeList();
        inventoryShowData();
        inventoryStatusList();
        deletBtn.setOnAction(e -> inventoryDeleteBtn());
        updateBtn.setOnAction(e -> inventoryUpdateBtn());


        try {
            int userId = UserSession.getUserId();
            String username = UserSession.getUsername();
            displayUsername(username); // Afficher le nom d'utilisateur lorsque l'application démarre
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
