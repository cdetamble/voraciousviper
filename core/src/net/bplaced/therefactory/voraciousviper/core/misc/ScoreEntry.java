package net.bplaced.therefactory.voraciousviper.core.misc;

public class ScoreEntry {

	private static final String FieldSeparator = ";";
	private String playerId = "";
	private String playerName = "";
	private String date = "";
	private int level;
	private int score;
	private int numSteps;

	@Override
	public String toString() {
		String sb = playerId + FieldSeparator +
				playerName + FieldSeparator +
				date + FieldSeparator +
				level + FieldSeparator +
				score + FieldSeparator +
				numSteps;
		return sb;
	}

	public static ScoreEntry fromString(String string) {
		String[] fields = string.split(FieldSeparator);
		ScoreEntry scoreEntry = new ScoreEntry();
		for (int i = 0; i < fields.length; i++) {
			switch (i) {
				case 0:
					scoreEntry.setPlayerId(fields[i]);
					break;
				case 1:
					scoreEntry.setPlayerName(fields[i]);
					break;
				case 2:
					scoreEntry.setDate(fields[i]);
					break;
				case 3:
					scoreEntry.setLevel(Integer.parseInt(fields[i]));
					break;
				case 4:
					scoreEntry.setScore(Integer.parseInt(fields[i]));
					break;
				case 5:
					scoreEntry.setNumSteps(Integer.parseInt(fields[i]));
					break;
				default:
					break;
			}
		}
		return scoreEntry;
	}

	public ScoreEntry() {
	}

	public String getName() {
		return playerName;
	}
	public void setPlayerName(String name) {
		if (name == null) name = "";
		this.playerName = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		if (date == null) date = "";
		this.date = date;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public void setNumSteps(int numSteps) {
		this.numSteps = numSteps;
	}
	public int getNumSteps() {
		return numSteps;
	}

	public void setPlayerId(String id) {
		this.playerId = id;
	}

	public String getId() {
		return playerId;
	}

}
