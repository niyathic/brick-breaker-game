/*
 * UserScore class containing information for a user's name and score
 */

public class UserScore implements Comparable<UserScore> {
    private final int score;
    private final String name;

    public UserScore(int score, String name) {
        this.score = score;
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(UserScore o) {
        if (this.score < o.score) {
            return -1;
        }
        else if (this.score == o.score) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
