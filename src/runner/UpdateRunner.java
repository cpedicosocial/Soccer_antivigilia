package runner;

import java.io.IOException;
import java.text.ParseException;

import predictions.Predictions.OnlyTodayMatches;
import scraper.Scraper;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class UpdateRunner implements Runnable {
    /**
     * This field sets the variable of class String
     */
	public String competition;
    /**
     * This field sets the variable of class OnlyTodayMatches
     */
	public OnlyTodayMatches onlyTodaysMatches;

	public UpdateRunner(String competition, OnlyTodayMatches onlyToday) {
		this.competition = competition;
		this.onlyTodaysMatches = onlyToday;
	}

	@Override
	public void run() {
		try {
			Scraper.checkAndUpdate(competition, onlyTodaysMatches);
		} catch (IOException | ParseException | InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Something was wrong");
			//e.printStackTrace();
		}
	}
}
