import jade.core.*;
import jade.core.AgentContainer;
import jade.core.behaviours.*;

import jade.wrapper.*;

import jade.lang.acl.*;

import jade.util.leap.Iterator;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;


public class Architect extends Agent {

	private AgentController ac = null;
	private AID[] subCoord;
	ArchitectFrame myGui;
	String host;
	int AgentCounter=0;

	public ArrayList<String> agentList;

	protected void setup() {

		agentList = new ArrayList<String>();
		getAllActiveAgents();


		myGui = new ArchitectFrame(this);
		myGui.displayGUI();


		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		try {

			ServiceDescription sd = new ServiceDescription();
			sd.setType("MainAgent");
			sd.setName("JADE-attack");

            DFAgentDescription list[] = DFService.search( this, dfd );
            if ( list.length>0 ) 
                DFService.deregister(this);
                
            dfd.addServices(sd);
            DFService.register(this,dfd);

            System.out.println("Agent " + getLocalName() + " registered service successfully\"" );
        }
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

	    ReceiveMsg();
		
	}



	protected void takeDown() {

		System.out.println("REMOVING AGENT");

		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Close the GUI
		myGui.dispose();

		// Printout a dismissal message
		System.out.println(getAID().getName()+" terminating.");
	}


	
	public void SendMessage(String action,String agentsNumber, String host2, String port, String ticker){

		addBehaviour(new OneShotBehaviour() {

        public void action() {

        	//get the names from ddl
        	String subCoordinatorName=myGui.getSubCoordinator();

        	if (action.contains("Create")){

        		System.out.println(subCoordinatorName+" is selected, and ready for creating sub-agents");

        		ACLMessage msg = new ACLMessage(16);
                msg.addReceiver(new AID(subCoordinatorName, AID.ISLOCALNAME));
                msg.setLanguage("English");

                String msgToSend=action+";"+agentsNumber+";"+host2+";"+port+";"+ticker; //getting the message from the gui the we want to send
                msg.setContent(msgToSend);
                send(msg);

                int tmp=Integer.parseInt(agentsNumber);
                AgentCounter=AgentCounter+tmp;
                myGui.readyAgents(AgentCounter);
                System.out.println("****I Sent Message to::>"+subCoordinatorName+"*****"+"\n"+
                                    "The Content of My Message is::>"+ msg.getContent());
        	}

        	if (action.contains("Attack")||action.contains("StopAttack"))
        	{
        		ACLMessage msg = new ACLMessage(16);
                msg.addReceiver(new AID(subCoordinatorName, AID.ISLOCALNAME));
                msg.setLanguage("English");
                String msgToSend=action+";"+agentsNumber+";"+host2+";"+port+";"+ticker; //getting the message from the gui the we want to send
                msg.setContent(msgToSend);
                send(msg);
                System.out.println("****I Sent Message to::>"+subCoordinatorName+"*****"+"\n"+
                                    "The Content of My Message is::>"+ msg.getContent());


                if (action.contains("StopAttack"))
                	AgentCounter = 0;
        	 	}       	    	
        	}
		});
    }

	
	//function called from the setup(). It is making the main agent able to listen for messaged addressed to him
	public void ReceiveMsg(){

		addBehaviour(new CyclicBehaviour() {

			    private String Message_Performative;
		        private String Message_Content;
		        private String SenderName;

		        public void action() {

		            ACLMessage msg = receive();

		            if(msg != null) {
		                Message_Performative = msg.getPerformative(msg.getPerformative());
		                Message_Content = msg.getContent();
		                SenderName = msg.getSender().getLocalName();
		                System.out.println(" ****The Architect received msg from ***"+ SenderName+ ":" + Message_Content);		               
		            }
		        }
		});
    }



    public void getAllActiveAgents() {


		DFAgentDescription dfd = new DFAgentDescription();
   		ServiceDescription sd = new ServiceDescription();
   		sd.setType( "SubCoordinator" );
		dfd.addServices(sd);

		SearchConstraints ALL = new SearchConstraints();
		ALL.setMaxResults(new Long(-1));  // In searchConstraints, specify the max number of replies (-1 means ALL). 

		try
		{

			DFAgentDescription[] result = DFService.search(this, dfd, ALL); 
		
			AID[] agents = new AID[result.length];

			for (int i=0; i<result.length; i++) {

				agents[i] = result[i].getName() ;
				agentList.add(agents[i].getLocalName());
			}

		} 
		catch (FIPAException fe) { 
			fe.printStackTrace(); 
        }

	}



	public void updateCombo() {
		addBehaviour(new OneShotBehaviour() {	 
			
			public void action() {

				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("SubCoordinator");
				template.addServices(sd);

				SearchConstraints ALL = new SearchConstraints();
				ALL.setMaxResults(new Long(-1));  // In searchConstraints, specify the max number of replies (-1 means ALL).


				try {

					DFAgentDescription[] result = DFService.search(myAgent, template, ALL); 

					AMSAgentDescription[] agents = AMSService.search(myAgent, new AMSAgentDescription(), ALL);

					AID[] agentList = new AID[agents.length];

					for (int i = 0; i < agents.length; i++) {

						agentList[i] = agents[i].getName();
						
					}	

					System.out.println("Found the following agents:");

					subCoord = new AID[result.length];

					myGui.clearDDL();

					for (int i = 0; i < result.length; i++) {

						subCoord[i] = result[i].getName();	

						for (AID s : agentList) {

							if (s.equals(subCoord[i])) {

								System.out.println(subCoord[i].getName());
								myGui.addItemDLL(subCoord[i].getName()); //adding the subCoordinators agents to the ComboBox
							}

						}
						
					}					
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
	}
}
