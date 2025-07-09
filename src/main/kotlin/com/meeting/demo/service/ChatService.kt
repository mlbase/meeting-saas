package com.meeting.demo.service

import com.meeting.demo.model.Participant
import com.meeting.demo.model.VoiceProfile
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class ChatService(private val chatClient: ChatClient) {
    
    // Simple prompt
    fun chat(message: String): String {
        return chatClient.prompt()
            .user(message)
            .call()
            .content() ?: ""
    }
    
    // System prompt + user prompt
    fun chatWithSystem(systemPrompt: String, userMessage: String): String {
        return chatClient.prompt()
            .system(systemPrompt)
            .user(userMessage)
            .call()
            .content() ?: ""
    }
    
    // Meeting transcription with custom prompt
    fun transcribeAndSummarize(transcription: String): String {
        val systemPrompt = """
            You are a meeting assistant. Analyze the following meeting transcription and provide:
            1. A brief summary
            2. Key action items
            3. Important decisions made
            4. Next steps
            
            Format the response in markdown with clear headings.
        """.trimIndent()
        
        return chatClient.prompt()
            .system(systemPrompt)
            .user("Meeting transcription: $transcription")
            .call()
            .content() ?: ""
    }
    
    // Speaker identification and role analysis
    fun analyzeSpeakersAndRoles(transcription: String, knownParticipants: List<Participant>? = null): String {
        val participantInfo = knownParticipants?.joinToString("\n") { 
            "- ${it.name}: ${it.role} (${it.department})" 
        } ?: "No participant information provided"
        
        val systemPrompt = """
            You are an expert meeting analyst. Analyze the meeting transcription to identify:
            1. Who spoke and when (speaker diarization)
            2. Each speaker's role/position based on their speech patterns and content
            3. Speaking time distribution
            4. Key contributions by each speaker
            5. Decision-making patterns (who asks questions, who makes decisions, who provides information)
            
            Known participants:
            $participantInfo
            
            For each speaker, identify:
            - Speaking style (formal/informal, technical/business-focused)
            - Authority level (gives directions, asks for approval, reports status)
            - Expertise areas based on topics they discuss
            - Communication patterns (interrupts, asks questions, provides answers)
            
            Format as markdown with clear sections for each speaker.
        """.trimIndent()
        
        return chatClient.prompt()
            .system(systemPrompt)
            .user("Meeting transcription with timestamps: $transcription")
            .call()
            .content() ?: ""
    }
    
    // Identify speaker from voice characteristics description
    fun identifySpeakerFromVoice(voiceDescription: String, knownVoices: List<VoiceProfile>): String {
        val voiceProfiles = knownVoices.joinToString("\n") { 
            "- ${it.name}: ${it.voiceCharacteristics}" 
        }
        
        val systemPrompt = """
            You are a voice identification expert. Match the voice description to known voice profiles.
            
            Known voice profiles:
            $voiceProfiles
            
            Analyze the voice characteristics and provide:
            1. Most likely speaker match
            2. Confidence level (1-10)
            3. Reasoning for the match
        """.trimIndent()
        
        return chatClient.prompt()
            .system(systemPrompt)
            .user("Voice description: $voiceDescription")
            .call()
            .content() ?: ""
    }
    
    // Role-based meeting analysis
    fun analyzeByRole(transcription: String, meetingType: String): String {
        val systemPrompt = """
            You are analyzing a $meetingType meeting. Identify speakers and classify them into roles:
            
            For $meetingType meetings, typical roles include:
            - Meeting Leader/Facilitator
            - Subject Matter Expert
            - Stakeholder/Decision Maker
            - Team Member/Contributor
            - Observer/Note Taker
            
            For each identified speaker:
            1. Assign most likely role
            2. Provide evidence from their speech patterns
            3. Note their influence level in the meeting
            4. Highlight key contributions
            
            Format as structured analysis with role-based grouping.
        """.trimIndent()
        
        return chatClient.prompt()
            .system(systemPrompt)
            .user("Meeting transcription: $transcription")
            .call()
            .content() ?: ""
    }
    
    // Advanced prompt with parameters
    fun analyzeWithTemplate(content: String, analysisType: String): String {
        return chatClient.prompt()
            .system("You are an expert in $analysisType analysis.")
            .user("Analyze this content: $content")
            .call()
            .content() ?: ""
    }
}