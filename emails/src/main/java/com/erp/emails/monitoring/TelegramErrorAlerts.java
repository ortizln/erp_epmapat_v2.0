package com.erp.emails.monitoring;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TelegramErrorAlerts extends AppenderBase<ILoggingEvent> {
    private final String token;
    private final String chatId;
    private final boolean enabled;
    private final long cooldownSeconds;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50), task -> { Thread t = new Thread(task, "telegram-alerts"); t.setDaemon(true); return t; },
            new ThreadPoolExecutor.AbortPolicy());
    private final Map<String, Instant> lastSent = new ConcurrentHashMap<>();

    public TelegramErrorAlerts(@Value("${TELEGRAM_BOT_TOKEN:}") String token,
            @Value("${TELEGRAM_CHAT_ID:}") String chatId,
            @Value("${TELEGRAM_ALERTS_ENABLED:false}") boolean enabled,
            @Value("${TELEGRAM_ALERT_COOLDOWN_SECONDS:300}") long cooldownSeconds) {
        this.token = token; this.chatId = chatId; this.enabled = enabled;
        this.cooldownSeconds = Math.max(1, cooldownSeconds);
    }

    @PostConstruct
    public void attach() {
        if (!enabled || token.isBlank() || chatId.isBlank()) return;
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        setContext(context); setName("TELEGRAM_ERRORS"); start();
        context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).addAppender(this);
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!event.getLevel().isGreaterOrEqual(Level.ERROR)) return;
        String key = event.getLoggerName();
        Instant now = Instant.now();
        Instant previous = lastSent.get(key);
        if (previous != null && previous.plusSeconds(cooldownSeconds).isAfter(now)) return;
        String message = event.getFormattedMessage();
        // No enviar cuerpos de facturas, destinatarios o credenciales a Telegram.
        String text = "EPMAPA-T | msvc-emails | ERROR\n" + now + "\nOrigen: " + key
                + "\nEvento: " + event.getMessage().replace("{}", "[ver log]");
        Object[] arguments = event.getArgumentArray();
        if (arguments != null && arguments.length > 0 && arguments[0] instanceof java.util.UUID)
            text += "\nEmail ID: " + arguments[0];
        if (message != null && message.contains("Authentication failed"))
            text += "\nSMTP rechazo la autenticacion. Revisar la cuenta configurada en BD.";
        final String alert = text.substring(0, Math.min(text.length(), 3500));
        try {
            executor.execute(() -> send(key, now, alert));
            lastSent.put(key, now);
        } catch (RejectedExecutionException ignored) {
            addWarn("Cola de alertas Telegram llena; revisar logs locales");
        }
    }

    private void send(String key, Instant when, String text) {
        try {
            String body = mapper.writeValueAsString(Map.of("chat_id", chatId, "text", text));
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.telegram.org/bot" + token + "/sendMessage"))
                    .timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 || !mapper.readTree(response.body()).path("ok").asBoolean()) {
                lastSent.remove(key, when);
                addWarn("Telegram no acepto alerta; HTTP " + response.statusCode());
            }
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) Thread.currentThread().interrupt();
            lastSent.remove(key, when);
            // No registrar excepcion: la URL puede contener el token.
            addWarn("No se pudo entregar alerta Telegram; revisar conectividad/configuracion");
        }
    }

    @PreDestroy
    public void detach() {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).detachAppender(this);
        stop(); executor.shutdownNow();
    }
}
