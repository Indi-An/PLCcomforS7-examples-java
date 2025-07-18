package example_app;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.indian.plccom.fors7.eLogLevel;

//TableCellRenderer
class MyTableCellRenderer implements TableCellRenderer {

	private TableCellRenderer wrappedCellRenderer;

	public MyTableCellRenderer(TableCellRenderer cellRenderer) {
		super();
		this.wrappedCellRenderer = cellRenderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component rendererComponent = wrappedCellRenderer.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);
		if (column == 0) {
			if (hasFocus || isSelected) {

			} else {
				if (value.equals(eLogLevel.Error.toString())) {
					rendererComponent.setForeground(Color.red);
				} else if (value.equals(String.valueOf(Boolean.TRUE))) {
					rendererComponent.setForeground(Color.blue);
				} else {
					rendererComponent.setForeground(Color.black);
				}

			}
		} else {
			if (table.getModel().getValueAt(row, 0).equals(eLogLevel.Error.toString())) {
				rendererComponent.setForeground(Color.red);
			} else if (value.equals(String.valueOf(Boolean.TRUE))) {
				rendererComponent.setForeground(Color.blue);
			} else {
				rendererComponent.setForeground(Color.black);
			}
		}
		return rendererComponent;
	}
}
