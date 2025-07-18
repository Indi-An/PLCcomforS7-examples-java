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
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

import com.indian.plccom.fors7.LogEntry;
import com.indian.plccom.fors7.OperationResult;
import com.indian.plccom.fors7.PLCcomDevice;
import com.indian.plccom.fors7.ReadDataRequest;
import com.indian.plccom.fors7.ReadDataResult;
import com.indian.plccom.fors7.ReadWriteRequestSet;
import com.indian.plccom.fors7.ReadWriteResultSet;
import com.indian.plccom.fors7.WriteDataRequest;
import com.indian.plccom.fors7.WriteDataResult;
import com.indian.plccom.fors7.eLogLevel;
import com.indian.plccom.fors7.eOperationOrder;
import com.indian.plccom.fors7.eReadOptimizationMode;
import com.indian.plccom.fors7.eWriteOptimizationMode;

import example_app.DisabledJPanel.DisabledJPanel;

public class OptimizedReadWriteBox extends JFrame {

	private static final long serialVersionUID = 1L;
	private PLCcomDevice Device;
	private ResourceBundle resources;
	private ReadWriteRequestSet RequestSet = new ReadWriteRequestSet();
	@SuppressWarnings("unused")
	private CreateRequestInputBox RequestInputbox;

	private JPanel grpAddress;
	private DisabledJPanel panAddress;
	private JPanel grpAction;
	private DisabledJPanel panAction;
	private JTable lvRequests;
	private JTable lvLog;
	private JTable lvValues;
	private JButton btnAddRequest;
	private JButton btnRemoveRequest;
	private JButton btnExecute;
	private JButton btnSaveLogtoClipboard;
	private JButton btnSaveLogtoFile;
	private JButton btnClose;
	private JLabel lblLog;
	private JTextPane txtInfoRCB;
	private JLabel lblReadOptimizationMode;
	private JLabel lblWriteOptimizationMode;
	private JLabel lblOperationOrder;
	private JComboBox<eReadOptimizationMode> cmbReadOptimizeMode;
	private JComboBox<eWriteOptimizationMode> cmbWriteOptimizeMode;
	private JComboBox<eOperationOrder> cmbOperationOrder;
	private JButton btnHelpReadOptMode;
	private JButton btnHelpWriteOptMode;

