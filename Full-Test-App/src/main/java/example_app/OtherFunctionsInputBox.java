package example_app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OtherFunctionsInputBox extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtSSL_ID;
	private JTextField txtSSL_Index;
	private JSpinner timeSpinner;
	private JTextField txtSSL_ID_Hex;
	private JTextField txtSSL_Index_Hex;
	private String DateTimeFormatString_en = "yyyy-MM-dd hh:mm:ss a";
	private String DateTimeFormatString_de = "dd.MM.yyyy HH:mm:ss";
	eOtherFunctionOpenMode OtherFunctionOpenMode = null;

	Instant actDateTime = Instant.now();
	int SSL_ID = 0;
	int SSL_Index = 0;
	boolean isCancel = true;

	enum eOtherFunctionOpenMode {
		Date_and_Time, SSL_SZL
	}

	/**
	 * Create the dialog.
	 */
	public OtherFunctionsInputBox(eOtherFunctionOpenMode OtherFunctionOpenMode) {

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

		this.OtherFunctionOpenMode = OtherFunctionOpenMode;
		isCancel = true;
		setTitle("InputBox");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 233, 197);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblDate = new JLabel("DateTime");
			lblDate.setBounds(10, 11, 79, 14);
			contentPanel.add(lblDate);
		}
		{
			JLabel lblSSL_ID = new JLabel("SSL / SZL");
			lblSSL_ID.setBounds(10, 39, 79, 14);
			contentPanel.add(lblSSL_ID);
		}
		{
			txtSSL_ID = new JTextField();
			txtSSL_ID.setText("306");
			txtSSL_ID.setColumns(10);
			txtSSL_ID.setBounds(69, 33, 30, 20);
			txtSSL_ID.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					txtSSL_ID_TextChanged(e);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					txtSSL_ID_TextChanged(e);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					txtSSL_ID_TextChanged(e);
				}

			});
			contentPanel.add(txtSSL_ID);

		}
		{
			JLabel lblSSL_Index = new JLabel("Index");
			lblSSL_Index.setBounds(10, 71, 79, 14);
			contentPanel.add(lblSSL_Index);
		}
		{
			txtSSL_Index = new JTextField();
			txtSSL_Index.setText("4");
			txtSSL_Index.setColumns(10);
			txtSSL_Index.setBounds(69, 65, 30, 20);
			txtSSL_Index.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					txtSSL_Index_TextChanged(e);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					txtSSL_Index_TextChanged(e);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					txtSSL_Index_TextChanged(e);
				}
			});
			contentPanel.add(txtSSL_Index);
		}
		{
			JLabel lblHex = new JLabel("hex");
			lblHex.setBounds(144, 39, 27, 14);
			contentPanel.add(lblHex);
		}
		{
			JLabel lblHex_1 = new JLabel("hex");
			lblHex_1.setBounds(144, 71, 27, 14);
			contentPanel.add(lblHex_1);
		}
		{
			txtSSL_ID_Hex = new JTextField();
			txtSSL_ID_Hex.setText("132");
			txtSSL_ID_Hex.setEditable(false);
			txtSSL_ID_Hex.setColumns(10);
			txtSSL_ID_Hex.setBounds(170, 36, 47, 20);
			contentPanel.add(txtSSL_ID_Hex);
		}
		{
			txtSSL_Index_Hex = new JTextField();
			txtSSL_Index_Hex.setEditable(false);
			txtSSL_Index_Hex.setText("4");
			txtSSL_Index_Hex.setColumns(10);
			txtSSL_Index_Hex.setBounds(170, 68, 47, 20);
			contentPanel.add(txtSSL_Index_Hex);
		}
		{
			timeSpinner = new JSpinner(new SpinnerDateModel());
			JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner,
					Locale.getDefault().getLanguage().equals("de") ? DateTimeFormatString_de : DateTimeFormatString_en);
			timeSpinner.setBounds(69, 8, 148, 20);
			timeSpinner.setEditor(timeEditor);
			timeSpinner.setValue(new Date()); // will only show the current time
			contentPanel.add(timeSpinner);
		}

		JButton btnAccept = new JButton("accept");
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAccept_actionPerformed(e);
			}
		});
		btnAccept.setIcon(
				new ImageIcon(OtherFunctionsInputBox.class.getResource("/example_app/btnAccept.Image.png")));
		btnAccept.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAccept.setToolTipText("");
		btnAccept.setMargin(new Insets(0, 0, 0, 0));
		btnAccept.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAccept.setBounds(69, 96, 68, 68);
		contentPanel.add(btnAccept);

		JButton btnReject = new JButton("reject");
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnReject_actionPerformed(e);
			}
		});
		btnReject.setIcon(
				new ImageIcon(OtherFunctionsInputBox.class.getResource("/example_app/btnReject.Image.png")));
		btnReject.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnReject.setToolTipText("");
		btnReject.setMargin(new Insets(0, 0, 0, 0));
		btnReject.setHorizontalTextPosition(SwingConstants.CENTER);
		btnReject.setBounds(151, 96, 68, 68);
		contentPanel.add(btnReject);

		switch (this.OtherFunctionOpenMode) {
		case Date_and_Time:
			this.txtSSL_ID.setEnabled(false);
			this.txtSSL_Index.setEnabled(false);
			this.timeSpinner.setEnabled(true);
			break;
		case SSL_SZL:
			txtSSL_ID.setEnabled(true);
			txtSSL_Index.setEnabled(true);
			timeSpinner.setEnabled(false);
			break;
		}
	}

	private void btnAccept_actionPerformed(ActionEvent e) {
		try {

			switch (OtherFunctionOpenMode) {
			case Date_and_Time:
				SimpleDateFormat sdf = new SimpleDateFormat(
						Locale.getDefault().getLanguage().equals("de") ? DateTimeFormatString_de
								: DateTimeFormatString_en);
				actDateTime = sdf.parse(sdf.format(timeSpinner.getValue())).toInstant();
				SSL_ID = 0;
				SSL_Index = 0;
				break;
			case SSL_SZL:
				actDateTime = null;
				SSL_ID = Integer.valueOf(txtSSL_ID.getText());
				SSL_Index = Integer.valueOf(txtSSL_Index.getText());
				break;
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getClass().getName() + " " + ex.getMessage(), "",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		isCancel = false;

		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void btnReject_actionPerformed(ActionEvent e) {
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void txtSSL_ID_TextChanged(DocumentEvent e) {

		try {
			int value = Integer.valueOf(txtSSL_ID.getText());
			txtSSL_ID_Hex.setText(Integer.toHexString(0xFFFFF & value));
			txtSSL_ID_Hex.setBackground(null);
		} catch (Exception ex) {
			txtSSL_ID_Hex.setText("invalid");
			txtSSL_ID_Hex.setBackground(Color.red);
		}

	}

	private void txtSSL_Index_TextChanged(DocumentEvent e) {
		try {
			int value = Integer.valueOf(txtSSL_Index.getText());
			txtSSL_Index_Hex.setText(Integer.toHexString(0xFFFFF & value));
			txtSSL_Index_Hex.setBackground(null);
		} catch (Exception ex) {
			txtSSL_Index_Hex.setText("invalid");
			txtSSL_Index_Hex.setBackground(Color.red);
		}

	}
}
