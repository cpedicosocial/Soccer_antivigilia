package scraper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 36 Changes done
 */
public class ScraperControls_2 {

	static void controlFileFinally(FileInputStream file) {
		if (file != null) {
			try {
				file.close(); // OK
			} catch (java.io.IOException e3) {
				System.out.println("I/O Exception");
			}
		}
	}

	static Elements createElements() {
		return new Elements();
	}

	static Actions createAction(WebDriver driver) {
		return new Actions(driver);
	}
	
	static void controlCollectUpToDate(WebDriver driver, Set<ExtendedFixture> set, Date yesterday,
			ArrayList<ExtendedFixture> result, String competition) {
		while (true) {
			int setSize = set.size();
			String html = driver.getPageSource();
			Document matches = Jsoup.parse(html);

			boolean breakFlag = false;
			Element list = matches.select("table[class=matches   ]").first();
			Elements linksM = list.select("a[href]");
			Elements fixtures = createElements();

			control2ForCollectUpToDate(linksM, fixtures, competition, yesterday, set, result);

			if (breakFlag)
				break;
		}

		Actions actions = createAction(driver);
		actions.moveToElement(driver.findElement(By.className("previous"))).click().perform();
		Thread.sleep(1000);
		String htmlAfter = driver.getPageSource();

		if (html.equals(htmlAfter))
			break;
	}

