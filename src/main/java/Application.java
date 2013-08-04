import java.util.logging.Logger;

public class Application {

    private final static Logger LOG = Logger.getLogger(Application.class.getName());
    private final static int JOB_INTERVAL = 14400000;

    public static void main (String[] args) throws InterruptedException {
        while (true) {
            runQuoterPersonality();
            Thread.sleep(JOB_INTERVAL);
        }
    }

    /**
     * Run Quoter Personality to get the user's friend at random and select a random message to
     * post from the time line
     */
    public static void runQuoterPersonality() {
        Personality personality = new QuoterPersonality();
        String message = personality.messageToPost();
        long statusId = Poster.postStatus(message);
        if (statusId > 0) {
            LOG.info("Successfully posted to Twitter Status " + statusId + " - " + message);
        }
        else {
            LOG.severe("Error while posting the message - " + message);
        }
    }
}
