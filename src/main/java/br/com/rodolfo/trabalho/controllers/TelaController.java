package br.com.rodolfo.trabalho.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import br.com.rodolfo.trabalho.StartApp;
import br.com.rodolfo.trabalho.algorithms.JOEL;
import br.com.rodolfo.trabalho.configs.Configuracoes;
import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Restricao;
import br.com.rodolfo.trabalho.services.ObjetivoService;
import br.com.rodolfo.trabalho.services.RestricaoService;
import br.com.rodolfo.trabalho.utils.Metodos;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;

/**
 * TelaController
 */
public class TelaController implements Initializable {

    @FXML
    private TextArea textArea;

    @FXML
    private Button btnAnalisar;

    // Variaveis locais
    private DirectoryChooser directoryChooser;
    private List<Objetivo> objetivos;
    private List<Restricao> restricoes;
    private Configuracoes configuracoes;
    private final String PROPERTIES = "config.properties";
    // Services
    private ObjetivoService objetivoService;
    private RestricaoService restricaoService;

    @Override
	public void initialize(URL location, ResourceBundle resources) {
        
        this.objetivos  = new ArrayList<>();
        this.restricoes = new ArrayList<>();
        this.configuracoes = new Configuracoes();
        this.objetivoService  = new ObjetivoService();
        this.restricaoService = new RestricaoService();
        // this.textArea.setStyle("-fx-font-family: monospace");

        Properties properties = new Properties();

        try(InputStream is = TelaController.class.getClassLoader().getResourceAsStream(PROPERTIES)) {

            properties.load(is);

            this.configuracoes.objetivoInstancias = properties.getProperty("objetivo.instancias");
            this.configuracoes.restricaoInstancias = properties.getProperty("restricao.instancias");

        } catch(Exception e) {

            this.textArea.setText("ERRO (contactar o criador do programa) -> " + e.toString());
        }

	}

    @FXML
    public void btnAnalisarAction() {

        JOEL joel = new JOEL(7, objetivos, restricoes);

        this.textArea.setText(joel.execute());
    }

    @FXML
    public void abrirArquivos() { 

        DirectoryChooser chooser = getDirectoryChooser();
        File file = chooser.showDialog(StartApp.mainStage);
        this.textArea.clear();

        if(file != null) {

            if(Metodos.verificarArquivos(this.configuracoes.arquivosParaLista(), file.list())) {

                abrirArquivosJSON(file.getAbsolutePath());

            } else {

                this.textArea.setText("ERRO. Diretório não possui os arquivos JSON necessários. ('objetivos.json' ou 'restricoes.json').");
            }

            chooser.setInitialDirectory(file);

        } else {

            this.textArea.setText("ERRO. Impossível localizar o diretório.");
        }

    }

    @FXML
    public void fecharPrograma() {

        Platform.exit();
        System.exit(0);
    }


    // Metodos locais
    private DirectoryChooser getDirectoryChooser() {
        
        if (directoryChooser == null) {

            directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Abrir diretório");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        return directoryChooser;
    }

    private void abrirArquivosJSON(String caminho) {

        this.objetivos.clear();
        this.restricoes.clear();

        try {

            this.objetivos.addAll(this.objetivoService.buscarInstancias(caminho.concat("//").concat(configuracoes.objetivoInstancias)));
            this.restricoes.addAll(this.restricaoService.buscarInstancias(caminho.concat("//").concat(configuracoes.restricaoInstancias)));

        } catch (IOException e) {
            
            this.textArea.setText("ERRO (contactar o criador do programa) -> " + e.toString());
        } 

    }

    
}