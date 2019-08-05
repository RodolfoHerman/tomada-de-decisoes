package br.com.rodolfo.trabalho.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 * TelaController
 */
public class TelaController implements Initializable {

    @FXML
    private TextArea textArea;

    @FXML
    private Button btnAnalisar;

    @FXML
    private TextField hurwiczTextField;
    
    @FXML
    private TextField cenariosTexteField;
    
    @FXML
    private TextField passosTextField;

    @FXML
    private ProgressBar barraProgresso;

    // Variaveis locais
    private DirectoryChooser directoryChooser;
    private List<Objetivo> objetivos;
    private List<Restricao> restricoes;
    private Configuracoes configuracoes;
    private final String PROPERTIES = "config.properties";
    private final List<String> ERROS = new ArrayList<>();
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
        
        validarCampos();

        if(this.ERROS.isEmpty()) {

            resetar();
            this.btnAnalisar.setDisable(true);
            
            JOEL joel = new JOEL(7, objetivos, restricoes);

            this.textArea.setText(joel.execute());

        } else {

            this.textArea.setText(this.ERROS.stream().collect(Collectors.joining(System.lineSeparator())));
        }
        
    }

    private void validarCampos() {

        this.ERROS.clear();
        
        String hurwiczTexto  = this.hurwiczTextField.getText().replaceAll(",", ".");
        String cenariosTexto = this.cenariosTexteField.getText().replaceAll(",", ".");
        String passosTexto   = this.passosTextField.getText().replaceAll(",", ".");

        if(!Metodos.isNumero(hurwiczTexto)) {

            this.ERROS.add("Erro! Campo 'HURWICZ' aceita somente números!");
        
        } else if(Double.valueOf(hurwiczTexto) < 0.0 || Double.valueOf(hurwiczTexto) > 1.0){

            this.ERROS.add("Erro! Campo 'HURWICZ' deve ser um número entre 0 e 1!");
        }

        if(!Metodos.isNumero(cenariosTexto)) {

            this.ERROS.add("Erro! Campo 'CENÁRIOS' aceita somente números!");

        } else if(Double.valueOf(cenariosTexto) <= 0.0) {

            this.ERROS.add("Erro! Campo 'CENÁRIOS' deve ser um número maior do que zero!");
        }

        if(!Metodos.isNumero(passosTexto)) {

            this.ERROS.add("Erro! Campo 'PASSOS' aceita somente números!");

        } else if(Double.valueOf(passosTexto) <= 0.0) {

            this.ERROS.add("Erro! Campo 'PASSOS' deve ser um número maior do que zero!");
        }

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

            resetar();

        } catch (IOException e) {
            
            this.textArea.setText("ERRO (contactar o criador do programa) -> " + e.toString());
        } 

    }

    private void resetar() {
        
        this.barraProgresso.progressProperty().unbind();
        this.btnAnalisar.disableProperty().unbind();
        this.hurwiczTextField.disableProperty().unbind();
        this.cenariosTexteField.disableProperty().unbind();
        this.passosTextField.disableProperty().unbind();

        this.barraProgresso.setProgress(0.0);
        this.btnAnalisar.setDisable(false);
        this.hurwiczTextField.setDisable(false);
        this.cenariosTexteField.setDisable(false);
        this.passosTextField.setDisable(false);
    }

    
}