package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
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

            //chat id
            Long chatId = update.message().chat().id();

            //Message that will be displayed when using the /start command
            String messageText = "Приветсвенное сообщение";

            //the message we send
            SendMessage message = new SendMessage(chatId, messageText);

            //we receive a message
            String getMessage = update.message().text();

            //compliance check
            switch (getMessage) {
                case "/start":
                    sendResponse(message);
            }

            //Pattern —  как раз задаёт паттерн для поиска (регулярное выражение)
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");


            //Чтобы проверить, удовлетворяет ли вообще строка паттерну, используют метод matches
            Matcher matcher = pattern.matcher(getMessage);

            //check that the string matches the pattern
            if (matcher.matches()) {
                // обрабатываем ситуацию, когда строка соответствует паттерну
                String date = matcher.group(1);
                String item = matcher.group(3);

                //method that creates notification task
                createNotionTask(chatId ,date, item);

                //a method that sends a message at a specified time
                run();
            }

        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //date parsing method
    private LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    //create new task
    public NotificationTask createNotionTask(Long chatId, String date, String item) {
        //создание события
        NotificationTask notificationTask = new NotificationTask();

        notificationTask.setChatId(chatId);
        notificationTask.setDataTime(parseDate(date));
        notificationTask.setMessage(item);

        notificationTaskRepository.save(notificationTask);

        return notificationTask;
    }

    //method that sends a message
    public SendResponse sendResponse(SendMessage message) {
        return telegramBot.execute(message);
    }

    //a method that sends a message at a specified time
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        //current date
        LocalDateTime timeNow = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        //receiving an object according to date
        Collection<NotificationTask> notificationTasks = notificationTaskRepository.findNotificationTaskByDataTime(timeNow);

        //from the resulting collection we take each object
        notificationTasks.forEach(mes -> {
            SendMessage sendMessage = new SendMessage(mes.getChatId(), mes.getMessage());
            sendResponse(sendMessage);
        });
    }
}
