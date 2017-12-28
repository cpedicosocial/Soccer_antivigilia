package utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class Utils_3 {
	public static float bestCot(ArrayList<FinalEntry> finals) {
		ArrayList<ArrayList<FinalEntry>> byYear = new ArrayList<>();

		float bestCot = 0f;
		float bestProfit = getProfit(finals);

		for (int j = 1; j <= 12; j++) {
			ArrayList<FinalEntry> filtered = createFinalEntry();
			float cot = j * 0.02f;
			filtered.addAll(Utils.cotRestrict(finals, cot));

			float currProfit = 0f;
			currProfit += Utils.getProfit(filtered);

			if (currProfit > bestProfit) {
				bestProfit = currProfit;
				bestCot = cot;
			}

		}

		return 5 * bestCot / 6;
	}

	public static ArrayList<FinalEntry> allOvers(ArrayList<ExtendedFixture> current) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (ExtendedFixture i : current) {
			FinalEntry n = new FinalEntry(i, 1f, i.result, 0.55f, 0.55f, 0.55f);
			result.add(n);
		}
		return result;
	}

	public static ArrayList<FinalEntry> allUnders(ArrayList<ExtendedFixture> current) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (ExtendedFixture i : current) {
			FinalEntry n = new FinalEntry(i, 0f, i.result, 0.55f, 0.55f, 0.55f);
			result.add(n);
		}
		return result;
	}

	public static ArrayList<FinalEntry> higherOdds(ArrayList<ExtendedFixture> current) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (ExtendedFixture i : current) {
			float prediction = i.maxOver >= i.maxUnder ? 1f : 0f;
			FinalEntry n = new FinalEntry(i, prediction, i.result, 0.55f, 0.55f, 0.55f);
			result.add(n);
		}
		return result;
	}

	public static ArrayList<FinalEntry> lowerOdds(ArrayList<ExtendedFixture> current) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (ExtendedFixture i : current) {
			float prediction = i.maxOver >= i.maxUnder ? 0f : 1f;
			FinalEntry n = new FinalEntry(i, prediction, i.result, 0.55f, 0.55f, 0.55f);
			result.add(n);
		}
		return result;
	}

	public static float avgShotsDiffHomeWin(HSSFSheet sheet, Date date) {
		int totalHome = 0;
		int totalAway = 0;
		int count = 0;
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getRowNum() == 0)
				continue;
			Cell dateCell = row.getCell(XlSUtils.getColumnIndex(sheet, "Date"));
			int homegoal = (int) row.getCell(XlSUtils.getColumnIndex(sheet, "FTHG")).getNumericCellValue();
			int awaygoal = (int) row.getCell(XlSUtils.getColumnIndex(sheet, "FTAG")).getNumericCellValue();
			if (row.getCell(XlSUtils.getColumnIndex(sheet, "HST")) != null
					&& row.getCell(XlSUtils.getColumnIndex(sheet, "AST")) != null && dateCell != null
					&& dateCell.getDateCellValue().before(date)
					&& row.getCell(XlSUtils.getColumnIndex(sheet, "HST")).getCellType() == 0
					&& row.getCell(XlSUtils.getColumnIndex(sheet, "AST")).getCellType() == 0 && homegoal > awaygoal) {
				totalHome += (int) row.getCell(XlSUtils.getColumnIndex(sheet, "HST")).getNumericCellValue();
				totalAway += (int) row.getCell(XlSUtils.getColumnIndex(sheet, "AST")).getNumericCellValue();

				count++;
			}
		}
		return count == 0 ? 0 : (float) (totalHome - totalAway) / count;
	}

	public static String replaceNonAsciiWhitespace(String s) {

		String resultString = s.replaceAll("[^\\p{ASCII}]", "");

		return resultString;
	}

	public static Date getYesterday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Date oneDayBefore = cal.getTime();
		return oneDayBefore;

	}

	public static Object getTommorow(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date oneDayBefore = cal.getTime();
		return oneDayBefore;
	}

	public static boolean isToday(Date date) {
		Date today = new Date();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(today);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		return sameDay;
	}

	public static ArrayList<FinalEntry> equilibriums(ArrayList<FinalEntry> finals) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			if (i.prediction == 0.5f)
				result.add(i);
		}

		return result;
	}

	public static ArrayList<FinalEntry> noequilibriums(ArrayList<FinalEntry> finals) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		for (FinalEntry i : finals) {
			if (i.prediction != 0.5f)
				result.add(i);
		}

		return result;
	}

	public static float getProfit(ArrayList<FinalEntry> finals) {
		float profit = 0f;
		for (FinalEntry i : finals) {
			profit += i.getProfit();
		}
		return profit;
	}

	public static float getNormalizedProfit(ArrayList<FinalEntry> all) {
		float sum = 0f;
		for (FinalEntry i : all)
			sum += i.getNormalizedProfit();

		return sum / (getNormalizedStakeSum(all) / all.size());
	}

	public static float getAvgOdds(ArrayList<FinalEntry> finals) {
		float total = 0f;
		for (FinalEntry i : finals) {
			float coeff = i.prediction >= i.upper ? i.fixture.maxOver : i.fixture.maxUnder;
			total += coeff;
		}
		return finals.size() == 0 ? 0 : total / finals.size();
	}

	public static float pValueCalculator(int newCount, float newYield, float newAvgOdds) {
		if (newCount < 5)
			return -1f;
		double standardDeviation = Math.pow((1 + newYield) * (newAvgOdds - 1 - newYield), 0.5);
		double tStatistic = newYield * Math.pow(newCount, 0.5) / standardDeviation;
		TDistribution td = new TDistribution(newCount - 1);
		double pValue = 1 - td.cumulativeProbability(tStatistic);
		return (float) (1 / pValue);
	}

	public static float evaluateRecord(ArrayList<FinalEntry> all) {
		return pValueCalculator(all.size(), Utils.getYield(all), Utils.getAvgOdds(all));
	}

	public static float evaluateRecordNormalized(ArrayList<FinalEntry> all) {
		return pValueCalculator(all.size(), Utils.getNormalizedYield(all), Utils.getAvgOdds(all));
	}

	public static float getYield(ArrayList<FinalEntry> all) {
		return getProfit(all) / all.size();
	}

	public static float getNormalizedYield(ArrayList<FinalEntry> all) {

		return getNormalizedProfit(all) / all.size();
	}

	public static float[] createProfitMovementData(ArrayList<FinalEntry> all) {
		all.sort(new Comparator<FinalEntry>() {

			@Override
			public int compare(FinalEntry o1, FinalEntry o2) {

				return o1.fixture.date.compareTo(o2.fixture.date);
			}
		});
		float profit = 0;
		float[] result = new float[all.size() + 1];
		result[0] = 0f;
		for (int i = 0; i < all.size(); i++) {
			profit += all.get(i).getProfit();
			result[i + 1] = profit;
		}
		return result;
	}

	/**
	 * 
	 * @param all
	 * @return the avg return of the picks a.k.a the avg vigorish
	 */
	public static float avgReturn(ArrayList<ExtendedFixture> all) {
		float total = 0f;
		for (ExtendedFixture i : all) {
			total += 1f / i.maxOver + 1f / i.maxUnder;
		}
		return all.size() == 0 ? 0 : total / all.size();
	}

	public static void fairValue(ArrayList<ExtendedFixture> current) {

		for (ExtendedFixture i : current) {
			float sum = 1f / i.maxOver + 1f / i.maxUnder;
			i.maxOver = 1f / ((1f / i.maxOver) / sum);
			i.maxUnder = 1f / ((1f / i.maxUnder) / sum);

			if (i.asianHome > 1f && i.asianAway > 1f) {
				float sumAsian = 1f / i.asianHome + 1f / i.asianAway;
				i.asianHome = 1f / ((1f / i.asianHome) / sumAsian);
				i.asianAway = 1f / ((1f / i.asianAway) / sumAsian);
			}
		}
	}

	public static ArrayList<FinalEntry> runRandom(ArrayList<ExtendedFixture> current) {
		ArrayList<FinalEntry> result = new ArrayList<>();
		Random random = new Random();
		for (ExtendedFixture i : current) {
			boolean next = random.nextBoolean();
			float prediction = next ? 1f : 0f;
			FinalEntry n = new FinalEntry(i, prediction, i.result, 0.55f, 0.55f, 0.55f);
			result.add(n);
		}
		return result;
	}

	// for update of results from soccerway
	public static Date findLastPendingFixture(ArrayList<ExtendedFixture> all) {

		all.sort(new Comparator<ExtendedFixture>() {

			@Override
			public int compare(ExtendedFixture o1, ExtendedFixture o2) {
				return o1.date.compareTo(o2.date);
			}
		});

		boolean noPendings = true;
		for (ExtendedFixture i : all)
			if (i.result.goalsHomeTeam == -1) {
				noPendings = false;
				break;
			}

		Date last;
		if (all.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Scraper.CURRENT_YEAR);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			last = cal.getTime();
		} else {
			last = all.get(all.size() - 1).date;
		}
		Date lastNotPending = last;

		for (int i = all.size() - 1; i >= 0; i--) {
			if (all.get(i).result.goalsHomeTeam == -1 && all.get(i).result.goalsAwayTeam == -1)
				lastNotPending = all.get(i).date;
		}

		return /* noPendings ? new Date() : */ lastNotPending;

	}

	public static ArrayList<FinalEntry> mainGoalLine(ArrayList<FinalEntry> finals,
			HashMap<ExtendedFixture, FullFixture> map) {
		ArrayList<FinalEntry> fulls = new ArrayList<>();
		for (FinalEntry f : finals) {

			fulls.add(new FullEntry(f.fixture, f.prediction, f.result, f.threshold, f.lower, f.upper,
					map.get(f.fixture).goalLines.main));
		}

		return fulls;
	}

	// TO DO
	public static ArrayList<FinalEntry> bestValueByDistibution(ArrayList<FinalEntry> finals,
			HashMap<ExtendedFixture, FullFixture> map, ArrayList<ExtendedFixture> all, HSSFSheet sheet) {
		ArrayList<FinalEntry> fulls = new ArrayList<>();
		for (FinalEntry f : finals) {

			int[] distributionHome = getGoalDistribution(f.fixture, all, f.fixture.homeTeam);
			int[] distributionAway = getGoalDistribution(f.fixture, all, f.fixture.awayTeam);
			FullEntry best = findBestLineFullEntry(f, distributionHome, distributionAway, map);

			fulls.add(best);
		}

		return fulls;
	}
	
	// offset of main line
	public static ArrayList<FinalEntry> customGoalLine(ArrayList<FinalEntry> finals,
			HashMap<ExtendedFixture, FullFixture> map, float offset) {
		ArrayList<FinalEntry> fulls = new ArrayList<>();

		for (FinalEntry f : finals) {
			boolean isOver = f.prediction > f.threshold;
			FullEntry full = new FullEntry(f.fixture, f.prediction, f.result, f.threshold, f.lower, f.upper, null);
			full = fullControls(full,map);
			fulls.add(full);
		}

		return fulls;
	}

	public static ArrayList<String> getTeamsList(ArrayList<ExtendedFixture> odds) {
		ArrayList<String> result = new ArrayList<>();
		for (ExtendedFixture i : odds) {
			if (!result.contains(i.homeTeam))
				result.add(i.homeTeam);
			if (!result.contains(i.awayTeam))
				result.add(i.awayTeam);
		}
		return result;
	}

	/**
	 * 
	 * @param team
	 * @param fixtures
	 * @return list of the fixtures for the given team
	 */
	public static ArrayList<ExtendedFixture> getFixturesList(String team, ArrayList<ExtendedFixture> fixtures) {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		for (ExtendedFixture i : fixtures)
			if (i.homeTeam.equals(team) || i.awayTeam.equals(team))
				result.add(i);

		return result;
	}
}
