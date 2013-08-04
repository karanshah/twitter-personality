import twitter4j.*;

import java.util.List;
import java.util.logging.Logger;

public class QuoterPersonality implements Personality {
    private Twitter twitter = null;
    private final static Logger LOG = Logger.getLogger(QuoterPersonality.class.getName());
    private static final int FRIEND_PAGINATION_LIMIT = 5000;
    private static final int STATUS_PAGINATION_LIMIT = 200;
    private static final int TOTAL_STATUS_LIMIT = 3200;

    /**
     * Default Constructor.  Initialize twitter4j
     */
    public QuoterPersonality() {
        twitter = TwitterFactory.getSingleton();
    }

    /**
     * Creates a new message to post
     * @return message
     */
    public String messageToPost() {
        return createNewMessage();
    }

    /**
     * Create a new message by getting a random friend of the current user
     * and getting a random tweet from the friends time line
     * @return new message
     */
    private String createNewMessage() {
        String message = null;
        try {
            long twitterId = twitter.getId();
            Long friendId = getRandomFriend(twitterId);
            if (friendId != null) {
                Status randomStatus = getRandomStatus(friendId);
                message = randomStatus != null ? randomStatus.getText() : null;
            }
        } catch (TwitterException e) {
            LOG.severe("Unable to create a new message - " + e);
        }

        return message;
    }

    /**
     * Finds a count of statuses for the twitter user and generates a random
     * number to get a status
     * @param userId
     * @return random Status
     * @throws TwitterException
     */
    private Status getRandomStatus(long userId) throws TwitterException {
        User user = twitter.showUser(userId);
        int statusesCount = user.getStatusesCount();
        if (statusesCount > TOTAL_STATUS_LIMIT) {
            statusesCount = TOTAL_STATUS_LIMIT;
        }
        int randomNumber = (int) (Math.random() * statusesCount);
        return getStatus(user, randomNumber);
    }

    /**
     * Goes through the user's time line to get the random status
     * @param user
     * @param index
     * @return random Status
     * @throws TwitterException
     */
    private Status getStatus(User user, int index) throws TwitterException {
        Status status = null;
        if (index < 0 || user == null) {
            return status;
        }
        if (index > TOTAL_STATUS_LIMIT) {
            index = TOTAL_STATUS_LIMIT;
        }
        int mod = index % STATUS_PAGINATION_LIMIT;
        int cursor = (index / STATUS_PAGINATION_LIMIT);
        Paging paging = new Paging();
        paging.setCount(200);
        long maxId;
        List<Status> userTimeLine = null;
        for (int i = 0; i < cursor; i++) {
            userTimeLine = twitter.getUserTimeline(user.getScreenName(), paging);
            if (userTimeLine != null && userTimeLine.size() > 0) {
                Status tempStatus = userTimeLine.get(userTimeLine.size()-1);
                maxId = tempStatus != null ? tempStatus.getId() : 0;
                paging.setMaxId(maxId);
            }
        }
        if (userTimeLine != null && userTimeLine.size() >= mod-1) {
            status = userTimeLine.get(mod);
        }
        return status;
    }

    /**
     * Finds the number of friends the current user has and creates a
     * random number to pick a friend
     * @param twitterId
     * @return twitter id of the friend
     * @throws TwitterException
     */
    private Long getRandomFriend(long twitterId) throws TwitterException {
        User currentUser = twitter.showUser(twitterId);
        int friendsCount = currentUser.getFriendsCount();
        int randomNumber = (int) (Math.random() * (friendsCount));
        return findFriend(twitterId, randomNumber);
    }

    /**
     * finds the friends twitter id for the current user
     * @param twitterId
     * @param index
     * @return twitter id of the friend
     * @throws TwitterException
     */
    private Long findFriend(long twitterId, int index) throws TwitterException {
        Long friendId = null;
        if (index < 0 || twitterId <= 0) {
            return friendId;
        }
        int mod = index % FRIEND_PAGINATION_LIMIT;
        int cursor = (index / FRIEND_PAGINATION_LIMIT) - 1;
        IDs friendsIDs = twitter.getFriendsIDs(cursor);
        if (friendsIDs != null && friendsIDs.getIDs().length >= mod) {
            friendId = friendsIDs.getIDs()[mod];
        }

        return friendId;
    }
}
