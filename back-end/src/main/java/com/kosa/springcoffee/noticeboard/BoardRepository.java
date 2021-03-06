package com.kosa.springcoffee.noticeboard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByCategory(String category, Pageable pageable);

    Page<Board> findByTitleContaining(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = "writer", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select b from sc_notice_board b where b.boardNo = :boardNo")
    Optional<Board> getWithWriter(Long boardNo);

    @EntityGraph(attributePaths = {"writer"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select b from sc_notice_board b where b.writer.email = :email")
    List<Board> getList(String email);
}
