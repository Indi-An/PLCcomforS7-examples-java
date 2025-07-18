package example_app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.indian.plccom.fors7.DataBaseConnector;
import com.indian.plccom.fors7.FileSystemConnector;
import com.indian.plccom.fors7.LoggingConnector;
import com.indian.plccom.fors7.PLCComDataServer;
import com.indian.plccom.fors7.eImageOutputFormat;

import example_app.DisabledJPanel.DisabledJPanel;

public class DataServerLoggingSettings extends JDialog {

	private static final long serialVersionUID = 1L;
	private ResourceBundle resources;
	private PLCComDataServer myDataServer;

	private JTable lvConnectors;
	private JTextField txtConnectorNameDB;
	private JTextField txtConnectorName;
	private JTextField txtEncryptionPassword;
	private JTextField txtMaxNumberOfLogFiles;
	private JTextField txtMaxAgeHours;
	private JTextField txtMaxFileSizeMB;
	private JComboBox<eTypeOfConnector> cmbConnectorType;
	private JLabel lblConnectorType;
	private JPanel grbNewConnector;
	private DisabledJPanel panNewConnector;
	private JButton btnAccept;
	private JButton btnReject;
	private JTextPane lblInfoConnectorNameDB;
	private JTextPane lblInfoIsWriteLogActiveDB;
	private JTextPane lblConnectionMessage;
	private JTextPane lblInfoIsWriteImageActiveDB;
	private JComboBox<eImageOutputFormat> cmbImageOutputFormat;
	private JLabel lblEncryptionPassword;
	private JLabel lblMaxNumberOfLogFiles;
	private JLabel lblMaxAgeHours;
	private JLabel lblMaxFileSizeMB;
	private JLabel lblImageOutputFormat;
	private JPanel grbDatabaseConnectorSettings;
	private JPanel grbFilesystemConnectorSettings;
	private JLabel lblConnectorNameDB;
	private JLabel lblConnectionString;
	private JLabel lblIsWriteLogActiveDB;
	private JLabel lblIsWriteImageActiveDB;
	private JCheckBox chkIsWriteImageActiveDB;
	private JCheckBox chkIsWriteLogActiveDB;
	private JTextArea txtSQLConnectionString;
	private JPanel txtInfo;
	private JButton btnClose;
	private JButton btnRemoveConnector;
	private JButton btnAddConnector;
	private JTextPane txtInfoCreateListener;
	private JLabel lblConnectorName;
	private JLabel lblFolderName;
	private JLabel lblIsWriteLogActive;
	private JLabel lblIsWriteImageActive;
	private JCheckBox chkIsWriteImageActive;
	private JCheckBox chkIsWriteLogActive;
	private JPanel panel_2;
	private JTextPane lblInfoConnectorName;
	private JTextPane lblInfoIsWriteLogActive;
	private JTextPane lblInfoIsWriteImageActive;
	private JTextPane lblInfoEncryptionPassword;
	private JTextPane lblInfoMaxNumberOfLogFiles;
	private JTextPane lblInfoMaxAgeHours;
	private JTextPane lblInfoMaxFileSizeMB;
	private JTextPane lblInfoImageOutputFormat;
	private JButton btnFolderName;
	private JTextArea txtFolderName;
	private JButton btnAllInstalljdbcDriver;
	private JTextPane txtInfoDocu;

	private enum eTypeOfConnector {
		Filesystem_Connector, Database_Connector
	}

	/**
	 * Create the dialog.
	 */
	public DataServerLoggingSettings(ResourceBundle rb) {
		this.resources = rb;
		initialize();
	}

