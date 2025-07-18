package example_app;

// Imports for UI, date/time, collections, and PLC communication
import java.awt.Font;
import java.awt.Toolkit;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import java.awt.Color;

import javax.swing.SwingConstants;
import com.indian.plccom.fors7.*;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.ImageIcon;

import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dialog.ModalExclusionType;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * AlarmDetails is a JFrame that displays detailed information about an alarm notification.
 * It shows various alarm properties and localized text entries in a structured form.
 */
public class AlarmDetails extends JFrame {

	private static final long serialVersionUID = 1L;
	private ResourceBundle resources;
	/**
	 * Non-visual text field, possibly for internal use.
	 */
	private final JTextArea textField = new JTextArea();
	// Text areas for displaying alarm properties
	private JTextArea txtMessageType;
	private JTextArea txtAlarmId;
	private JTextArea txtTsGoing;
	private JTextArea txtProducer;
	private JTextArea txtClassId;
	private JTextArea txtTSComing;
	private JTextArea txtState;
	private JTextArea txtAlarmNo;
	private JTextArea txtTSAck;
	private JTextArea txtAdditionalText1;
	private JTextArea txtAdditionalText2;
	private JTextArea txtAdditionalText3;
	private JTextArea txtAdditionalText4;
	private JTextArea txtAdditionalText5;
	private JTextArea txtAdditionalText6;
	private JTextArea txtAdditionalText7;
	private JTextArea txtAdditionalText8;
	private JTextArea txtAdditionalText9;

	// UI components for labels and buttons
	private JButton btnClose;
	private JLabel lblProducer;
	private JLabel lblAlarmClassId;
	private JLabel lblTSComing;
	private JLabel lblMessageType;
	private JLabel lblAlarmId;
	private JLabel lblTsGoing;
	private JLabel lblState;
	private JLabel lblAlarmNo;
	private JLabel lblTSAck;
	private JLabel lblAlarmText;
	private JTextArea txtAlarmText;
	private JLabel lblInfoText;
	private JTextArea txtInfoText;
	private JLabel lblAdditionalText1;
	private JLabel lblAdditionalText2;
	private JLabel lblAdditionalText3;
	private JLabel lblAdditionalText4;
	private JLabel lblAdditionalText5;
	private JLabel lblAdditionalText6;
	private JLabel lblAdditionalText7;
	private JLabel lblAdditionalText8;
	private JLabel lblAdditionalText9;

