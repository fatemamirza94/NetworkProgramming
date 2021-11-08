import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;

import jade.gui.GuiEvent;



public class ArchitectFrame extends javax.swing.JFrame {

	private Architect myAgent2;

    private JButton AttackBtn;
    private JButton CreateBtn;
    private JButton WithdrawBtn;
    private JButton RefreshBtn;
    private JComboBox<Object> ComboSubCoord;
    private JLabel SubCoordLbl;
    private JLabel AgentsNumberLbl;
    private JLabel AddressLbl;
    private JLabel TickerLbl;
    private JTextField TFticker;
    private JTextField TFagentsNumber;
    private JTextField TFaddress;
    private JLabel AttackAgentsLbl1;
    private JLabel AttackAgentsLbl2;
    private JPanel controlPanel;




    public ArchitectFrame(Architect c) {

    	super(c.getLocalName());
    	myAgent2 = c;

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent2.doDelete();
            }
        });
        


        SubCoordLbl = new JLabel();
        SubCoordLbl.setPreferredSize(new Dimension(400, 25));

        ComboSubCoord = new JComboBox<>(myAgent2.agentList.toArray());
        ComboSubCoord.setSelectedIndex(-1);
        ComboSubCoord.setPreferredSize(new Dimension(400, 35));

        RefreshBtn = new JButton();
        RefreshBtn.setPreferredSize(new Dimension(300, 40));

        AgentsNumberLbl = new JLabel();
        AgentsNumberLbl.setPreferredSize(new Dimension(400, 25));

        TFagentsNumber = new JTextField();
        TFagentsNumber.setPreferredSize(new Dimension(400, 35));

        AddressLbl = new JLabel();
        AddressLbl.setPreferredSize(new Dimension(400, 25));

        TFaddress = new JTextField();
        TFaddress.setPreferredSize(new Dimension(400, 35));
        

        TickerLbl = new JLabel();
        TickerLbl.setPreferredSize(new Dimension(400, 25));

        TFticker = new JTextField();
        TFticker.setPreferredSize(new Dimension(400, 35));


        CreateBtn = new JButton();
        CreateBtn.setPreferredSize(new Dimension(300, 40));


        AttackAgentsLbl1 = new JLabel();
        AttackAgentsLbl1.setPreferredSize(new Dimension(400, 30));


        AttackBtn = new JButton();
        AttackBtn.setPreferredSize(new Dimension(230, 40));

        WithdrawBtn = new JButton();
        WithdrawBtn.setPreferredSize(new Dimension(230, 40));


        AttackAgentsLbl2 = new JLabel();
        AttackAgentsLbl2.setPreferredSize(new Dimension(400, 30));


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);


        SubCoordLbl.setText("Sub co-ordinators");
        AddressLbl.setText("Enter the hostname and port to be attacked");
        TickerLbl.setText("Enter duration of ticker behaviour");
        AgentsNumberLbl.setText("Enter the number of agents to be created");
        

        CreateBtn.setText("Create");
        CreateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	String ticker = TFticker.getText(); //ticker
            	String agentsNumber = TFagentsNumber.getText(); //no agents
            	String address = TFaddress.getText(); //host port
            	String newAddress[]=address.split(":");
        		String host=newAddress[0];
        		String port = newAddress[1];
        		String action="Create";
        		myAgent2.SendMessage(action,agentsNumber, host, port, ticker);
            }
        });
 
        RefreshBtn.setText("Refresh");
        RefreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //call function from listing all sub coord
            	myAgent2.updateCombo();
            }
        });       

        AttackBtn.setText("Start Attack");
        AttackBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { 
                AttackAgentsLbl2.setText("Attack Started");
            	String ticker = TFticker.getText(); //ticker
            	String agentsNumber = TFagentsNumber.getText(); //no agents
            	String address = TFaddress.getText(); //host port
            	String newAddress[]=address.split(":");
        		String host2=newAddress[0];
        		String port = newAddress[1];
        		String action="Attack";
            	myAgent2.SendMessage(action, agentsNumber, host2, port, ticker);
            }
        });

        WithdrawBtn.setText("Stop Attack");
        WithdrawBtn.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	AttackAgentsLbl1.setText("Active agents: 0");
                AttackAgentsLbl2.setText("Attack Stopped");
            	String ticker = TFticker.getText(); //ticker
            	String agentsNumber = TFagentsNumber.getText(); //no agents
            	String address = TFaddress.getText(); //host port
            	String newAddress[]=address.split(":");
        		String host2=newAddress[0];
        		String port = newAddress[1];
        		String action="StopAttack";
            	myAgent2.SendMessage(action, agentsNumber, host2, port, ticker);
            }
        });




        controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));

        controlPanel.add(SubCoordLbl);
        controlPanel.add(ComboSubCoord );
        controlPanel.add(RefreshBtn);
        controlPanel.add(AgentsNumberLbl);
        controlPanel.add(TFagentsNumber);
        controlPanel.add(AddressLbl);
        controlPanel.add(TFaddress);
        controlPanel.add(TickerLbl);
        controlPanel.add(TFticker);
        controlPanel.add(CreateBtn);
        controlPanel.add(AttackAgentsLbl1);
        controlPanel.add(AttackBtn);
        controlPanel.add(WithdrawBtn);
        controlPanel.add(AttackAgentsLbl2);

        Container contentPane = getContentPane();
        contentPane.setPreferredSize(new Dimension(550, 670));
        getContentPane().add(controlPanel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                myAgent2.doDelete();
            }
        });
    }


    public void displayGUI() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        setTitle("The Architect");
        setResizable(false);
        super.setVisible(true);
    }

    
    
	public void addItemDLL(String name) {
		String tmp[]=name.split("@");
		Object a = tmp[0];
		ComboSubCoord.addItem(a);
        ComboSubCoord.setSelectedIndex(-1);
	}

	public String getSubCoordinator() {
		String name=ComboSubCoord.getSelectedItem().toString();
		//String name = tmp.split("@")[0];
		return name;
	}

	public void clearDDL() {
		ComboSubCoord.removeAllItems();
	}
	
	public void readyAgents(int number){
		String tmp = Integer.toString(number);
		AttackAgentsLbl1.setText("Active agents: " + tmp);
	}
	
}
