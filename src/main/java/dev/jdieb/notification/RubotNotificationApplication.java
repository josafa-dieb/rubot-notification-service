package dev.jdieb.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import dev.jdieb.notification.utils.TelegramBotAPI;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class RubotNotificationApplication {

	public static Dotenv dotenv;
	public static TelegramBotAPI telegramBotApi;

	public static void main(String[] args) {
		telegramBotApi = new TelegramBotAPI();
		dotenv = Dotenv.configure().directory("<directory_to_dotenv>").load();
		telegramBotApi.sendMessageToChat(1392614092, "[RUBOT] Serviço de notificação inicializado");
		SpringApplication.run(RubotNotificationApplication.class, args);
	}

}
