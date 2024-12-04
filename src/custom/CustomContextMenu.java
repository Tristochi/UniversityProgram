package custom;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class CustomContextMenu extends JPopupMenu{
	private JMenuItem menuItem;
	private JPanel pane;
	
	public CustomContextMenu(JPanel pane) {
		this.pane = pane;
		menuItem = new JMenuItem("Refresh Panel");
		menuItem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		add(menuItem);
		addMenuListener();
	}

	private void addMenuListener() {
		menuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(pane instanceof FormRefresh pane) {
					pane.refreshComponents();
				}
			}
		});
	}
}
