package com.example.hummet.data.repository

import com.example.hummet.data.model.InterviewTopic
import com.example.hummet.data.model.TopicDetail
import com.example.hummet.data.remote.InterviewApiService

class InterviewRepository(private val apiService: InterviewApiService) {

    private val fakeTopics = listOf(
        InterviewTopic(
            id = "1",
            title = "Object-Oriented Programming",
            description = "Core OOP concepts: encapsulation and inheritance.",
            difficulty = "Junior",
            category = "Backend",
            questionsCount = 24,
            estimatedTime = "45 min",
            icon = "backend",
            objectives = listOf(
                "Understand encapsulation and data hiding",
                "Master inheritance and polymorphism",
                "Learn abstraction principles"
            ),
            prerequisites = listOf("Basic programming knowledge", "Understanding of classes")
        ),
        InterviewTopic(
            id = "2",
            title = "Kotlin Coroutines",
            description = "Structured concurrency and Flow.",
            difficulty = "Middle",
            category = "Mobile",
            questionsCount = 32,
            estimatedTime = "60 min",
            icon = "mobile",
            objectives = listOf(
                "Master async programming with coroutines",
                "Understand Flow and StateFlow",
                "Handle cancellation and exceptions"
            ),
            prerequisites = listOf("Kotlin basics", "Understanding of threads")
        ),
        InterviewTopic(
            id = "3",
            title = "Jetpack Compose",
            description = "Modern declarative UI and State management.",
            difficulty = "Middle",
            category = "Mobile",
            questionsCount = 28,
            estimatedTime = "55 min",
            icon = "mobile",
            objectives = listOf(
                "Build declarative UIs",
                "Manage state effectively",
                "Create reusable composables"
            ),
            prerequisites = listOf("Android basics", "Kotlin knowledge")
        ),
        InterviewTopic(
            id = "4",
            title = "Clean Architecture",
            description = "SOLID principles and layered architecture.",
            difficulty = "Senior",
            category = "Architecture",
            questionsCount = 20,
            estimatedTime = "50 min",
            icon = "architecture",
            objectives = listOf(
                "Apply SOLID principles",
                "Design scalable architectures",
                "Separate concerns effectively"
            ),
            prerequisites = listOf("Design patterns", "Software engineering experience")
        )
    )

    private val fakeTopicDetails = mapOf(
        "1" to TopicDetail(
            topic = fakeTopics[0],
            sampleQuestions = listOf(
                "What is encapsulation and why is it important?",
                "Explain the difference between inheritance and composition",
                "How does polymorphism work in OOP?"
            ),
            skillsCovered = listOf(
                "Encapsulation",
                "Inheritance",
                "Polymorphism",
                "Abstraction",
                "Design Patterns"
            ),
            completionRate = 0.65f
        ),
        "2" to TopicDetail(
            topic = fakeTopics[1],
            sampleQuestions = listOf(
                "What is the difference between launch and async?",
                "How do you handle exceptions in coroutines?",
                "Explain the lifecycle of a coroutine"
            ),
            skillsCovered = listOf(
                "Coroutine Builders",
                "Dispatchers",
                "Flow API",
                "Exception Handling",
                "Structured Concurrency"
            ),
            completionRate = 0.40f
        ),
        "3" to TopicDetail(
            topic = fakeTopics[2],
            sampleQuestions = listOf(
                "What is recomposition in Compose?",
                "How do you manage state in Compose?",
                "Explain remember vs rememberSaveable"
            ),
            skillsCovered = listOf(
                "Composable Functions",
                "State Management",
                "Side Effects",
                "Recomposition",
                "UI Testing"
            ),
            completionRate = 0.55f
        ),
        "4" to TopicDetail(
            topic = fakeTopics[3],
            sampleQuestions = listOf(
                "Explain the SOLID principles",
                "What is dependency injection?",
                "How do you structure a clean architecture project?"
            ),
            skillsCovered = listOf(
                "SOLID Principles",
                "Dependency Injection",
                "Layer Separation",
                "Repository Pattern",
                "Use Cases"
            ),
            completionRate = 0.30f
        )
    )

    suspend fun getTopics(): Result<List<InterviewTopic>> {
        return try {
            val apiTopics = apiService.getTopics()
            Result.success(apiTopics)
        } catch (e: Exception) {
            Result.success(fakeTopics)
        }
    }

    suspend fun getTopicDetail(topicId: String): Result<TopicDetail> {
        return try {
            val detail = apiService.getTopicDetail(topicId)
            Result.success(detail)
        } catch (e: Exception) {
            val fakeDetail = fakeTopicDetails[topicId]
            if (fakeDetail != null) {
                Result.success(fakeDetail)
            } else {
                Result.failure(Exception("Topic not found"))
            }
        }
    }
}