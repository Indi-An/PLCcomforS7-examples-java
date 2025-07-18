package example_app;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.indian.plccom.fors7.authentication;
import com.indian.plccom.fors7.ConnectCallbackNotifier;
import com.indian.plccom.fors7.ConnectResult;
import com.indian.plccom.fors7.ConnectionStateChangeNotifier;
import com.indian.plccom.fors7.MPI_Device;
import com.indian.plccom.fors7.OperationResult;
import com.indian.plccom.fors7.PLCcomCoreDevice;
import com.indian.plccom.fors7.PLCcomDevice;
import com.indian.plccom.fors7.PPI_Device;
import com.indian.plccom.fors7.SymbolicDevice;
import com.indian.plccom.fors7.TCP_ISO_Device;
import com.indian.plccom.fors7.Tls13Device;
import com.indian.plccom.fors7.IProjectImportProgressChangedCallback;
import com.indian.plccom.fors7.LegacySymbolicDevice;
import com.indian.plccom.fors7.eBaudrate;
import com.indian.plccom.fors7.eConnectionState;
import com.indian.plccom.fors7.ePLCType;
import com.indian.plccom.fors7.eSpeed;
import com.indian.plccom.fors7.eTypeOfCommunication;
import com.indian.plccom.fors7.IConnectCallback;
import com.indian.plccom.fors7.IConnectionStateChangeCallback;

import example_app.DisabledJPanel.DisabledJPanel;
import jssc.SerialPortList;

import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JProgressBar;

