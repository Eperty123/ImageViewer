package gui;

import bll.SlideshowInstanceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Main instance;
    private static SlideshowInstanceManager slideshowInstanceManager = SlideshowInstanceManager.getInstance();
    private final static String mainTitle = "Image Viewer";

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        slideshowInstanceWatcher();

        // Start a new instance of the image viewer.
        startInstance(primaryStage);
    }

    private void slideshowInstanceWatcher() {
        slideshowInstanceManager.start();
    }

    public Object startInstance(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ImageViewerWindow.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle(mainTitle);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        slideshowInstanceManager.addControllerInstance(loader.getController(), primaryStage);
        return loader.getController();
    }

    public Object startNewInstance() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ImageViewerWindow.fxml"));
        Parent root = loader.load();

        var stage = new Stage();
        stage.setTitle(mainTitle);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();

        slideshowInstanceManager.addControllerInstance(loader.getController(), stage);
        //setPrimaryStage(stage);
        return loader.getController();
    }

    private void setPrimaryStage(Stage stage) {
        if (primaryStage != stage && stage != null)
            primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Main getInstance() {
        return instance;
    }

    public SlideshowInstanceManager getSlideshowInstanceManager() {
        return slideshowInstanceManager;
    }

    public static String getMainTitle() {
        return mainTitle;
    }

}
