package main;

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author fourplus <fourplus1718@gmail.com>
 * @since 1.0
 * @version 11 Changes done
 */
public class PlayerFixture {
    /**
     * This field sets the variable of class ExtendedFixture
     */
	public ExtendedFixture fixture;
    /**
     * This field sets the variable of class String
     */
	public String team;
    /**
     * This field sets the variable of class String
     */
	public String name;
    /**
     * This field sets the int variable
     */
	public int minutesPlayed;
    /**
     * This field sets the boolean variable
     */
	public boolean lineup;
    /**
     * This field sets the boolean variable
     */
	public boolean substitute;
    /**
     * This field sets the int variable
     */
	public int goals;
    /**
     * This field sets the int variable
     */
	public int assists;

	public PlayerFixture(ExtendedFixture fixture, String team, String name, int minutesPlayed, boolean lineup,
			boolean substitute, int goals, int assists) {
		super();
		this.fixture = fixture;
		this.team = team;
		this.name = name;
		this.minutesPlayed = minutesPlayed;
		this.lineup = lineup;
		this.substitute = substitute;
		this.goals = goals;
		this.assists = assists;
	}

	public String toString() {
		return fixture + "/n" + name + " " + lineup + " " + minutesPlayed + "' " + goals + " " + assists;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fixture == null) ? 0 : fixture.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}

	private static boolean controlFixture(PlayerFixture other)
	{
		if (fixture == null && other.fixture != null)
			return false;
		if (fixture != null && !fixture.equals(other.fixture))
			return false;
	}
	
	private static boolean controlName(PlayerFixture other)
	{
		if (name == null && other.name != null)
			return false;
		if (name != null && !name.equals(other.name))
			return false;
	}
	
	private static boolean controlTeam(PlayerFixture other)
	{
		if (team == null && other.team != null)
			return false;
		if (team != null && !team.equals(other.team))
			return false;

	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PlayerFixture))
			return false;
		PlayerFixture other = (PlayerFixture) obj;
		controlFixture(other);
		controlName(other);
		controlTeam(other);
		return true;
	}
	
	
}
