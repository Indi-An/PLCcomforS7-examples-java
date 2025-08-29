package example_app;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import example_app.DisabledJPanel.DisabledJPanel;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.indian.plccom.fors7.*;
import com.indian.plccom.fors7.AddressNode;
import com.indian.plccom.fors7.OperationResult;
import com.indian.plccom.fors7.PLCcomCoreDevice;
import com.indian.plccom.fors7.ReadSymbolicRequest;
import com.indian.plccom.fors7.ReadSymbolicResultSet;
import com.indian.plccom.fors7.VariableDetails;
import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.awt.Rectangle;
import javax.swing.border.LineBorder;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

/**
 * ReadWriteSymbolic is a JFrame-based UI for reading and writing symbolic variables on a PLC device.
 * It allows browsing the PLC address space, reading variable values, writing new values,
 * and viewing diagnostic logs. The UI is fully localized and supports clipboard and file export for logs.
 */
public class ReadWriteSymbolic extends JFrame {

	private static final long serialVersionUID = 1L;
	private SymbolicDevice mDevice; // The PLC device connection
	private ResourceBundle resources; // Resource bundle for localization
	@SuppressWarnings("unused")
	private CreateRequestInputBox RequestInputbox; // Input box for creating requests

	// UI components for address group, log, and controls
	private JPanel grpAddress;
	private DisabledJPanel panAddress;
	private JTable lvLog;
	private JButton btnSaveLogtoClipboard;
	private JButton btnSaveLogtoFile;
	private JButton btnClose;
	private JLabel lblLog;
	private JTextPane txtInfoSymbRCB;
	private JTree treePlcInventory;
	private JTextField txtFullVariableName;
	private JTextField txtDataType;
	private JCheckBox chIsReadable;
	private JCheckBox chIsWritable;
	private JCheckBox chIsArray;
	private JCheckBox chIsStruct;
	private JCheckBox chIsSubscribable;
	private JLabel lblNewLabel;
	private JLabel lblIsWritable;
	private JLabel lblIsArray;
	private JLabel lblIsStruct;

	private JLabel lblIsSubscribable;
	private JScrollPane valueScrollPane;
	private JTextArea txtValue;
	private JLabel lblValue;
	private JTextField txtQuality;
	private JLabel lblQuality;
	private JLabel lblResulttext;
	private JTextArea txtMessage;
	private JScrollPane resultScrollPane;
	private JButton btnWrite;

	/**
	 * Constructs the ReadWriteSymbolic dialog and initializes all UI components.
	 * @param Device The PLC device to connect to.
	 * @param rb ResourceBundle for localized UI texts.
	 */
	public ReadWriteSymbolic(PLCcomCoreDevice Device, ResourceBundle rb) {
		this.resources = rb;
		setTitle("ReadWriteSymbolic");
		initialize(); // Build the UI and set up all components
		this.mDevice = (SymbolicDevice) Device;
	}

	/**
	 * Initializes the UI components, layout, and event listeners for the dialog.
	 * Sets up the address tree, log table, and all controls.
	 */
	private void initialize() {
		// Set global look and feel platform independent and set default font
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
			// Optionally, you could set the Windows look and feel here
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}

