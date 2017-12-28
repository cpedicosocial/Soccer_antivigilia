package predictions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import entries.FinalEntry;
import main.Test.DataType;
import predictions.Predictions.OnlyTodayMatches;
import runner.RunnerAsianPredictions;
import runner.RunnerPredictions;
import scraper.Scraper;
import settings.Settings;
import utils.Utils;
import xls.XlSUtils;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Predictions {
    /**
     * This field sets the array with chechlist
     */
	public static ArrayList<String> CHECKLIST = new ArrayList<>();

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

		// CHECKLIST.add("ENG");
		// CHECKLIST.add("ENG2");
		// CHECKLIST.add("ENG3");
		// CHECKLIST.add("ENG4");
		// CHECKLIST.add("ENG5");
		// CHECKLIST.add("IT");
		// CHECKLIST.add("IT2");
		// CHECKLIST.add("FR");
		// CHECKLIST.add("FR2");
		// CHECKLIST.add("SPA");
		// CHECKLIST.add("SPA2");
		// CHECKLIST.add("GER");
//		 CHECKLIST.add("GER2");
		// CHECKLIST.add("SCO");
		// CHECKLIST.add("NED");
		// CHECKLIST.add("BEL");
		// CHECKLIST.add("SWI");
		// CHECKLIST.add("POR");
		// CHECKLIST.add("GRE");
		// CHECKLIST.add("TUR");
		// CHECKLIST.add("BUL");
		// CHECKLIST.add("RUS");
		// CHECKLIST.add("AUS");
		// CHECKLIST.add("DEN");
		// CHECKLIST.add("CZE");
		// CHECKLIST.add("ARG");
		// CHECKLIST.add("POL");
		// CHECKLIST.add("CRO");
		// CHECKLIST.add("SLO");
		// CHECKLIST.add("USA");
		// CHECKLIST.add("SWE");
		// CHECKLIST.add("NOR");
		// CHECKLIST.add("FIN");
		 CHECKLIST.add("BRA");
//		CHECKLIST.add("BRB");

//		 Scraper.updateInParallel(CHECKLIST, 2, OnlyTodayMatches.FALSE,
//		 UpdateType.AUTOMATIC, 21, 11);

//		predictions(2017, DataType.ODDSPORTAL, UpdateType.AUTOMATIC, OnlyTodayMatches.TRUE, 22, 11);

//		 predictions(2017, DataType.ODDSPORTAL, UpdateType.MANUAL,
//		 OnlyTodayMatches.FALSE, 21, 11);
		 
		 Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
	}

	private static void controlEquilibriumGreater(ArrayList<FinalEntry> equilibriumsData, boolean allUnders)
	{
		if (Utils.getProfit(Utils.allUnders(Utils.onlyFixtures(equilibriumsData))) > 0f) {
			allUnders = true;
			Utils.printStats(Utils.allUnders(Utils.onlyFixtures(equilibriumsData)), "Equilibriums as unders");
		}
	}
	
	private static void controlEquilibriumSmaller(ArrayList<FinalEntry> equilibriumsData, boolean allOvers)
	{
		if (Utils.getProfit(Utils.allUnders(Utils.onlyFixtures(equilibriumsData))) <= 0f &&
				Utils.getProfit(Utils.allOvers(Utils.onlyFixtures(equilibriumsData))) > 0f) {
				allOvers = true;
				Utils.printStats(Utils.allOvers(Utils.onlyFixtures(equilibriumsData)), "Equilibriums as overs");
			} else {
				System.out.println("No value in equilibriums");
		}
	}
	
	private static void controlOnlyToday(OnlyTodayMatches onlyToday, ArrayList<FinalEntry> pending, int month, int year)
	{
		if (onlyToday.equals(OnlyTodayMatches.TRUE)) {
			pending = Utils.gamesForDay(pending, LocalDate.of(2017, month, day));
		}
	}
	
	private static void controlFile(FileInputStream file, DataType type, String base, int year)
	{
		if (type.equals(DataType.ALLEURODATA))
			file = new FileInputStream(new File(base + "\\data\\all-euro-data-" + year + "-" + (year + 1) + ".xls"));
		else if (type.equals(DataType.ODDSPORTAL))
			file = new FileInputStream(new File(base + "\\data\\odds" + year + ".xls"));
	}
	
	private static void controlFileFinally(FileInputStream file)
	{
		if (file != null) {
			try {
				file.close (); // OK
			} catch (java.io.IOException e3) {
				System.out.println("I/O Exception");
	       }
		}
	}
	
	public static ArrayList<FinalEntry> predictions(int year, DataType type, UpdateType automatic,
			OnlyTodayMatches onlyToday, int day, int month)
					throws InterruptedException, ExecutionException, IOException {
		try {
			String base = new File("").getAbsolutePath();
			FileInputStream file = null;
			controlFile(file, type, base, year);
			HSSFWorkbook workbook = new HSSFWorkbook(file);
		} catch (Exception e) {
			System.out.println("Something was wrong");
		} finally {
			controlFileFinally(file);
		}
		Iterator<Sheet> sheet = workbook.sheetIterator();
		ArrayList<FinalEntry> all = new ArrayList<>();

		ExecutorService pool = Executors.newFixedThreadPool(3);
		ArrayList<Future<ArrayList<FinalEntry>>> threadArray = new ArrayList<Future<ArrayList<FinalEntry>>>();
		ArrayList<String> leagues = automatic.equals(UpdateType.AUTOMATIC) ? Scraper.getTodaysLeagueList(day, month)
				: CHECKLIST;
		System.out.println(leagues);
		while (sheet.hasNext()) {
			HSSFSheet sh = (HSSFSheet) sheet.next();
			// if (!sh.getSheetName().equals("ENG2"))
			// continue;
			if (!leagues.contains(sh.getSheetName()))
				continue;

			threadArray.add(pool.submit(new RunnerPredictions(sh, year)));
		}

		for (Future<ArrayList<FinalEntry>> fd : threadArray) {
			all.addAll(fd.get());
			// System.out.println("Total profit: " + String.format("%.2f",
			// totalProfit));
		}
		// System.out.println("Total profit for season " + year + " is " +
		// String.format("%.2f", totalProfit));
		workbook.close();
		pool.shutdown();

		all.sort(Comparator.comparing(FinalEntry::getPrediction));
		// all.sort(new Comparator<FinalEntry>() {
		//
		// @Override
		// public int compare(FinalEntry o1, FinalEntry o2) {
		// return ((Float) o1.prediction).compareTo((Float) o2.prediction);
		// }
		// });

		ArrayList<FinalEntry> result = new ArrayList<>();
		HashMap<String, ArrayList<FinalEntry>> byLeague = Utils.byLeague(all);

		for (Entry<String, ArrayList<FinalEntry>> i : byLeague.entrySet()) {

			ArrayList<FinalEntry> data = Utils.notPendingFinals(i.getValue());
			ArrayList<FinalEntry> equilibriumsData = Utils.equilibriums(data);
			ArrayList<FinalEntry> dataProper = Utils.noequilibriums(data);
			ArrayList<FinalEntry> pending = Utils.pendingFinals(i.getValue());
			
			controlOnlyToday(onlyToday, pending, month, year);
			
			if (pending.isEmpty())
				continue;

			System.out.println(i.getKey());
			ArrayList<FinalEntry> equilibriumsPending = Utils.equilibriums(pending);
			ArrayList<FinalEntry> pendingProper = Utils.noequilibriums(pending);

			boolean allUnders = false;
			boolean allOvers = false;
			
			controlEquilibriumGreater(equilibriumsData,allUnders);
			controlEquilibriumSmaller(equilibriumsData,allOvers);
			controlAllUnders(equilibriumsPending, allOvers, result);

			Utils.printStats(dataProper, "all");
			Utils.printStats(Utils.onlyUnders(dataProper), "unders");
			System.out.println(Utils.onlyUnders(pendingProper));
			result.addAll(pendingProper);
			Utils.printStats(Utils.onlyOvers(dataProper), "overs");
			System.out.println(Utils.onlyOvers(pendingProper));
			System.out
					.println("---------------------------------------------------------------------------------------");
		}

		result.sort(new Comparator<FinalEntry>() {

			@Override
			public int compare(FinalEntry o1, FinalEntry o2) {

				return ((Float) o2.prediction).compareTo((Float) o1.prediction);
			}

		});
		System.out.println(result);

		return all;
	}

	public static float asianPredictions(int year, boolean parsedLeagues)
			throws InterruptedException, ExecutionException, IOException {
		try {
			String base = new File("").getAbsolutePath();
	
			FileInputStream file;
			if (!parsedLeagues)
				file = new FileInputStream(new File(base + "\\data\\all-euro-data-" + year + "-" + (year + 1) + ".xls"));
			else
				file = new FileInputStream(new File(base + "\\data\\odds" + year + ".xls"));
		} catch (Exception e) {
			System.out.println("Something was wrong");
		} finally {
			if (file != null) {
				try {
					file.close (); // OK
				} catch (java.io.IOException e3) {
					System.out.println("I/O Exception");
               }
			}
		}
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		Iterator<Sheet> sheet = workbook.sheetIterator();
		float totalProfit = 0.0f;

		ExecutorService pool = Executors.newFixedThreadPool(3);
		ArrayList<Future<Float>> threadArray = new ArrayList<Future<Float>>();
		while (sheet.hasNext()) {
			HSSFSheet sh = (HSSFSheet) sheet.next();
			// if (!sh.getSheetName().equals("ENG2"))
			// continue;
			if (!Predictions.CHECKLIST.contains(sh.getSheetName()))
				continue;

			threadArray.add(pool.submit(new RunnerAsianPredictions(sh, year)));
		}

		for (Future<Float> fd : threadArray) {
			totalProfit += fd.get();
			// System.out.println("Total profit: " + String.format("%.2f",
			// totalProfit));
		}
		// System.out.println("Total profit for season " + year + " is " +
		// String.format("%.2f", totalProfit));
		workbook.close();
		pool.shutdown();
		return totalProfit;
	}

	public enum OnlyTodayMatches {
		TRUE, FALSE
	}

}
