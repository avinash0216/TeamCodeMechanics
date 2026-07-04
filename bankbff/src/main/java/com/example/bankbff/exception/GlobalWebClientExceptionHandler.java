package com.example.bankbff.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class GlobalWebClientExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalWebClientExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientResponseException(WebClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        String detailMessage = ex.getMessage();

        String path = detailMessage.indexOf("http") > 0 ? detailMessage.substring(detailMessage.indexOf("http")) : null;

        try {
            JsonNode node = objectMapper.readTree(body);
            if (node.isObject()) {
                ((ObjectNode) node).put("path", path);
                return ResponseEntity.status(ex.getStatusCode()).body(node);
            } else {
                // Non-object JSON (e.g., array or primitive)
                ObjectNode wrapper = objectMapper.createObjectNode();
                wrapper.set("error", node);
                wrapper.put("path", path);
                return ResponseEntity.status(ex.getStatusCode()).body(wrapper);
            }
        } catch (Exception e) {
            // If body is not JSON, return an object with message and path
            ObjectNode wrapper = objectMapper.createObjectNode();
            wrapper.put("message", body);
            wrapper.put("path", path);
            return ResponseEntity.status(ex.getStatusCode()).body(wrapper);
        }
    }
}
