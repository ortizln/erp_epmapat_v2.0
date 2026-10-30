package com.erp.sri_files.services;

import com.erp.sri_files.dto.AttachmentDTO;
import com.erp.sri_files.dto.EmailMsAttachmentInput;
import com.erp.sri_files.dto.EmailMsIdResponse;
import com.erp.sri_files.dto.EmailMsSendEmailRequest;
import com.erp.sri_files.dto.SendMailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailMsClientService {

    @Value("${OPERATIONS_API_KEY:}") private String operationsKey;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public EmailMsClientService(
            RestTemplate restTemplate,
            @Value("${app.email-ms.base-url:http://msvc-emails:9099}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public UUID enqueueDocumentEmail(SendMailRequest req, String correlationId) {
        EmailMsSendEmailRequest payload = new EmailMsSendEmailRequest(
                req.to(),
                req.cc(),
                req.bcc(),
                req.subject(),
                null,
                null,
                req.htmlBody(),
                stripHtml(req.htmlBody()),
                correlationId,
                mapAttachments(req.attachments())
        );

        try {
            ResponseEntity<EmailMsIdResponse> response = restTemplate.postForEntity(
                    baseUrl + "/api/v1/emails/documents",
                    new org.springframework.http.HttpEntity<>(payload,internalHeaders()),
                    EmailMsIdResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().id() == null) {
                throw new IllegalStateException("msvc-emails no devolvio un identificador de encolamiento");
            }

            return response.getBody().id();
        } catch (RestClientException ex) {
            throw new IllegalStateException("No fue posible conectar con msvc-emails: " + ex.getMessage(), ex);
        }
    }

    public com.erp.sri_files.dto.EmailMsStatusResponse consultarEstado(UUID id) {
        var response = restTemplate.exchange(baseUrl + "/api/v1/emails/" + id + "/status",
                org.springframework.http.HttpMethod.GET,new org.springframework.http.HttpEntity<>(internalHeaders()),
                com.erp.sri_files.dto.EmailMsStatusResponse.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
            throw new IllegalStateException("No se pudo confirmar estado del correo " + id);
        return response.getBody();
    }

    public Object intentos(java.util.UUID id) {
        return restTemplate.exchange(baseUrl+"/api/v1/emails/"+id+"/attempts",org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(internalHeaders()),Object.class).getBody();
    }
    public void accion(java.util.UUID id,String action,Object body,String key,String actor) {
        var headers=new org.springframework.http.HttpHeaders();
        headers.set("X-Operations-Key",key);headers.set("X-Operations-Actor",actor);
        restTemplate.postForEntity(baseUrl+"/api/v1/emails/"+id+"/"+action,
                new org.springframework.http.HttpEntity<>(body,headers),Object.class);
    }
    private org.springframework.http.HttpHeaders internalHeaders(){
        var headers=new org.springframework.http.HttpHeaders();
        if(operationsKey!=null)headers.set("X-Operations-Key",operationsKey);
        headers.set("X-Operations-Actor","msvc-sri");return headers;
    }
    public boolean health() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/actuator/health", Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            return false;
        }
    }

    private List<EmailMsAttachmentInput> mapAttachments(List<AttachmentDTO> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }

        return attachments.stream()
                .map(att -> new EmailMsAttachmentInput(att.filename(), att.mimeType(), att.base64()))
                .toList();
    }

    private String stripHtml(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }

        return html.replaceAll("<[^>]*>", " ")
                .replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
