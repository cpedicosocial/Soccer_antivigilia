package runner;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import entries.AsianEntry;
import xls.AsianUtils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class RunnerAsianFinals implements Callable<ArrayList<AsianEntry>> {
    /**
     * This field sets the variable of class HSSFSheet
     */
	public HSSFSheet sh;
    /**
     * This field sets the int variable
     */
	public int year;

	public RunnerAsianFinals(HSSFSheet sh, int year) {
		this.sh = sh;
		this.year = year;
	}

	@Override
	public ArrayList<AsianEntry> call() throws Exception {
		ArrayList<AsianEntry> finals = AsianUtils.realisticFinals(sh, year);
		// System.out.println("Profit for " + sh.getSheetName() + " " + year + "
		// is: " + String.format("%.2f", profit));
		return finals;
	}
}
