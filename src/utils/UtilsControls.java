package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

class UtilsControls{
	
	public static String query(String address) throws IOException {
		if (--count == 0)
			try {
				long now = System.currentTimeMillis();
				System.out.println("Sleeping for " + (61 * 1000 - (now - start)) / 1000);
				long time = 61 * 1000 - (now - start);
				Thread.sleep(time < 0 ? 61 : time);
				count = 50;
				start = System.currentTimeMillis();
			}catch (InterruptedException e1) {
				System.out.println("Something was wrong");
				//e1.printStackTrace();
			}catch(SSLException ssle) {
			    logSecurityIssue(ssle); //
			    terminateInsecureConnection();
			}
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.addRequestProperty("X-Auth-Token", TOKEN);
		InputStreamReader isr = null;
		isr = new InputStreamReader(conn.getInputStream());
		BufferedReader bfr = new BufferedReader(isr);
		String output;
		StringBuffer sb = new StringBuffer();
		while ((output = bfr.readLine()) != null) {
			sb.append(output);
		}
		return sb.toString();
	}
	
	static float controlMonth(Calendar cal,int month,float bank,float betSize,FinalEntry i,int succ,int alls)
	{
		if (cal.get(Calendar.MONTH) == month) 
		{
			float gain = i.prediction >= i.upper ? i.fixture.maxOver : i.fixture.maxUnder;
			bank += betSize * (i.success() ? (gain - 1f) : -1f);
			succ += i.success() ? 1 : 0;
			alls++;
		}
	}
	static float controlNotMonth(Calendar cal,int month,float bank,float betSize,FinalEntry i,int succ,int alls,float previous,float percent)
	{
		if (cal.get(Calendar.MONTH) != month)  
		{
			System.out.println("Bank after month: " + (month + 1) + " is: " + bank + " unit: " + betSize
					+ " profit: " + (bank - previous) + " in units: " + (bank - previous) / betSize + " rate: "
					+ (float) succ / alls + "%");
			previous = bank;
			betSize = bank * percent;
			month = cal.get(Calendar.MONTH);
			float gain = i.prediction >= i.upper ? i.fixture.maxOver : i.fixture.maxUnder;
			bank += betSize * (i.success() ? (gain - 1f) : -1f);
			alls = 1;
			succ = i.success() ? 1 : 0;
		}
	}
	
	static float controlAvg(float avgShotsUnder,float avgShotsOver)
	{
		if (avgShotsUnder > avgShotsOver) 
		{
			return 0.5f;
		}
		return 0f;
	}
	static float controlExpectedGreater(float expected,float avgShotsUnder,float avgShotsOver,float dist)
	{
		if (expected >= avgShotsOver && expected > avgShotsUnder) {
			float score = 0.5f + 0.5f * (expected - avgShotsOver) / dist;
			return (score >= 0 && score <= 1f) ? score : 1f;
		}
		return 0f;
	}
	static float controlExpectedSmaller(float expected,float avgShotsUnder,float avgShotsOver,float dist)
	{
		if ((expected >= avgShotsOver && expected > avgShotsUnder) && expected <= avgShotsUnder && expected < avgShotsOver) {
			float score = 0.5f - 0.5f * (-expected + avgShotsUnder) / dist;
			return (score >= 0 && score <= 1f) ? score : 0f;
		} else 
		{
			// System.out.println(f);
			return 0.5f;
		}
	}
	
