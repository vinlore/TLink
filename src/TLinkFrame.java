import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;


@SuppressWarnings("serial")
public class TLinkFrame extends JFrame {

	private JPanel mainPanel;
	private JPanel routePanel;
	private JPanel stopPanel;
	private JPanel customerPanel;
	private JPanel driverPanel;
	private JPanel operatorPanel;
	
	private JPanel routeMenu;
	private JPanel menuPanel;
	private JTabbedPane tabPane;

	private JTable searchTable;
	private JTable customerTable;
	private JTable routeTable;
	private JTable stopTable;
	private JScrollPane customerScrollPane;

	private JButton updateBtn;
	private JButton clearBtn;

	private JLabel title;
	private JTextField routeField;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			/*
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}*/
		} catch (Exception e) {

		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TLinkFrame frame = new TLinkFrame("TLink Database App");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TLinkFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 400);
		init();
	}

	public void init() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		title = new JLabel("TLink Database App");	

		/*updateBtn = new JButton("Update");
		clearBtn = new JButton("Clear");
		routeField = new JTextField("Route");*/

		routePanel = createRoutePanel();
		stopPanel = createStopPanel();
		customerPanel = createCustomerPanel();
		driverPanel = createDriverPanel();
		operatorPanel = createOperatorPanel();

		Vehicle vehicle = new Vehicle();  
		// Uncomment below to display from database
		//customerTable = new JTable(vehicle.displayVehicles());	
		customerTable = new JTable();
		customerScrollPane = new JScrollPane(customerTable);
		customerPanel.add(customerScrollPane, BorderLayout.CENTER);

		// Tabs pane
		tabPane = new JTabbedPane();
		tabPane.addTab("Routes", routePanel);
		tabPane.addTab("Stops", stopPanel);
		tabPane.addTab("Customer", customerPanel);
		tabPane.addTab("Driver", driverPanel);
		tabPane.addTab("Operator", operatorPanel);
		tabPane.setFocusable(false);

		mainPanel.add(title, BorderLayout.PAGE_START);
		mainPanel.add(tabPane, BorderLayout.CENTER);

		setContentPane(mainPanel);
	}

	private JPanel createRoutePanel() {		
		routeTable = new JTable();
		JScrollPane routeScrollPanel = new JScrollPane(routeTable);
		Route route = new Route();
		routeTable.setModel(route.displayRoutes());

		JButton routeSearchBtn = new JButton("Search");
		routeSearchBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String routeName = JOptionPane.showInputDialog(null, "Enter route name");
				Route route = new Route();
				ResultTableModel search = route.searchRoutes(routeName);
				if (search.empty) {
					JOptionPane.showMessageDialog(null, "No routes found");
				}
				else {
					routeTable.removeAll();
					routeTable.setModel(search);
				}
			}
		});

		JButton stopsBtn = new JButton("Stops");
		stopsBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				int rid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter route ID"));
				Route route = new Route();
				ResultTableModel search = route.getAllStops(rid);
				if (search.empty) {
					JOptionPane.showMessageDialog(null, "No stops found");
				}
				else {
					routeTable.removeAll();
					routeTable.setModel(search);
				}
			}
		});
		
		JButton resetBtn = new JButton("Reset");
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Route route = new Route();
				routeTable.removeAll();
				routeTable.setModel(route.displayRoutes());
			}
		});

		routeMenu = new JPanel();
		routeMenu.setLayout(new GridLayout(1, 2));
		routeMenu.add(routeSearchBtn);
		routeMenu.add(stopsBtn);
		
		routePanel = new JPanel();
		routePanel.setLayout(new BorderLayout());
		routePanel.add(routeScrollPanel);
		routePanel.add(resetBtn, BorderLayout.SOUTH);
		routePanel.add(routeMenu, BorderLayout.NORTH);
		return routePanel;
	}

	private JPanel createStopPanel() {
		stopTable = new JTable();
		JScrollPane stopScrollPanel = new JScrollPane(stopTable);
		Stop stop = new Stop();
		stopTable.setModel(stop.displayStops());

		JButton stopSearchBtn = new JButton("Search");
		stopSearchBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String stopName = JOptionPane.showInputDialog(null, "Enter stop name");
				Stop stop = new Stop();
				ResultTableModel search = stop.searchStops(stopName);
				if (search.empty) {
					JOptionPane.showMessageDialog(null, "No stops found");
				}
				else {
					stopTable.removeAll();
					stopTable.setModel(search);
				}
			}
		});

		JButton routesBtn = new JButton("Find route(s)");
		routesBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int sid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter stop ID"));
				Stop stop = new Stop();
				ResultTableModel search = stop.findAllRoutes(sid);
				if (search.empty) {
					JOptionPane.showMessageDialog(null, "No routes found");
				}
				else {
					stopTable.removeAll();
					stopTable.setModel(search);
				}
			}
		});
		
		JButton resetBtn = new JButton("Reset");
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Stop stop = new Stop();
				stopTable.removeAll();
				stopTable.setModel(stop.displayStops());
			}
		});

		JPanel stopMenu = new JPanel();
		stopMenu.setLayout(new GridLayout(1, 2));
		stopMenu.add(stopSearchBtn);
		stopMenu.add(routesBtn);

		stopPanel = new JPanel();
		stopPanel.setLayout(new BorderLayout());
		stopPanel.add(stopScrollPanel);
		stopPanel.add(stopMenu, BorderLayout.NORTH);
		stopPanel.add(resetBtn, BorderLayout.SOUTH);
		return stopPanel;
	}

	private JPanel createCustomerPanel() {
		customerPanel = new JPanel();
		customerPanel.setLayout(new BorderLayout());
		return customerPanel;
	}

	private JPanel createDriverPanel() {
		driverPanel = new JPanel();
		return driverPanel;
	}

	private JPanel createOperatorPanel() {
		operatorPanel = new JPanel();
		return operatorPanel;
	}
}
