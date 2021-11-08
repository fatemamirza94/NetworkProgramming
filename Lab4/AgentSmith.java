import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.io.*;
import java.net.*;


public class AgentSmith extends Agent {

	Socket server = null;
	PrintWriter out = null;
    BufferedReader in = null;

	String TargetHost = "";
	int Port = 0;
	long TickInterval = 0;
	boolean doAttack = false;

	String fibRange = "40";

	SendRequest sr;


	protected void setup() {
		
		Object[] args = getArguments();
		TickInterval = Integer.parseInt((String) args[0]);
		TargetHost = (String) args[1];
		Port = Integer.parseInt((String) args[2]);


		/** Registration with the DF */
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());


		try {

			ServiceDescription sd = new ServiceDescription();
			sd.setType("Smith");
			sd.setName(getName());
			sd.addOntologies("SmithAgent");

            DFAgentDescription list[] = DFService.search( this, dfd );
            if ( list.length>0 ) 
                DFService.deregister(this);
                
            dfd.addServices(sd);
            DFService.register(this, dfd);

        }
		catch (FIPAException fe) {
			fe.printStackTrace();
			System.err.println(getLocalName() + " registration with DF unsucceeded. Reason: " + fe.getMessage());
			doDelete();
		}


		addBehaviour(new ReceiveMessage());

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



	/* The implementation of ticker behavior of agent class. It will make a periodic tcp connection to the specified server	*/

	public class SendRequest extends TickerBehaviour {
		
		long currentTick = 0;


		// Construct a TickerBehaviour that call its onTick() method every period ms.

		public SendRequest(Agent a, long period) {
			super(a, period);
			currentTick = period;
		}
		

		// This method is invoked periodically with the period defined in the constructor

		protected void onTick() {

			try {

				if (currentTick != TickInterval) {
					reset(TickInterval); // This method is called to reset the behaviour and starts again. TickInterval - the new tick time
				}

				if (!doAttack) {
					done(); // Check if this behaviour is done
				}

				System.out.println("I am opening socket to "+TargetHost+":"+Port);
				
				//a tcp connection socket opened here
				server = new Socket(TargetHost, Port);

				out = new PrintWriter(server.getOutputStream(), true);
            	in = new BufferedReader(new InputStreamReader(server.getInputStream()));

            	// sends the fiboRange to server and receives the output
            	out.println(fibRange);
				String serverResponse = in.readLine();

				System.out.println("Connection Established with ip: "+TargetHost+" at port: "+Port+" at Ticker value: "+TickInterval);
				System.out.println("Fibo series for n("+ fibRange +") ="+serverResponse);
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			finally {
	            try {
	                if (out != null) {
	                	out.close();
	                }
	                if (in != null) {
	                	in.close();
	                	server.close();
	                }
	            }
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

			//System.out.println("connection established");
		} 
	}

	
	public class ReceiveMessage extends CyclicBehaviour {

		private String Message_Content;
		private String SenderName;


		// Runs the behaviour
		public void action() {

			ACLMessage msg = receive();

			if (msg != null) {

				Message_Content = msg.getContent();
				SenderName = msg.getSender().getLocalName();

				System.out.println(this.myAgent + " received a Message from " + SenderName + " : "+ Message_Content);
				String str = msg.getContent();
				System.out.println(str);

				if (str.contains("Attack")){

					// a new tcp connection is to be made in every TickInterval millisecs
					SendRequest sr = new SendRequest(this.myAgent, TickInterval);
					addBehaviour(sr);
				}

				if (str.contains("StopAttack")){

					System.out.println("attack terminated");
					//sr.stop(); // Make the TickerBehaviour terminate.
					doDelete();

				}

			} 

			else {
				block();
			}

		}

	}

}
