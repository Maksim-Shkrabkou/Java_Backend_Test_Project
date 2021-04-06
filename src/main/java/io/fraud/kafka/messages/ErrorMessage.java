package io.fraud.kafka.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorMessage {

    @JsonProperty("msg")
    private DealMessage message;

    @JsonProperty("errors")
    private List<String> errors;
}
