package com.example.file_service.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<FileMetaData, ObjectId>{

    List<FileMetaData> findAllByUserId(long userId);

}