	/**
	 * Creates the dialog and initializes the UI with the given alarm and resource bundle.
	 * @param alarm The alarm notification to display.
	 * @param rb The resource bundle for localized UI texts.
	 */
	public AlarmDetails(AlarmNotification alarm, ResourceBundle rb) {

		resources = rb;

		textField.setColumns(10); // Set column count for internal text field
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE); // Exclude from modal dialogs
		setResizable(false); // Window is not resizable
		setTitle("AlarmDetails"); // Set window title
		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(AlarmDetails.class.getResource("/example_app/btnAlarmMessages.Image.png")));
		getContentPane().setLayout(null); // Use absolute positioning

		// Create and configure close button
		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e); // Handle close button click
			}
		});
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setIcon(new ImageIcon(AlarmDetails.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(1263, 754, 68, 68);
		getContentPane().add(btnClose);

		// Main panel for alarm details
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 11, 1321, 733);
		getContentPane().add(panel);
		panel.setLayout(null);

		// UI label and text area setup for alarm details
		// Producer label and text area
		lblProducer = new JLabel("Producer");
		lblProducer.setBounds(10, 23, 86, 20);
		lblProducer.setPreferredSize(new Dimension(70, 20));
		lblProducer.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblProducer);

		txtProducer = new JTextArea();
		txtProducer.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtProducer.setMinimumSize(new Dimension(5, 20));
		txtProducer.setBounds(102, 23, 300, 22);
		txtProducer.setPreferredSize(new Dimension(100, 20));
		txtProducer.setEditable(false);
		panel.add(txtProducer);

		// Alarm class label and text area
		lblAlarmClassId = new JLabel("ClassId");
		lblAlarmClassId.setBounds(435, 25, 99, 20);
		lblAlarmClassId.setPreferredSize(new Dimension(70, 20));
		panel.add(lblAlarmClassId);

		txtClassId = new JTextArea();
		txtClassId.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtClassId.setMinimumSize(new Dimension(5, 20));
		txtClassId.setBounds(544, 23, 300, 22);
		txtClassId.setPreferredSize(new Dimension(100, 20));
		txtClassId.setEditable(false);
		panel.add(txtClassId);

		// Timestamp coming label and text area
		lblTSComing = new JLabel("Coming");
		lblTSComing.setBounds(867, 27, 120, 20);
		lblTSComing.setPreferredSize(new Dimension(70, 20));
		panel.add(lblTSComing);

		txtTSComing = new JTextArea();
		txtTSComing.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtTSComing.setMinimumSize(new Dimension(5, 20));
		txtTSComing.setBounds(991, 25, 300, 22);
		txtTSComing.setPreferredSize(new Dimension(100, 20));
		txtTSComing.setEditable(false);
		panel.add(txtTSComing);

		// Message type label and text area
		lblMessageType = new JLabel("MessageType");
		lblMessageType.setBounds(10, 54, 86, 20);
		lblMessageType.setPreferredSize(new Dimension(70, 20));
		lblMessageType.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblMessageType);

		txtMessageType = new JTextArea();
		txtMessageType.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtMessageType.setMinimumSize(new Dimension(5, 20));
		txtMessageType.setBounds(102, 56, 300, 22);
		txtMessageType.setPreferredSize(new Dimension(100, 20));
		txtMessageType.setEditable(false);
		panel.add(txtMessageType);

		// Alarm ID label and text area
		lblAlarmId = new JLabel("AlarmId");
		lblAlarmId.setBounds(435, 56, 99, 20);
		lblAlarmId.setPreferredSize(new Dimension(70, 20));
		panel.add(lblAlarmId);

		txtAlarmId = new JTextArea();
		txtAlarmId.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAlarmId.setMinimumSize(new Dimension(5, 20));
		txtAlarmId.setBounds(544, 56, 300, 22);
		txtAlarmId.setPreferredSize(new Dimension(100, 20));
		txtAlarmId.setEditable(false);
		panel.add(txtAlarmId);

		// Timestamp going label and text area
		lblTsGoing = new JLabel("Going");
		lblTsGoing.setBounds(867, 58, 120, 20);
		lblTsGoing.setPreferredSize(new Dimension(70, 20));
		panel.add(lblTsGoing);

		txtTsGoing = new JTextArea();
		txtTsGoing.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtTsGoing.setMinimumSize(new Dimension(5, 20));
		txtTsGoing.setBounds(991, 56, 300, 22);
		txtTsGoing.setPreferredSize(new Dimension(100, 20));
		txtTsGoing.setEditable(false);
		panel.add(txtTsGoing);

		// State label and text area
		lblState = new JLabel("State");
		lblState.setBounds(10, 92, 86, 14);
		lblState.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblState);

		txtState = new JTextArea();
		txtState.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtState.setMinimumSize(new Dimension(5, 20));
		txtState.setBounds(102, 89, 300, 22);
		txtState.setPreferredSize(new Dimension(100, 20));
		txtState.setEditable(false);
		panel.add(txtState);

		// Alarm number label and text area
		lblAlarmNo = new JLabel("AlarmNo.");
		lblAlarmNo.setBounds(435, 94, 99, 14);
		panel.add(lblAlarmNo);

		txtAlarmNo = new JTextArea();
		txtAlarmNo.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAlarmNo.setMinimumSize(new Dimension(5, 20));
		txtAlarmNo.setBounds(544, 89, 300, 22);
		txtAlarmNo.setPreferredSize(new Dimension(100, 20));
		txtAlarmNo.setEditable(false);
		panel.add(txtAlarmNo);

		// Acknowledged timestamp label and text area
		lblTSAck = new JLabel("Acknowledged");
		lblTSAck.setBounds(867, 96, 120, 14);
		panel.add(lblTSAck);

		txtTSAck = new JTextArea();
		txtTSAck.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtTSAck.setMinimumSize(new Dimension(5, 20));
		txtTSAck.setBounds(991, 89, 300, 22);
		txtTSAck.setPreferredSize(new Dimension(100, 20));
		txtTSAck.setEditable(false);
		panel.add(txtTSAck);

		// Alarm text label and text area
		lblAlarmText = new JLabel("AlarmText");
		lblAlarmText.setBounds(10, 259, 86, 14);
		lblAlarmText.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAlarmText);

		// Info text area
		txtInfoText = new JTextArea();
		txtInfoText.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtInfoText.setBounds(102, 131, 1189, 76);
		txtInfoText.setEditable(false);
		txtInfoText.setRows(4);
		txtInfoText.setColumns(153);
		panel.add(txtInfoText);

		// Additional text 9 label and text area
		lblAdditionalText9 = new JLabel("AdditionalText9");
		lblAdditionalText9.setBounds(10, 685, 86, 14);
		lblAdditionalText9.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText9);

		txtAlarmText = new JTextArea();
		txtAlarmText.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAlarmText.setBounds(102, 230, 1189, 76);
		txtAlarmText.setRows(4);
		txtAlarmText.setEditable(false);
		txtAlarmText.setColumns(153);
		panel.add(txtAlarmText);

		// Info text label
		lblInfoText = new JLabel("InfoText");
		lblInfoText.setBounds(10, 159, 86, 14);
		lblInfoText.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblInfoText);

		// Additional text 1 label and text area
		txtAdditionalText1 = new JTextArea();
		txtAdditionalText1.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText1.setBounds(102, 328, 1189, 22);
		txtAdditionalText1.setEditable(false);
		txtAdditionalText1.setColumns(153);
		panel.add(txtAdditionalText1);

		lblAdditionalText1 = new JLabel("AdditionalText1");
		lblAdditionalText1.setBounds(10, 330, 86, 14);
		lblAdditionalText1.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText1);

		// Additional text 2 label and text area
		txtAdditionalText2 = new JTextArea();
		txtAdditionalText2.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText2.setBounds(102, 372, 1189, 22);
		txtAdditionalText2.setEditable(false);
		txtAdditionalText2.setColumns(153);
		panel.add(txtAdditionalText2);

		lblAdditionalText2 = new JLabel("AdditionalText2");
		lblAdditionalText2.setBounds(10, 374, 86, 14);
		lblAdditionalText2.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText2);

		// Additional text 3 label and text area
		txtAdditionalText3 = new JTextArea();
		txtAdditionalText3.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText3.setBounds(102, 416, 1189, 22);
		txtAdditionalText3.setEditable(false);
		txtAdditionalText3.setColumns(153);
		panel.add(txtAdditionalText3);

		lblAdditionalText3 = new JLabel("AdditionalText3");
		lblAdditionalText3.setBounds(10, 418, 86, 14);
		lblAdditionalText3.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText3);

		// Additional text 4 label and text area
		txtAdditionalText4 = new JTextArea();
		txtAdditionalText4.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText4.setBounds(102, 460, 1189, 22);
		txtAdditionalText4.setEditable(false);
		txtAdditionalText4.setColumns(153);
		panel.add(txtAdditionalText4);

		lblAdditionalText4 = new JLabel("AdditionalText4");
		lblAdditionalText4.setBounds(10, 462, 86, 14);
		lblAdditionalText4.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText4);

		// Additional text 5 label and text area
		txtAdditionalText5 = new JTextArea();
		txtAdditionalText5.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText5.setBounds(102, 504, 1189, 22);
		txtAdditionalText5.setEditable(false);
		txtAdditionalText5.setColumns(153);
		panel.add(txtAdditionalText5);

		lblAdditionalText5 = new JLabel("AdditionalText5");
		lblAdditionalText5.setVerticalAlignment(SwingConstants.BOTTOM);
		lblAdditionalText5.setBounds(10, 506, 86, 14);
		lblAdditionalText5.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText5);

		// Additional text 6 label and text area
		txtAdditionalText6 = new JTextArea();
		txtAdditionalText6.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText6.setBounds(102, 548, 1189, 22);
		txtAdditionalText6.setEditable(false);
		txtAdditionalText6.setColumns(153);
		panel.add(txtAdditionalText6);

		lblAdditionalText6 = new JLabel("AdditionalText6");
		lblAdditionalText6.setBounds(10, 550, 86, 14);
		lblAdditionalText6.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText6);

		// Additional text 7 label and text area
		txtAdditionalText7 = new JTextArea();
		txtAdditionalText7.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText7.setBounds(102, 592, 1189, 22);
		txtAdditionalText7.setEditable(false);
		txtAdditionalText7.setColumns(153);
		panel.add(txtAdditionalText7);

		lblAdditionalText7 = new JLabel("AdditionalText7");
		lblAdditionalText7.setBounds(10, 594, 86, 14);
		lblAdditionalText7.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText7);

		// Additional text 8 label and text area
		txtAdditionalText8 = new JTextArea();
		txtAdditionalText8.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText8.setBounds(102, 636, 1189, 22);
		txtAdditionalText8.setEditable(false);
		txtAdditionalText8.setColumns(153);
		panel.add(txtAdditionalText8);

		lblAdditionalText8 = new JLabel("AdditionalText8");
		lblAdditionalText8.setVerticalAlignment(SwingConstants.BOTTOM);
		lblAdditionalText8.setBounds(10, 638, 86, 14);
		lblAdditionalText8.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblAdditionalText8);

		// Additional text 9 text area
		txtAdditionalText9 = new JTextArea();
		txtAdditionalText9.setFont(new Font("Monospaced", Font.BOLD, 13));
		txtAdditionalText9.setBounds(102, 683, 1189, 22);
		txtAdditionalText9.setEditable(false);
		txtAdditionalText9.setColumns(153);
		panel.add(txtAdditionalText9);
		initialize();

		setFields(alarm);

	}

	/**
	 * Handles the close button action. Hides the window.
	 * @param e The action event.
	 */
	protected void btnClose_actionPerformed(ActionEvent e) {
		this.setVisible(false); // Hide the window
	}

	/**
	 * Initializes the look and feel and sets localized texts for all labels and buttons.
	 */
	private void initialize() {

		// Set global look and feel platform independent
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

			@SuppressWarnings("rawtypes")
			java.util.Enumeration keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
					UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 11)); // Set default font
				}
			}

			// Optionally set Windows look and feel
			// UIManager.setLookAndFeel(
			// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}

		this.setBounds(15, 10, 1357, 878); // Set window size and position

		this.getContentPane().setLayout(null);

		// Set localized texts for all UI elements
		this.btnClose.setText(resources.getString("btnClose_Text"));
		this.lblProducer.setText(resources.getString("lblProducer_Text"));
		this.lblMessageType.setText(resources.getString("lblMessageType_Text"));
		this.lblState.setText(resources.getString("lblState_Text"));
		this.lblAlarmClassId.setText(resources.getString("lblAlarmClassId_Text"));
		this.lblAlarmId.setText(resources.getString("lblAlarmId_Text"));
		this.lblAlarmNo.setText(resources.getString("lblAlarmNo_Text"));
		this.lblTSComing.setText(resources.getString("lblTSComing_Text"));
		this.lblTsGoing.setText(resources.getString("lblTsGoing_Text"));
		this.lblTSAck.setText(resources.getString("lblTSAck_Text"));
		this.lblInfoText.setText(resources.getString("lblInfoText_Text"));
		this.lblAlarmText.setText(resources.getString("lblAlarmText_Text"));
		this.lblAdditionalText1.setText(resources.getString("lblAdditionalText1_Text"));
		this.lblAdditionalText2.setText(resources.getString("lblAdditionalText2_Text"));
		this.lblAdditionalText3.setText(resources.getString("lblAdditionalText3_Text"));
		this.lblAdditionalText4.setText(resources.getString("lblAdditionalText4_Text"));
		this.lblAdditionalText5.setText(resources.getString("lblAdditionalText5_Text"));
		this.lblAdditionalText6.setText(resources.getString("lblAdditionalText6_Text"));
		this.lblAdditionalText7.setText(resources.getString("lblAdditionalText7_Text"));
		this.lblAdditionalText8.setText(resources.getString("lblAdditionalText8_Text"));
		this.lblAdditionalText9.setText(resources.getString("lblAdditionalText9_Text"));
	}

	/**
	 * Sets the fields of the form with the data from the given alarm.
	 * Handles localization and formatting of alarm text entries and timestamps.
	 * @param alarm The alarm notification to display.
	 */
	private void setFields(AlarmNotification alarm) {
		try {

			// Set the window title to the alarm id
			setTitle("Alarm Details " + alarm.getAlarmId());

			// Determine locale for text entries
			Locale appLocale = Locale.getDefault();
			Locale textLocale = alarm.getTextCultureInfos().containsKey(appLocale) ? appLocale : alarm.getTextCultureInfos().values().iterator().next();

			// Formatter for Instants (optional: .withLocale(appLocale))
			DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(appLocale)
					.withZone(ZoneId.systemDefault());

			// Set alarm details in the UI
			txtProducer.setText(alarm.getAlarmProducer().toString());
			txtAlarmId.setText(alarm.getAlarmId().toString());
			txtMessageType.setText(alarm.getMessageType().toString());
			txtState.setText(alarm.getAlarmState().toString());
			txtClassId.setText(alarm.getAlarmClass().toString());
			txtAlarmNo.setText(alarm.getAlarmNumber().toString());

			// Format and set timestamps (Instant -> String)
			txtTSComing.setText(alarm.getTimeStampComing() != null ? dtf.format(alarm.getTimeStampComing()) : "");
			txtTsGoing.setText(alarm.getTimeStampGoing() != null ? dtf.format(alarm.getTimeStampGoing()) : "");
			txtTSAck.setText(alarm.getTimeStampAck() != null ? dtf.format(alarm.getTimeStampAck()) : "");

			// Filter text entries by type and locale
			Map<Locale, List<PlcAlarmTextEntry>> textsByLocale = alarm.getAlarmTextEntries().stream()
					.collect(Collectors.groupingBy(PlcAlarmTextEntry::getCulture));

			List<PlcAlarmTextEntry> entries = textsByLocale.getOrDefault(textLocale, Collections.emptyList());

			// Helper function: get text by type
			Function<eAlarmTextType, String> getText = type -> entries.stream()
					.filter(e -> e.getAlarmTextType() == type).map(PlcAlarmTextEntry::getText).findFirst().orElse("");

			// Set all text fields for alarm texts
			txtInfoText.setText(getText.apply(eAlarmTextType.InfoText));
			txtAlarmText.setText(getText.apply(eAlarmTextType.AlarmText));
			txtAdditionalText1.setText(getText.apply(eAlarmTextType.AdditionalText1));
			txtAdditionalText2.setText(getText.apply(eAlarmTextType.AdditionalText2));
			txtAdditionalText3.setText(getText.apply(eAlarmTextType.AdditionalText3));
			txtAdditionalText4.setText(getText.apply(eAlarmTextType.AdditionalText4));
			txtAdditionalText5.setText(getText.apply(eAlarmTextType.AdditionalText5));
			txtAdditionalText6.setText(getText.apply(eAlarmTextType.AdditionalText6));
			txtAdditionalText7.setText(getText.apply(eAlarmTextType.AdditionalText7));
			txtAdditionalText8.setText(getText.apply(eAlarmTextType.AdditionalText8));
			txtAdditionalText9.setText(getText.apply(eAlarmTextType.AdditionalText9));

		} catch (Exception ex) {
			// Show error dialog if something goes wrong
			JOptionPane.showMessageDialog(
			        this,                                                 // parent component
			        "Error while creating alarm details form:\n" + ex.getMessage(), // message
			        "Error",                                               // title
			        JOptionPane.ERROR_MESSAGE                              // icon
			    );
		}
	}
}