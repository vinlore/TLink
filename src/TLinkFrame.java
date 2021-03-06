import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Time;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import oracle.sql.DATE;


@SuppressWarnings("serial")
public class TLinkFrame extends JFrame {

	private JPanel mainPanel;
	private JPanel headerPanel;
	private JPanel routePanel;
	private JPanel stopPanel;
	private JPanel customerPanel;
	private JPanel driverPanel;
	private JPanel operatorPanel;

	private JPanel routeMenu;
	private JTabbedPane tabPane;

	private JTable customerTable;
	private JTable driverTable;
	private JPanel customerMenu;
	private JPanel driverMenu;
	private JTable routeTable;
	private JTable stopTable;

	private JScrollPane customerScrollPane;
	private JScrollPane driverScrollPane;

	private JLabel title;
	private JLabel welcome;

	private String insertError = "Insertion failed.\n" +
			"The application cannot connect to the database or" + 
			" the identifier already exists";
	private String dateError = "Invalid date format";
	private String deleteError = "Removal failed.\n" +
			"The application cannot connect to the database or" + 
			" the identifier does not exist";
	private String negativeError = "Please check entries that are negative or zero";
	private String numberError = "One or more fields require a numeric value";
	private int empId = -1;
	private String password = "bossman";

	public static void main(String[] args) {
		try {
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());


			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
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

		headerPanel = new JPanel();
		headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		headerPanel.setLayout(new BorderLayout());
		title = new JLabel("TLink Database");	
		welcome = new JLabel("Welcome, Guest");
		headerPanel.add(title, BorderLayout.WEST);
		headerPanel.add(welcome, BorderLayout.EAST);

		routePanel = createRoutePanel();
		stopPanel = createStopPanel();
		customerPanel = createCustomerPanel();
		driverPanel = createDriverPanel();
		operatorPanel = createOperatorPanel();

		// Tabs pane
		tabPane = new JTabbedPane();
		tabPane.addTab("Routes", routePanel);
		tabPane.addTab("Stops", stopPanel);
		tabPane.addTab("Customer", customerPanel);
		tabPane.addTab("Driver", driverPanel);
		tabPane.addTab("Operator", operatorPanel);
		tabPane.setFocusable(false);

		mainPanel.add(headerPanel, BorderLayout.PAGE_START);
		mainPanel.add(tabPane, BorderLayout.CENTER);

		setContentPane(mainPanel);
	}


