package runner;

import java.util.concurrent.Callable;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import settings.Settings;
import xls.XlSUtils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class RunnerOptimals implements Callable<Float> {
    /**
     * This field sets the variable of class HSSFSheet
     */
	public HSSFSheet sh;
    /**
     * This field sets the int variable
     */
	public int year;

	public RunnerOptimals(HSSFSheet sh, int year) {
		this.sh = sh;
		this.year = year;
	}

	@Override
	public Float call() throws Exception {
		Settings set = XlSUtils.optimalSettings(sh, year);
		System.out.println(year + " " + set);
		// System.out.println("Total profit for " + year + " is: " +
		// set.profit);
		return set.profit;
	}
}