	static ArrayList<ExtendedFixture> collectUpToDate(String competition, int currentYear, Date yesterday, String add)
			throws IOException, ParseException, InterruptedException {
		ArrayList<ExtendedFixture> result = new ArrayList<>();
		Set<ExtendedFixture> set = new HashSet<>();
		String address;
		if (add == null) {
			address = EntryPoints.getLink(competition, currentYear);
			System.out.println(address);
		} else
			address = add;
		System.setProperty("webdriver.chrome.drive", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		driver.navigate().to(address);

		controlCollectUpToDate(driver, set, yesterday, result, competition);

		driver.close();

		if (result.size() != set.size())
			System.out.println("size problem of shots data");

		ArrayList<ExtendedFixture> setlist = new ArrayList<>();
		set.addAll(result);
		setlist.addAll(set);
		return setlist;
	}

	static void controlList(ArrayList<ExtendedFixture> list, String competition, int collectYear, Date oldestTocheck) {
		int count = 0;
		int maxTries = 1;
		try {
			while (true) {
				list = collectUpToDate(competition, collectYear, Utils.getYesterday(oldestTocheck), null);
				break;
			}
		} catch (Exception e) {
			if (++count == maxTries)
				throw e;
		}
	}

	static void controlFirstDoubleFor(ArrayList<ExtendedFixture> all, ArrayList<ExtendedFixture> combined,
			ArrayList<ExtendedFixture> toAdd) {

		for (ExtendedFixture i : all) {
			boolean continueFlag = false;
			for (ExtendedFixture comb : combined) {
				if (i.homeTeam.equals(comb.homeTeam) && i.awayTeam.equals(comb.awayTeam)
						&& (Math.abs(i.date.getTime() - comb.date.getTime()) <= 24 * 60 * 60 * 1000)) {
					continueFlag = true;
				}
			}
			if (!continueFlag)
				toAdd.add(i);
		}
	}

	static void controlSecondDoubleFor(ArrayList<ExtendedFixture> toAdd, ArrayList<ExtendedFixture> next,
			ArrayList<ExtendedFixture> withNext) {
		for (ExtendedFixture i : toAdd) {
			boolean continueFlag = false;
			for (ExtendedFixture n : next) {
				if (i.homeTeam.equals(n.homeTeam) && i.awayTeam.equals(n.awayTeam)
						&& (Math.abs(i.date.getTime() - n.date.getTime()) <= 24 * 60 * 60 * 1000)) {
					continueFlag = true;
					break;
				}
			}
			if (!continueFlag)
				withNext.add(i);
		}
	}

	static ArrayList<String> createArrayListString() {
		return new ArrayList<String>();
	}

	static void control1of2ForOddsUpToDate(WebDriver driver, String competition,
			ArrayList<String> links,  Date yesterday, boolean breakFlag, Set<ExtendedFixture> result) {
		for (String i : links) {
			ExtendedFixture ef = getOddsFixture(driver, i, competition, false, OnlyTodayMatches.FALSE);

			if (ef != null && ef.date.before(yesterday)) {
				breakFlag = true;
				break;
			}
			result.add(ef);
		}
	}
	
	static void control2ForOddsUpToDate(WebDriver driver, List<WebElement> list, String competition,
			ArrayList<String> links, boolean breakFlag, Date yesterday, Set<ExtendedFixture> result) {
		for (WebElement i : list) {
			// better logic here?
			Thread.sleep(100);
			if (i.getText().contains("-") && isFixtureLink(i.getAttribute("href")))
				links.add(i.getAttribute("href"));
			
			control1of2ForOddsUpToDate(links, driver, competition, yesterday, breakFlag, result);
		}
	}

	static void controlOddsUpToDate(WebDriver driver, String address, String competition, Date yesterday,
			Set<ExtendedFixture> result, int maxPage) {
		boolean breakFlag = false;
		try {
			for (int page = 1; page <= maxPage; page++) {
				driver.navigate().to(address + "/results/#/page/" + page + "/");

				String[] splitAddress = address.split("/");
				String leagueYear = splitAddress[splitAddress.length - 1];
				List<WebElement> list = driver.findElements(By.cssSelector("a[href*='" + leagueYear + "']"));
				ArrayList<String> links = createArrayListString();

				control2ForOddsUpToDate(driver, list, competition, links, breakFlag, yesterday, result);
			}
		} catch (Exception e) {
			System.out.println("Something was wrong");
			// e.printStackTrace();
			page--;
			System.out.println("Starting over from page:" + page);
			driver.close();
			Thread.sleep(20000);
			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.manage().window().maximize();

			driver.navigate().to(address + "/results/");
			login(driver);
			driver.navigate().to(address + "/results/");
		}
	}

	static void login(WebDriver driver) {
		String label = "Login";
		driver.findElement(By.xpath("//button[contains(.,'" + label + "')]")).click();

		String pass = "";
		Path wiki_path = Paths.get(new File("").getAbsolutePath(), "pass.txt");

		Charset charset = Charset.forName("ISO-8859-1");
		try {
			List<String> lines = Files.readAllLines(wiki_path, Charset.defaultCharset());

			for (String line : lines)
				pass += (line.trim());
		} catch (IOException e) {
			System.out.println("Something was wrong");
		}

		driver.findElement(By.id("login-username1")).sendKeys("Vortex84");
		driver.findElement(By.id("login-password1")).sendKeys(pass);

		// click the local login button
		driver.findElements(By.xpath("//button[contains(.,'" + label + "')]")).get(1).click();
	}

	static void controlPages(List<WebElement> pages, int maxPage) {

		for (WebElement i : pages) {
			if (isNumeric(i.getText()) && Integer.parseInt(i.getText().trim()) > maxPage)
				maxPage = Integer.parseInt(i.getText().trim());
		}
	}

	static void controlListLinks(List<WebElement> list, ArrayList<String> links) {
		for (WebElement i : list) {
			// better logic here?
			if (i.getText().contains("-") && isFixtureLink(i.getAttribute("href")))
				links.add(i.getAttribute("href"));
		}
	}

	static boolean isFixtureLink(String attribute) {
		String[] split = attribute.split("/");
		String fixturePart = split[split.length - 1];
		String[] split2 = fixturePart.split("-");
		if (split2.length < 3)
			return false;
		else {
			if (split2[split2.length - 1].length() == 8)
				return true;
			else
				return false;
		}

	}

	static void controlLinks(ArrayList<String> links, WebDriver driver, String competition,
			Set<ExtendedFixture> result) {
		for (String i : links) {
			ExtendedFixture ef = getFastOddsFixture(driver, i, competition);
			if (ef != null)
				result.add(ef);

			break;
		}
	}
	
	static void controlOneResElement(WebElement resElement, String resString, boolean liveMatchesFlag){
		if (resElement != null && (resString.contains("penalties") || resString.contains("ET") || resString.contains("Postponed"))) {
			return null;
		}
		if (resElement != null && ((!liveMatchesFlag && resString.contains("already started")) || resString.contains("Abandoned"))) {
			return null;
		}
	}
	
	static void controlTwoResElement(WebElement resElement, String resString, Result fullResult, Result htResult){
		if (resElement != null && (resString.contains("awarded") && resString.contains(home))) {
			fullResult = new Result(3, 0);
			htResult = new Result(3, 0);
		}
		if (resElement != null && (resString.contains("awarded") && resString.contains(away))) {
			fullResult = new Result(0, 3);
			htResult = new Result(0, 3);
		}
	}

	static void controlIfElseNotNull(WebElement resElement, String resString, Result fullResult, Result htResult){
		if (resElement != null && (resString.contains("(") && resString.contains(")"))) {
			String full = resString.split(" ")[2];
			String half = resString.split(" ")[3].substring(1, 4);
			fullResult = new Result(Integer.parseInt(full.split(":")[0]), Integer.parseInt(full.split(":")[1]));
			htResult = new Result(Integer.parseInt(half.split(":")[0]), Integer.parseInt(half.split(":")[1]));
		} else {
			fullResult = new Result(-1, -1);
			htResult = new Result(-1, -1);
		}
	}
	
	static void controlPinnIndex(WebElement row, List<WebElement> customer, int pinnIndex){
		if (row.getText().contains("Pinnacle"))
			pinnIndex = customer.indexOf(row) + 1;
		if (row.getText().contains("bet365"))
			pinnIndex = customer.indexOf(row) + 1;
	}

	static void controlClick(WebElement tabs){
		for (WebElement t : tabs) {
			if (t.getText().contains("O/U")) {
				t.click();
				break;
			}
		}
	}
	
	static void controlDiv(List<WebElement> divs, WebElement div25, WebDriver driver){
		for (WebElement div : divs) {
			if (div.getText().contains("+2.5")) {
				div25 = div;
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("window.scrollBy(0,250)", "");
				div.click();
				break;
			}
		}
	}
	
	static void controlfirstOdds(WebElement row, float overOdds, float underOdds){
		if (row.getText().contains("Pinnacle")) {
			String textOdds = row.getText();
			overOdds = Float.parseFloat(textOdds.split("\n")[2].trim());
			underOdds = Float.parseFloat(textOdds.split("\n")[3].trim());
			break;
		}
	}
	
	static void controlOdds(WebElement row, float overOdds, float underOdds, float overOdds365, float underOdds365){
		controlfirstOdds(row, overOdds, underOdds);
		
		if (row.getText().contains("bet365")) {
			String textOdds = row.getText();
			try {
				overOdds365 = Float.parseFloat(textOdds.split("\n")[2].trim());
				underOdds365 = Float.parseFloat(textOdds.split("\n")[3].trim());
			} catch (Exception e) {
				// nothing
			}
		}
	}
	
	static void controlForOdds(WebElement row, List<WebElement> rows, float overOdds, float underOdds, float overOdds365, float underOdds365){
		for (WebElement row : rows) {
			controlOdds(row, overOdds, underOdds, overOdds365, underOdds365);
		}
	}
	
	static void controlFloat(float overOdds, float overOdds365, float underOdds, float underOdds365){
		if (Float.compare(overOdds, -1f)==0) {
			overOdds = overOdds365;
			underOdds = underOdds365;
		}
	}
	
	static void controlOUTable(WebElement div25, float overOdds, float underOdds, float overOdds365, float underOdds365){
		if (div25 != null) {
		WebElement OUTable = div25.findElement(By.xpath("//table"));

		// find the row
		List<WebElement> rows = OUTable.findElements(By.xpath("//tr"));

		controlForOdds(row, rows, overOdds, underOdds, overOdds365, underOdds365);
		controlFloat(overOdds, overOdds365, underOdds, underOdds365);
		}
	}
	
	static void controlContainsAh(List<WebElement> tabs, String s){
		for (WebElement t : tabs) {
			if (t.getText().contains(s)) {
				t.click();
				break;
			}
		}
	}
	
		
}
