/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Scanner;
import Emulador.*;

/**
 *
 * @author Diana
 */
public class Z80Interfaz extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLVista.fxml"));

        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("./styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
       Ensamblador e = new Ensamblador();
        Scanner s = new Scanner(System.in);
        System.out.print("Digite Nombre archivo .txt:");
        String fileName = s.nextLine();
        if(fileName.endsWith(".txt")){
        if(fileName != null){
        e.readFile(fileName);
        }else{
            System.out.println("NO DIGITO NINGUN NOMBRE");
            System.exit(-1);
        }
        }else{
        System.out.println("EL ARCHIVO NO EXISTE O NO ES .txt!");
            System.exit(-1);
        }
               
//launch(args);
    }

}