	static float controlLoop(int x,int y,float step,ArrayList<HTEntry> all,int ymax,
			MaximizingBy newMaxBy, float currentTH,
			float bestx,float besty,float bestz,float bestw,float bestTH,float bestEval,String bestDescription,
			float bestProfit,float bestWinRatio)
	{
		int zmax = ymax - y;
		for (int z = 0; z <= zmax; z++) {
			int w = zmax - z;
			System.out.println(x * step + " " + y * step + " " + z * step + " " + w * step);

			for (HTEntry hte : all) {
				hte.fe.prediction = x * step * hte.zero + y * step * hte.one + z * step * hte.two
						+ w * step * hte.more;
			}

			float currentProfit, currentWinRate = 0f;
			float currEval = 1f;
			
			currEval=controlNewMaxBy1(newMaxBy,all,currentProfit,currEval);
			currEval=controlNewMaxBy2(newMaxBy,all,currentProfit,currEval);
			currEval=controlNewMaxBy3(newMaxBy,all,currentProfit,currEval);
			System.out.println(currentProfit);
			System.out.println("1 in " + currEval);

			currEval=UtilsControls.controlCurrEval(currEval,bestEval,bestProfit,currentProfit,
					bestWinRatio,currentWinRate,bestx,step,x,besty,y,bestz,z,bestw,w,bestTH,currentTH,
					bestDescription);
		}
	}
	private static float controlNewMaxBy1(MaximizingBy newMaxBy,ArrayList<HTEntry> all,float currentProfit,float currEval)
	{
		if (newMaxBy.equals(MaximizingBy.BOTH) && all.size() >= 100) {
			currentProfit = getProfitHT(all);
			currEval = evaluateRecord(getFinals(all));
		}
	}
	private static float controlNewMaxBy2(MaximizingBy newMaxBy,ArrayList<HTEntry> all,float currentProfit,float currEval)
	{
		if (!newMaxBy.equals(MaximizingBy.BOTH) && newMaxBy.equals(MaximizingBy.UNDERS) && onlyUnders(getFinals(all)).size() >= 100) {
			currentProfit = getProfitHT(onlyUndersHT(all));
			currEval = evaluateRecord(onlyUnders(getFinals(all)));
		}
	}
	private static float controlNewMaxBy3(MaximizingBy newMaxBy,ArrayList<HTEntry> all,float currentProfit,float currEval)
	{
		if (newMaxBy.equals(MaximizingBy.OVERS) && onlyOvers(getFinals(all)).size() >= 100) {
			currentProfit = getProfitHT(onlyOversHT(all));
			currEval = evaluateRecord(onlyOvers(getFinals(all)));
		} else {
			currentProfit = Float.NEGATIVE_INFINITY;
		}
	}
	private static float controlCurrEval(float currEval,float bestEval,float bestProfit,float currentProfit,
			float bestWinRatio,float currentWinRate,float bestx,float step,int x,float besty,int y,float bestz,int z,float bestw,int w,
			float bestTH,float currentTH,String bestDescription)
	{
		if (/* currentProfit > bestProfit */ currEval > bestEval/*
				 * currentWinRate
				 * >
				 * bestWinRatio
				 */) {
			bestProfit = currentProfit;
			bestEval = currEval;
			bestWinRatio = currentWinRate;
			bestx = step * x;
			besty = step * y;
			bestz = step * z;
			bestw = step * w;
			bestTH = currentTH;
			bestDescription = x * step + "*zero + " + y * step + "*one + " + z * step + " *two+ "
					+ w * step + " *>=3";
			// System.out.println(bestProfit);
			// System.out.println("1 in " + bestEval);
		}
		return bestEval;
	}

	static float controlNewMaxBy5(MaximizingBy newMaxBy,ArrayList<HTEntry> all)
	{
		if (newMaxBy.equals(MaximizingBy.UNDERS))
			all = onlyUndersHT(all);
		if (!newMaxBy.equals(MaximizingBy.UNDERS) && newMaxBy.equals(MaximizingBy.OVERS))
			all = onlyOversHT(all);
		return all;
	}
	
