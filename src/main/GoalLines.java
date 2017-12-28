package main;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class GoalLines {
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

	public GoalLines(Line line1, Line line2, Line main, Line line3, Line line4) {
		super();
		this.line1 = line1;
		this.line2 = line2;
		this.main = main;
		this.line3 = line3;
		this.line4 = line4;
	}

	public GoalLines(Line main) {
		super();
		this.main = main;
	}

	public Line[] getArrayLines() {
		Line[] lines = { line1, line2, main, line3, line4 };
		return lines;
	}

	public Line[] get3Lines() {
		Line[] lines = { line2, main, line3 };
		return lines;
	}

}
