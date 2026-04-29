package com.project.downloadmanager.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Random;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LogError {
    Random random = new Random();

    private long id = Math.abs(random.nextLong());
    private long download_id;
    private String error_message;
    private LocalDateTime created_at;

    public LogError(long id, long download_id, String error_message, LocalDateTime created_at) {
        this.id = id;
        this.download_id = download_id;
        this.error_message = error_message;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Причина помилки: " + error_message + "\nЧас коли виникнула помилка: " + created_at;
    }

}
