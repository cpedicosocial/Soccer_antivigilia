package runner;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import main.ExtendedFixture;
import scraper.Scraper;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class RunnerOdds implements Callable<ArrayList<ExtendedFixture>> {
    /**
     * This field sets the variable of class String
     */
	public String competition;
    /**
     * This field sets the int variable
     */
	public int year;
    /**
     * This field sets the variable of class String
     */
	public String add;
    /**
     * This field sets the int variable
     */
	public int page;

	public RunnerOdds(String competition, int year, String add, int page) {
		this.competition = competition;
		this.year = year;
		this.add = add;
		this.page = page;
	}

	@Override
	public ArrayList<ExtendedFixture> call() throws Exception {
		ArrayList<ExtendedFixture> tobet = Scraper.oddsByPage(competition, year, add, page);
		return tobet;
	}
}
