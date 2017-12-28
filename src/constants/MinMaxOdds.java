package constants;

import java.util.HashMap;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class MinMaxOdds {
	
	private final String LITERAL1 = "E0";
	private final String LITERAL2 = "EC";
	private final String LITERAL3 = "E1";
	private final String LITERAL4 = "E2";
	private final String LITERAL5 = "SC0";
	private final String LITERAL6 = "I1";
	private final String LITERAL7 = "I2";
	private final String LITERAL8 = "F1";
	private final String LITERAL9 = "T1";
	private final String LITERAL10 = "B1";
	private final String LITERAL11 = "F2";
	private final String LITERAL12 = "P1";
	private final String LITERAL13 = "D1";
	private final String LITERAL14 = "D2";
	private final String LITERAL15 = "SP2";
	private final String LITERAL16 = "E3";
	private final String LITERAL17 = "SP1";
	private final String LITERAL18 = "ENG";
	private final String LITERAL19 = "ENG2";
	private final String LITERAL20 = "ENG3";
	private final String LITERAL21 = "ENG4";
	private final String LITERAL22 = "ENG5";
	private final String LITERAL23 = "GER";
	private final String LITERAL24 = "SPA";
	private final String LITERAL25 = "IT";
	private final String LITERAL26 = "FR";
	
    /**
     * This field sets the array with dont
     */
	public static final String[] DONT = { LITERAL1, LITERAL2, LITERAL3, LITERAL4, LITERAL5, LITERAL6, LITERAL7, LITERAL8, LITERAL9, LITERAL10 };
    /**
     * This field sets the array with draw
     */
	public static final String[] DRAW = { LITERAL6, LITERAL7, LITERAL11, LITERAL12, LITERAL13, LITERAL14, LITERAL15 };
    /**
     * This field sets the array with all euro data
     */
	public static final String[] ALLEURODATA = { LITERAL1, LITERAL3, LITERAL4, LITERAL16, LITERAL2, LITERAL5, "SC1", "SC2", "SC3", LITERAL13, LITERAL14,
			LITERAL17, LITERAL15, LITERAL6, LITERAL7, LITERAL8, LITERAL11, "N1", LITERAL10, LITERAL12, LITERAL9, "G1" };
    /**
     * This field sets the array with shots
     */
	public static final String[] SHOTS = { LITERAL1, LITERAL3, LITERAL4, LITERAL16, LITERAL2, LITERAL5, LITERAL13, LITERAL17, LITERAL6, LITERAL8 };
    /**
     * This field sets the array with rest
     */
	public static final String[] REST = { "SC1", "SC2", "SC3", LITERAL14, LITERAL15, LITERAL7, LITERAL11, "N1", LITERAL10, LITERAL12, LITERAL9, "G1" };
    /**
     * This field sets the array with NBPTG
     */
	public static final String[] NBPTG = { "N1", LITERAL10, LITERAL12, LITERAL9, "G1" };
    /**
     * This field sets the array with PFS
     */
	public static final String[] PFS = { LITERAL1, LITERAL3, LITERAL4, LITERAL16, LITERAL2, LITERAL5, LITERAL17, LITERAL13, LITERAL6, LITERAL8 };
    /**
     * This field sets the array with shots equivalents
     */
	public static final String[] SHOTSEQUIVALENTS = { LITERAL18, LITERAL19, LITERAL20, LITERAL21, LITERAL22, LITERAL5, LITERAL23, LITERAL24, LITERAL25,
			LITERAL26 };
    /**
     * This field sets the array with shots dont
     */
	public static final String[] SHOTSDONT = { LITERAL6, LITERAL8, LITERAL3, LITERAL16 };
    /**
     * This field sets the array with manual
     */
	public static final String[] MANUAL = { "ARG", "ARG2", "BRA", "BRB", "SWE", "NOR", "USA", "ICE", "FIN", "JP", "SWI",
			"DEN", "AUS", "CZE", "RUS", "NED", "POR", "BEL", "FR2", "TUR", "GRE", "HUN", LITERAL14, "IT2", "POL", LITERAL25,
			LITERAL24, "SPA2", LITERAL23, "GER2", LITERAL26, LITERAL18, "SCO", LITERAL19, LITERAL20, LITERAL21, LITERAL22, "BUL", "CRO", "SLO",
			"SLK" };

    /**
     * This field sets the array with fakebooks
     */
	public static final String[] FAKEBOOKS = { "Tempobet", "Interwetten", "Vulkanbet", "Scandibet", "Island Casino",
			"Betser", "Bethard", "Vernons", "18bet", "BetOlimp", "TonyBet", "Betsafe", "Intertops", "Betcruise",
			"ComeOn", "NordicBet", "RealDealBet", "BetOnline", "ScandiBet", "Asianodds", "Jetbull", "youwin" };

    /**
     * This field sets the array with full
     */
	public static final String[] FULL = { "SWI", "BRA", LITERAL18, LITERAL24 };

    /**
     * This field sets the hashmap with equivalents
     */
	public static HashMap<String, String> equivalents = new HashMap<>();
    /**
     * This field sets the hashmap with reverse equivalents
     */
	public static HashMap<String, String> reverseEquivalents = new HashMap<>();

	static {
		equivalents.put(LITERAL25, LITERAL6);
		equivalents.put(LITERAL18, LITERAL1);
		equivalents.put(LITERAL19, LITERAL3);
		equivalents.put(LITERAL20, LITERAL4);
		equivalents.put(LITERAL21, LITERAL16);
		equivalents.put(LITERAL22, LITERAL2);
		equivalents.put(LITERAL23, LITERAL13);
		equivalents.put(LITERAL26, LITERAL8);
		equivalents.put("SCO", LITERAL5);
		equivalents.put(LITERAL24, LITERAL17);
		equivalents.put("SPA2", LITERAL15);
		// -----------------------
		reverseEquivalents.put(LITERAL6, LITERAL25);
		reverseEquivalents.put(LITERAL1, LITERAL18);
		reverseEquivalents.put(LITERAL3, LITERAL19);
		reverseEquivalents.put(LITERAL4, LITERAL20);
		reverseEquivalents.put(LITERAL16, LITERAL21);
		reverseEquivalents.put(LITERAL2, LITERAL22);
		reverseEquivalents.put(LITERAL13, LITERAL23);
		reverseEquivalents.put(LITERAL8, LITERAL26);
		reverseEquivalents.put(LITERAL5, "SCO");
		reverseEquivalents.put(LITERAL17, LITERAL24);
		reverseEquivalents.put(LITERAL15, "SPA2");
	}

}