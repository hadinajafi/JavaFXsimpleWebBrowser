/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webbrowser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author hadin
 */
public class WebLayoutController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private WebEngine engine;

    private ArrayList<String> urlHistory = new ArrayList<>();
    private int iterator = 0;

    @FXML
    private ImageView imageSecurity;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private WebView webView;

    @FXML
    private TextField browseField;

    @FXML
    private Button browseBtn;

    @FXML
    private Button jsStatusBtn;

    @FXML
    private Button btnBack;

    @FXML
    private Button forwardBtn;

    @FXML
    private Button stopLoadingBtn;

    @FXML
    void enterKeyTyped(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            browseBtnClicked(null);
        }
    }

    @FXML
    void backBtnMouseClicked(MouseEvent event) {
        iterator--;
        if (iterator >= 0) {
            engine.load(urlHistory.get(iterator));
        }
        else{
            btnBack.setDisable(true);
        }

    }

    @FXML
    void forwardBtnClicked(MouseEvent event) {
        iterator++;
        if (iterator < urlHistory.size()) {
            engine.load(urlHistory.get(iterator));
        }
        else{
            forwardBtn.setDisable(true);
        }
    }

    @FXML
    void stopBtnMouseClicked(MouseEvent event) {

    }

    public void setWebEngine(WebEngine engine) {
        this.engine = engine;
    }

    public WebEngine getWebEngine() {
        return this.engine;
    }

    @FXML
    void browseBtnClicked(MouseEvent event) {
        Tooltip secureTooltip = new Tooltip("Connection is secure");
        Tooltip unsecureTooltip = new Tooltip("Connection is NOT secure!");

        engine.setJavaScriptEnabled(false);
        if (browseField.getText() == null || "".equals(browseField.getText())) {
            return;
        } else if (browseField.getText().startsWith("http://") || browseField.getText().startsWith("https://")) {
            engine.load(browseField.getText());
            //setting the connection security icon
            if (browseField.getText().startsWith("https://")) {
                imageSecurity.setImage(new Image("/icons/secure.png"));
                Tooltip.install(imageSecurity, secureTooltip);
            } else if (browseField.getText().startsWith("http://")) {
                imageSecurity.setImage(new Image("/icons/unsecure.png"));
                Tooltip.install(imageSecurity, unsecureTooltip);
            }

            //the listener will put the new page url in the browseField
//            engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
//                if (Worker.State.SUCCEEDED.equals(newValue)) {
//                    browseField.setText(engine.getLocation());
//                }
//            });
        } else if (!browseField.getText().startsWith("http://") || !browseField.getText().startsWith("https://")) {
            //engine.load("https://" + browseField.getText());
            HttpURLConnection.setFollowRedirects(true);
            try {
                URL url = new URL("https://" + browseField.getText());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    engine.load(url.toString());
                    imageSecurity.setImage(new Image("/icons/secure.png"));
                    Tooltip.install(imageSecurity, secureTooltip);
                    //the listener, will put the new url in browse field
//                    engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
//                        if (Worker.State.SUCCEEDED.equals(newValue)) {
//                            browseField.setText(engine.getLocation());
//                        }
//                    });

                } else {
                    engine.loadContent("<center>Can't find the page!</center>");
                    browseField.setText(engine.getLocation());
                }
            } catch (MalformedURLException ex) {
                ex.getMessage();
                System.out.println("webbrowser.WebLayoutController.browseBtnClicked()");
            } catch (IOException ex) {
                ex.getMessage();
                //the protocol is not https
                URL unsecurl;
                try {
                    unsecurl = new URL("http://" + browseField.getText());
                    HttpURLConnection secureCon = (HttpURLConnection) unsecurl.openConnection();
                    secureCon.connect();
                    if (secureCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        engine.load(unsecurl.toString());
                        imageSecurity.setImage(new Image("/icons/unsecure.png"));
                        Tooltip.install(imageSecurity, unsecureTooltip);
//                        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
//                            if (Worker.State.SUCCEEDED.equals(newValue)) {
//                                browseField.setText(engine.getLocation());
//                            }
//                        });
                    }
                } catch (MalformedURLException ex1) {
                    ex.getMessage();
                } catch (IOException ex1) {
                    ex.getMessage();
                    engine.loadContent("<center>Can't find the page</center>");
                }

            }
        }

        //browseField.setText(engine.getLocation());
        if (browseField.getText().startsWith("http://")) {
            imageSecurity.setImage(new Image("/icons/unsecure.png"));
            Tooltip.install(imageSecurity, unsecureTooltip);
        } else if (browseField.getText().startsWith("https://")) {
            imageSecurity.setImage(new Image("/icons/secure.png"));
            Tooltip.install(imageSecurity, secureTooltip);
        }

    }

    @FXML
    void jsBtnEnableDisable(MouseEvent event) {
        if (engine.isJavaScriptEnabled()) {
            engine.setJavaScriptEnabled(false);
            engine.reload();
            jsStatusBtn.setGraphic(new ImageView(new Image("/icons/jsd.png")));
        } else if (!engine.isJavaScriptEnabled()) {
            engine.setJavaScriptEnabled(true);
            jsStatusBtn.setGraphic(new ImageView(new Image("/icons/jse.png")));
            engine.reload();
        }
    }

    private void addToHistory(String url) {
        if (!urlHistory.contains(url)) {
            urlHistory.add(url);
            //enable disabled buttons
            forwardBtn.setDisable(false);
            btnBack.setDisable(false);
            iterator = urlHistory.size()-1;
        }
        System.out.println(urlHistory.toString());
        System.out.println(iterator);
    }
    
    //initialize images
    private ArrayList<ImageView> initializeImages(){  
        ImageView jsImg = new ImageView(new Image("/icons/jsd.png"));
        
        ImageView backImg = new ImageView(new Image("/icons/arrowleft2.png"));
        backImg.setFitHeight(16);
        backImg.setFitWidth(16);
        
        ImageView forwardImg = new ImageView(new Image("/icons/arrowright2.png"));
        forwardImg.setFitHeight(16);
        forwardImg.setFitWidth(16);
        
        ImageView stopImage = new ImageView(new Image("/icons/arrowclose.png"));
        stopImage.setFitHeight(16);
        stopImage.setFitWidth(16);
        
        ArrayList<ImageView> list = new ArrayList<>();
        list.add(jsImg);
        list.add(backImg);
        list.add(forwardImg);
        list.add(stopImage);
        return list;
    }
    
    //set images to the buttons
    private void initialzeButtons(){
        ArrayList<ImageView> imageList = initializeImages();
        jsStatusBtn.setGraphic(imageList.get(0));
        jsStatusBtn.setTooltip(new Tooltip("Enable/disable javascript"));
        btnBack.setGraphic(imageList.get(1));
        btnBack.setTooltip(new Tooltip("Go one page back"));
        forwardBtn.setGraphic(imageList.get(2));
        forwardBtn.setTooltip(new Tooltip("Go one page forward"));
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            browseField.requestFocus();
        });
        
        //change progressbar color to blue
        progressbar.setStyle("-fx-accent: #2176CB");
        //set images to the buttons:
        initialzeButtons();
        //load default duckduckgo.org search engine on startup.
        engine = webView.getEngine();
        engine.setJavaScriptEnabled(false);
        engine.load("https://duckduckgo.org");

//        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
//            if (Worker.State.SUCCEEDED.equals(newValue)) {
//                browseField.setText(engine.getLocation());
//            }
//        });
        //listener for location changing of the webview. set location on browse field
        engine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            browseField.setText(engine.getLocation());
            
        });
        
        engine.getLoadWorker().progressProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            progressbar.setProgress(engine.getLoadWorker().getProgress());
            if (progressbar.getProgress() == 1.0) {
                addToHistory(engine.getLocation());
                //when loading page completed, change progress bar color to green
                progressbar.setStyle("-fx-accent: #31B131");
            } else {
                progressbar.setStyle("-fx-accent: #2176CB");
            }
        });

    }

}
