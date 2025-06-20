package com.reminderapp.reminder.service;

import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import com.reminderapp.reminder.repository.RemindersRepository;
import com.reminderapp.reminder.repository.UserRepository;
import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.dto.UpdateReminderRequest;
import com.reminderapp.reminder.service.mapper.ReminderMapper;
import com.reminderapp.reminder.specification.ReminderSpecs;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Transactional
public class ReminderServiceImpl implements ReminderService {
    private final UserRepository userRepo;
    private final RemindersRepository reminderRepo;
    private final ReminderMapper mapper;

    //TODO: Proper exception handling

    @Override
    public ReminderDto createReminder(CreateReminderRequest request) {
        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.remind().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reminder date and time should be in the future");
        }

        Reminder reminder = mapper.toEntity(request, user);
        Reminder saved = reminderRepo.save(reminder);

        return mapper.toDto(saved);
    }

    @Override
    public ReminderDto updateReminder(UpdateReminderRequest request) {
        Reminder reminder = reminderRepo.findById(request.reminderId())
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        ReminderDto dto = new ReminderDto(
                request.reminderId(),
                reminder.getUser().getUserId(),
                request.title(),
                request.description(),
                request.remind());

        mapper.updateEntity(dto, reminder);
        Reminder updated = reminderRepo.save(reminder);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderDto getReminderById(long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        return mapper.toDto(reminder);
    }

    @Override
    public void deleteReminder(long id) {
        Reminder reminder = reminderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder does not exist"));
        reminderRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderDto> findAll(String search, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Specification<Reminder> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            spec = Specification.where(spec.and(ReminderSpecs.hasWord(search)));
        }

        if(from != null) {
            spec = Specification.where(spec.and(ReminderSpecs.afterDateTime(from)));
        }

        if(to != null) {
            spec = Specification.where(spec.and(ReminderSpecs.beforeDateTime(to)));
        }

        Page<Reminder> page = reminderRepo.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }
}
