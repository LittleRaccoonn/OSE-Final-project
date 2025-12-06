package com.example.demoproject;

import com.example.demoproject.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML private TextField organizerInput;
    @FXML private TextField eventNameInput;
    @FXML private TextField placeInput;
    @FXML private TextField dateInput;
    @FXML private TextField timeInput;
    @FXML private TextField durationInput;
    @FXML private TextField peopleInput;
    @FXML private TextField phoneInput;

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> organizerColumn;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> placeColumn;
    @FXML private TableColumn<Event, String> dateTimeColumn;
    @FXML private TableColumn<Event, Integer> durationColumn;
    @FXML private TableColumn<Event, Integer> peopleColumn;
    @FXML private TableColumn<Event, String> phoneColumn;

    private ObservableList<Event> events = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = new ObjectMapper();

    // Файлы для автосохранения
    private static final String AUTO_SAVE_CSV = "autosave.csv";
    private static final String AUTO_SAVE_JSON = "autosave.json";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка таблицы
        organizerColumn.setCellValueFactory(new PropertyValueFactory<>("organizerName"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        peopleColumn.setCellValueFactory(new PropertyValueFactory<>("peopleQuantity"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        eventTable.setItems(events);

        // Автозагрузка при запуске
        autoLoadData();

        // Добавляем обработчик закрытия окна
        if (eventTable.getScene() != null && eventTable.getScene().getWindow() != null) {
            eventTable.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::handleWindowClose);
        }
    }

    // Автозагрузка данных при запуске
    private void autoLoadData() {
        // Сначала пробуем загрузить из JSON
        File jsonFile = new File(AUTO_SAVE_JSON);
        if (jsonFile.exists() && jsonFile.length() > 0) {
            try {
                Event[] eventsArray = objectMapper.readValue(jsonFile, Event[].class);
                events.addAll(eventsArray);
                System.out.println("Автозагрузка из JSON: " + eventsArray.length + " мероприятий");
                return;
            } catch (IOException e) {
                System.out.println("Ошибка автозагрузки из JSON: " + e.getMessage());
            }
        }

        // Если JSON не удалось, пробуем CSV
        File csvFile = new File(AUTO_SAVE_CSV);
        if (csvFile.exists() && csvFile.length() > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                List<Event> loadedEvents = new ArrayList<>();
                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] parts = parseCSVLine(line);
                    if (parts.length >= 7) {
                        Event event = new Event(
                                parts[0].replace("\"", ""),
                                parts[1].replace("\"", ""),
                                parts[2].replace("\"", ""),
                                parts[3].replace("\"", ""),
                                Integer.parseInt(parts[4]),
                                Integer.parseInt(parts[5]),
                                parts[6].replace("\"", "")
                        );
                        loadedEvents.add(event);
                    }
                }

                events.addAll(loadedEvents);
                System.out.println("Автозагрузка из CSV: " + loadedEvents.size() + " мероприятий");

            } catch (Exception e) {
                System.out.println("Ошибка автозагрузки из CSV: " + e.getMessage());
            }
        }

        // Если файлов нет - начинаем с пустого списка
        if (events.isEmpty()) {
            System.out.println("Автозагрузка: сохранений не найдено, начинаем с пустого списка");
        }
    }

    // Автосохранение при закрытии
    private void handleWindowClose(WindowEvent event) {
        autoSaveData();
    }

    // Автосохранение данных
    private void autoSaveData() {
        if (!events.isEmpty()) {
            try {
                // Сохраняем в JSON
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(AUTO_SAVE_JSON), events);
                System.out.println("Автосохранение в JSON: " + events.size() + " мероприятий");

                // Также сохраняем в CSV для надежности
                saveToCSV(new File(AUTO_SAVE_CSV));
                System.out.println("Автосохранение в CSV: " + events.size() + " мероприятий");

            } catch (IOException e) {
                System.out.println("Ошибка автосохранения: " + e.getMessage());
            }
        } else {
            // Если список пуст, удаляем файлы автосохранения
            try {
                Files.deleteIfExists(Paths.get(AUTO_SAVE_JSON));
                Files.deleteIfExists(Paths.get(AUTO_SAVE_CSV));
                System.out.println("Список пуст, файлы автосохранения удалены");
            } catch (IOException e) {
                System.out.println("Ошибка удаления файлов автосохранения: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            // Проверка заполнения полей
            if (organizerInput.getText().isEmpty() ||
                    eventNameInput.getText().isEmpty() ||
                    placeInput.getText().isEmpty() ||
                    dateInput.getText().isEmpty() ||
                    timeInput.getText().isEmpty() ||
                    durationInput.getText().isEmpty() ||
                    peopleInput.getText().isEmpty() ||
                    phoneInput.getText().isEmpty()) {

                showAlert("Ошибка", "Заполните все поля!", Alert.AlertType.ERROR);
                return;
            }

            // Создание объекта Event
            Event newEvent = new Event(
                    organizerInput.getText(),
                    eventNameInput.getText(),
                    placeInput.getText(),
                    dateInput.getText() + " " + timeInput.getText(),
                    Integer.parseInt(durationInput.getText()),
                    Integer.parseInt(peopleInput.getText()),
                    phoneInput.getText()
            );

            // Добавление в таблицу
            events.add(newEvent);

            // Очистка полей
            clearInputFields();

            showAlert("Успех", "Мероприятие добавлено!", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные числа в поля 'Длительность' и 'Количество людей'",
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSaveToCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы", "*.csv")
        );
        fileChooser.setInitialFileName("events_" + System.currentTimeMillis() + ".csv");

        File file = fileChooser.showSaveDialog(eventTable.getScene().getWindow());
        if (file != null) {
            saveToCSV(file);
        }
    }

    @FXML
    private void handleSaveToJSON(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить как JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON файлы", "*.json")
        );
        fileChooser.setInitialFileName("events_" + System.currentTimeMillis() + ".json");

        File file = fileChooser.showSaveDialog(eventTable.getScene().getWindow());
        if (file != null) {
            saveToJSON(file);
        }
    }

    @FXML
    private void handleAutoSave(ActionEvent event) {
        autoSaveData();
        showAlert("Автосохранение", "Данные сохранены автоматически", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleLoadFromCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить из CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы", "*.csv")
        );

        File file = fileChooser.showOpenDialog(eventTable.getScene().getWindow());
        if (file != null) {
            loadFromCSV(file);
        }
    }

    @FXML
    private void handleLoadFromJSON(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить из JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON файлы", "*.json")
        );

        File file = fileChooser.showOpenDialog(eventTable.getScene().getWindow());
        if (file != null) {
            loadFromJSON(file);
        }
    }

    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            events.remove(selectedEvent);
            showAlert("Успех", "Мероприятие удалено", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Ошибка", "Выберите мероприятие для удаления", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClearAll(ActionEvent event) {
        if (!events.isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Подтверждение");
            confirm.setHeaderText("Очистить все мероприятия");
            confirm.setContentText("Вы уверены? Все данные будут удалены.");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                events.clear();
                showAlert("Успех", "Все мероприятия удалены", Alert.AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private void handleClearAutoSave(ActionEvent event) {
        try {
            Files.deleteIfExists(Paths.get(AUTO_SAVE_JSON));
            Files.deleteIfExists(Paths.get(AUTO_SAVE_CSV));
            events.clear();
            showAlert("Успех", "Автосохранения очищены", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось очистить автосохранения: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePrintList(ActionEvent event) {
        if (events.isEmpty()) {
            showAlert("Информация", "Список мероприятий пуст", Alert.AlertType.INFORMATION);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== СПИСОК МЕРОПРИЯТИЙ ===\n\n");

        int counter = 1;
        for (Event e : events) {
            sb.append(counter++).append(". ").append(e.getEventName()).append("\n");
            sb.append("   Организатор: ").append(e.getOrganizerName()).append("\n");
            sb.append("   Место: ").append(e.getPlace()).append("\n");
            sb.append("   Время: ").append(e.getDateTime()).append("\n");
            sb.append("   Длительность: ").append(e.getDuration()).append(" ч.\n");
            sb.append("   Участников: ").append(e.getPeopleQuantity()).append("\n");
            sb.append("   Телефон: ").append(e.getPhoneNumber()).append("\n\n");
        }

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);

        Stage stage = new Stage();
        stage.setTitle("Список мероприятий");
        stage.setScene(new javafx.scene.Scene(textArea, 500, 400));
        stage.show();
    }

    private void saveToCSV(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Заголовок CSV
            writer.println("Organizer,Event Name,Place,Date and Time,Duration (hours),People Quantity,Phone Number");

            // Данные
            for (Event event : events) {
                writer.println(event.toString());
            }

            showAlert("Успех", "Данные сохранены в CSV файл", Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка при сохранении CSV: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void saveToJSON(File file) {
        try {
            objectMapper.writeValue(file, events);
            showAlert("Успех", "Данные сохранены в JSON файл", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка при сохранении JSON: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void loadFromCSV(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Event> loadedEvents = new ArrayList<>();
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = parseCSVLine(line);
                if (parts.length >= 7) {
                    Event event = new Event(
                            parts[0].replace("\"", ""),
                            parts[1].replace("\"", ""),
                            parts[2].replace("\"", ""),
                            parts[3].replace("\"", ""),
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]),
                            parts[6].replace("\"", "")
                    );
                    loadedEvents.add(event);
                }
            }

            events.setAll(loadedEvents);
            showAlert("Успех", "Загружено " + loadedEvents.size() + " мероприятий",
                    Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Ошибка", "Ошибка при загрузке CSV: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void loadFromJSON(File file) {
        try {
            Event[] eventsArray = objectMapper.readValue(file, Event[].class);
            events.setAll(eventsArray);
            showAlert("Успех", "Загружено " + eventsArray.length + " мероприятий",
                    Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка при загрузке JSON: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
    }

    private void clearInputFields() {
        organizerInput.clear();
        eventNameInput.clear();
        placeInput.clear();
        dateInput.clear();
        timeInput.clear();
        durationInput.clear();
        peopleInput.clear();
        phoneInput.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}