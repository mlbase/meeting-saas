package com.meeting.demo.controller

import com.meeting.demo.service.ChatService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(private val chatService: ChatService) {
    
    @PostMapping("/simple")
    fun simpleChat(@RequestBody request: ChatRequest): ChatResponse {
        val response = chatService.chat(request.message)
        return ChatResponse(response)
    }
    
    @PostMapping("/meeting-summary")
    fun meetingSummary(@RequestBody request: TranscriptionRequest): ChatResponse {
        val response = chatService.transcribeAndSummarize(request.transcription)
        return ChatResponse(response)
    }
    
    @PostMapping("/analyze")
    fun analyze(@RequestBody request: AnalysisRequest): ChatResponse {
        val response = chatService.analyzeWithTemplate(request.content, request.analysisType)
        return ChatResponse(response)
    }
}

data class ChatRequest(val message: String)
data class TranscriptionRequest(val transcription: String)
data class AnalysisRequest(val content: String, val analysisType: String)
data class ChatResponse(val response: String)