	/**
	 * Create the dialog.
	 */
	public OptimizedReadWriteBox(PLCcomDevice Device, ResourceBundle rb) {
		this.resources = rb;
		setTitle("OptimizedReadWriteBox");
		initialize();
		this.Device = Device;
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

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				OptimizedReadWriteBox.class.getResource("/example_app/btnReadCollection.Image.png")));
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

		this.setBounds(15, 10, 810, 801);

		this.getContentPane().setLayout(null);

		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(
				new TitledBorder(null, "add read request", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.grpAddress.setBounds(12, 68, 782, 174);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		panAddress = new DisabledJPanel(grpAddress);
		panAddress.setBounds(grpAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		grpAction = new JPanel();
		grpAction.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		grpAction.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				ResourceBundle.getBundle("resources").getString("grpAction_Text"),
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grpAction.setBounds(12, 253, 782, 243);
		getContentPane().add(grpAction);
		grpAction.setLayout(null);

		panAction = new DisabledJPanel(grpAction);

		btnExecute = new JButton("execute");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReadCollection_actionPerformed(e);
			}
		});
		btnExecute.setIcon(
				new ImageIcon(OptimizedReadWriteBox.class.getResource("/example_app/btnExecute.Image.png")));
		btnExecute.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnExecute.setToolTipText("");
		btnExecute.setMargin(new Insets(0, 0, 0, 0));
		btnExecute.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExecute.setBounds(18, 97, 68, 68);
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
				OptimizedReadWriteBox.class.getResource("/example_app/btnSaveLogtoClipboard.Image.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setToolTipText("");
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(25, 524, 68, 68);
		getContentPane().add(btnSaveLogtoClipboard);

		btnSaveLogtoFile = new JButton("<html><center>save log to</center><center>file</center></html>");
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
		btnSaveLogtoFile.setBounds(25, 598, 68, 68);
		getContentPane().add(btnSaveLogtoFile);

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
		btnClose.setBounds(714, 677, 68, 68);
		getContentPane().add(btnClose);

		// ############### begin init lvRequests #####################

		lvRequests = new JTable();
		lvRequests.setShowVerticalLines(false);
		lvRequests.setShowHorizontalLines(false);
		lvRequests.setShowGrid(false);
		lvRequests.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvRequests.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvRequests.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "RequestUUID", "Request" }) {
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
		lvRequests.getColumnModel().getColumn(0).setMinWidth(227);
		lvRequests.getColumnModel().getColumn(0).setPreferredWidth(227);
		lvRequests.getColumnModel().getColumn(0).setMaxWidth(227);
		lvRequests.getColumnModel().getColumn(1).setResizable(true);
		lvRequests.getColumnModel().getColumn(1).setPreferredWidth(408);

		lvRequests.setBounds(new Rectangle(0, 0, 662, 140));

		lvRequests.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lvRequests_SelectedIndexChanged(e);
			}
		});

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvRequests.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.LEFT);

		JPanel lvListenerContainer = new JPanel();
		JScrollPane scrollPanelvListener = new JScrollPane(lvRequests);
		lvRequests.setFillsViewportHeight(true);

		lvListenerContainer.setLayout(new BorderLayout());
		lvListenerContainer.add(lvRequests.getTableHeader(), BorderLayout.PAGE_START);
		scrollPanelvListener.setBounds(lvListenerContainer.getBounds());
		lvListenerContainer.add(scrollPanelvListener, BorderLayout.NORTH);

		lvListenerContainer.setBounds(new Rectangle(101, 20, 671, 140));
		grpAddress.add(lvListenerContainer);

		// ############### end init lvListener #####################

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
		btnAddRequest.setBounds(18, 20, 68, 68);
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
		btnRemoveRequest.setBounds(18, 92, 68, 68);
		grpAddress.add(btnRemoveRequest);

		// ############### begin init lvValues #####################
		lvValues = new JTable();
		lvValues.setShowVerticalLines(false);
		lvValues.setShowHorizontalLines(false);
		lvValues.setShowGrid(false);
		lvValues.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvValues.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "", "Results" }) {
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

		// set renderer for colored rows
		lvValues.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvValues.getDefaultRenderer(Object.class)));

		// set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvValues.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

		lvValues.getColumnModel().getColumn(0).setResizable(true);
		lvValues.getColumnModel().getColumn(0).setPreferredWidth(0);
		lvValues.getColumnModel().getColumn(0).setMinWidth(0);
		lvValues.getColumnModel().getColumn(0).setMaxWidth(0);
		lvValues.getColumnModel().getColumn(1).setResizable(true);
		lvValues.getColumnModel().getColumn(1).setPreferredWidth(308);

		lvValues.setBounds(100, 19, 550, 140);

		JScrollPane scrollPanelvValues = new JScrollPane(lvValues);
		lvValues.setFillsViewportHeight(true);

		JPanel lvValuesContainer = new JPanel();

		lvValuesContainer.setLayout(new BorderLayout());
		lvValuesContainer.add(lvValues.getTableHeader(), BorderLayout.PAGE_START);
		lvValuesContainer.add(scrollPanelvValues, BorderLayout.CENTER);

		lvValuesContainer.setBounds(new Rectangle(96, 97, 676, 140));
		grpAction.add(lvValuesContainer);

		lblReadOptimizationMode = new JLabel("<html><center>Read</center><center>optimization</center></html>");
		lblReadOptimizationMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblReadOptimizationMode.setBounds(10, 13, 81, 28);
		grpAction.add(lblReadOptimizationMode);

		cmbReadOptimizeMode = new JComboBox<eReadOptimizationMode>();
		cmbReadOptimizeMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbReadOptimizeMode_actionPerformed(e);
			}
		});
		cmbReadOptimizeMode.setBounds(99, 11, 246, 21);
		grpAction.add(cmbReadOptimizeMode);

		lblWriteOptimizationMode = new JLabel("<html><center>Write</center><center>optimization</center></html>");
		lblWriteOptimizationMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWriteOptimizationMode.setBounds(10, 43, 81, 28);
		grpAction.add(lblWriteOptimizationMode);

		cmbWriteOptimizeMode = new JComboBox<eWriteOptimizationMode>();
		cmbWriteOptimizeMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWriteOptimizeMode_actionPerformed(e);
			}
		});
		cmbWriteOptimizeMode.setBounds(99, 41, 246, 21);
		grpAction.add(cmbWriteOptimizeMode);

		lblOperationOrder = new JLabel("operation order");
		lblOperationOrder.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOperationOrder.setBounds(10, 72, 81, 14);
		grpAction.add(lblOperationOrder);

		cmbOperationOrder = new JComboBox<eOperationOrder>();
		cmbOperationOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbOperationOrder_actionPerformed(e);
			}
		});
		cmbOperationOrder.setBounds(99, 70, 246, 21);
		grpAction.add(cmbOperationOrder);

		btnHelpReadOptMode = new JButton("");
		btnHelpReadOptMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnHelpReadOptMode_actionPerformed(e);
			}
		});
		btnHelpReadOptMode.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnHelpReadOptMode.Image.png")));
		btnHelpReadOptMode.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnHelpReadOptMode.setToolTipText("");
		btnHelpReadOptMode.setMargin(new Insets(0, 0, 0, 0));
		btnHelpReadOptMode.setHorizontalTextPosition(SwingConstants.CENTER);
		btnHelpReadOptMode.setBounds(352, 10, 24, 24);
		grpAction.add(btnHelpReadOptMode);

		btnHelpWriteOptMode = new JButton("");
		btnHelpWriteOptMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnHelpWriteOptMode_actionPerformed(e);
			}
		});
		btnHelpWriteOptMode.setIcon(new ImageIcon(
				OptimizedReadWriteBox.class.getResource("/example_app/btnHelpWriteOptMode.Image.png")));
		btnHelpWriteOptMode.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnHelpWriteOptMode.setToolTipText("");
		btnHelpWriteOptMode.setMargin(new Insets(0, 0, 0, 0));
		btnHelpWriteOptMode.setHorizontalTextPosition(SwingConstants.CENTER);
		btnHelpWriteOptMode.setBounds(352, 36, 24, 24);
		grpAction.add(btnHelpWriteOptMode);

		// ############### end init lvValues #####################
		// ############### begin init lvLog #####################
		lvLog = new JTable();
		lvLog.setShowVerticalLines(false);
		lvLog.setShowHorizontalLines(false);
		lvLog.setShowGrid(false);
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "", "Text" }) {
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
		lvLog.getColumnModel().getColumn(0).setPreferredWidth(0);
		lvLog.getColumnModel().getColumn(0).setMinWidth(0);
		lvLog.getColumnModel().getColumn(0).setMaxWidth(0);
		lvLog.getColumnModel().getColumn(1).setResizable(true);
		lvLog.getColumnModel().getColumn(1).setPreferredWidth(6000);
		lvLog.setBounds(112, 449, 550, 142);
		lvLog.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		lvLog.setAutoscrolls(true);

		JScrollPane scrollPanelvLog = new JScrollPane(lvLog);
		lvLog.setFillsViewportHeight(true);

		JPanel lvLogContainer = new JPanel();

		lvLogContainer.setLayout(new BorderLayout());
		lvLogContainer.add(lvLog.getTableHeader(), BorderLayout.PAGE_START);
		lvLogContainer.add(scrollPanelvLog, BorderLayout.CENTER);

		lvLogContainer.setBounds(new Rectangle(106, 524, 676, 142));
		getContentPane().add(lvLogContainer);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		panel.setBackground(SystemColor.info);
		panel.setBounds(196, 4, 598, 59);
		getContentPane().add(panel);

		txtInfoRCB = new JTextPane();
		txtInfoRCB.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoRCB.setText(
				"ReadDataRequestCollection: Read processes will be assembled and optimized due to minimize the necessary PLC accesses.");
		txtInfoRCB.setEditable(false);
		txtInfoRCB.setBorder(null);
		txtInfoRCB.setBackground(SystemColor.info);
		txtInfoRCB.setBounds(66, 2, 522, 50);
		panel.add(txtInfoRCB);

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

		lblLog = new JLabel("diagnostic output");
		lblLog.setHorizontalAlignment(SwingConstants.LEFT);
		lblLog.setBounds(107, 507, 116, 14);
		getContentPane().add(lblLog);

	}

	protected void cmbReadOptimizeMode_actionPerformed(ActionEvent e) {
		RequestSet.setReadOptimizationMode((eReadOptimizationMode) cmbReadOptimizeMode.getSelectedItem());
	}

	protected void cmbWriteOptimizeMode_actionPerformed(ActionEvent e) {
		RequestSet.setWriteOptimizationMode((eWriteOptimizationMode) cmbWriteOptimizeMode.getSelectedItem());
	}

	protected void cmbOperationOrder_actionPerformed(ActionEvent e) {
		// write opertion order to RequestSet
		RequestSet.setOperationOrder((eOperationOrder) cmbOperationOrder.getSelectedItem());
	}

	protected void btnHelpReadOptMode_actionPerformed(ActionEvent e) {

		// show help message
		JOptionPane.showMessageDialog(this, resources.getString("Hint_Read_OptimizationMode_Text"),
				resources.getString("Hint_Read_OptimizationMode_Overview"), JOptionPane.INFORMATION_MESSAGE);
	}

	protected void btnHelpWriteOptMode_actionPerformed(ActionEvent e) {

		// show help message
		JOptionPane.showMessageDialog(this, resources.getString("Hint_Write_OptimizationMode_Text"),
				resources.getString("Hint_Write_OptimizationMode_Overview"), JOptionPane.INFORMATION_MESSAGE);
	}

	protected void formWindowClosing(WindowEvent e) {
		Main.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {

		RequestInputbox = new CreateRequestInputBox(true, resources);

		// fill comboboxes
		cmbReadOptimizeMode.setModel(new DefaultComboBoxModel<eReadOptimizationMode>(eReadOptimizationMode.values()));
		cmbReadOptimizeMode.setSelectedItem(eReadOptimizationMode.NONE);

		cmbWriteOptimizeMode
				.setModel(new DefaultComboBoxModel<eWriteOptimizationMode>(eWriteOptimizationMode.values()));
		cmbWriteOptimizeMode.setSelectedItem(eWriteOptimizationMode.NONE);

		cmbOperationOrder.setModel(new DefaultComboBoxModel<eOperationOrder>(eOperationOrder.values()));
		cmbOperationOrder.setSelectedItem(eOperationOrder.WRITE_BEVOR_READ);

		// set ressources
		this.lblLog.setText(resources.getString("lblLog_Text"));
		this.grpAddress.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAction.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAction_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.btnClose.setText(resources.getString("btnClose_Text"));
		this.btnAddRequest.setText(resources.getString("btnAddRequest_Text"));
		this.btnRemoveRequest.setText(resources.getString("btnRemoveRequest_Text"));
		this.btnExecute.setText(resources.getString("btnExecute_Text"));
		this.txtInfoRCB.setText(resources.getString("txtInfoRCB_Text"));
		this.btnSaveLogtoClipboard.setText(resources.getString("btnSaveLogtoClipboard_Text"));
		this.btnSaveLogtoFile.setText(resources.getString("btnSaveLogtoFile_Text"));
		this.lblReadOptimizationMode.setText(resources.getString("lblReadOptimizationMode_Text"));
		this.lblWriteOptimizationMode.setText(resources.getString("lblWriteOptimizationMode_Text"));
		this.lblOperationOrder.setText(resources.getString("lblOperationOrder_Text"));

	}

	private void btnAddRequest_actionPerformed(ActionEvent arg) {

		try {

			RequestInputbox = new CreateRequestInputBox(true, resources);
			RequestInputbox.showDialog();

			if (RequestInputbox.Result == JOptionPane.OK_OPTION && RequestInputbox.RequestItem != null) {
				// add new request to request collection
				if (RequestInputbox.RequestItem instanceof ReadDataRequest)
					RequestSet.addRequest((ReadDataRequest) RequestInputbox.RequestItem);
				else if (RequestInputbox.RequestItem instanceof WriteDataRequest)
					RequestSet.addRequest((WriteDataRequest) RequestInputbox.RequestItem);

				// update UI controls
				fillRequestListView();
			}



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
				RequestSet.removeRequest(lvRequests.getModel().getValueAt(lvRequests.getSelectedRow(), 0).toString());
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

			// fill ListView with current ReadDataRequests and WriteDataRequests
			switch (RequestSet.getOperationOrder()) {
			case WRITE_BEVOR_READ:
				for (WriteDataRequest rr : RequestSet.getWriteDataRequests()) {
					model.addRow(new Object[] { rr.getRequestUUID(), rr.toString() });
				}
				for (ReadDataRequest rr : RequestSet.getReadDataRequests()) {
					model.addRow(new Object[] { rr.getRequestUUID(), rr.toString() });
				}
				break;

			case READ_BEVOR_WRITE:
				for (ReadDataRequest rr : RequestSet.getReadDataRequests()) {
					model.addRow(new Object[] { rr.getRequestUUID(), rr.toString() });
				}
				for (WriteDataRequest rr : RequestSet.getWriteDataRequests()) {
					model.addRow(new Object[] { rr.getRequestUUID(), rr.toString() });
				}
				break;
			}

			model.fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	private void btnReadCollection_actionPerformed(ActionEvent e) {
		try {

			// read from device
			ReadWriteResultSet ResultSet = Device.readWriteData(RequestSet);

			// clear lvValues
			clearTable(lvValues);

			// set diagnostic output
			clearTable(lvLog);

			// evaluate write results
			for (WriteDataResult res : ResultSet.getWriteDataResults()) {

				StringBuilder sb = new StringBuilder();
				addRowToTable(lvValues,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "Start Result:" });
				sb.append(res.toString());
				sb.append(System.getProperty("line.separator"));

				addRowToTable(lvValues,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), sb.toString() });

				sb = new StringBuilder();
				addRowToTable(lvValues,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "End Result:" });

				addRowToTable(lvValues, new Object[] { " ", sb.toString() });

				// inserting empty row
				addRowToTable(lvValues, new Object[] { " ", " " });

				// getting request diagnostic logs
				addRowToTable(lvLog,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "Begin diagnostic log" });

				addRowToTable(lvLog,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), res.toString() });

				for (LogEntry le : res.getDiagnosticLog()) {
					addRowToTable(lvLog,
							new Object[] { le.getLogLevel().toString(),
									DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()) + " "
											+ le.getSender() + le.getText() + " " + le.getStackTraceString() });

				}

				addRowToTable(lvLog,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "End diagnostic log" });

				// inserting empty row
				addRowToTable(lvLog, new Object[] { " ", " " });

			}

			// evaluate read results
			for (ReadDataResult res : ResultSet.getReadDataResults()) {

				addRowToTable(lvValues,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "Start Result:" });

				addRowToTable(lvValues,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), res.toString() });

				if (res.getQuality().equals(OperationResult.eQuality.GOOD)) {
					int counter = 0;

					// create format string for position-flag
					String FormatString = "%0" + String.valueOf(String.valueOf(res.getValues().length).length()) + "d";

					for (Object item : res.getValues()) {
						StringBuilder sb = new StringBuilder();
						sb.append(res.getDataType().toString());
						sb.append(": ");
						sb.append(String.format(FormatString, counter++));
						sb.append(" => Value: ");
						sb.append(String.valueOf(item));
						addRowToTable(lvValues,
								new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
										? eLogLevel.Information.toString()
										: eLogLevel.Error.toString(), sb.toString() });
					}
				}

				addRowToTable(lvValues,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "End Result" });

				// inserting empty row
				addRowToTable(lvValues, new Object[] { " ", " " });

				// getting request diagnostic logs
				addRowToTable(lvLog,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "Begin diagnostic log" });

				addRowToTable(lvLog,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), res.toString() });

				for (LogEntry le : res.getDiagnosticLog()) {
					addRowToTable(lvLog,
							new Object[] { le.getLogLevel().toString(),
									DateFormat.getDateTimeInstance().format(le.getTimeStamp().getTime()) + " "
											+ le.getSender() + le.getText() + " " + le.getStackTraceString() });

				}

				addRowToTable(lvLog,
						new Object[] { res.getQuality().equals(OperationResult.eQuality.GOOD)
								? eLogLevel.Information.toString()
								: eLogLevel.Error.toString(), "End diagnostic log" });

				// inserting empty row
				addRowToTable(lvLog, new Object[] { " ", " " });

			}
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
