package runner;

import java.util.concurrent.Callable;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import xls.XlSUtils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Runner implements Callable<Float> {
    /**
     * This field sets the variable of class HSSFSheet
     */
	public HSSFSheet sh;
    /**
     * This field sets the int variable
     */
	public int year;

	public Runner(HSSFSheet sh, int year) {
		this.sh = sh;
		this.year = year;
	}

	@Override
	public Float call() throws Exception {
		Float profit = /*XlSUtils.realisticAggregate(sh, year, 5);*/ XlSUtils.realisticFromDB(sh, year);
//		System.out.println("Profit for  " + sh.getSheetName() + " " + year + " is: " + String.format("%.2f", profit));
		return profit;
	}
}
