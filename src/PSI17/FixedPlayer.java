package PSI17;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class FixedPlayer extends Agent {
	private static final long serialVersionUID = 1L;
	
	private int fixedDecision = 0;

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

			if (msg != null) {

				if (msg.getContent().split("#")[0].equals("Id")) {
					int S = Integer.parseInt(msg.getContent().split(",")[1]);
					fixedDecision = (int) (Math.random() * S);// TODO
				}

				if (msg.getContent().equals("Position")) {
					ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
					reply.setContent("Position#" + fixedDecision);
					reply.addReceiver(msg.getSender());
					send(reply);
				}

			} else {
				block();
			}
		}
		
	}

}
