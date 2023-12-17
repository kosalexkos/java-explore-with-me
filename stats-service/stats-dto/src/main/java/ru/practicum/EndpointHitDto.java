package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {
    Integer id;
    @NotNull
    @NotBlank
    String app;
    @NotBlank
    private String uri;
    @NotNull
    @NotBlank
    @Length(min = 7, max = 15)
    private String ip;
    @NotNull
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}