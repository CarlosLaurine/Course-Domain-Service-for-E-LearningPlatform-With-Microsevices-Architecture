package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> insertModule( @PathVariable(value = "courseId") UUID courseId,
                                                @RequestBody @Valid ModuleDTO moduleDTO){

        log.debug("POST saveModule moduleDto received {} ", moduleDTO.toString());

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }

        var moduleModel = new ModuleModel();

        BeanUtils.copyProperties(moduleDTO, moduleModel);

        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModelOptional.get());

        moduleService.save(moduleModel);

        log.debug("POST saveModule moduleId saved {} ", moduleModel.getModuleId());
        log.info("Module saved successfully moduleId {} ", moduleModel.getModuleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(moduleModel);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId){

        log.debug("DELETE deleteModule moduleId received {} ", moduleId);

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourseById(courseId, moduleId);

        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found for this Course");
        }

        moduleService.delete(moduleModelOptional.get());

        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);
        log.info("Module deleted successfully moduleId {} ", moduleId);

        return ResponseEntity.status(HttpStatus.OK).body("Module Deleted Successfully");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule( @RequestBody @Valid ModuleDTO moduleDTO,
                                                @PathVariable(value = "courseId") UUID courseId,
                                                @PathVariable(value = "moduleId") UUID moduleId){
        log.debug("PUT updateModule moduleDto received {} ", moduleDTO.toString());

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourseById(courseId, moduleId);

        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found for this Course");
        }

        var moduleModel = moduleModelOptional.get();

        BeanUtils.copyProperties(moduleDTO, moduleModel);

        moduleService.save(moduleModel);

        log.debug("PUT updateModule moduleId saved {} ", moduleModel.getModuleId());
        log.info("Module updated successfully moduleId {} ", moduleModel.getModuleId());

        return ResponseEntity.status(HttpStatus.OK).body(moduleModel);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModulesFromACourse(@PathVariable(value = "courseId") UUID courseId,
                                                                      SpecificationTemplate.ModuleSpec spec,
                                                                      @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findAllModulesByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getModuleById(@PathVariable(value = "courseId") UUID courseId,
                                                @PathVariable(value = "moduleId") UUID moduleId){
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourseById(courseId, moduleId);
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found for this Course");
        }
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }


}
