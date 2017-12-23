package main;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Line {
    /**
     * This field sets the float variable
     */
	public float line;
    /**
     * This field sets the float variable
     */
	public float home;
    /**
     * This field sets the float variable
     */
	public float away;
    /**
     * This field sets the variable of class String
     */
	public String bookmaker;

	public Line(float line, float home, float away, String bookmaker) {
		super();
		this.line = line;
		this.home = home;
		this.away = away;
		this.bookmaker = bookmaker;
	}

	public String toString() {
		return "line " + line + " " + home + " " + away + "\n";
	}

}
