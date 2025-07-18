package example_app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

public class LicenseInputBox extends JDialog {

	private static final long serialVersionUID = 409661293931322018L;
	private ResourceBundle resources = null;

	private JTextPane txtInfoLI;
	private JPanel panel;
	private JLabel lblPic;
	private JLabel lblUser;
	private JLabel lblSerial;
	private JTextField txtUser;
	private JTextField txtSerial;
	private JButton btnClose;

	String User;
	String Serial;

	/**
	 * Create the dialog.
	 */
	public LicenseInputBox(ResourceBundle rb) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				formWindowOpened(e);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				formwindowClosing(e);
			}

		});

		resources = rb;

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

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(LicenseInputBox.class.getResource("/example_app/key.png")));
		setBounds(100, 100, 514, 246);
		getContentPane().setLayout(null);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(SystemColor.info);
		panel.setBounds(12, 11, 474, 62);
		getContentPane().add(panel);
		panel.setLayout(null);

		txtInfoLI = new JTextPane();
		txtInfoLI.setEditable(false);

		txtInfoLI.setBackground(SystemColor.info);

		txtInfoLI.setText(
				"Thank you for using our PLCCom component.\r\nBefore you can run PLCCom, please enter your license information.\r\nPlease take the data from the email that you received after download, or from your license certificate.");
		txtInfoLI.setBounds(56, 2, 413, 58);
		panel.add(txtInfoLI);

		lblPic = new JLabel();
		lblPic.setBounds(2, 2, 32, 32);
		panel.add(lblPic);
		lblPic.setVerticalAlignment(SwingConstants.TOP);
		lblPic.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPic.setIcon(new ImageIcon(Main.class.getResource("/example_app/pictureBox1.Image.png")));

		lblUser = new JLabel("user:");
		lblUser.setBounds(12, 87, 46, 14);
		getContentPane().add(lblUser);

		lblSerial = new JLabel("serial:");
		lblSerial.setBounds(12, 113, 46, 14);
		getContentPane().add(lblSerial);

		txtUser = new JTextField();
		txtUser.setBounds(54, 84, 432, 20);
		getContentPane().add(txtUser);

		txtSerial = new JTextField();
		txtSerial.setBounds(54, 110, 432, 20);
		getContentPane().add(txtSerial);

		btnClose = new JButton("<html><center>close</center></html>");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnClose_actionPerformed(e);
			}
		});
		btnClose.setIcon(new ImageIcon(LicenseInputBox.class.getResource("/example_app/btnClose.Image.png")));
		btnClose.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		btnClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClose.setBounds(420, 134, 68, 68);
		getContentPane().add(btnClose);
	}

	private void formWindowOpened(WindowEvent e) {
		// Set location to center screen
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (this.getWidth() / 2);
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (this.getHeight() / 2);
		this.setLocation(x, y);

		this.txtInfoLI.setText(resources.getString("txtInfoLI_Text"));
		this.btnClose.setText(resources.getString("btnClose_Text"));
	}

	private void formwindowClosing(WindowEvent e) {
		this.User = txtUser.getText();
		this.Serial = txtSerial.getText();
	}

	private void btnClose_actionPerformed(ActionEvent e) {

		this.User = txtUser.getText();
		this.Serial = txtSerial.getText();
		this.setVisible(false);

		// send form closing event
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

}
