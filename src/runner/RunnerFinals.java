package runner;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import entries.FinalEntry;
import xls.XlSUtils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class RunnerFinals implements Callable<ArrayList<FinalEntry>> {
    /**
     * This field sets the variable of class HSSFSheet
     */
	public HSSFSheet sh;
    /**
     * This field sets the int variable
     */
	public int year;

	public RunnerFinals(HSSFSheet sh, int year) {
		this.sh = sh;
		this.year = year;
	}

	@Override
	public ArrayList<FinalEntry> call() throws Exception {
		ArrayList<FinalEntry> tobet = XlSUtils.finalsShots(sh, year);
		return tobet;
	}
}