	// ROUTE SECTION

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
				if(routeName != null) {
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
			}
		});

		JButton stopsBtn = new JButton("Stops");
		stopsBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int rid = -1;
				try {
					rid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter route ID"));
				} catch (NumberFormatException nfe) {
					//ignore (this gets thrown only if user hits cancel before entering anything)
				}
				if (rid != -1) {
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
			}
		});

		JButton resetBtn = new JButton("Show All");
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

	// STOP SECTION

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
				if (stopName != null) {
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
			}
		});

		JButton routesBtn = new JButton("Find route(s)");
		routesBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int sid = -1;
				try {
					sid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter stop ID"));
				} catch (NumberFormatException nfe) {
					//ignore (this gets thrown only if user hits cancel before entering anything)
				}
				if (sid != -1) {
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
			}
		});

		JButton resetBtn = new JButton("Show All");
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

	// CUSTOMER SECTION

	private JPanel createCustomerPanel() {
		customerTable = new JTable();
		customerScrollPane = new JScrollPane(customerTable);
		final int[] custID = {-1};
		final JButton logoutBtn = new JButton("Logout");
		final JButton loginBtn = new JButton("Login");
		final JButton updateBalanceBtn = new JButton("Update Balance");

		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int cid = -1;
				try {
					cid = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Customer ID")); 
				} catch (NumberFormatException nfe) {
					//ignore (this gets thrown only if user hits cancel before entering anything)
				};
				Customer customer = new Customer();
				ResultTableModel passResults = customer.login(cid);
				if (passResults.empty) {
					JOptionPane.showMessageDialog(null, "Login failed");
				}
				else {
					String name = passResults.getValueAt(0, 0).toString();
					welcome.setText("Welcome, " + name);
					custID[0] = cid;
					customerMenu.setLayout(new GridLayout(2, 2));
					customerMenu.add(updateBalanceBtn);
					customerMenu.add(logoutBtn);
					customerMenu.remove(loginBtn);
					if(customer.accessedAllVehicles(cid)){
						JOptionPane.showMessageDialog(customerMenu, "You have been on every vehicle.", "Congratulations!", JOptionPane.PLAIN_MESSAGE);
					}
					customerPanel.revalidate();
					customerPanel.repaint();
					customerTable.removeAll();
					customerTable.setModel(passResults);
				}
			}
		});


		logoutBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Customer customer = new Customer();
				welcome.setText("Welcome, Guest");
				customerTable.setModel(customer.login(-1));
				customerMenu.add(loginBtn);
				customerMenu.remove(logoutBtn);
				customerMenu.remove(updateBalanceBtn);
				customerMenu.setLayout(new GridLayout(1, 2));
				customerMenu.revalidate();
				customerMenu.repaint();
			}
		});


		updateBalanceBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel updatePanel = new JPanel();
				updatePanel.setLayout(new GridLayout(0, 1));
				JLabel amtLabel = new JLabel("Enter new amount to add");
				JTextField amtField = new JTextField();
				updatePanel.add(amtLabel);
				updatePanel.add(amtField);	

				String title = "Update Address";
				int option = JOptionPane.OK_CANCEL_OPTION;

				int input = JOptionPane.showConfirmDialog(null, updatePanel, title, option);
				if(input == JOptionPane.OK_OPTION) {				
					int cid = -1;
					int amtToAdd = 0;
					try {
						cid = custID[0];
						amtToAdd = Integer.parseInt(amtField.getText());
						if (amtToAdd > 0) {
							Customer customer = new Customer();
							customer.updateBalance(cid, amtToAdd);
							ResultTableModel updateBalanceResults = customer.login(cid);
							if (updateBalanceResults.empty) {
								JOptionPane.showMessageDialog(null, "CustomerID not found - please try again");
							}
							else {
								customerTable.removeAll();
								customerTable.setModel(updateBalanceResults);
								JOptionPane.showMessageDialog(null, "Update successful");
							}	
						} else {
							JOptionPane.showMessageDialog(null, negativeError);
						}
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}
				}				
			}
		});

		JButton resetBtn = new JButton("Show All");
		resetBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				customerTable.removeAll();
				customerTable.revalidate();
				customerTable.repaint();
			}
		});

		customerMenu = new JPanel();
		customerMenu.setLayout(new GridLayout(1, 2));
		customerMenu.add(loginBtn);
		//customerMenu.add(logoutBtn);
		//customerMenu.add(updateBalanceBtn);

		customerPanel = new JPanel();
		customerPanel.setLayout(new BorderLayout());
		customerPanel.add(customerScrollPane);
		customerPanel.add(resetBtn, BorderLayout.SOUTH);
		customerPanel.add(customerMenu, BorderLayout.NORTH);
		return customerPanel;

	}


	// DRIVER SECTION

	private JPanel createDriverPanel() {
		driverTable = new JTable();
		driverScrollPane = new JScrollPane(driverTable);

		JButton driverUpdateInfoBtn = new JButton("Update Information");
		JButton driverViewAllShiftsBtn = new JButton("View All Shifts");
		JButton driverViewWeekShiftsBtn = new JButton("View All Shifts This Week");
		JButton driverGetShiftsBtn = new JButton("Get Shifts");
		final JButton logoutBtn = new JButton("Logout");
		final JButton loginBtn = new JButton("Login");

		final JPanel menuBase = new JPanel();		
		menuBase.setLayout(new BorderLayout());
		menuBase.add(loginBtn, BorderLayout.NORTH);

		// Add Buttons to driverMenu
		driverMenu = new JPanel();
		driverMenu.setLayout(new GridLayout(1, 3));
		driverMenu.add(driverGetShiftsBtn);
		driverMenu.add(driverViewAllShiftsBtn);
		driverMenu.add(driverViewWeekShiftsBtn);
		driverMenu.add(driverUpdateInfoBtn);
		driverMenu.setVisible(false);
		menuBase.add(driverMenu, BorderLayout.SOUTH);

		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel loginPanel = new JPanel();
				loginPanel.setLayout(new GridLayout(0, 1));
				JLabel loginLabel = new JLabel("Enter Employee ID");
				JTextField loginField = new JTextField();
				loginPanel.add(loginLabel);
				loginPanel.add(loginField);

				String title = "Login";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, loginPanel, title, option);
					if (input == JOptionPane.OK_OPTION) {
						try {
							int did = Integer.parseInt(loginField.getText());
							Driver driver= new Driver();
							ResultTableModel passResults = driver.login(did);
							if (passResults.empty) {
								JOptionPane.showMessageDialog(null, "Login failed");
							}
							else {
								empId = did;
								String name = passResults.getValueAt(0, 1).toString();
								welcome.setText("Welcome, " + name);
								driverMenu.setVisible(true);
								menuBase.add(logoutBtn, BorderLayout.NORTH);
								menuBase.remove(loginBtn);
								driverTable.removeAll();
								driverTable.setModel(passResults);
								validInput = true;
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, "Login failed");
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}
		});

		logoutBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Driver driver = new Driver();
				welcome.setText("Welcome, Guest");
				empId = -1;
				menuBase.add(loginBtn, BorderLayout.NORTH);
				menuBase.remove(logoutBtn);
				driverTable.removeAll();
				driverTable.setModel(driver.login(-1));
				driverMenu.setVisible(false);
			}
		});

		driverGetShiftsBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				String dateStr = JOptionPane.showInputDialog(null, "Enter Date as: YYYY-MM-DD");
				if (dateStr != null) {
					Driver driver = new Driver();
					ResultTableModel getShiftsResults = driver.viewShifts(empId, dateStr);
					if (getShiftsResults.empty) {
						JOptionPane.showMessageDialog(null, "No shifts found for given DriverID and date. "
								+ "Ensure date format is YYYY-MM-DD");
					} else {
						driverTable.removeAll();
						driverTable.setModel(getShiftsResults);
					}
				}
			}
		});

		driverViewAllShiftsBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Driver driver = new Driver();
				ResultTableModel viewAllShiftsResults = driver.viewAllShifts(empId);
				if (viewAllShiftsResults.empty) {
					JOptionPane.showMessageDialog(null, "No shifts found for given DriverID");
				} else {
					driverTable.removeAll();
					driverTable.setModel(viewAllShiftsResults);
				}
			}
		});

		driverViewWeekShiftsBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Driver driver = new Driver();
				ResultTableModel viewAllShiftsResults = driver.viewWeekShifts(empId);
				if (viewAllShiftsResults.empty) {
					JOptionPane.showMessageDialog(null, "No shifts this week.");
				}
				else {
					driverTable.removeAll();
					driverTable.setModel(viewAllShiftsResults);
				}
			}
		});

		driverUpdateInfoBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel updatePanel = new JPanel();
				updatePanel.setLayout(new GridLayout(0, 1));
				JLabel phoneLabel = new JLabel("Enter new phone number - no spaces or dashes");
				JLabel addressLabel = new JLabel("Enter new address");
				JTextField phoneField = new JTextField();
				JTextField addressField = new JTextField();
				updatePanel.add(phoneLabel);
				updatePanel.add(phoneField);
				updatePanel.add(addressLabel);
				updatePanel.add(addressField);

				String title = "Update Information";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, updatePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {			
							String newPhone = phoneField.getText().trim();
							String newAddress = addressField.getText().trim();
							Driver driver = new Driver();

							if (newPhone.equals("") && newAddress.equals("")) {
								JOptionPane.showMessageDialog(null, "Please fill in at least one field");
							} else {							
								if (!newPhone.equals("")) {
									driver.updatePhoneNum(empId, newPhone);
								}
								if (!newAddress.equals("")) {
									driver.updateAddress(empId, newAddress);	
								}
								driverTable.removeAll();							
								ResultTableModel viewDriverInfo = driver.viewDriverInfo(empId);
								driverTable.setModel(viewDriverInfo);
								JOptionPane.showMessageDialog(null, "Update Successful");
								validInput = true;
							}							
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}
		});		

		driverPanel = new JPanel();
		driverPanel.setLayout(new BorderLayout());
		driverPanel.add(driverScrollPane);
		driverPanel.add(menuBase, BorderLayout.NORTH);
		return driverPanel;
	}

	// OPERATOR SECTION

	private JPanel createOperatorPanel() {
		JPanel addPanel = createAddTabPanel();
		JPanel removePanel = createRemoveTabPanel();
		JPanel updatePanel = createUpdateTabPanel();
		JPanel reportPanel = createReportTabPanel();
		JPanel viewPanel = createViewTabPanel();
		final JTabbedPane operatorTabs = new JTabbedPane();
		operatorTabs.add("Add", addPanel);
		operatorTabs.add("Remove", removePanel);
		operatorTabs.addTab("Update", updatePanel);
		operatorTabs.addTab("Report", reportPanel);
		operatorTabs.addTab("View", viewPanel);
		operatorTabs.setTabPlacement(JTabbedPane.LEFT);
		operatorTabs.setVisible(false);

		final JButton operatorLogin = new JButton("Login");
		final JButton operatorLogout = new JButton("Logout");

		operatorLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel loginPanel = new JPanel();
				loginPanel.setLayout(new GridLayout(0, 1));
				JLabel loginLabel = new JLabel("Enter Operator Password:");
				JPasswordField loginField = new JPasswordField();
				loginPanel.add(loginLabel);
				loginPanel.add(loginField);

				String title = "Operator Login";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, loginPanel, title, option);
					if (input == JOptionPane.OK_OPTION) {
						try {
							@SuppressWarnings("deprecation")
							String pw = loginField.getText();;
							if (!pw.equals(password)) {
								JOptionPane.showMessageDialog(null, "Login failed");
							}
							else {
								welcome.setText("Welcome, operator");
								operatorPanel.remove(operatorLogin);
								operatorPanel.add(operatorLogout, BorderLayout.NORTH);
								operatorTabs.setVisible(true);
								validInput = true;
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, "Login failed");
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}
		});

		operatorLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				welcome.setText("Welcome, Guest");
				operatorPanel.remove(operatorLogout);
				operatorPanel.add(operatorLogin, BorderLayout.NORTH);
				operatorTabs.setVisible(false);
			}
		});

		operatorPanel = new JPanel();
		operatorPanel.setLayout(new BorderLayout());
		operatorPanel.add(operatorTabs, BorderLayout.CENTER);
		operatorPanel.add(operatorLogin, BorderLayout.NORTH);
		return operatorPanel;
	}

	private JPanel createAddTabPanel() {
		final JTable operatorTable = new JTable();
		JScrollPane operatorScrollPane = new JScrollPane(operatorTable);
		JPanel addPanel = new JPanel();
		JPanel addPane = new JPanel();
		final JButton addCustomerBtn = new JButton("Add Customer");
		final JButton addDriverBtn = new JButton("Add Driver");
		final JButton addRouteBtn = new JButton("Add Route");
		final JButton addStopBtn = new JButton("Add Stop");
		final JButton addDriverVehicleBtn = new JButton("Add Driver Vehicle");
		final JButton addDriverlessVehicleBtn = new JButton("Add Driverless Vehicle");
		addCustomerBtn.setVisible(false);
		addDriverBtn.setVisible(false);
		addRouteBtn.setVisible(false);
		addStopBtn.setVisible(false);
		addDriverVehicleBtn.setVisible(false);
		addDriverlessVehicleBtn.setVisible(false);

		String[] addOptions = {"Select where to add to...", "Customer", "Driver", "Route", "Stop", "Driver Vehicle", "Driverless Vehicle"};
		JComboBox<String> addList = new JComboBox<String>(addOptions);
		addPanel.setLayout(new BorderLayout());

		addPanel.add(addList, BorderLayout.NORTH);
		addPanel.add(operatorScrollPane);
		addPanel.add(addPane, BorderLayout.SOUTH);
		OverlayLayout overlay = new OverlayLayout(addPane);
		addPane.setLayout(overlay);
		addPane.add(addCustomerBtn);
		addPane.add(addDriverBtn);
		addPane.add(addRouteBtn);
		addPane.add(addStopBtn);
		addPane.add(addDriverVehicleBtn);
		addPane.add(addDriverlessVehicleBtn);

		addList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>) arg0.getSource();
				String addOption = (String)cb.getSelectedItem();

				if (addOption.equals("Select where to add to...")) {
					addCustomerBtn.setVisible(false);
					addDriverBtn.setVisible(false);
					addRouteBtn.setVisible(false);
					addStopBtn.setVisible(false);
					addDriverVehicleBtn.setVisible(false);
					addDriverlessVehicleBtn.setVisible(false);
					Customer customer = new Customer();
					operatorTable.setModel(customer.login(-1));
				}

				else if (addOption.equals("Customer")) {
					addCustomerBtn.setVisible(true);
					addDriverBtn.setVisible(false);
					addRouteBtn.setVisible(false);
					addStopBtn.setVisible(false);
					addDriverVehicleBtn.setVisible(false);
					addDriverlessVehicleBtn.setVisible(false);
					OwnsPass ownsPass = new OwnsPass();
					operatorTable.setModel(ownsPass.displayOwnsPass());
				}

				else if (addOption.equals("Driver")) {
					addDriverBtn.setVisible(true);
					addCustomerBtn.setVisible(false);
					addRouteBtn.setVisible(false);
					addStopBtn.setVisible(false);
					addDriverVehicleBtn.setVisible(false);
					addDriverlessVehicleBtn.setVisible(false);
					Driver driver = new Driver();
					operatorTable.setModel(driver.displayDrivers());
				}

				else if (addOption.equals("Route")) {
					addDriverBtn.setVisible(false);
					addCustomerBtn.setVisible(false);
					addRouteBtn.setVisible(true);
					addStopBtn.setVisible(false);
					addDriverVehicleBtn.setVisible(false);
					addDriverlessVehicleBtn.setVisible(false);
					Route route = new Route();
					operatorTable.setModel(route.displayRoutes());
				}

				else if (addOption.equals("Stop")) {
					addDriverBtn.setVisible(false);
					addCustomerBtn.setVisible(false);
					addRouteBtn.setVisible(false);
					addStopBtn.setVisible(true);
					addDriverVehicleBtn.setVisible(false);
					addDriverlessVehicleBtn.setVisible(false);
					Stop stop = new Stop();
					operatorTable.setModel(stop.displayStops());
				}

				else if (addOption.equals("Driver Vehicle")) {
					addDriverBtn.setVisible(false);
					addCustomerBtn.setVisible(false);
					addRouteBtn.setVisible(false);
					addStopBtn.setVisible(false);
					addDriverVehicleBtn.setVisible(true);
					addDriverlessVehicleBtn.setVisible(false);
					Driveable driveable = new Driveable();
					operatorTable.setModel(driveable.displayVehicles());
				}

				else if (addOption.equals("Driverless Vehicle")) {
					addDriverBtn.setVisible(false);
					addCustomerBtn.setVisible(false);
					addRouteBtn.setVisible(false);
					addStopBtn.setVisible(false);
					addDriverVehicleBtn.setVisible(false);
					addDriverlessVehicleBtn.setVisible(true);
					Driverless driverless = new Driverless();
					operatorTable.setModel(driverless.displayVehicles());
				}
			}
		});

		addCustomerBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel cidLabel = new JLabel("Enter CustomerID");
				JLabel nameLabel = new JLabel("Enter Customer name");
				JLabel pidLabel = new JLabel("Enter Pass ID");
				JTextField cidField = new JTextField();
				JTextField nameField = new JTextField();
				JTextField pidField = new JTextField();
				addPanel.add(cidLabel);
				addPanel.add(cidField);
				addPanel.add(nameLabel);
				addPanel.add(nameField);
				addPanel.add(pidLabel);
				addPanel.add(pidField);

				String title = "Add Customer";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int newCid = Integer.parseInt(cidField.getText().trim());			
							String newName = nameField.getText().trim();
							int newPid = Integer.parseInt(pidField.getText().trim());
							if (newName.equals("")) {
								JOptionPane.showMessageDialog(null, "Please fill in every field");							
							} else {								
								Customer customer = new Customer();
								OwnsPass ownsPass = new OwnsPass();
								boolean success = customer.insertCustomer(newCid, newName);
								if (success) {
									ownsPass.insertOwnsPass(newPid, 0, newCid);
									operatorTable.removeAll();							
									ResultTableModel viewCustomerInfo = ownsPass.displayOwnsPass();
									operatorTable.setModel(viewCustomerInfo);
									JOptionPane.showMessageDialog(null, "Customer added");
									validInput = true;
								} else {
									JOptionPane.showMessageDialog(null, insertError);
								}
							}							
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		addDriverBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel didLabel = new JLabel("Enter DriverID:");
				JLabel nameLabel = new JLabel("Enter driver name");
				JLabel phoneLabel = new JLabel("Enter phone number");
				JLabel addressLabel = new JLabel("Enter address");
				JTextField didField = new JTextField();
				JTextField nameField = new JTextField();
				JTextField phoneField = new JTextField();
				JTextField addressField = new JTextField();
				addPanel.add(didLabel);
				addPanel.add(didField);
				addPanel.add(nameLabel);
				addPanel.add(nameField);
				addPanel.add(phoneLabel);
				addPanel.add(phoneField);
				addPanel.add(addressLabel);
				addPanel.add(addressField);

				String title = "Add Driver";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int newDid = Integer.parseInt(didField.getText().trim());			
							String newName = nameField.getText().trim();
							String newPhone = phoneField.getText().trim();
							String newAddress = addressField.getText().trim();
							Driver driver = new Driver();

							if (newName.equals("") || newPhone.equals("") || newAddress.equals("")) {
								JOptionPane.showMessageDialog(null, "Please fill in every field");
							} else {							
								boolean success = driver.insertDriver(newDid, newName, newPhone, newAddress);
								if (success) {
									operatorTable.removeAll();							
									ResultTableModel viewDriverInfo = driver.displayDrivers();
									operatorTable.setModel(viewDriverInfo);
									JOptionPane.showMessageDialog(null, "Driver added");
									validInput = true;
								} else {
									JOptionPane.showMessageDialog(null, insertError);
								}
							}							
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		addRouteBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel ridLabel = new JLabel("Enter route number:");
				JLabel nameLabel = new JLabel("Enter route name:");
				JLabel startLabel = new JLabel("Enter start time HH:MM:SS");
				JLabel endLabel = new JLabel("Enter end time HH:MM:SS");
				JTextField ridField = new JTextField();
				JTextField nameField = new JTextField();
				JTextField startField = new JTextField();
				JTextField endField = new JTextField();
				addPanel.add(ridLabel);
				addPanel.add(ridField);
				addPanel.add(nameLabel);
				addPanel.add(nameField);
				addPanel.add(startLabel);
				addPanel.add(startField);
				addPanel.add(endLabel);
				addPanel.add(endField);

				String title = "Add Route";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						String ridStr = ridField.getText().trim();	
						String startStr = startField.getText().trim();
						String endStr = endField.getText().trim();
						String newName = nameField.getText().trim();							

						if (ridStr.equals("") || newName.equals("") || startStr.equals("") || endStr.equals("")) {
							JOptionPane.showMessageDialog(null, "Please fill in every field");
						} 
						else {
							try {
								int newRid = Integer.parseInt(ridStr);									
								Time newStart = Time.valueOf(startStr);
								Time newEnd = Time.valueOf(endStr);	
								Route route = new Route();
								boolean success = route.insertRoute(newRid, newName, newStart, newEnd);
								if (success) {
									operatorTable.removeAll();							
									ResultTableModel viewRouteInfo = route.displayRoutes();
									operatorTable.setModel(viewRouteInfo);
									JOptionPane.showMessageDialog(null, "Route added");
									validInput = true;
								} else {
									JOptionPane.showMessageDialog(null, insertError);
								}
							}							
							catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null, numberError);
							}
							catch (IllegalArgumentException iae) {
								JOptionPane.showMessageDialog(null, dateError);								
							};							
						}
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		addStopBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel sidLabel = new JLabel("Enter stop number:");
				JLabel nameLabel = new JLabel("Enter stop name:");
				JLabel locationLabel = new JLabel("Enter stop location");
				JTextField sidField = new JTextField();
				JTextField nameField = new JTextField();
				JTextField locationField = new JTextField();
				addPanel.add(sidLabel);
				addPanel.add(sidField);
				addPanel.add(nameLabel);
				addPanel.add(nameField);
				addPanel.add(locationLabel);
				addPanel.add(locationField);

				String title = "Add Stop";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						String sidStr = sidField.getText().trim();
						String newName = nameField.getText().trim();
						String newLocation = locationField.getText().trim();						
						if (sidStr.equals("") || newName.equals("") || newLocation.equals("")) {
							JOptionPane.showMessageDialog(null, "Please fill in every field");
						} else {							
							try {
								int newSid = Integer.parseInt(sidStr);							
								Stop stop = new Stop();

								boolean success = stop.insertStop(newSid, newName, newLocation);
								if (success) {
									operatorTable.removeAll();							
									ResultTableModel viewStopInfo = stop.displayStops();
									operatorTable.setModel(viewStopInfo);
									JOptionPane.showMessageDialog(null, "Stop added");
									validInput = true;
								} else {
									JOptionPane.showMessageDialog(null, insertError);
								}
							}
							catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null, numberError);
							}
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		addDriverVehicleBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel vidLabel = new JLabel("Enter vehicle number:");
				JLabel ageLabel = new JLabel("Enter vehicle age:");
				JLabel capLabel = new JLabel("Enter vehicle capacity:");
				JLabel typeLabel = new JLabel("Enter vehicle type:");
				JTextField vidField = new JTextField();
				JTextField ageField = new JTextField();				
				JTextField capField = new JTextField();
				JTextField typeField = new JTextField();
				addPanel.add(vidLabel);
				addPanel.add(vidField);
				addPanel.add(ageLabel);
				addPanel.add(ageField);
				addPanel.add(capLabel);
				addPanel.add(capField);
				addPanel.add(typeLabel);
				addPanel.add(typeField);

				String title = "Add Driveable Vehicle";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						String vidStr = vidField.getText().trim();
						String ageStr = ageField.getText().trim();
						String capStr = capField.getText().trim();
						String typeStr = typeField.getText().trim();

						if (vidStr.equals("") || ageStr.equals("") || capStr.equals("") || typeStr.equals("")) {
							JOptionPane.showMessageDialog(null, "Please fill in every field");
						} else {		
							try {
								int newVid = Integer.parseInt(vidField.getText().trim());
								int newAge = Integer.parseInt(ageField.getText().trim());
								int newCap = Integer.parseInt(capField.getText().trim());

								// Check constraint

								if (newVid > 0 && newAge >= 0 && newCap > 0) {
									Driveable driveable = new Driveable();			
									boolean success = driveable.insertVehicle(newVid, newAge, newCap, typeStr);
									if (success) {
										operatorTable.removeAll();							
										ResultTableModel viewDriverlessInfo = driveable.displayVehicles();
										operatorTable.setModel(viewDriverlessInfo);
										JOptionPane.showMessageDialog(null, "Driveable vehicle added");
										validInput = true;
									} else {
										JOptionPane.showMessageDialog(null, insertError);
									}
								} else {
									JOptionPane.showMessageDialog(null, negativeError);
								}
							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null, numberError);								
							}
						}
					} else {
						validInput = true;
					}
				} while (!validInput);
			}
		});

		addDriverlessVehicleBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel vidLabel = new JLabel("Enter vehicle number:");
				JLabel ageLabel = new JLabel("Enter vehicle age:");
				JLabel capLabel = new JLabel("Enter vehicle capacity:");
				JTextField vidField = new JTextField();
				JTextField ageField = new JTextField();				
				JTextField capField = new JTextField();
				addPanel.add(vidLabel);
				addPanel.add(vidField);
				addPanel.add(ageLabel);
				addPanel.add(ageField);
				addPanel.add(capLabel);
				addPanel.add(capField);

				String title = "Add Driverless Vehicle";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						String vidStr = vidField.getText().trim();
						String ageStr = ageField.getText().trim();
						String capStr = capField.getText().trim();

						if (vidStr.equals("") || ageStr.equals("") || capStr.equals("")) {
							JOptionPane.showMessageDialog(null, "Please fill in every field");
						} else {		
							try {
								int newVid = Integer.parseInt(vidField.getText().trim());
								int newAge = Integer.parseInt(ageField.getText().trim());
								int newCap = Integer.parseInt(capField.getText().trim());

								// Check constraint

								if(newVid > 0 && newAge >= 0 && newCap > 0) {
									Driverless driverless = new Driverless();			
									boolean success = driverless.insertVehicle(newVid, newAge, newCap);
									if (success) {
										operatorTable.removeAll();							
										ResultTableModel viewDriverlessInfo = driverless.displayVehicles();
										operatorTable.setModel(viewDriverlessInfo);
										JOptionPane.showMessageDialog(null, "Driverless vehicle added");
										validInput = true;	
									} else {
										JOptionPane.showMessageDialog(null, insertError);
									}
								} else {
									JOptionPane.showMessageDialog(null, negativeError);
								}
							} catch (NumberFormatException nfe) {
								JOptionPane.showMessageDialog(null, numberError);								
							}
						}
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});		

		return addPanel;
	}

	private JPanel createRemoveTabPanel() {
		final JTable operatorTable = new JTable();
		JScrollPane operatorScrollPane = new JScrollPane(operatorTable);
		JPanel removePanel = new JPanel();
		JPanel removePane = new JPanel();
		final JButton removeCustomerBtn = new JButton("Remove Customer");
		final JButton removeDriverBtn = new JButton("Remove Driver");
		final JButton removeRouteBtn = new JButton("Remove Route");
		final JButton removeStopBtn = new JButton("Remove Stop");
		final JButton removeDriverVehicleBtn = new JButton("Remove Vehicle");
		final JButton removeDriverlessVehicleBtn = new JButton("Remove Vehicle");
		removeCustomerBtn.setVisible(false);
		removeDriverBtn.setVisible(false);
		removeRouteBtn.setVisible(false);
		removeStopBtn.setVisible(false);
		removeDriverVehicleBtn.setVisible(false);
		removeDriverlessVehicleBtn.setVisible(false);

		String[] removeOptions = {"Select where to remove from...", "Customer", "Driver", "Route", "Stop", "Driver Vehicle", "Driverless Vehicle"};
		JComboBox<String> removeList = new JComboBox<String>(removeOptions);
		removePanel.setLayout(new BorderLayout());

		removePanel.add(removeList, BorderLayout.NORTH);
		removePanel.add(operatorScrollPane);
		removePanel.add(removePane, BorderLayout.SOUTH);
		OverlayLayout overlay = new OverlayLayout(removePane);
		removePane.setLayout(overlay);
		removePane.add(removeCustomerBtn);
		removePane.add(removeDriverBtn);
		removePane.add(removeRouteBtn);
		removePane.add(removeStopBtn);
		removePane.add(removeDriverVehicleBtn);
		removePane.add(removeDriverlessVehicleBtn);

		removeList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>) arg0.getSource();
				String removeOption = (String)cb.getSelectedItem();

				if (removeOption.equals("Select where to remove from...")) {
					removeCustomerBtn.setVisible(false);
					removeDriverBtn.setVisible(false);
					removeRouteBtn.setVisible(false);
					removeStopBtn.setVisible(false);
					removeDriverVehicleBtn.setVisible(false);
					removeDriverlessVehicleBtn.setVisible(false);
					Customer customer = new Customer();
					operatorTable.setModel(customer.login(-1));
				}

				else if (removeOption.equals("Customer")) {
					removeCustomerBtn.setVisible(true);
					removeDriverBtn.setVisible(false);
					removeRouteBtn.setVisible(false);
					removeStopBtn.setVisible(false);
					removeDriverVehicleBtn.setVisible(false);
					removeDriverlessVehicleBtn.setVisible(false);
					Customer customer = new Customer();
					operatorTable.setModel(customer.displayCustomers());
				}

				else if (removeOption.equals("Driver")) {
					removeDriverBtn.setVisible(true);
					removeCustomerBtn.setVisible(false);
					removeRouteBtn.setVisible(false);
					removeStopBtn.setVisible(false);
					removeDriverVehicleBtn.setVisible(false);
					removeDriverlessVehicleBtn.setVisible(false);
					Driver driver = new Driver();
					operatorTable.setModel(driver.displayDrivers());
				}

				else if (removeOption.equals("Route")) {
					removeDriverBtn.setVisible(false);
					removeCustomerBtn.setVisible(false);
					removeRouteBtn.setVisible(true);
					removeStopBtn.setVisible(false);
					removeDriverVehicleBtn.setVisible(false);
					removeDriverlessVehicleBtn.setVisible(false);
					Route route = new Route();
					operatorTable.setModel(route.displayRoutes());
				}

				else if (removeOption.equals("Stop")) {
					removeDriverBtn.setVisible(false);
					removeCustomerBtn.setVisible(false);
					removeRouteBtn.setVisible(false);
					removeStopBtn.setVisible(true);
					removeDriverVehicleBtn.setVisible(false);
					removeDriverlessVehicleBtn.setVisible(false);
					Stop stop = new Stop();
					operatorTable.setModel(stop.displayStops());
				}

				else if (removeOption.equals("Driver Vehicle")) {
					removeDriverBtn.setVisible(false);
					removeCustomerBtn.setVisible(false);
					removeRouteBtn.setVisible(false);
					removeStopBtn.setVisible(false);
					removeDriverVehicleBtn.setVisible(true);
					removeDriverlessVehicleBtn.setVisible(false);
					Driveable driveable = new Driveable();
					operatorTable.setModel(driveable.displayVehicles());
				}

				else if (removeOption.equals("Driverless Vehicle")) {
					removeDriverBtn.setVisible(false);
					removeCustomerBtn.setVisible(false);
					removeRouteBtn.setVisible(false);
					removeStopBtn.setVisible(false);
					removeDriverVehicleBtn.setVisible(false);
					removeDriverlessVehicleBtn.setVisible(true);
					Driverless driverless = new Driverless();
					operatorTable.setModel(driverless.displayVehicles());
				}
			}
		});

		removeCustomerBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel removePanel = new JPanel();
				removePanel.setLayout(new GridLayout(0, 1));
				JLabel cidLabel = new JLabel("Enter CustomerID");
				JTextField cidField = new JTextField();
				removePanel.add(cidLabel);
				removePanel.add(cidField);

				String title = "Remove Customer";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, removePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int cid = Integer.parseInt(cidField.getText().trim());			
							Customer customer = new Customer();				
							boolean success = customer.deleteCustomer(cid);
							if (success) {
								operatorTable.removeAll();							
								ResultTableModel viewCustomerInfo = customer.displayCustomers();
								operatorTable.setModel(viewCustomerInfo);
								JOptionPane.showMessageDialog(null, "Customer " + cid + " removed");
								validInput = true;	
							} else {
								JOptionPane.showMessageDialog(null, deleteError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		removeDriverBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel removePanel = new JPanel();
				removePanel.setLayout(new GridLayout(0, 1));
				JLabel didLabel = new JLabel("Enter Driver ID:");
				JTextField didField = new JTextField();
				removePanel.add(didLabel);
				removePanel.add(didField);

				String title = "Remove Driver";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, removePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int did = Integer.parseInt(didField.getText().trim());			
							Driver driver = new Driver();
							boolean success = driver.deleteDriver(did);
							if (success) {
								operatorTable.removeAll();							
								ResultTableModel viewDriverInfo = driver.displayDrivers();
								operatorTable.setModel(viewDriverInfo);
								JOptionPane.showMessageDialog(null, "Driver " + did + " removed");
								validInput = true;
							} else {
								JOptionPane.showMessageDialog(null, deleteError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		removeRouteBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel removePanel = new JPanel();
				removePanel.setLayout(new GridLayout(0, 1));
				JLabel ridLabel = new JLabel("Enter route number:");
				JTextField ridField = new JTextField();
				removePanel.add(ridLabel);
				removePanel.add(ridField);

				String title = "Remove Route";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, removePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int rid = Integer.parseInt(ridField.getText().trim());			
							Route route = new Route();							
							boolean success = route.deleteRoute(rid);
							if (success) {
								operatorTable.removeAll();							
								ResultTableModel viewRouteInfo = route.displayRoutes();
								operatorTable.setModel(viewRouteInfo);
								JOptionPane.showMessageDialog(null, "Route removed");
								validInput = true;	
							} else {
								JOptionPane.showMessageDialog(null, deleteError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		removeStopBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel removePanel = new JPanel();
				removePanel.setLayout(new GridLayout(0, 1));
				JLabel sidLabel = new JLabel("Enter stop number:");
				JTextField sidField = new JTextField();
				removePanel.add(sidLabel);
				removePanel.add(sidField);

				String title = "Remove Stop";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, removePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int sid = Integer.parseInt(sidField.getText().trim());			
							Stop stop = new Stop();						
							boolean success = stop.deleteStop(sid);
							if (success) {
								operatorTable.removeAll();							
								ResultTableModel viewStopInfo = stop.displayStops();
								operatorTable.setModel(viewStopInfo);
								JOptionPane.showMessageDialog(null, "Stop removed");
								validInput = true;	
							} else {
								JOptionPane.showMessageDialog(null, deleteError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}			
		});

		removeDriverVehicleBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel removePanel = new JPanel();
				removePanel.setLayout(new GridLayout(0, 1));
				JLabel vidLabel = new JLabel("Enter vehicle number:");
				JTextField vidField = new JTextField();
				removePanel.add(vidLabel);
				removePanel.add(vidField);

				String title = "Remove Vehicle";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, removePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int vid = Integer.parseInt(vidField.getText().trim());			
							Driveable driveable = new Driveable();		
							boolean success = driveable.deleteVehicle(vid);
							if (success) {
								operatorTable.removeAll();							
								ResultTableModel viewDriverVehicleInfo = driveable.displayVehicles();
								operatorTable.setModel(viewDriverVehicleInfo);
								JOptionPane.showMessageDialog(null, "Vehicle removed");
								validInput = true;
							} else {
								JOptionPane.showMessageDialog(null, deleteError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}	
		});

		removeDriverlessVehicleBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel removePanel = new JPanel();
				removePanel.setLayout(new GridLayout(0, 1));
				JLabel vidLabel = new JLabel("Enter vehicle number:");
				JTextField vidField = new JTextField();
				removePanel.add(vidLabel);
				removePanel.add(vidField);

				String title = "Remove Vehicle";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, removePanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int vid = Integer.parseInt(vidField.getText().trim());			
							Driverless driverless = new Driverless();		
							boolean success = driverless.deleteVehicle(vid);
							if (success) {
								operatorTable.removeAll();							
								ResultTableModel viewDriverVehicleInfo = driverless.displayVehicles();
								operatorTable.setModel(viewDriverVehicleInfo);
								JOptionPane.showMessageDialog(null, "Vehicle removed");
								validInput = true;
							} else {
								JOptionPane.showMessageDialog(null, deleteError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}	
		});

		return removePanel;
	}

	// UPDATE SECTION

	private JPanel createUpdateTabPanel() {
		final JTable operatorTable = new JTable();
		JScrollPane operatorScrollPane = new JScrollPane(operatorTable);
		JPanel updatePanel = new JPanel();
		JPanel updatePane = new JPanel();
		final JButton updateCustomerNameBtn = new JButton("Update Customer name");
		final JButton updateCustomerBalanceBtn = new JButton("Update Customer balance");
		updateCustomerNameBtn.setVisible(false);
		updateCustomerBalanceBtn.setVisible(false);

		String[] addOptions = {"Select where to update from...", "Customer"};
		JComboBox<String> updateList = new JComboBox<String>(addOptions);
		updatePanel.setLayout(new BorderLayout());

		updatePanel.add(updateList, BorderLayout.NORTH);
		updatePanel.add(operatorScrollPane);
		updatePanel.add(updatePane, BorderLayout.SOUTH);
		updatePane.setLayout(new GridLayout(0, 2));
		updatePane.add(updateCustomerNameBtn);
		updatePane.add(updateCustomerBalanceBtn);

		updateList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>) arg0.getSource();
				String addOption = (String)cb.getSelectedItem();

				if (addOption.equals("Select where to update from...")) {
					Customer customer = new Customer();
					updateCustomerNameBtn.setVisible(false);
					updateCustomerBalanceBtn.setVisible(false);
					operatorTable.setModel(customer.login(-1));
				}

				else if (addOption.equals("Customer")) {
					updateCustomerNameBtn.setVisible(true);
					updateCustomerBalanceBtn.setVisible(true);
					OwnsPass ownsPass = new OwnsPass();
					operatorTable.setModel(ownsPass.displayOwnsPass());
				}
			}
		});

		updateCustomerNameBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel cidLabel = new JLabel("Enter Customer ID:");
				JLabel nameLabel = new JLabel("Enter new name:");
				JTextField cidField = new JTextField();
				JTextField nameField = new JTextField();
				addPanel.add(cidLabel);
				addPanel.add(cidField);
				addPanel.add(nameLabel);
				addPanel.add(nameField);

				String title = "Update Customer name";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int newCid = Integer.parseInt(cidField.getText().trim());			
							String newName = nameField.getText().trim();
							Customer customer = new Customer();

							if (newName.equals("")) {
								JOptionPane.showMessageDialog(null, "Please fill in every field.");
							} else {							
								customer.updateName(newCid, newName);
								operatorTable.removeAll();							
								ResultTableModel viewCustomerInfo = customer.displayCustomers();
								operatorTable.setModel(viewCustomerInfo);
								JOptionPane.showMessageDialog(null, "Customer name updated.");
								validInput = true;
							}							
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, "Invalid format - please try again.");
						};
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		updateCustomerBalanceBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JPanel addPanel = new JPanel();
				addPanel.setLayout(new GridLayout(0, 1));
				JLabel cidLabel = new JLabel("Enter Customer ID:");
				JLabel balanceLabel = new JLabel("Enter amount to add to balance:");
				JTextField cidField = new JTextField();
				JTextField balanceField = new JTextField();
				addPanel.add(cidLabel);
				addPanel.add(cidField);
				addPanel.add(balanceLabel);
				addPanel.add(balanceField);

				String title = "Update Customer balance";
				int option = JOptionPane.OK_CANCEL_OPTION;
				boolean validInput = false;

				do {
					int input = JOptionPane.showConfirmDialog(null, addPanel, title, option);
					if(input == JOptionPane.OK_OPTION) {
						try {
							int newCid = Integer.parseInt(cidField.getText().trim());			
							int newBalance = Integer.parseInt(balanceField.getText().trim());

							if (newBalance > 0) {
								Customer customer = new Customer();
								customer.updateBalance(newCid, newBalance);
								OwnsPass ownsPass = new OwnsPass();
								operatorTable.removeAll();							
								ResultTableModel viewOwnsPassInfo = ownsPass.displayOwnsPass();
								operatorTable.setModel(viewOwnsPassInfo);
								JOptionPane.showMessageDialog(null, "Balance updated");
								validInput = true;
							} else {
								JOptionPane.showMessageDialog(null, negativeError);
							}
						} catch (NumberFormatException nfe) {
							JOptionPane.showMessageDialog(null, numberError);
						}
					} else {
						validInput = true;
					}
				} while (!validInput);
			}				
		});

		return updatePanel;
	}

	// REPORT SECTION

	public JPanel createReportTabPanel() {
		final JTable reportTable = new JTable();
		JScrollPane reportTableScrollPane = new JScrollPane(reportTable);
		final JPanel reportPanel = new JPanel();

		String[] options = {"This month", "January", "February", "March", "April", "May", "June", "July", 
				"August", "September", "October", "November", "December"};
		final JComboBox<String> monthList = new JComboBox<String>(options);
		monthList.setVisible(false);

		JButton maximumBtn = new JButton("Busiest route(s)");
		JButton minimumBtn = new JButton("Quietest route(s)");
		JButton resetBtn = new JButton("All routes");
		final JPanel routeReportPane = new JPanel();
		routeReportPane.setLayout(new FlowLayout());
		routeReportPane.add(maximumBtn);
		routeReportPane.add(minimumBtn);
		routeReportPane.add(resetBtn);
		routeReportPane.setVisible(false);
		
		JPanel optionPanel = new JPanel();
		optionPanel.add(routeReportPane);
		optionPanel.add(monthList);

		String[] reportOptions = {"Select report to view...", "Vehicle", "Route"};
		JComboBox<String> reportList = new JComboBox<String>(reportOptions);

		reportPanel.setLayout(new BorderLayout());
		reportPanel.add(reportList, BorderLayout.NORTH);
		reportPanel.add(reportTableScrollPane);
		reportPanel.add(optionPanel, BorderLayout.SOUTH);

		reportList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>) arg0.getSource();
				String reportOption = (String)cb.getSelectedItem();
				Route route = new Route();

				if (reportOption.equals("Select report to view...")) {
					reportTable.setModel(route.searchRoutes(-1));
					monthList.setVisible(false);
					routeReportPane.setVisible(false);
				}

				else if (reportOption.equals("Vehicle")) {
					reportTable.setModel(route.searchRoutes(-1));
					reportPanel.remove(routeReportPane);
					monthList.setVisible(true);
					routeReportPane.setVisible(false);
				}

				else if (reportOption.equals("Route")) {
					routeReportPane.setVisible(true);
					monthList.setVisible(false);
					ResultTableModel report = route.customersPerRoute();
					if (report.empty)
						JOptionPane.showMessageDialog(null, "No one is using our vehicles :(");
					reportTable.setModel(report);
				}
			}
		});

		maximumBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Route route = new Route();
				ResultTableModel report = route.busiestRoute();
				if (report.empty)
					JOptionPane.showMessageDialog(null, "No one is using our vehicles :(");
				reportTable.setModel(report);
			}				
		});

		minimumBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Route route = new Route();
				ResultTableModel report = route.quietestRoute();
				if (report.empty)
					JOptionPane.showMessageDialog(null, "No one is using our vehicles :(");
				reportTable.setModel(report);
			}				
		});

		resetBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Route route = new Route();
				ResultTableModel report = route.customersPerRoute();
				if (report.empty)
					JOptionPane.showMessageDialog(null, "No one is using our vehicles :(");
				reportTable.setModel(report);
			}				
		});

		monthList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>) arg0.getSource();
				String monthOption = (String)cb.getSelectedItem();
				Vehicle vehicle = new Vehicle();
				ResultTableModel report = vehicle.searchVehicles(-1);

				if (monthOption.equals("January")) report = vehicle.generateVehicleReport(1);
				else if (monthOption.equals("February")) report = vehicle.generateVehicleReport(2);
				else if (monthOption.equals("March")) report = vehicle.generateVehicleReport(3);
				else if (monthOption.equals("April")) report = vehicle.generateVehicleReport(4);
				else if (monthOption.equals("May")) report = vehicle.generateVehicleReport(5);
				else if (monthOption.equals("June")) report = vehicle.generateVehicleReport(6);
				else if (monthOption.equals("July")) report = vehicle.generateVehicleReport(7);
				else if (monthOption.equals("August")) report = vehicle.generateVehicleReport(8);
				else if (monthOption.equals("September")) report = vehicle.generateVehicleReport(9);
				else if (monthOption.equals("October")) report = vehicle.generateVehicleReport(10);
				else if (monthOption.equals("November")) report = vehicle.generateVehicleReport(11);
				else if (monthOption.equals("December")) report = vehicle.generateVehicleReport(12);

				else {
					try {
						report = vehicle.generateVehicleReport(DATE.getCurrentDate().intValue());
					} catch (SQLException e) {
					}
				}

				if (report.empty) {
					JOptionPane.showMessageDialog(null, "No report for vehicles this month");
					report = vehicle.searchVehicles(-1);
				}

				reportTable.setModel(report);
			}
		});

		return reportPanel;
	}

	// VIEW SECTION

	private JPanel createViewTabPanel() {
		final JTable viewTable = new JTable();
		JScrollPane viewScrollPane = new JScrollPane(viewTable);
		JPanel viewPanel = new JPanel();
		JPanel viewPane = new JPanel();
		final JButton viewCustomersBtn = new JButton("View All Customers");
		final JButton viewDriversBtn = new JButton("View All Drivers");
		final JButton viewRoutesBtn = new JButton("View All Routes");
		final JButton viewStopsBtn = new JButton("View All Stops");
		final JButton viewDriverVehiclesBtn = new JButton("View All Driver Vehicles");
		final JButton viewDriverlessVehiclesBtn = new JButton("View All Driverless Vehicles");
		final JButton searchCustomerBtn = new JButton("Search Customer");
		final JButton searchDriverBtn = new JButton("Search Driver");
		final JButton searchRouteBtn = new JButton("Search Route");
		final JButton searchStopBtn = new JButton("Search Stop");
		final JButton searchDriverVehicleBtn = new JButton("Search Driver Vehicle");
		final JButton searchDriverlessVehicleBtn = new JButton("Search Driverless Vehicle");
		viewCustomersBtn.setVisible(false);
		viewDriversBtn.setVisible(false);
		viewRoutesBtn.setVisible(false);
		viewStopsBtn.setVisible(false);
		viewDriverVehiclesBtn.setVisible(false);
		viewDriverlessVehiclesBtn.setVisible(false);
		searchCustomerBtn.setVisible(false);
		searchDriverBtn.setVisible(false);
		searchRouteBtn.setVisible(false);
		searchStopBtn.setVisible(false);
		searchDriverVehicleBtn.setVisible(false);
		searchDriverlessVehicleBtn.setVisible(false);

		String[] viewOptions = {"Select what to view...", "Customers", "Drivers", 
				"Routes", "Stops", "Driver Vehicles",
				"Driverless Vehicles", "Vehicle Drivers"};
		JComboBox<String> viewList = new JComboBox<String>(viewOptions);
		viewPanel.setLayout(new BorderLayout());

		viewPanel.add(viewList, BorderLayout.NORTH);
		viewPanel.add(viewScrollPane);
		viewPanel.add(viewPane, BorderLayout.SOUTH);
		viewPane.setLayout(new FlowLayout());    // TODO: Fix hidden search btns
		viewPane.add(viewCustomersBtn);
		viewPane.add(viewDriversBtn);
		viewPane.add(viewRoutesBtn);
		viewPane.add(viewStopsBtn);
		viewPane.add(viewDriverVehiclesBtn);
		viewPane.add(viewDriverlessVehiclesBtn);
		viewPane.add(searchCustomerBtn);
		viewPane.add(searchDriverBtn);
		viewPane.add(searchStopBtn);
		viewPane.add(searchRouteBtn);
		viewPane.add(searchDriverVehicleBtn);
		viewPane.add(searchDriverlessVehicleBtn);


		viewList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>) arg0.getSource();
				String addOption = (String)cb.getSelectedItem();

				if (addOption.equals("Select what to view...")) {
					viewCustomersBtn.setVisible(false);
					viewDriversBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(false);
					Customer customer = new Customer();
					viewTable.setModel(customer.login(-1));
				}

				else if (addOption.equals("Customers")) {
					viewCustomersBtn.setVisible(true);
					viewDriversBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(true);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(false);
					OwnsPass ownsPass = new OwnsPass();
					viewTable.setModel(ownsPass.displayOwnsPass());
				}

				else if (addOption.equals("Drivers")) {
					viewDriversBtn.setVisible(true);
					viewCustomersBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(true);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(false);
					Driver driver = new Driver();
					viewTable.setModel(driver.displayDrivers());
				}

				else if (addOption.equals("Routes")) {
					viewDriversBtn.setVisible(false);
					viewCustomersBtn.setVisible(false);
					viewRoutesBtn.setVisible(true);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(true);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(false);
					Route route = new Route();
					viewTable.setModel(route.displayRoutes());
				}

				else if (addOption.equals("Stops")) {
					viewDriversBtn.setVisible(false);
					viewCustomersBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(true);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(true);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(false);
					Stop stop = new Stop();
					viewTable.setModel(stop.displayStops());
				}

				else if (addOption.equals("Driver Vehicles")) {
					viewDriversBtn.setVisible(false);
					viewCustomersBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(true);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(true);
					searchDriverlessVehicleBtn.setVisible(false);
					Driveable driveable = new Driveable();
					viewTable.setModel(driveable.displayVehicles());
				}

				else if (addOption.equals("Driverless Vehicles")) {
					viewDriversBtn.setVisible(false);
					viewCustomersBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(true);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(true);
					Driverless driverless = new Driverless();
					viewTable.setModel(driverless.displayVehicles());
				}

				else if (addOption.equals("Vehicle Drivers")) {
					viewDriversBtn.setVisible(false);
					viewCustomersBtn.setVisible(false);
					viewRoutesBtn.setVisible(false);
					viewStopsBtn.setVisible(false);
					viewDriverVehiclesBtn.setVisible(false);
					viewDriverlessVehiclesBtn.setVisible(false);
					searchCustomerBtn.setVisible(false);
					searchDriverBtn.setVisible(false);
					searchRouteBtn.setVisible(false);
					searchStopBtn.setVisible(false);
					searchDriverVehicleBtn.setVisible(false);
					searchDriverlessVehicleBtn.setVisible(false);
					DrivenBy drivenBy = new DrivenBy();
					viewTable.setModel(drivenBy.displayDrivenBy());
				}
			}
		});

		viewCustomersBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				OwnsPass ownsPass = new OwnsPass();
				viewTable.setModel(ownsPass.displayOwnsPass());
			}				
		});

		searchCustomerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String cidStr = JOptionPane.showInputDialog(null, "Enter Customer ID");
				if (cidStr != null) {
					try {
						int cid = Integer.parseInt(cidStr);
						OwnsPass ownsPass = new OwnsPass();
						ResultTableModel search = ownsPass.searchOwnsPass(cid);
						if (search.empty) {
							JOptionPane.showMessageDialog(null, "No customer found");
						}
						else {
							viewTable.removeAll();
							viewTable.setModel(search);
						}
					}
					catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}
				}
			}
		});

		viewDriversBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Driver driver = new Driver();
				viewTable.setModel(driver.displayDrivers());
			}				
		});

		searchDriverBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String didStr = JOptionPane.showInputDialog(null, "Enter Driver ID");
				if (didStr != null) {
					try {
						int did = Integer.parseInt(didStr);
						Driver driver = new Driver();
						ResultTableModel search = driver.viewDriverInfo(did);
						if (search.empty) {
							JOptionPane.showMessageDialog(null, "No driver found");
						}
						else {
							viewTable.removeAll();
							viewTable.setModel(search);
						}						
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}
				}
			}
		});

		viewRoutesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Route route = new Route();
				viewTable.setModel(route.displayRoutes());
			}				
		});

		searchRouteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String ridStr = JOptionPane.showInputDialog(null, "Enter Route ID");
				if (ridStr != null) {
					try {
						int rid = Integer.parseInt(ridStr);
						Route route = new Route();
						ResultTableModel search = route.searchRoutes(rid);
						if (search.empty) {
							JOptionPane.showMessageDialog(null, "No route found");
						}
						else {
							viewTable.removeAll();
							viewTable.setModel(search);
						}
					} 
					catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}
				}
			}
		});

		viewStopsBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Stop stop = new Stop();
				viewTable.setModel(stop.displayStops());
			}				
		});

		searchStopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String sidStr = JOptionPane.showInputDialog(null, "Enter Stop ID");
				if (sidStr != null) {
					try {
						int sid = Integer.parseInt(sidStr);
						Stop stop = new Stop();
						ResultTableModel search = stop.searchStops(sid);
						if (search.empty) {
							JOptionPane.showMessageDialog(null, "No stop found");
						}
						else {
							viewTable.removeAll();
							viewTable.setModel(search);
						}						
					}
					catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}
				}
			}
		});

		viewDriverVehiclesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Driveable driveable = new Driveable();
				viewTable.setModel(driveable.displayVehicles());
			}				
		});

		searchDriverVehicleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String vidStr = JOptionPane.showInputDialog(null, "Enter Vehicle ID");
				if(vidStr != null) {
					try {
						int vid = Integer.parseInt(vidStr);
						Driveable driveable = new Driveable();
						ResultTableModel search = driveable.searchVehicles(vid);
						if (search.empty) {
							JOptionPane.showMessageDialog(null, "No driver vehicle found");
						}
						else {
							viewTable.removeAll();
							viewTable.setModel(search);
						}						
					}
					catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}
				}
			}
		});

		viewDriverlessVehiclesBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Driverless driverless = new Driverless();
				viewTable.setModel(driverless.displayVehicles());
			}				
		});

		searchDriverlessVehicleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String vidStr = JOptionPane.showInputDialog(null, "Enter Vehicle ID");
				if (vidStr != null) {
					try {
						int vid = Integer.parseInt(vidStr);
						Driverless driverless = new Driverless();
						ResultTableModel search = driverless.searchVehicles(vid);
						if (search.empty) {
							JOptionPane.showMessageDialog(null, "No driverless vehicle found");
						}
						else {
							viewTable.removeAll();
							viewTable.setModel(search);
						}						
					}
					catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(null, numberError);
					}		
				}
			}
		});

		return viewPanel;
	}
}
