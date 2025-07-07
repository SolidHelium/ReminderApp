package com.reminderapp.reminder.service.mapper;

import com.reminderapp.reminder.dto.CreateUserRequest;
import com.reminderapp.reminder.dto.UpdateUserRequest;
import com.reminderapp.reminder.dto.UserDto;
import com.reminderapp.reminder.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "reminders", ignore = true)
    User createUser(CreateUserRequest request);

    UserDto toDto(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "reminders", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UpdateUserRequest request, @MappingTarget User user);

//    @Mapping(target = "userId", ignore = true)
//    @Mapping(target = "role", ignore = true)
//    @Mapping(target = "reminders", ignore = true)
//    void changePassword(ChangePasswordRequest request, @MappingTarget User user);
}
