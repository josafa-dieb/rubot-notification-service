package dev.jdieb.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dev.jdieb.notification.utils.TelegramBotAPI;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class RubotNotificationApplication {

	public static Dotenv dotenv;
	public static TelegramBotAPI telegramBotApi;

	public static void main(String[] args) {
		telegramBotApi = new TelegramBotAPI();
		dotenv = Dotenv.configure().directory("/home/ubuntu/rubot/").load();
		telegramBotApi.sendMessageToChat(1392614092, "[RUBOT] Serviço de notificação inicializado!");
		SpringApplication.run(RubotNotificationApplication.class, args);
	}

}
