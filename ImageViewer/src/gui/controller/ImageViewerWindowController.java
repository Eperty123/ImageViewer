package gui.controller;

import be.Slide;
import be.SlideshowInstance;
import bll.Logger;
import bll.SlideshowManager;
import gui.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ImageViewerWindowController implements Initializable {
    private SlideshowManager slideShowManager;
    private Main main = Main.getInstance();
    private SlideshowInstance slideshowInstance;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;
    @FXML
    private TextArea slideshowDelayTimeTxtBox;
    @FXML
    private Slider slideshowDelayTimeSlider;
    @FXML
    private Button btnNewSlideshowInstance;
    @FXML
    private Button btnStartSlideshow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slideShowManager = new SlideshowManager(imageView);
        registerEvents();
        initializeValues();
    }

    private void initializeValues() {
        slideshowDelayTimeTxtBox.setText(String.format("%.2f", slideshowDelayTimeSlider.getValue()));
    }

    private void registerEvents() {
        // Register slider value and listen for the change event.
        slideshowDelayTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            slideshowDelayTimeTxtBox.setText(String.format("%.2f", newVal));
            slideShowManager.setSlideShowDelay((double) newVal);
        });

        // Register text change event.
        slideshowDelayTimeTxtBox.textProperty().addListener((obs, oldVal, newVal) -> {
            var sliderVal = Double.parseDouble(newVal.replace(",", "."));
            slideshowDelayTimeSlider.setValue(sliderVal);
        });

        // Register slideshow change event.
        slideShowManager.valueProperty().addListener((obs, oldMessage, newMessage) -> {

            // Handle slide change.
            if (newMessage.getClass().equals(Slide.class))
                slideshowInstance.getStage().setTitle(String.format("%s - %s", Main.getMainTitle(), ((Slide) newMessage).getFileName()));
        });

        // Register message change event.
        slideShowManager.messageProperty().addListener((obs, oldMessage, newMessage) -> {

            // Handle slide change.
            switch (newMessage) {
                case SlideshowManager.cannotStartMessage:
                case SlideshowManager.slideshowStoppedMessage:
                    updateButtonText(btnStartSlideshow, "Start Slideshow");
                    break;

                case SlideshowManager.canStartMessage:
                case SlideshowManager.slideshowStartedMessage:
                    updateButtonText(btnStartSlideshow, "Stop Slideshow");
                    break;
            }
            Logger.getInstance().log(newMessage);
        });

        registerButtonEvents();
    }

    private void registerButtonEvents() {
        btnStartSlideshow.addEventHandler(MouseEvent.MOUSE_PRESSED, (x) -> {
            if (x.getButton() == MouseButton.PRIMARY) {

                if (!slideShowManager.isSlideshowStarted())
                    startSlideshow();
                else if (slideShowManager.isPaused())
                    unpauseSlideshow();
                else if (!slideShowManager.isPaused())
                    pauseSlideshow();
                else
                    stopSlideshow();

                Logger.getInstance().log(Boolean.toString(slideShowManager.isSlideshowStarted()));
            }
        });
    }

    @FXML
    private void handleBtnLoadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (files != null && !files.isEmpty()) {
            files.forEach((File f) ->
            {
                slideShowManager.addSlide(f.getPath());
            });
            displayImage();
        }
    }

    @FXML
    private void handleBtnPreviousAction() {
        slideShowManager.previous();
    }

    @FXML
    private void handleBtnNextAction() {
        slideShowManager.next();
    }

    private void displayImage() {
        var slide = slideShowManager.showSlide();
        if (slide != null && slideshowInstance != null)
            slideshowInstance.getStage().setTitle(String.format("%s - %s", Main.getMainTitle(), slide.getFileName()));
    }

    @FXML
    private void handleBtnStartNewInstance() throws IOException {
        main.startNewInstance();
    }

    @FXML
    private void startSlideshow() {
        if (!slideShowManager.isSlideshowStarted())
            slideShowManager.start();
        //main.getSlideshowInstanceManager().setActiveSlideshow(slideshowInstance);
    }

    @FXML
    private void stopSlideshow() {
        if (slideShowManager != null) {
            slideShowManager.stop();
        }
    }

    @FXML
    private void pauseSlideshow() {
        if (slideShowManager != null) {
            slideShowManager.pause();
        }
    }

    private void unpauseSlideshow() {
        if (slideShowManager != null)
            slideShowManager.unpause();
    }

    public SlideshowManager getSlideShowManager() {
        return slideShowManager;
    }

    public void updateButtonText(Button btn, String txt) {
        if (btn != null) btn.setText(txt);
    }


    public SlideshowInstance getSlideshowInstance() {
        return slideshowInstance;
    }

    public void setSlideshowInstance(SlideshowInstance slideshowInstance) {
        this.slideshowInstance = slideshowInstance;
    }

    public void appendTextToTile(String text) {
        var stage = getSlideshowInstance().getStage();
        stage.setTitle(stage.getTitle() + " " + text);
    }
}
