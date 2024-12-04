package custom;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class ContextMenuListener extends MouseAdapter{
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.isPopupTrigger()) {
			showMenu(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.isPopupTrigger()) {
			showMenu(e);
		}
	}
	
	private void showMenu(MouseEvent e) {
		if(e.getComponent() instanceof JPanel pane) {
			CustomContextMenu menu = new CustomContextMenu(pane);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
