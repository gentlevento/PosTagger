package Pos_tagger;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class TaggerController {
	@FXML
	private TextArea input_text;
	@FXML
	private Button submit_btn;

	@FXML
	private Label result;

	@FXML
	private Label tabular;

	@FXML
	private Label pos_list;

	@FXML
	private ListView tabular_list;

	@FXML
	private ListView tagging_list;

	private ObservableList<String> tabular_items = FXCollections.observableArrayList();
	private ObservableList<String> tagging_items = FXCollections.observableArrayList();

	private Main main;

	@FXML
	public void initialize() {
		tagging_list.setItems(tagging_items);
		tabular_list.setItems(tabular_items);
	}

	public void setMainApp(Main main) {
		this.main = main;

	}

	@FXML
	protected void submitOnclick(ActionEvent event) throws IOException {
		tabular_items = FXCollections.observableArrayList();
		tagging_items = FXCollections.observableArrayList();

		tagging_list.setItems(tagging_items);
		tabular_list.setItems(tabular_items);

		String input = input_text.getText();
		Korean_Tagger kt = new Korean_Tagger();
		Pos_tagger pt = new Pos_tagger();

		pt.tagging(input, kt);

		// tabular parsing list
		ArrayList<String> ss = kt.get_tp_rst();

		for (int i = 0; i < ss.size(); i++) {
			tabular_items.add(ss.get(i));
		}

		tabular_list.setItems(tabular_items);

		ss = pt.total_pos_list();

		// tagging list
		for (int i = 0; i < ss.size(); i++) {
			tagging_items.add(ss.get(i));
		}

		tagging_list.setItems(tagging_items);

		result.setText(pt.get_pos_rst());

	}

}
