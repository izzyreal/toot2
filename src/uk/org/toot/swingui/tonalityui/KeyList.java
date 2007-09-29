package uk.org.toot.swingui.tonalityui;

import java.util.List;
import javax.swing.JList;

import uk.org.toot.music.tonality.*;

public class KeyList extends JList {

	public KeyList() {
		super();
	}

	public void setKeys(List<Key> keys) {
		setListData(keys.toArray());
	}
}
