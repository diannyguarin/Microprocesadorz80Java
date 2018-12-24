/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import Emulador.*;
/**
 *
 * @author Diana
 */
public class FXMLVistaController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        /*
        Z80 z = new Z80();
        System.out.println(z.getA());
        System.out.println(Integer.parseInt(z.getA(),2));
        z.ADD(Integer.toBinaryString(7));
        System.out.println(z.getA());
        System.out.println(Integer.parseInt(z.getA(),2));
        z.SUB(Integer.toBinaryString(10));
        System.out.println(z.getA());
        z.ADD(Integer.toBinaryString(5));
        System.out.println(z.getA());
        System.out.println(Integer.parseInt(z.getA(),2));
                */
    }    
    
}
