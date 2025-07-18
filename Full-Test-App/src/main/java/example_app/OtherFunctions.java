package example_app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.indian.plccom.fors7.BasicInfoResult;
import com.indian.plccom.fors7.CPUModeInfoResult;
import com.indian.plccom.fors7.DiagnosticInfoEntry;
import com.indian.plccom.fors7.DiagnosticInfoResult;
import com.indian.plccom.fors7.LEDInfoResult;
import com.indian.plccom.fors7.LogEntry;
import com.indian.plccom.fors7.OperationResult;
import com.indian.plccom.fors7.PLCClockTimeResult;
import com.indian.plccom.fors7.PLCcomCoreDevice;
import com.indian.plccom.fors7.SystemStatusListItemEntry;
import com.indian.plccom.fors7.SystemStatusListResult;
import com.indian.plccom.fors7.eLogLevel;

import example_app.OtherFunctionsInputBox.eOtherFunctionOpenMode;

public class OtherFunctions extends JFrame {

	private static final long serialVersionUID = 1L;
	private PLCcomCoreDevice _device;
	private ResourceBundle _resources;

	private JPanel contentPane;
	private JTable lvLog;
	private JTable lvValues;
	private JButton btnStartPLC;
	private JButton btnGetPLCTime;
	private JButton btnsetPLCTime;
	private JButton btnBasicInfo;
	private JButton btnCPUMode;
	private JButton btnPLCLEDInfo;
	private JButton btnReadSSL_SZL;
	private JButton btnDiagnoseBuffer;
	private JButton btnStopPLC;
	private JButton btnSaveLogtoClipboard;
	private JButton btnSaveLogtoFile;
	private JButton btnClose;
	private JLabel lblLog;
	private JTextPane txtInfoOF;
	private JPanel grpAction;

	/**
	 * Create the frame.
	 */
	public OtherFunctions(PLCcomCoreDevice device, ResourceBundle rb) {
		this._resources = rb;
		this._device = device;
		initialize();
	}

