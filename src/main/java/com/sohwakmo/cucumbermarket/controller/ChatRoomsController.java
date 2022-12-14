package com.sohwakmo.cucumbermarket.controller;

import com.sohwakmo.cucumbermarket.domain.ChatRoom;
import com.sohwakmo.cucumbermarket.domain.Member;
import com.sohwakmo.cucumbermarket.domain.Message;
import com.sohwakmo.cucumbermarket.repository.MemberRepository;
import com.sohwakmo.cucumbermarket.repository.MessageRepository;
import com.sohwakmo.cucumbermarket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomsController {
    private final ChatRoomService chatRoomService;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;


    /**
     * 마이페이지에서 채팅 리스트의 목록을 불러온다.
     * @param memberNo 회원 번호를 받아서정보를 가져온다.
     * @param model 
     * @return 채팅리스트
     */
    @GetMapping("/list")
    public String showChatList(Integer memberNo,Model model){ // 멤버넘버로 닉네임도 찾아서 채팅방리스트에서 메세지 리시트도 받아야함.
        log.info("memberId={}",memberNo);
        List<ChatRoom> list = chatRoomService.getAllChatList(memberNo);
        String memberNickname = chatRoomService.getLoginedName(memberNo);
        Member member = chatRoomService.getLoginedMember(memberNo);
        for(ChatRoom c : list){
            chatRoomService.setMessages(c,memberNickname);
        }
        model.addAttribute("list",list);
        model.addAttribute("memberNo",memberNo);
        model.addAttribute("nickName", memberNickname);
        return "/chat/chatList";
    }

   @GetMapping("/productChatList")
   public String showProductChatList(Integer memberNo,Model model){ // 멤버넘버로 닉네임도 찾아서 채팅방리스트에서 메세지 리시트도 받아야함.
       log.info("memberId={}",memberNo);
       List<ChatRoom> list = chatRoomService.getAllChatList(memberNo);
       String memberNickname = chatRoomService.getLoginedName(memberNo);
       Member member = chatRoomService.getLoginedMember(memberNo);
       for(ChatRoom c : list){
           chatRoomService.setMessages(c,memberNickname);
       }
       model.addAttribute("list",list);
       model.addAttribute("memberNo",memberNo);
       model.addAttribute("nickName", memberNickname);
       return "/chat/productChatList";
   }

    @GetMapping("/chatRoom")
    public void getRoom(String roomId,String nickname,Integer memberNo,Model model){
        ChatRoom chatRoom = chatRoomService.saveAndGetChatRoom(roomId,memberNo,nickname);
        List<Message> loadMessage = chatRoomService.getAllMessages(chatRoom);
        log.info(chatRoom.toString());
        Member chatRoomMember = memberRepository.findByNickname(roomId).orElse(null);
        chatRoomService.setLastCheckUser(chatRoom,nickname); // 이 채팅방에 누가 제일 마직막에 들어갔는지 업데이트
        model.addAttribute("room", chatRoom);
        model.addAttribute("chatRoomMember",chatRoomMember);
        model.addAttribute("nickname",nickname);
        model.addAttribute("memberNo", memberNo);
        model.addAttribute("nicknameNum",chatRoom.getMember().getMemberNo());
        model.addAttribute("messages", loadMessage);
    }

    @GetMapping("/delete")
    public String deleteChatRoom(String roomId, String nickname, Integer memberNo, RedirectAttributes attributes){
        chatRoomService.deleteChatRoom(roomId, nickname,memberNo);
        attributes.addAttribute("memberNo", memberNo);
        return "redirect:/chat/list";
    }
}
