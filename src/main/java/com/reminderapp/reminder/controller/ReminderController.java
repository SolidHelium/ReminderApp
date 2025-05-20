package com.reminderapp.reminder.controller;

import com.reminderapp.reminder.service.ReminderService;
import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/reminders")
public class ReminderController {
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderDto> create(@RequestBody CreateReminderRequest request) {
        ReminderDto dto = reminderService.createReminder(request);
        URI location = URI.create("api/v1/reminders" + dto.reminderId());
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
}
