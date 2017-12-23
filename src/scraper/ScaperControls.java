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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class ScaperControls {

	static void controlFileFinally(FileInputStream file) {
		if (file != null) {
			try {
				file.close (); // OK
				} catch (java.io.IOException e3) {
				System.out.println("I/O Exception");
			}
		}
	}
	
	private static Elements createElements(){
		return new Elements();
	}
	
	private static Actions createAction(WebDriver driver){
		return new Actions(driver);	
	}
	
	private static void controlCollectUpToDate(WebDriver driver, Set<ExtendedFixture> set, Date yesterday) {
		while (true) {
			int setSize = set.size();
			String html = driver.getPageSource();
			Document matches = Jsoup.parse(html);

			boolean breakFlag = false;
			Element list = matches.select("table[class=matches   ]").first();
			Elements linksM = list.select("a[href]");
			Elements fixtures = createElements();
			for (Element linkM : linksM) {
				if (isScore(linkM.text())) {
					fixtures.add(linkM);
				}
			}

			for (int i = fixtures.size() - 1; i >= 0; i--) {
				Document fixture = Jsoup.connect(BASE + fixtures.get(i).attr("href")).timeout(0).get();
				ExtendedFixture ef = getFixture(fixture, competition);
				if (ef != null && ef.date.before(yesterday)) {
					breakFlag = true;
					break;
				}
				result.add(ef);
				set.add(ef);
			}

			if (breakFlag)
				break;
	}
	
	static ArrayList<ExtendedFixture> collectUpToDate(String competition, int currentYear, Date yesterday,
			String add) throws IOException, ParseException, InterruptedException {
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

		// try {
		// WebDriverWait wait = new WebDriverWait(driver, 10);
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hstp_14536_interstitial_pub"))).click();
		// System.out.println("Successfully closed efbet add");
		// } catch (Exception e) {
		// System.out.println("Problem closing efbet add");
		// }

		controlCollectUpToDate(driver, set, yesterday, );
		/**
		while (true) {
			int setSize = set.size();
			String html = driver.getPageSource();
			Document matches = Jsoup.parse(html);

			boolean breakFlag = false;
			Element list = matches.select("table[class=matches   ]").first();
			Elements linksM = list.select("a[href]");
			Elements fixtures = createElements();
			for (Element linkM : linksM) {
				if (isScore(linkM.text())) {
					fixtures.add(linkM);
				}
			}

			for (int i = fixtures.size() - 1; i >= 0; i--) {
				Document fixture = Jsoup.connect(BASE + fixtures.get(i).attr("href")).timeout(0).get();
				ExtendedFixture ef = getFixture(fixture, competition);
				if (ef != null && ef.date.before(yesterday)) {
					breakFlag = true;
					break;
				}
				result.add(ef);
				set.add(ef);
			}

			if (breakFlag)
				break;
				
			*/

			Actions actions = createAction(driver);
			actions.moveToElement(driver.findElement(By.className("previous"))).click().perform();
			Thread.sleep(1000);
			String htmlAfter = driver.getPageSource();

			if (html.equals(htmlAfter))
				break;

			// Additional stopping condition - no new entries
			// if (set.size() == setSize)
			// break;

		}

		driver.close();

		if (result.size() != set.size())
			System.out.println("size problem of shots data");

		ArrayList<ExtendedFixture> setlist = new ArrayList<>();
		set.addAll(result);
		setlist.addAll(set);
		return setlist;
	}

	static void controlList(ArrayList<ExtendedFixture> list, String competition, int collectYear){
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

	static void controlFirstDoubleFor(ArrayList<ExtendedFixture> all, 
			ArrayList<ExtendedFixture> combined, ArrayList<ExtendedFixture> toAdd) {
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
	
	static void controlSecondDoubleFor(ArrayList<ExtendedFixture> toAdd, ArrayList<ExtendedFixture> next) {
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

	private static ArrayList<String> createArrayListString(){
		return new ArrayList<String>();
	}	

	static controlOddsUpToDate(WebDriver driver, String address, String competition, 
			Date yesterday, Set<ExtendedFixture> result) {
		boolean breakFlag = false;
		try {
			for (int page = 1; page <= maxPage; page++) {
				driver.navigate().to(address + "/results/#/page/" + page + "/");

				String[] splitAddress = address.split("/");
				String leagueYear = splitAddress[splitAddress.length - 1];
				List<WebElement> list = driver.findElements(By.cssSelector("a[href*='" + leagueYear + "']"));
				ArrayList<String> links = createArrayListString();
				for (WebElement i : list) {
					// better logic here?
					Thread.sleep(100);
					if (i.getText().contains("-") && isFixtureLink(i.getAttribute("href")))
						links.add(i.getAttribute("href"));
					
				for (String i : links) {
					ExtendedFixture ef = getOddsFixture(driver, i, competition, false, OnlyTodayMatches.FALSE);

					if (ef != null && ef.date.before(yesterday)) {
						breakFlag = true;
						break;
					}
					result.add(ef);
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Something was wrong");
				//e.printStackTrace();
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
}
	
