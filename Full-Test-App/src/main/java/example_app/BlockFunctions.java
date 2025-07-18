package example_app;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.indian.plccom.fors7.*;

import javax.swing.JFileChooser;

import example_app.BlockFunctionsInputBox.eBlockFunctionOpenMode;

import java.awt.Rectangle;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BlockFunctions extends JFrame {

	private static final long serialVersionUID = 1L;
	private PLCcomDevice Device;
	private ResourceBundle resources;

	private JPanel contentPane;
	private JTextPane txtInfoBF;
	private JPanel grpAction;
	private JTable lvValues;
	private JPanel panel;
	private JLabel lblInfo;
	private JLabel lblLogo;
	private JTable lvLog;
	private JButton btnsendPW;
	private JButton btnBlockList;
	private JButton btnBlockLen;
	private JButton btnBackup_Block;
	private JButton btnRestore_Block;
	private JButton btnDeleteBlock;
	private JButton btnBackupPLC;
	private JButton btnRestorePLC;
	private JLabel lblLog;
	private JButton btnSaveLogtoClipboard;
	private JButton btnSaveLogtoFile;
	private JButton btnClose;

	/**
	 * Create the frame.
	 */
	public BlockFunctions(PLCcomDevice Device, ResourceBundle rb) {
		this.resources = rb;
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
				.getImage(BlockFunctions.class.getResource("/example_app/btnBlockFunctions.Image.png")));
		setTitle("BlockFunctions");
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 10, 693, 776);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblLogo = new JLabel();
		lblLogo.setVerticalAlignment(SwingConstants.TOP);
		lblLogo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLogo.setBounds(16, 4, 130, 60);
		ImageIcon originalIcon = new ImageIcon(
				Main.class.getResource("/example_app/indi.logo2021.1_rgb_PLCcom_130_60.png"));
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(scaledImage));
		contentPane.add(lblLogo);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		panel.setBackground(SystemColor.info);
		panel.setBounds(187, 8, 472, 59);
		contentPane.add(panel);

		txtInfoBF = new JTextPane();
		txtInfoBF.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoBF.setText(
				"In this window you can read the object list, call up the length of individual objects. \r\nIn addition, functions for backup and restore are available.");
		txtInfoBF.setEditable(false);
		txtInfoBF.setBorder(null);
		txtInfoBF.setBackground(SystemColor.info);
		txtInfoBF.setBounds(66, 2, 396, 50);
		panel.add(txtInfoBF);

		lblInfo = new JLabel();
		lblInfo.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/pictureBox1.Image.png")));
		lblInfo.setVerticalAlignment(SwingConstants.TOP);
		lblInfo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblInfo.setBounds(2, 2, 32, 32);
		panel.add(lblInfo);

		grpAction = new JPanel();
		grpAction.setLayout(null);
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Action", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(10, 71, 656, 398);
		contentPane.add(grpAction);

		// ############### begin init lvValues #####################
		lvValues = new JTable();
		lvValues.setShowGrid(false);
		lvValues.setShowHorizontalLines(false);
		lvValues.setShowVerticalLines(false);
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
			Class[] columnTypes = new Class[] { String.class };

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});

		// set renderer for colored rows
		lvValues.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvValues.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvValues.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		//
		// lvValues.setDefaultRenderer(String.class, centerRenderer);
		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(320);

		lvValues.setBounds(100, 19, 550, 140);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.CENTER);

		lvValuesContainer.setBounds(new Rectangle(180, 19, 470, 368));
		grpAction.add(lvValuesContainer);

		// ############### end init lvValues #####################

		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setShowGrid(false);
		lvLog.setShowHorizontalLines(false);
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

		lvLogContainer.setBounds(new Rectangle(191, 501, 472, 142));
		getContentPane().add(lvLogContainer);

		btnsendPW = new JButton("<html><center>send</center><center>password</center></html>");
		btnsendPW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnsendPW_actionPerformed(e);
			}
		});
		btnsendPW
				.setIcon(new ImageIcon(BlockFunctions.class.getResource("/example_app/btnsendPW.Image.png")));
		btnsendPW.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnsendPW.setToolTipText("");
		btnsendPW.setMargin(new Insets(0, 0, 0, 0));
		btnsendPW.setHorizontalTextPosition(SwingConstants.CENTER);
		btnsendPW.setBounds(15, 19, 68, 68);
		grpAction.add(btnsendPW);

		btnBlockList = new JButton("<html><center>get PLC</center><center>block list</center></html>");
		btnBlockList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBlockList_actionPerformed(e);
			}

		});
		btnBlockList.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnBlockList.Image.png")));
		btnBlockList.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBlockList.setToolTipText("");
		btnBlockList.setMargin(new Insets(0, 0, 0, 0));
		btnBlockList.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBlockList.setBounds(15, 94, 68, 68);
		grpAction.add(btnBlockList);

		btnBlockLen = new JButton("<html><center>get PLC</center><center>block len</center></html>");
		btnBlockLen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBlockLen_actionPerformed(e);
			}
		});
		btnBlockLen.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnBlockLen.Image.png")));
		btnBlockLen.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBlockLen.setToolTipText("");
		btnBlockLen.setMargin(new Insets(0, 0, 0, 0));
		btnBlockLen.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBlockLen.setBounds(93, 94, 68, 68);
		grpAction.add(btnBlockLen);

		btnBackup_Block = new JButton("<html><center>backup</center><center>block</center></html>");
		btnBackup_Block.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBackup_Block_actionPerformed(e);
			}
		});
		btnBackup_Block.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnBackup_Block.Image.png")));
		btnBackup_Block.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBackup_Block.setToolTipText("");
		btnBackup_Block.setMargin(new Insets(0, 0, 0, 0));
		btnBackup_Block.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBackup_Block.setBounds(15, 169, 68, 68);
		grpAction.add(btnBackup_Block);

		btnRestore_Block = new JButton("<html><center>restore</center><center>block</center></html>");
		btnRestore_Block.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRestore_Block_actionPerformed(e);
			}
		});
		btnRestore_Block.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnRestore_Block.Image.png")));
		btnRestore_Block.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRestore_Block.setToolTipText("");
		btnRestore_Block.setMargin(new Insets(0, 0, 0, 0));
		btnRestore_Block.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRestore_Block.setBounds(93, 169, 68, 68);
		grpAction.add(btnRestore_Block);

		btnDeleteBlock = new JButton("<html><center>delete</center><center>block</center></html>");
		btnDeleteBlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDeleteBlock_actionPerformed(e);
			}
		});
		btnDeleteBlock.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnDeleteBlock.Image.png")));
		btnDeleteBlock.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnDeleteBlock.setToolTipText("");
		btnDeleteBlock.setMargin(new Insets(0, 0, 0, 0));
		btnDeleteBlock.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDeleteBlock.setBounds(15, 244, 68, 68);
		grpAction.add(btnDeleteBlock);

		btnBackupPLC = new JButton("<html><center>backup</center><center>PLC</center></html>");
		btnBackupPLC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnBackupPLC_actionPerformed(e);
			}
		});
		btnBackupPLC.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnBackupPLC.Image.png")));
		btnBackupPLC.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnBackupPLC.setToolTipText("");
		btnBackupPLC.setMargin(new Insets(0, 0, 0, 0));
		btnBackupPLC.setHorizontalTextPosition(SwingConstants.CENTER);
		btnBackupPLC.setBounds(15, 319, 68, 68);
		grpAction.add(btnBackupPLC);

		btnRestorePLC = new JButton("<html><center>restore</center><center>PLC</center></html>");
		btnRestorePLC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRestorePLC_addActionListener(e);
			}
		});
		btnRestorePLC.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnRestorePLC.Image.png")));
		btnRestorePLC.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRestorePLC.setToolTipText("");
		btnRestorePLC.setMargin(new Insets(0, 0, 0, 0));
		btnRestorePLC.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRestorePLC.setBounds(93, 319, 68, 68);
		grpAction.add(btnRestorePLC);

		lblLog = new JLabel("diagnostic output");
		lblLog.setHorizontalAlignment(SwingConstants.LEFT);
		lblLog.setBounds(192, 480, 116, 14);
		contentPane.add(lblLog);

		btnSaveLogtoClipboard = new JButton("<html><center>copy log to</center><center>clipboard</center></html>");
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e);
			}
		});
		btnSaveLogtoClipboard.setIcon(new ImageIcon(
				BlockFunctions.class.getResource("/example_app/btnSaveLogtoClipboard.Image.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(26, 493, 68, 68);
		contentPane.add(btnSaveLogtoClipboard);

		btnSaveLogtoFile = new JButton("<html><center>save log to</center><center>file</center></html>");
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_addActionListener(e);
			}
		});
		btnSaveLogtoFile.setIcon(
				new ImageIcon(BlockFunctions.class.getResource("/example_app/btnSaveLogtoFile.Image.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(26, 570, 68, 68);
		contentPane.add(btnSaveLogtoFile);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(BlockFunctions.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(599, 652, 68, 68);
		contentPane.add(btnClose);

	}

	protected void formWindowClosing(WindowEvent e) {
		Main.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent e) {

		this.btnsendPW.setText(resources.getString("btnsendPW_Text"));
		this.btnBlockList.setText(resources.getString("btnBlockList_Text"));
		this.btnBlockLen.setText(resources.getString("btnBlockLen_Text"));
		this.btnBackup_Block.setText(resources.getString("btnBackup_Block_Text"));
		this.btnRestore_Block.setText(resources.getString("btnRestore_Block_Text"));
		this.btnDeleteBlock.setText(resources.getString("btnDeleteBlock_Text"));
		this.grpAction.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAction_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.btnClose.setText(resources.getString("btnClose_Text"));
		this.lblLog.setText(resources.getString("lblLog_Text"));
		this.btnSaveLogtoClipboard.setText(resources.getString("btnSaveLogtoClipboard_Text"));
		this.btnSaveLogtoFile.setText(resources.getString("btnSaveLogtoFile_Text"));
		this.txtInfoBF.setText(resources.getString("txtInfoBF_Text"));
	}

	private void btnsendPW_actionPerformed(ActionEvent e) {
		try {

			BlockFunctionsInputBox bip = new BlockFunctionsInputBox(eBlockFunctionOpenMode.EnterPW, false);
			bip.setVisible(true);
			if (!bip.isCancel) {
				OperationResult res = Device.sendPassWord(bip.EnterPW);

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
				// clear lvValues model
				clearTable(lvValues);
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnBlockList_actionPerformed(ActionEvent e) {
		try {
			BlockFunctionsInputBox bip = new BlockFunctionsInputBox(eBlockFunctionOpenMode.Only_BlockType, true);
			bip.setVisible(true);
			if (!bip.isCancel) {
				BlockListResult res = Device.getBlockList(bip.BlockType);

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

					for (BlockListEntry item : res.getBlockList()) {
						addRowToTable(lvValues, new Object[] { String.valueOf(
								String.valueOf(item.getBlockType()) + String.valueOf(item.getBlockNumber())) });
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnBlockLen_actionPerformed(ActionEvent e) {
		try {
			BlockFunctionsInputBox bip = new BlockFunctionsInputBox(eBlockFunctionOpenMode.BlockType_and_BlockNumber,
					false);

			bip.setVisible(true);
			if (!bip.isCancel) {

				BlockListLengthResult res = Device.getBlockLenght(bip.BlockType, bip.BlockNumber);

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

					addRowToTable(lvValues, new Object[] { String.valueOf(String.valueOf(res.getBlockType())
							+ String.valueOf(res.getBlockNumber()) + " Len:" + res.getBlockLength()) });

				}
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnBackup_Block_actionPerformed(ActionEvent e) {
		try {
			BlockFunctionsInputBox bip = new BlockFunctionsInputBox(eBlockFunctionOpenMode.BlockType_and_BlockNumber,
					false);

			bip.setVisible(true);
			if (!bip.isCancel) {
				// open SaveFileDialog
				final JFileChooser dr = new JFileChooser(new File("."));
				FileFilter filter = new FileNameExtensionFilter("Binary Files *.mc7", "mc7");
				dr.addChoosableFileFilter(filter);
				int returnVal = dr.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					// read Block into ReadPLCBlockResult
					ReadPLCBlockResult res = Device.readPLCBlock_MC7(bip.BlockType, bip.BlockNumber);

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

					// clear lvValue model
					clearTable(lvValues);

					// evaluate values and write file
					if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
						try {
							// save buffer in specified file
							File file = new File(dr.getSelectedFile().getAbsolutePath());

							// rename file to .mc7, you can adjust the extension
							if (!file.getAbsolutePath().endsWith(".mc7")) {
								file = new File(dr.getSelectedFile().getAbsolutePath() + ".mc7");
							}
							// if file doesn�t exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}
							FileOutputStream fs = new FileOutputStream(file);
							fs.write(res.getBuffer());
							fs.close();
							JOptionPane.showMessageDialog(this,
									"Block " + String.valueOf(String.valueOf(bip.BlockType))
											+ String.valueOf(bip.BlockNumber) + resources.getString("successful_saved")
											+ dr.getSelectedFile().getAbsolutePath(),
									"", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(this, "operation unsuccessful", "",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void btnRestore_Block_actionPerformed(ActionEvent e) {

		// open SaveFileDialog
		final JFileChooser dr = new JFileChooser(new File("."));
		FileFilter filter = new FileNameExtensionFilter("Binary Files *.mc7", "mc7");
		dr.addChoosableFileFilter(filter);
		int returnVal = dr.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {
				InputStream is = null;
				byte[] buffer = null;
				try {
					is = new BufferedInputStream(new FileInputStream(dr.getSelectedFile().getAbsolutePath()));
					buffer = new byte[is.available()];
					is.read(buffer);
				} finally {
					if (is != null) {
						is.close();
					}
				}
				WritePLCBlockRequest Requestdata = new WritePLCBlockRequest(buffer);
				// Write Buffer into PLC
				OperationResult res = Device.writePLCBlock_MC7(Requestdata);

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

				// clear lvValue model
				clearTable(lvValues);

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "operation unsuccessful", "", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void btnDeleteBlock_actionPerformed(ActionEvent e) {
		try {
			BlockFunctionsInputBox bip = new BlockFunctionsInputBox(eBlockFunctionOpenMode.BlockType_and_BlockNumber,
					false);

			bip.setVisible(true);
			if (!bip.isCancel) {
				if (JOptionPane.showConfirmDialog(null,
						resources.getString("Continue_Warning_Restore") + System.getProperty("line.separator")
								+ resources.getString("Continue_Question"),
						resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					OperationResult res = Device.deleteBlock(bip.BlockType, bip.BlockNumber);

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

					// clear lvValue model
					clearTable(lvValues);

				} else {
					JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * void will saving a complete PLC data structur into a target folder
	 * 
	 * @param e
	 */
	private void btnBackupPLC_actionPerformed(ActionEvent e) {
		try {

			BlockFunctionsInputBox bip = new BlockFunctionsInputBox(eBlockFunctionOpenMode.Only_BlockType, true);

			bip.setVisible(true);
			if (!bip.isCancel) {
				BlockListResult res = Device.getBlockList(bip.BlockType);

				// open SaveFileDialog
				final JFileChooser dr = new JFileChooser(new File("."));
				dr.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = dr.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						// starting evaluate results
						// set diagnostic output
						clearTable(lvLog);

						// add summary log entry
						addRowToTable(lvLog,
								new Object[] {
										res.getQuality().equals(OperationResult.eQuality.GOOD)
												? eLogLevel.Information.toString()
												: eLogLevel.Error.toString(),
										DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
										"Summary: " + res.toString() });
						addRowToTable(lvLog,
								new Object[] {
										res.getQuality().equals(OperationResult.eQuality.GOOD)
												? eLogLevel.Information.toString()
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

						// clear lvValue model
						clearTable(lvValues);

						if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
							StringBuilder sb = new StringBuilder();
							sb.append(resources.getString("reading_blocklist"));
							for (BlockListEntry ble : res.getBlockList()) {
								sb = new StringBuilder();
								sb.append(String.valueOf(ble.getBlockType()));
								sb.append(String.valueOf(ble.getBlockNumber()));

								addRowToTable(lvValues, new Object[] { sb.toString() });
							}

							for (BlockListEntry ble : res.getBlockList()) {
								sb = new StringBuilder();
								sb.append(resources.getString("starting_backup"));
								sb.append(String.valueOf(ble.getBlockType()));
								sb.append(String.valueOf(ble.getBlockNumber()));

								// read Block into ReadPLCBlockResult
								ReadPLCBlockResult res1 = Device.readPLCBlock_MC7(ble.getBlockType(),
										ble.getBlockNumber());

								// evaluate results
								addRowToTable(lvValues, new Object[] {
										Calendar.getInstance().toString() + " Summary: " + res1.toString() });
								addRowToTable(lvValues, new Object[] {
										Calendar.getInstance().toString() + " Message: " + res1.getMessage() });

								if (res1.getQuality().equals(OperationResult.eQuality.GOOD)) {

									// save buffer in specified file
									try {
										// save buffer in specified file
										File file = new File(dr.getSelectedFile().getAbsolutePath()
												+ OSValidator.getFolderSeparator() + String.valueOf(ble.getBlockType())
												+ "_" + String.valueOf(ble.getBlockNumber()) + ".mc7");
										// if file doesn�t exists, then create
										// it
										if (!file.exists()) {
											file.createNewFile();
										}
										FileOutputStream fs = new FileOutputStream(file);
										fs.write(res1.getBuffer());
										fs.close();
										sb.append("Block " + String.valueOf(ble.getBlockType())
												+ String.valueOf(ble.getBlockNumber())
												+ resources.getString("successful_saved") + file.getAbsolutePath());
									} catch (IOException ex) {

										JOptionPane.showMessageDialog(this,
												ex.getClass().getName() + " " + ex.getMessage(), "",
												JOptionPane.ERROR_MESSAGE);

										JOptionPane.showMessageDialog(this, "operation unsuccessful", "",
												JOptionPane.ERROR_MESSAGE);
									}

									addRowToTable(lvValues, new Object[] { sb.toString() });
								} else {
									sb.append(res1.getQuality().toString());
									addRowToTable(lvValues, new Object[] { sb.toString() });
									break;
								}
							}

						} else {
							return;
						}
					} finally {
						this.setCursor(Cursor.getDefaultCursor());
					}
				} else {
					JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * void will restoring a complete PLC data structur from source folder
	 * 
	 * @param e
	 */
	private void btnRestorePLC_addActionListener(ActionEvent e) {
		try {
			final JFileChooser dr = new JFileChooser(new File("."));
			dr.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = dr.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (JOptionPane.showConfirmDialog(null,
						resources.getString("Continue_Warning_Restore") + System.getProperty("line.separator")
								+ resources.getString("Continue_Question"),
						resources.getString("Important_question"),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						// Clear old log
						clearTable(lvLog);

						// clear lvValue model
						clearTable(lvValues);

						File dir = new File(dr.getSelectedFile().getAbsolutePath());
						File[] files = dir.listFiles(new MyFileNameFilter(".mc7"));
						for (File file : files) {

							if (!file.exists()) {
								JOptionPane.showMessageDialog(this, "operation unsuccessful, file not exist", "",
										JOptionPane.ERROR_MESSAGE);
								return;
							}
							InputStream is = null;
							byte[] buffer = null;
							try {
								is = new BufferedInputStream(new FileInputStream(file));
								buffer = new byte[is.available()];
								is.read(buffer);
							} finally {
								if (is != null) {
									is.close();
								}
							}
							WritePLCBlockRequest Requestdata = new WritePLCBlockRequest(buffer);
							// Write Buffer into PLC
							OperationResult res = Device.writePLCBlock_MC7(Requestdata);

							// starting evaluate results
							// set diagnostic output
							// add log entrys

							for (LogEntry le : res.getDiagnosticLog()) {
								addRowToTable(lvLog,
										new Object[] { le.getLogLevel().toString(),
												DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()),
												le.getSender() + le.getText() + " " + le.getStackTraceString() });

							}

							if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
								addRowToTable(lvLog, new Object[] { eLogLevel.Information.toString(),
										DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
										"Block " + Requestdata.getBlockInfo().getHeader().getBlockType().toString()
												+ String.valueOf(
														Requestdata.getBlockInfo().getHeader().getBlockNumber())
												+ resources.getString("successful_saved_PLC") + file.getName() });
							} else if (res.getQuality()
									.equals(OperationResult.eQuality.BAD_NO_PERMISSION_TO_ACCESS_THE_OBJECT)) {
								addRowToTable(lvLog, new Object[] { eLogLevel.Information.toString(),
										DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
										"Block " + Requestdata.getBlockInfo().getHeader().getBlockType().toString()
												+ String.valueOf(
														Requestdata.getBlockInfo().getHeader().getBlockNumber())
												+ " unsuccessfull Quality" + res.getQuality().toString() + " "
												+ file.getName() });

							} else if (res.getQuality().equals(OperationResult.eQuality.BAD_DEVICE_IS_NOT_CONNECTED)) {

								addRowToTable(lvLog, new Object[] { eLogLevel.Information.toString(),
										DateFormat.getDateTimeInstance()
												.format(Calendar.getInstance().getTime().getTime()),
										"Block " + Requestdata.getBlockInfo().getHeader().getBlockType().toString()
												+ String.valueOf(
														Requestdata.getBlockInfo().getHeader().getBlockNumber())
												+ " unsuccessfull Quality" + res.getQuality().toString() + " "
												+ file.getName() });
								break;
							} else {

								addRowToTable(lvLog, new Object[] { eLogLevel.Information.toString(),
										DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()),
										"Block " + Requestdata.getBlockInfo().getHeader().getBlockType().toString()
												+ String.valueOf(
														Requestdata.getBlockInfo().getHeader().getBlockNumber())
												+ " unsuccessfull Quality" + res.getQuality().toString() + " "
												+ file.getName() });
							}
						}
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
								JOptionPane.ERROR_MESSAGE);

						JOptionPane.showMessageDialog(this, "operation unsuccessful", "", JOptionPane.ERROR_MESSAGE);
					} finally {
						this.setCursor(Cursor.getDefaultCursor());
					}

				} else {
					JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, resources.getString("operation_aborted"), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	// FileNameFilter implementation
	private static class MyFileNameFilter implements FilenameFilter {

		private String extension;

		MyFileNameFilter(String ext) {
			this.extension = ext.toLowerCase();
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(extension);
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

	private void btnSaveLogtoFile_addActionListener(ActionEvent e) {
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
