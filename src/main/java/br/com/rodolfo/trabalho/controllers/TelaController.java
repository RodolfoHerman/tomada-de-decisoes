package br.com.rodolfo.trabalho.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * TelaController
 */
public class TelaController implements Initializable {

    @FXML
    private TextArea textArea;

    @FXML
    private Button btnAnalisar;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

    @FXML
    public void btnAnalisarAction() {}

    @FXML
    public void abrirArquivos() { }

    @FXML
    public void fecharPrograma() {

        Platform.exit();
        System.exit(0);
    }


    
}