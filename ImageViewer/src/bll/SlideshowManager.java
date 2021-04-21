package bll;

import be.Slide;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlideshowManager extends Task {

    private List<Slide> images = new ArrayList<>();
    private int currentSlideIndex = 0;
    private double slideShowDelay = 1.0;
    private double currentSlideShowDelay;
    private ImageView imageView;
    private ExecutorService executorService;
    private boolean paused;

    private static SlideshowManager instance;

    public static final String canStartMessage = "canStartSlideshow";
    public static final String cannotStartMessage = "cannotStartSlideshow";
    public static final String slideshowStartedMessage = "slideshowStarted";
    public static final String slideshowStoppedMessage = "slideshowStopped";

    public SlideshowManager() {
        initialize();
    }

    private void initialize() {

    }

    public SlideshowManager(ImageView imageView) {
        initialize();
        setImageView(imageView);
    }

    public SlideshowManager(ImageView imageView, ArrayList<Slide> images) {
        initialize();
        setImageView(imageView);
        addSlides(images);
    }

    /**
     * Add a slide given by a file path.
     *
     * @param path The path to image.
     */
    public void addSlide(String path) {
        var file = new File(path);
        if (file.exists()) {
            // Proceed to add this file.
            images.add(new Slide(path));
        } else
            System.out.println(String.format("The file %s doesn't exist! You sure you're okay bro????", file.getPath()));
    }

    /**
     * Add an array of slides.
     *
     * @param slides The array of slides to add.
     */
    public void addSlides(ArrayList<Slide> slides) {
        if (slides != null && !slides.isEmpty())
            images.addAll(slides);
    }

    /**
     * Get the current slide.
     *
     * @return Returns null if no slide is found.
     */
    public Slide getCurrentSlide() {
        if (hasSlide(currentSlideIndex)) {
            var slide = images.get(currentSlideIndex);
            setImage(slide.getImage());
            return slide;
        }
        return null;
    }

    /**
     * Remove a slide given by a file path.
     *
     * @param path The path of the slide.
     * @return Returns true if removed otherwise false.
     */
    public boolean removeSlide(String path) {
        if (hasSlide(path)) {
            images.remove(getSlide(path));
            return true;
        }
        return false;
    }

    /**
     * Remove a slide given by a file path.
     *
     * @param index The index of the slide. (This should refer to the index in the image array.)
     * @return Returns true if removed otherwise false.
     */
    public boolean removeSlide(int index) {
        if (hasSlide(index)) {
            images.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Does a slide with the specified file path exist?
     *
     * @param path The file path of the desired slide.
     * @return Returns true if exists otherwise false.
     */
    public boolean hasSlide(String path) {
        return getSlide(path) != null;
    }

    /**
     * Does a slide with the specified index exist?
     *
     * @param index The index of the desired slide. (This should refer to the index in the images array.)
     * @return Returns true if exists otherwise false.
     */
    public boolean hasSlide(int index) {
        return getSlide(index) != null;
    }

    /**
     * Get a slide with the specified file path.
     *
     * @param path The file path of the desired slide.
     * @return Returns null if no slide of said path is found.
     */
    public Slide getSlide(String path) {
        for (int i = 0; i < images.size(); i++) {
            var image = images.get(i);
            if (image.getFilePath().equals(path))
                return image;
        }
        return null;
    }

    /**
     * Get a slide with the specified file path.
     *
     * @param index The index the desired slide. (This should refer to the index in the image array.)
     * @return Returns null if no slide of said path is found.
     */
    public Slide getSlide(int index) {
        if (images.size() > 0 && index <= images.size())
            return images.get(index);
        return null;
    }

    /**
     * Set the slideshow delay time. (Max 5.)
     *
     * @param slideShowDelay The slide show delay in seconds.
     */
    public void setSlideShowDelay(double slideShowDelay) {
        if (slideShowDelay > 5) slideShowDelay = 5.0;
        this.slideShowDelay = slideShowDelay;
    }

    /**
     * Show the next slide.
     *
     * @return Returns the next slide.
     */
    public synchronized Slide next() {
        currentSlideIndex = (currentSlideIndex + 1) % images.size();
        var slide = images.get(currentSlideIndex);
        setImage(slide.getImage());
        return slide;
    }

    /**
     * Show the previous slide.
     *
     * @return Returns the previous slide.
     */
    public synchronized Slide previous() {
        currentSlideIndex = (currentSlideIndex - 1 + images.size()) % images.size();
        var slide = images.get(currentSlideIndex);
        setImage(slide.getImage());
        return slide;
    }

    /**
     * Show the slide in the image view.
     *
     * @return Returns the current shown slide.
     */
    public synchronized Slide showSlide() {
        var currentSlide = getCurrentSlide();
        if (currentSlide != null) {
            setImage(currentSlide.getImage());
            return currentSlide;
        }
        return null;
    }

    /**
     * Get the image view.
     *
     * @return Returns the image view.
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Set the image view.
     *
     * @param imageView The image view to use.
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Set the image for the image view.
     *
     * @param image The image to use.
     */
    protected void setImage(Image image) {
        if (image != null) imageView.setImage(image);
    }

    /**
     * Set the image for the image view.
     *
     * @param slide The slide to use.
     */
    protected void setImage(Slide slide) {
        if (slide != null) imageView.setImage(slide.getImage());
    }

    /**
     * Get all the available slides.
     *
     * @return Returns null if no slides are found.
     */
    public List<Slide> getSlides() {
        return images;
    }

    /**
     * Start the slideshow thread. Please use start() to start the slideshow and not this!
     */
    @Override
    protected Object call() throws Exception {
        while (true) {

            try {
                // If requested to kill the slideshow, stop.
                if (executorService == null || executorService != null && (executorService.isTerminated() || executorService.isShutdown())) {
                    Logger.getInstance().log("Executioner thread is either null, killed or shut down! Cannot go any further, man.");
                    break;
                }

                // If paused don't do anything.
                if (paused) {
                    //Logger.getInstance().log(String.format("%s is paused.", getClass().getSimpleName()));
                    Thread.sleep(500);
                    continue;
                }

                // The timer is in seconds!
                if (currentSlideShowDelay < slideShowDelay) {
                    currentSlideShowDelay++;

                } else {
                    // When the time has passed show the next slide and reset the current slideshow timer.
                    currentSlideShowDelay = 0;

                    //Logger.getInstance().log("Slideshow time passed, showing next slide.");
                    updateValue(next());
                }

                //Logger.getInstance().log(String.format("Current slideshow time: %.2f, desired: %.2f.", currentSlideShowDelay, slideShowDelay));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Start the slideshow.
     */
    public void start() {
        if (executorService != null && (!executorService.isTerminated() || !executorService.isShutdown())) {
            updateMessage(cannotStartMessage);
            return;
        } else if (images.size() <= 0) {
            Logger.getInstance().log("No slides found! Please add some first.");
            updateMessage(cannotStartMessage);
            return;
        }

        updateMessage(slideshowStartedMessage);
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this);
        //Logger.getInstance().log("Starting slideshow thread!");

    }

    /**
     * Pause the slideshow.
     */
    public void pause() {
        if (!paused) {
            paused = true;
            updateMessage(slideshowStoppedMessage);
        }
    }

    /**
     * Unpause the slideshow.
     */
    public void unpause() {
        if (paused) {
            paused = false;
            updateMessage(slideshowStartedMessage);
            //Logger.getInstance().log("Unpaused.");
        }
    }

    /**
     * Stop the slideshow entirely. This will not allow restarts. Use pause() instead.
     */
    public void stop() {
        if (executorService != null) {
            executorService.shutdown();
            updateMessage(slideshowStoppedMessage);
            Logger.getInstance().log("Killed slideshow thread.");
        }
    }

    /**
     * Is the slideshow thread started?
     *
     * @return Returns true if yes otherwise false.
     */
    public boolean isSlideshowStarted() {
        return executorService != null && (!executorService.isShutdown() || !executorService.isTerminated());
    }

    /**
     * Is the slideshow paused?
     *
     * @return
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Can the slideshow be started?
     *
     * @return Returns true if yes otherwise false.
     */
    public boolean canStart() {
        return isSlideshowStarted() && images.size() > 0;
    }

    /**
     * Get the singleton instance.
     */
    public static SlideshowManager getInstance() {
        return instance == null ? instance = new SlideshowManager() : instance;
    }
}
