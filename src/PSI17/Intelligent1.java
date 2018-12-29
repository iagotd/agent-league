package PSI17;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Intelligent1 extends Agent {
	private static final long serialVersionUID = 1L;
	
	public final String VANISHING = "VANISHING";
	public final String NON_VANISHING = "NON_VANISHING";
	
	MaxPointDecisor decisor;
	String Id;
	int resultPosition, rivalPosition;

	protected void setup() {
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Player");
		sd.setName("Game");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new JugarLiga());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		System.out.println("Player " + getAID().getName() + " Finished.");
	}

	private class JugarLiga extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		public void action() {
			ACLMessage msg = myAgent.receive();
			String leagueStage;

			if (msg != null) {

				switch (msg.getContent().split("#").length) {
				case 0:
					leagueStage = msg.getContent();
					break;
				default:
					leagueStage = msg.getContent().split("#")[0];
					break;

				}

				switch (leagueStage) {
				case "Id": //Id#2#5,4,1000,100,25
					Id = msg.getContent().split("#")[1];
					int N = Integer.parseInt(msg.getContent().charAt(5) + "");
					int S = Integer.parseInt(msg.getContent().split(",")[1]);
					int R = Integer.parseInt(msg.getContent().split(",")[2]);
					int I = Integer.parseInt(msg.getContent().split(",")[3]);
					int P = Integer.parseInt(msg.getContent().split(",")[4]);
					decisor = new MaxPointDecisor(N, S, R, I, P);//TODO
					break;
				case "NewGame": //NewGame#2,4
					if(!msg.getContent().split(",")[1].equals(Id)) {
						resultPosition = 0;
						rivalPosition = 1;
					}else {
						resultPosition = 1;
						rivalPosition = 0;
					}
					break;
				case "Position": //Position
					ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
					reply.setContent("Position#" + decisor.decide(VANISHING, resultPosition)); // TODO
					reply.addReceiver(msg.getSender());
					send(reply);
					break;
				case "Results": //Results#1,0#4,2
					int myMove    = Integer.parseInt(msg.getContent().split("#")[1].split(",")[resultPosition]);
					int rivalMove = Integer.parseInt(msg.getContent().split("#")[1].split(",")[rivalPosition]);
					int myPrize    = Integer.parseInt(msg.getContent().split("#")[2].split(",")[resultPosition]);
					int rivalPrize = Integer.parseInt(msg.getContent().split("#")[2].split(",")[rivalPosition]);
					decisor.discoverPosition(myMove, rivalMove, myPrize, rivalPrize);
					break;
				case "EndGame": //EndGame
					System.out.println("Is asking EndGame");
					decisor.setUpMatrices();
					break;
				case "Changed": //Changed#25
					System.out.println("Something has changed");
					decisor.updateMatricesAfterChange(Float.parseFloat(msg.getContent().split("#")[1]));
					break;
				}

			} else {
				block();
			}
		}

		private void imprimirVanishingMatrix() {
			
			for (int i = 0; i < decisor.getS(); i++) {
				for (int j = 0; j < decisor.getS(); j++) {
					System.out.print("[" + decisor.getGameVanishingMatrix()[i][j] + "]");
				}
				System.out.println();
			}
			
		}
	}
}
