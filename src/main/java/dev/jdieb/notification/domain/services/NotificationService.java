package dev.jdieb.notification.domain.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
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

@Service
public class NotificationService {

	@Autowired
	private ObjectMapper objectMapper;

	private HttpRequest buildRequest(String requestBody) {
		return HttpRequest.newBuilder()
				.uri(URI.create(RubotNotificationApplication.dotenv.get("RUBOT_API_HISTORICO_RECARGA")))
				.version(Version.HTTP_1_1).header("Content-Type", "application/json")
				.header("Cache-Control", "no-cache")
				.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:92.0) Gecko/20100101 Firefox/92.0")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
	}

	public void start(Notification notify) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {

				String jsonRequestBody = String.format("{ \"matricula\": \"%d\",\"cartao\": \"%s\" }",
						notify.getMatricula(), notify.getCartao());
				HttpRequest request = buildRequest(jsonRequestBody);
				HttpClient httpClient = HttpClient.newHttpClient();

				try {
					HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
					List<HistoricoRecargaDTO> jsonDataList = objectMapper.readValue(response.body(),
							new TypeReference<List<HistoricoRecargaDTO>>() {
							});
					HistoricoRecargaDTO lastRecharge = jsonDataList.get(0);
					long currentTime = System.currentTimeMillis();

					if (notify.getStatus() == 0) {

						System.out.println("Verificando: " + notify.getAluno());
						notify.setStatus(1);
						notify.setUltimaRecarga(lastRecharge.getDate());
						notify.setLastTime(currentTime);

					} else if (notify.getStatus() == 1) {
						if (!notify.getUltimaRecarga().equalsIgnoreCase(lastRecharge.getDate())) {
							RubotNotificationApplication.telegramBotApi.sendMessageToChat(notify.getChatId(),
									"🔔 Sua recarga de " + lastRecharge.getRechargedValue()
											+ " creditos já está no seu cartão - Total de créditos: "
											+ lastRecharge.getCurrentValue());

							System.out.println("Recarga completa: " + notify.getAluno());
							notify.setStatus(2);
							scheduler.shutdown();
						} else if (((currentTime - notify.getLastTime()) / 1000) > 180) {
							RubotNotificationApplication.telegramBotApi.sendMessageToChat(notify.getChatId(), "🔔 Olá "
									+ notify.getAluno().split(" ")[0] + ", sua recarga de " + notify.getCreditos()
									+ " créditos está demorando muito, passe a verificar no histórico de recargas.");
							System.out.println("Recarga incompleta: " + notify.getAluno());
							notify.setStatus(2);
							scheduler.shutdown();
						}
					} else if (notify.getStatus() == 2) {
						scheduler.shutdown();
					}
				} catch (IOException | InterruptedException e) {
					scheduler.shutdown();
					RubotNotificationApplication.telegramBotApi.sendMessageToChat(notify.getChatId(),
							"🔔 Não conseguimos saber sua recarga irá cair devido a falhas no sistema, portanto fique de olho no seu histórico de recargas.");
				}
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

}
