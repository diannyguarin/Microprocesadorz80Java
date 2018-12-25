/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

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
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select an text file");
        jfc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
		jfc.addChoosableFileFilter(filter);


		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
			try {
				BufferedReader br = new BufferedReader(new FileReader(selectedFile));
				String line = null;
				 while ((line = br.readLine()) != null) {
				   System.out.println(line);
				 }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("File not found");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
