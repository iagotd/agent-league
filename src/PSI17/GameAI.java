package PSI17;

public class GameAI {
	
	final float MID_VALUE = (float) 4.5;
	
	//Our Variables
	private int N, S, R, I, P;
	private String[][] gameMatrix;
	private String[][] gameVanishingMatrix;
	private int[][] gameMatrixChanges;
	
	//Constructor
	public GameAI(int n, int s, int r, int i, int p) {
		setN(n);
		setS(s);
		setR(r);
		setI(i);
		setP(p);
		setUpMatrices();
	}
	
	//Empty Constructor
	public GameAI() {
	}
	
	//Getters $ Setters
	public int getN() {return N;}
	public void setN(int n) {N = n;}
	public int getS() {return S;}
	public void setS(int s) {S = s;}
	public int getR() {return R;}
	public void setR(int r) {R = r;}
	public int getI() {return I;}
	public void setI(int i) {I = i;}
	public int getP() {return P;}
	public void setP(int p) {P = p;}
	public String[][] getGameMatrix() {return gameMatrix;}
	public void setGameMatrix(String[][] gameMatrix) {this.gameMatrix = gameMatrix;}
	public String[][] getGameVanishingMatrix() {return gameVanishingMatrix;}
	public void setGameVanishingMatrix(String[][] gameVanishingMatrix) {this.gameVanishingMatrix = gameVanishingMatrix;}
	public int[][] getGameMatrixChanges() {return gameMatrixChanges;}
	public void setGameMatrixChanges(int[][] gameMatrixChanges) {this.gameMatrixChanges = gameMatrixChanges;}
	
	//public void metodoqueActualizaPosicion que se pone a 0 en changed
	
	
	public void setUpMatrices() {
		setGameMatrix(setUpMatrix("*"));
		setGameVanishingMatrix((setUpMatrix("4.5-4.5")));
		setGameMatrixChanges(new int[getS()][getS()]);
	}
	
	public String[][] setUpMatrix(String matrixText) {
		
		String[][] auxMatrix = new String[getS()][getS()];
		
		for(int i = 0; i < getS(); i++) {
			for(int j = 0; j < getS(); j++) {
				auxMatrix[i][j] = matrixText;
			}
		}
		return auxMatrix;
	}
	
	/** This function updates the number of changes in the gameMatrixChanges.
	 *  This function also changes the vanishingMatrix */
	public void updateMatricesAfterChange(float percentajeChanged) {
		for(int i = 0; i < getS(); i++) {
			for(int j = 0; j < getS(); j++) {
				gameMatrixChanges[i][j]++;
			}
		}
		vanishVanishingMatrix(percentajeChanged);
	}

	private void vanishVanishingMatrix(float percentajeChanged) {
		
		float SameValues = 0;
		
		for(int i = 0; i < getS(); i++) {
			for(int j = 0; j < getS(); j++) {
				
				if(gameMatrix[i][j].equals("*")) {
					gameVanishingMatrix[i][j] = MID_VALUE + "-" + MID_VALUE;
				}else {
					SameValues = (float) Math.pow(((100 - percentajeChanged)/100), gameMatrixChanges[i][j]);
					float p1Values  = Float.parseFloat(gameMatrix[i][j].split("-")[0]);
					float p2Values  = Float.parseFloat(gameMatrix[i][j].split("-")[1]);
					float newP1Value = p1Values*SameValues + (MID_VALUE)*(1-SameValues);
					float newP2Value = p2Values*SameValues + (MID_VALUE)*(1-SameValues);
					gameVanishingMatrix[i][j] = String.valueOf(newP1Value) + "-" + String.valueOf(newP2Value);
				}
			}
		}
	}
	
	public void discoverPosition(int myMove, int rivalMove, int myPrize, int rivalPrize) {
		gameMatrix[myMove][rivalMove] = String.valueOf(myPrize) + "-" + String.valueOf(rivalPrize);
		gameVanishingMatrix[myMove][rivalMove] = String.valueOf(myPrize) + "-" + String.valueOf(rivalPrize);
		gameMatrixChanges[myMove][rivalMove] = 0;
	}

}
