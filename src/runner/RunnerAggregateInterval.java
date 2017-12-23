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
public class RunnerAggregateInterval implements Callable<Settings> {
    /**
     * This field sets the variable of class HSSFSheet
     */
	public HSSFSheet sh;
    /**
     * This field sets the int variable
     */
	public int startYear;
    /**
     * This field sets the int variable
     */
	public int endYear;

	public RunnerAggregateInterval(int startYear, int endYear, HSSFSheet sh) {
		this.sh = sh;
		this.startYear = startYear;
		this.endYear = endYear;
	}

	@Override
	public Settings call() throws Exception {

		Settings setts = XlSUtils.aggregateOptimals(startYear, endYear, sh.getSheetName());
		System.out.println(setts + " avg: " + setts.profit / (endYear - startYear + 1));

		return setts;
	}
}