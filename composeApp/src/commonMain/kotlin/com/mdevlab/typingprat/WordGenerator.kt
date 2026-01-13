package com.mdevlab.typingprat

object WordGenerator {
    private val paragraphs = listOf(
        // Technology
        "Technology has transformed the way we live and work. From smartphones to artificial intelligence, innovations continue to reshape our daily routines. The rapid pace of change requires us to adapt constantly, learning new skills and embracing digital tools that enhance productivity and communication.",

        // Programming
        "Learning to code is like learning a new language. It requires patience, practice, and persistence. Every programmer starts with simple programs and gradually builds complex applications. The journey from beginner to expert is filled with challenges, but each obstacle overcome brings valuable knowledge and confidence.",

        // Typing Practice
        "Developing fast and accurate typing skills is essential in today's digital world. Whether you are writing emails, coding software, or creating documents, efficient typing saves time and reduces frustration. Regular practice with varied content helps build muscle memory and improves overall speed without sacrificing accuracy.",

        // Nature
        "The forest awakens with the first light of dawn. Birds begin their morning chorus while dew drops glisten on leaves and grass. A gentle breeze carries the scent of pine and wildflowers through the air. Nature offers a peaceful escape from the busy modern world, reminding us of life's simple beauty.",

        // Science
        "Scientific discovery drives human progress forward. Through careful observation, experimentation, and analysis, researchers unlock the mysteries of our universe. From understanding subatomic particles to exploring distant galaxies, science expands our knowledge and opens new possibilities for improving life on Earth.",

        // History
        "History teaches us valuable lessons about humanity. By studying past civilizations, we understand how societies rise and fall, how cultures evolve, and how individuals shape the course of events. Learning from historical successes and failures helps us make better decisions for our collective future.",

        // Health and Wellness
        "Maintaining good health requires a balanced approach to life. Regular exercise strengthens the body while proper nutrition provides essential fuel for daily activities. Mental wellness is equally important, achieved through stress management, adequate sleep, and meaningful social connections with others.",

        // Education
        "Education empowers individuals to reach their full potential. Through learning, we acquire knowledge, develop critical thinking skills, and prepare for future challenges. Quality education opens doors to opportunities and enables people to contribute meaningfully to their communities and society as a whole.",

        // Business
        "Successful businesses adapt to changing market conditions. They listen to customer feedback, innovate their products and services, and build strong teams. Leadership, strategic planning, and effective communication are essential elements that distinguish thriving organizations from those that struggle to survive.",

        // Art and Creativity
        "Creativity flows from imagination and curiosity. Artists express their unique perspectives through various mediums including painting, sculpture, music, and literature. Creative endeavors enrich our culture, challenge our assumptions, and inspire us to see the world from different viewpoints and possibilities."
    )

    private val essays = listOf(
        // Essay on Remote Work
        """Remote work has become increasingly common in recent years, fundamentally changing how organizations operate and employees balance their professional and personal lives. This shift was accelerated by global events that forced companies to adopt flexible working arrangements almost overnight. Many workers discovered they could be equally productive, or even more so, when working from home without the distractions and time consumption of daily commutes. However, remote work also presents challenges including maintaining team cohesion, preventing burnout, and ensuring clear communication across distributed teams. Companies must invest in proper technology infrastructure and develop new management strategies to support remote employees effectively.""",

        // Essay on Climate Change
        """Climate change represents one of the most significant challenges facing humanity today. Rising global temperatures are causing glaciers to melt, sea levels to rise, and weather patterns to become increasingly unpredictable. These changes affect ecosystems worldwide, threatening biodiversity and disrupting agricultural systems that billions of people depend upon for food. Addressing climate change requires coordinated international effort, technological innovation, and individual commitment to sustainable practices. Renewable energy sources, improved efficiency, and conservation efforts all play important roles in reducing greenhouse gas emissions and protecting our planet for future generations.""",

        // Essay on Digital Literacy
        """Digital literacy has become as fundamental as reading and writing in modern society. Understanding how to navigate the internet safely, evaluate online information critically, and use digital tools effectively are essential skills for students, workers, and citizens alike. Schools are integrating technology education into their curricula, teaching children not only how to use devices but also how to think critically about digital content. As misinformation spreads easily online, the ability to distinguish reliable sources from unreliable ones becomes increasingly important. Digital literacy also includes understanding privacy concerns, cybersecurity basics, and responsible online behavior."""
    )

    fun randomSentence(): String = paragraphs.random()

    fun randomEssay(): String = essays.random()

    fun randomContent(difficulty: Difficulty = Difficulty.MEDIUM): String = when (difficulty) {
        Difficulty.EASY -> paragraphs.random().split(".").take(2).joinToString(".") + "."
        Difficulty.MEDIUM -> paragraphs.random()
        Difficulty.HARD -> essays.random()
    }

    enum class Difficulty { EASY, MEDIUM, HARD }
}