	static float controlNewMaxByAll(MaximizingBy newMaxBy,ArrayList<HTEntry> all,float currentProfit,float currEval)
	{
		if (newMaxBy.equals(MaximizingBy.BOTH) && all.size() >= 100) {
			currentProfit = getProfitHT(all);
			currEval = evaluateRecord(getFinals(all));
			// currentWinRate = getSuccessRate(getFinals(all));
		}
		return currEval;
	}
	static float controlNewMaxByUnders(MaximizingBy newMaxBy,ArrayList<HTEntry> all,float currentProfit,float currEval)
	{
		if (newMaxBy.equals(MaximizingBy.UNDERS) && onlyUnders(getFinals(all)).size() >= 100) {
			currentProfit = getProfitHT(onlyUndersHT(all));
			currEval = evaluateRecord(onlyUnders(getFinals(all)));
			// currentWinRate
			// =getSuccessRate(onlyUnders(getFinals(all)));

		}
	}
	static float controlNewMaxByOvers(MaximizingBy newMaxBy,ArrayList<HTEntry> all,float currentProfit,float currEval)
	{
		if (newMaxBy.equals(MaximizingBy.OVERS) && onlyOvers(getFinals(all)).size() >= 100) {
			currentProfit = getProfitHT(onlyOversHT(all));
			currEval = evaluateRecord(onlyOvers(getFinals(all)));
			// currentWinRate
			// =getSuccessRate(onlyOvers(getFinals(all)));
		} else {
			currentProfit = Float.NEGATIVE_INFINITY;
		}
		return currEval;
	}
	static String controlNewMaxByFinal(float currEval,float bestEval,float currentProfit,float bestProfit,float step,float bestx,int x,
			float besty,int y,String bestDescription)
	{
		if (/* currentProfit > bestProfit */ currEval > bestEval/*
				 * currentWinRate
				 * >
				 * bestWinRatio
				 */) {
			bestProfit = currentProfit;
			bestEval = currEval;
			bestx = step * x;
			besty = step * y;
			bestDescription = x * step + "*zero + " + y * step + "*one + ";
			// System.out.println(bestProfit);
			// System.out.println("1 in " + bestEval);
		}
		return bestDescription;
	}
	static ArrayList<HTEntry> controlNewMaxByEquals(MaximizingBy newMaxBy,ArrayList<HTEntry> all)
	{
		if (newMaxBy.equals(MaximizingBy.UNDERS))
			all = onlyUndersHT(all);
		if (!newMaxBy.equals(MaximizingBy.UNDERS) && newMaxBy.equals(MaximizingBy.OVERS))
			all = onlyOversHT(all);
		return all;
	}
	
	
	private static float methodOneBestNperWeek(ArrayList<FinalEntry> curr, float coeff){
		for (int j = 0; j < n; j++) {
			if (curr.get(j).success()) {
				coeff *= curr.get(j).prediction >= curr.get(j).upper ? curr.get(j).fixture.maxOver
						: curr.get(j).fixture.maxUnder;
				successes++;
				notlosses++;
			}else if ( (curr.get(j).prediction >= curr.get(j).upper
					&& curr.get(j).fixture.getTotalGoals() == 2)
					|| (curr.get(j).prediction <= curr.get(j).lower
							&& curr.get(j).fixture.getTotalGoals() == 3)) {
				notlosses++;
				coeff = -1f;
				break;
			} else {
				coeff = -1f;
				break;
			}
		}
		return coeff;
	}

	static float methodTwoBestNperWeek(ArrayList<FinalEntry> curr, ArrayList<FinalEntry> filtered, float profit, int winBets, int loseBets, Date currDate){
		Date date = filtered.get(i).fixture.date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		Date next = cal.getTime();
		if (date.equals(currDate) || date.equals(next)) {
			curr.add(filtered.get(i));
		}else if (i + 1 < filtered.size()) {
			currDate = filtered.get(i + 1).fixture.date;
			curr.sort(new Comparator<FinalEntry>() {
				@Override
				public int compare(FinalEntry o1, FinalEntry o2) {
					Float certainty1 = o1.getCOT();
					Float certainty2 = o2.getCOT();
					return certainty2.compareTo(certainty1);
				}
			});
			boolean flag = true;
			float coeff = 1f;
			int successes = 0;
			int notlosses = 0;
			if (curr.size() >= n) {
				// System.out.println(curr);
				coeff = methodOneBestNperWeek(curr,coeff);					
				// System.out.println(curr.get(0).fixture.date + " " + "
				// " + successes + " not loss: " + notlosses
				// + " pr: " + (coeff != -1f ? (coeff - 1f) : coeff));
				if (coeff != -1f)
					winBets++;
				else
					loseBets++;

				profit += (coeff != -1f ? (coeff - 1f) : coeff);
			}
			curr = new ArrayList<>();
		} else {
			break;
		}
		return profit;
	}
	
