package cafeshopmanagementsystem;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FXMLDocumentController implements Initializable {

    @FXML private AnchorPane side_createForm, su_loginForm, su_loginForm1, fp_questionform, rp_newpassform;
    @FXML private TextField su_surname, su_answer, surname, fp_username, fp_answer;
    @FXML private PasswordField su_password, su_password1, rp_newpassword, rp_confirmpassword;
    @FXML private Button side_createbtn, su_loginBtn, su_loginBtn1, side_alreadyhave, fp_back, fp_proceed, rp_back, rp_changepassword;
    @FXML private Hyperlink su_forgetPsword;
    @FXML private ComboBox<String> su_question;
    @FXML private ComboBox<String> fp_question;
    @FXML private ImageView image;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private final String[] questionList = {
            "What is your favorite Color?",
            "What's your favorite food?",
            "What's your birth date?"
    };


    public void regLquestionList() {
        if (su_question == null) {
            System.err.println("su_question is not initialized!");
            return;
        }

        // Crée une ObservableList à partir du tableau questionList
        ObservableList<String> observableQuestionList = FXCollections.observableArrayList(questionList);
        su_question.setItems(observableQuestionList);
        System.out.println("Data filled");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Vérifie que su_question est bien initialisé

            regLquestionList();

            //System.err.println("su_question is not initialized! from main init");

    }
    // LOGIN BUTTON
    @FXML
    public void su_loginBtn() throws IOException {
        if (surname.getText().isEmpty() || su_password.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        String selectData = "SELECT id,surname, password FROM employee WHERE surname = ? AND password = ?";
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(selectData);
            prepare.setString(1, surname.getText());
            prepare.setString(2, su_password.getText());

            result = prepare.executeQuery();

            if (result.next()) {
                int userId = result.getInt("id");
                String username = result.getString("surname");
                UserSession.setUserId(userId);
                UserSession.setUsername(username);
                System.out.println(userId + username);

                System.out.println(result);
                data.username =su_surname.getText();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Connexion réussie !");
                
                // Load new window
try {
    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainForm.fxml")));
    Stage stage = new Stage();
    stage.setTitle("Cafe Shop Management System");
    stage.setScene(new Scene(root));
    stage.show();

    // Fermer la fenêtre actuelle
    Stage currentStage = (Stage) su_loginBtn.getScene().getWindow();
    currentStage.close();
} catch (IOException e) {
    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger mainForm.fxml\n" + e.getMessage());
    e.printStackTrace();
}

            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nom d'utilisateur ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }

    // REGISTRATION
    @FXML
    public void regBtn() {
        if (su_surname.getText().isEmpty() || su_password1.getText().isEmpty()
                || su_question.getSelectionModel().isEmpty() || su_answer.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (su_password1.getText().length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        String regData = "INSERT INTO employee (surname, password, question, answer, date) VALUES (?, ?, ?, ?, ?)";
        connect = database.connectDB();

        try {
            prepare = connect.prepareStatement(regData);
            prepare.setString(1, su_surname.getText());
            prepare.setString(2, su_password1.getText());
            prepare.setString(3, su_question.getSelectionModel().getSelectedItem());
            prepare.setString(4, su_answer.getText());
            prepare.setDate(5, new java.sql.Date(new java.util.Date().getTime()));

            prepare.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Compte créé avec succès !");

            su_surname.clear();
            su_password1.clear();
            su_question.getSelectionModel().clearSelection();
            su_answer.clear();

            TranslateTransition slider = new TranslateTransition(Duration.seconds(0.5), side_createForm);
            slider.setToX(0);
            slider.play();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    // POPULATE QUESTION COMBOBOX

    // FORGOT PASSWORD
    @FXML
    public void switchForgotPass() {
        su_loginForm.setVisible(false);
        fp_questionform.setVisible(true);
    }

    @FXML
    public void proceedBtn() {
        if (fp_username.getText().isEmpty() || fp_question.getSelectionModel().isEmpty() || fp_answer.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        String selectData = "SELECT * FROM employee WHERE surname = ? AND question = ? AND answer = ?";
        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(selectData);
            prepare.setString(1, fp_username.getText());
            prepare.setString(2, fp_question.getSelectionModel().getSelectedItem());
            prepare.setString(3, fp_answer.getText());

            result = prepare.executeQuery();
            if (result.next()) {
                fp_questionform.setVisible(false);
                rp_newpassform.setVisible(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Informations incorrectes.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }

    @FXML
    public void changePassBtn() {
        if (rp_newpassword.getText().isEmpty() || rp_confirmpassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (!rp_newpassword.getText().equals(rp_confirmpassword.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        String updatePass = "UPDATE employee SET password = ? WHERE surname = ?";
        try {
            connect = database.connectDB();
            prepare = connect.prepareStatement(updatePass);
            prepare.setString(1, rp_newpassword.getText());
            prepare.setString(2, fp_username.getText());

            int rows = prepare.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe mis à jour !");
                backToLoginForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", e.getMessage());
        }
    }

    @FXML
    public void backToLoginForm() {
        rp_newpassword.clear();
        rp_confirmpassword.clear();
        fp_username.clear();
        fp_answer.clear();
        fp_question.getSelectionModel().clearSelection();

        rp_newpassform.setVisible(false);
        fp_questionform.setVisible(false);
        su_loginForm.setVisible(true);
    }

    @FXML
    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition(Duration.seconds(0.5), side_createForm);
        if (event.getSource() == side_createbtn) {
            slider.setToX(300);
            side_alreadyhave.setVisible(true);
            side_createbtn.setVisible(false);
        } else if (event.getSource() == side_alreadyhave) {
            slider.setToX(0);
            side_alreadyhave.setVisible(false);
            side_createbtn.setVisible(true);
        }
        slider.play();
    }

    // UTILITAIRE
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
