package br.com.rodolfo.trabalho.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import br.com.rodolfo.trabalho.StartApp;
import br.com.rodolfo.trabalho.algorithms.JOEL;
import br.com.rodolfo.trabalho.configs.Configuracoes;
import br.com.rodolfo.trabalho.models.Objetivo;
import br.com.rodolfo.trabalho.models.Restricao;
import br.com.rodolfo.trabalho.services.ObjetivoService;
import br.com.rodolfo.trabalho.services.RestricaoService;
import br.com.rodolfo.trabalho.utils.Mensagem;
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
    // Informações
    public final String INF_AUTOR = "Desenvolvido por Rodolfo Herman Lara e Silva, "
                + "mestrando em Engenharia Elétrica (2018) e integrante do Laboratório de Análise e Tratamento de Imagens (LATIM) "
                + "na Pontifícia Universidade Católica de Minas Gerais (PUC Minas). "
                + "Recebeu o título de Bacharel em Ciência da Computação pela PUC Minas em 2016."
                + "\n\nE-mail : ciencia.rodolfo@gmail.com\nLinkedin : https://br.linkedin.com/in/rodolfoherman";

    public final String INF_PROG = "Este programa é uma implementação do artigo : \nOn multicriteria decision"
            + "making under conditions of uncertainty\n\nAutores: \nPereira Jr, J.\nEkel, P.\nPalhasres, R\nParreiras, R.\n\n"
            + "Implementação feita por : Rodolfo Herman Lara e Silva\n"
            + "E-mail : ciencia.rodolfo@gmail.com";

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
        
        this.textArea.clear();
        validarCampos();

        if(this.ERROS.isEmpty()) {

            this.btnAnalisar.setDisable(true);

            double hurwicz = Double.valueOf(this.hurwiczTextField.getText().replaceAll(",", "."));
            double passos  = Double.valueOf(this.passosTextField.getText().replaceAll(",", "."));
            int cenarios   = Double.valueOf(this.cenariosTexteField.getText().replaceAll(",", ".")).intValue();
            
            JOEL joel = new JOEL(hurwicz, passos, cenarios, objetivos, restricoes);

            this.barraProgresso.progressProperty().bind(joel.progressProperty());

            joel.setOnSucceeded(Event -> {

                resetar();

                try {

                    this.textArea.setText(joel.get());

                } catch (InterruptedException | ExecutionException e) {
                 
                    this.textArea.setText("Erro ao processar. Entrar em contato com o desenvolvedor!");
				}

            });

            Thread thread = new Thread(joel);
            thread.setDaemon(true);
            thread.start();

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

    @FXML
    public void mostrarInformacaoAutor() {
        
        Mensagem.mostrarInformacao("Sobre o autor", "Informações do autor", this.INF_AUTOR);
    }

    @FXML
    public void mostrarInformacaoPrograma() {
        
        Mensagem.mostrarInformacao("Sobre o programa", "Informações do programa", this.INF_PROG);
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