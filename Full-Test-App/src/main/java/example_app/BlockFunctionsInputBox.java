package example_app;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.indian.plccom.fors7.eBlockType;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Insets;
import javax.swing.ImageIcon;

public class BlockFunctionsInputBox extends JDialog {

	private static final long serialVersionUID = 377769075778699279L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtBlockNumber;
	private JTextField txtEnterPW;
	private JComboBox<eBlockType> cmbBlockType;

	eBlockFunctionOpenMode BlockFunctionOpenMode = null;

	eBlockType BlockType = eBlockType.AllBlocks;
	int BlockNumber = 0;
	String EnterPW = "";
	boolean isCancel = true;
	private JButton btnAccept;
	private JButton btnReject;

	enum eBlockFunctionOpenMode {
		Only_BlockType, BlockType_and_BlockNumber, EnterPW
	}

	/**
	 * Create the dialog.
	 */
	public BlockFunctionsInputBox(eBlockFunctionOpenMode BlockFunctionOpenMode, boolean FocusAllBlock) {

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

		this.BlockFunctionOpenMode = BlockFunctionOpenMode;
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
			JLabel label = new JLabel("Type of Block");
			label.setBounds(10, 11, 113, 14);
			contentPanel.add(label);
		}
		{
			cmbBlockType = new JComboBox<eBlockType>();
			cmbBlockType.setBounds(133, 11, 84, 20);
			contentPanel.add(cmbBlockType);
		}
		{
			JLabel label = new JLabel("Number of Block");
			label.setBounds(10, 43, 113, 14);
			contentPanel.add(label);
		}
		{
			txtBlockNumber = new JTextField();
			txtBlockNumber.setText("1");
			txtBlockNumber.setColumns(10);
			txtBlockNumber.setBounds(133, 43, 84, 20);
			contentPanel.add(txtBlockNumber);
		}
		{
			JLabel label = new JLabel("Enter Password");
			label.setBounds(10, 71, 113, 14);
			contentPanel.add(label);
		}
		{
			txtEnterPW = new JTextField();
			txtEnterPW.setColumns(10);
			txtEnterPW.setBounds(133, 71, 84, 20);
			contentPanel.add(txtEnterPW);
		}

		cmbBlockType.setModel(new DefaultComboBoxModel<eBlockType>(eBlockType.values()));
		{
			btnAccept = new JButton("accept");
			btnAccept.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnAccept_actionPerformed(e);
				}
			});
			btnAccept.setIcon(new ImageIcon(
					BlockFunctionsInputBox.class.getResource("/example_app/btnAccept.Image.png")));
			btnAccept.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnAccept.setToolTipText("");
			btnAccept.setMargin(new Insets(0, 0, 0, 0));
			btnAccept.setHorizontalTextPosition(SwingConstants.CENTER);
			btnAccept.setBounds(67, 96, 68, 68);
			contentPanel.add(btnAccept);
		}
		{
			btnReject = new JButton("reject");
			btnReject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnReject_actionPerformed(e);
				}
			});
			btnReject.setIcon(new ImageIcon(
					BlockFunctionsInputBox.class.getResource("/example_app/btnReject.Image.png")));
			btnReject.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnReject.setToolTipText("");
			btnReject.setMargin(new Insets(0, 0, 0, 0));
			btnReject.setHorizontalTextPosition(SwingConstants.CENTER);
			btnReject.setBounds(149, 96, 68, 68);
			contentPanel.add(btnReject);
		}
		if (FocusAllBlock) {
			cmbBlockType.setSelectedItem(eBlockType.AllBlocks);
		} else {
			cmbBlockType.setSelectedItem(eBlockType.DB);
		}

		switch (this.BlockFunctionOpenMode) {
		case BlockType_and_BlockNumber:
			txtBlockNumber.setEnabled(true);
			cmbBlockType.setEnabled(true);
			txtEnterPW.setEnabled(false);
			break;
		case EnterPW:
			txtBlockNumber.setEnabled(false);
			cmbBlockType.setEnabled(false);
			txtEnterPW.setEnabled(true);
			break;
		case Only_BlockType:

			txtBlockNumber.setEnabled(false);
			cmbBlockType.setEnabled(true);
			txtEnterPW.setEnabled(false);
			break;
		}

	}

	private void btnAccept_actionPerformed(ActionEvent e) {
		try {

			switch (this.BlockFunctionOpenMode) {
			case BlockType_and_BlockNumber:
				BlockType = (eBlockType) cmbBlockType.getSelectedItem();
				BlockNumber = Integer.parseInt(txtBlockNumber.getText());
				EnterPW = "";
				break;
			case EnterPW:
				BlockType = (eBlockType) cmbBlockType.getSelectedItem();
				BlockNumber = 0;
				EnterPW = txtEnterPW.getText();
				break;
			case Only_BlockType:
				BlockType = (eBlockType) cmbBlockType.getSelectedItem();
				BlockNumber = 0;
				EnterPW = "";
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
}
