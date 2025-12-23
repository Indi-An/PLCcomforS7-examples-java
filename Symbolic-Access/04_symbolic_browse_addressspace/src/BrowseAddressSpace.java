import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.indian.plccom.fors7.AddressNode;
import com.indian.plccom.fors7.ConnectResult;
import com.indian.plccom.fors7.SymbolicDevice;
import com.indian.plccom.fors7.OperationResult.eQuality;
import com.indian.plccom.fors7.Tls13Device;
import com.indian.plccom.fors7.VariableDetails;
import com.indian.plccom.fors7.authentication;

import javax.swing.JPasswordField;

public class BrowseAddressSpace extends JFrame {
	private static final long serialVersionUID = 1L;
	private SymbolicDevice mDevice;

	private JPanel grpAddress;
	private JButton btnClose;
	private JTree treePlcInventory;
	private JTextField txtFullVariableName;
	private JTextField txtDataType;
	private JCheckBox chIsReadable;
	private JCheckBox chIsWritable;
	private JCheckBox chIsArray;
	private JCheckBox chIsStruct;
	private JLabel lblNewLabel;
	private JLabel lblIsWritable;
	private JLabel lblIsArray;
	private JLabel lblIsStruct;
	private JTextField txtIpAddress;
	private JTextField txtSerial;
	private JTextField txtUser;
	private JButton btnConnect;
	private JButton btnDisconnect;

