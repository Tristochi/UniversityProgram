package custom;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

// Used to store hidden information (id) in Combo boxes

public class CustomComboBox extends JComboBox {
	
	private static final long serialVersionUID = 1L;

	public CustomComboBox() {
		setRenderer(new ItemRenderer());
	}
	
	public void addCustomItem(int id, String string) {
		addItem(new Item(id, string));
	}
	
	public int getSelectedItemId() {
		Item item = (Item) getSelectedItem();
		return item.getId();
	}
	
	public String getSelectedItemString() {
		Item item = (Item) getSelectedItem();
		return item.getString();
	}
	
	public void setSelectedItem(int id, String string) {
		for(int i = 0; i < getItemCount(); i++) {
			Item item = (Item) getItemAt(i);
			
			if(id == item.getId() && string.equals(item.getString())) {
				setSelectedIndex(i);
			}
		}
	}
	
	class ItemRenderer extends BasicComboBoxRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			// only show the string value of Item
			if(value != null) {
				Item item = (Item)value;
				setText(item.getString());
			}
			
			return this;
		}
	}
	
	class Item {
		private int id;
		private String string;
		
		public Item(int id, String string) {
			this.id = id;
			this.string = string;
		}
		
		public int getId() {
			return id;
		}
		
		public String getString() {
			return string;
		}
	}
}
