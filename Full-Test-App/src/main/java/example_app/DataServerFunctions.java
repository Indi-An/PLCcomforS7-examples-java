
package example_app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.indian.plccom.fors7.ConnectionStateChangeNotifier;
import com.indian.plccom.fors7.IConnectionStateChangeCallback;
import com.indian.plccom.fors7.IIncomingLogEntryCallback;
import com.indian.plccom.fors7.IReadDataResultChangeCallback;
import com.indian.plccom.fors7.IncomingLogEntryEventNotifier;
import com.indian.plccom.fors7.LogEntry;
import com.indian.plccom.fors7.MPI_Device;
import com.indian.plccom.fors7.PLCComDataServer;
import com.indian.plccom.fors7.PLCComDataServer_MPI;
import com.indian.plccom.fors7.PLCComDataServer_PPI;
import com.indian.plccom.fors7.PLCComDataServer_TCP;
import com.indian.plccom.fors7.PLCcomDevice;
import com.indian.plccom.fors7.PPI_Device;
import com.indian.plccom.fors7.ReadDataRequest;
import com.indian.plccom.fors7.ReadDataResult;
import com.indian.plccom.fors7.ReadDataResultChangeNotifier;
import com.indian.plccom.fors7.TCP_ISO_Device;
import com.indian.plccom.fors7.eConnectionState;
import com.indian.plccom.fors7.eDataType;
import com.indian.plccom.fors7.eLogLevel;
import com.indian.plccom.fors7.eRegion;

import example_app.DisabledJPanel.DisabledJPanel;

