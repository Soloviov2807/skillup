package com.example.course_service.service;

import com.example.common.event.CoachProfileChangedEvent;
import com.example.course_service.dto.CoachInfoResponse;
import com.example.course_service.dto.PublicUrlResponse;
import com.example.course_service.dto.course.*;
import com.example.course_service.exception.CourseNotFound;
import com.example.course_service.mapper.CourseMapper;
import com.example.course_service.model.Course;
import com.example.course_service.dto.DownloadUrl;
import com.example.course_service.repo.CourseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepo repo;
    private final CourseMapper courseMapper;
    private final FileService fileService;
    private final UserService userService;


    public List<CoursePreviewResponse> getAllCourses() {

        List<Course> courses = repo.findAll();

        return courses.stream()
                .map(course -> {

                    String previewUrl = null;

                    if(course.getPreviewImageId() != null && !course.getPreviewImageId().isBlank()){

                        PublicUrlResponse publicUrlResponse = fileService.getPublicFile(course.getPreviewImageId());

                        previewUrl = publicUrlResponse.url();

                    }

                    return new CoursePreviewResponse(
                            course.getId(),
                            course.getName(),
                            course.getRating(),
                            course.getPrice(),
                            previewUrl,
                            course.getCoachName()
                    );


                }).toList();

    }

    @Transactional
    public void addCourse(String name,
                          String description,
                          BigDecimal price,
                          long userId,
                          MultipartFile preview) throws IOException {


        CoachInfoResponse coachInfo = userService.getCoachInfo(userId);

        Course course = new Course();

        course.setName(name);
        course.setDescription(description);
        course.setPrice(price);
        course.setDuration(0);
        course.setRating(0);
        course.setTotalRating(0);
        course.setReviewsCount(0);
        course.setCoachId(userId);
        course.setCoachName(coachInfo.name());

        String previewId = null;

        if(preview != null && !preview.isEmpty()){

            previewId = fileService.uploadCourseCoverOrThrow(preview);

            course.setPreviewImageId(previewId);


        }

        if(coachInfo.avatarId() != null && !coachInfo.avatarId().isEmpty()){
            course.setAvatarImageId(coachInfo.avatarId());
        }

        try {
            repo.save(course);
        } catch (Exception e) {
            fileService.deleteFileSafe(previewId);
            throw e;
        }






    }

    public CourseResponse getCourseById(long courseId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        String previewUrl = null;
        String avatarUrl = null;


        if(course.getPreviewImageId() != null && !course.getPreviewImageId().isBlank()){
            PublicUrlResponse publicUrlResponse = fileService.getPublicFile(course.getPreviewImageId());

            previewUrl = publicUrlResponse.url();

        }

        if(course.getAvatarImageId() != null && !course.getAvatarImageId().isBlank()){
            PublicUrlResponse publicUrlResponse = fileService.getPublicFile(course.getAvatarImageId());

            avatarUrl = publicUrlResponse.url();

        }




        return courseMapper.toDto(course, previewUrl, avatarUrl);

    }

    @Transactional
    public void deleteCourseById(long courseId, long userId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        if(course.getCoachId() != userId){
            throw new AccessDeniedException("Not your course");
        }

        repo.deleteById(courseId);
        fileService.deleteFileSafe(course.getPreviewImageId());

    }



    @Transactional
    public void updateCourse(String name,
                             String description,
                             BigDecimal price,
                             long userId,
                             MultipartFile preview,
                             long courseId) throws IOException {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        if(course.getCoachId() != userId){
            throw new AccessDeniedException("Not your course");
        }

        if(name != null){
            course.setName(name);
        }

        if (description != null){
            course.setDescription(description);
        }


        if(price != null){
            course.setPrice(price);
        }


        String oldPreviewId = course.getPreviewImageId();
        String newPreviewId = null;


        if(preview != null && !preview.isEmpty()){

            String fileName = preview.getOriginalFilename().toLowerCase();
            String mimeType = preview.getContentType();


            if (mimeType == null || !mimeType.startsWith("image/")){
                throw new RuntimeException("File must be an image");
            }

            if (!fileName.endsWith(".jpg")
                    && !fileName.endsWith(".jpeg")
                    && !fileName.endsWith(".png")
                    && !fileName.endsWith(".webp")) {
                throw new RuntimeException("Invalid image format");
            }

            newPreviewId = fileService.uploadCourseCoverOrThrow(preview);

            course.setPreviewImageId(newPreviewId);


        }

        try {
            repo.save(course);

            if (newPreviewId != null) {
                fileService.deleteFileSafe(oldPreviewId);
            }
        } catch (Exception e) {
            fileService.deleteFileSafe(newPreviewId);
            throw e;
        }
    }


    public ResponseEntity<DownloadUrl> getCoursePreview(long courseId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        return fileService.getFile(course.getPreviewImageId());

    }

    public CoursePaymentInfoResponse getCoursePaymentInfoById(long courseId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        return new CoursePaymentInfoResponse(
                course.getId(),
                course.getName(),
                course.getPrice());
    }

    public Boolean isPurchased(long userId, long courseId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        return course.getStudentsIds().contains(userId);

    }

    public List<MyCoursePreviewResponse> getMyCourses(long userId) {

        List<Course> courses = repo.findAll();

        return courses
                .stream()
                .filter(
                course -> course.getStudentsIds().contains(userId))
                .map(course -> {

                    String previewUrl = null;

                    if(course.getPreviewImageId() != null && !course.getPreviewImageId().isBlank()){

                        PublicUrlResponse publicUrlResponse = fileService.getPublicFile(course.getPreviewImageId());

                        previewUrl = publicUrlResponse.url();

                    }

                    return new MyCoursePreviewResponse(
                            course.getId(),
                            course.getName(),
                            previewUrl
                    );


                }).toList();


    }

    public MyCourseResponse getMyCourse(long userId, long courseId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        if(!course.getStudentsIds().contains(userId)){
            throw new AccessDeniedException("Not your course");
        }

        return courseMapper.toMyCourseDto(course);

    }

    public void handleCoachProfileChanged(CoachProfileChangedEvent coachProfileChangedEvent) {

        repo.updateCoachInfo(coachProfileChangedEvent.id(), coachProfileChangedEvent.name(), coachProfileChangedEvent.avatarId());


    }

    public CoachCourseResponse getCoachCourse(long userId, long courseId) {

        Course course = repo.findById(courseId).orElseThrow(() -> new CourseNotFound("Course not found"));

        if(course.getCoachId() != userId){
            throw new AccessDeniedException("Not your course");
        }

        String previewUrl = null;

        if(course.getPreviewImageId() != null && !course.getPreviewImageId().isBlank()){

            PublicUrlResponse publicUrlResponse = fileService.getPublicFile(course.getPreviewImageId());

            previewUrl = publicUrlResponse.url();

        }

        return courseMapper.toCoachCourseDto(course, previewUrl);

    }

    public List<CoachCoursePreviewResponse> getCoachCourses(long userId) {

        List<Course> courses = repo.findAllByCoachId(userId);

        return courses
                .stream()
                .map(course -> {

                    String previewUrl = null;

                    if(course.getPreviewImageId() != null && !course.getPreviewImageId().isBlank()){

                        PublicUrlResponse publicUrlResponse = fileService.getPublicFile(course.getPreviewImageId());

                        previewUrl = publicUrlResponse.url();

                    }

                    return new CoachCoursePreviewResponse(
                            course.getId(), course.getName(), previewUrl
                    );


                }).toList();

    }
}