	static void isVerboseSystemOutPrint(boolean verbose, Stats object){
		if (verbose)
			System.out.println(object);
	}
	
	static void statsControl(Stats st,ArrayList<Stats> normalizedStats){
		if (st.getPvalueOdds() > 4 && !st.all.isEmpty())
			normalizedStats.add(new NormalizedStats(st.all, "norm " + st.description));
	}
	
	static boolean samePrediction(ArrayList<FinalEntry>[] lists, int index) {

		boolean flag = true;
		for (ArrayList<FinalEntry> i : lists) {
			for (ArrayList<FinalEntry> j : lists) {
				if (!samePrediction(i.get(index), j.get(index)))
					flag = false;
			}
		}
		return flag;
	}
	
	static FinalEntry getFE(ArrayList<FinalEntry> finals2, FinalEntry fe) {
		for (FinalEntry i : finals2) {
			if (i.fixture.equals(fe.fixture))
				return i;
		}
		return null;
	}
	
	static HashMap<String, Float> thresholdsByLeague(ArrayList<FinalEntry> all) {
		HashMap<String, Float> result = new HashMap<>();

		for (FinalEntry i : all)
			if (result.containsKey(i.fixture.competition))
				continue;
			else
				result.put(i.fixture.competition, i.threshold);
		return result;
	}
	static void trueOddsEqual(ArrayList<FinalEntry> all) {
		for (FinalEntry i : all) {
			float sum = 1f / i.fixture.maxOver + 1f / i.fixture.maxUnder;
			i.fixture.maxOver = 1f / ((1f / i.fixture.maxOver) / sum);
			i.fixture.maxUnder = 1f / ((1f / i.fixture.maxUnder) / sum);

			if (i.fixture.asianHome > 1f && i.fixture.asianAway > 1f) {
				float sumAsian = 1f / i.fixture.asianHome + 1f / i.fixture.asianAway;
				i.fixture.asianHome = 1f / ((1f / i.fixture.asianHome) / sumAsian);
				i.fixture.asianAway = 1f / ((1f / i.fixture.asianAway) / sumAsian);
			}
		}
	}

	static void trueOddsProportional(ArrayList<FinalEntry> all) {
		for (FinalEntry i : all) {
			float margin = 1f / i.fixture.maxOver + 1f / i.fixture.maxUnder - 1f;
			i.fixture.maxOver = 2 * i.fixture.maxOver / (2f - margin * i.fixture.maxOver);
			i.fixture.maxUnder = 2 * i.fixture.maxUnder / (2f - margin * i.fixture.maxUnder);

			if (i.fixture.asianHome > 1f && i.fixture.asianAway > 1f) {
				float marginAsian = 1f / i.fixture.asianHome + 1f / i.fixture.asianAway - 1f;
				i.fixture.asianHome = 2 * i.fixture.asianHome / (2f - marginAsian * i.fixture.asianHome);
				i.fixture.asianAway = 2 * i.fixture.asianAway / (2f - marginAsian * i.fixture.asianAway);
			}
		}
	}
	
	private static void cotControls(FinalEntry fe,ArrayList<FinalEntry> cot5,ArrayList<FinalEntry> cot10,ArrayList<FinalEntry> cot15,ArrayList<FinalEntry> cot20,ArrayList<FinalEntry> cot25){
		float cot = fe.prediction > fe.threshold ? (fe.prediction - fe.threshold) : (fe.threshold - fe.prediction);
		if (cot >= 0.25f){
			cot25.add(fe);
		}else if (cot >= 0.2f){
			cot20.add(fe);
		}else if (cot >= 0.15f){
			cot15.add(fe);
		}else if (cot >= 0.10f){
			cot10.add(fe);
		}else if (cot >= 0.05f){
			cot5.add(fe);
		}
	}
	
