package ohm.softa.a07.controllers;


import javafx.application.Platform;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ohm.softa.a07.api.OpenMensaAPI;
import ohm.softa.a07.model.Meal;
import ohm.softa.a07.utils.MealsFilterUtility;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	private static final Logger logger = LogManager.getLogger(MainController.class);
	private static DateFormat date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private static String id = "256";
	private  OpenMensaAPI mensa;
	// use annotation to tie to component in XML
	@FXML
	private Button btnRefresh;

	@FXML
	private Button btnClose;
	@FXML
	private ListView<Meal> mealsList;

	@FXML
	private CheckBox chkVegetarian;

	private ObservableList<Meal> meals;

	public MainController(){
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.build();

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://openmensa.org/api/v2/")
			.client(client)
			.build();

		mensa  = retrofit.create(OpenMensaAPI.class);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the event handler (callback)
		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				//ObservableList<String> list = FXCollections.observableArrayList("Hans", "Dampf");
				loadData();
				meals = mealsList.getItems();
			}
		});
	}

	@FXML
	private void refreshItem(){
		loadData();
	}

	@FXML
	private void closeItem(){
		logger.debug("closing the application");

		Platform.exit();
		System.exit(0);
	}
	@FXML
	private void chkBoxVegetarian(){
		loadData();
	}

	private void loadData(){
		mensa.listMeals(date.format(new Date())).enqueue(new Callback<List<Meal>>() {
			@Override
			public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {

				if(!response.isSuccessful()){
					logger.error("Response was not successful");
					return;
				}

				if(response.body()==null) {
					logger.error("Response was successful, but the body is empty");
					return;
				}
				Platform.runLater(() -> {
					meals.clear();
					meals.addAll(chkVegetarian.isSelected()
						? MealsFilterUtility.filterForVegetarin(response.body())
						: response.body());
				});
			}

			@Override
			public void onFailure(Call<List<Meal>> call, Throwable t) {
				logger.error("Error occurred while fetching the API");
				Platform.runLater(() -> {
					meals.clear();
					new Alert(Alert.AlertType.ERROR, "Error occurred while fetching the API", ButtonType.OK).showAndWait();
				});
			}
		});
	}
}
