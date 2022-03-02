package com.ead.course.controllers;

import com.ead.course.dtos.CourseDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.validations.CourseValidator;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/controllers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseValidator courseValidator;

   @PostMapping
   public ResponseEntity<Object> insertCourse(@RequestBody CourseDTO courseDTO, Errors errors){
       log.debug("POST saveCourse courseDto received {} ", courseDTO.toString());
       courseValidator.validate(courseDTO, errors);
       if(errors.hasErrors()){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
       }
       var courseModel = new CourseModel();
       BeanUtils.copyProperties(courseDTO, courseModel);
       courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
       courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
       courseService.save(courseModel);
       log.debug("POST saveCourse courseId saved {} ", courseModel.getCourseId());
       log.info("Course saved successfully courseId {} ", courseModel.getCourseId());
       return ResponseEntity.status(HttpStatus.CREATED).body(courseModel);
   }

   @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID courseId){
       log.debug("DELETE deleteCourse courseId received {} ", courseId);
       Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
       if(!courseModelOptional.isPresent()){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
       }
       courseService.delete(courseModelOptional.get());
       log.debug("DELETE deleteCourse courseId deleted {} ", courseId);
       log.info("Course deleted successfully courseId {} ", courseId);
       return ResponseEntity.status(HttpStatus.OK).body("Course Deleted Successfully");
   }

    @PutMapping("/{courseId}")
    public ResponseEntity<Object> updateCourse( @RequestBody @Valid CourseDTO courseDTO,
                                                @PathVariable(value = "courseId") UUID courseId){
        log.debug("PUT updateCourse courseDto received {} ", courseDTO.toString());
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }
        var courseModel = courseModelOptional.get();

        BeanUtils.copyProperties(courseDTO, courseModel);
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        courseService.save(courseModel);

        log.debug("PUT updateCourse courseId saved {} ", courseModel.getCourseId());
        log.info("Course updated successfully courseId {} ", courseModel.getCourseId());

        return ResponseEntity.status(HttpStatus.OK).body(courseModel);
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable,
                                                           @RequestParam(required = false) UUID userId){
       boolean hasUserId = false;

       if(userId != null){
           hasUserId = true;
       }

        Page<CourseModel> courseModelPage = (hasUserId) ? courseService.findAll(SpecificationTemplate.courseUserId(userId).and(spec) , pageable) : courseService.findAll(spec, pageable);

        if (!courseModelPage.isEmpty()) {
            for (CourseModel course : courseModelPage.toList()) {
                course.add(linkTo(methodOn(CourseController.class).courseService.findById(course.getCourseId())).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(courseModelPage);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getCourseById(@PathVariable(value = "courseId") UUID courseId){
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }
}
