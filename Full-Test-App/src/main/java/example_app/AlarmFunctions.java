package example_app;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

import java.awt.Color;

import example_app.DisabledJPanel.DisabledJPanel;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextPane;

import java.awt.SystemColor;

import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import com.indian.plccom.fors7.*;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.swing.border.LineBorder;

/**
 * AlarmFunctions is a JFrame-based UI for displaying and managing PLC alarm and information notifications.
 * It provides tables for active alarms and information messages, allows acknowledging alarms,
 * and displays detailed alarm information in a dialog.
 *
 * Implements AlarmNotificationListener to receive alarm events from the PLC device.
 */
public class AlarmFunctions extends JFrame implements AlarmNotificationListener {

	private static final long serialVersionUID = 1L;
	private SymbolicDevice mDevice; // The PLC device connection
	private ResourceBundle resources; // Resource bundle for localization
	@SuppressWarnings("unused")
	private JPanel grbAlarms; // Panel for alarm messages
	private JPanel grbInformations; // Panel for information messages
	private DisabledJPanel panAlarm; // Overlay panel for disabling alarm area
	private DisabledJPanel panInformations; // Overlay panel for disabling info area
	private JTable lvAlarms; // Table for alarms
	private DefaultTableModel alarmTableModel; // Model for alarm table
	DefaultTableModel informationsTableModel; // Model for information table
	private JTable lvInformations; // Table for information messages
	private JButton btnClose; // Button to close the window
	private JTextPane txtInfoAlarms; // Info text for alarms
	private JButton btnAcknowledge; // Button to acknowledge alarms

	/**
	 * Constructs the AlarmFunctions dialog.
	 * @param Device The PLC device to connect to.
	 * @param rb ResourceBundle for localized UI texts.
	 */
	public AlarmFunctions(PLCcomCoreDevice Device, ResourceBundle rb) {
		this.resources = rb;
		setTitle("ReadWriteSymbolic");
		initialize(); // Build the UI and set up all components
		this.mDevice = (SymbolicDevice) Device;
	}

	/**
	 * Initializes the UI components and layout.
	 */
	private void initialize() {

		// Set global look and feel to be platform independent and set default font
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
				.getImage(AlarmFunctions.class.getResource("/example_app/btnReadCollection.Image.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// Add window listeners for open/close events
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				formWindowOpened(arg0); // Set up UI texts and load alarms on open
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing(e); // Decrement dialog count on close
			}
		});

		this.setBounds(15, 10, 1040, 857); // Set window size and position
		this.getContentPane().setLayout(null); // Use absolute positioning

		// --- Alarm Panel Setup ---
		this.grbAlarms = new JPanel();
		this.grbAlarms.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"Alarm Messages, doubleclick for details", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		this.grbAlarms.setBounds(6, 68, 1008, 289);
		this.grbAlarms.setLayout(null);
		this.getContentPane().add(grbAlarms);

