package dev.jdieb.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.jdieb.notification.domain.Notification;
import dev.jdieb.notification.domain.services.NotificationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/notify")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;
	
	@PostMapping(path = "/add")
	@ResponseStatus(HttpStatus.CREATED)
	public void register(@Valid @RequestBody Notification notification) {
		notificationService.start(notification);
	}
	
}