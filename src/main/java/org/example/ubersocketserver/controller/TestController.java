package org.example.ubersocketserver.controller;

import org.example.ubersocketserver.dto.ChatRequest;
import org.example.ubersocketserver.dto.ChatResponse;
import org.example.ubersocketserver.dto.TestRequest;
import org.example.ubersocketserver.dto.TestResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public TestController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/ping")
    @SendTo("/topic/ping")
    public TestResponse pingCheck(TestRequest message) {
        System.out.println("Received msg:"+message.getData());
        return TestResponse.builder().data("Received").build();
    }

//    @Scheduled(fixedDelay = 2000)
//    public void sendPeriodicMessage()
//    {
//        System.out.println("Executed periodic function");
//        simpMessagingTemplate.convertAndSend("/topic/scheduled","periodic msg sent"+System.currentTimeMillis());
//    }

    @MessageMapping("/chat")
    @SendTo("/topic/message")
    public ChatResponse chatMessage(ChatRequest request)
    {
        return ChatResponse.builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp(""+System.currentTimeMillis())
                .build();
    }
//to send msg to particular person in grp
    @MessageMapping("/privateChat/{room}/{userId}")
    public void privateChatMessage(@DestinationVariable String room,@DestinationVariable String userId, ChatRequest request)
    {
         ChatResponse response=ChatResponse.builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp(""+System.currentTimeMillis())
                .build();

         simpMessagingTemplate.convertAndSendToUser(userId,"/queue/privateMessage/"+room,response);
    }
}
