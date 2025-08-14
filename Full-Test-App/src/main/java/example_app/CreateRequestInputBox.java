package example_app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.indian.plccom.fors7.ReadDataRequest;
import com.indian.plccom.fors7.WriteDataRequest;
import com.indian.plccom.fors7.eDataType;
import com.indian.plccom.fors7.eRegion;
import com.indian.plccom.fors7.UnsignedDatatypes.UInteger;
import com.indian.plccom.fors7.UnsignedDatatypes.ULong;
import com.indian.plccom.fors7.UnsignedDatatypes.UShort;

import example_app.DisabledJPanel.DisabledJPanel;

@SuppressWarnings("serial")
public class CreateRequestInputBox extends JDialog {

	Object RequestItem = null;
	int Result = JOptionPane.UNDEFINED_CONDITION;

	private ResourceBundle resources;
	private String ValuetoWrite = "";
	private boolean InitInProgress = true;
	private boolean withWriteOption = true;
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
	private JTextPane txtInfoRequest;
	private JPanel grbWriteValues;
	private DisabledJPanel panWriteValues;
	private JCheckBox chkSingleValue;
	private JLabel lblEnterValues;
	private JTextArea txtMultipleNumericValues;
	private JScrollPane scrollMultipleNumericValues;
	private JTextArea txtMultipleBoolValues;
	private JScrollPane scrollMultipleBoolValues;
	private JRadioButton rbOn;
	private JRadioButton rbOff;
	private JTextField txtSingleValues;
	private JLabel lblMode;
	private JLabel lblDB;
	private JTextField txtDB;
	private JPanel panel;
	private JLabel label;
	private JLabel lblLogo;
	private JButton btnAcceptRequest;
	private JButton btnAbort;
	private JRadioButton rbRead;
	private JRadioButton rbWrite;
	private JComboBox<Charset> cmbCharSet;
	private JLabel lblFactor;
	private JTextField txtFactor;
	private JTextField txtRequest;
	private JCheckBox chkAllowMultipleBits;

