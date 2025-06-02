package com.reminderapp.reminder.specification;

import com.reminderapp.reminder.entity.Reminder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReminderSpecs {

    public static Specification<Reminder> hasWord(String word) {
        return (root, query, criteriaBuilder) -> {

            String pattern = "%" + word.toLowerCase() + "%";
            Predicate predicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            );
            return predicate;
        };
    }

    public static Specification<Reminder> afterDateTime(LocalDateTime dateTime) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get("remind"), dateTime);
            return predicate;
        });
    }

    public static Specification<Reminder> beforeDateTime(LocalDateTime dateTime) {
        return ((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.lessThanOrEqualTo(root.get("remind"), dateTime);
            return predicate;
        });
    }

//    public static Specification<Reminder> hasKeyword(String keyword) {
//        // ↓ Outer return returns this whole object:
//        return new Specification<Reminder>() {
//            @Override
//            public Predicate toPredicate(Root<Reminder> root,
//                                         CriteriaQuery<?> query,
//                                         CriteriaBuilder cb) {
//                String pattern = "%" + keyword.toLowerCase() + "%";
//                // ↓ Inner return returns the Predicate from toPredicate(...)
//                return cb.or(
//                        cb.like(cb.lower(root.get("title")), pattern),
//                        cb.like(cb.lower(root.get("description")), pattern)
//                );
//            }
//        };
//    }
}