		// Set window icon, size, and close operation
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(ReadWriteSymbolic.class.getResource("/example_app/btnReadCollection.Image.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// Add window listeners for open/close events
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0); // Set up UI texts and load address tree on open
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing(e); // Decrement dialog count on close
			}
		});

		this.setBounds(15, 10, 810, 845); // Set window size and position
		this.getContentPane().setLayout(null); // Use absolute positioning

		// --- Address Group Setup ---
		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "request",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAddress.setBounds(12, 68, 782, 461);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		panAddress = new DisabledJPanel(grpAddress);

		// Tree for PLC inventory (address space)
		treePlcInventory = new JTree();
		treePlcInventory.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				treePlcInventory_valueChanged(e); // Handle selection changes in the tree
			}
		});
		JScrollPane treeScroll = new JScrollPane(treePlcInventory); // Wrap JTree in a scroll pane
		treeScroll.setBounds(10, 21, 762, 142);
		grpAddress.add(treeScroll);

		// Labels and text fields for variable details
		JLabel lblDatatype = new JLabel("Datatype");
		lblDatatype.setBounds(10, 200, 95, 14);
		grpAddress.add(lblDatatype);

		JLabel lblVarName = new JLabel("Full Variable Name");
		lblVarName.setBounds(10, 176, 95, 14);
		grpAddress.add(lblVarName);

		txtFullVariableName = new JTextField();
		txtFullVariableName.setEditable(false);
		txtFullVariableName.setBounds(112, 174, 561, 20);
		grpAddress.add(txtFullVariableName);
		txtFullVariableName.setColumns(10);

		txtDataType = new JTextField();
		txtDataType.setEditable(false);
		txtDataType.setColumns(10);
		txtDataType.setBounds(112, 200, 561, 20);
		grpAddress.add(txtDataType);

		// Checkboxes for variable properties
		chIsReadable = new JCheckBox();
		chIsReadable.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsReadable.setBounds(751, 170, 21, 23);
		chIsReadable.setEnabled(false);
		grpAddress.add(chIsReadable);

		chIsWritable = new JCheckBox();
		chIsWritable.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsWritable.setBounds(751, 190, 21, 23);
		chIsWritable.setEnabled(false);
		grpAddress.add(chIsWritable);

		chIsArray = new JCheckBox();
		chIsArray.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsArray.setBounds(751, 231, 21, 23);
		chIsArray.setEnabled(false);
		grpAddress.add(chIsArray);

		chIsStruct = new JCheckBox();
		chIsStruct.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsStruct.setBounds(751, 251, 21, 23);
		chIsStruct.setEnabled(false);
		grpAddress.add(chIsStruct);


	    chIsSubscribable = new JCheckBox();
		chIsSubscribable.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsSubscribable.setEnabled(false);
		chIsSubscribable.setBounds(751, 210, 21, 23);
		grpAddress.add(chIsSubscribable);

		// Labels for checkboxes
		lblNewLabel = new JLabel("is readable");
		lblNewLabel.setBounds(683, 174, 69, 14);
		grpAddress.add(lblNewLabel);

		lblIsWritable = new JLabel("is writable");
		lblIsWritable.setBounds(683, 194, 69, 14);
		grpAddress.add(lblIsWritable);

		lblIsArray = new JLabel("is Array");
		lblIsArray.setBounds(683, 235, 69, 14);
		grpAddress.add(lblIsArray);

		lblIsStruct = new JLabel("is Struct");
		lblIsStruct.setBounds(683, 255, 69, 14);
		grpAddress.add(lblIsStruct);

	    lblIsSubscribable = new JLabel("is subscribable");
		lblIsSubscribable.setBounds(683, 214, 69, 14);
		grpAddress.add(lblIsSubscribable);

		// Text area for variable value
		txtValue = new JTextArea();
		txtValue.setEditable(false);
		valueScrollPane = new JScrollPane(txtValue);
		valueScrollPane.setBounds(112, 231, 561, 143);
		grpAddress.add(valueScrollPane);

		lblValue = new JLabel("Value");
		lblValue.setBounds(10, 235, 95, 14);
		grpAddress.add(lblValue);

		// Text field for quality
		txtQuality = new JTextField();
		txtQuality.setEditable(false);
		txtQuality.setColumns(10);
		txtQuality.setBounds(112, 385, 561, 20);
		txtQuality.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		grpAddress.add(txtQuality);

		lblQuality = new JLabel("Quality");
		lblQuality.setBounds(10, 388, 95, 14);
		grpAddress.add(lblQuality);

		lblResulttext = new JLabel("Resulttext");
		lblResulttext.setBounds(10, 423, 95, 14);
		grpAddress.add(lblResulttext);

		// Text area for result message
		txtMessage = new JTextArea();
		txtMessage.setEditable(false);
		txtMessage.setBorder(null);
		resultScrollPane = new JScrollPane(txtMessage);
		resultScrollPane.setBounds(112, 416, 561, 36);
		grpAddress.add(resultScrollPane);

		// Button to write value to PLC
		btnWrite = new JButton("Write");
		btnWrite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWrite_actionPerformed(e); // Handle write button click
			}
		});
		btnWrite.setBounds(695, 318, 67, 56);
		grpAddress.add(btnWrite);

		panAddress.setBounds(grpAddress.getBounds());
		panAddress.setDisabledColor(new Color(240, 240, 240, 100));
		panAddress.setEnabled(true);
		this.getContentPane().add(panAddress);

		// --- Log and Control Buttons ---
		btnSaveLogtoClipboard = new JButton("<html><center>copy log to</center><center>clipboard</center></html>");
		btnSaveLogtoClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoClipboard_actionPerformed(e); // Copy log to clipboard
			}
		});
		btnSaveLogtoClipboard.setIcon(new ImageIcon(
				ReadWriteSymbolic.class.getResource("/example_app/btnSaveLogtoClipboard.Image.png")));
		btnSaveLogtoClipboard.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoClipboard.setToolTipText("");
		btnSaveLogtoClipboard.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoClipboard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoClipboard.setBounds(25, 560, 68, 68);
		getContentPane().add(btnSaveLogtoClipboard);

		btnSaveLogtoFile = new JButton("<html><center>save log to</center><center>file</center></html>");
		btnSaveLogtoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSaveLogtoFile_actionPerformed(e); // Save log to file
			}
		});
		btnSaveLogtoFile.setIcon(new ImageIcon(
				ReadWriteSymbolic.class.getResource("/example_app/btnSaveLogtoFile.Image.png")));
		btnSaveLogtoFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSaveLogtoFile.setToolTipText("");
		btnSaveLogtoFile.setMargin(new Insets(0, 0, 0, 0));
		btnSaveLogtoFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSaveLogtoFile.setBounds(25, 628, 68, 68);
		getContentPane().add(btnSaveLogtoFile);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e); // Handle close button click
			}
		});
		btnClose.setIcon(
				new ImageIcon(ReadWriteSymbolic.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(716, 703, 68, 68);
		getContentPane().add(btnClose);

		// --- Log Table Setup ---
		lvLog = new JTable();
		lvLog.setShowVerticalLines(false);
		lvLog.setShowHorizontalLines(false);
		lvLog.setShowGrid(false);
		lvLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lvLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvLog.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "", "Text" }) {
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
		// Set cell renderer for colored rows
		lvLog.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvLog.getDefaultRenderer(Object.class)));
		// Set header renderer for horizontal alignment left
		((DefaultTableCellRenderer) lvLog.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
		// Set columns
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
		lvLogContainer.setBounds(new Rectangle(106, 560, 676, 136));
		getContentPane().add(lvLogContainer);

		// --- Info Panel and Logo ---
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		panel.setBackground(SystemColor.info);
		panel.setBounds(196, 4, 598, 59);
		getContentPane().add(panel);

		txtInfoSymbRCB = new JTextPane();
		txtInfoSymbRCB.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoSymbRCB.setText(
				"Read or write your symbolically defined variables");
		txtInfoSymbRCB.setEditable(false);
		txtInfoSymbRCB.setBorder(null);
		txtInfoSymbRCB.setBackground(SystemColor.info);
		txtInfoSymbRCB.setBounds(66, 2, 522, 50);
		panel.add(txtInfoSymbRCB);

		JLabel label = new JLabel();
		label.setIcon(
				new ImageIcon(ReadWriteSymbolic.class.getResource("/example_app/pictureBox1.Image.png")));
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
		lblLog.setBounds(106, 540, 116, 14);
		getContentPane().add(lblLog);

	}

	/**
	 * Handles the window closing event to update the open dialog count.
	 * @param e WindowEvent
	 */
	protected void formWindowClosing(WindowEvent e) {
		Main.CountOpenDialogs--;
	}

	/**
	 * Handles the window opened event to set localized texts and load the PLC address tree.
	 * Initializes the address tree and sets up UI texts from the resource bundle.
	 * @param arg0 WindowEvent
	 */
	protected void formWindowOpened(WindowEvent arg0) {

		RequestInputbox = new CreateRequestInputBox(true, resources);

		// set ressources
		this.lblLog.setText(resources.getString("lblLog_Text"));
		this.grpAddress.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		this.btnClose.setText(resources.getString("btnClose_Text"));

		this.txtInfoSymbRCB.setText(resources.getString("txtInfoSymbRCB_Text"));
		this.btnSaveLogtoClipboard.setText(resources.getString("btnSaveLogtoClipboard_Text"));
		this.btnSaveLogtoFile.setText(resources.getString("btnSaveLogtoFile_Text"));

		var globalAddressTree = mDevice.getAddressNodeTree();
		// Erstellen des unsichtbaren Root-Knotens
		DefaultMutableTreeNode invisibleRoot = new DefaultMutableTreeNode("invisible root node");

		for (var addressTreeNode : globalAddressTree) {
			var rootNode = new DefaultMutableTreeNode(addressTreeNode.getObjectDescriptor());
			// rootNode.setUserObject(addressTreeNode.getNodeDetails());
			addChildNodes(rootNode, addressTreeNode);
			invisibleRoot.add(rootNode);
		}

		DefaultTreeModel treeModel = new DefaultTreeModel(invisibleRoot);

		treePlcInventory.setRootVisible(false);
		treePlcInventory.setShowsRootHandles(true);
		treePlcInventory.setModel(treeModel);

	}

	/**
	 * Recursively adds child nodes to the address tree for display in the JTree.
	 * @param parentTreeNode The parent tree node in the JTree.
	 * @param addressNode The corresponding address node from the PLC.
	 */
	private void addChildNodes(DefaultMutableTreeNode parentTreeNode, AddressNode addressNode) {
		for (var childnode : addressNode.getChildNodes()) {
			var childTreeNode = new DefaultMutableTreeNode(childnode.getNodeDetails().getNodeName());
			childTreeNode.setUserObject(childnode.getNodeDetails());
			parentTreeNode.add(childTreeNode);
			addChildNodes(childTreeNode, childnode);
		}
	}

	/**
	 * Handles selection changes in the PLC inventory tree.
	 * Updates the UI with variable details and reads the value if readable.
	 * @param e TreeSelectionEvent
	 */
	private void treePlcInventory_valueChanged(TreeSelectionEvent e) {
		try {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treePlcInventory
					.getLastSelectedPathComponent();
			if (selectedNode != null) {

				VariableDetails nodedetails;
				if (selectedNode.getUserObject() != null && selectedNode.getUserObject() instanceof VariableDetails) {
					nodedetails = (VariableDetails) selectedNode.getUserObject();
					txtFullVariableName.setText(nodedetails.getFullVariableName());
					txtDataType.setText(nodedetails.getVariableDataType().toString());
					chIsReadable.setSelected(nodedetails.isReadable());
					chIsWritable.setSelected(nodedetails.isWritable());
					chIsSubscribable.setSelected(nodedetails.isSubscribable());
					chIsArray.setSelected(nodedetails.isArray());
					chIsStruct.setSelected(nodedetails.isStruct());
				} else {
					txtDataType.setText("");
					txtValue.setEditable(false);
					txtValue.setText("");
					return;

				}

				if (nodedetails.isWritable()) {
					btnWrite.setEnabled(true);
					txtValue.setEditable(true); // Corrected from txtValue.ReadOnly = false;
				} else {
					btnWrite.setEnabled(false);
					txtValue.setEditable(false); // Corrected from txtValue.ReadOnly = true;
				}

				if (nodedetails.isReadable()) {

					ReadSymbolicRequest readRequest = new ReadSymbolicRequest();
					readRequest.addFullVariableName(txtFullVariableName.getText());

					ReadSymbolicResultSet readResult = mDevice.readData(readRequest);

					if (readResult.getQuality() == OperationResult.eQuality.GOOD
							|| readResult.getQuality() == OperationResult.eQuality.WARNING_PARTITIAL_BAD) {
						txtValue.setText(readResult.getVariables().get(0).getValueAsJson());
						txtValue.setCaretPosition(0);
					}

					txtQuality.setText(readResult.getQuality().toString());
					txtMessage.setText(readResult.getMessage());

					switch (readResult.getQuality()) {
					case GOOD:
						txtQuality.setBackground(new Color(50, 205, 50)); // Equivalent to LimeGreen
						break;
					case WARNING_PARTITIAL_BAD:
						txtQuality.setBackground(Color.YELLOW);
						break;
					default:
						txtQuality.setBackground(Color.RED);
						break;
					}

				} else {
					txtValue.setText("");
					txtMessage.setText("");
					txtQuality.setText("");
					txtValue.setText("");
					txtQuality.setText("");
					txtMessage.setText("");
					txtQuality.setBackground(Color.WHITE);
				}
			}
		} catch (Exception ex) {

			JOptionPane.showMessageDialog(this, getExceptionText(ex));
		}
	}

	/**
	 * Handles the write button action to write a value to the selected PLC variable.
	 * Updates the UI with the result of the write operation.
	 * @param e ActionEvent
	 */
	private void btnWrite_actionPerformed(ActionEvent e) {
		try {
			var variableBody = mDevice.getEmptyVariableBody(txtFullVariableName.getText());

			if (variableBody == null)
				throw new NullPointerException("Cannot found variable for writing");

			variableBody.setValueAsJson(txtValue.getText());

			WriteSymbolicRequest writeRequest = new WriteSymbolicRequest(variableBody);

			WriteSymbolicResultSet writeResult = mDevice.writeData(writeRequest);

			if (writeResult.isQualityGoodOrWarning()) {
				txtQuality.setText(writeResult.getWriteOperationResults().get(0).getQuality().toString());
				txtMessage.setText(writeResult.getWriteOperationResults().get(0).getMessage());

				switch (writeResult.getWriteOperationResults().get(0).getQuality()) {
				case GOOD:
					txtQuality.setBackground(new Color(50, 205, 50)); // Equivalent to LimeGreen
					break;
				case WARNING_PARTITIAL_BAD:
					txtQuality.setBackground(Color.YELLOW);
					break;
				default:
					txtQuality.setBackground(Color.RED);
					break;
				}
			} else {
				txtQuality.setText(writeResult.getQuality().toString());
				txtMessage.setText(writeResult.getMessage());

				switch (writeResult.getQuality()) {
				case GOOD:
					txtQuality.setBackground(new Color(50, 205, 50)); // Equivalent to LimeGreen
					break;
				case WARNING_PARTITIAL_BAD:
					txtQuality.setBackground(Color.YELLOW);
					break;
				default:
					txtQuality.setBackground(Color.RED);
					break;
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, getExceptionText(ex));
		}
	}

	/**
	 * Handles the close button action. Hides the window and updates dialog count.
	 * @param e ActionEvent
	 */
	private void btnClose_actionPerformed(ActionEvent e) {
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

	/**
	 * Handles the action to copy the diagnostic log to the clipboard.
	 * @param e ActionEvent
	 */
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

	/**
	 * Handles the action to save the diagnostic log to a file.
	 * @param e ActionEvent
	 */
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

	/**
	 * Utility method to get a formatted exception text including stack trace.
	 * @param ex Exception
	 * @return String with error type, message, and stack trace
	 */
	private static String getExceptionText(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String stackTrace = sw.toString();

		String errorMessage = "Errortype: " + ex.getClass().getName() + "\n" + "Errormessage: " + ex.getMessage()
				+ "\n\n" + "Stacktrace:\n" + stackTrace;
		return errorMessage;
	}
}
