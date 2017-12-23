package utils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Triple {
    /**
     * This field sets the float variable
     */
	public float first;
    /**
     * This field sets the variable of class Pair
     */
	public Pair pair;

	public Triple(float newFirst, Pair newPair) {
		super();
		this.first = newFirst;
		this.pair = newPair;
	}

	public static Triple of(float newHome, Pair newPair) {
		return new Triple(newHome, newPair);
	}

	public String toString() {
		return first + " : " + pair;
	}
}