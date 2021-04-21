package bll;

import be.SlideshowInstance;
import gui.controller.ImageViewerWindowController;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlideshowInstanceManager extends Task {

    private double currentInstanceShowTime;
    private int currentInstanceIndex;
    private ExecutorService executorService;
    private List<SlideshowInstance> slideshowInstances;

    private SlideshowInstance currentSlideshow;


    /**
     * The amount of time for an instance to be shown.
     */
    public static double instanceShowTime = 20;

    private static SlideshowInstanceManager instance;

    public SlideshowInstanceManager() {
        initialize();
    }

    private void initialize() {
        slideshowInstances = new ArrayList<>();
    }

    /**
     * Add a new ImageViewerWindowController instance.
     *
     * @param slideshowInstance The ImageViewerWindowController instance to add.
     * @return Returns true if successful otherwise false.
     */
    public boolean addControllerInstance(SlideshowInstance slideshowInstance) {
        if (slideshowInstance != null && !slideshowInstances.contains(slideshowInstance)) {
            slideshowInstances.add(slideshowInstance);
            Logger.getInstance().log(String.format("New instance of %s added.", slideshowInstance.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    /**
     * Add a new ImageViewerWindowController instance.
     *
     * @param controller The ImageViewerWindowController instance to add.
     * @return Returns true if successful otherwise false.
     */
    public SlideshowInstance addControllerInstance(ImageViewerWindowController controller, Stage stage) {
        if (controller != null) {
            var id = slideshowInstances.size() + 1;
            var instance = new SlideshowInstance(id, controller, stage);
            slideshowInstances.add(instance);
            Logger.getInstance().log(String.format("New instance of %s added.", controller.getClass().getSimpleName()));
            return instance;
        }
        return null;
    }

    /**
     * Remove an ImageViewerWindowController instance.
     *
     * @param controller The ImageViewerWindowController instance to remove.
     * @return Returns true if successful otherwise false.
     */
    public boolean removeControllerInstance(SlideshowInstance controller) {
        if (controller != null && slideshowInstances.contains(controller)) {
            for (int i = 0; i < slideshowInstances.size(); i++) {
                var currController = slideshowInstances.get(i);
                if (currController.equals(controller)) {
                    slideshowInstances.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove an ImageViewerWindowController instance.
     *
     * @param id The ImageViewerWindowController instance to remove.
     * @return Returns true if successful otherwise false.
     */
    public boolean removeControllerInstance(long id) {
        for (int i = 0; i < slideshowInstances.size(); i++) {
            var currController = slideshowInstances.get(i);
            if (currController.getId() == id) {
                slideshowInstances.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Show the next slide.
     *
     * @return Returns the next slide.
     */
    public synchronized SlideshowInstance next() {
        currentInstanceIndex = (currentInstanceIndex + 1) % slideshowInstances.size();
        return slideshowInstances.get(currentInstanceIndex);
    }

    /**
     * Show the previous slide.
     *
     * @return Returns the previous slide.
     */
    public synchronized SlideshowInstance previous() {
        currentInstanceIndex = (currentInstanceIndex - 1 + slideshowInstances.size()) % slideshowInstances.size();
        return slideshowInstances.get(currentInstanceIndex);
    }

    /**
     * Pause all other slideshows except the one specified by an id.
     *
     * @param id The id of the slideshow to ignore.
     */
    public synchronized void pauseAllExcept(long id) {
        for (int i = 0; i < slideshowInstances.size(); i++) {
            var slideInstance = slideshowInstances.get(i);
            if (slideInstance.getId() != id && !slideInstance.getSlideshowManager().isPaused()) {
                slideInstance.getSlideshowManager().pause();
                slideInstance.setActive(false);
                //currentSlideshow.setActive(true);
            }
        }
    }

    public synchronized void pauseAll() {
        for (int i = 0; i < slideshowInstances.size(); i++) {
            var slideInstance = slideshowInstances.get(i);
            if (slideInstance.isActive()) {
                slideInstance.getSlideshowManager().pause();
                slideInstance.setActive(false);
                //currentSlideshow.setActive(true);
            }
        }
    }

    /**
     * Resume all other slideshows except the one specified by an id.
     *
     * @param id The id of the slideshow to ignore.
     */
    public synchronized void resumeAllExcept(long id) {
        for (int i = 0; i < slideshowInstances.size(); i++) {
            var slideInstance = slideshowInstances.get(i);
            if (slideInstance.getId() != id && !slideInstance.isActive()) {
                slideInstance.getSlideshowManager().unpause();
                slideInstance.setActive(true);
                //currentSlideshow.setActive(true);
            }
        }
    }

    /**
     * Resume all slideshows.
     */
    public synchronized void resumeAll() {
        for (int i = 0; i < slideshowInstances.size(); i++) {
            var slideInstance = slideshowInstances.get(i);
            if (slideInstance.getSlideshowManager().isPaused()) {
                slideInstance.getSlideshowManager().unpause();
                slideInstance.setActive(true);
                //currentSlideshow.setActive(true);
            }
        }
    }

    /**
     * Is any other slideshow instances currently playing than the one specified?
     *
     * @param slideshowInstance The target slideshow to compare with.
     * @return Returns true if yes otherwsie false.
     */
    public synchronized boolean isAnyOtherSlideshowPlayingThan(SlideshowInstance slideshowInstance) {
        for (int i = 0; i < slideshowInstances.size(); i++) {
            var slideInstance = slideshowInstances.get(i);
            if (slideInstance.getId() != slideshowInstance.getId() && slideInstance.getSlideshowManager().isSlideshowStarted() && !slideInstance.getSlideshowManager().isPaused()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start the slideshow instance watcher thread. Please use start() to start the slideshow watcher and not this!
     */
    @Override
    protected synchronized Object call() throws Exception {
        while (true) {

            try {
                // If requested to kill the slideshow, stop.
                if (executorService == null || executorService != null && (executorService.isTerminated() || executorService.isShutdown())) {
                    Logger.getInstance().log("Executioner thread is either null, killed or shut down! Cannot go any further, man.");
                    break;
                }

                // if only an instance is active don't do anything.
                if (slideshowInstances.size() <= 1) {
                    Thread.sleep(500);
                    continue;
                }

                // If no current slideshow is active pick the next available.
                // And make it active.
                if (currentSlideshow == null) {
                    currentSlideshow = next();
                    currentSlideshow.setActive(true);
                    currentInstanceShowTime = 0;
                    if (isAnyOtherSlideshowPlayingThan(currentSlideshow))
                        pauseAllExcept(currentSlideshow.getId());

                    // Let's give the cpu some time to think...
                    Thread.sleep(300);
                } else {
                    //if (currentSlideshow.isActive() && currentSlideshow.getSlideshowManager().isSlideshowStarted()) {

                    // If our current slideshow is active...
                    if (currentSlideshow.isActive()) {
                        Logger.getInstance().log(String.format("Instance: %d is currently active.", currentSlideshow.getId()));

                        // Start it by either starting the thread Task itself, pause or unpause.
                        if (!currentSlideshow.getSlideshowManager().isSlideshowStarted())
                            currentSlideshow.getSlideshowManager().start();
                        else if (currentSlideshow.getSlideshowManager().isPaused())
                            currentSlideshow.getSlideshowManager().unpause();

                        // Increment the timer.
                        if (currentInstanceShowTime < instanceShowTime)
                            currentInstanceShowTime++;
                        else {
                            // Once we reach the limit, reset the timer.
                            // And nullify the current slideshow to be reusable for the next slideshow.
                            currentInstanceShowTime = 0;
                            currentSlideshow.setActive(false);
                            currentSlideshow = null;
                            //resumeAll();
                        }

                        // Since the timer is in seconds we then have to make the thread sleep like so.
                        //Logger.getInstance().log(String.format("Current show time: %d, desired time: %d", currentInstanceShowTime, instanceShowTime));
                        Thread.sleep(1000);
                    }
                }

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
            return;
        }
//        else if (controllerInstances.size() <= 0) {
//            Logger.getInstance().log("No controllers found! Please add one first.");
//            return;
//        }

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this);
        Logger.getInstance().log("Starting slideshow instance manager thread!");

    }

    /**
     * Set the current active slideshow.
     *
     * @param slideshowInstance The slideshow to set as active.
     */
    public void setActiveSlideshow(SlideshowInstance slideshowInstance) {
        if (slideshowInstance != null && slideshowInstance.getSlideshowManager().isSlideshowStarted()) {
            //currentSlideshow = slideshowInstance;
            //currentSlideshow.setActive(true);
        }
    }

    /**
     * Get the singleton instance.
     *
     * @return Returns null if something goes wrong.
     */
    public static SlideshowInstanceManager getInstance() {
        return instance == null ? instance = new SlideshowInstanceManager() : instance;
    }
}
