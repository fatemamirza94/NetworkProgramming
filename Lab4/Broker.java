import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import jade.wrapper.*;


public class Broker extends Agent {

	private AID[] SmithAgent;

	public final static String basename = "Smith";

	private static String NumberOfAgents = "";


	protected void setup() {

		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		try {

			ServiceDescription sd = new ServiceDescription();
			sd.setType("SubCoordinator");
			sd.setName(getName());

            DFAgentDescription list[] = DFService.search( this, dfd );
            if ( list.length>0 ) 
                DFService.deregister(this);
                
            dfd.addServices(sd);
            DFService.register(this,dfd);

            System.out.println("Agent " + getLocalName() + " registered service successfully\"" );
        }

		catch (FIPAException e) {
			System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
			doDelete();
		}

		ReceiveMessage rm = new ReceiveMessage();
		addBehaviour(rm);

	}


	protected void takeDown() {
	
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		// System.out.println(getAID().getName()+" terminating.");
	}



	public class ReceiveMessage extends CyclicBehaviour {

		// Variable to Hold the content of the received Message
		private String Message_Performative;
		private String Message_Content;
		private String SenderName;

		public void action() {

			// Receive a Message
			ACLMessage msg = receive();

			if (msg != null) {

				Message_Content = msg.getContent();

				SenderName = msg.getSender().getLocalName();

				System.out.println("***The subcoordinator Received a Message***" + SenderName +": " + Message_Content);

				String str = msg.getContent();

				String[] tokens = str.split(";");

				String action = tokens[0];
				NumberOfAgents = tokens[1];
				String TickInterval = tokens[4];
				String TargetHost = tokens[2];
				String Port = tokens[3];

				if (action.contains("Create")){

					String[] args = { TickInterval, TargetHost, Port };

					// create agent t1 on the same container of the creator agent
					AgentContainer container = (AgentContainer) getContainerController();

					for (int i = 0; i < Integer.parseInt(NumberOfAgents); i++) {

						try {

							String S = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(new Timestamp(System.currentTimeMillis()));

							AgentController t1 = container.createNewAgent(basename + S + "-" + i, "AgentSmith", args);
							t1.start();

							System.out.println(getLocalName()
									+ " CREATED AND STARTED NEW SMITH:"
									+ basename + "-" + i + " ON CONTAINER "
									+ container.getContainerName());
						} catch (Exception any) {
							any.printStackTrace();
						}
					}
				}

				if (action.contains("Attack")||action.contains("StopAttack")){

					addBehaviour(new OneShotBehaviour() {					
						
						public void action() {

							DFAgentDescription template = new DFAgentDescription();
							ServiceDescription sd = new ServiceDescription();
							sd.setType("Smith");
							template.addServices(sd);

							try {

								DFAgentDescription[] result = DFService.search(myAgent, template); 
								System.out.println("Found the following agents:");
								SmithAgent = new AID[result.length];

								for (int i = 0; i < result.length; ++i) {

									SmithAgent[i] = result[i].getName();
									System.out.println(SmithAgent[i].getName());

									ACLMessage msg = new ACLMessage(16);
									msg.addReceiver(new AID(SmithAgent[i].getLocalName(), AID.ISLOCALNAME));
									msg.setLanguage("English");

									String msgToSend=action; 
									msg.setContent(msgToSend);
									
									send(msg);
								}								
							}
							catch (FIPAException fe) {
								fe.printStackTrace();
							}							
						}
					});
					
				}
				

			}

		}
	}
}


