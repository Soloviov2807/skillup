package com.example.course_service.service;

import com.example.course_service.mapper.LessonMapper;
import com.example.course_service.model.Lesson;
import com.example.course_service.model.Section;
import com.example.course_service.dto.DownloadUrl;
import com.example.course_service.dto.lesson.LessonRequest;
import com.example.course_service.dto.lesson.LessonResponse;
import com.example.course_service.repo.LessonRepo;
import com.example.course_service.repo.SectionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final SectionRepo sectionRepo;
    private final LessonMapper lessonMapper;
    private final LessonRepo lessonRepo;
    private final FileService fileService;


    @Transactional
    public void addLesson(LessonRequest lessonRequest, long userId, MultipartFile video, long sectionId) throws IOException {

        Section section = sectionRepo.findById(sectionId).orElseThrow(() -> new RuntimeException("Section not found"));

        if(section.getCourse().getCoachId() != userId){
            throw new RuntimeException("Access denied");
        }

        Lesson lesson = new Lesson();

        lesson.setSection(section);
        lesson.setName(lessonRequest.name());

        validate(video);



        String videoId = fileService.uploadVideoOrThrow(video);

        lesson.setVideoId(videoId);

        lesson.setDuration(0);

        try{
            lessonRepo.save(lesson);
        } catch (Exception e){
            fileService.deleteFileSafe(videoId);
        }


    }


    private void validate(MultipartFile video){

        String mimeType = video.getContentType();
        String fileName = video.getOriginalFilename().toLowerCase();

        if(video == null || video.isEmpty()){
            throw new RuntimeException("File is empty");
        }

        if (mimeType == null || !mimeType.startsWith("video/")){
            throw new RuntimeException("File must be a video");
        }

        if(fileName == null || !(fileName.endsWith(".mp4") || fileName.endsWith(".mov"))){
            throw new RuntimeException("Invalid video format");
        }
    }


    public LessonResponse getLesson(long lessonId) {

        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));


        return lessonMapper.toDto(lesson);

    }

    public ResponseEntity<DownloadUrl> getLessonVideo(long lessonId) {

        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));

        return fileService.getFile(lesson.getVideoId());


    }


    @Transactional
    public void deleteLesson(long lessonId, long userId) {

        Lesson lesson = lessonRepo.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));

        if(lesson.getSection().getCourse().getCoachId() != userId){
            throw new AccessDeniedException("Not your course");
        }


        lessonRepo.deleteById(lessonId);

        fileService.deleteFileSafe(lesson.getVideoId());

    }
}
