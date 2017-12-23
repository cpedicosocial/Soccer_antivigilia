package main;

import java.util.Date;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class TimeLine extends Line {

	public Date time;

	public TimeLine(float line, float home, float away, Date time, String bookmaker) {
		super(line, home, away, bookmaker);
		this.time = time;
	}
	
	public String toString() {
		return "line " + line + " " + home + " " + away + " " + "\n";
	}

}
