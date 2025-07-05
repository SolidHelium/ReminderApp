package com.reminderapp.reminder.controller;

import com.reminderapp.reminder.service.NotificationService;
import com.reminderapp.reminder.service.ReminderService;
import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/reminder")
public class ReminderController {
    private final ReminderService reminderService;

    @PostMapping
    public ResponseEntity<ReminderDto> create(@RequestBody CreateReminderRequest request) {
        ReminderDto dto = reminderService.createReminder(request);
        URI location = URI.create("api/v1/reminder/" + dto.reminderId());
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDto> getById(@PathVariable long id) {
        return ResponseEntity.ok(reminderService.getReminderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderDto> updateById(@PathVariable long id,
                                                  @RequestBody UpdateReminderRequest request) {
        UpdateReminderRequest updateReminderRequest = new UpdateReminderRequest(
                id,
                request.title(),
                request.description(),
                request.remind());
        ReminderDto dto = reminderService.updateReminder(updateReminderRequest);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<Page<ReminderDto>> list(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")LocalDateTime to,

            Pageable pageable
    ) {
        Page<ReminderDto> page = reminderService.findAll(search, from, to, pageable);
        return ResponseEntity.ok(page);
    }
}
