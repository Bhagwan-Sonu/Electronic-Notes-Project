package com.enotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.enotes.entity.FileDetails;

public interface FileRepository extends JpaRepository<FileDetails, Integer> {

}
