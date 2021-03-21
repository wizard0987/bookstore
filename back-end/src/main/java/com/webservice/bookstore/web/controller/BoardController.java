package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.domain.entity.board.Board;
import com.webservice.bookstore.service.BoardService;
import com.webservice.bookstore.service.ReplyService;
import com.webservice.bookstore.web.dto.BoardDTO;
import com.webservice.bookstore.web.dto.PageRequestDTO;
import com.webservice.bookstore.web.dto.PageResultDTO;
import com.webservice.bookstore.web.dto.ReplyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE+";charset=utf-8")
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private ReplyService replyService;


    @GetMapping("/board/")
    public ResponseEntity<PageResultDTO<BoardDTO, Board>> showPageBoardList(PageRequestDTO pageRequestDTO){
        PageResultDTO<BoardDTO,Board> pageResultDTO = boardService.pageCommunityList(pageRequestDTO);

        return new ResponseEntity<>(pageResultDTO, HttpStatus.OK);
    }

    @GetMapping("/board/{board_id}/")
    public ResponseEntity<Object> showDetatilBoard(@PathVariable("board_id") long id){
        BoardDTO boardDTO = boardService.showBoardDetailPage(id);
        List<ReplyDTO> replylist = replyService.getBoardReplylist(boardDTO);
        List<Object> list=new ArrayList<>();
        list.add((Object) boardDTO);
        list.add((Object) replylist);

        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PostMapping("/board/")
    public ResponseEntity<String> boardRegister(@RequestBody BoardDTO boardDTO){
        boardService.boardRegister(boardDTO);
        return new ResponseEntity<>("string",HttpStatus.OK);
    }
}