	public BrowseAddressSpace() {
		setResizable(false);
		setTitle("Symbolic browse address space");
		initialize();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BrowseAddressSpace frame = new BrowseAddressSpace();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		this.setBounds(15, 10, 738, 552);

		this.getContentPane().setLayout(null);

		this.grpAddress = new JPanel();
		this.grpAddress.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "result",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		this.grpAddress.setBounds(10, 166, 710, 267);
		this.grpAddress.setLayout(null);
		this.getContentPane().add(grpAddress);

		treePlcInventory = new JTree();
		treePlcInventory.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("root") {
				{
				}
			}
		));
		treePlcInventory.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				treePlcInventory_valueChanged(e);
			}
		});
		JScrollPane treeScroll = new JScrollPane(treePlcInventory);
		treeScroll.setBounds(10, 21, 687, 142);
		grpAddress.add(treeScroll);

		JLabel lblDatatype = new JLabel("Datatype");
		lblDatatype.setBounds(10, 200, 95, 14);
		grpAddress.add(lblDatatype);

		JLabel lblVarName = new JLabel("Full Variable Name");
		lblVarName.setBounds(10, 176, 95, 14);
		grpAddress.add(lblVarName);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 10, 10);
		grpAddress.add(panel);

		txtFullVariableName = new JTextField();
		txtFullVariableName.setEditable(false);
		txtFullVariableName.setBounds(112, 174, 489, 20);
		grpAddress.add(txtFullVariableName);
		txtFullVariableName.setColumns(10);

		txtDataType = new JTextField();
		txtDataType.setEditable(false);
		txtDataType.setColumns(10);
		txtDataType.setBounds(112, 200, 489, 20);
		grpAddress.add(txtDataType);

		chIsReadable = new JCheckBox();
		chIsReadable.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsReadable.setBounds(668, 170, 21, 23);
		chIsReadable.setEnabled(false);
		grpAddress.add(chIsReadable);

		chIsWritable = new JCheckBox();
		chIsWritable.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsWritable.setBounds(668, 190, 21, 23);
		chIsWritable.setEnabled(false);
		grpAddress.add(chIsWritable);

		chIsArray = new JCheckBox();
		chIsArray.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsArray.setBounds(668, 210, 21, 23);
		chIsArray.setEnabled(false);
		grpAddress.add(chIsArray);

		chIsStruct = new JCheckBox();
		chIsStruct.setHorizontalTextPosition(SwingConstants.LEFT);
		chIsStruct.setBounds(668, 230, 21, 23);
		chIsStruct.setEnabled(false);
		grpAddress.add(chIsStruct);

		lblNewLabel = new JLabel("is readable");
		lblNewLabel.setBounds(611, 174, 58, 14);
		grpAddress.add(lblNewLabel);

		lblIsWritable = new JLabel("is writable");
		lblIsWritable.setBounds(611, 194, 58, 14);
		grpAddress.add(lblIsWritable);

		lblIsArray = new JLabel("is Array");
		lblIsArray.setBounds(611, 214, 58, 14);
		grpAddress.add(lblIsArray);

		lblIsStruct = new JLabel("is Struct");
		lblIsStruct.setBounds(611, 234, 58, 14);
		grpAddress.add(lblIsStruct);

		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					if (mDevice != null) {
						// unload and dispose all objects
						mDevice.disConnect();
						mDevice = null;
					}

				} finally {
					System.exit(0);
				}
			}

		});

		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(640, 437, 68, 68);
		getContentPane().add(btnClose);

		JPanel grbAddress = new JPanel();
		grbAddress.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		grbAddress.setBounds(10, 75, 710, 88);
		getContentPane().add(grbAddress);
		grbAddress.setLayout(null);

		txtIpAddress = new JTextField();
		txtIpAddress.setBounds(91, 12, 213, 20);
		grbAddress.add(txtIpAddress);
		txtIpAddress.setColumns(10);

		JPasswordField txtPassword = new JPasswordField();
		txtPassword.setBounds(91, 43, 213, 20);
		grbAddress.add(txtPassword);

		JLabel lblPassword = new JLabel("Plc Password");
		lblPassword.setBounds(10, 46, 95, 14);
		grbAddress.add(lblPassword);

		JLabel lblIpAddress = new JLabel("Ip address");
		lblIpAddress.setBounds(10, 15, 95, 14);
		grbAddress.add(lblIpAddress);

		btnConnect = new JButton("<html><center>connect</center><center>Plc</center></html>");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					if (mDevice != null) {
						// unload and dispose all objects
						mDevice.disConnect();
					}

					authentication.setUser(txtUser.getText());
					authentication.setSerial(txtSerial.getText());

					// create a Tls13Device instance for access to modern PLCs with TLS 1.3 support
					mDevice = new Tls13Device(txtIpAddress.getText(), new String(txtPassword.getPassword()));
					// or create a LegacySymbolicDevice instance for a legacy access to older PLCs
					//mDevice = new LegacySymbolicDevice(txtIpAddress.getText(), new String(txtPassword.getPassword()));

					ConnectResult connectResult = mDevice.connect();
					if (connectResult.getQuality() == eQuality.GOOD) {

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

					} else {
						JOptionPane.showMessageDialog(null,
								"Connection not sucessfull Quality: " + connectResult.getQuality().toString()
										+ " Message: " + connectResult.getMessage(),
								"Connection not sucessfull", JOptionPane.INFORMATION_MESSAGE);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				finally
				{
					setCursor(Cursor.getDefaultCursor());
				}

			}
		});
		btnConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnConnect.setToolTipText("");
		btnConnect.setMargin(new Insets(0, 0, 0, 0));
		btnConnect.setHorizontalTextPosition(SwingConstants.CENTER);
		btnConnect.setBounds(327, 11, 68, 68);
		grbAddress.add(btnConnect);

		btnDisconnect = new JButton("<html><center>disconnect</center><center>Plc</center></html>");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// clear tree
		        DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("root");
				treePlcInventory.setModel(new DefaultTreeModel(newRoot));

				if (mDevice != null) {
					// unload and dispose all objects
					mDevice.disConnect();
				}
			}
		});
		btnDisconnect.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnDisconnect.setToolTipText("");
		btnDisconnect.setMargin(new Insets(0, 0, 0, 0));
		btnDisconnect.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDisconnect.setBounds(410, 11, 68, 68);
		grbAddress.add(btnDisconnect);

		JPanel grbSerial = new JPanel();
		grbSerial.setLayout(null);
		grbSerial.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "serial", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		grbSerial.setBounds(10, 11, 710, 64);
		getContentPane().add(grbSerial);

		JLabel lblSerialCode = new JLabel("enter serialcode first     >>>");
		lblSerialCode.setFont(new Font("Arial", Font.BOLD, 13));
		lblSerialCode.setBounds(6, 26, 314, 16);
		grbSerial.add(lblSerialCode);

		JLabel lblUser = new JLabel("user:");
		lblUser.setBounds(328, 15, 46, 14);
		grbSerial.add(lblUser);

		JLabel lblSerial = new JLabel("serial:");
		lblSerial.setBounds(328, 41, 46, 14);
		grbSerial.add(lblSerial);

		txtSerial = new JTextField();
		txtSerial.setBounds(370, 38, 326, 20);
		grbSerial.add(txtSerial);

		txtUser = new JTextField();
		txtUser.setBounds(370, 12, 326, 20);
		grbSerial.add(txtUser);

	}

	private void addChildNodes(DefaultMutableTreeNode parentTreeNode, AddressNode addressNode) {
		for (var childnode : addressNode.getChildNodes()) {
			var childTreeNode = new DefaultMutableTreeNode(childnode.getNodeDetails().getNodeName());
			childTreeNode.setUserObject(childnode.getNodeDetails());
			parentTreeNode.add(childTreeNode);
			addChildNodes(childTreeNode, childnode);
		}
	}

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

					chIsArray.setSelected(nodedetails.isArray());
					chIsStruct.setSelected(nodedetails.isStruct());
				} else {
					txtDataType.setText("");
					return;

				}

			}
		} catch (Exception ex) {

			JOptionPane.showMessageDialog(this, ex.getMessage());
		}
	}
}
