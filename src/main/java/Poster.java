import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.logging.Logger;

public class Poster {

    private final static Logger LOG = Logger.getLogger(Poster.class.getName());

    /**
     * Post the message to the twitter account.  Returns a status id of the new post
     * @param message
     * @return statusId
     */
    public static long postStatus(String message) {
        long statusId = 0;
        if (message == null || message.length() > 140) {
            return statusId;
        }
        Twitter twitter = TwitterFactory.getSingleton();
        try {
            Status status = twitter.updateStatus(message);
            statusId = status.getId();
        } catch (TwitterException e) {
            LOG.severe("Unable to post the message to Twitter:- \n" + message + "\n" + e);
        }
        return statusId;
    }
}
