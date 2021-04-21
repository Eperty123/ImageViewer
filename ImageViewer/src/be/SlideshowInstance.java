package be;

import bll.SlideshowManager;
import gui.controller.ImageViewerWindowController;
import javafx.stage.Stage;

public class SlideshowInstance {
    private long id;
    private ImageViewerWindowController controller;
    private Stage stage;
    private boolean active;

    public SlideshowInstance() {

    }

    public SlideshowInstance(long id, ImageViewerWindowController controller, Stage stage) {
        setId(id);
        setController(controller);
        setStage(stage);
    }

    public SlideshowInstance(ImageViewerWindowController controller, Stage stage) {
        setController(controller);
        setStage(stage);
    }

    /**
     * Get the id of the slideshow instance.
     *
     * @return Returns the id.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id of the slideshow instance.
     *
     * @param id The id to use.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the slideshow instance's controller.
     *
     * @return Returns the image viewer controller.
     */
    public ImageViewerWindowController getController() {
        return controller;
    }

    /**
     * Get the slideshow instance's slideshow manager.
     *
     * @return Returns the slideshow manager.
     */
    public SlideshowManager getSlideshowManager() {
        return getController().getSlideShowManager();
    }

    /**
     * Set the slideshow instance's controller.
     *
     * @param controller The controller to use.
     */
    public void setController(ImageViewerWindowController controller) {
        this.controller = controller;
        this.controller.setSlideshowInstance(this);
    }

    /**
     * Get the slideshow instance's stage.
     *
     * @return Returns the stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Set the slideshow instance's stage.
     *
     * @param stage The stage to use.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Is the slideshow instsance currently active and performing slideshow view?
     *
     * @return Returns true if yes otherwise false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the slideshow instance's active status.
     *
     * @param active The state to use.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}
