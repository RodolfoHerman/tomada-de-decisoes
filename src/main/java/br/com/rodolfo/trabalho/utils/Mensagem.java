package br.com.rodolfo.trabalho.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.StageStyle;

/**
 * Mensagem
 */
public class Mensagem {

    private Mensagem() {}

    public static void mostrarInformacao(String titulo, String cabecalho, String mesangem) {
        
        Alert alert = new Alert(AlertType.INFORMATION);
        
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);

        TextArea textArea = new TextArea(mesangem);

        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane conteudo = new GridPane();

        conteudo.setMaxWidth(Double.MAX_VALUE);
        conteudo.add(textArea, 0, 0);

        alert.getDialogPane().setContent(conteudo);
        alert.showAndWait();
    }
}