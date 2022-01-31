package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseUserRepository courseUserRepository;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {

        List<ModuleModel> courseModulesList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());
        if(!courseModulesList.isEmpty()){
            for(ModuleModel module : courseModulesList){
                List<LessonModel> moduleLessonsList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());
                if(!moduleLessonsList.isEmpty()){
                    lessonRepository.deleteAll(moduleLessonsList);
                }
            }
            moduleRepository.deleteAll(courseModulesList);
        }
        List<CourseUserModel> subscriptions = courseUserRepository.findAllSubscriptionsFromACourse(courseModel.getCourseId());
        if(!subscriptions.isEmpty()){
            courseUserRepository.deleteAll(subscriptions);
        }
        courseRepository.delete(courseModel);
    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
    }
}
