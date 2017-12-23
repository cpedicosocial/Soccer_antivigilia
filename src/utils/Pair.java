package utils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Pair {
    /**
     * This field sets the float variable
     */
	public float home;
    /**
     * This field sets the float variable
     */
	public float away;

	public Pair(float home, float away) {
		super();
		this.home = home;
		this.away = away;
	}

	public static Pair of(float newHome, float newAway) {
		return new Pair(newHome, newAway);
	}

	public String toString() {
		return home + " : " + away;
	}
}