	private static void cerControls(FinalEntry fe,ArrayList<FinalEntry> cer80,ArrayList<FinalEntry> cer70,ArrayList<FinalEntry> cer60,ArrayList<FinalEntry> cer50,ArrayList<FinalEntry> cer40){
		float certainty = fe.getCertainty();
		if (certainty >= 0.8f){
			cer80.add(fe);
		}else if (certainty >= 0.7f){
			cer70.add(fe);
		}else if (certainty >= 0.6f){
			cer60.add(fe);
		}else if (certainty >= 0.5f) {
			cer50.add(fe);
		} else {
			cer40.add(fe);
		}
	}

	static ArrayList<Stats> byCertaintyandCOT(ArrayList<FinalEntry> all, String prefix, boolean verbose) {
		ArrayList<Stats> result = new ArrayList<>();
		ArrayList<FinalEntry> cot5 = new ArrayList<>();
		ArrayList<FinalEntry> cot10 = new ArrayList<>();
		ArrayList<FinalEntry> cot15 = new ArrayList<>();
		ArrayList<FinalEntry> cot20 = new ArrayList<>();
		ArrayList<FinalEntry> cot25 = new ArrayList<>();
		ArrayList<FinalEntry> cer80 = new ArrayList<>();
		ArrayList<FinalEntry> cer70 = new ArrayList<>();
		ArrayList<FinalEntry> cer60 = new ArrayList<>();
		ArrayList<FinalEntry> cer50 = new ArrayList<>();
		ArrayList<FinalEntry> cer40 = new ArrayList<>();
		for (FinalEntry fe : all) {
			cerControls(fe,cer80,cer70,cer60,cer50,cer40);
			cotControls(fe,cot5,cot10,cot15,cot20,cot25);
		}

		isVerboseSystemOutPrint(verbose,new Stats(cer80, prefix + " " + "cer80"));
		isVerboseSystemOutPrint(verbose,new Stats(cer70, prefix + " " + "cer70"));
		isVerboseSystemOutPrint(verbose,new Stats(cer60, prefix + " " + "cer60"));
		isVerboseSystemOutPrint(verbose,new Stats(cer50, prefix + " " + "cer50"));
		isVerboseSystemOutPrint(verbose,new Stats(cer40, prefix + " " + "cer40"));
		
		result.add(new Stats(cer80, prefix + " " + "cer80"));
		result.add(new Stats(cer70, prefix + " " + "cer70"));
		result.add(new Stats(cer60, prefix + " " + "cer60"));
		result.add(new Stats(cer50, prefix + " " + "cer50"));
		result.add(new Stats(cer40, prefix + " " + "cer40"));

		isVerboseSystemOutPrint(verbose,new Stats(cot25, prefix + " " + "cot25"));
		isVerboseSystemOutPrint(verbose,new Stats(cot20, prefix + " " + "cot20"));
		isVerboseSystemOutPrint(verbose,new Stats(cot15, prefix + " " + "cot15"));
		isVerboseSystemOutPrint(verbose,new Stats(cot10, prefix + " " + "cot10"));
		isVerboseSystemOutPrint(verbose,new Stats(cot5, prefix + " " + "cot5"));
		
		result.add(new Stats(cot25, prefix + " " + "cot25"));
		result.add(new Stats(cot20, prefix + " " + "cot20"));
		result.add(new Stats(cot15, prefix + " " + "cot15"));
		result.add(new Stats(cot10, prefix + " " + "cot10"));
		result.add(new Stats(cot5, prefix + " " + "cot5"));

		return result;

	}
		static boolean flagControls(float bankroll,boolean flag){
		if (bankroll < 0) {
			flag = true;
		}
		return flag;
	}
	
}