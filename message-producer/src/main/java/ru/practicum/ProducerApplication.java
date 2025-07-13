package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.message.model.Message;
import ru.yandex.message.MessageUtils;

@SpringBootApplication
@EnableConfigurationProperties
public class ProducerApplication {
	@Autowired
	private KafkaTemplate<String, Message> kafkaTemplate;

	public static void main(String[] args) {
		var context = SpringApplication.run(ProducerApplication.class, args);
		ProducerApplication app = context.getBean(ProducerApplication.class);
		new MessageUtils().publishingMessages(app.kafkaTemplate);
	}
}
