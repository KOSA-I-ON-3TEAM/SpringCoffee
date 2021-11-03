package com.kosa.springcoffee.repository;

import com.kosa.springcoffee.entity.QnaBoard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QnaBoardRepository extends JpaRepository<QnaBoard, Long> {
    @EntityGraph(attributePaths = "writer", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select b from sc_qna_board b where b.qnaBoardNo = :qnaBoardNo")
    Optional<QnaBoard> getWithWriter(Long qnaBoardNo);

    @EntityGraph(attributePaths = {"writer"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select b from sc_qna_board b where b.writer.email = :email")
    List<QnaBoard> getList(String email);

}
