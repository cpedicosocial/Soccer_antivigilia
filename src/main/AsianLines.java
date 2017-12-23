package main;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */

public class AsianLines {
    /**
     * This field sets the variable of class Line
     */
	public Line line1;
    /**
     * This field sets the variable of class Line
     */
	public Line line2;
    /**
     * This field sets the variable of class Line
     */
	public Line main;
    /**
     * This field sets the variable of class Line
     */
	public Line line3;
    /**
     * This field sets the variable of class Line
     */
	public Line line4;

	public AsianLines(Line main) {
		super();
		this.main = main;
	}

	public AsianLines(Line line1, Line line2, Line main, Line line3, Line lien4) {
		super();
		this.line1 = line1;
		this.line2 = line2;
		this.main = main;
		this.line3 = line3;
		this.line4 = lien4;
	}

}
