package com.reminderapp.reminder.service.mapper;

import com.reminderapp.reminder.dto.CreateReminderRequest;
import com.reminderapp.reminder.dto.ReminderDto;
import com.reminderapp.reminder.entity.Reminder;
import com.reminderapp.reminder.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    @Mapping(target = "reminderId", ignore = true)
    Reminder toEntity(CreateReminderRequest createRemRequest, User user);

    @Mapping(source = "entity.user.userId", target = "userId")
    ReminderDto toDto(Reminder entity);

    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ReminderDto dto, @MappingTarget Reminder entity);
}