public class Main extends JFrame
		implements IConnectionStateChangeCallback, IConnectCallback, IProjectImportProgressChangedCallback {

	private PLCcomCoreDevice _device = new TCP_ISO_Device();
	private ResourceBundle resources = ResourceBundle.getBundle("resources");
	static int CountOpenDialogs = 0;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextPane txtWarning;
	private JTextField txtSerial;
	private JTextField txtUser;
	private JPanel grbConnectionSettings;
	private DisabledJPanel panAddress;
	private JComboBox<Locale> cmbLanguage;
	private JButton btnEditConnectionSettings;
	private JLabel lblLanguage;
	private DisabledJPanel panSerial;
	private JPanel grbSerial;
	private JLabel lblSerialCode;
	private JLabel lblSerial;
	private JLabel lblUser;
	private JLabel label;
	private JTextField txtIdleTimeSpan;
	private JPanel grbParams;
	private JPanel grbAddress;
	private DisabledJPanel panParams;
	private DisabledJPanel panConnectionSettings;
	private JLabel lblConnectionType;
	private JLabel lblPLCType;
	private JLabel lblBaudrate;
	private JLabel lblBusSpeed;
	private JLabel lblProtectionUser;

	private JLabel lblProtectionPassword;
	private JComboBox<eTypeOfCommunication> cmbConnectionType;
	private JComboBox<ePLCType> cmbPLCType;
	private JComboBox<eBaudrate> cmbBaudrate;
	private JComboBox<eSpeed> cmbBusspeed;
	private JLabel lblAdress0;
	private JLabel lblAdress1;
	private JLabel lblAdress2;
	private JLabel lblAdress3;
	private JLabel lblAdress4;
	private JTextField txtAdress0;
	private JTextField txtAdress1;
	private JTextField txtAdress2;
	private JTextField txtAdress3;
	private JTextField txtAdress4;
	private JPanel statusBar;
	private JTextField lblDeviceState;
	private JTextField txtProgbarStatus;
	private JProgressBar prgProjectImport;
	private JPanel grbConnection;
	private DisabledJPanel panConnection;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JPanel grbAccess;
	private DisabledJPanel panAccess;
	private JButton btnReadWriteFunctions;
	private JButton btnOptimizedReadWrite;
	private JButton btnReadWriteSymbolic;
	private JButton btnSubscribeVariables;
	private JButton btnClose;
	private JButton btnDataServer;
	private JButton btnBlockFunctions;
	private JButton btnOtherFunctions;
	private JButton btnSaveConnectionSettings;
	private JLabel lblAsyncConnect;
	private JLabel lblAutoConnect;
	private JCheckBox chkAutoConnect;
	private JCheckBox chkAsyncConnect;
	private JLabel lblMaxIdleTime;
	private JButton btnPlcTypeHelp;
	private JTextField txtProtectionUser;
	private JPasswordField txtProtectionPassword;
	private JButton btnAlarmMessages;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
						
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setResizable(false);

		// set global lock and feel platform independent
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			@SuppressWarnings("rawtypes")
			java.util.Enumeration keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
					UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 11));
				}
			}

			// UIManager.setLookAndFeel(
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}

		setTitle("Start Example");
		
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(Main.class.getResource("/example_app/industrial_robot.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 782, 730);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing(e);
			}

		});

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblPic = new javax.swing.JLabel();
		lblPic.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPic.setVerticalAlignment(SwingConstants.TOP);
		lblPic.setBounds(525, 220, 165, 76);
		ImageIcon originalIcon = new ImageIcon(
				Main.class.getResource("/example_app/indi.logo2021.1_rgb_PLCcom_165_76.png"));
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(lblPic.getWidth(), lblPic.getHeight(), Image.SCALE_SMOOTH);
		lblPic.setIcon(new ImageIcon(scaledImage));
		contentPane.add(lblPic);

		txtWarning = new JTextPane();
		txtWarning.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtWarning.setBackground(SystemColor.info);
		txtWarning.setEditable(false);

		txtWarning.setText("");
		txtWarning.setBounds(12, 12, 713, 204);

		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
		txtWarning.selectAll();
		txtWarning.setParagraphAttributes(attributeSet, false);
		txtWarning.select(0, 0);
		contentPane.setLayout(null);
		JScrollPane scrollPanelvLog = new JScrollPane(txtWarning);
		JPanel lvLogContainer = new JPanel();
		lvLogContainer.setBounds(12, 12, 751, 204);
		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(scrollPanelvLog);
		contentPane.add(lvLogContainer);

		grbSerial = new JPanel();
		grbSerial.setBorder(new TitledBorder(UIManager

				.getBorder("TitledBorder.border"), "serial",

				TitledBorder.LEADING, TitledBorder.TOP, null,

				new Color(0, 0, 0)));
		grbSerial.setBounds(0, 0, 796, 64);

		panSerial = new DisabledJPanel(grbSerial);
		panSerial.setDisabledColor(new Color(240, 240, 240, 100));
		panSerial.setEnabled(false);
		panSerial.setBounds(12, 300, 746, 64);
		panSerial.setLayout(null);
		contentPane.add(panSerial);
		grbSerial.setLayout(null);

		lblSerialCode = new JLabel("enter serialcode first     >>>");
		lblSerialCode.setBounds(6, 26, 314, 16);
		lblSerialCode.setFont(new Font("Arial", Font.BOLD, 13));
		grbSerial.add(lblSerialCode);

		lblUser = new JLabel("user:");
		lblUser.setBounds(328, 15, 46, 14);
		grbSerial.add(lblUser);

		lblSerial = new JLabel("serial:");
		lblSerial.setBounds(328, 41, 46, 14);
		grbSerial.add(lblSerial);

		txtSerial = new JTextField();
		txtSerial.setBounds(370, 38, 357, 20);
		txtSerial.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtSerial_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtSerial_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtSerial_TextChanged(e);
			}

		});
		grbSerial.add(txtSerial);

		txtUser = new JTextField();
		txtUser.setBounds(370, 12, 357, 20);
		txtUser.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtUser_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtUser_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtUser_TextChanged(e);
			}

		});
		grbSerial.add(txtUser);

		btnEditConnectionSettings = new JButton("<html><center>edit</center><center>settings</center></html>");
		btnEditConnectionSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnEditConnectionSettings_actionPerformed(e);
			}
		});
		btnEditConnectionSettings.setIcon(
				new ImageIcon(Main.class.getResource("/example_app/btnEditConnectionSettings.Image.png")));
		btnEditConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnEditConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
		btnEditConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnEditConnectionSettings.setBounds(20, 225, 68, 68);
		contentPane.add(btnEditConnectionSettings);

		lblLanguage = new JLabel("\r\nlanguage");
		lblLanguage.setBounds(327, 227, 53, 20);
		contentPane.add(lblLanguage);

		cmbLanguage = new JComboBox<Locale>();
		cmbLanguage.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cmbLanguage_itemStateChanged(e);
			}
		});
		cmbLanguage.setBounds(380, 227, 91, 20);
		contentPane.add(cmbLanguage);

		grbConnectionSettings = new JPanel();
		grbConnectionSettings.setBounds(12, 371, 746, 195);
		grbConnectionSettings.setLayout(null);
		grbConnectionSettings.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Connection settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPane.add(grbConnectionSettings);

		panConnectionSettings = new DisabledJPanel(grbConnectionSettings);
		panConnectionSettings.setBounds(grbConnectionSettings.getBounds());
		panConnectionSettings.setDisabledColor(new Color(240, 240, 240, 100));
		panConnectionSettings.setEnabled(false);
		contentPane.add(panConnectionSettings);

		label = new JLabel("ms");
		label.setBounds(153, 64, 20, 13);
		grbConnectionSettings.add(label);

		txtIdleTimeSpan = new JTextField();
		txtIdleTimeSpan.setText("3000");
		txtIdleTimeSpan.setBounds(114, 60, 36, 20);

		grbConnectionSettings.add(txtIdleTimeSpan);

		chkAsyncConnect = new JCheckBox("");
		chkAsyncConnect.setSelected(true);
		chkAsyncConnect.setBounds(114, 87, 20, 23);
		grbConnectionSettings.add(chkAsyncConnect);

		chkAutoConnect = new JCheckBox("");
		chkAutoConnect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				chkAutoConnect_itemStateChanged(e);
			}
		});
		chkAutoConnect.setBounds(114, 31, 20, 23);
		grbConnectionSettings.add(chkAutoConnect);

		lblAsyncConnect = new JLabel("<html>asynchronous<br/>connect</html>");
		lblAsyncConnect.setBounds(10, 87, 103, 30);
		grbConnectionSettings.add(lblAsyncConnect);

		lblAutoConnect = new JLabel("auto connect");
		lblAutoConnect.setBounds(10, 31, 103, 20);
		grbConnectionSettings.add(lblAutoConnect);

		btnSaveConnectionSettings = new JButton("<html><center>save</center><center>settings</center></html>");
		btnSaveConnectionSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveConnectionSettings_actionPerformed(e);
			}
		});
		btnSaveConnectionSettings.setIcon(
				new ImageIcon(Main.class.getResource("/example_app/btnSaveConnectionSettings.Image.png")));
		btnSaveConnectionSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveConnectionSettings.setMargin(new Insets(0, 0, 0, 0));
		btnSaveConnectionSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveConnectionSettings.setBounds(10, 120, 68, 68);
		grbConnectionSettings.add(btnSaveConnectionSettings);

		grbParams = new JPanel();
		grbParams.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Parameter",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grbParams.setBounds(177, 8, 285, 169);
		grbConnectionSettings.add(grbParams);

		panParams = new DisabledJPanel(grbParams);
		grbParams.setLayout(null);

		lblConnectionType = new JLabel("ConnectionType");
		lblConnectionType.setBounds(10, 29, 90, 14);
		grbParams.add(lblConnectionType);

		lblPLCType = new JLabel("PLC Type");
		lblPLCType.setBounds(10, 54, 90, 14);
		grbParams.add(lblPLCType);

		lblBaudrate = new JLabel("Baudrate");
		lblBaudrate.setVisible(false);
		lblBaudrate.setBounds(10, 79, 90, 14);
		grbParams.add(lblBaudrate);

		lblBusSpeed = new JLabel("Busspeed");
		lblBusSpeed.setVisible(false);
		lblBusSpeed.setBounds(10, 104, 90, 14);
		grbParams.add(lblBusSpeed);

		cmbConnectionType = new JComboBox<eTypeOfCommunication>();
		cmbConnectionType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbConnectionType_actionPerformed(e);
			}
		});
		cmbConnectionType.setBounds(95, 26, 147, 20);
		grbParams.add(cmbConnectionType);

		cmbPLCType = new JComboBox<ePLCType>();
		cmbPLCType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				cmbPLCType_actionPerformed(e);
			}
		});
		cmbPLCType.setBounds(95, 51, 147, 20);
		grbParams.add(cmbPLCType);

		cmbBaudrate = new JComboBox<eBaudrate>();
		cmbBaudrate.setVisible(false);
		cmbBaudrate.setBounds(95, 76, 147, 20);
		grbParams.add(cmbBaudrate);

		cmbBusspeed = new JComboBox<eSpeed>();
		cmbBusspeed.setVisible(false);
		cmbBusspeed.setBounds(95, 101, 147, 20);
		grbParams.add(cmbBusspeed);

		btnPlcTypeHelp = new JButton("");
		btnPlcTypeHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPlcTypeHelp_actionPerformed(e);
			}
		});
		btnPlcTypeHelp.setBounds(248, 48, 28, 28);
		grbParams.add(btnPlcTypeHelp);
		btnPlcTypeHelp.setBackground(new Color(240, 240, 240));
		btnPlcTypeHelp.setIcon(new ImageIcon(Main.class.getResource("/example_app/symbol_questionmark.png")));
		btnPlcTypeHelp.setMargin(new Insets(0, 0, 0, 0));
		btnPlcTypeHelp.setHorizontalTextPosition(SwingConstants.CENTER);
		panParams.setBounds(grbParams.getBounds());
		panParams.setDisabledColor(new Color(240, 240, 240, 100));
		panParams.setEnabled(true);
		grbConnectionSettings.add(panParams);

		grbAddress = new JPanel();
		grbAddress.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Address",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grbAddress.setBounds(472, 8, 241, 169);
		grbConnectionSettings.add(grbAddress);
		grbAddress.setLayout(null);

		panAddress = new DisabledJPanel(grbAddress);
		panAddress.setBounds(grbAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		grbConnectionSettings.add(panAddress);

		lblAdress0 = new JLabel("IP");
		lblAdress0.setBounds(10, 30, 139, 14);
		grbAddress.add(lblAdress0);

		lblAdress1 = new JLabel("PLC Port (usually ISO 102)");
		lblAdress1.setBounds(10, 55, 139, 14);
		grbAddress.add(lblAdress1);

		lblAdress2 = new JLabel("Local Port (intern 0)");
		lblAdress2.setBounds(10, 80, 139, 14);
		grbAddress.add(lblAdress2);

		lblAdress3 = new JLabel("Rack ID");
		lblAdress3.setBounds(10, 105, 139, 14);
		grbAddress.add(lblAdress3);

		lblAdress4 = new JLabel("Slot ID");
		lblAdress4.setBounds(10, 130, 139, 14);
		grbAddress.add(lblAdress4);

		txtAdress0 = new JTextField();
		txtAdress0.setBounds(145, 27, 85, 20);
		grbAddress.add(txtAdress0);

		txtAdress1 = new JTextField();
		txtAdress1.setBounds(145, 52, 85, 20);
		grbAddress.add(txtAdress1);

		txtAdress2 = new JTextField();
		txtAdress2.setText("0");
		txtAdress2.setBounds(145, 77, 85, 20);
		grbAddress.add(txtAdress2);

		txtAdress3 = new JTextField();
		txtAdress3.setText("0");
		txtAdress3.setBounds(145, 102, 85, 20);
		grbAddress.add(txtAdress3);

		txtAdress4 = new JTextField();
		txtAdress4.setText("2");
		txtAdress4.setBounds(145, 127, 85, 20);
		grbAddress.add(txtAdress4);

		lblProtectionUser = new JLabel("User");
		lblProtectionUser.setBounds(10, 55, 139, 14);
		lblProtectionUser.setVisible(false);
		grbAddress.add(lblProtectionUser);

		lblProtectionPassword = new JLabel("Password");
		lblProtectionPassword.setBounds(10, 80, 139, 14);
		lblProtectionPassword.setVisible(false);
		grbAddress.add(lblProtectionPassword);

		txtProtectionUser = new JPasswordField();
		txtProtectionUser.setBounds(145, 52, 85, 20);
		txtProtectionUser.setVisible(false);
		grbAddress.add(txtProtectionUser);

		txtProtectionPassword = new JPasswordField();
		txtProtectionPassword.setBounds(145, 77, 85, 20);
		txtProtectionPassword.setVisible(false);
		txtProtectionPassword.setEchoChar('*');
		grbAddress.add(txtProtectionPassword);

		lblMaxIdleTime = new JLabel("<html>max. idle time until<br/>closing the port</html>");
		lblMaxIdleTime.setBounds(10, 55, 103, 30);
		grbConnectionSettings.add(lblMaxIdleTime);

		// Creating the StatusBar.
		// statusBar.setLayout(new BorderLayout());
		statusBar = new JPanel();
		statusBar.setBounds(0, 668, 758, 25);
		statusBar.setLayout(null);
		statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		statusBar.setBackground(Color.WHITE);
		contentPane.add(statusBar);

		lblDeviceState = new JTextField("Disconnected");
		lblDeviceState.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceState.setBackground(Color.WHITE);
		lblDeviceState.setFocusable(false);
		lblDeviceState.setEditable(false);
		lblDeviceState.setSize(172, 18);
		lblDeviceState.setLocation(4, 2);
		statusBar.add(lblDeviceState);

		txtProgbarStatus = new JTextField("");
		txtProgbarStatus.setVisible(false);
		txtProgbarStatus.setHorizontalAlignment(SwingConstants.CENTER);
		txtProgbarStatus.setFocusable(false);
		txtProgbarStatus.setEditable(false);
		txtProgbarStatus.setBackground(Color.WHITE);
		txtProgbarStatus.setSize(162, 18);
		txtProgbarStatus.setLocation(180, 2);
		statusBar.add(txtProgbarStatus);

		prgProjectImport = new JProgressBar();
		prgProjectImport.setVisible(false);
		prgProjectImport.setBounds(474, 2, 146, 18);
		statusBar.add(prgProjectImport);

		grbConnection = new JPanel();
		grbConnection.setLayout(null);
		grbConnection.setBorder(null);
		grbConnection.setBounds(12, 570, 161, 94);
		contentPane.add(grbConnection);

		panConnection = new DisabledJPanel(grbConnection);

		btnConnect = new JButton("<html><center>connect</center></html>");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnConnect_actionPerformed(e);
			}
		});
		btnConnect.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnConnect.Image.png")));
		btnConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnConnect.setMargin(new Insets(0, 0, 0, 0));
		btnConnect.setHorizontalTextPosition(SwingConstants.CENTER);
		btnConnect.setBounds(10, 13, 68, 68);
		grbConnection.add(btnConnect);

		btnDisconnect = new JButton("<html><center>disconnect</center></html>");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDisconnect_actionPerformed(e);
			}
		});
		btnDisconnect.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnDisconnect.Image.png")));
		btnDisconnect.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnDisconnect.setMargin(new Insets(0, 0, 0, 0));
		btnDisconnect.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDisconnect.setBounds(88, 13, 68, 68);
		grbConnection.add(btnDisconnect);
		panConnection.setBounds(grbConnection.getBounds());
		panConnection.setDisabledColor(new Color(240, 240, 240, 100));
		panConnection.setEnabled(true);
		contentPane.add(panConnection);

		grbAccess = new JPanel();
		grbAccess.setLayout(null);
		grbAccess.setBorder(null);
		grbAccess.setBounds(190, 570, 483, 94);
		contentPane.add(grbAccess);

		panAccess = new DisabledJPanel(grbAccess);
		panAccess.setBounds(grbAccess.getBounds());
		panAccess.setDisabledColor(new Color(240, 240, 240, 100));
		panAccess.setEnabled(false);
		contentPane.add(panAccess);

		btnReadWriteFunctions = new JButton("<html><center>read write</center><center>functions</center></html>");
		btnReadWriteFunctions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadWriteFunctions_actionPerformed(e);
			}
		});
		btnReadWriteFunctions.setIcon(
				new ImageIcon(Main.class.getResource("/example_app/btnReadWriteFunctions.Image.png")));
		btnReadWriteFunctions.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReadWriteFunctions.setMargin(new Insets(0, 0, 0, 0));
		btnReadWriteFunctions.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReadWriteFunctions.setBounds(10, 13, 68, 68);
		grbAccess.add(btnReadWriteFunctions);

		btnOptimizedReadWrite = new JButton("<html><center>optimize</center><center>read write</center></html>");
		btnOptimizedReadWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadCollection_actionPerformed(e);
			}
		});

		btnOptimizedReadWrite
				.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnReadCollection.Image.png")));
		btnOptimizedReadWrite.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnOptimizedReadWrite.setMargin(new Insets(0, 0, 0, 0));
		btnOptimizedReadWrite.setHorizontalTextPosition(SwingConstants.CENTER);
		btnOptimizedReadWrite.setBounds(88, 13, 68, 68);
		grbAccess.add(btnOptimizedReadWrite);

		btnDataServer = new JButton("<html><center>start</center><center>data server</center></html>");
		btnDataServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDataServer_actionPerformed(e);

			}

		});
		btnDataServer.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnDataServer.Image.png")));
		btnDataServer.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnDataServer.setMargin(new Insets(0, 0, 0, 0));
		btnDataServer.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDataServer.setBounds(166, 13, 68, 68);
		grbAccess.add(btnDataServer);

		btnBlockFunctions = new JButton("<html><center>block</center><center>functions</center></html>");
		btnBlockFunctions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBlockFunctions_actionPerformed(e);
			}

		});
		btnBlockFunctions
				.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnBlockFunctions.Image.png")));
		btnBlockFunctions.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBlockFunctions.setMargin(new Insets(0, 0, 0, 0));
		btnBlockFunctions.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBlockFunctions.setBounds(244, 13, 68, 68);
		grbAccess.add(btnBlockFunctions);

		btnOtherFunctions = new JButton("<html><center>other</center><center>functions</center></html>");
		btnOtherFunctions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnOtherFunctions_actionPerformed(e);
			}

		});
		btnOtherFunctions
				.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnOtherFunctions.Image.png")));
		btnOtherFunctions.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnOtherFunctions.setMargin(new Insets(0, 0, 0, 0));
		btnOtherFunctions.setHorizontalTextPosition(SwingConstants.CENTER);
		btnOtherFunctions.setBounds(400, 13, 68, 68);
		grbAccess.add(btnOtherFunctions);

		btnReadWriteSymbolic = new JButton("<html><center>read write</center><center>symbolic</center></html>");
		btnReadWriteSymbolic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadWriteSymbolic_actionPerformed(e);
			}
		});
		btnReadWriteSymbolic.setIcon(
				new ImageIcon(Main.class.getResource("/example_app/btnReadWriteFunctions.Image.png")));
		btnReadWriteSymbolic.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReadWriteSymbolic.setMargin(new Insets(0, 0, 0, 0));
		btnReadWriteSymbolic.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReadWriteSymbolic.setBounds(10, 15, 68, 68);
		grbAccess.add(btnReadWriteSymbolic);

		btnSubscribeVariables = new JButton("<html><center>subscribe</center><center>variables</center></html>");
		btnSubscribeVariables.setIcon(
				new ImageIcon(Main.class.getResource("/example_app/btnReadWriteFunctions.Image.png")));
		btnSubscribeVariables.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSubscribeVariables.setMargin(new Insets(0, 0, 0, 0));
		btnSubscribeVariables.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSubscribeVariables.setBounds(88, 13, 68, 68);
		grbAccess.add(btnSubscribeVariables);

		btnAlarmMessages = new JButton("<html><center>Alarm</center><center>Messages</center></html>");
		btnAlarmMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAlarmMessages_actionPerformed(e);
			}
		});
		btnAlarmMessages.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAlarmMessages.setMargin(new Insets(0, 0, 0, 0));
		btnAlarmMessages.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnAlarmMessages.Image.png")));
		btnAlarmMessages.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAlarmMessages.setBounds(322, 13, 68, 68);
		grbAccess.add(btnAlarmMessages);

		btnClose = new JButton("<html><center>close</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(Main.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(686, 583, 68, 68);
		contentPane.add(btnClose);
	}


	private void formWindowOpened(WindowEvent arg0) {
		// Set location to center screen
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2);
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2);
		this.setLocation(x, y);

		// set language combobox
		Locale[] allLocale = new Locale[2];
		if (Locale.getDefault().getLanguage().toString().toLowerCase() == "de") {
			allLocale[0] = Locale.getDefault();
			allLocale[1] = new Locale("en", "US");

		} else if (Locale.getDefault().getLanguage().toString().toLowerCase() == "en") {
			allLocale[0] = Locale.getDefault();
			allLocale[1] = new Locale("de", "DE");

		} else {
			allLocale[0] = new Locale("en", "US");
			allLocale[1] = new Locale("de", "DE");
		}
		cmbLanguage.setModel(new DefaultComboBoxModel<Locale>(allLocale));

		// set UI language
		try {
			SetLanguage((Locale) cmbLanguage.getSelectedItem());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}

		cmbPLCType.setModel(new DefaultComboBoxModel<ePLCType>(ePLCType.values()));

		cmbConnectionType.setModel(new DefaultComboBoxModel<eTypeOfCommunication>(eTypeOfCommunication.values()));

		cmbBusspeed.setModel(new DefaultComboBoxModel<eSpeed>(eSpeed.values()));

		cmbBaudrate.setModel(new DefaultComboBoxModel<eBaudrate>(eBaudrate.values()));

		txtProgbarStatus.setText("DeviceType: " + _device.getClass().getName());

		LoadSettingsFromFile();

	}

	private void formWindowClosing(WindowEvent e) {
		if (_device != null) {
			_device.disConnect();
		}
	}

	private void btnClose_actionPerformed(ActionEvent e) {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			if (_device != null) {
				// unload and dispose all objects
				_device.disConnect();
				_device = null;
			}

		} finally {
			this.setCursor(Cursor.getDefaultCursor());
			System.exit(0);
		}
	}

	// incoming On_ConnectionStateChange event
	@Override
	public void On_ConnectionStateChange(eConnectionState value) {
		try {


			 // If we're not in the EDT, we move all the rest there:
		    if (!SwingUtilities.isEventDispatchThread()) {
		        SwingUtilities.invokeLater(() -> On_ConnectionStateChange(value));
		        return;
		    }


			if (!_device.getAutoConnectState()) {
				if (panAccess.isEnabled() != _device.IsConnected()) {
					panAccess.setEnabled(_device.IsConnected());
				}
				if (panParams.isEnabled() == _device.IsConnected()) {
					panParams.setEnabled(!_device.IsConnected());
				}
				if (panAddress.isEnabled() == _device.IsConnected()) {
					panAddress.setEnabled(!_device.IsConnected());
				}

				btnReadWriteSymbolic.setVisible(_device.getPLCType() == ePLCType.Symbolic_Tls13
						|| _device.getPLCType() == ePLCType.Symbolic_Legacy);
				btnSubscribeVariables.setVisible(_device.getPLCType() == ePLCType.Symbolic_Tls13
						|| _device.getPLCType() == ePLCType.Symbolic_Legacy);
				btnAlarmMessages.setEnabled(_device.getPLCType() == ePLCType.Symbolic_Tls13
						|| _device.getPLCType() == ePLCType.Symbolic_Legacy);
				btnReadWriteFunctions.setVisible(_device.getPLCType() != ePLCType.Symbolic_Tls13
						&& _device.getPLCType() != ePLCType.Symbolic_Legacy);
				btnAlarmMessages.setEnabled(_device.getPLCType() == ePLCType.Symbolic_Tls13
						|| _device.getPLCType() == ePLCType.Symbolic_Legacy);
				btnOptimizedReadWrite.setEnabled(_device.getPLCType() != ePLCType.Symbolic_Tls13
						&& _device.getPLCType() != ePLCType.Symbolic_Legacy);
				btnDataServer.setEnabled(_device.getPLCType() != ePLCType.Symbolic_Tls13
						&& _device.getPLCType() != ePLCType.Symbolic_Legacy);
				btnBlockFunctions.setEnabled(_device.getPLCType() != ePLCType.Symbolic_Tls13
						&& _device.getPLCType() != ePLCType.Symbolic_Legacy);
			}
			lblDeviceState.setText(resources.getString("State_" + value.toString())
					+ ((_device != null && _device.getDeviceInfo().getName() != null)
							? " " + _device.getDeviceInfo().getName()
							: ""));
			lblDeviceState.setBackground((value == eConnectionState.connected) ? Color.BLUE : Color.WHITE);
			lblDeviceState.setForeground((value == eConnectionState.connected) ? Color.WHITE : Color.BLACK);
		} catch (Exception ex) {

			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// incoming On_ConnectCallback event
	@Override
	public void On_ConnectCallback(ConnectResult Result) {
		if (!Result.getQuality().equals(OperationResult.eQuality.GOOD)) {

			String message = resources.getString("connect_unsuccessful") + System.getProperty("line.separator")
					+ resources.getString("MessageText") + Result.getMessage();

			if (Result.getInnerException() != null)
				message += System.getProperty("line.separator") + "InnerException: "
						+ Result.getInnerException().getMessage();

			JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
		}

		// DeRegister event for showing prject import progress
		if (_device instanceof SymbolicDevice) {
			if (SwingUtilities.isEventDispatchThread()) {
					((SymbolicDevice) _device).removeOnProjectImportProgressChangedListener(this);
					txtProgbarStatus.setVisible(false);
					prgProjectImport.setVisible(false);
			} else {
				SwingUtilities.invokeLater(() -> {
						((SymbolicDevice) _device).removeOnProjectImportProgressChangedListener(this);
						txtProgbarStatus.setVisible(false);
						prgProjectImport.setVisible(false);
				});
			}
		}

	}

	private void SetLanguage(Locale locale) {
		// set UI-Controls with actual Locale information
		// init ResourceManager

		// set thread locale
		Locale.setDefault(locale);

		ResourceBundle.clearCache();
		resources = ResourceBundle.getBundle("resources", locale);

		// set controls
		this.txtWarning.setText(resources.getString("txtWarning_Text"));
		this.setTitle(resources.getString("main_Text"));
		this.lblAutoConnect.setText(resources.getString("lblAutoConnect2"));
		this.lblMaxIdleTime.setText(resources.getString("lblmaxIdleTime_Text"));
		this.btnConnect.setText(resources.getString("btnConnect_Text"));
		this.btnDisconnect.setText(resources.getString("btnDisconnect_Text"));
		this.lblPLCType.setText(resources.getString("lblPLCType_Text"));
		this.grbAddress.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAdress_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grbConnectionSettings.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grbConnection_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.btnReadWriteFunctions.setText(resources.getString("btnReadWriteFunctions_Text"));
		this.btnOptimizedReadWrite.setText(resources.getString("btnOptimizedReadWrite_Text"));
		this.btnDataServer.setText(resources.getString("btnDataServer_Text"));
		this.btnOtherFunctions.setText(resources.getString("btnOtherFunctions_Text"));
		this.btnBlockFunctions.setText(resources.getString("btnBlockFunctions_Text"));
		this.lblSerialCode.setText(resources.getString("lblSerialCode_Text"));
		this.lblLanguage.setText(resources.getString("lblLanguage_Text"));

		this.lblBaudrate.setText(resources.getString("lblBaudrate_Text"));
		this.lblBusSpeed.setText(resources.getString("lblBusSpeed_Text"));
		this.lblAdress1.setText(resources.getString("lblPLCPort_Text"));
		this.lblAdress2.setText(resources.getString("lblLocalPort_Text"));
		this.lblAdress3.setText(resources.getString("lblRack_Text"));
		this.lblAdress4.setText(resources.getString("lblSlot_Text"));
		this.btnClose.setText(resources.getString("btnClose_Text"));
		this.btnEditConnectionSettings.setText(resources.getString("btnEditConnectionSettings_Text"));
		this.btnSaveConnectionSettings.setText(resources.getString("btnSaveConnectionSettings_Text"));
		this.lblConnectionType.setText(resources.getString("lblConnectionType_Text"));
		this.lblAsyncConnect.setText(resources.getString("chkAsyncConnect_TextAsync"));
		this.lblProtectionPassword.setText(resources.getString("lblPlcPassword_Text"));

	}

	private void cmbLanguage_itemStateChanged(ItemEvent e) {
		// set UI language
		try {
			SetLanguage((Locale) cmbLanguage.getSelectedItem());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void LoadSettingsFromFile() {
		Properties p = new Properties();

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// open file
			FileInputStream fis = new FileInputStream(new File("PlccomExSettings.xml").getAbsolutePath());
			p.loadFromXML(fis);

			// load saved settiungs from PLCcomModbusSlaveSettings.xml
			if (p.containsKey("user")) {
				txtUser.setText(p.getProperty("user"));
			}
			if (p.containsKey("serial")) {
				txtSerial.setText(p.getProperty("serial"));
			}

			if (p.containsKey("TypeOfCommunication")) {
				cmbConnectionType.setSelectedItem(eTypeOfCommunication.valueOf(p.getProperty("TypeOfCommunication")));
			}

			if (p.containsKey("ePLCtype")) {
				cmbPLCType.setSelectedItem(ePLCType.valueOf(p.getProperty("ePLCtype")));
			}

			if (p.containsKey("eBaudrate")) {
				cmbBaudrate.setSelectedItem(eBaudrate.valueOf(p.getProperty("eBaudrate")));
			}

			if (p.containsKey("eSpeed")) {
				cmbBusspeed.setSelectedItem(p.getProperty("eSpeed"));
			}

			if (p.containsKey("Adress0")) {
				txtAdress0.setText(p.getProperty("Adress0"));
			}

			if (p.containsKey("Adress1")) {
				txtAdress1.setText(p.getProperty("Adress1"));
			}

			if (p.containsKey("Adress2")) {
				txtAdress2.setText(p.getProperty("Adress2"));
			}

			if (p.containsKey("Adress3")) {
				txtAdress3.setText(p.getProperty("Adress3"));
			}

			if (p.containsKey("Adress4")) {
				txtAdress4.setText(p.getProperty("Adress4"));
			}

			if (p.containsKey("ProtectionUser")) {
				txtProtectionUser.setText(p.getProperty("ProtectionUser"));
			}

			if (p.containsKey("ProtectionPassword")) {
				String encryptedPassword = p.getProperty("ProtectionPassword");
				if (!encryptedPassword.trim().isEmpty())
					txtProtectionPassword.setText(AesEncryption.decryptString(encryptedPassword));
			}

			if (p.containsKey("chkAsyncConnect")) {
				chkAsyncConnect.setSelected(Boolean.valueOf(p.getProperty("chkAsyncConnect")));
			}

			if (p.containsKey("AutoConnect")) {
				if (cmbPLCType.getSelectedItem() == ePLCType.valueOf(p.getProperty("ePLCtype"))) {
					chkAutoConnect.setSelected(false);
				} else {
					chkAutoConnect.setSelected(Boolean.valueOf(p.getProperty("AutoConnect")));
				}
			}

		} catch (FileNotFoundException ignore) {

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			panConnection.setEnabled(!chkAutoConnect.isSelected());
			panAccess.setEnabled(chkAutoConnect.isSelected());
			setCursor(Cursor.getDefaultCursor());
		}

	}

	void btnSaveConnectionSettings_actionPerformed(ActionEvent arg) {

		// Try convert txtIdeleTimeSpan text value

		try {
			int us = Integer.valueOf(txtIdleTimeSpan.getText());
			if (us < 1000) {
				JOptionPane.showMessageDialog(this, resources.getString("Error_txtIdleTimeSpan"), "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, resources.getString("Error_txtIdleTimeSpan") + " "
					+ ex.getClass().getName() + " " + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// Write Settings in PLCcomCoreExSettings.xml
			Properties p = new Properties();
			p.setProperty("user", txtUser.getText());
			p.setProperty("serial", txtSerial.getText());

			if (cmbConnectionType.getSelectedItem() != null) {
				p.setProperty("TypeOfCommunication", cmbConnectionType.getSelectedItem().toString());
			}
			if (cmbPLCType.getSelectedItem() != null) {
				p.setProperty("ePLCtype", cmbPLCType.getSelectedItem().toString());
			}
			if (cmbBaudrate.getSelectedItem() != null) {
				p.setProperty("eBaudrate", cmbBaudrate.getSelectedItem().toString());
			}
			if (cmbBusspeed.getSelectedItem() != null) {
				p.setProperty("eSpeed", cmbBusspeed.getSelectedItem().toString());
			}

			p.setProperty("Adress0", txtAdress0.getText());
			p.setProperty("Adress1", txtAdress1.getText());
			p.setProperty("Adress2", txtAdress2.getText());
			p.setProperty("Adress3", txtAdress3.getText());
			p.setProperty("Adress4", txtAdress4.getText());

			if (!txtProtectionUser.getText().isEmpty())
				p.setProperty("ProtectionUser", txtProtectionUser.getText());

			String pw = new String(txtProtectionPassword.getPassword());
			if (!pw.trim().isEmpty()) {
				String encryptedPassword = AesEncryption.encryptString(pw);
				p.setProperty("ProtectionPassword", encryptedPassword);
			}

			p.setProperty("txtIdeleTimeSpan", txtIdleTimeSpan.getText());
			p.setProperty("AutoConnect", String.valueOf(chkAutoConnect.isSelected()));
			p.setProperty("chkAsyncConnect", String.valueOf(chkAsyncConnect.isSelected()));

			FileOutputStream fos = new FileOutputStream(new File("PlccomExSettings.xml").getAbsolutePath());
			p.storeToXML(fos, "PLCcom Example Settings", "UTF8");
			fos.close();
			JOptionPane.showMessageDialog(this,
					resources.getString("successfully_saved") + System.getProperty("line.separator") + "File: "
							+ new File("PLCcomModbusCoreExSettings.xml").getAbsolutePath(),
					"", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			btnEditConnectionSettings.setEnabled(true);
			panAccess.setEnabled(chkAutoConnect.isSelected());
			panConnectionSettings.setEnabled(false);
			panConnection.setEnabled(!chkAutoConnect.isSelected());
			panSerial.setEnabled(false);
			SetupDevice();
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void btnConnect_actionPerformed(ActionEvent e) {

		SetupDevice();

		// Special settings for Tls13Device project import settings
		if (_device instanceof SymbolicDevice) {

			// set progressbar for showing progress of project import
			txtProgbarStatus.setText(resources.getString("project_import_starts"));
			txtProgbarStatus.setVisible(true);
			prgProjectImport.setValue(0);
			prgProjectImport.setVisible(true);

			// Register event for showing project import progress
			((SymbolicDevice) _device).addOnProjectImportProgressChangedListener(this);
		}

		// Example asynchronous/ synchronous Connect
		if (chkAsyncConnect.isSelected()) {
			// asynchronous call
			_device.beginConnect();
		} else {
			// synchronous call (standard)
			ConnectResult res = _device.connect();
			if (!res.getQuality().equals(OperationResult.eQuality.GOOD)) {

				String message = resources.getString("connect_unsuccessful") + System.getProperty("line.separator")
						+ resources.getString("MessageText") + res.getMessage();

				if (res.getInnerException() != null)
					message += System.getProperty("line.separator") + "InnerException: "
							+ res.getInnerException().getMessage();

				JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
			}

			if (_device instanceof SymbolicDevice) {
				// DeRegister event for showing project import progress
				((SymbolicDevice) _device).removeOnProjectImportProgressChangedListener(this);
				txtProgbarStatus.setVisible(false);
				prgProjectImport.setVisible(false);
			}
		}
	}

	private void btnDisconnect_actionPerformed(ActionEvent e) {
		_device.disConnect();
	}

	private void cmbConnectionType_actionPerformed(ActionEvent e) {
		try {
			switch ((eTypeOfCommunication) this.cmbConnectionType.getSelectedItem()) {
			case TCP:
				this.lblAdress0.setText("IP");
				this.txtAdress0.setText("");
				this.cmbBaudrate.setVisible(false);
				this.cmbBusspeed.setVisible(false);
				this.lblBaudrate.setVisible(false);
				this.lblBusSpeed.setVisible(false);
				if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Logo0BA7_compatibel) {
					this.lblAdress3.setText(resources.getString("Serviceport_Text"));
					this.lblAdress4.setText(resources.getString("Local_Serviceport_Text"));
					this.txtAdress3.setText("10001");
					this.txtAdress4.setText("0");
				} else if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Logo0BA8_compatibel
						|| (ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Logo0BA0_compatibel
						|| (ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Logo0BA1_compatibel
						|| (ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Logo0BA2_compatibel) {
					this.lblAdress3.setText(resources.getString("Serviceport_Text"));
					this.lblAdress4.setText(resources.getString("Local_Serviceport_Text"));
					this.txtAdress3.setText("10005");
					this.txtAdress4.setText("0");
				} else if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Tls13
						|| (ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Legacy) {
					this.lblAdress3.setText(resources.getString("lblRack_Text"));
					this.lblAdress4.setText(resources.getString("lblSlot_Text"));
					this.txtAdress3.setText("");
					this.txtAdress4.setText("");
				} else {
					this.lblAdress3.setText(resources.getString("lblRack_Text"));
					this.lblAdress4.setText(resources.getString("lblSlot_Text"));
					this.txtAdress3.setText("0");
					this.txtAdress4.setText("2");
				}
				break;
			case MPI:
			case PPI:
				this.lblAdress0.setText("ComPort");
				this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
				this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));

				String[] allSerialPorts = SerialPortList.getPortNames();

				if (allSerialPorts.length > 0) {
					this.txtAdress0.setText(allSerialPorts[0]);
				} else {
					this.txtAdress0.setText("No serial ports detected");
				}
				this.txtAdress3.setText("0");
				this.txtAdress4.setText("2");
				this.cmbBaudrate.setVisible(true);
				this.cmbBusspeed.setVisible(true);
				this.lblBaudrate.setVisible(true);
				this.lblBusSpeed.setVisible(true);
				break;
			default:

				break;
			}

			switch ((eTypeOfCommunication) this.cmbConnectionType.getSelectedItem()) {
			case TCP:
				this.cmbBaudrate.setEnabled(false);
				this.cmbBusspeed.setEnabled(false);
				this.txtAdress2.setText("0");
				this.txtAdress1.setText("102");
				if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Tls13) {
					this.txtAdress2.setEnabled(false);
					this.txtAdress1.setEnabled(false);
				} else {
					this.txtAdress2.setEnabled(true);
					this.txtAdress1.setEnabled(true);
				}
				break;
			case MPI:
				this.cmbBusspeed.setSelectedItem(eSpeed.Speed187k.toString());
				this.cmbBaudrate.setSelectedItem(eBaudrate.b38400.toString());
				this.cmbBaudrate.setEnabled(true);
				this.cmbBusspeed.setEnabled(true);
				this.txtAdress2.setEnabled(false);
				this.txtAdress1.setEnabled(false);
				break;
			case PPI:
				this.cmbBaudrate.setSelectedItem(eBaudrate.b9600.toString());
				this.cmbBaudrate.setEnabled(true);
				this.cmbBusspeed.setEnabled(false);
				this.txtAdress2.setEnabled(false);
				this.txtAdress1.setEnabled(false);
				break;
			default:
				JOptionPane.showMessageDialog(this, resources.getString("undefinend_Connectiontype"), "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	private void cmbPLCType_actionPerformed(ActionEvent e) {
		try {
			if (cmbPLCType.getSelectedItem().toString().toUpperCase().equals("LOGO_COMPATIBEL")) {
				JOptionPane.showMessageDialog(this, resources.getString("logo_compatibel_is_obsolete"), "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			switch ((ePLCType) cmbPLCType.getSelectedItem()) {
			case S7_200_compatibel:
				if (cmbConnectionType.getSelectedItem().equals(null)
						|| ((eTypeOfCommunication) cmbConnectionType.getSelectedItem())
								.equals(eTypeOfCommunication.TCP)) {
					this.lblAdress3.setText(resources.getString("lblRack_Text"));
					this.lblAdress4.setText(resources.getString("lblSlot_Text"));
					this.txtAdress3.setText("0");
					this.txtAdress4.setText("0");
				} else {
					this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
					this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));
					this.txtAdress3.setText("0");
					this.txtAdress4.setText("2");
				}

				this.lblAdress1.setVisible(true);
				this.lblAdress2.setVisible(true);
				this.lblAdress3.setVisible(true);
				this.lblAdress4.setVisible(true);
				this.txtAdress1.setVisible(true);
				this.txtAdress2.setVisible(true);
				this.txtAdress3.setVisible(true);
				this.txtAdress4.setVisible(true);

				this.lblProtectionUser.setVisible(false);
				this.lblProtectionPassword.setVisible(false);
				this.txtProtectionUser.setVisible(false);
				this.txtProtectionPassword.setVisible(false);

				this.txtIdleTimeSpan.setEnabled(true);
				this.chkAutoConnect.setEnabled(true);
				break;
			case S7_300_400_compatibel:
			case WinAC_RTX_2010_compatibel:
				if (cmbConnectionType.getSelectedItem().equals(null)
						|| ((eTypeOfCommunication) cmbConnectionType.getSelectedItem())
								.equals(eTypeOfCommunication.TCP)) {
					this.lblAdress3.setText(resources.getString("lblRack_Text"));
					this.lblAdress4.setText(resources.getString("lblSlot_Text"));
				} else {
					this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
					this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));
				}
				this.txtAdress3.setText("0");
				this.txtAdress4.setText("2");

				this.lblAdress1.setVisible(true);
				this.lblAdress2.setVisible(true);
				this.lblAdress3.setVisible(true);
				this.lblAdress4.setVisible(true);
				this.txtAdress1.setVisible(true);
				this.txtAdress2.setVisible(true);
				this.txtAdress3.setVisible(true);
				this.txtAdress4.setVisible(true);

				this.lblProtectionUser.setVisible(false);
				this.lblProtectionPassword.setVisible(false);
				this.txtProtectionUser.setVisible(false);
				this.txtProtectionPassword.setVisible(false);

				this.txtIdleTimeSpan.setEnabled(true);
				this.chkAutoConnect.setEnabled(true);
				break;
			case S7_1200_compatibel:
			case S7_1500_compatibel:
				if (cmbConnectionType.getSelectedItem().equals(null)
						|| ((eTypeOfCommunication) cmbConnectionType.getSelectedItem())
								.equals(eTypeOfCommunication.TCP)) {
					this.lblAdress3.setText(resources.getString("lblRack_Text"));
					this.lblAdress4.setText(resources.getString("lblSlot_Text"));
				} else {
					this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
					this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));
				}
				this.txtAdress3.setText("0");
				this.txtAdress4.setText("0");

				this.lblAdress1.setVisible(true);
				this.lblAdress2.setVisible(true);
				this.lblAdress3.setVisible(true);
				this.lblAdress4.setVisible(true);
				this.txtAdress1.setVisible(true);
				this.txtAdress2.setVisible(true);
				this.txtAdress3.setVisible(true);
				this.txtAdress4.setVisible(true);

				this.lblProtectionUser.setVisible(false);
				this.lblProtectionPassword.setVisible(false);
				this.txtProtectionUser.setVisible(false);
				this.txtProtectionPassword.setVisible(false);

				this.txtIdleTimeSpan.setEnabled(true);
				this.chkAutoConnect.setEnabled(true);
				break;
			case Logo0BA7_compatibel:
				if (cmbConnectionType.getSelectedItem().equals(null)
						|| ((eTypeOfCommunication) cmbConnectionType.getSelectedItem())
								.equals(eTypeOfCommunication.TCP)) {
					this.lblAdress3.setText(resources.getString("Serviceport_Text"));
					this.lblAdress4.setText(resources.getString("Local_Serviceport_Text"));
					this.txtAdress3.setText("10001");
					this.txtAdress4.setText("0");
				} else {
					this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
					this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));
					this.txtAdress3.setText("0");
					this.txtAdress4.setText("0");
				}

				this.lblAdress1.setVisible(true);
				this.lblAdress2.setVisible(true);
				this.lblAdress3.setVisible(true);
				this.lblAdress4.setVisible(true);
				this.txtAdress1.setVisible(true);
				this.txtAdress2.setVisible(true);
				this.txtAdress3.setVisible(true);
				this.txtAdress4.setVisible(true);

				this.lblProtectionUser.setVisible(false);
				this.lblProtectionPassword.setVisible(false);
				this.txtProtectionUser.setVisible(false);
				this.txtProtectionPassword.setVisible(false);

				this.txtIdleTimeSpan.setEnabled(true);
				this.chkAutoConnect.setEnabled(true);
				break;
			case Logo0BA8_compatibel:
			case Logo0BA0_compatibel:
			case Logo0BA1_compatibel:
			case Logo0BA2_compatibel:
				if (cmbConnectionType.getSelectedItem().equals(null)
						|| ((eTypeOfCommunication) cmbConnectionType.getSelectedItem())
								.equals(eTypeOfCommunication.TCP)) {
					this.lblAdress3.setText(resources.getString("Serviceport_Text"));
					this.lblAdress4.setText(resources.getString("Local_Serviceport_Text"));
					this.txtAdress3.setText("10005");
					this.txtAdress4.setText("0");
				} else {
					this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
					this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));
					this.txtAdress3.setText("0");
					this.txtAdress4.setText("0");
				}

				this.lblAdress1.setVisible(true);
				this.lblAdress2.setVisible(true);
				this.lblAdress3.setVisible(true);
				this.lblAdress4.setVisible(true);
				this.txtAdress1.setVisible(true);
				this.txtAdress2.setVisible(true);
				this.txtAdress3.setVisible(true);
				this.txtAdress4.setVisible(true);

				this.lblProtectionUser.setVisible(false);
				this.lblProtectionPassword.setVisible(false);
				this.txtProtectionUser.setVisible(false);
				this.txtProtectionPassword.setVisible(false);

				this.txtIdleTimeSpan.setEnabled(true);
				this.chkAutoConnect.setEnabled(true);
				break;
			case Symbolic_Tls13:
			case Symbolic_Legacy:
				this.lblAdress3.setText(resources.getString("lblRack_Text"));
				this.lblAdress4.setText(resources.getString("lblSlot_Text"));
				this.txtAdress3.setText("");
				this.txtAdress4.setText("");

				this.lblAdress1.setVisible(false);
				this.lblAdress2.setVisible(false);
				this.lblAdress3.setVisible(false);
				this.lblAdress4.setVisible(false);
				this.txtAdress1.setVisible(false);
				this.txtAdress2.setVisible(false);
				this.txtAdress3.setVisible(false);
				this.txtAdress4.setVisible(false);

				this.lblProtectionUser.setVisible(true);
				this.lblProtectionPassword.setVisible(true);
				this.txtProtectionUser.setVisible(true);
				this.txtProtectionPassword.setVisible(true);

				if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Legacy) {
					this.txtProtectionUser.setText("");
					this.txtProtectionUser.setEnabled(false);
				} else
					this.txtProtectionUser.setEnabled(true);

				this.txtProtectionPassword.setEnabled(true);

				this.txtIdleTimeSpan.setEnabled(false);
				this.chkAutoConnect.setSelected(false);
				this.chkAutoConnect.setEnabled(false);
				break;
			case Other:
				if (cmbConnectionType.getSelectedItem().equals(null)
						|| ((eTypeOfCommunication) cmbConnectionType.getSelectedItem())
								.equals(eTypeOfCommunication.TCP)) {
					this.lblAdress3.setText(resources.getString("lblRack_Text"));
					this.lblAdress4.setText(resources.getString("lblSlot_Text"));
				} else {
					this.lblAdress3.setText(resources.getString("lblBusAdressLocal_Text"));
					this.lblAdress4.setText(resources.getString("lblBusAdressPLC_Text"));
				}
				this.txtAdress4.setText("");
				this.txtAdress3.setText("");

				this.lblAdress1.setVisible(true);
				this.lblAdress2.setVisible(true);
				this.lblAdress3.setVisible(true);
				this.lblAdress4.setVisible(true);
				this.txtAdress1.setVisible(true);
				this.txtAdress2.setVisible(true);
				this.txtAdress3.setVisible(true);
				this.txtAdress4.setVisible(true);

				this.lblProtectionUser.setVisible(false);
				this.lblProtectionPassword.setVisible(false);
				this.txtProtectionUser.setVisible(false);
				this.txtProtectionPassword.setVisible(false);

				this.txtIdleTimeSpan.setEnabled(true);
				this.chkAutoConnect.setEnabled(true);
				break;
			default:
				JOptionPane.showMessageDialog(this, resources.getString("undefinend_PLCType"), "",
						JOptionPane.ERROR_MESSAGE);
				break;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void chkAutoConnect_itemStateChanged(ItemEvent e) {
		txtIdleTimeSpan.setEnabled(chkAutoConnect.isSelected());
		SetupDevice();
	}

	private void SetupDevice() {
		authentication.setSerial(txtSerial.getText());
		authentication.setUser(txtUser.getText());
		try {
			if (_device.IsConnected())
				_device.disConnect();
			switch ((eTypeOfCommunication) cmbConnectionType.getSelectedItem()) {
			case TCP:
				if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Tls13) {
					_device = new Tls13Device(txtAdress0.getText(), txtProtectionUser.getText(),
							new String(txtProtectionPassword.getPassword()));
				} else if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Legacy) {
					_device = new LegacySymbolicDevice(txtAdress0.getText(),
							new String(txtProtectionPassword.getPassword()));
				} else {
					_device = new TCP_ISO_Device(txtAdress0.getText(), Integer.valueOf(txtAdress3.getText()),
							Integer.valueOf(txtAdress4.getText()), (ePLCType) cmbPLCType.getSelectedItem());
					_device.setTCP_Port_PLC(Integer.valueOf(txtAdress1.getText()));
					_device.setTCP_Port_Local(Integer.valueOf(txtAdress2.getText()));
					if (((ePLCType) cmbPLCType.getSelectedItem()).equals(ePLCType.Logo0BA7_compatibel)
							|| ((ePLCType) cmbPLCType.getSelectedItem()).equals(ePLCType.Logo0BA8_compatibel)
							|| ((ePLCType) cmbPLCType.getSelectedItem()).equals(ePLCType.Logo0BA0_compatibel)
							|| ((ePLCType) cmbPLCType.getSelectedItem()).equals(ePLCType.Logo0BA1_compatibel)
							|| ((ePLCType) cmbPLCType.getSelectedItem()).equals(ePLCType.Logo0BA2_compatibel)) {
						((TCP_ISO_Device) _device).setTCP_LOGO_ServicePort_PLC(Integer.valueOf(txtAdress3.getText()));
						((TCP_ISO_Device) _device).setTCP_LOGO_ServicePort_Local(Integer.valueOf(txtAdress4.getText()));
					}
				}
				break;
			case MPI:
				_device = new MPI_Device(txtAdress0.getText(), Integer.valueOf(txtAdress3.getText()),
						Integer.valueOf(txtAdress4.getText()), (eBaudrate) cmbBaudrate.getSelectedItem(),
						(eSpeed) cmbBusspeed.getSelectedItem(), (ePLCType) cmbPLCType.getSelectedItem());
				break;
			case PPI:
				_device = new PPI_Device(txtAdress0.getText(), Integer.valueOf(txtAdress3.getText()),
						Integer.valueOf(txtAdress4.getText()), (eBaudrate) cmbBaudrate.getSelectedItem(),
						ePLCType.S7_200_compatibel);
				break;
			default:
				JOptionPane.showMessageDialog(this, resources.getString("undefinend_Connectiontype"), "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// register Connection state change event
		_device.StateChangeNotifier = new ConnectionStateChangeNotifier(this);

		// register connect callback event, occurs when option 'asynchronous
		// connect' is activated
		_device.ConnectCBNotifier = new ConnectCallbackNotifier(this);

		txtProgbarStatus.setText("Device Type: " + _device.getClass().getName());

		// Set Auto Connect State
		if ((ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Tls13
				|| (ePLCType) cmbPLCType.getSelectedItem() == ePLCType.Symbolic_Legacy)
			_device.setAutoConnect(chkAutoConnect.isSelected());
		else
			_device.setAutoConnect(chkAutoConnect.isSelected(), Integer.valueOf(txtIdleTimeSpan.getText()));

	}

	private void txtUser_TextChanged(DocumentEvent e) {
		authentication.setUser(txtUser.getText());
	}

	private void txtSerial_TextChanged(DocumentEvent e) {
		authentication.setSerial(txtSerial.getText());
	}

	private void btnEditConnectionSettings_actionPerformed(ActionEvent e) {
		try {
			if (CountOpenDialogs > 0) {
				JOptionPane.showMessageDialog(this, resources.getString("to_many_windows"), "",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			_device.disConnect();
			btnEditConnectionSettings.setEnabled(false);
			panAccess.setEnabled(false);
			panConnectionSettings.setEnabled(true);
			panConnection.setEnabled(false);
			panSerial.setEnabled(true);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnReadWriteFunctions_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		ReadWriteBox rwb = new ReadWriteBox((PLCcomDevice) _device, resources);
		rwb.setVisible(true);
	}

	private void btnReadCollection_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		OptimizedReadWriteBox rcb = new OptimizedReadWriteBox((PLCcomDevice) _device, resources);
		rcb.setVisible(true);
	}

	private void btnBlockFunctions_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		BlockFunctions bf = new BlockFunctions((PLCcomDevice) _device, resources);
		bf.setVisible(true);
	}

	private void btnOtherFunctions_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		OtherFunctions of = new OtherFunctions((PLCcomCoreDevice) _device, resources);
		of.setVisible(true);
	}

	private void btnDataServer_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		DataServerFunctions dsf = new DataServerFunctions((PLCcomDevice) _device, resources);
		dsf.setVisible(true);
	}

	private void btnReadWriteSymbolic_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;

		if (_device instanceof Tls13Device) {
			ReadWriteSymbolic rws = new ReadWriteSymbolic((Tls13Device) _device, resources);
			rws.setVisible(true);
		}
		else if (_device instanceof LegacySymbolicDevice) {
			ReadWriteSymbolic rws = new ReadWriteSymbolic((LegacySymbolicDevice) _device, resources);
			rws.setVisible(true);
		}
	}

	protected void btnAlarmMessages_actionPerformed(ActionEvent e) {
		CountOpenDialogs++;
		AlarmFunctions alf = new AlarmFunctions((PLCcomCoreDevice) _device, resources);
		alf.setVisible(true);
	}


	public void btnPlcTypeHelp_actionPerformed(ActionEvent e) {

		String caption = resources.getString("captionPlcTypeHelp");
		String message = resources.getString("messagePlcTypeHelp");
		JOptionPane.showMessageDialog(this, message, caption, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void onProjectImportProgressChanged(int progress) {
		if (SwingUtilities.isEventDispatchThread()) {
			txtProgbarStatus.setText("Project Import " + progress + "%");
			prgProjectImport.setValue(progress);
		} else {
			SwingUtilities.invokeLater(() -> {
				txtProgbarStatus.setText("Project Import " + progress + "%");
				prgProjectImport.setValue(progress);
			});
		}
	}
}
