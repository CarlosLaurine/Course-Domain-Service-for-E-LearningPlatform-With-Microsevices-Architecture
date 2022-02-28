package com.ead.course.validations;

import com.ead.course.dtos.CourseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDTO courseDTO = (CourseDTO) o;
        validator.validate(courseDTO, errors);
        if(!errors.hasErrors()){
            validateUserInstructor(courseDTO.getUserInstructorId(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructorId, Errors errors){
//       ResponseEntity<UserDTO> responseUser;
//       try{
//           responseUser = authUserClient.getUserById(userInstructorId);
//           if(responseUser.getBody().getUserType().equals(UserType.STUDENT)){
//               errors.rejectValue("userInstructor","UserInstructorError","User must be INSTRUCTOR or ADMIN");
//           }
//           }
//       catch (HttpStatusCodeException exception){
//
//           if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)){
//
//               errors.rejectValue("userInstructor","UserInstructorError","User NOT Found");
//
//           }
//
//
//       }
       }
}