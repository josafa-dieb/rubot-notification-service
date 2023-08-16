package dev.jdieb.notification.utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import dev.jdieb.notification.RubotNotificationApplication;

public class TelegramBotAPI extends TelegramLongPollingBot {

	@Override
	public void onUpdateReceived(Update update) {
		System.out.println("Atualizações: " + update.getMessage());
	}

	@Override
	public String getBotUsername() {
		return RubotNotificationApplication.dotenv.get("TELEGRAM_BOT_USERNAME");
	}

	@Override
	public String getBotToken() {
		return RubotNotificationApplication.dotenv.get("TELEGRAM_TOKEN");
	}

	public void sendMessageToChat(long chatId, String message) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(String.valueOf(chatId));
		sendMessage.setText(message);

		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
