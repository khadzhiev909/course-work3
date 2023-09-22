package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // Обрабатываем входящие сообщения

            //chat id
            Long chatId = update.message().chat().id();

            //Сообщение, которое будет выводить при команде /start
            String messageText = "Привет, чем могу быть полезен";

            //отправляемое сообщение
            SendMessage message = new SendMessage(chatId, messageText);

            //сообщение, которые получили
            String getMessage = update.message().text();
            //Проверка на сообщение
            switch (getMessage) {
                case "/start":
                    sendResponse(message);
            }

            //Pattern —  как раз задаёт паттерн для поиска (регулярное выражение)
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");


            //Чтобы проверить, удовлетворяет ли вообще строка паттерну, используют метод matches
            Matcher matcher = pattern.matcher(getMessage);



            if (matcher.matches()) {
                // обрабатываем ситуацию, когда строка соответствует паттерну
                String date = matcher.group(1);
                String item = matcher.group(3);

                //создание события
                NotificationTask notificationTask = new NotificationTask();

                notificationTask.setChatId(chatId);
                notificationTask.setDataTime(parseDate(date));
                notificationTask.setMessage(item);

                notificationTaskRepository.save(notificationTask);
                System.out.println(notificationTask);

            }

//
//            System.out.println("update.message().text() = " + getMessage);
//            System.out.println(matcher.find());


        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //медот для парсинга даты
    private LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }


    //отправка ответа на сообщение /start
    public SendResponse sendResponse(SendMessage message) {
        return telegramBot.execute(message);
    }


}