public class DataServerFunctions extends JFrame
		implements IConnectionStateChangeCallback, IReadDataResultChangeCallback, IIncomingLogEntryCallback {

	private static final long serialVersionUID = 1L;
	private PLCComDataServer myDataServer = null;
	private ResourceBundle resources;

	private JPanel grpAddress;
	private DisabledJPanel panAddress;
	private JComboBox<eDataType> cmbDataType;
	private JComboBox<eRegion> cmbRegion;
	private JTextField txtStartAddress;
	private JTextField txtBit;
	private JTextField txtQuantity;
	private JLabel lblRegion;
	private JLabel lblDataType;
	private JLabel lblStartAddress;
	private JLabel lblBit;
	private JLabel lblLength;
	private JPanel grpAction;
	private DisabledJPanel panAction;
	private JTable lvRequests;
	private JTable lvLog;
	private JTable lvValues;
	private JPanel statusBar;
	private JTextField lblDeviceUUID;
	private JTextField lblDeviceType;
	private JTextField txtRequestKey;
	private JLabel lblRequestKey;
	private JButton btnAddRequest;
	private JButton btnRemoveRequest;
	private JButton btnStartServer;
	private JButton btnSaveLogtoClipboard;
	private JButton btnSaveLogtoFile;
	private JButton btnLoadRequests;
	private JButton btnSaveRequests;
	private JButton btnClose;
	private JTextField txtDB;
	private JTextPane txtInfoDS;
	private JLabel lblDB;
	private JButton btnStopServer;
	private JPanel grbLogSettings;
	private JButton btnLoggingSettings;
	private JTextPane txtInfoLoggingConnectors;
	private JCheckBox chkAutoScroll;
	private JCheckBox chkLogging;
	private JLabel lblLogLevel;
	private JComboBox<eLogLevel> cmbLogLevel;
	private JTextField lblDeviceState;

	/**
	 * Create the dialog.
	 */
	public DataServerFunctions(PLCcomDevice Device, ResourceBundle rb) {
		this.resources = rb;
		setTitle("PLCcom Data Server");
		initialize();

		// Create an instance depending on the device type
		if (Device instanceof TCP_ISO_Device) {
			this.myDataServer = new PLCComDataServer_TCP("PLCDataServerTCP1", (TCP_ISO_Device) Device, 500);
		} else if (Device instanceof MPI_Device) {
			this.myDataServer = new PLCComDataServer_MPI("PLCDataServerMPI1", (MPI_Device) Device, 500);
		} else if (Device instanceof PPI_Device) {
			this.myDataServer = new PLCComDataServer_PPI("PLCDataServerPPI1", (PPI_Device) Device, 500);
		}

		// register Connection state change event
		myDataServer.connectionStateChangeNotifier = new ConnectionStateChangeNotifier(this);

		// register incoming log event
		myDataServer.incomingLogEntryEventNotifier = new IncomingLogEntryEventNotifier(this);

		// register change ReadDataResult event
		myDataServer.readDataResultChangeNotifier = new ReadDataResultChangeNotifier(this);

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

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(DataServerFunctions.class.getResource("/example_app/btnDataServer.Image.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

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

		this.setBounds(15, 15, 691, 829);

		this.getContentPane().setLayout(null);

		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(
				new TitledBorder(null, "add read request", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.grpAddress.setBounds(12, 68, 656, 272);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		panAddress = new DisabledJPanel(grpAddress);
		panAddress.setBounds(grpAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		cmbRegion = new JComboBox<eRegion>();
		cmbRegion.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cmbRegion_addItemListener(e);
			}
		});
		cmbRegion.setBounds(124, 37, 165, 21);
		grpAddress.add(cmbRegion);

		cmbDataType = new JComboBox<eDataType>();
		cmbDataType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cmbDataType_itemStateChanged(e);
			}
		});
		cmbDataType.setBounds(124, 64, 165, 21);
		grpAddress.add(cmbDataType);

		txtStartAddress = new JTextField();
		txtStartAddress.setText("0");
		txtStartAddress.setBounds(481, 38, 165, 20);
		grpAddress.add(txtStartAddress);
		txtStartAddress.setColumns(10);

		txtBit = new JTextField();
		txtBit.setEnabled(false);
		txtBit.setText("0");
		txtBit.setColumns(10);
		txtBit.setBounds(481, 65, 165, 20);
		grpAddress.add(txtBit);

		txtQuantity = new JTextField();
		txtQuantity.setText("1");
		txtQuantity.setColumns(10);
		txtQuantity.setBounds(124, 93, 165, 20);
		grpAddress.add(txtQuantity);

		lblRegion = new JLabel("region");
		lblRegion.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRegion.setBounds(1, 40, 116, 14);
		grpAddress.add(lblRegion);

		lblDataType = new JLabel("data type");
		lblDataType.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDataType.setBounds(1, 67, 116, 14);
		grpAddress.add(lblDataType);

		lblStartAddress = new JLabel("start address (usually in Bytes)");
		lblStartAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStartAddress.setBounds(311, 39, 162, 14);
		grpAddress.add(lblStartAddress);

		lblBit = new JLabel("bit");
		lblBit.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBit.setBounds(315, 66, 158, 14);
		grpAddress.add(lblBit);

		lblLength = new JLabel("quantity");
		lblLength.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLength.setBounds(52, 93, 69, 14);
		grpAddress.add(lblLength);

		txtRequestKey = new JTextField();
		txtRequestKey.setColumns(10);
		txtRequestKey.setBounds(124, 12, 165, 20);
		grpAddress.add(txtRequestKey);

		lblRequestKey = new JLabel("requestkey");
		lblRequestKey.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRequestKey.setBounds(1, 15, 116, 14);
		grpAddress.add(lblRequestKey);

		grpAction = new JPanel();
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Results",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(11, 351, 656, 339);
		getContentPane().add(grpAction);
		grpAction.setLayout(null);

		btnStartServer = new JButton("<html><center>run</center><center>data server</center></html>");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStartServer_actionPerformed(e);
			}
		});
		btnStartServer.setIcon(new ImageIcon(
				DataServerFunctions.class.getResource("/example_app/btnStartServer.Image.png")));
		btnStartServer.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnStartServer.setToolTipText("");
		btnStartServer.setMargin(new Insets(0, 0, 0, 0));
		btnStartServer.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStartServer.setBounds(10, 16, 68, 68);
		grpAction.add(btnStartServer);

		panAction = new DisabledJPanel(grpAction);
		panAction.setBounds(grpAction.getBounds());
		panAction.setDisabledColor(new Color(240, 240, 240, 100));
		panAction.setEnabled(true);
		getContentPane().add(panAction);

		btnLoadRequests = new JButton("<html><center>load</center><center>request</center></html>");
		btnLoadRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoadRequest_actionPerformed(e);
			}
		});
		btnLoadRequests.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnLoadRequest.Image.png")));
		btnLoadRequests.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnLoadRequests.setToolTipText("");
		btnLoadRequests.setMargin(new Insets(0, 0, 0, 0));
		btnLoadRequests.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLoadRequests.setBounds(454, 700, 68, 68);
		getContentPane().add(btnLoadRequests);

		btnSaveRequests = new JButton("<html><center>save</center><center>request</center></html>");
		btnSaveRequests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveRequest_actionPerformed(e);
			}
		});
		btnSaveRequests.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnSaveRequest.Image.png")));
		btnSaveRequests.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveRequests.setToolTipText("");
		btnSaveRequests.setMargin(new Insets(0, 0, 0, 0));
		btnSaveRequests.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveRequests.setBounds(527, 700, 68, 68);
		getContentPane().add(btnSaveRequests);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(
				new ImageIcon(OptimizedReadWriteBox.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(600, 699, 68, 68);
		getContentPane().add(btnClose);

		// ############### begin init lvRequests #####################
		lvRequests = new JTable();
		lvRequests.setShowHorizontalLines(false);
		lvRequests.setShowVerticalLines(false);
		lvRequests.setShowGrid(false);

		lvRequests.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvRequests.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "RequestKey", "Request" }) {
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
		lvRequests.getColumnModel().getColumn(0).setResizable(true);
		lvRequests.getColumnModel().getColumn(0).setMinWidth(0);
		lvRequests.getColumnModel().getColumn(0).setPreferredWidth(0);
		lvRequests.getColumnModel().getColumn(0).setMaxWidth(0);
		lvRequests.getColumnModel().getColumn(1).setResizable(true);
		lvRequests.getColumnModel().getColumn(1).setPreferredWidth(520);

		lvRequests.setBounds(new Rectangle(0, 0, 550, 140));

		lvRequests.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lvRequests_SelectedIndexChanged(e);
			}
		});

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvRequests.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.LEFT);

		lvRequests.setFillsViewportHeight(true);
		JPanel lvListenerContainer = new JPanel();
		JScrollPane scrollPanelvListener = new JScrollPane(lvRequests);

		lvListenerContainer.setLayout(new BorderLayout());
		lvListenerContainer.add(lvRequests.getTableHeader(), BorderLayout.PAGE_START);
		scrollPanelvListener.setBounds(lvListenerContainer.getBounds());
		lvListenerContainer.add(scrollPanelvListener, BorderLayout.NORTH);

		lvListenerContainer.setBounds(new Rectangle(101, 121, 550, 140));
		grpAddress.add(lvListenerContainer);

		// ############### end init lvListener #####################
		// ############### begin init lvValues #####################
		lvValues = new JTable();
		lvValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lvValues.setShowGrid(false);
		lvValues.setShowHorizontalLines(false);
		lvValues.setShowVerticalLines(false);
		lvValues.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvValues.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Timestamp", "ItemKey", "Value", "Quality", "Message" }) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});

		// set renderer for colored rows
		lvValues.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvValues.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvValues.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(100);
		lvValues.getColumnModel().getColumn(1).setResizable(true);
		lvValues.getColumnModel().getColumn(1).setPreferredWidth(100);
		lvValues.getColumnModel().getColumn(2).setResizable(true);
		lvValues.getColumnModel().getColumn(2).setPreferredWidth(200);
		lvValues.getColumnModel().getColumn(3).setResizable(true);
		lvValues.getColumnModel().getColumn(3).setPreferredWidth(200);
		lvValues.getColumnModel().getColumn(4).setResizable(true);
		lvValues.getColumnModel().getColumn(4).setPreferredWidth(1000);

		lvValues.setBounds(100, 19, 550, 140);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.CENTER);

		lvValuesContainer.setBounds(new Rectangle(100, 16, 550, 140));
		grpAction.add(lvValuesContainer);

		btnStopServer = new JButton("<html><center>stop</center><center>data server</center></html>");
		btnStopServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStopServer_actionPerformed(e);
			}
		});
		btnStopServer.setIcon(
				new ImageIcon(DataServerFunctions.class.getResource("/example_app/btnStopServer.Image.png")));
		btnStopServer.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnStopServer.setToolTipText("");
		btnStopServer.setMargin(new Insets(0, 0, 0, 0));
		btnStopServer.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStopServer.setBounds(10, 88, 68, 68);
		grpAction.add(btnStopServer);

		// ############### end init lvValues #####################
		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setShowGrid(false);
		lvLog.setShowHorizontalLines(false);
		lvLog.setShowVerticalLines(false);
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "LogLevel", "Timestamp", "Text" }) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] { String.class, String.class, String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

		});

		// set cell renderer for colored rows
		lvLog.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvLog.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvLog.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		// set columns
		lvLog.getColumnModel().getColumn(0).setResizable(true);
		lvLog.getColumnModel().getColumn(0).setPreferredWidth(70);
		lvLog.getColumnModel().getColumn(1).setResizable(true);
		lvLog.getColumnModel().getColumn(1).setPreferredWidth(110);
		lvLog.getColumnModel().getColumn(2).setResizable(true);
		lvLog.getColumnModel().getColumn(2).setPreferredWidth(6000);
		lvLog.setBounds(112, 449, 550, 142);
		lvLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		lvLog.setAutoscrolls(true);

		JScrollPane scrollPanelvLog = new JScrollPane(lvLog);
		lvLog.setFillsViewportHeight(true);

		JPanel lvLogContainer = new JPanel();
		lvLogContainer.setBounds(100, 184, 550, 142);
		grpAction.add(lvLogContainer);

		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(lvLog.getTableHeader(), BorderLayout.PAGE_START);
		lvLogContainer.add(scrollPanelvLog, BorderLayout.CENTER);

		// ############### end init lvLog #####################

		btnSaveLogtoFile = new JButton("<html><center>save log to</center><center>file</center></html>");
		btnSaveLogtoFile.setBounds(10, 258, 68, 68);
		grpAction.add(btnSaveLogtoFile);
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_actionPerformed(e);
			}
		});
		btnSaveLogtoFile.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnSaveLogtoFile.Image.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setToolTipText("");
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);

		btnSaveLogtoClipboard = new JButton("<html><center>copy log to</center><center>clipboard</center></html>");
		btnSaveLogtoClipboard.setBounds(10, 184, 68, 68);
		grpAction.add(btnSaveLogtoClipboard);
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e);
			}
		});
		btnSaveLogtoClipboard.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnSaveLogtoClipboard.Image.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setToolTipText("");
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);

		chkLogging = new JCheckBox("diagnostic output");
		chkLogging.setSelected(true);
		chkLogging.setBounds(100, 160, 117, 23);
		grpAction.add(chkLogging);

		chkAutoScroll = new JCheckBox("AutoScoll");
		chkAutoScroll.setSelected(true);
		chkAutoScroll.setBounds(222, 160, 117, 23);
		grpAction.add(chkAutoScroll);

		lblLogLevel = new JLabel("LogLevel");
		lblLogLevel.setBounds(345, 164, 56, 14);
		grpAction.add(lblLogLevel);

		cmbLogLevel = new JComboBox<eLogLevel>();
		cmbLogLevel.setBounds(396, 160, 117, 21);
		grpAction.add(cmbLogLevel);

		btnAddRequest = new JButton("<html><center>add</center><center>Request</center></html>");
		btnAddRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				btnAddRequest_actionPerformed(arg);
			}
		});
		btnAddRequest.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnAddRequest.Image.png")));
		btnAddRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAddRequest.setToolTipText("");
		btnAddRequest.setMargin(new Insets(0, 0, 0, 0));
		btnAddRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAddRequest.setBounds(10, 121, 68, 68);
		grpAddress.add(btnAddRequest);

		btnRemoveRequest = new JButton("<html><center>remove</center><center>Request</center></html>");
		btnRemoveRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRemoveRequest_actionPerformed(e);
			}
		});
		btnRemoveRequest.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnRemoveRequest.Image.png")));
		btnRemoveRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRemoveRequest.setToolTipText("");
		btnRemoveRequest.setMargin(new Insets(0, 0, 0, 0));
		btnRemoveRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRemoveRequest.setBounds(10, 195, 68, 68);
		grpAddress.add(btnRemoveRequest);

		lblDB = new JLabel("DB (only for DB, use 0 otherwise)");
		lblDB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDB.setBounds(294, 14, 179, 14);
		grpAddress.add(lblDB);

		txtDB = new JTextField();
		txtDB.setText("1");
		txtDB.setColumns(10);
		txtDB.setBounds(481, 13, 165, 20);
		grpAddress.add(txtDB);

		statusBar = new JPanel();
		statusBar.setLayout(null);
		// Creating the StatusBar.
		// statusBar.setLayout(new BorderLayout());
		statusBar.setBounds(0, 780, 681, 22);
		statusBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		statusBar.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(statusBar);

		lblDeviceState = new JTextField("disconnected");
		lblDeviceState.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceState.setFocusable(false);
		lblDeviceState.setEditable(false);
		lblDeviceState.setBackground(Color.WHITE);
		lblDeviceState.setSize(200, 18);
		lblDeviceState.setLocation(2, 2);
		statusBar.add(lblDeviceState);

		lblDeviceType = new JTextField("Adapter Type: nothing");
		lblDeviceType.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceType.setFocusable(false);
		lblDeviceType.setEditable(false);
		lblDeviceType.setBackground(Color.WHITE);
		lblDeviceType.setSize(227, 18);
		lblDeviceType.setLocation(204, 2);
		statusBar.add(lblDeviceType);

		lblDeviceUUID = new JTextField("UUID: nothing");
		lblDeviceUUID.setHorizontalAlignment(SwingConstants.CENTER);
		lblDeviceUUID.setFocusable(false);
		lblDeviceUUID.setEditable(false);
		lblDeviceUUID.setBackground(Color.WHITE);
		lblDeviceUUID.setSize(244, 18);
		lblDeviceUUID.setLocation(433, 2);
		statusBar.add(lblDeviceUUID);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		panel.setBackground(SystemColor.info);
		panel.setBounds(196, 4, 472, 59);
		getContentPane().add(panel);

		txtInfoDS = new JTextPane();
		txtInfoDS.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoDS.setText(
				"The com.indian.plccom.fors7 data server monitors autonomous data in the PLC in an adjustable cycle and notifies the parent software for a change with an event.");
		txtInfoDS.setEditable(false);
		txtInfoDS.setBorder(null);
		txtInfoDS.setBackground(SystemColor.info);
		txtInfoDS.setBounds(66, 2, 396, 50);
		panel.add(txtInfoDS);

		JLabel label = new JLabel();
		label.setIcon(
				new ImageIcon(OptimizedReadWriteBox.class.getResource("/example_app/pictureBox1.Image.png")));
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(2, 2, 32, 32);
		panel.add(label);

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

		grbLogSettings = new JPanel();
		grbLogSettings.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grbLogSettings.setBounds(10, 695, 433, 79);
		getContentPane().add(grbLogSettings);
		grbLogSettings.setLayout(null);

		btnLoggingSettings = new JButton("<html><center>logging</center><center>connectors</center></html>");
		btnLoggingSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLoggingSettings_actionPerformed(e);
			}
		});
		btnLoggingSettings.setIcon(new ImageIcon(
				DataServerFunctions.class.getResource("/example_app/btnLoggingSettings.Image.png")));
		btnLoggingSettings.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnLoggingSettings.setToolTipText("");
		btnLoggingSettings.setMargin(new Insets(0, 0, 0, 0));
		btnLoggingSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLoggingSettings.setBounds(11, 6, 68, 68);
		grbLogSettings.add(btnLoggingSettings);

		txtInfoLoggingConnectors = new JTextPane();
		txtInfoLoggingConnectors.setText(
				"\u201ALogging-Connectors\u2018 store read variable data to  the file system or a SQL database for further use.\r\nContinuous archiving and saving an Image with just a few rows of code. \r\n");
		txtInfoLoggingConnectors.setEditable(false);
		txtInfoLoggingConnectors.setBorder(new LineBorder(new Color(0, 0, 0)));
		txtInfoLoggingConnectors.setBackground(SystemColor.info);
		txtInfoLoggingConnectors.setBounds(101, 7, 322, 64);
		grbLogSettings.add(txtInfoLoggingConnectors);

	}

	protected void formWindowClosing(WindowEvent e) {
		Main.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {

		lblDeviceUUID.setText("UUID: " + myDataServer.getDeviceUUID().toString());
		lblDeviceType.setText("Type: " + myDataServer.getClass().getName());

		// fill combobox with enum values
		cmbDataType.setModel(new DefaultComboBoxModel<eDataType>(eDataType.values()));

		cmbDataType.setSelectedItem(eDataType.BYTE);

		cmbRegion.setModel(new DefaultComboBoxModel<eRegion>(eRegion.values()));

		cmbLogLevel.setModel(new DefaultComboBoxModel<eLogLevel>(eLogLevel.values()));

		// set ressources
		this.grpAddress.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAction.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAction_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.btnClose.setText(resources.getString("btnClose_Text"));
		this.btnAddRequest.setText(resources.getString("btnAddRequest_Text"));
		this.btnRemoveRequest.setText(resources.getString("btnRemoveRequest_Text"));
		this.btnStartServer.setText(resources.getString("btnStartServer_Text"));
		this.btnLoadRequests.setText(resources.getString("btnLoadRequests_Text"));
		this.btnSaveRequests.setText(resources.getString("btnSaveRequests_Text"));
		this.txtInfoDS.setText(resources.getString("txtInfoDS_Text"));
		this.btnSaveLogtoClipboard.setText(resources.getString("btnSaveLogtoClipboard_Text"));
		this.btnSaveLogtoFile.setText(resources.getString("btnSaveLogtoFile_Text"));
		this.lblDataType.setText(resources.getString("lblDataType_Text"));
		this.lblDB.setText(resources.getString("lblDB_Text"));
		this.lblStartAddress.setText(resources.getString("lblStartAdress_Text"));
		this.lblBit.setText(resources.getString("lblBit_Text"));
		this.lblLength.setText(resources.getString("lblLength_Text"));
		this.lblRegion.setText(resources.getString("lblRegion_Text"));
		this.chkLogging.setText(resources.getString("lblLog_Text"));
		this.btnStopServer.setText(resources.getString("btnStopServer_Text"));
		this.lblStartAddress.setText(resources.getString("lblStartAdress_Text"));
		this.btnLoggingSettings.setText(resources.getString("btnLoggingSettings_Text"));
		this.txtInfoLoggingConnectors.setText(resources.getString("txtInfoLoggingConnectors_Text"));

	}

	private void btnAddRequest_actionPerformed(ActionEvent arg) {
		try {
			// define new request
			ReadDataRequest RequestItem = null;
			// @formatter:off
			RequestItem = new ReadDataRequest((eRegion) cmbRegion.getSelectedItem(), // Region
							Integer.valueOf(txtDB.getText()), // DB only for datablock operations otherwise 0
							Integer.valueOf(txtStartAddress.getText()), // Read start address
							(eDataType) cmbDataType.getSelectedItem(), // Target Datatype
							Integer.valueOf(txtQuantity.getText()), // quantity of objects to be read
							Byte.valueOf(txtBit.getText())); // Address of first Bit

			// @formatter:on

			// add new request to request collection
			myDataServer.addReadDataRequest(RequestItem);

			// update listview
			fillRequestListView();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnRemoveRequest_actionPerformed(ActionEvent e) {
		// remove request from request collection
		try {
			if (lvRequests.getSelectedRow() > -1) {
				myDataServer.removeReadDataRequest(
						(String) lvRequests.getModel().getValueAt(lvRequests.getSelectedRow(), 0));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			fillRequestListView();

		}

	}

	private void lvRequests_SelectedIndexChanged(ListSelectionEvent e) {
		btnRemoveRequest.setEnabled(lvRequests.getSelectedRow() > -1);
	}

	private void fillRequestListView() {
		try {
			// clear ListView initial
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// clear lvValues
			DefaultTableModel model = (DefaultTableModel) lvRequests.getModel();
			// clear model
			while (model.getRowCount() > 0) {
				model.removeRow(0);
			}

			// fill ListView with current ReadRequests
			for (ReadDataRequest rr : myDataServer.getReadDataRequests()) {
				model.addRow(new Object[] { rr.getItemkey(), rr.toString() });
			}
			model.fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void btnStartServer_actionPerformed(ActionEvent e) {
		try {
			// start PLCcom data server
			myDataServer.startServer();
			btnStartServer.setEnabled(false);
			btnStopServer.setEnabled(true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnStopServer_actionPerformed(ActionEvent e) {
		try {
			// start PLCcom data server
			myDataServer.stopServer();
			btnStartServer.setEnabled(true);
			btnStopServer.setEnabled(false);
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

	// private Index for updating Value JTable
	private HashMap<String, Integer> ValueIndex = new HashMap<String, Integer>();

	@Override
	public void On_ReadDataResult(ReadDataResult res) {

		try {

			// processed incoming results
			if (ValueIndex.containsKey(res.getItemkey())) {

				// get RowIndex
				int RowIndex = ValueIndex.get(res.getItemkey());

				// Set Timestamp
				lvValues.getModel().setValueAt(
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()), RowIndex, 0);

				// Set Value
				lvValues.getModel().setValueAt(ArrayToString(res.getValues()), RowIndex, 2);

				// Set Quality
				lvValues.getModel().setValueAt(res.getQuality().toString(), RowIndex, 3);

				// Set Message
				lvValues.getModel().setValueAt(res.getMessage(), RowIndex, 4);

				// refresh ListView only by change of values
				((AbstractTableModel) lvValues.getModel()).fireTableDataChanged();

			} else {
				// Key not exists => insert row into ListView
				ValueIndex.put(res.getItemkey(), lvValues.getRowCount());

				addRowToTable(lvValues,
						new Object[] { DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
								res.getItemkey(), ArrayToString(res.getValues()), res.getQuality().toString(),
								res.getMessage() });

			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private String ArrayToString(Object[] value) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length; i++) {
			try {
				sb.append(String.valueOf(value[i]));
			} catch (Exception ex) {
				sb.append(String.valueOf(ex.getMessage()));
			}
			if (i < value.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	@Override
	public void OnIncomingLogEntry(LogEntry[] value) {
		try {

			if (!chkLogging.isSelected())
				return;

			// write LogEntry into JTable lvLog
			for (LogEntry le : value) {
				if (le.getLogLevel().getValue() < ((eLogLevel) cmbLogLevel.getSelectedItem()).getValue())
					return;
				synchronized (lvLog) {

					addRowToTable(lvLog,
							new Object[] { le.getLogLevel().toString(),
									DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
									le.getSender() + le.getText() + " " + le.getStackTraceString() });

					// remove old items
					clearTable(lvLog, 100);

					if (chkAutoScroll.isSelected() && lvLog.getRowCount() > 1) {
						Rectangle rect = lvLog.getCellRect(lvLog.getRowCount() - 1, 0, true);
						lvLog.scrollRectToVisible(rect);
					}
				}
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void On_ConnectionStateChange(eConnectionState value) {
		try {
			lblDeviceState.setText(resources.getString("State_" + value.toString())
					+ ((myDataServer.getDeviceInfo() != null && myDataServer.getDeviceInfo().getName() != null)
							? " " + myDataServer.getDeviceInfo().getName()
							: ""));
			lblDeviceState.setBackground(value.equals(eConnectionState.connected) ? Color.blue : Color.white);
			lblDeviceState.setForeground(value.equals(eConnectionState.connected) ? Color.white : Color.black);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnSaveLogtoClipboard_actionPerformed(ActionEvent e) {
		// copy diagnostic log to clipboard

		try {
			// get model from JTable
			DefaultTableModel model = (DefaultTableModel) lvLog.getModel();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < model.getRowCount(); i++) {
				sb.append(model.getValueAt(i, 0));
				sb.append(" ");
				sb.append(model.getValueAt(i, 1));
				sb.append(" ");
				sb.append(model.getValueAt(i, 2));
				sb.append(System.getProperty("line.separator"));
			}

			if (sb.length() > 0) {
				// copy log in clipboard
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

				clipboard.setContents(new StringSelection(sb.toString()), null);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnSaveLogtoFile_actionPerformed(ActionEvent e) {
		try {
			// open SaveFileDialog
			File f = new File(
					new File("").getAbsolutePath() + OSValidator.getFolderSeparator() + "PLCcomDiagnosticLog.log");
			JFileChooser dr = new JFileChooser(f);

			dr.setSelectedFile(f);

			FileFilter filter = new FileNameExtensionFilter("log Files *.log", "log");
			dr.addChoosableFileFilter(filter);
			int returnVal = dr.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// copy diagnostic log to file
				// get model from JTable
				DefaultTableModel model = (DefaultTableModel) lvLog.getModel();

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < model.getRowCount(); i++) {
					sb.append(model.getValueAt(i, 0));
					sb.append(" ");
					sb.append(model.getValueAt(i, 1));
					sb.append(" ");
					sb.append(model.getValueAt(i, 2));
				}
				if (sb.length() > 0) {
					try (Writer writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(dr.getSelectedFile().getAbsolutePath()), "utf-8"))) {
						writer.write(sb.toString());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				JOptionPane.showMessageDialog(
						this, resources.getString("successfully_saved") + System.getProperty("line.separator")
								+ "File: " + dr.getSelectedFile().getAbsolutePath(),
						"", JOptionPane.INFORMATION_MESSAGE);

			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnLoadRequest_actionPerformed(ActionEvent e) {
		Properties p = new Properties();

		try {
			// open SaveFileDialog
			File f = new File(
					new File("").getAbsolutePath() + OSValidator.getFolderSeparator() + "PLCcomRequestCollection.xml");
			JFileChooser dr = new JFileChooser(f);

			dr.setSelectedFile(f);

			FileFilter filter = new FileNameExtensionFilter("xml Files *.xml", "xml");
			dr.addChoosableFileFilter(filter);
			int returnVal = dr.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// open file
				FileInputStream fis = new FileInputStream(dr.getSelectedFile().getAbsolutePath());

				// load saved settiungs from PLCcomModbusRequestCollection.xml
				p.loadFromXML(fis);
				// load listener
				Enumeration<?> prop = p.propertyNames();
				while (prop.hasMoreElements()) {
					String key = (String) prop.nextElement();
					String value = p.getProperty(key);
					boolean isIncomplete = false;

					// found request
					if (key.startsWith("Request#")) {
						String RequestKey = value;
						eRegion Region = null;
						eDataType dt = null;
						int DB = 0;
						int StartAddress = 0;
						int Quantity = 0;
						byte Bit = 0;

						if (p.containsKey(RequestKey + "#" + "Region")) {
							Region = eRegion.valueOf(p.getProperty(RequestKey + "#" + "Region"));
						} else {
							isIncomplete = true;
						}

						if (p.containsKey(RequestKey + "#" + "Target_Datatype")) {
							dt = eDataType.valueOf(p.getProperty(RequestKey + "#" + "Target_Datatype"));
						} else {
							isIncomplete = true;
						}

						if (p.containsKey(RequestKey + "#" + "DB")) {
							DB = Integer.valueOf(p.getProperty(RequestKey + "#" + "DB"));
						} else {
							isIncomplete = true;
						}

						if (p.containsKey(RequestKey + "#" + "StartAddress")) {
							StartAddress = Integer.valueOf(p.getProperty(RequestKey + "#" + "StartAddress"));
						} else {
							isIncomplete = true;
						}

						if (p.containsKey(RequestKey + "#" + "Quantity")) {
							Quantity = Integer.valueOf(p.getProperty(RequestKey + "#" + "Quantity"));
						} else {
							isIncomplete = true;
						}

						if (p.containsKey(RequestKey + "#" + "Bit")) {
							Bit = Byte.valueOf(p.getProperty(RequestKey + "#" + "Bit"));
						}

					// @formatter:off
					if (!isIncomplete) {
						// define new read request

						ReadDataRequest RequestItem = new ReadDataRequest((eRegion) Region, // Region
																			DB, // DB only for datablock operations otherwise 0
																			StartAddress, // Read start address
																			dt, // Target Datatype
																			Quantity, // quantity of objects to be read
																			Bit); // Address of first Bit

						// add new request to request collection
						myDataServer.addReadDataRequest(RequestItem);

					}
					// @formatter:on

					}
				}
				// Refresh listview
				fillRequestListView();

			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (FileNotFoundException ignore) {

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (Throwable ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}

	}

	private void btnSaveRequest_actionPerformed(ActionEvent e) {
		try {
			// open SaveFileDialog
			File f = new File(
					new File("").getAbsolutePath() + OSValidator.getFolderSeparator() + "PLCcomRequestCollection.xml");
			JFileChooser dr = new JFileChooser(f);

			dr.setSelectedFile(f);

			FileFilter filter = new FileNameExtensionFilter("xml Files *.xml", "xml");
			dr.addChoosableFileFilter(filter);
			int returnVal = dr.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				// Write Settings in PLCcomModbusRequestCollection.xml
				Properties p = new Properties();

				for (ReadDataRequest rreq : myDataServer.getReadDataRequests()) {
					String RequestName = rreq.getItemkey();
					p.setProperty("Request#" + RequestName, RequestName);
					p.setProperty(RequestName + "#" + "Region", String.valueOf(rreq.getRegion()));
					p.setProperty(RequestName + "#" + "Target_Datatype", String.valueOf(rreq.getDataType()));
					p.setProperty(RequestName + "#" + "DB", String.valueOf(rreq.getDB()));
					p.setProperty(RequestName + "#" + "StartAddress", String.valueOf(rreq.getStartAddress()));
					p.setProperty(RequestName + "#" + "Bit", String.valueOf(rreq.getBit()));
					p.setProperty(RequestName + "#" + "Quantity", String.valueOf(rreq.getQuantity()));
				}

				FileOutputStream fos = new FileOutputStream(dr.getSelectedFile().getAbsolutePath());
				p.storeToXML(fos, "PLCcom Example Settings", "UTF8");
				fos.close();
				JOptionPane.showMessageDialog(
						this, resources.getString("successfully_saved") + System.getProperty("line.separator")
								+ "File: " + dr.getSelectedFile().getAbsolutePath(),
						"", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} catch (HeadlessException ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	private void btnLoggingSettings_actionPerformed(ActionEvent e) {
		try {
			DataServerLoggingSettings dsls = new DataServerLoggingSettings(resources);
			myDataServer = dsls.ShowSettings(myDataServer);
			if (myDataServer.getLoggingConnectors().length == 0) {
				this.txtInfoLoggingConnectors.setText(resources.getString("txtInfoLoggingConnectors_Text"));
			} else {
				this.txtInfoLoggingConnectors.setText(String.valueOf(myDataServer.getLoggingConnectors().length) + " "
						+ resources.getString("txtInfoLoggingDefinedConnectors_Text"));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cmbDataType_itemStateChanged(ItemEvent e) {

		try {
			// set bit textfield
			txtBit.setEnabled(((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.BIT));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cmbRegion_addItemListener(ItemEvent e) {
		try {
			// enable or disable DB field
			txtDB.setEnabled(((eRegion) cmbRegion.getSelectedItem()).equals(eRegion.DataBlock));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("unused")
	private void clearTable(JTable Target) {
		// clear complete JTable
		try {
			DefaultTableModel modLog = (DefaultTableModel) Target.getModel();
			// clear model
			while (modLog.getRowCount() > 0) {
				modLog.removeRow(0);
			}
			((AbstractTableModel) Target.getModel()).fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearTable(JTable Target, int Count) {
		// clear complete JTable
		try {
			DefaultTableModel modLog = (DefaultTableModel) Target.getModel();
			// clear model
			while (modLog.getRowCount() > Count) {
				modLog.removeRow(0);
			}
			((AbstractTableModel) Target.getModel()).fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addRowToTable(JTable Target, Object[] entrys) {
		// adding row to JTable
		try {
			DefaultTableModel modLog = (DefaultTableModel) Target.getModel();
			// add summary log entry
			modLog.addRow(entrys);
			((AbstractTableModel) Target.getModel()).fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
