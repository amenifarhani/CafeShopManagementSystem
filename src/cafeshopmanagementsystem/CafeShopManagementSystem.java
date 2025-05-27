/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cafeshopmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author use
 */
public class CafeShopManagementSystem extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        stage.setTitle("Cafe Shop Management System");
        stage.setMaxHeight(410);
        stage.setMinWidth(610);
        
        stage.setScene(scene);
        stage.show();
    }
    private static boolean testDatabaseConnection() {
        try (Connection conn = database.connectDB()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error during database connection check: " + e.getMessage());
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (testDatabaseConnection()) {
            System.out.println("Database connection successful.");
            launch(args);
        } else {
            System.err.println("Failed to connect to the database. Application will exit.");
            System.exit(1);
        }
    }


}