		// 1. Define the table model for alarms
		alarmTableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "", "MessageType", "State", "Id", "Timestamp", "Alarmtext", "Tag" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false; // Make all cells non-editable
			}

			@SuppressWarnings("rawtypes")
			private final Class[] columnTypes = { String.class, String.class, String.class, String.class, String.class,
					String.class, AlarmNotification.class };

			@Override
			public Class<?> getColumnClass(int col) {
				return columnTypes[col];
			}
		};

		// 2. Create the alarm table and configure it
		lvAlarms = new JTable(alarmTableModel);
		lvAlarms.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Disable auto-resize to allow custom widths
		lvAlarms.setDefaultRenderer(Object.class, new MyTableCellRenderer(lvAlarms.getDefaultRenderer(Object.class)));
		((DefaultTableCellRenderer) lvAlarms.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
		lvAlarms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected at a time
		lvAlarms.setFillsViewportHeight(true); // Table fills the viewport

		// 3. Add the table to a scroll pane and container panel
		JScrollPane scrollPane = new JScrollPane(lvAlarms);
		JPanel lvAlarmsContainer = new JPanel(new BorderLayout());
		lvAlarmsContainer.setBounds(10, 22, 988, 263);
		lvAlarmsContainer.add(scrollPane, BorderLayout.CENTER);
		grbAlarms.add(lvAlarmsContainer);

		// 4. Set column widths after table is added to the scroll pane
		// set
		// and, very importantly, delay them in the EDT so that Swing takes them over:
		SwingUtilities.invokeLater(() -> {
			int[] widths = { 0, 150, 150, 40, 120, 500, 0 };
			TableColumnModel cm = lvAlarms.getColumnModel();
			for (int i = 0; i < widths.length; i++) {
				cm.getColumn(i).setResizable(true);
				cm.getColumn(i).setPreferredWidth(widths[i]);
				if (widths[i] == 0) {
					// Completely hide the first and last columns (e.g., for internal use)
					cm.getColumn(i).setMinWidth(0);
					cm.getColumn(i).setMaxWidth(0);
				}
			}
			lvAlarms.revalidate();
			lvAlarms.repaint();
		});

		// 5. Add selection listener to enable/disable acknowledge button
		lvAlarms.getSelectionModel().addListSelectionListener(e -> {
			lvAlarms_listSelectionChange(e);
		});

		// 6. Add mouse listener for double-click to show alarm details
		lvAlarms.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    	lvAlarms_mouseClicked(e);
		    }
		});

		// 7. Overlay panel to visually disable alarm area if needed
		panAlarm = new DisabledJPanel(grbAlarms);
		panAlarm.setBounds(grbAlarms.getBounds());
		panAlarm.setDisabledColor(new Color(240, 240, 240, 100));
		panAlarm.setEnabled(true);
		this.getContentPane().add(panAlarm);

		// --- Information Panel Setup ---
		grbInformations = new JPanel();
		grbInformations.setLayout(null);
		grbInformations.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"Information Messages, doubleclick for details", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		grbInformations.setBounds(0, 436, 1008, 296);
		getContentPane().add(grbInformations);

		// 1. Define the table model for information messages
		informationsTableModel = new DefaultTableModel(new Object[][] {},
				new String[] { "", "MessageType", "Timestamp", "Alarmtext", "Tag" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false; // Make all cells non-editable
			}

			@SuppressWarnings("rawtypes")
			private final Class[] columnTypes = { String.class, String.class, String.class, String.class,
					AlarmNotification.class };

			@Override
			public Class<?> getColumnClass(int col) {
				return columnTypes[col];
			}
		};

		// 2. Create the information table and configure it
		lvInformations = new JTable(informationsTableModel);
		lvInformations.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		lvInformations.setDefaultRenderer(Object.class,
				new MyTableCellRenderer(lvInformations.getDefaultRenderer(Object.class)));
		((DefaultTableCellRenderer) lvInformations.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.LEFT);
		lvInformations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lvInformations.setFillsViewportHeight(true);

		// 3. Add the table to a scroll pane and container panel
		JScrollPane scrollPaneInformations = new JScrollPane(lvInformations);
		JPanel lvInformationsContainer = new JPanel(new BorderLayout());
		lvInformationsContainer.setBounds(10, 22, 988, 263);
		lvInformationsContainer.add(scrollPaneInformations, BorderLayout.CENTER);
		grbInformations.add(lvInformationsContainer);

		// 4. Set column widths after table is added to the scroll pane
		SwingUtilities.invokeLater(() -> {
			int[] widths = { 0, 120, 120, 722,0 };
			TableColumnModel cm = lvInformations.getColumnModel();
			for (int i = 0; i < widths.length; i++) {
				cm.getColumn(i).setResizable(true);
				cm.getColumn(i).setPreferredWidth(widths[i]);
				if (widths[i] == 0) {
					cm.getColumn(i).setMinWidth(0);
					cm.getColumn(i).setMaxWidth(0);
				}
			}
			lvInformations.revalidate();
			lvInformations.repaint();
		});

		// 5. Overlay panel to visually disable information area if needed
		panInformations = new DisabledJPanel(grbInformations);
		panInformations.setBounds(grbInformations.getBounds());
		panInformations.setDisabledColor(new Color(240, 240, 240, 100));
		panInformations.setEnabled(true);
		this.getContentPane().add(panInformations);

		// --- Button and Info Panel Setup ---
		// Close button to exit the dialog
		btnClose = new JButton("<html><center>close</center><center>window</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e); // Handle close button click
			}
		});
		btnClose.setIcon(new ImageIcon(AlarmFunctions.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setToolTipText("");
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(933, 739, 68, 68);
		getContentPane().add(btnClose);

		// Info panel at the top with logo and description
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setLayout(null);
		panel.setBackground(SystemColor.info);
		panel.setBounds(196, 4, 598, 59);
		getContentPane().add(panel);

		// Info text for alarms
		txtInfoAlarms = new JTextPane();
		txtInfoAlarms.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtInfoAlarms.setText("Read or write your symbolically defined variables");
		txtInfoAlarms.setEditable(false);
		txtInfoAlarms.setBorder(null);
		txtInfoAlarms.setBackground(SystemColor.info);
		txtInfoAlarms.setBounds(66, 2, 522, 50);
		panel.add(txtInfoAlarms);

		// Icon for the info panel
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(AlarmFunctions.class.getResource("/example_app/pictureBox1.Image.png")));
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(2, 2, 32, 32);
		panel.add(label);

		// Company logo
		JLabel lblLogo = new JLabel();
		lblLogo.setVerticalAlignment(SwingConstants.TOP);
		lblLogo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblLogo.setBounds(16, 4, 130, 60);
		ImageIcon originalIcon = new ImageIcon(
				Main.class.getResource("/example_app/indi.logo2021.1_rgb_PLCcom_130_60.png"));
		Image originalImage = originalIcon.getImage();
		Image scaledImage = originalImage.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(),
				Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(scaledImage));
		getContentPane().add(lblLogo);

		// Button to acknowledge (confirm) alarms
		btnAcknowledge = new JButton("<html><center>confirm</center><center>Alarm</center></html>");
		btnAcknowledge.setEnabled(false); // Only enabled if an acknowledgeable alarm is selected
		btnAcknowledge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAcknowledge_actionPerformed(e); // Handle acknowledge button click
			}
		});
		btnAcknowledge.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnAcknowledge.setToolTipText("");
		btnAcknowledge.setMargin(new Insets(0, 0, 0, 0));
		btnAcknowledge.setIcon(
				new ImageIcon(AlarmFunctions.class.getResource("/example_app/btnAcknowledge.Image.png")));
		btnAcknowledge.setHorizontalTextPosition(SwingConstants.CENTER);
		btnAcknowledge.setBounds(933, 364, 68, 68);
		getContentPane().add(btnAcknowledge);

	}

	/**
	 * Handles double-clicks on the alarm table to show alarm details.
	 * @param e MouseEvent
	 */
	protected void lvAlarms_mouseClicked(MouseEvent e) {
		// Only respond to double-clicks with the left mouse button
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            // Determine which row was clicked in the view
            int viewRow = lvAlarms.rowAtPoint(e.getPoint());
            if (viewRow < 0) return;  // Click was outside any row

            // If using sorting/filtering, map to model index
            int modelRow = lvAlarms.convertRowIndexToModel(viewRow);

            // Retrieve the AlarmNotification from the hidden tag column
            AlarmNotification alarm =
                (AlarmNotification) alarmTableModel.getValueAt(modelRow, 6);

            // Open the alarm details dialog on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                AlarmDetails alarmDetails =
                    new AlarmDetails( alarm, resources);
                // Center the dialog relative to this window
                alarmDetails.setLocationRelativeTo(AlarmFunctions.this);
                alarmDetails.setVisible(true);
            });
        }
	}

	/**
	 * Handles selection changes in the alarm table to enable/disable the acknowledge button.
	 * @param e ListSelectionEvent
	 */
	private void lvAlarms_listSelectionChange(ListSelectionEvent e) {
		// Only react if the selection is fixed (not adjusting)
		if (e.getValueIsAdjusting())
			return;

		// Check all selected rows for acknowledgeable alarms
		boolean enable = false;
		for (int viewRow : lvAlarms.getSelectedRows()) {
			// Convert view index to model index if using a sorter/filter
			int modelRow = lvAlarms.convertRowIndexToModel(viewRow);

			// Retrieve the alarm object from the hidden column
			AlarmNotification alarm = (AlarmNotification) alarmTableModel.getValueAt(modelRow, 6);

			// Enable if at least one acknowledgeable alarm is selected
			if (alarm.getMessageType() == eAlarmMessageType.AcknowledgeableAlarm) {
				enable = true;
				break;
			}
		}

		// Enable or disable the acknowledge button
		btnAcknowledge.setEnabled(enable);

	}

	/**
	 * Handles the window closing event to update dialog count.
	 * @param e WindowEvent
	 */
	protected void formWindowClosing(WindowEvent e) {
		Main.CountOpenDialogs--; // Decrement the count of open dialogs
	}

	/**
	 * Handles the window opened event to set localized texts and load alarms.
	 * @param arg0 WindowEvent
	 */
	protected void formWindowOpened(WindowEvent arg0) {

		// Set localized texts for all UI elements
		this.grbAlarms.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), resources.getString("grbAlarm_Text"),
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		this.grbInformations.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				resources.getString("grbInformations_Text"), TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));

		this.btnClose.setText(resources.getString("btnClose_Text"));
		this.txtInfoAlarms.setText(resources.getString("txtInfoAlarms_Text"));

		// Set column headers for alarm and information tables
		lvAlarms.getColumnModel().getColumn(1).setHeaderValue(resources.getString("colMessageType_Text"));
		lvAlarms.getColumnModel().getColumn(4).setHeaderValue(resources.getString("colTimestamp_Text"));
		lvAlarms.getColumnModel().getColumn(5).setHeaderValue(resources.getString("colAlarmText_Text"));
		lvAlarms.getTableHeader().repaint();

		lvInformations.getColumnModel().getColumn(1).setHeaderValue(resources.getString("colMessageType_Text"));
		lvInformations.getColumnModel().getColumn(2).setHeaderValue(resources.getString("colTimestamp_Text"));
		lvInformations.getColumnModel().getColumn(3).setHeaderValue(resources.getString("colAlarmText_Text"));
		lvInformations.getTableHeader().repaint();

		// Load alarms from the PLC and subscribe to alarm notifications
		LoadAlarms();
		SubscribeAlarms();
	}

	/**
	 * Loads the current alarms from the PLC device and populates the alarm table.
	 */
	private void LoadAlarms() {
		try {

			// Request all active alarms from the PLC device
			BrowseAlarmsRequest browseAlarmsRequest = new BrowseAlarmsRequest(Locale.getDefault());
			BrowseAlarmsResult result = mDevice.browseActiveAlarms(browseAlarmsRequest);

			if (result.isQualityGood()) {
				DefaultTableModel model = (DefaultTableModel) lvAlarms.getModel();
				model.setRowCount(0); // Clear the alarm table

				// Sort alarms by priority
				List<AlarmNotification> sorted = result.getAlarms().stream()
						.sorted(Comparator.comparing(AlarmNotification::getAlarmPriority)).collect(Collectors.toList());

				DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
				ZoneId zid = ZoneId.systemDefault();

				for (AlarmNotification alarm : sorted) {
					Object[] lvi = new Object[7];
					lvi[0] = alarm.getAlarmId();
					lvi[1] = alarm.getMessageType().toString();
					lvi[2] = alarm.getAlarmState().toString();
					lvi[3] = String.valueOf(alarm.getAlarmNumber());

					// Convert Instant to LocalDateTime String for display
					Instant ts = alarm.getTimeStampComing();
					lvi[4] = fmt.format(ts.atZone(zid));

					// Determine locale for text entries
					Locale appLocale = Locale.getDefault();

					//get key for locale
					Integer keyForLocale = null;
					for (Map.Entry<Integer, Locale> entry : alarm.getTextCultureInfos().entrySet()) {
					    if (entry.getValue().equals(appLocale)) {
					        keyForLocale = entry.getKey();
					        break;
					    }
					}

					// Determine the best locale for alarm text
					Locale textLocale = (keyForLocale != null)
					    ? appLocale
					    : alarm.getTextCultureInfos().values().iterator().next();


					// Get the alarm text in the selected locale
					String text = alarm.getAlarmTextEntries().stream()
							.filter(e -> e.getCulture().equals(textLocale)
									&& e.getAlarmTextType() == eAlarmTextType.AlarmText)
							.findFirst().map(e -> e.getText()).orElse("");

					lvi[5] = text;

					lvi[6] = alarm; // Store the alarm object for later use (hidden column)

					model.addRow(lvi); // Add the alarm to the table
				}
			} else {
				// Show error dialog if the request failed
				JOptionPane.showMessageDialog(this,
						resources.getString("error_browsing_alarms") + "\n" + result.getMessage(), "",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			// Show error dialog for unexpected exceptions
			JOptionPane.showMessageDialog(this, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Subscribes to alarm notifications from the PLC device.
	 */
	private void SubscribeAlarms() {
		try {
			mDevice.addAlarmListener(this); // Register this class as an alarm listener

		} catch (Exception ex) {
			// Show error dialog if subscription fails
			JOptionPane.showMessageDialog(this, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Handles the close button action. Removes the alarm handler and hides the window.
	 * @param e ActionEvent
	 */
	private void btnClose_actionPerformed(ActionEvent e) {

		mDevice.removeAlarmNotificationHandler(this); // Unregister this class as an alarm handler
		this.setVisible(false); // Hide the window

		// Send form closing event to update dialog count
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

	/**
	 * Handles incoming alarm notifications and updates the UI accordingly.
	 * @param sender The sender of the notification.
	 * @param alarmNotification The alarm notification object.
	 */
	@Override
	public void onAlarm(Object sender, AlarmNotification alarmNotification) {

		if (alarmNotification == null)
			return; // Ignore null notifications

		// If called from a background thread, redirect to the Event Dispatch Thread
	    if (!SwingUtilities.isEventDispatchThread()) {
	        SwingUtilities.invokeLater(() -> onAlarm(sender, alarmNotification));
	        return;
	    }

		// Helper objects for time formatting
		ZoneId zone = ZoneId.systemDefault();
		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

		String alarmId = alarmNotification.getAlarmId().toString();
		switch (alarmNotification.getMessageType()) {
		case AcknowledgeableAlarm:
		case NonAcknowledgeableAlarm:
			// Remove alarm from table if it is no longer active or was canceled
			boolean shouldRemove = alarmNotification.getAlarmState() == eAlarmState.NotActive
					|| alarmNotification.getAlarmState() == eAlarmState.Canceled;

			if (shouldRemove) {
				// Remove all rows with matching alarm ID
				for (int row = 0; row < alarmTableModel.getRowCount(); row++) {
					if (alarmTableModel.getValueAt(row, 0).toString().equals(alarmId)) {
						alarmTableModel.removeRow(row);
						break;
					}
				}
			} else {
				boolean updated = false;

				// Determine locale for text entries
				Locale appLocale = Locale.getDefault();

				//get key for locale
				Integer keyForLocale = null;
				for (Map.Entry<Integer, Locale> entry : alarmNotification.getTextCultureInfos().entrySet()) {
				    if (entry.getValue().equals(appLocale)) {
				        keyForLocale = entry.getKey();
				        break;
				    }
				}

				// Determine the best locale for alarm text
				final Locale loc = (keyForLocale != null)
				    ? appLocale
				    : alarmNotification.getTextCultureInfos().values().iterator().next();

				// Try to update an existing row for this alarm
				for (int row = 0; row < alarmTableModel.getRowCount(); row++) {
					if (alarmTableModel.getValueAt(row, 0).toString().equals(alarmId)) {
						// Update all columns for the alarm
						alarmTableModel.setValueAt(alarmNotification.getMessageType().toString(), row, 1);
						alarmTableModel.setValueAt(alarmNotification.getAlarmState().toString(), row, 2);
						alarmTableModel.setValueAt(alarmNotification.getAlarmNumber(), row, 3);
						String ts = Optional.ofNullable(alarmNotification.getTimeStampComing())
								.map(i -> fmt.format(i.atZone(zone))).orElse("");
						alarmTableModel.setValueAt(ts, row, 4);
						String text = alarmNotification.getAlarmTextEntries().stream()
								.filter(a -> a.getCulture().equals(loc)
									&& a.getAlarmTextType() == eAlarmTextType.AlarmText)
								.map(a -> a.getText()).findFirst().orElse("");
						alarmTableModel.setValueAt(text, row, 5);
						alarmTableModel.setValueAt(alarmNotification, row, 6);
						updated = true;
					}
				}
				if (!updated) {
					// Add new entry if not found
					String type = alarmNotification.getMessageType().toString();
					String state = alarmNotification.getAlarmState().toString();
					int num = alarmNotification.getAlarmNumber().intValue();
					String timeStampComing = Optional.ofNullable(alarmNotification.getTimeStampComing())
							.map(i -> fmt.format(i.atZone(zone))).orElse("");

					String alarmText = alarmNotification.getAlarmTextEntries().stream()
							.filter(a -> a.getCulture().equals(loc) && a.getAlarmTextType() == eAlarmTextType.AlarmText)
							.map(a -> a.getText()).findFirst().orElse("");

					alarmTableModel.addRow(
						new Object[] { alarmId, type, state, num, timeStampComing, alarmText, alarmNotification });
				}
			}
			break;

		case InformationNotification:

			// Determine locale for text entries
			Locale appLocale = Locale.getDefault();

			//get key for locale
			Integer keyForLocale = null;
			for (Map.Entry<Integer, Locale> entry : alarmNotification.getTextCultureInfos().entrySet()) {
			    if (entry.getValue().equals(appLocale)) {
			        keyForLocale = entry.getKey();
			        break;
			    }
			}

			// Find the best locale for information text
			final Locale locInfo = (keyForLocale != null)
			    ? appLocale
			    : alarmNotification.getTextCultureInfos().values().iterator().next();


			String typeInfo = alarmNotification.getMessageType().toString();
			String tsInfo = Optional.ofNullable(alarmNotification.getTimeStampComing())
					.map(i -> fmt.format(i.atZone(zone))).orElse("");

			String txtInfo = alarmNotification.getAlarmTextEntries().stream()
					.filter(a -> a.getCulture().equals(locInfo) && a.getAlarmTextType() == eAlarmTextType.AlarmText)
					.map(a -> a.getText()).findFirst().orElse("");

			informationsTableModel.addRow(new Object[] { alarmId, typeInfo, tsInfo, txtInfo, alarmNotification });
			break;

		default:
			// No action for other types
		}
	}

	/**
	 * Handles the acknowledge button action to confirm selected alarms.
	 * @param e ActionEvent
	 */
	protected void btnAcknowledge_actionPerformed(ActionEvent e) {

		try {
			// Get indices of selected rows in the view
			int[] selectedRows = lvAlarms.getSelectedRows();
			if (selectedRows.length > 0) {
				for (int viewRow : selectedRows) {
					// Convert view row index to model index, if using a RowSorter/filter
					int modelRow = lvAlarms.convertRowIndexToModel(viewRow);

					// Retrieve the AlarmNotification object from the hidden tag column
					AlarmNotification alarm = (AlarmNotification) alarmTableModel.getValueAt(modelRow, 6);

					// Send acknowledgment request to the device
					OperationResult res = mDevice.ackAlarm(alarm.getAlarmId());

					// Show an error dialog if the operation quality is bad
					if (res.isQualityBad()) {
						JOptionPane.showMessageDialog(
							this, String.format("Operation not successful%nQuality: %s%nMessage: %s",
								res.getQuality(), res.getMessage()),
							"Acknowledgment Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} catch (Exception ex) {
			// Show a generic error dialog for any unexpected exceptions
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Unexpected Error", JOptionPane.ERROR_MESSAGE);

		}
	}
}
