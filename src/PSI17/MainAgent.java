package PSI17;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

public class MainAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private MatrixGUI matrixGUI;
	List<AMSAgentDescription> players = new ArrayList<AMSAgentDescription>();
	private final int N = 3, S = 4, R = 1000, I = 100, P = 25;

	public String[][] gameMatrix = new String[S][S];
	private List<String> matchUp = new ArrayList<String>();

	private int numberOfMatches = 0, matchNumber = 0, movesLeft;
	private int[] playerScores = null;

	int move0, move1;
	int score0, score1;
	int iteration;

	static List<String> playerNames;

	protected void setup() {
		matrixGUI = new MatrixGUI(this);
		System.setOut(new PrintStream(matrixGUI.getLoggingOutputStream()));
		matrixGUI.logLine("Agent " + getAID().getName() + " is ready.");
		players = takePlayers();
	}

	public ArrayList<AMSAgentDescription> takePlayers() {
		matrixGUI.logLine("Looking for new Players.");
		AMSAgentDescription[] agents = takeAgents();
		return kickNonPlayers(agents);
	}

	public void StartLeague() {
		matrixGUI.logLine("Starting new League.");
		addBehaviour(new LeagueAdministrator());
	}

	private AMSAgentDescription[] takeAgents() {
		AMSAgentDescription[] agents = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(-1L);
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println(e);
		}
		return agents;
	}

	private ArrayList<AMSAgentDescription> kickNonPlayers(AMSAgentDescription[] agents) {
		ArrayList<AMSAgentDescription> players = new ArrayList<AMSAgentDescription>();

		for (int i = 0; i < agents.length; i++) {
			switch (agents[i].getName().getName().split("@")[0]) {
			case "ams":
				break;
			case "df":
				break;
			case "rma":
				break;
			default:
				if (!agents[i].getName().getName().split("@")[0].equals(getAID().getName().split("@")[0])) {
					players.add(agents[i]);
				}
			}
		}
		
		playerNames = players.stream().map(e -> e.getName().getName().split("@")[0]).collect(Collectors.toList());
		matrixGUI.setPlayersUI(playerNames);
		matrixGUI.logLine(players.size() + " Players Found");

		return players;
	}

	private class LeagueAdministrator extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;

		public void action() {

			setupLeagueValues();
			sendLeagueInfo();
			createMatchUp();

			while ((numberOfMatches - matchNumber) > 0) {
				informAgents("NewGame#" + matchUp.get(matchNumber));
				createMatrix();
				movesLeft = R;
				iteration = I;

				while (movesLeft > 0) {
					nextTurn();
				}

				informAgents("EndGame");
				matchNumber++;
			}

			showScores();
		}

		private void setupLeagueValues() {
			numberOfMatches = (N * (N - 1)) / 2;
			playerScores = new int[N];
		}

		private void sendLeagueInfo() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			String matchInfo = "#" + N + "," + S + "," + R + "," + I + "," + P;

			for (int i = 0; i < players.size(); i++) {
				msg.setContent("Id#" + i + matchInfo);
				msg.addReceiver(players.get(i).getName());
				send(msg);
				msg.clearAllReceiver();
			}
		}

		private void createMatchUp() {
			for (int i = 0; i < N; i++) {
				for (int j = i + 1; j < N; j++) {
					matchUp.add(i + "," + j);
				}
			}
		}

		private void informAgents(String content) {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			String player0 = matchUp.get(matchNumber).split(",")[0];
			String player1 = matchUp.get(matchNumber).split(",")[1];

			msg.setContent(content);

			msg.addReceiver(players.get(Integer.parseInt(player0)).getName());
			msg.addReceiver(players.get(Integer.parseInt(player1)).getName());
			send(msg);
		}

		private void createMatrix() {

			for (int row = 0; row < gameMatrix.length; row++) {
				for (int column = 0; column < gameMatrix[row].length; column++) {
					if (row < column) {
						int auxRandom1 = (int) (Math.random() * 10);
						int auxRandom2 = (int) (Math.random() * 10);
						gameMatrix[row][column] = auxRandom1 + "-" + auxRandom2;
					} else if (row > column) {
						String mirrored = gameMatrix[column][row].split("-")[1] + "-"
								+ gameMatrix[column][row].split("-")[0];
						gameMatrix[row][column] = mirrored;
					} else if (row == column) {
						int auxRandom = (int) (Math.random() * 10);
						gameMatrix[row][column] = auxRandom + "-" + auxRandom;
					}
				}
			}

			matrixGUI.updateMatrix(gameMatrix);
		}

		private void nextTurn() {
			
			movesLeft--;
			iteration--;
			
			nextMove();
			calculateResults();
			informAgents("Results#" + move0 + "," + move1 + "#" + score0 + "," + score1);

			if (iteration == 0 && I != 0) {
				updateMatrix();
				iteration = I;
			}
		}

		private void nextMove() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			String player0 = matchUp.get(matchNumber).split(",")[0];
			String player1 = matchUp.get(matchNumber).split(",")[1];

			msg.setContent("Position");
			msg.addReceiver(players.get(Integer.parseInt(player0)).getName());
			send(msg);

			ACLMessage msg0 = null;
			while (true) {
				msg0 = myAgent.receive();
				if (msg0 != null) {
					break;
				}
			}

			msg.clearAllReceiver();
			msg.addReceiver(players.get(Integer.parseInt(player1)).getName());
			send(msg);

			ACLMessage msg1 = null;
			while (true) {
				msg1 = myAgent.receive();
				if (msg1 != null) {
					break;
				}
			}

			move0 = Integer.parseInt(msg0.getContent().split("#")[1]);
			move1 = Integer.parseInt(msg1.getContent().split("#")[1]);
		}

		private void calculateResults() {
			String player0 = matchUp.get(matchNumber).split(",")[0];
			String player1 = matchUp.get(matchNumber).split(",")[1];

			String chosenVector = gameMatrix[move0][move1];
			score0 = Integer.parseInt(chosenVector.split("-")[0]);
			score1 = Integer.parseInt(chosenVector.split("-")[1]);

			playerScores[Integer.parseInt(player0)] += score0;
			playerScores[Integer.parseInt(player1)] += score1;
		}

		private void updateMatrix() {

			double suma = 0;
			boolean[][] changedMatrix = new boolean[S][S];

			while (P > suma) {

				int randomRow = (int) (Math.random() * S);
				int randomColumn = (int) (Math.random() * S);

				String randRNumber = String.valueOf((int) (Math.random() * 10));
				String randCNumber = String.valueOf((int) (Math.random() * 10));

				if (!changedMatrix[randomRow][randomColumn]) {
					if (randomRow == randomColumn) {
						suma += (double) (100 / ((1.0) * S * S));
						gameMatrix[randomRow][randomColumn] = randRNumber + "-" + randCNumber;
						changedMatrix[randomRow][randomColumn] = true;
					} else {
						suma += 2 * (double) (100 / ((1.0) * S * S));
						gameMatrix[randomRow][randomColumn] = randRNumber + "-" + randCNumber;
						gameMatrix[randomColumn][randomRow] = randCNumber + "-" + randRNumber;
						changedMatrix[randomRow][randomColumn] = true;
						changedMatrix[randomColumn][randomRow] = true;
					}
				}

			}
			
			informAgents("Changed#" + suma);
			matrixGUI.updateMatrix(gameMatrix);
		}

		private void showScores() {
			for (int i = 0; i < N; i++) {
				System.out.println("ADMIN --> " + playerNames.get(i) + "got " + playerScores[i] + " points.");
			}
		}

	}

	public int getN() { return N;}
	public int getS() {return S;}
	public int getR() {return R;}
	public int getI() {return I;}
	public int getP() {return P;}

}