	private void initialize() {

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

		setTitle("create new logging connector");
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(DataServerLoggingSettings.class.getResource("/example_app/pencil2.png")));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModal(true);
		setBounds(10, 10, 828, 787);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				formWindowOpened(e);
			}

		});
		getContentPane().setLayout(null);

		JLabel lblLogo = new JLabel();
		lblLogo.setVerticalAlignment(SwingConstants.TOP);
		lblLogo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLogo.setBounds(16, 4, 130, 60);
		ImageIcon originalIcon = new ImageIcon(
				Main.class.getResource("/example_app/indi.logo2021.1_rgb_PLCcom_130_60.png"));
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(scaledImage));
		getContentPane().add(lblLogo);

		JPanel panel = new JPanel();
		panel.setBounds(187, 5, 600, 70);
		panel.setLayout(null);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(SystemColor.info);
		getContentPane().add(panel);

		txtInfoCreateListener = new JTextPane();
		txtInfoCreateListener.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoCreateListener.setText(
				"\u201ALogging-Connectors\u2018 store read variable data to  the file system or a SQL database for further use.\r\nDue to not included database provider this example just shows filesystem and SQL-Server connections.");
		txtInfoCreateListener.setEditable(false);
		txtInfoCreateListener.setBorder(null);
		txtInfoCreateListener.setBackground(SystemColor.info);
		txtInfoCreateListener.setBounds(66, 2, 524, 62);
		panel.add(txtInfoCreateListener);

		JLabel label_1 = new JLabel();
		label_1.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/pictureBox1.Image.png")));
		label_1.setVerticalAlignment(SwingConstants.TOP);
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setBounds(2, 2, 32, 32);
		panel.add(label_1);

		grbNewConnector = new JPanel();
		grbNewConnector.setLayout(null);
		grbNewConnector.setBounds(2, 242, 805, 422);
		grbNewConnector.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(grbNewConnector);

		panNewConnector = new DisabledJPanel(grbNewConnector);
		panNewConnector.setBounds(grbNewConnector.getBounds());
		panNewConnector.setDisabledColor(new Color(240, 240, 240, 100));
		panNewConnector.setEnabled(false);
		getContentPane().add(panNewConnector);

		grbFilesystemConnectorSettings = new JPanel();
		grbFilesystemConnectorSettings.setLayout(null);
		grbFilesystemConnectorSettings.setBounds(7, 43, 797, 298);
		grbFilesystemConnectorSettings.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Filesystem connector settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grbNewConnector.add(grbFilesystemConnectorSettings);

		lblConnectorName = new JLabel("new connector name");
		lblConnectorName.setBounds(1, 64, 124, 14);
		lblConnectorName.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblConnectorName);

		lblIsWriteLogActive = new JLabel("isWriteLogActive");
		lblIsWriteLogActive.setBounds(1, 92, 124, 14);
		lblIsWriteLogActive.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblIsWriteLogActive);

		lblIsWriteImageActive = new JLabel("isWriteImageActive");
		lblIsWriteImageActive.setBounds(1, 115, 124, 14);
		lblIsWriteImageActive.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblIsWriteImageActive);

		chkIsWriteImageActive = new JCheckBox("");
		chkIsWriteImageActive.setBounds(129, 111, 25, 23);
		chkIsWriteImageActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chkIsWriteImageActive_actionPerformed(e);
			}
		});
		chkIsWriteImageActive.setSelected(true);
		grbFilesystemConnectorSettings.add(chkIsWriteImageActive);

		chkIsWriteLogActive = new JCheckBox("");
		chkIsWriteLogActive.setBounds(129, 89, 25, 23);
		chkIsWriteLogActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chkIsWriteLogActive_actionPerformed(e);
			}
		});
		chkIsWriteLogActive.setSelected(true);
		grbFilesystemConnectorSettings.add(chkIsWriteLogActive);

		txtConnectorName = new JTextField();
		txtConnectorName.setBounds(133, 61, 163, 20);
		txtConnectorName.setColumns(10);
		grbFilesystemConnectorSettings.add(txtConnectorName);

		panel_2 = new JPanel();
		panel_2.setBounds(309, 56, 480, 235);
		panel_2.setLayout(null);
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBackground(SystemColor.info);
		grbFilesystemConnectorSettings.add(panel_2);

		lblInfoConnectorName = new JTextPane();
		lblInfoConnectorName.setText("<= The desired name of new connector. Name must be unique");
		lblInfoConnectorName.setEditable(false);
		lblInfoConnectorName.setBorder(null);
		lblInfoConnectorName.setBackground(SystemColor.info);
		lblInfoConnectorName.setBounds(3, 5, 462, 18);
		panel_2.add(lblInfoConnectorName);

		lblInfoIsWriteLogActive = new JTextPane();
		lblInfoIsWriteLogActive.setText("<= Turns data output in a log archive on or off");
		lblInfoIsWriteLogActive.setEditable(false);
		lblInfoIsWriteLogActive.setBorder(null);
		lblInfoIsWriteLogActive.setBackground(SystemColor.info);
		lblInfoIsWriteLogActive.setBounds(3, 34, 462, 18);
		panel_2.add(lblInfoIsWriteLogActive);

		lblInfoIsWriteImageActive = new JTextPane();
		lblInfoIsWriteImageActive.setText(
				"<= Allows you to write the current server data image in the file system for your further use");
		lblInfoIsWriteImageActive.setEditable(false);
		lblInfoIsWriteImageActive.setBorder(null);
		lblInfoIsWriteImageActive.setBackground(SystemColor.info);
		lblInfoIsWriteImageActive.setBounds(3, 53, 462, 28);
		panel_2.add(lblInfoIsWriteImageActive);

		lblInfoEncryptionPassword = new JTextPane();
		lblInfoEncryptionPassword.setText(
				"<= If you enter an encryption password, the data is stored in encrypted form. \r\n     You can read the data using the supplied decryption tool again.");
		lblInfoEncryptionPassword.setEditable(false);
		lblInfoEncryptionPassword.setBorder(null);
		lblInfoEncryptionPassword.setBackground(SystemColor.info);
		lblInfoEncryptionPassword.setBounds(3, 82, 462, 28);
		panel_2.add(lblInfoEncryptionPassword);

		lblInfoMaxNumberOfLogFiles = new JTextPane();
		lblInfoMaxNumberOfLogFiles.setText(
				"<= You can restrict the maximum number of files. \r\n     When the value is exceeded the old files are automatically deleted. -1 = Disabled.");
		lblInfoMaxNumberOfLogFiles.setEditable(false);
		lblInfoMaxNumberOfLogFiles.setBorder(null);
		lblInfoMaxNumberOfLogFiles.setBackground(SystemColor.info);
		lblInfoMaxNumberOfLogFiles.setBounds(3, 114, 462, 29);
		panel_2.add(lblInfoMaxNumberOfLogFiles);

		lblInfoMaxAgeHours = new JTextPane();
		lblInfoMaxAgeHours.setText("<= The desired name of new connector. Name must be unique");
		lblInfoMaxAgeHours.setEditable(false);
		lblInfoMaxAgeHours.setBorder(null);
		lblInfoMaxAgeHours.setBackground(SystemColor.info);
		lblInfoMaxAgeHours.setBounds(3, 147, 462, 28);
		panel_2.add(lblInfoMaxAgeHours);

		lblInfoMaxFileSizeMB = new JTextPane();
		lblInfoMaxFileSizeMB.setText(
				"<= You can restrict the maximum size of files. \r\n     When the value is exceeded the old files are automatically deleted. -1 = Disabled.");
		lblInfoMaxFileSizeMB.setEditable(false);
		lblInfoMaxFileSizeMB.setBorder(null);
		lblInfoMaxFileSizeMB.setBackground(SystemColor.info);
		lblInfoMaxFileSizeMB.setBounds(3, 176, 462, 30);
		panel_2.add(lblInfoMaxFileSizeMB);

		lblInfoImageOutputFormat = new JTextPane();
		lblInfoImageOutputFormat
				.setText("<= You can output the data for the image in shallow .dat format (csv) or in .xml format");
		lblInfoImageOutputFormat.setEditable(false);
		lblInfoImageOutputFormat.setBorder(null);
		lblInfoImageOutputFormat.setBackground(SystemColor.info);
		lblInfoImageOutputFormat.setBounds(3, 213, 462, 21);
		panel_2.add(lblInfoImageOutputFormat);

		txtEncryptionPassword = new JTextField();
		txtEncryptionPassword.setBounds(133, 138, 163, 20);
		txtEncryptionPassword.setColumns(10);
		grbFilesystemConnectorSettings.add(txtEncryptionPassword);

		txtMaxNumberOfLogFiles = new JTextField();
		txtMaxNumberOfLogFiles.setText("-1");
		txtMaxNumberOfLogFiles.setBounds(133, 170, 163, 20);
		txtMaxNumberOfLogFiles.setColumns(10);
		grbFilesystemConnectorSettings.add(txtMaxNumberOfLogFiles);

		txtMaxAgeHours = new JTextField();
		txtMaxAgeHours.setText("-1");
		txtMaxAgeHours.setBounds(133, 200, 163, 20);
		txtMaxAgeHours.setColumns(10);
		grbFilesystemConnectorSettings.add(txtMaxAgeHours);

		txtMaxFileSizeMB = new JTextField();
		txtMaxFileSizeMB.setText("-1");
		txtMaxFileSizeMB.setBounds(133, 234, 163, 20);
		txtMaxFileSizeMB.setColumns(10);
		grbFilesystemConnectorSettings.add(txtMaxFileSizeMB);

		cmbImageOutputFormat = new JComboBox<eImageOutputFormat>();
		cmbImageOutputFormat.setBounds(134, 265, 163, 21);
		grbFilesystemConnectorSettings.add(cmbImageOutputFormat);

		lblEncryptionPassword = new JLabel("<html>encryption password<br>(empty = no encrypting)</html>");
		lblEncryptionPassword.setBounds(1, 134, 124, 34);
		lblEncryptionPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblEncryptionPassword);

		lblMaxNumberOfLogFiles = new JLabel("max number of log files");
		lblMaxNumberOfLogFiles.setBounds(1, 173, 124, 14);
		lblMaxNumberOfLogFiles.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblMaxNumberOfLogFiles);

		lblMaxAgeHours = new JLabel("max age in hours");
		lblMaxAgeHours.setBounds(1, 203, 124, 14);
		lblMaxAgeHours.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblMaxAgeHours);

		lblMaxFileSizeMB = new JLabel("max file size in MB");
		lblMaxFileSizeMB.setBounds(1, 237, 124, 14);
		lblMaxFileSizeMB.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblMaxFileSizeMB);

		lblImageOutputFormat = new JLabel("image output format");
		lblImageOutputFormat.setBounds(2, 268, 124, 14);
		lblImageOutputFormat.setHorizontalAlignment(SwingConstants.RIGHT);
		grbFilesystemConnectorSettings.add(lblImageOutputFormat);

		txtFolderName = new JTextArea();
		txtFolderName.setBounds(133, 50, 140, 34);

		JScrollPane scrollPaneltxtFolderName = new JScrollPane(txtFolderName);
		scrollPaneltxtFolderName.setBounds(133, 16, 612, 34);
		grbFilesystemConnectorSettings.add(scrollPaneltxtFolderName);

		btnFolderName = new JButton("...");
		btnFolderName.setBounds(753, 16, 34, 34);
		grbFilesystemConnectorSettings.add(btnFolderName);

		lblFolderName = new JLabel("folder");
		lblFolderName.setBounds(-9, 26, 124, 14);
		grbFilesystemConnectorSettings.add(lblFolderName);
		lblFolderName.setHorizontalAlignment(SwingConstants.RIGHT);
		btnFolderName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnFolderName_actionPerformed(e);
			}
		});

		lblConnectorType = new JLabel("type of connector");
		lblConnectorType.setBounds(7, 19, 116, 14);
		lblConnectorType.setHorizontalAlignment(SwingConstants.RIGHT);
		grbNewConnector.add(lblConnectorType);

		cmbConnectorType = new JComboBox<eTypeOfConnector>();
		cmbConnectorType.setBounds(141, 16, 158, 21);
		cmbConnectorType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cmbConnectorType_itemStateChanged(e);
			}
		});
		grbNewConnector.add(cmbConnectorType);

		btnAccept = new JButton("accept");
		btnAccept.setBounds(647, 343, 68, 68);
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAccept_actionPerformed(e);
			}
		});
		btnAccept.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/btnAccept.Image.png")));
		btnAccept.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAccept.setToolTipText("");
		btnAccept.setMargin(new Insets(0, 0, 0, 0));
		btnAccept.setHorizontalTextPosition(SwingConstants.CENTER);
		grbNewConnector.add(btnAccept);

		btnReject = new JButton("reject");
		btnReject.setBounds(727, 343, 68, 68);
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReject_actionPerformed(e);
			}
		});
		btnReject.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/btnReject.Image.png")));
		btnReject.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReject.setToolTipText("");
		btnReject.setMargin(new Insets(0, 0, 0, 0));
		btnReject.setHorizontalTextPosition(SwingConstants.CENTER);
		grbNewConnector.add(btnReject);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(SystemColor.info);
		panel_1.setBounds(318, 2, 480, 40);
		grbNewConnector.add(panel_1);

		txtInfoDocu = new JTextPane();
		txtInfoDocu.setText(
				"You can find help in the document 'Documentation_PLCcom_dotnet_database_interface_english.pdf'");
		txtInfoDocu.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoDocu.setEditable(false);
		txtInfoDocu.setBorder(null);
		txtInfoDocu.setBackground(SystemColor.info);
		txtInfoDocu.setBounds(44, 2, 426, 32);
		panel_1.add(txtInfoDocu);

		JLabel label_2 = new JLabel();
		label_2.setIcon(new ImageIcon(DataServerLoggingSettings.class.getResource("/example_app/help2.png")));
		label_2.setVerticalAlignment(SwingConstants.TOP);
		label_2.setHorizontalAlignment(SwingConstants.TRAILING);
		label_2.setBounds(2, 2, 32, 32);
		panel_1.add(label_2);

		grbDatabaseConnectorSettings = new JPanel();
		grbDatabaseConnectorSettings.setLayout(null);
		grbDatabaseConnectorSettings.setVisible(false);
		grbDatabaseConnectorSettings.setBounds(7, 43, 797, 298);
		grbDatabaseConnectorSettings.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Database connector settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grbNewConnector.add(grbDatabaseConnectorSettings);

		lblConnectorNameDB = new JLabel("new connector name");
		lblConnectorNameDB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblConnectorNameDB.setBounds(4, 104, 116, 14);
		grbDatabaseConnectorSettings.add(lblConnectorNameDB);

		lblIsWriteLogActiveDB = new JLabel("isWriteLogActive");
		lblIsWriteLogActiveDB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIsWriteLogActiveDB.setBounds(2, 129, 116, 14);
		grbDatabaseConnectorSettings.add(lblIsWriteLogActiveDB);

		lblIsWriteImageActiveDB = new JLabel("isWriteImageActive");
		lblIsWriteImageActiveDB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIsWriteImageActiveDB.setBounds(2, 152, 116, 14);
		grbDatabaseConnectorSettings.add(lblIsWriteImageActiveDB);

		chkIsWriteImageActiveDB = new JCheckBox("");
		chkIsWriteImageActiveDB.setSelected(true);
		chkIsWriteImageActiveDB.setBounds(124, 148, 25, 23);
		grbDatabaseConnectorSettings.add(chkIsWriteImageActiveDB);

		chkIsWriteLogActiveDB = new JCheckBox("");
		chkIsWriteLogActiveDB.setSelected(true);
		chkIsWriteLogActiveDB.setBounds(124, 125, 25, 23);
		grbDatabaseConnectorSettings.add(chkIsWriteLogActiveDB);

		txtConnectorNameDB = new JTextField();
		txtConnectorNameDB.setColumns(10);
		txtConnectorNameDB.setBounds(125, 100, 185, 20);
		grbDatabaseConnectorSettings.add(txtConnectorNameDB);

		txtInfo = new JPanel();
		txtInfo.setLayout(null);
		txtInfo.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtInfo.setBackground(SystemColor.info);
		txtInfo.setBounds(312, 93, 480, 201);
		grbDatabaseConnectorSettings.add(txtInfo);

		lblInfoConnectorNameDB = new JTextPane();
		lblInfoConnectorNameDB.setText("<= The desired name of new connector. Name must be unique");
		lblInfoConnectorNameDB.setEditable(false);
		lblInfoConnectorNameDB.setBorder(null);
		lblInfoConnectorNameDB.setBackground(SystemColor.info);
		lblInfoConnectorNameDB.setBounds(2, 8, 462, 18);
		txtInfo.add(lblInfoConnectorNameDB);

		lblInfoIsWriteLogActiveDB = new JTextPane();
		lblInfoIsWriteLogActiveDB.setText("<= Turns data output in a log archive on or off");
		lblInfoIsWriteLogActiveDB.setEditable(false);
		lblInfoIsWriteLogActiveDB.setBorder(null);
		lblInfoIsWriteLogActiveDB.setBackground(SystemColor.info);
		lblInfoIsWriteLogActiveDB.setBounds(2, 34, 462, 18);
		txtInfo.add(lblInfoIsWriteLogActiveDB);

		lblInfoIsWriteImageActiveDB = new JTextPane();
		lblInfoIsWriteImageActiveDB.setText(
				"<= Allows you to write the current server data image in the file system for your further use");
		lblInfoIsWriteImageActiveDB.setEditable(false);
		lblInfoIsWriteImageActiveDB.setBorder(null);
		lblInfoIsWriteImageActiveDB.setBackground(SystemColor.info);
		lblInfoIsWriteImageActiveDB.setBounds(2, 56, 462, 25);
		txtInfo.add(lblInfoIsWriteImageActiveDB);

		lblConnectionMessage = new JTextPane();
		lblConnectionMessage.setText(
				"com.indian.plccom.fors7 supports all database connections that derive from DbConnection- Object.\r\nPLCCom been tested with:\r\n=> Oracle\r\n=> MS SQL-Server\r\n=> MySQL\r\n=> PostgresSQL\r\n=> Firebird\r\n=> SQLITE");
		lblConnectionMessage.setEditable(false);
		lblConnectionMessage.setBorder(null);
		lblConnectionMessage.setBackground(SystemColor.info);
		lblConnectionMessage.setBounds(47, 86, 419, 113);
		txtInfo.add(lblConnectionMessage);

		JLabel label_4 = new JLabel();
		label_4.setIcon(
				new ImageIcon(DataServerLoggingSettings.class.getResource("/example_app/information.png")));
		label_4.setVerticalAlignment(SwingConstants.TOP);
		label_4.setHorizontalAlignment(SwingConstants.TRAILING);
		label_4.setBounds(8, 86, 32, 32);
		txtInfo.add(label_4);

		txtSQLConnectionString = new JTextArea();
		txtSQLConnectionString.setBounds(126, 21, 556, 42);
		txtSQLConnectionString.setText("jdbc:sqlserver://localhost;databaseName=plccom;integratedSecurity=true;");

		JScrollPane scrollPaneltxtSQLConnectionString = new JScrollPane(txtSQLConnectionString);
		scrollPaneltxtSQLConnectionString.setBounds(new Rectangle(126, 21, 573, 68));
		grbDatabaseConnectorSettings.add(scrollPaneltxtSQLConnectionString, BorderLayout.NORTH);

		lblConnectionString = new JLabel("ConnectionString");
		lblConnectionString.setBounds(-1, 19, 116, 27);
		grbDatabaseConnectorSettings.add(lblConnectionString);
		lblConnectionString.setHorizontalAlignment(SwingConstants.RIGHT);

		btnAllInstalljdbcDriver = new JButton(
				"<html><center>all installed</center><center>JDBC Driver</center></html>");
		btnAllInstalljdbcDriver.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/environment_information.png")));
		btnAllInstalljdbcDriver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnallInstalljdbcDriver_actionPerformed(e);
			}
		});
		btnAllInstalljdbcDriver.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAllInstalljdbcDriver.setToolTipText("");
		btnAllInstalljdbcDriver.setMargin(new Insets(0, 0, 0, 0));
		btnAllInstalljdbcDriver.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAllInstalljdbcDriver.setBounds(716, 20, 68, 68);
		grbDatabaseConnectorSettings.add(btnAllInstalljdbcDriver);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.setBounds(734, 670, 68, 68);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		getContentPane().add(btnClose);

		JPanel grbConnectoren = new JPanel();
		grbConnectoren.setLayout(null);
		grbConnectoren.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbConnectoren.setBounds(2, 76, 805, 157);
		getContentPane().add(grbConnectoren);

		// ############### end init lvListener #####################

		// ############### begin init lvRequests #####################
		lvConnectors = new JTable();
		lvConnectors.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lvConnectors.setShowHorizontalLines(false);
		lvConnectors.setShowVerticalLines(false);
		lvConnectors.setShowGrid(false);
		lvConnectors.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvConnectors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvConnectors.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Name", "Connector" }) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		lvConnectors.getColumnModel().getColumn(0).setResizable(true);
		lvConnectors.getColumnModel().getColumn(0).setPreferredWidth(0);
		lvConnectors.getColumnModel().getColumn(1).setResizable(true);
		lvConnectors.getColumnModel().getColumn(1).setPreferredWidth(2000);

		// lvConnectors.setBounds(new Rectangle(0, 0, 550, 140));

		lvConnectors.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lvConnectors_SelectedIndexChanged(e);
			}
		});

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvConnectors.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.LEFT);

		lvConnectors.setFillsViewportHeight(true);
		JPanel lvListenerContainer = new JPanel();
		lvListenerContainer.setBounds(103, 11, 692, 140);
		grbConnectoren.add(lvListenerContainer);
		JScrollPane scrollPanelvListener = new JScrollPane(lvConnectors);
		lvListenerContainer.setLayout(new BorderLayout());
		lvListenerContainer.add(lvConnectors.getTableHeader(), BorderLayout.PAGE_START);
		lvListenerContainer.add(scrollPanelvListener, BorderLayout.CENTER);

		btnAddConnector = new JButton("<html><center>add</center><center>connector</center></html>");
		btnAddConnector.setBounds(17, 11, 68, 68);
		grbConnectoren.add(btnAddConnector);
		btnAddConnector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAddConnector_actionPerformed(e);
			}
		});
		btnAddConnector.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/btnAddRequest.Image.png")));
		btnAddConnector.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAddConnector.setToolTipText("");
		btnAddConnector.setMargin(new Insets(0, 0, 0, 0));
		btnAddConnector.setHorizontalTextPosition(SwingConstants.CENTER);

		btnRemoveConnector = new JButton("<html><center>remove</center><center>connector</center></html>");
		btnRemoveConnector.setEnabled(false);
		btnRemoveConnector.setBounds(17, 82, 68, 68);
		grbConnectoren.add(btnRemoveConnector);
		btnRemoveConnector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRemoveConnector_actionPerformed(e);
			}
		});
		btnRemoveConnector.setIcon(new ImageIcon(
				DataServerLoggingSettings.class.getResource("/example_app/btnRemoveRequest.Image.png")));
		btnRemoveConnector.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRemoveConnector.setToolTipText("");
		btnRemoveConnector.setMargin(new Insets(0, 0, 0, 0));
		btnRemoveConnector.setHorizontalTextPosition(SwingConstants.CENTER);

	}

	private void formWindowOpened(WindowEvent e) {

		try {
			// init controls
			cmbConnectorType.setModel(new DefaultComboBoxModel<eTypeOfConnector>(eTypeOfConnector.values()));
			cmbImageOutputFormat.setModel(new DefaultComboBoxModel<eImageOutputFormat>(eImageOutputFormat.values()));

			this.txtFolderName.setText(new File("").getAbsolutePath());

			this.btnClose.setText(resources.getString("btnClose_Text"));
			this.btnAccept.setText(resources.getString("btnAccept_Text"));
			this.btnReject.setText(resources.getString("btnReject_Text"));
			this.lblInfoIsWriteLogActiveDB.setText(resources.getString("lblInfoIsWriteLogActiveDB_Text"));
			this.lblInfoIsWriteLogActive.setText(resources.getString("lblInfoIsWriteLogActive_Text"));
			this.lblInfoIsWriteImageActive.setText(resources.getString("lblInfoIsWriteImageActive_Text"));
			this.lblInfoEncryptionPassword.setText(resources.getString("lblInfoEncryptionPassword_Text"));
			this.lblInfoMaxNumberOfLogFiles.setText(resources.getString("lblInfoMaxNumberOfLogFiles_Text"));
			this.lblInfoMaxAgeHours.setText(resources.getString("lblInfoMaxAgeHours_Text"));
			this.lblInfoMaxFileSizeMB.setText(resources.getString("lblInfoMaxFileSizeMB_Text"));
			this.lblInfoImageOutputFormat.setText(resources.getString("lblInfoImageOutputFormat_Text"));
			this.lblConnectorName.setText(resources.getString("lblConnectorName_Text"));
			this.lblConnectorNameDB.setText(resources.getString("lblConnectorName_Text"));
			this.lblConnectorType.setText(resources.getString("lblConnectorType_Text"));
			this.lblConnectionString.setText(resources.getString("lblConnectionString_Text"));
			this.lblInfoConnectorName.setText(resources.getString("lblInfoConnectorName_Text"));
			this.lblInfoConnectorNameDB.setText(resources.getString("lblInfoConnectorName_Text"));
			this.txtInfoCreateListener.setText(resources.getString("lblInfoCreateListener_Text"));
			this.grbFilesystemConnectorSettings.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
					resources.getString("grbFilesystemConnectorSettings_Text"), TitledBorder.LEADING, TitledBorder.TOP,
					null, new Color(0, 0, 0)));
			this.grbDatabaseConnectorSettings.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
					resources.getString("grbDatabaseConnectorSettings_Text"), TitledBorder.LEADING, TitledBorder.TOP,
					null, new Color(0, 0, 0)));
			this.lblFolderName.setText(resources.getString("lblFolderName_Text"));
			this.lblIsWriteLogActiveDB.setText(resources.getString("chkIsWriteLogActive_Text"));
			this.lblIsWriteImageActiveDB.setText(resources.getString("lblIsWriteImageActive_Text"));
			this.lblIsWriteLogActive.setText(resources.getString("chkIsWriteLogActive_Text"));
			this.lblIsWriteImageActive.setText(resources.getString("lblIsWriteImageActive_Text"));
			this.lblEncryptionPassword.setText(resources.getString("lblEncryptionPassword_Text"));
			this.lblMaxNumberOfLogFiles.setText(resources.getString("lblMaxNumberOfLogFiles_Text"));
			this.lblMaxAgeHours.setText(resources.getString("lblMaxAgeHours_Text"));
			this.lblMaxFileSizeMB.setText(resources.getString("lblMaxFileSizeMB_Text"));
			this.lblImageOutputFormat.setText(resources.getString("lblImageOutputFormat_Text"));
			this.lblConnectionMessage.setText(resources.getString("lblConnectionMessage_Text"));
			this.lblInfoIsWriteImageActiveDB.setText(resources.getString("lblInfoIsWriteImageActiveDB_Text"));
			this.btnAllInstalljdbcDriver.setText(resources.getString("btnAllInstalljdbcDriver_Text"));

			this.btnAddConnector.setText(resources.getString("btnAddConnector_Text"));
			this.btnRemoveConnector.setText(resources.getString("btnRemoveConnector_Text"));
			this.txtInfoDocu.setText(resources.getString("txtInfoDocu_Text_file"));

			// search unused Connector name
			int counter = 1;
			String RequestName = (((eTypeOfConnector) cmbConnectorType.getSelectedItem())
					.equals(eTypeOfConnector.Filesystem_Connector) ? "Filesystem" : "Database") + "Connector_";
			do {
				if (myDataServer.getLoggingConnectorPerName(RequestName + String.format("%03d", counter)) == null) {
					txtConnectorName.setText(RequestName + String.format("%03d", counter));
					break;
				} else {
					counter++;
				}
			} while (true);

			txtConnectorNameDB.setText(txtConnectorName.getText());

			// fill connector listview
			fillConnectorListView();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	PLCComDataServer ShowSettings(PLCComDataServer DataServer) {
		try {
			myDataServer = DataServer;
			this.setVisible(true);
		} catch (Exception ex) {

		}
		return myDataServer;
	}

	private void cmbConnectorType_itemStateChanged(ItemEvent e) {
		try {
			switch ((eTypeOfConnector) cmbConnectorType.getSelectedItem()) {
			case Filesystem_Connector:
				grbDatabaseConnectorSettings.setVisible(false);
				grbFilesystemConnectorSettings.setVisible(true);
				txtInfoDocu.setText(resources.getString("txtInfoDocu_Text_file"));
				break;
			case Database_Connector:
				grbDatabaseConnectorSettings.setVisible(true);
				grbFilesystemConnectorSettings.setVisible(false);
				txtInfoDocu.setText(resources.getString("txtInfoDocu_Text_DB"));
				break;
			}

			// search unused Connector name
			int counter = 1;
			String RequestName = (((eTypeOfConnector) cmbConnectorType.getSelectedItem())
					.equals(eTypeOfConnector.Filesystem_Connector) ? "Filesystem" : "Database") + "Connector_";
			do {
				if (myDataServer.getLoggingConnectorPerName(RequestName + String.format("%03d", counter)) == null) {
					txtConnectorName.setText(RequestName + String.format("%03d", counter));
					break;
				} else {
					counter++;
				}
			} while (true);

			txtConnectorNameDB.setText(txtConnectorName.getText());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void lvConnectors_SelectedIndexChanged(ListSelectionEvent e) {
		try {
			btnRemoveConnector
					.setEnabled(lvConnectors.getSelectedRows() != null && lvConnectors.getSelectedRows().length > 0);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnFolderName_actionPerformed(ActionEvent e) {
		try {

			// open SaveFileDialog
			final JFileChooser dr = new JFileChooser(new File("."));
			dr.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = dr.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				txtFolderName.setText(dr.getSelectedFile().getAbsolutePath());
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void chkIsWriteLogActive_actionPerformed(ActionEvent e) {

		try {
			txtMaxAgeHours.setEnabled(chkIsWriteLogActive.isSelected());
			txtMaxFileSizeMB.setEnabled(chkIsWriteLogActive.isSelected());
			txtMaxNumberOfLogFiles.setEnabled(chkIsWriteLogActive.isSelected());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void chkIsWriteImageActive_actionPerformed(ActionEvent e) {
		try {
			cmbImageOutputFormat.setEnabled(chkIsWriteImageActive.isSelected());
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnClose_actionPerformed(ActionEvent e) {
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void btnReject_actionPerformed(ActionEvent e) {
		try {

			// search unused Connector name
			int counter = 1;
			String RequestName = (((eTypeOfConnector) cmbConnectorType.getSelectedItem())
					.equals(eTypeOfConnector.Filesystem_Connector) ? "Filesystem" : "Database") + "Connector_";
			do {
				if (myDataServer.getLoggingConnectorPerName(RequestName + String.format("%03d", counter)) == null) {
					txtConnectorName.setText(RequestName + String.format("%03d", counter));
					break;
				} else {
					counter++;
				}
			} while (true);

			txtConnectorNameDB.setText(txtConnectorName.getText());
			panNewConnector.setEnabled(false);
			btnRemoveConnector
					.setEnabled(lvConnectors.getSelectedRows() != null && lvConnectors.getSelectedRowCount() > 0);
			btnAddConnector.setEnabled(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnAccept_actionPerformed(ActionEvent e) {
		try {
			LoggingConnector con = null;
			switch ((eTypeOfConnector) cmbConnectorType.getSelectedItem()) {
			case Filesystem_Connector:
				int MaxNumberOfLogFiles;
				int MaxAgeHours;
				int MaxFileSizeMB;

				// parse inputs before execute
				MaxNumberOfLogFiles = Integer.valueOf(txtMaxNumberOfLogFiles.getText());
				MaxAgeHours = Integer.valueOf(txtMaxAgeHours.getText());
				MaxFileSizeMB = Integer.valueOf(txtMaxFileSizeMB.getText());

				// @formatter:off
				// create new FileSystemConnector instance
				con = new FileSystemConnector(txtFolderName.getText(), // Target folder
											txtConnectorName.getText(), // unique connector name
											';', // text separator recommendation ';'
											chkIsWriteLogActive.isSelected(), // activate progressive logging
											chkIsWriteImageActive.isSelected(), // activate immage writing
											(eImageOutputFormat) cmbImageOutputFormat.getSelectedItem(), // output format .dat or .xml
											MaxNumberOfLogFiles, // restrict the maximum number of files. When the value is exceeded the old files are automatically deleted. -1 = Disabled.
											MaxAgeHours, // restrict the maximum age of files. When the value is exceeded the old files are automatically deleted. -1 = Disabled.
											MaxFileSizeMB, // You can restrict the maximum size of files. When the value is exceeded the old files are automatically deleted. -1 = Disabled.
											txtEncryptionPassword.getText()); // If you enter an encryption password, the data is stored in encrypted form. You can read the data using the supplied decryption tool again.

				// @formatter:on
				break;
			case Database_Connector:

				if (txtSQLConnectionString.getText() == null || txtSQLConnectionString.getText().equals(""))
					throw new Exception("Connectionstring is null or empty");

				// @formatter:off
				// Create two valid JDBC Connections and refer it to new database connector
				Connection SQLConLogArchive = DriverManager.getConnection(txtSQLConnectionString.getText());
				Connection SQLConImageArchive = DriverManager.getConnection(txtSQLConnectionString.getText());
				con = new DataBaseConnector(SQLConLogArchive, // a valid database connection for writing 'Log-Archive' based of a DbConnection object. If the value is null, the flag 'isWriteLogActive'  will be disabled
											SQLConImageArchive, // a valid database connection for writing 'Image-Archive' based of a DbConnection object. If the value is null, the flag 'isWriteImageActive' will be disabled
											txtConnectorNameDB.getText(), // unique connector name
											chkIsWriteLogActiveDB.isSelected(), // activate progressive logging
											chkIsWriteImageActiveDB.isSelected()); // activate image writing

				((DataBaseConnector) con).setTableName_DATAIMAGE("PLCCOM_DATAIMAGE");
				((DataBaseConnector) con).setTableName_DATALOG("PLCCOM_DATALOG");
				
				// @formatter:on
				break;
			}

			// Add Connector to PLCcom data server
			myDataServer.addOrReplaceLoggingConnector(con);

			// search unused Connector name
			int counter = 1;
			String RequestName = (((eTypeOfConnector) cmbConnectorType.getSelectedItem())
					.equals(eTypeOfConnector.Filesystem_Connector) ? "Filesystem" : "Database") + "Connector_";
			do {
				if (myDataServer.getLoggingConnectorPerName(RequestName + String.format("%03d", counter)) == null) {
					txtConnectorName.setText(RequestName + String.format("%03d", counter));
					break;
				} else {
					counter++;
				}
			} while (true);

			txtConnectorNameDB.setText(txtConnectorName.getText());
			panNewConnector.setEnabled(false);
			btnRemoveConnector
					.setEnabled(lvConnectors.getSelectedRows() != null && lvConnectors.getSelectedRowCount() > 0);
			btnAddConnector.setEnabled(true);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		} finally {
			fillConnectorListView();
		}

	}

	private void fillConnectorListView() {
		try {
			// clear ListView initial
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// clear lvValues
			DefaultTableModel model = (DefaultTableModel) lvConnectors.getModel();
			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}

			// fill ListView with current ReadRequests
			for (LoggingConnector rr : myDataServer.getLoggingConnectors()) {
				model.addRow(new Object[] { rr.getConnectorName(), rr.toString() });
			}
			model.fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void btnAddConnector_actionPerformed(ActionEvent e) {
		try {

			// search unused Connector name
			int counter = 1;
			String RequestName = (((eTypeOfConnector) cmbConnectorType.getSelectedItem())
					.equals(eTypeOfConnector.Filesystem_Connector) ? "Filesystem" : "Database") + "Connector_";
			do {
				if (myDataServer.getLoggingConnectorPerName(RequestName + String.format("%03d", counter)) == null) {
					txtConnectorName.setText(RequestName + String.format("%03d", counter));
					break;
				} else {
					counter++;
				}
			} while (true);

			txtConnectorNameDB.setText(txtConnectorName.getText());
			panNewConnector.setEnabled(true);
			btnRemoveConnector.setEnabled(false);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnRemoveConnector_actionPerformed(ActionEvent e) {
		// remove request from request collection
		try {
			if (lvConnectors.getSelectedRow() > -1) {
				myDataServer.removeLoggingConnector(
						(String) lvConnectors.getModel().getValueAt(lvConnectors.getSelectedRow(), 0));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			fillConnectorListView();

			// search unused Connector name
			int counter = 1;
			String RequestName = (((eTypeOfConnector) cmbConnectorType.getSelectedItem())
					.equals(eTypeOfConnector.Filesystem_Connector) ? "Filesystem" : "Database") + "Connector_";
			do {
				if (myDataServer.getLoggingConnectorPerName(RequestName + String.format("%03d", counter)) == null) {
					txtConnectorName.setText(RequestName + String.format("%03d", counter));
					break;
				} else {
					counter++;
				}
			} while (true);
		}
	}

	private void btnallInstalljdbcDriver_actionPerformed(ActionEvent e) {
		try {
			StringBuilder sb = new StringBuilder();
			Enumeration<Driver> x = DriverManager.getDrivers();

			// Print out all loaded JDBC drivers.
			while (x.hasMoreElements()) {
				Object driverAsObject = x.nextElement();
				sb.append("JDBC Driver=" + driverAsObject);
				sb.append(System.getProperty("line.separator"));
			}

			if (sb.length() < 1) {
				sb.append(resources.getString("no_jdbcdriver_found"));
			}

			JOptionPane.showMessageDialog(this, sb.toString(), "", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}
}
