package ohm.softa.a07;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		var test = getClass().getResource("main.fxml");
		Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
		stage.setTitle("Cafeteria");
		stage.setScene(new Scene(root, 800, 600));
		stage.show();
	}
}