	public void initialize() {

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

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(OtherFunctions.class.getResource("/example_app/btnOtherFunctions.Image.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(10, 10, 713, 836);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(SystemColor.info);
		panel.setBounds(187, 8, 472, 59);
		contentPane.add(panel);

		txtInfoOF = new JTextPane();
		txtInfoOF.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoOF.setText(
				"In this window, you can start and stop the PLC. In addition, you can reading basic data from hardware, get and set PLC-clock and retrieve diagnostic data.");
		txtInfoOF.setEditable(false);
		txtInfoOF.setBorder(null);
		txtInfoOF.setBackground(SystemColor.info);
		txtInfoOF.setBounds(60, 2, 402, 48);
		panel.add(txtInfoOF);

		JLabel lblInfo = new JLabel();
		lblInfo.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/pictureBox1.Image.png")));
		lblInfo.setVerticalAlignment(SwingConstants.TOP);
		lblInfo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblInfo.setBounds(2, 2, 32, 32);
		panel.add(lblInfo);

		JLabel lblLogo = new JLabel();
		lblLogo.setVerticalAlignment(SwingConstants.TOP);
		lblLogo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLogo.setBounds(16, 4, 130, 60);
		ImageIcon originalIcon = new ImageIcon(
				Main.class.getResource("/example_app/indi.logo2021.1_rgb_PLCcom_130_60.png"));
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(scaledImage));
		contentPane.add(lblLogo);

		grpAction = new JPanel();
		grpAction.setLayout(null);
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Action", TitledBorder.LEADING,

				TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(7, 69, 680, 472);
		contentPane.add(grpAction);

		// ############### begin init lvValues #####################
		lvValues = new JTable();
		lvValues.setShowHorizontalLines(false);
		lvValues.setShowVerticalLines(false);
		lvValues.setShowGrid(false);
		lvValues.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lvValues.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lvValues.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvValues.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Values" }) {
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
		lvValues.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvValues.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvValues.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		// lvValues.setDefaultRenderer(String.class, centerRenderer);
		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(1000);

		lvValues.setBounds(100, 19, 550, 140);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.SOUTH);

		lvValuesContainer.setBounds(new Rectangle(180, 19, 470, 443));
		grpAction.add(lvValuesContainer);

		// ############### end init lvValues #####################

		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setShowHorizontalLines(false);
		lvLog.setShowGrid(false);
		lvLog.setShowVerticalLines(false);
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "LogLevel", "Timestamp", "Text" }) {
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

		lvLogContainer.setBounds(new Rectangle(186, 559, 472, 142));
		getContentPane().add(lvLogContainer);

		btnStartPLC = new JButton("start PLC");
		btnStartPLC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStartPLC_actionPerformed(e);
			}
		});
		btnStartPLC.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnStartPLC.Image.png")));
		btnStartPLC.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnStartPLC.setToolTipText("");
		btnStartPLC.setMargin(new Insets(0, 0, 0, 0));
		btnStartPLC.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStartPLC.setBounds(15, 19, 68, 68);
		grpAction.add(btnStartPLC);

		btnGetPLCTime = new JButton("<html><center>get PLC</center><center>time</center></html>");
		btnGetPLCTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnGetPLCTime_addActionListener(e);
			}
		});
		btnGetPLCTime.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnGetPLCTime.Image.png")));
		btnGetPLCTime.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnGetPLCTime.setToolTipText("");
		btnGetPLCTime.setMargin(new Insets(0, 0, 0, 0));
		btnGetPLCTime.setHorizontalTextPosition(SwingConstants.CENTER);
		btnGetPLCTime.setBounds(15, 165, 68, 68);
		grpAction.add(btnGetPLCTime);

		btnsetPLCTime = new JButton("<html><center>set PLC</center><center>time</center></html>");
		btnsetPLCTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnsetPLCTime_actionPerformed(e);
			}
		});
		btnsetPLCTime.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnsetPLCTime.Image.png")));
		btnsetPLCTime.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnsetPLCTime.setToolTipText("");
		btnsetPLCTime.setMargin(new Insets(0, 0, 0, 0));
		btnsetPLCTime.setHorizontalTextPosition(SwingConstants.CENTER);
		btnsetPLCTime.setBounds(93, 165, 68, 68);
		grpAction.add(btnsetPLCTime);

		btnBasicInfo = new JButton("<html><center>get PLC</center><center>basic Info</center></html>");
		btnBasicInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBasicInfo_actionPerformed(e);
			}
		});
		btnBasicInfo.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnBasicInfo.Image.png")));
		btnBasicInfo.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBasicInfo.setToolTipText("");
		btnBasicInfo.setMargin(new Insets(0, 0, 0, 0));
		btnBasicInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBasicInfo.setBounds(93, 244, 68, 68);
		grpAction.add(btnBasicInfo);

		btnCPUMode = new JButton("<html><center>get CPU</center><center>mode</center></html>");
		btnCPUMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCPUMode_actionPerformed(e);
			}
		});
		btnCPUMode.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnCPUMode.Image.png")));
		btnCPUMode.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnCPUMode.setToolTipText("");
		btnCPUMode.setMargin(new Insets(0, 0, 0, 0));
		btnCPUMode.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCPUMode.setBounds(15, 92, 68, 68);
		grpAction.add(btnCPUMode);

		btnPLCLEDInfo = new JButton("<html><center>get PLC</center><center>LED Info</center></html>");
		btnPLCLEDInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPLCLEDInfo_actionPerformed(e);
			}
		});
		btnPLCLEDInfo.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnPLCLEDInfo.Image.png")));
		btnPLCLEDInfo.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnPLCLEDInfo.setToolTipText("");
		btnPLCLEDInfo.setMargin(new Insets(0, 0, 0, 0));
		btnPLCLEDInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		btnPLCLEDInfo.setBounds(15, 244, 68, 68);
		grpAction.add(btnPLCLEDInfo);

		btnReadSSL_SZL = new JButton("<html><center>read</center><center>SSL/SZL</center></html>");
		btnReadSSL_SZL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadSSL_SZL_actionPerformed(e);
			}
		});
		btnReadSSL_SZL.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnReadSSL_SZL.Image.png")));
		btnReadSSL_SZL.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReadSSL_SZL.setToolTipText("");
		btnReadSSL_SZL.setMargin(new Insets(0, 0, 0, 0));
		btnReadSSL_SZL.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReadSSL_SZL.setBounds(15, 319, 68, 68);
		grpAction.add(btnReadSSL_SZL);

		btnDiagnoseBuffer = new JButton("<html><center>diagnostic</center><center>data</center></html>");
		btnDiagnoseBuffer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDiagnoseBuffer_actionPerformed(e);
			}
		});
		btnDiagnoseBuffer.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnDiagnoseBuffer.Image.png")));
		btnDiagnoseBuffer.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnDiagnoseBuffer.setToolTipText("");
		btnDiagnoseBuffer.setMargin(new Insets(0, 0, 0, 0));
		btnDiagnoseBuffer.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDiagnoseBuffer.setBounds(15, 394, 68, 68);
		grpAction.add(btnDiagnoseBuffer);

		btnStopPLC = new JButton("stop PLC");
		btnStopPLC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStopPLC_actionPerformed(e);
			}
		});
		btnStopPLC.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnStopPLC.Image.png")));
		btnStopPLC.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnStopPLC.setToolTipText("");
		btnStopPLC.setMargin(new Insets(0, 0, 0, 0));
		btnStopPLC.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStopPLC.setBounds(93, 19, 68, 68);
		grpAction.add(btnStopPLC);

		btnSaveLogtoClipboard = new JButton("<html><center>copy log to</center><center>clipboard</center></html>");
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e);
			}
		});
		btnSaveLogtoClipboard.setIcon(new ImageIcon(
				OtherFunctions.class.getResource("/example_app/btnSaveLogtoClipboard.Image.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(25, 558, 68, 68);
		contentPane.add(btnSaveLogtoClipboard);

		btnSaveLogtoFile = new JButton("<html><center>save log to</center><center>file</center></html>");
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_actionPerformed(e);
			}
		});
		btnSaveLogtoFile.setIcon(
				new ImageIcon(OtherFunctions.class.getResource("/example_app/btnSaveLogtoFile.Image.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(25, 633, 68, 68);
		contentPane.add(btnSaveLogtoFile);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(OtherFunctions.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(591, 712, 68, 68);
		contentPane.add(btnClose);

		lblLog = new JLabel("diagnostic output");
		lblLog.setHorizontalAlignment(SwingConstants.LEFT);
		lblLog.setBounds(187, 543, 116, 14);
		contentPane.add(lblLog);
	}

	private void formWindowOpened(WindowEvent arg0) {

		this.btnStartPLC.setText(_resources.getString("btnStartPLC_Text"));
		this.btnStopPLC.setText(_resources.getString("btnStopPLC_Text"));
		this.btnGetPLCTime.setText(_resources.getString("btnGetPLCTime_Text"));
		this.btnsetPLCTime.setText(_resources.getString("btnsetPLCTime_Text"));
		this.btnBasicInfo.setText(_resources.getString("btnBasicInfo_Text"));
		this.btnCPUMode.setText(_resources.getString("btnCPUMode_Text"));
		this.btnPLCLEDInfo.setText(_resources.getString("btnPLCLEDInfo_Text"));
		this.btnReadSSL_SZL.setText(_resources.getString("btnReadSSL_SZL_Text"));
		this.btnDiagnoseBuffer.setText(_resources.getString("btnDiagnoseBuffer_Text"));
		this.lblLog.setText(_resources.getString("lblLog_Text"));
		this.grpAction.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), _resources.getString("grpAction_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.btnClose.setText(_resources.getString("btnClose_Text"));
		this.txtInfoOF.setText(_resources.getString("txtInfoOF_Text"));
	}

	private void formWindowClosing(WindowEvent e) {
		Main.CountOpenDialogs--;
	}

	private void btnStartPLC_actionPerformed(ActionEvent e) {
		try {
			if (JOptionPane.showConfirmDialog(null,
					_resources.getString("Continue_Warning_Start") + System.getProperty("line.separator")
							+ _resources.getString("Continue_Question"),
					_resources.getString("Important_question"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

				// execute function
				OperationResult res = _device.startPLC();

				// starting evaluate results
				// set diagnostic output
				clearTable(lvLog);

				// add summary log entry
				addRowToTable(lvLog, new Object[] {
						res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						"Summary: " + res.toString() });
				addRowToTable(lvLog, new Object[] {
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
				if (res.getQuality() == OperationResult.eQuality.GOOD) {

					addRowToTable(lvValues, new Object[] { String.valueOf(String.valueOf(res.getMessage())) });

				}

			} else {
				JOptionPane.showMessageDialog(this, _resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnStopPLC_actionPerformed(ActionEvent e) {
		try {
			if (JOptionPane.showConfirmDialog(null,
					_resources.getString("Continue_Warning_Stop") + System.getProperty("line.separator")
							+ _resources.getString("Continue_Question"),
					_resources.getString("Important_question"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				// execute function
				OperationResult res = _device.stopPLC();

				// starting evaluate results
				// set diagnostic output
				clearTable(lvLog);

				// add summary log entry
				addRowToTable(lvLog, new Object[] {
						res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						"Summary: " + res.toString() });
				addRowToTable(lvLog, new Object[] {
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

				if (res.getQuality() == OperationResult.eQuality.GOOD) {

					addRowToTable(lvValues, new Object[] { String.valueOf(String.valueOf(res.getMessage())) });

				}
			} else {
				JOptionPane.showMessageDialog(this, _resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnGetPLCTime_addActionListener(ActionEvent e) {
		try {
			// execute function
			PLCClockTimeResult res = _device.getPLCClockTime();

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

			if (res.getQuality() == OperationResult.eQuality.GOOD) {

				// format date and time
				SimpleDateFormat sdf = new SimpleDateFormat(_resources.getString("DateTimeFormatString"));
				addRowToTable(lvValues, new Object[] { sdf.format(Date.from(res.getPLCClockTime())) });

			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnsetPLCTime_actionPerformed(ActionEvent e) {
		try {

			OtherFunctionsInputBox ofb = new OtherFunctionsInputBox(eOtherFunctionOpenMode.Date_and_Time);
			ofb.setVisible(true);
			if (!ofb.isCancel) {
				if (JOptionPane.showConfirmDialog(null,
						_resources.getString("Continue_Warning_SetTime") + System.getProperty("line.separator")
								+ _resources.getString("Continue_Question"),
						_resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

					// execute function
					OperationResult res = _device.setPLCClockTime(ofb.actDateTime);

					// starting evaluate results
					// set diagnostic output
					clearTable(lvLog);

					// add summary log entry
					addRowToTable(lvLog, new Object[] {
							res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
									: eLogLevel.Error.toString(),
							DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
							"Summary: " + res.toString() });
					addRowToTable(lvLog, new Object[] {
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

					if (res.getQuality() == OperationResult.eQuality.GOOD) {

						addRowToTable(lvValues, new Object[] { String.valueOf(String.valueOf(res.getMessage())) });

					}

				} else {
					JOptionPane.showMessageDialog(this, _resources.getString("operation_aborted"), "",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, _resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnBasicInfo_actionPerformed(ActionEvent e) {
		try {
			// execute function
			BasicInfoResult res = _device.getBasicInfo();

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

			if (res.getQuality() == OperationResult.eQuality.GOOD) {

				addRowToTable(lvValues, new Object[] { "Device Name: " + res.getName() });
				addRowToTable(lvValues, new Object[] { "Order Number: " + res.getOrdernumber() });
				addRowToTable(lvValues, new Object[] { "Module Version: " + res.getModuleVersion() });
				addRowToTable(lvValues, new Object[] { "Firmware Version: " + res.getFirmwareVersion() });

			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnCPUMode_actionPerformed(ActionEvent e) {
		try {
			// execute function
			CPUModeInfoResult res = _device.getCPUMode();

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

			if (res.getQuality() == OperationResult.eQuality.GOOD) {

				addRowToTable(lvValues, new Object[] { "CPU Mode: " + res.getCPUModeInfo() });
				addRowToTable(lvValues, new Object[] { "CPU State: " + res.getCPUStateInfo() });

			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnPLCLEDInfo_actionPerformed(ActionEvent e) {
		try {
			// execute function
			LEDInfoResult res = _device.getLEDInfo();

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

			if (res.getQuality() == OperationResult.eQuality.GOOD) {

				for (LEDInfoResult.sLEDInfo LEDInfo : res.getLEDInfo()) {
					addRowToTable(lvValues, new Object[] { "LED: " + String.valueOf(LEDInfo.identifier) + " State: "
							+ (LEDInfo.state?"On ": "Off ") + " isFlashing: " + String.valueOf(LEDInfo.flashing) });
				}

			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnReadSSL_SZL_actionPerformed(ActionEvent e) {
		try {
			// important!!! please search the id and index information in the
			// plc-documentation
			// You must convert the specified values hex in decimal
			OtherFunctionsInputBox ofb = new OtherFunctionsInputBox(eOtherFunctionOpenMode.SSL_SZL);
			ofb.setVisible(true);
			if (!ofb.isCancel) {

				// execute function
				SystemStatusListResult res = _device.getSystemStatusList(ofb.SSL_ID, ofb.SSL_Index);

				// starting evaluate results
				// set diagnostic output
				clearTable(lvLog);

				// add summary log entry
				addRowToTable(lvLog, new Object[] {
						res.getQuality().equals(OperationResult.eQuality.GOOD) ? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(),
						DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
						"Summary: " + res.toString() });
				addRowToTable(lvLog, new Object[] {
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

				if (res.getQuality() == OperationResult.eQuality.GOOD) {
					for (SystemStatusListItemEntry ssle : res.getSZLItemEntrys()) {
						for (byte b : ssle.getBuffer()) {
							addRowToTable(lvValues, new Object[] { String.valueOf(b) + " " });
						}

					}
				}
			} else {
				JOptionPane.showMessageDialog(this, _resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnDiagnoseBuffer_actionPerformed(ActionEvent e) {
		try {
			// read the diagnosticinfo into DiagnosticInfoResult-object
			// execute function
			DiagnosticInfoResult res = _device.getDiagnosticInfo();

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

			if (res.getQuality() == OperationResult.eQuality.GOOD) {
				for (DiagnosticInfoEntry myDiagnosticInfoEntry : res.getDiagnosticInfoEntrys()) {
					StringBuilder sb = new StringBuilder();
					sb.append(_resources.getString("Timestamp") + ": "
							+ myDiagnosticInfoEntry.getDiagnosticTimestamp().toString());
					sb.append(" ");
					sb.append("ID: " + String.valueOf(myDiagnosticInfoEntry.getDiagnosticID()));
					sb.append(" ");
					sb.append(_resources.getString("Message") + ": " + myDiagnosticInfoEntry.getDiagnosticText());

					addRowToTable(lvValues, new Object[] { sb.toString() });
				}
			}

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
						this, _resources.getString("successfully_saved") + System.getProperty("line.separator")
								+ "File: " + dr.getSelectedFile().getAbsolutePath(),
						"", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, _resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	private void btnClose_actionPerformed(ActionEvent e) {
		try {
			this.setVisible(false);

			// send form closing event
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
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
