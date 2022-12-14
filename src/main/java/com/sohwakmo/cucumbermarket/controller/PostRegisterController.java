package com.sohwakmo.cucumbermarket.controller;

import com.sohwakmo.cucumbermarket.domain.Post;
import com.sohwakmo.cucumbermarket.service.PostService;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/reply")
public class PostRegisterController {

    private final PostService postService;

    @DeleteMapping("/{imageSrc}")
    public ResponseEntity<String> deleteImage(@PathVariable String imageSrc) throws Exception{
        log.info("src={}", imageSrc);
        String result = postService.chekImageNumandDeleteImage(imageSrc);
        log.info(result);
        return ResponseEntity.ok("성공");
    }

    @PutMapping("/1{postNo}")
    public ResponseEntity<String>modifyImage01(@PathVariable Integer postNo,@RequestParam("file")@RequestBody MultipartFile data)throws Exception{
        log.info("file={}",data.getResource());
        Post post = postService.findPostByPostNo(postNo);
        log.info(post.toString());
        String fileName = postService.modifyImage01(post,data);
        log.info(fileName);
        return ResponseEntity.ok(fileName);
    }

    @PutMapping("/2{postNo}")
    public ResponseEntity<String>modifyImage02(@PathVariable Integer postNo,@RequestParam("file")@RequestBody MultipartFile data)throws Exception{
        log.info("file={}",data.getResource());
        Post post = postService.findPostByPostNo(postNo);
        log.info(post.toString());
        String fileName = postService.modifyImage02(post,data);
        log.info(fileName);
        return ResponseEntity.ok(fileName);
    }

    @PutMapping("/3{postNo}")
    public ResponseEntity<String>insertImage(@PathVariable Integer postNo, @RequestParam("file") @RequestBody MultipartFile data) throws Exception {
        log.info("data={}", data);
        log.info("postNo={}", postNo);
        Post post = postService.findPostByPostNo(postNo);
        if (post.getImageUrl01() != null && post.getImageUrl02() != null) {
            return ResponseEntity.ok("사진은 2장까지 가능합니다!!");
        } else {
            String result = postService.insertImage(post, data);
            return ResponseEntity.ok(result);
        }
    }
}
