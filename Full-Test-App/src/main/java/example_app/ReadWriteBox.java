package example_app;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import example_app.DisabledJPanel.DisabledJPanel;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.indian.plccom.fors7.UnsignedDatatypes.UInteger;
import com.indian.plccom.fors7.UnsignedDatatypes.ULong;
import com.indian.plccom.fors7.UnsignedDatatypes.UShort;
import com.indian.plccom.fors7.LogEntry;
import com.indian.plccom.fors7.OperationResult;
import com.indian.plccom.fors7.PLCcomDevice;
import com.indian.plccom.fors7.ReadDataRequest;
import com.indian.plccom.fors7.ReadDataResult;
import com.indian.plccom.fors7.WriteDataRequest;
import com.indian.plccom.fors7.WriteDataResult;
import com.indian.plccom.fors7.eDataType;
import com.indian.plccom.fors7.eLogLevel;
import com.indian.plccom.fors7.eRegion;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Rectangle;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class ReadWriteBox extends JFrame {

	private PLCcomDevice Device;
	private ResourceBundle resources;
	private String ValuetoWrite = "";
	private JPanel grpAddress;
	private DisabledJPanel panAddress;
	private JComboBox<eDataType> cmbDataType;
	private JComboBox<eRegion> cmbRegion;
	private JTextField txtReadAddress;
	private JTextField txtBit;
	private JTextField txtQuantity;
	private JTextField txtWriteAddress;
	private JLabel lblRegion;
	private JLabel lblDataType;
	private JLabel lblReadAddress;
	private JLabel lblBit;
	private JLabel lblQuantity;
	private JLabel lblWriteAddress;
	private JTextPane txtInfoRB;
	private JPanel grbWriteValues;
	private DisabledJPanel panWriteValues;
	private JCheckBox chkSingleValue;
	private JLabel lblEnterValues;
	private JTextArea txtMultipleNumericValues;
	private JScrollPane scrollMultipleNumericValues;
	private JTextArea txtMultipleBoolValues;
	private JScrollPane scrollMultipleBoolValues;
	private JPanel grpAction;
	private DisabledJPanel panAction;
	private JTable lvLog;
	private JTable lvValues;
	private JRadioButton rbOn;
	private JRadioButton rbOff;
	private JTextField txtSingleValues;
	private JLabel lblMode;
	private JLabel lblDB;
	private JTextField txtDB;
	private JPanel panel;
	private JLabel label;
	private JLabel lblLogo;
	private JLabel lblLog;
	private JButton btnSaveLogtoClipboard;
	private JButton btnExecute;
	private JButton btnSaveLogtoFile;
	private JButton btnClose;
	private JRadioButton rbRead;
	private JRadioButton rbWrite;
	private JComboBox<Charset> cmbCharSet;
	private JLabel lblFactor;
	private JTextField txtFactor;

	/**
	 * Create the dialog.
	 */
	public ReadWriteBox(PLCcomDevice Device, ResourceBundle rb) {
		resources = rb;
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		this.Device = Device;
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

		setTitle("ReadWriteBox");

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(ReadWriteBox.class.getResource("/example_app/btnReadWriteFunctions.Image.png")));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setBounds(10, 10, 688, 790);

		this.getContentPane().setLayout(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing();
			}
		});

		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "request",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAddress.setBounds(12, 67, 658, 268);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		panAddress = new DisabledJPanel(grpAddress);
		panAddress.setBounds(grpAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		cmbRegion = new JComboBox<eRegion>();
		cmbRegion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				cmbRegion_actionPerformed(arg);
			}
		});
		cmbRegion.setBounds(123, 35, 175, 21);
		grpAddress.add(cmbRegion);

		cmbDataType = new JComboBox<eDataType>();
		cmbDataType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbDataType_actionPerformed(e);
			}
		});
		cmbDataType.setBounds(123, 63, 175, 21);
		grpAddress.add(cmbDataType);

		txtReadAddress = new JTextField();
		txtReadAddress.setText("0");
		txtReadAddress.setBounds(123, 123, 175, 20);
		grpAddress.add(txtReadAddress);
		txtReadAddress.setColumns(10);

		txtBit = new JTextField();
		txtBit.setEnabled(false);
		txtBit.setText("0");
		txtBit.setColumns(10);
		txtBit.setBounds(123, 184, 175, 20);
		grpAddress.add(txtBit);

		txtQuantity = new JTextField();
		txtQuantity.setText("1");
		txtQuantity.setColumns(10);
		txtQuantity.setBounds(123, 213, 42, 20);
		grpAddress.add(txtQuantity);

		txtWriteAddress = new JTextField();
		txtWriteAddress.setText("0");
		txtWriteAddress.setColumns(10);
		txtWriteAddress.setBounds(123, 154, 175, 20);
		grpAddress.add(txtWriteAddress);

		lblRegion = new JLabel("region");
		lblRegion.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRegion.setBounds(0, 38, 116, 14);
		grpAddress.add(lblRegion);

		lblDataType = new JLabel(
				ResourceBundle.getBundle("example_app.resources").getString("lblDataType_Text"));
		lblDataType.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDataType.setBounds(0, 65, 116, 14);
		grpAddress.add(lblDataType);

		lblReadAddress = new JLabel(
				ResourceBundle.getBundle("example_app.resources").getString("lblReadAddress_Text"));
		lblReadAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblReadAddress.setBounds(2, 126, 116, 14);
		grpAddress.add(lblReadAddress);

		lblBit = new JLabel("bit");
		lblBit.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBit.setBounds(2, 187, 116, 14);
		grpAddress.add(lblBit);

		lblQuantity = new JLabel("quantity");
		lblQuantity.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQuantity.setBounds(2, 216, 116, 14);
		grpAddress.add(lblQuantity);

		lblWriteAddress = new JLabel(
				ResourceBundle.getBundle("example_app.resources").getString("lblWriteAddress_Text"));
		lblWriteAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWriteAddress.setBounds(2, 148, 116, 32);
		grpAddress.add(lblWriteAddress);

		grbWriteValues = new JPanel();
		grbWriteValues.setBorder(new LineBorder(new Color(0, 0, 0)));
		grbWriteValues.setBounds(327, 32, 319, 229);
		grpAddress.add(grbWriteValues);
		grbWriteValues.setLayout(null);

		panWriteValues = new DisabledJPanel(grbWriteValues);
		panWriteValues.setBounds(grbWriteValues.getBounds());
		panWriteValues.setDisabledColor(new Color(240, 240, 240, 100));
		panWriteValues.setEnabled(false);
		grpAddress.add(panWriteValues);

		chkSingleValue = new JCheckBox("single value");
		chkSingleValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg) {
				chkSingleValue_mouseClicked(arg);
			}
		});
		chkSingleValue.setBounds(41, 11, 82, 17);
		grbWriteValues.add(chkSingleValue);

		txtMultipleNumericValues = new JTextArea();
		txtMultipleNumericValues.setText("0\r\n0\r\n0\r\n0");
		txtMultipleNumericValues.setBorder(null);
		txtMultipleNumericValues.setBounds(41, 54, 275, 94);
		txtMultipleNumericValues.setLineWrap(true);
		txtMultipleNumericValues.setWrapStyleWord(true);
		scrollMultipleNumericValues = new JScrollPane(txtMultipleNumericValues);
		scrollMultipleNumericValues.setBounds(new Rectangle(41, 54, 275, 164));
		grbWriteValues.add(scrollMultipleNumericValues);
		txtMultipleNumericValues.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtMultipleNumericValues_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtMultipleNumericValues_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtMultipleNumericValues_TextChanged(e);
			}
		});

		txtMultipleBoolValues = new JTextArea();
		txtMultipleBoolValues.setText("true\r\ntrue\r\ntrue\r\ntrue");
		txtMultipleBoolValues.setVisible(false);
		txtMultipleBoolValues.setBorder(null);
		txtMultipleBoolValues.setBounds(41, 54, 275, 94);
		txtMultipleBoolValues.setLineWrap(true);
		txtMultipleBoolValues.setWrapStyleWord(true);
		scrollMultipleBoolValues = new JScrollPane(txtMultipleBoolValues);
		scrollMultipleBoolValues.setBounds(new Rectangle(41, 54, 275, 133));
		scrollMultipleBoolValues.setVisible(false);
		grbWriteValues.add(scrollMultipleBoolValues);
		txtMultipleBoolValues.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtMultipleBoolValues_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtMultipleBoolValues_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtMultipleBoolValues_TextChanged(e);
			}
		});

		lblEnterValues = new JLabel("Please enter the desired value:");
		lblEnterValues.setBounds(38, 32, 219, 13);
		grbWriteValues.add(lblEnterValues);

		rbOn = new JRadioButton("ON");
		rbOn.setSelected(true);
		rbOn.setVisible(false);
		rbOn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rbOn_mouseClicked(e);
			}
		});
		rbOn.setBounds(41, 53, 52, 17);
		grbWriteValues.add(rbOn);

		rbOff = new JRadioButton("OFF");
		rbOff.setVisible(false);
		rbOff.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rbOff_mouseClicked(e);
			}
		});
		rbOff.setBounds(179, 53, 49, 17);
		grbWriteValues.add(rbOff);

		ButtonGroup btnGroupOnOFF = new ButtonGroup();
		btnGroupOnOFF.add(rbOn);
		btnGroupOnOFF.add(rbOff);

		txtSingleValues = new JTextField();
		txtSingleValues.setVisible(false);
		txtSingleValues.setText("0");
		txtSingleValues.setBounds(41, 53, 275, 20);
		grbWriteValues.add(txtSingleValues);
		txtSingleValues.setColumns(10);

		lblMode = new JLabel("read / write mode");
		lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMode.setBounds(2, 15, 116, 14);
		grpAddress.add(lblMode);

		rbRead = new JRadioButton("read on PLC");
		rbRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbRead_addActionListener(e);
			}
		});
		rbRead.setSelected(true);
		rbRead.setBounds(122, 11, 85, 23);
		grpAddress.add(rbRead);

		rbWrite = new JRadioButton("write to plc");
		rbWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbWrite_addActionListener(e);
			}
		});
		rbWrite.setBounds(208, 11, 112, 23);
		grpAddress.add(rbWrite);

		lblDB = new JLabel("<html>DB (only for DB, <br>use 0 otherwise)</html>");
		lblDB.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDB.setBounds(2, 88, 116, 32);
		grpAddress.add(lblDB);

		txtDB = new JTextField();
		txtDB.setText("1");
		txtDB.setColumns(10);
		txtDB.setBounds(123, 92, 175, 20);
		grpAddress.add(txtDB);

		cmbCharSet = new JComboBox<Charset>();
		cmbCharSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbCharSet_actionPerformed(e);
			}
		});
		cmbCharSet.setEnabled(false);
		cmbCharSet.setBounds(123, 240, 175, 21);
		grpAddress.add(cmbCharSet);

		JLabel lblCharSet = new JLabel("charset");
		lblCharSet.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCharSet.setBounds(0, 243, 116, 14);
		grpAddress.add(lblCharSet);

		lblFactor = new JLabel(" x charset");
		lblFactor.setHorizontalAlignment(SwingConstants.LEFT);
		lblFactor.setForeground(Color.BLUE);
		lblFactor.setEnabled(false);
		lblFactor.setBounds(178, 216, 58, 14);
		grpAddress.add(lblFactor);

		txtFactor = new JTextField();
		txtFactor.setText("1");
		txtFactor.setForeground(Color.BLUE);
		txtFactor.setEnabled(false);
		txtFactor.setColumns(10);
		txtFactor.setBounds(256, 213, 42, 20);
		grpAddress.add(txtFactor);
		txtSingleValues.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtSingleValues_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtSingleValues_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtSingleValues_TextChanged(e);
			}
		});

		grpAction = new JPanel();
		grpAction.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				ResourceBundle.getBundle("example_app.resources").getString("grpAction_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(12, 342, 656, 157);
		getContentPane().add(grpAction);
		grpAction.setLayout(null);

		panAction = new DisabledJPanel(grpAction);

		btnExecute = new JButton("<html><center>execute</center><center>function</center></html>");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnExecute_actionPerformed(e);
			}
		});
		btnExecute
				.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/btnExecute.Image.png")));
		btnExecute.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExecute.setToolTipText("");
		btnExecute.setMargin(new Insets(0, 0, 0, 0));
		btnExecute.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExecute.setBounds(17, 19, 68, 68);
		grpAction.add(btnExecute);
		panAction.setBounds(grpAction.getBounds());
		panAction.setDisabledColor(new Color(240, 240, 240, 100));
		panAction.setEnabled(true);
		getContentPane().add(panAction);

		btnSaveLogtoClipboard = new JButton("<html><center>copy log to</center><center>clipboard</center></html>");
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e);
			}
		});
		btnSaveLogtoClipboard.setIcon(new ImageIcon(
				ReadWriteBox.class.getResource("/example_app/btnSaveLogtoClipboard.Image.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(30, 510, 68, 68);
		getContentPane().add(btnSaveLogtoClipboard);

		btnSaveLogtoFile = new JButton("<html><center>save log to</center><center>file</center></html>");
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_actionPerformed(e);
			}
		});
		btnSaveLogtoFile.setIcon(
				new ImageIcon(ReadWriteBox.class.getResource("/example_app/btnSaveLogtoFile.Image.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(30, 585, 68, 68);
		getContentPane().add(btnSaveLogtoFile);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(596, 662, 68, 68);
		getContentPane().add(btnClose);

		// ############### begin init lvValues #####################
		lvValues = new JTable();
		lvValues.setShowHorizontalLines(false);
		lvValues.setShowVerticalLines(false);
		lvValues.setShowGrid(false);
		lvValues.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvValues.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Position", "Values" }) {
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

		// set renderer for colored rows
		lvValues.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvValues.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvValues.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		// lvValues.setDefaultRenderer(String.class, centerRenderer);
		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(20);
		lvValues.getColumnModel().getColumn(1).setResizable(true);
		lvValues.getColumnModel().getColumn(1).setPreferredWidth(308);

		lvValues.setBounds(100, 19, 550, 140);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.CENTER);

		lvValuesContainer.setBounds(new Rectangle(100, 19, 550, 127));
		grpAction.add(lvValuesContainer);

		// ############### end init lvValues #####################
		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setShowVerticalLines(false);
		lvLog.setShowHorizontalLines(false);
		lvLog.setShowGrid(false);
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "LogLevel", "Timestamp", "Text" }) {
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
		DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) lvLog.getTableHeader()
				.getDefaultRenderer();
		headerRenderer.setHorizontalAlignment(JLabel.LEFT);

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

		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(lvLog.getTableHeader(), BorderLayout.PAGE_START);
		lvLogContainer.add(scrollPanelvLog, BorderLayout.CENTER);

		lvLogContainer.setBounds(new Rectangle(113, 510, 550, 142));
		getContentPane().add(lvLogContainer);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(SystemColor.info);
		panel.setBounds(187, 6, 472, 59);
		getContentPane().add(panel);
		panel.setLayout(null);

		txtInfoRB = new JTextPane();
		txtInfoRB.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoRB.setBounds(60, 2, 402, 48);
		panel.add(txtInfoRB);
		txtInfoRB.setBorder(null);
		txtInfoRB.setText(
				"You can execute single read and write processes on this window. For optimized read processes please use the ReadDataRequestCollection.");
		txtInfoRB.setEditable(false);
		txtInfoRB.setBackground(SystemColor.info);

		label = new JLabel();
		label.setIcon(new ImageIcon(ReadWriteBox.class.getResource("/example_app/pictureBox1.Image.png")));
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(2, 2, 32, 32);
		panel.add(label);

		lblLogo = new JLabel();
		lblLogo.setVerticalAlignment(SwingConstants.TOP);
		lblLogo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLogo.setBounds(16, 4, 130, 60);
		ImageIcon originalIcon = new ImageIcon(
				Main.class.getResource("/example_app/indi.logo2021.1_rgb_PLCcom_130_60.png"));
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(scaledImage));
		getContentPane().add(lblLogo);

		lblLog = new JLabel("diagnostic output");
		lblLog.setHorizontalAlignment(SwingConstants.LEFT);
		lblLog.setBounds(113, 485, 116, 14);
		getContentPane().add(lblLog);

	}

	protected void formWindowClosing() {
		Main.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {
		try {

			// fill combobox with enum values
			cmbDataType.setModel(new DefaultComboBoxModel<eDataType>(eDataType.values()));
			cmbDataType.setSelectedItem(eDataType.BYTE);

			cmbRegion.setModel(new DefaultComboBoxModel<eRegion>(eRegion.values()));

			// fill cmbCharSet
			Charset[] c = Charset.availableCharsets().values()
					.toArray(new Charset[Charset.availableCharsets().values().size()]);
			cmbCharSet.setModel(new DefaultComboBoxModel<Charset>(c));
			cmbCharSet.setSelectedItem(Charset.forName("US-ASCII"));

			this.lblLog.setText(resources.getString("lblLog_Text"));
			this.grpAddress.setBorder(
					new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
							TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			this.grpAction.setBorder(
					new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAction_Text"),
							TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			this.lblDataType.setText(resources.getString("lblDataType_Text"));
			this.lblReadAddress.setText(resources.getString("lblReadAddress_Text"));
			this.lblWriteAddress.setText(resources.getString("lblWriteAddress_Text"));
			this.lblBit.setText(resources.getString("lblBit_Text"));
			this.lblQuantity.setText(resources.getString("lblLength_Text"));
			this.btnClose.setText(resources.getString("btnClose_Text"));
			this.btnExecute.setText(resources.getString("btnExecute_Text"));
			this.btnSaveLogtoClipboard.setText(resources.getString("btnSaveLogtoClipboard_Text"));
			this.btnSaveLogtoFile.setText(resources.getString("btnSaveLogtoFile_Text"));
			this.txtInfoRB.setText(resources.getString("txtInfoRB_OR_Text"));
			this.chkSingleValue.setText(resources.getString("chkSingleValue_Text"));
			this.lblEnterValues.setText(resources.getString("lblValues_Text"));
			this.lblRegion.setText(resources.getString("lblRegion_Text"));
			this.lblMode.setText(resources.getString("lblMode_Text"));
			this.rbRead.setText(resources.getString("rbRead_Text"));
			this.rbWrite.setText(resources.getString("rbWrite_Text"));

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cmbRegion_actionPerformed(ActionEvent arg) {
		try {
			// enable or disable DB field
			txtDB.setEnabled(((eRegion) cmbRegion.getSelectedItem()).equals(eRegion.DataBlock));

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

	private void cmbCharSet_actionPerformed(ActionEvent e) {
		try {
			// get byte length of one character from desired charset
			txtFactor.setText(String.valueOf(((Charset) cmbCharSet.getSelectedItem()).encode("0").limit()));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnExecute_actionPerformed(ActionEvent e) {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (rbRead.isSelected()) {
				// execute read
				ExecRead();
			} else {
				// execute write
				ExecWrite();
			}
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void ExecRead() {
		try {

			// @formatter:off

			// declare a ReadDataRequest object and
			// set the request parameters
			ReadDataRequest myReadDataRequest = new ReadDataRequest((eRegion) cmbRegion.getSelectedItem(), // Region
					Integer.valueOf(txtDB.getText()), /* DB only for datablock operations otherwise 0 */
					Integer.valueOf(txtReadAddress.getText()), /* read start adress */
					(eDataType) cmbDataType.getSelectedItem(), /* desired datatype */
					Integer.valueOf(txtQuantity.getText()) * Integer.valueOf(txtFactor.getText()), /*
																									 * Quantity of
																									 * reading values
																									 */
					Byte.valueOf(txtBit.getText()), /* Bit / only for bit operations */
					(Charset) cmbCharSet.getSelectedItem() // Optionally the Encoding for eventual string operations
			);

			// @formatter:on
			// read from device
			ReadDataResult res = Device.readData(myReadDataRequest);

			// starting evaluate results
			// set diagnostic output
			clearTable(lvLog);

			// add summary log entry
			addRowToTable(lvLog,
					new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							"Summary: " + res.toString() });
			addRowToTable(lvLog,
					new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							"Message: " + res.getMessage() });

			// add log entrys
			for (LogEntry le : res.getDiagnosticLog()) {
				addRowToTable(lvLog,
						new Object[] { le.getLogLevel().toString(),
								DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
								le.getSender() + le.getText() + " " + le.getStackTraceString() });

			}

			// evaluate values
			clearTable(lvValues);

			lvValues.getColumnModel().getColumn(0).setHeaderValue("Position");
			if (res.getQuality() == OperationResult.eQuality.GOOD) {

				int Position = 0;
				for (Object item : res.getValues()) {
					addRowToTable(lvValues,
							new Object[] { String.valueOf(Position++), String.valueOf(item.toString()) });
				}
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void ExecWrite() {
		try {
			// parse valuestring and add writable Data here
			Utilities.sValues_to_Write vtw = null;
			vtw = Utilities.CheckValues(ValuetoWrite, eDataType.valueOf(cmbDataType.getSelectedItem().toString()));

			if (!vtw.ParseError) {
				// last warning
				if (JOptionPane.showConfirmDialog(null,
						resources.getString("Continue_Warning_Write") + System.getProperty("line.separator")
								+ resources.getString("Continue_Question"),
						resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

					// @formatter:off

					// declare a WriteRequest object and
					// set the request parameters
					// WriteDataRequest myWriteRequest = new
					// WriteDataRequest((eRegion) cmbRegion.getSelectedItem(),
					// // Region
					// Integer.valueOf(txtDB.getText()), // DB / only for data
					// block operations operations otherwise 0
					// Integer.valueOf(txtWriteAddress.getText()), // write
					// start address
					// Byte.valueOf(txtBit.getText())); // Bit/only for bit
					// operations

					WriteDataRequest myWriteRequest = new WriteDataRequest((eRegion) cmbRegion.getSelectedItem(), // Region
							Integer.valueOf(txtDB.getText()), // DB / only for
																// data block
																// operations
																// operations
																// otherwise 0
							Integer.valueOf(txtWriteAddress.getText()), //write start adress
							(Charset) cmbCharSet.getSelectedItem() // Optionally the Encoding for eventual string operations
							);


                    //allow writing multliple bits
                    //Important hint: If multiple bits are present, this WriteRequest can not be processed optimally.
                    //All bits are written one after the other.
                    //It is better and more efficient if a separate WriteRequest is used for each bit.
                    myWriteRequest.setAllowMultipleBits (true);

					// @formatter:on

					// add writable data to request
					for (Object writevalue : vtw.values) {
						switch ((eDataType) cmbDataType.getSelectedItem()) {
						case BIT:
							myWriteRequest.addBit((Boolean) writevalue);
							break;
						case BYTE:
							myWriteRequest.addByte((Byte) writevalue);
							break;
						case INT:
							myWriteRequest.addInt((Short) writevalue);
							break;
						case DINT:
							myWriteRequest.addDInt((Integer) writevalue);
							break;
						case LINT:
							myWriteRequest.addLInt((Long) writevalue);
							break;
						case WORD:
							myWriteRequest.addWord((UShort) writevalue);
							break;
						case DWORD:
							myWriteRequest.addDWord((UInteger) writevalue);
							break;
						case LWORD:
							myWriteRequest.addLWord((ULong) writevalue);
							break;
						case REAL:
							myWriteRequest.addReal((Float) writevalue);
							break;
						case LREAL:
							myWriteRequest.addLReal((Double) writevalue);
							break;
						case RAWDATA:
							myWriteRequest.addByte((Byte) writevalue);
							break;
						case BCD8:
							myWriteRequest.addBCD8((Byte) writevalue);
							break;
						case BCD16:
							myWriteRequest.addBCD16((Short) writevalue);
							break;
						case BCD32:
							myWriteRequest.addBCD32((Integer) writevalue);
							break;
						case BCD64:
							myWriteRequest.addBCD64((Long) writevalue);
							break;
						case DATETIME:
						case DATE_AND_TIME:
							myWriteRequest.addDATE_AND_TIME((Instant) writevalue);
							break;
						case LDATE_AND_TIME:
							myWriteRequest.addLDATE_AND_TIME((Instant) writevalue);
							break;
						case DTL:
							myWriteRequest.addDTL((Instant) writevalue);
							break;
						case S5TIME:
							myWriteRequest.addS5TIME((Long) writevalue);
							break;
						case TIME_OF_DAY:
							myWriteRequest.addTIME_OF_DAY((LocalTime) writevalue);
							break;
						case LTIME_OF_DAY:
							myWriteRequest.addLTIME_OF_DAY((LocalTime) writevalue);
							break;
						case TIME:
							myWriteRequest.addTIME((Duration) writevalue);
							break;
						case LTIME:
							myWriteRequest.addLTIME((Duration) writevalue);
							break;
						case DATE:
							myWriteRequest.addDATE((LocalDate) writevalue);
							break;
						case STRING:
							myWriteRequest.addString((String) writevalue);
							break;
						case S7STRING:
							myWriteRequest.addS7String((String) writevalue);
							break;
						case S7WSTRING:
							myWriteRequest.addS7WString((String) writevalue);
							break;
						default:
							// abort message
							JOptionPane.showMessageDialog(null, resources.getString("wrong_datatype") + " "
									+ resources.getString("operation_aborted"), "", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					// write
					WriteDataResult res = Device.writeData(myWriteRequest);

					// evaluate results
					// set diagnostic output
					clearTable(lvLog);

					// add summary log entry
					addRowToTable(lvLog, new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							" Message: " + res.getMessage() });

					// add log entrys
					for (LogEntry le : res.getDiagnosticLog()) {
						addRowToTable(lvLog,
								new Object[] { le.getLogLevel().toString(),
										DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
										le.getText() + " " + le.getStackTraceString() });

					}
				} else {
					// abort message
					JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				// parse error message
				clearTable(lvLog);

				// add log entry
				addRowToTable(lvLog,
						new Object[] { eLogLevel.Error.toString(),
								DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
								resources.getString("ParseError") });

				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void chkSingleValue_mouseClicked(MouseEvent arg) {
		// switch between txtMultipleValues and txtSingleValues
		if (((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.BIT)) {
			txtSingleValues.setVisible(false);
			rbOn.setVisible(chkSingleValue.isSelected());
			rbOff.setVisible(chkSingleValue.isSelected());
			scrollMultipleBoolValues.setVisible(!chkSingleValue.isSelected());
			txtMultipleBoolValues.setVisible(!chkSingleValue.isSelected());
			txtMultipleNumericValues.setVisible(false);
			ValuetoWrite = chkSingleValue.isSelected() ? String.valueOf(rbOn.isSelected())
					: txtMultipleBoolValues.getText();
		} else {
			txtSingleValues.setVisible(chkSingleValue.isSelected());
			rbOn.setVisible(false);
			rbOff.setVisible(false);
			scrollMultipleNumericValues.setVisible(!chkSingleValue.isSelected());
			txtMultipleNumericValues.setVisible(!chkSingleValue.isSelected());
			txtMultipleBoolValues.setVisible(false);
			ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText() : txtMultipleNumericValues.getText();
		}
		lblEnterValues.setVisible(true);
		this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
				: resources.getString("lblMultipleValues_Text"));

	}

	private void txtMultipleNumericValues_TextChanged(DocumentEvent e) {
		// set writable string
		ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText() : txtMultipleNumericValues.getText();
	}

	private void txtMultipleBoolValues_TextChanged(DocumentEvent e) {
		// set writable string
		ValuetoWrite = chkSingleValue.isSelected() ? String.valueOf(rbOn.isSelected())
				: txtMultipleBoolValues.getText();
	}

	private void txtSingleValues_TextChanged(DocumentEvent e) {
		// set writable string
		ValuetoWrite = txtSingleValues.getText();
	}

	private void rbOn_mouseClicked(MouseEvent e) {
		// set writable string
		ValuetoWrite = String.valueOf(rbOn.isSelected());
	}

	private void rbOff_mouseClicked(MouseEvent e) {
		// set writable string
		ValuetoWrite = String.valueOf(rbOn.isSelected());
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

	private void cmbDataType_actionPerformed(ActionEvent e) {
		// switch byteorder depending on the selected function
		if (((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.BIT)) {
			txtBit.setEnabled(true);
			txtSingleValues.setVisible(false);
			rbOn.setVisible(chkSingleValue.isSelected());
			rbOff.setVisible(chkSingleValue.isSelected());
			scrollMultipleBoolValues.setVisible(!chkSingleValue.isSelected());
			txtMultipleBoolValues.setVisible(!chkSingleValue.isSelected());
			scrollMultipleNumericValues.setVisible(false);
			txtMultipleNumericValues.setVisible(false);
			ValuetoWrite = chkSingleValue.isSelected() ? String.valueOf(rbOn.isSelected())
					: txtMultipleBoolValues.getText();
			txtBit.setEnabled(true);
		} else {
			txtBit.setEnabled(false);
			txtSingleValues.setVisible(chkSingleValue.isSelected());
			rbOn.setVisible(false);
			rbOff.setVisible(false);
			scrollMultipleNumericValues.setVisible(!chkSingleValue.isSelected());
			txtMultipleNumericValues.setVisible(!chkSingleValue.isSelected());
			scrollMultipleBoolValues.setVisible(false);
			txtMultipleBoolValues.setVisible(false);
			ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText() : txtMultipleNumericValues.getText();
			txtBit.setEnabled(false);
		}
		lblEnterValues.setVisible(true);
		this.lblEnterValues.setText(chkSingleValue.isSelected() ? resources.getString("lblValues_Text")
				: resources.getString("lblMultipleValues_Text"));

		this.cmbCharSet.setEnabled(((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.STRING)
				|| ((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.S7STRING));

		if (((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.STRING)
				|| ((eDataType) cmbDataType.getSelectedItem()).equals(eDataType.S7STRING)) {
			this.txtFactor.setEnabled(true);
			this.lblFactor.setEnabled(true);
		} else {
			this.txtFactor.setEnabled(false);
			this.lblFactor.setEnabled(false);
			this.cmbCharSet.setSelectedItem(Charset.forName("US-ASCII"));
		}

	}

	private void rbRead_addActionListener(ActionEvent e) {
		rbWrite.setSelected(!rbRead.isSelected());

		// enable or disable input fields
		panWriteValues.setEnabled(!rbRead.isSelected());
		lvValues.setEnabled(rbRead.isSelected());
		txtQuantity.setEnabled(rbRead.isSelected());

	}

	private void rbWrite_addActionListener(ActionEvent e) {
		rbRead.setSelected(!rbWrite.isSelected());

		// enable or disable input fields
		panWriteValues.setEnabled(!rbRead.isSelected());
		lvValues.setEnabled(rbRead.isSelected());
		txtQuantity.setEnabled(rbRead.isSelected());
	}

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
