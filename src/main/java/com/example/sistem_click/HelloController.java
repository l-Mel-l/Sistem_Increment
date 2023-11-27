package com.example.sistem_click;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class HelloController extends Application {
    private static final int PORT = 8070;

    private Label clickCountLabel;
    private int clickCount = 0;
    private PrintWriter out;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        AnchorPane root = loader.load();

        // Получаем ссылки на элементы пользовательского интерфейса
        Button clickButton = (Button) loader.getNamespace().get("btn");
        clickCountLabel = (Label) loader.getNamespace().get("lableid");

        clickButton.setOnAction(event -> {
            clickCount++;
            clickCountLabel.setText(Integer.toString(clickCount));
            out.println(clickCount);
            out.flush();
        });
        Button decrementButton = (Button) loader.getNamespace().get("decrementButton");

        decrementButton.setOnAction(event -> {
            clickCount--;
            clickCountLabel.setText(Integer.toString(clickCount));
            out.println(clickCount);
            out.flush();
        });

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Network Clicker");
        primaryStage.show();

        Scanner scanner = new Scanner(System.in);

        System.out.print("\"server\" или \"client\"?: ");
        String role = scanner.nextLine();
        if (role.equalsIgnoreCase("server")) {
            runServer();
        } else if (role.equalsIgnoreCase("client")) {
            System.out.print("Введите IP сервера: ");
            String serverIP = scanner.nextLine();
            runClient(serverIP);
        } else {
            System.out.println("Прочтите внимательнее!");
        }
        scanner.close();
    }

    private void runServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен, ожидание подключения клиента");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Клиент подключен, адрес: " + clientSocket.getInetAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("0");
            Thread.sleep(200);

            new Thread(() -> {
                try {
                    while (true) {
                        String clientMessage = in.readLine();
                        if (clientMessage == null) {
                            break;
                        }
                        handleClientMessage(clientMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runClient(String serverIP) {
        try {
            Socket socket = new Socket(serverIP, PORT);
            System.out.println("Подключено к серверу");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("0");
            Thread.sleep(200);

            new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = in.readLine();
                        if (serverMessage == null) {
                            break;
                        }
                        handleServerMessage(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientMessage(String message) {
        // Извлекаем количество кликов из сообщения
        try {
            int receivedClickCount = Integer.parseInt(message.replaceAll("\\D", ""));
            clickCount = receivedClickCount;

            Platform.runLater(() -> clickCountLabel.setText(Integer.toString(clickCount)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String message) {
        // Та же обработка, что и у клиента
        handleClientMessage(message);
        out.flush();
    }
}
//C:\Users\79623\.jdks\jbr-17.0.6\bin\java.exe -javaagent:C:\Users\79623\AppData\Local\JetBrains\Toolbox\apps\IDEA-C\ch-0\223.8617.56\lib\idea_rt.jar=59654:C:\Users\79623\AppData\Local\JetBrains\Toolbox\apps\IDEA-C\ch-0\223.8617.56\bin -Dfile.encoding=UTF-8 -classpath C:\Users\79623\.m2\repository\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar;C:\Users\79623\.m2\repository\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2.jar;C:\Users\79623\.m2\repository\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2.jar;C:\Users\79623\.m2\repository\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar -p C:\Users\79623\.m2\repository\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar;C:\Users\79623\.m2\repository\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar;C:\Users\79623\IdeaProjects\sistem_click\target\classes;C:\Users\79623\.m2\repository\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar;C:\Users\79623\.m2\repository\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar -m com.example.sistem_click/com.example.sistem_click.HelloController
//