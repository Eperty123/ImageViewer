package be;

import javafx.scene.image.Image;

import java.io.File;

public class Slide {

    private String fileName;
    private File file;
    private Image image;
    private boolean loaded;

    public Slide() {

    }

    public Slide(String filePath) {
        setFilePath(filePath);
    }

    /**
     * Get the file name of the image.
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name of the image. (Only changes the file name variable and not the file's file name itself.)
     * @param fileName
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the file path of the image.
     * @return
     */
    public String getFilePath() {
        return file.toURI().getPath();
    }

    /**
     * Set the file path of the image and load it.
     * @param filePath The file path of the image to load.
     */
    public void setFilePath(String filePath) {
        file = new File(filePath);
        if (file.exists()) {
            setFileName(file.getName());
            setImage(new Image(file.toURI().toString()));
            loaded = true;
        } else loaded = false;
    }

    /**
     * Get the image instance.
     * @return Returns null if no image instance is instantiated.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set the image.
     * @param image The image to replace with.
     */
    protected void setImage(Image image) {
        this.image = image;
    }

    /**
     * Is the image loaded?
     * @return Returns true if yes otherwise false.
     */
    public boolean isLoaded() {
        return loaded;
    }
}
