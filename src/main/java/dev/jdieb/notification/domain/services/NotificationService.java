package dev.jdieb.notification.domain.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.jdieb.notification.RubotNotificationApplication;
import dev.jdieb.notification.domain.Notification;
import dev.jdieb.notification.domain.dto.HistoricoRecargaDTO;
import lombok.AllArgsConstructor;

@Service
public class NotificationService {

	@Autowired
	private ObjectMapper objectMapper;

	public void start(Notification notify) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {

				String url = RubotNotificationApplication.dotenv.get("RUBOT_API_HISTORICO_RECARGA");
				String jsonRequestBody = String.format("{ \"matricula\": \"%d\",\"cartao\": \"%s\" }",
						notify.getMatricula(), notify.getCartao());

				HttpClient httpClient = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
						.header("Content-Type", "application/json")
						.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:92.0) Gecko/20100101 Firefox/92.0")
						.POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody)).build();
				try {
					HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
					List<HistoricoRecargaDTO> jsonDataList = objectMapper.readValue(response.body(),
							new TypeReference<List<HistoricoRecargaDTO>>() {
							});
					HistoricoRecargaDTO lastRecharge = jsonDataList.get(0);
					long currentTime = System.currentTimeMillis();
					if (notify.getStatus() == 0) {
						notify.setStatus(1);
						notify.setUltimaRecarga(lastRecharge.getDate());
						notify.setLastTime(currentTime);
					} else if (notify.getStatus() == 1) {
						if (!notify.getUltimaRecarga().equalsIgnoreCase(lastRecharge.getDate())) {
							RubotNotificationApplication.telegramBotApi.sendMessageToChat(notify.getChatId(),
									"🔔 Sua recarga de " + lastRecharge.getRechargedValue()
											+ " creditos já está no seu cartão - Total de créditos: "
											+ lastRecharge.getCurrentValue());
							scheduler.shutdown();
							notify.setStatus(2);
						} else if ((currentTime - notify.getLastTime()) / 1000 > 20) {
							RubotNotificationApplication.telegramBotApi.sendMessageToChat(notify.getChatId(),
									"🔔 Nao sabemos se sua recarga de " + notify.getCreditos() + " creditos caiu no seu cartao, pois o sistema sofreu perdas de dados com falhas externas, verifique diretamente no historico de recargas.");
							scheduler.shutdown();
							notify.setStatus(2);
						}
					} else if (notify.getStatus() == 2) {
						scheduler.shutdown();
					}
				} catch (IOException | InterruptedException e) {
					scheduler.shutdown();
					RubotNotificationApplication.telegramBotApi.sendMessageToChat(notify.getChatId(),
							"🔔 Nao conseguimos completar sua recarga pois o sistema do restaurante universtario se encontra instavel.");
				}
			}
		}, 0, 10, TimeUnit.SECONDS);
	}

}
