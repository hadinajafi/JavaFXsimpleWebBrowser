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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.net.ssl.HttpsURLConnection;
import sun.net.www.protocol.https.HttpsURLConnectionImpl;

/**
 * FXML Controller class
 *
 * @author hadin
 */
public class WebLayoutController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private boolean jsEnabled = false;
    private WebEngine engine;

    @FXML
    private ImageView imageSecurity;

    @FXML
    private WebView webView;

    @FXML
    private TextField browseField;

    @FXML
    private Button browseBtn;

    @FXML
    private Button jsStatusBtn;

    @FXML
    void enterKeyTyped(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            browseBtnClicked(null);
        }
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
            //the listener will put the new page url in the browseField
            engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                        if (Worker.State.SUCCEEDED.equals(newValue)) {
                            browseField.setText(engine.getLocation());
                        }
                    });
        } else if (!browseField.getText().startsWith("http://") || !browseField.getText().startsWith("https://")) {
            //engine.load("https://" + browseField.getText());
            HttpURLConnection.setFollowRedirects(true);
            try {
                URL url = new URL("https://" + browseField.getText());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.connect();
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    engine.load(url.toString());
                    //the listener, will put the new url in browse field
                    engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                        if (Worker.State.SUCCEEDED.equals(newValue)) {
                            browseField.setText(engine.getLocation());
                        }
                    });

                } else {
                    engine.loadContent("<center>Can't find the page!</center>");
                    browseField.setText(engine.getLocation());
                }
            } catch (MalformedURLException ex) {
                ex.getMessage();
                System.out.println("webbrowser.WebLayoutController.browseBtnClicked()");
            } catch (IOException ex) {
                ex.getMessage();
                System.out.println("IOEXception");
                URL securl;
                try {
                    securl = new URL("http://" + browseField.getText());
                    HttpURLConnection secureCon = (HttpURLConnection) securl.openConnection();
                    secureCon.connect();
                    if (secureCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        engine.load(securl.toString());
                        browseField.setText(securl.toString());
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
        if (browseField.getText().contains("http://")) {
            imageSecurity.setImage(new Image("/icons/unsecure.png"));
            Tooltip.install(imageSecurity, unsecureTooltip);
        } else if (browseField.getText().contains("https://")) {
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            browseField.requestFocus();
        });
        Image js = new Image("/icons/jsd.png");
        jsStatusBtn.setGraphic(new ImageView(js));
        engine = webView.getEngine();
        engine.setJavaScriptEnabled(false);
        engine.load("https://duckduckgo.org");
    }

}
