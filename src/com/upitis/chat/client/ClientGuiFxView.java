package com.upitis.chat.client;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by upitis on 12.05.16.
 */
public class ClientGuiFxView extends Application {

    public static void main(String[] args){
        launch(args);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ClientForm.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        ClientGuiFxController controller = loader.getController();
        controller.setView(this);

        final Task task = controller.new SocketThread();
        new Thread(task).start();

        primaryStage.setTitle("Чат");
        primaryStage.show();

    }
}
