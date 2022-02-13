package com.ead.course.services;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UtilsService {
    String generateUrlGetAllUsersByCourse(UUID courseId, Pageable pageable);

    String generateUrlgetUserById(UUID userId);

    String generateUrlPostSubscriptionUserInCourse(UUID userId);

    String generateUrlDeleteCourseInAuthUser (UUID courseId);
}