	/**
	 * Create the dialog.
	 */
	public CreateRequestInputBox(boolean withWriteOption, ResourceBundle rb) {
		resources = rb;
		this.withWriteOption = withWriteOption;
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
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

		setTitle("");

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				CreateRequestInputBox.class.getResource("/example_app/btnReadWriteFunctions.Image.png")));
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModal(true);
		this.setBounds(10, 10, 688, 506);

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
		this.grpAddress.setBounds(12, 67, 658, 311);
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
		txtReadAddress.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtReadAddress_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtReadAddress_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtReadAddress_TextChanged(e);
			}
		});

		txtBit = new JTextField();
		txtBit.setEnabled(false);
		txtBit.setText("0");
		txtBit.setColumns(10);
		txtBit.setBounds(123, 184, 175, 20);
		txtBit.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtBit_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtBit_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtBit_TextChanged(e);
			}
		});
		grpAddress.add(txtBit);

		txtQuantity = new JTextField();
		txtQuantity.setText("1");
		txtQuantity.setColumns(10);
		txtQuantity.setBounds(123, 213, 42, 20);
		txtQuantity.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtQuantity_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtQuantity_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtQuantity_TextChanged(e);
			}
		});
		grpAddress.add(txtQuantity);

		txtWriteAddress = new JTextField();
		txtWriteAddress.setText("0");
		txtWriteAddress.setColumns(10);
		txtWriteAddress.setBounds(123, 154, 175, 20);
		txtWriteAddress.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtWriteAddress_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtWriteAddress_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtWriteAddress_TextChanged(e);
			}
		});

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
		chkSingleValue.setSelected(true);
		chkSingleValue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg) {
				chkSingleValue_mouseClicked(arg);
			}
		});
		chkSingleValue.setBounds(41, 31, 82, 17);
		grbWriteValues.add(chkSingleValue);

		txtMultipleNumericValues = new JTextArea();
		txtMultipleNumericValues.setText("0\r\n0\r\n0\r\n0");
		txtMultipleNumericValues.setBorder(null);
		txtMultipleNumericValues.setBounds(41, 54, 275, 94);
		txtMultipleNumericValues.setLineWrap(true);
		txtMultipleNumericValues.setWrapStyleWord(true);
		scrollMultipleNumericValues = new JScrollPane(txtMultipleNumericValues);
		scrollMultipleNumericValues.setBounds(new Rectangle(41, 74, 275, 138));
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
		scrollMultipleBoolValues.setBounds(new Rectangle(41, 74, 275, 133));
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
		lblEnterValues.setBounds(38, 52, 219, 13);
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
		rbOn.setBounds(41, 73, 52, 17);
		grbWriteValues.add(rbOn);

		rbOff = new JRadioButton("OFF");
		rbOff.setVisible(false);
		rbOff.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rbOff_mouseClicked(e);
			}
		});
		rbOff.setBounds(179, 73, 49, 17);
		grbWriteValues.add(rbOff);

		ButtonGroup btnGroupOnOFF = new ButtonGroup();
		btnGroupOnOFF.add(rbOn);
		btnGroupOnOFF.add(rbOff);

		txtSingleValues = new JTextField();
		txtSingleValues.setVisible(false);
		txtSingleValues.setText("0");
		txtSingleValues.setBounds(41, 73, 275, 20);
		grbWriteValues.add(txtSingleValues);
		txtSingleValues.setColumns(10);

		chkAllowMultipleBits = new JCheckBox("allow multiple Bits");
		chkAllowMultipleBits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chkAllowMultipleBits_actionPerformed(e);
			}
		});
		chkAllowMultipleBits.setBounds(41, 11, 130, 17);
		grbWriteValues.add(chkAllowMultipleBits);

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
		txtDB.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				txtDB_TextChanged(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtDB_TextChanged(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				txtDB_TextChanged(e);
			}
		});
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

		JLabel lblRequest = new JLabel("request");
		lblRequest.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRequest.setBounds(0, 272, 116, 14);
		grpAddress.add(lblRequest);

		txtRequest = new JTextField();
		txtRequest.setEditable(false);
		txtRequest.setText("");
		txtRequest.setColumns(10);
		txtRequest.setBounds(123, 272, 523, 20);
		grpAddress.add(txtRequest);
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

		btnAcceptRequest = new JButton("<html><center>Create</center><center>Request</center></html>");
		btnAcceptRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnAcceptRequest.setIcon(new ImageIcon(
				CreateRequestInputBox.class.getResource("/example_app/btnCreateRequest.Image.png")));
		btnAcceptRequest.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAcceptRequest.setMargin(new Insets(0, 0, 0, 0));
		btnAcceptRequest.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAcceptRequest.setBounds(590, 389, 68, 68);
		getContentPane().add(btnAcceptRequest);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(SystemColor.info);
		panel.setBounds(187, 6, 472, 59);
		getContentPane().add(panel);
		panel.setLayout(null);

		txtInfoRequest = new JTextPane();
		txtInfoRequest.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoRequest.setBounds(60, 2, 402, 48);
		panel.add(txtInfoRequest);
		txtInfoRequest.setBorder(null);
		txtInfoRequest.setText(
				"You can execute single read and write processes on this window. For optimized read processes please use the ReadDataRequestCollection.");
		txtInfoRequest.setEditable(false);
		txtInfoRequest.setBackground(SystemColor.info);

		label = new JLabel();
		label.setIcon(
				new ImageIcon(CreateRequestInputBox.class.getResource("/example_app/pictureBox1.Image.png")));
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

		btnAbort = new JButton("<html><center>Abort</center></html>");
		btnAbort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAbort_actionPerformed(e);
			}
		});
		btnAbort.setIcon(
				new ImageIcon(CreateRequestInputBox.class.getResource("/example_app/btnClose.Image.png")));
		btnAbort.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAbort.setMargin(new Insets(0, 0, 0, 0));
		btnAbort.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAbort.setBounds(512, 389, 68, 68);
		getContentPane().add(btnAbort);

	}

	protected void txtDB_TextChanged(DocumentEvent e) {
		if (txtDB.getText().isEmpty())
			return;
		CreateRequest();
	}

	protected void txtReadAddress_TextChanged(DocumentEvent e) {
		if (txtReadAddress.getText().isEmpty())
			return;
		CreateRequest();
	}

	protected void txtWriteAddress_TextChanged(DocumentEvent e) {
		if (txtWriteAddress.getText().isEmpty())
			return;
		CreateRequest();
	}

	protected void txtQuantity_TextChanged(DocumentEvent e) {
		if (txtQuantity.getText().isEmpty())
			return;
		CreateRequest();
	}

	protected void txtBit_TextChanged(DocumentEvent e) {
		if (txtBit.getText().isEmpty())
			return;
		CreateRequest();
	}

	protected void formWindowClosing() {
		Main.CountOpenDialogs--;
	}

	protected void formWindowOpened(WindowEvent arg0) {
		try {
			InitInProgress = true;

			// fill combobox with enum values
			cmbDataType.setModel(new DefaultComboBoxModel<eDataType>(eDataType.values()));
			cmbDataType.setSelectedItem(eDataType.BYTE);

			cmbRegion.setModel(new DefaultComboBoxModel<eRegion>(eRegion.values()));

			// fill cmbCharSet
			Charset[] c = Charset.availableCharsets().values()
					.toArray(new Charset[Charset.availableCharsets().values().size()]);
			cmbCharSet.setModel(new DefaultComboBoxModel<Charset>(c));
			cmbCharSet.setSelectedItem(Charset.forName("US-ASCII"));

			rbWrite.setEnabled(this.withWriteOption);

			this.grpAddress.setBorder(
					new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grpAddress_Text"),
							TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			this.lblDataType.setText(resources.getString("lblDataType_Text"));
			this.lblReadAddress.setText(resources.getString("lblReadAddress_Text"));
			this.lblWriteAddress.setText(resources.getString("lblWriteAddress_Text"));
			this.lblBit.setText(resources.getString("lblBit_Text"));
			this.lblQuantity.setText(resources.getString("lblLength_Text"));
			this.btnAcceptRequest.setText(resources.getString("btnAcceptRequest_Text"));
			this.btnAbort.setText(resources.getString("btnAbort_Text"));
			this.txtInfoRequest.setText(resources.getString("txtInfoRB_OR_Text"));
			this.chkSingleValue.setText(resources.getString("chkSingleValue_Text"));
			this.lblEnterValues.setText(resources.getString("lblValues_Text"));
			this.lblRegion.setText(resources.getString("lblRegion_Text"));
			this.lblMode.setText(resources.getString("lblMode_Text"));
			this.rbRead.setText(resources.getString("rbRead_Text"));
			this.rbWrite.setText(resources.getString("rbWrite_Text"));

			InitInProgress = false;
			CreateRequest();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			InitInProgress = false;
		}
	}

	private void CreateRequest() {
		// initialization in progress
		if (InitInProgress)
			return;

		if (rbRead.isSelected())
			CreateReadRequest();
		else
			CreateWriteRequest();

		txtRequest.setText(this.RequestItem.toString());
	}

	private void CreateReadRequest() {
		try {
			// declare a ReadDataRequest object and
			// set the request parameters
			ReadDataRequest readDataRequest = new ReadDataRequest((eRegion) cmbRegion.getSelectedItem(), // Region
					Integer.valueOf(txtDB.getText()), /* DB only for datablock operations otherwise 0 */
					Integer.valueOf(txtReadAddress.getText()), /* read start adress */
					(eDataType) cmbDataType.getSelectedItem(), /* desired datatype */
					Integer.valueOf(txtQuantity.getText()) * Integer.valueOf(txtFactor.getText()), // Quantity of
					Byte.valueOf(txtBit.getText()), /* Bit / only for bit operations */
					(Charset) cmbCharSet.getSelectedItem()); // Optionally the Encoding for eventual string operations

			this.RequestItem = readDataRequest;

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void CreateWriteRequest() {
		try {
			// parse valuestring and add writable Data here
			Utilities.sValues_to_Write vtw = null;
			vtw = Utilities.CheckValues(ValuetoWrite, eDataType.valueOf(cmbDataType.getSelectedItem().toString()));

			if (!vtw.ParseError) {

				WriteDataRequest writeDataRequest = new WriteDataRequest((eRegion) cmbRegion.getSelectedItem(), // Region
						Integer.valueOf(txtDB.getText()), // DB / only for
															// data block
															// operations
															// operations
															// otherwise 0
						Integer.valueOf(txtWriteAddress.getText()), // write start adress
						Byte.valueOf(txtBit.getText()), // Bit, only for bit operations otherwise 0
						(Charset) cmbCharSet.getSelectedItem() // Optionally the Encoding for eventual string
																// operations
				);

				// enable or disable multiple write bit mode
				writeDataRequest
						.setAllowMultipleBits(chkAllowMultipleBits.isEnabled() && chkAllowMultipleBits.isSelected());

				// add writable data to request
				for (Object writevalue : vtw.values) {
					switch ((eDataType) cmbDataType.getSelectedItem()) {
					case BIT:
						writeDataRequest.addBit((Boolean) writevalue);
						break;
					case BYTE:
						writeDataRequest.addByte((Byte) writevalue);
						break;
					case INT:
						writeDataRequest.addInt((Short) writevalue);
						break;
					case DINT:
						writeDataRequest.addDInt((Integer) writevalue);
						break;
					case LINT:
						writeDataRequest.addLInt((Long) writevalue);
						break;
					case WORD:
						writeDataRequest.addWord((UShort) writevalue);
						break;
					case DWORD:
						writeDataRequest.addDWord((UInteger) writevalue);
						break;
					case LWORD:
						writeDataRequest.addLWord((ULong) writevalue);
						break;
					case REAL:
						writeDataRequest.addReal((Float) writevalue);
						break;
					case LREAL:
						writeDataRequest.addLReal((Double) writevalue);
						break;
					case RAWDATA:
						writeDataRequest.addByte((Byte) writevalue);
						break;
					case BCD8:
						writeDataRequest.addBCD8((Byte) writevalue);
						break;
					case BCD16:
						writeDataRequest.addBCD16((Short) writevalue);
						break;
					case BCD32:
						writeDataRequest.addBCD32((Integer) writevalue);
						break;
					case BCD64:
						writeDataRequest.addBCD64((Long) writevalue);
						break;
					case DATETIME:
					case DATE_AND_TIME:
						writeDataRequest.addDATE_AND_TIME((Instant) writevalue);
						break;
					case LDATE_AND_TIME:
						writeDataRequest.addLDATE_AND_TIME((Instant) writevalue);
						break;
					case S5TIME:
						writeDataRequest.addS5TIME((Long) writevalue);
						break;
					case TIME_OF_DAY:
						writeDataRequest.addTIME_OF_DAY((LocalTime) writevalue);
						break;
					case LTIME_OF_DAY:
						writeDataRequest.addLTIME_OF_DAY((LocalTime) writevalue);
						break;
					case TIME:
						writeDataRequest.addTIME((Duration) writevalue);
						break;
					case LTIME:
						writeDataRequest.addLTIME((Duration) writevalue);
						break;
					case DATE:
						writeDataRequest.addDATE((LocalDate) writevalue);
						break;
					case STRING:
						writeDataRequest.addString((String) writevalue);
						break;
					case S7STRING:
						writeDataRequest.addS7String((String) writevalue);
						break;
					case S7WSTRING:
						writeDataRequest.addS7WString((String) writevalue);
						break;
					default:
						// abort message
						JOptionPane.showMessageDialog(null,
								resources.getString("wrong_datatype") + " " + resources.getString("operation_aborted"),
								"", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}

				this.RequestItem = writeDataRequest;

			} else {
				// abort message
				JOptionPane.showMessageDialog(this, resources.getString("ParseError"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}

			return;

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cmbRegion_actionPerformed(ActionEvent arg) {
		try {
			// enable or disable DB field
			txtDB.setEnabled(((eRegion) cmbRegion.getSelectedItem()).equals(eRegion.DataBlock));

			CreateRequest();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cmbCharSet_actionPerformed(ActionEvent e) {
		try {
			// get byte length of one character from desired charset
			txtFactor.setText(String.valueOf(((Charset) cmbCharSet.getSelectedItem()).encode("0").limit()));

			CreateRequest();

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

		CreateRequest();

	}

	private void txtMultipleNumericValues_TextChanged(DocumentEvent e) {

		if (txtMultipleNumericValues.getText().isEmpty())
			return;

		// set writable string
		ValuetoWrite = chkSingleValue.isSelected() ? txtSingleValues.getText() : txtMultipleNumericValues.getText();

		CreateRequest();
	}

	private void txtMultipleBoolValues_TextChanged(DocumentEvent e) {

		if (txtMultipleBoolValues.getText().isEmpty())
			return;

		// set writable string
		ValuetoWrite = chkSingleValue.isSelected() ? String.valueOf(rbOn.isSelected())
				: txtMultipleBoolValues.getText();

		CreateRequest();
	}

	private void txtSingleValues_TextChanged(DocumentEvent e) {

		if (txtSingleValues.getText().isEmpty())
			return;

		// set writable string
		ValuetoWrite = txtSingleValues.getText();

		CreateRequest();
	}

	private void rbOn_mouseClicked(MouseEvent e) {
		// set writable string
		ValuetoWrite = String.valueOf(rbOn.isSelected());

		CreateRequest();
	}

	private void rbOff_mouseClicked(MouseEvent e) {
		// set writable string
		ValuetoWrite = String.valueOf(rbOn.isSelected());

		CreateRequest();
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
			chkAllowMultipleBits.setEnabled(true);
			if (!chkAllowMultipleBits.isSelected()) {
				chkSingleValue.setSelected(true);
				chkSingleValue.setEnabled(false);
			}

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
			chkAllowMultipleBits.setEnabled(false);
			chkSingleValue.setEnabled(true);
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

		CreateRequest();

	}

	private void rbRead_addActionListener(ActionEvent e) {
		rbWrite.setSelected(!rbRead.isSelected());

		// enable or disable input fields
		panWriteValues.setEnabled(!rbRead.isSelected());
		txtQuantity.setEnabled(rbRead.isSelected());

		CreateRequest();
	}

	private void rbWrite_addActionListener(ActionEvent e) {
		rbRead.setSelected(!rbWrite.isSelected());

		// enable or disable input fields
		panWriteValues.setEnabled(!rbRead.isSelected());
		txtQuantity.setEnabled(rbRead.isSelected());

		CreateRequest();
	}

	private void chkAllowMultipleBits_actionPerformed(ActionEvent e) {
		if (chkAllowMultipleBits.isSelected()) {

			JOptionPane.showMessageDialog(this, resources.getString("warning_allow_multiple_bits"),
					resources.getString("Important_question"), JOptionPane.WARNING_MESSAGE);

			chkSingleValue.setEnabled(true);
			chkSingleValue.setSelected(false);
		} else {
			chkSingleValue.setSelected(true);
			chkSingleValue.setEnabled(false);
		}
	}

	void showDialog() {
		try {
			this.setVisible(true);
		} catch (Exception ex) {

		}
	}

	private void btnClose_actionPerformed(ActionEvent e) {

		this.Result = JOptionPane.OK_OPTION;
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void btnAbort_actionPerformed(ActionEvent e) {
		this.Result = JOptionPane.CANCEL_OPTION;

		this.setVisible(false);
		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

}
