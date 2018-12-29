package PSI17;

import java.util.Collections;

public class MinMaxDecisor extends GameAI {

	// Constructor
	public MinMaxDecisor(int n, int s, int r, int i, int p) {
		super(n, s, r, i, p);
	}

	// Empty Constructor
	public MinMaxDecisor() {
	}

	public int decide(String mode, int resultPosition) {
		if (mode.equals("VANISHING")) {
			return decideVanishingMove(resultPosition);
		}
		return decideMove(resultPosition);
	}

	public int decid2(String mode, int resultPosition) {
		if (mode.equals("VANISHING")) {
			return decideVanishingMove(resultPosition);
		}
		return decideMove(resultPosition);
	}

	/** The algorithm decides a move based on the min-max estrategy. */
	public int decideMove(int resultPosition) {

		float[] rows = new float[super.getS()];
		float[] columns = new float[super.getS()];

		float maxInRow = 0;
		float maxInColumn = 0;

		int bestRowPosition = 0;
		int bestColumnPosition = 0;

		for (int i = 0; i < super.getS(); i++) {
			for (int j = 0; j < super.getS(); j++) {

				float rowValue = (Float.parseFloat(super.getGameMatrix()[i][j].charAt(0) + ""));
				float columnValue = (Float.parseFloat(super.getGameMatrix()[j][i].charAt(2) + ""));

				if ((!super.getGameMatrix()[i][j].equals("*")) && ((rowValue < rows[i]) || (rows[i] == 0))) {
					rows[i] = rowValue;
				}

				if ((!super.getGameMatrix()[j][i].equals("*")) && ((columnValue < columns[i]) || (columns[i] == 0))) {
					columns[i] = columnValue;
				}

			}
		}

		for (int i = 0; i < super.getS(); i++) {
			if (rows[i] > maxInRow) {
				maxInRow = rows[i];
				bestRowPosition = i;
			}
			if (columns[i] > maxInColumn) {
				maxInColumn = columns[i];
				bestColumnPosition = i;
			}
		}

		if (resultPosition == 0) {
			return bestRowPosition;
		}
		return bestColumnPosition;
	}

	/**
	 * The algorithm decides a move based on the min-max estrategy. Old squares are
	 * less important. They vanish.
	 */
	public int decideVanishingMove(int resultPosition) {


		float[] rows = new float[super.getS()];
		float[] columns = new float[super.getS()];

		float maxInRow = 0;
		float maxInColumn = 0;

		int bestRowPosition = 0;
		int bestColumnPosition = 0;

		for (int i = 0; i < super.getS(); i++) {
			for (int j = 0; j < super.getS(); j++) {

				float rowValue = (Float.parseFloat(super.getGameVanishingMatrix()[i][j].split("-")[0]));
				float columnValue = (Float.parseFloat(super.getGameVanishingMatrix()[j][i].split("-")[1]));

				if ((rowValue < rows[i]) || (rows[i] == 0)) {
					rows[i] = rowValue;
				}

				if ((columnValue < columns[i]) || (columns[i] == 0)) {
					columns[i] = columnValue;
				}

			}
		}

		for (int i = 0; i < super.getS(); i++) {
			if (rows[i] > maxInRow) {
				maxInRow = rows[i];
				bestRowPosition = i;
			}
			if (columns[i] > maxInColumn) {
				maxInColumn = columns[i];
				bestColumnPosition = i;
			}
		}

		if (resultPosition == 0) {
			return bestRowPosition;
		}
		return bestColumnPosition;
	}

}
