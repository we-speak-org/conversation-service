package org.wespeak.lesson.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wespeak.lesson.entity.*;
import org.wespeak.lesson.repository.*;

import java.util.*;

/**
 * Service to seed the database with test data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeedService {

    private final CourseRepository courseRepository;
    private final UnitRepository unitRepository;
    private final LessonRepository lessonRepository;
    private final UserProgressRepository userProgressRepository;
    private final LessonCompletionRepository lessonCompletionRepository;

    /**
     * Clear all data and seed fresh test data.
     */
    public Map<String, Object> seedDatabase() {
        log.info("Starting database seed...");
        
        // Clear existing data
        lessonCompletionRepository.deleteAll();
        userProgressRepository.deleteAll();
        lessonRepository.deleteAll();
        unitRepository.deleteAll();
        courseRepository.deleteAll();
        
        // Create English courses
        List<Course> courses = createEnglishCourses();
        
        // Create French courses
        courses.addAll(createFrenchCourses());
        
        int totalLessons = 0;
        int totalExercises = 0;
        
        for (Course course : courses) {
            List<Unit> units = unitRepository.findByCourseIdOrderByOrder(course.getId());
            for (Unit unit : units) {
                List<Lesson> lessons = lessonRepository.findByUnitIdOrderByOrder(unit.getId());
                totalLessons += lessons.size();
                for (Lesson lesson : lessons) {
                    totalExercises += lesson.getExercises().size();
                }
            }
        }
        
        log.info("Database seeded successfully: {} courses, {} lessons, {} exercises", 
            courses.size(), totalLessons, totalExercises);
        
        return Map.of(
            "status", "success",
            "courses", courses.size(),
            "totalLessons", totalLessons,
            "totalExercises", totalExercises
        );
    }

    private List<Course> createEnglishCourses() {
        List<Course> courses = new ArrayList<>();
        
        // Course: English for Beginners (A1)
        Course courseA1 = courseRepository.save(Course.builder()
            .targetLanguageCode("en")
            .level("A1")
            .title("English for Beginners")
            .description("Learn the basics of English for everyday communication")
            .imageUrl("https://cdn.wespeak.com/courses/en-a1.png")
            .order(1)
            .requiredXP(0)
            .estimatedHours(20)
            .isPublished(true)
            .build());
        courses.add(courseA1);
        
        // Unit 1: Greetings
        Unit unit1 = unitRepository.save(Unit.builder()
            .courseId(courseA1.getId())
            .title("Greetings")
            .description("Learn how to greet people and introduce yourself")
            .order(1)
            .build());
        
        // Lesson 1.1: Saying Hello
        lessonRepository.save(createGreetingsLesson1(unit1.getId()));
        
        // Lesson 1.2: Introducing Yourself
        lessonRepository.save(createGreetingsLesson2(unit1.getId()));
        
        // Lesson 1.3: How are you?
        lessonRepository.save(createGreetingsLesson3(unit1.getId()));
        
        // Unit 2: Numbers and Time
        Unit unit2 = unitRepository.save(Unit.builder()
            .courseId(courseA1.getId())
            .title("Numbers and Time")
            .description("Learn numbers and how to tell the time")
            .order(2)
            .build());
        
        // Lesson 2.1: Numbers 1-10
        lessonRepository.save(createNumbersLesson1(unit2.getId()));
        
        // Lesson 2.2: Numbers 11-100
        lessonRepository.save(createNumbersLesson2(unit2.getId()));
        
        // Unit 3: At the Restaurant
        Unit unit3 = unitRepository.save(Unit.builder()
            .courseId(courseA1.getId())
            .title("At the Restaurant")
            .description("Learn vocabulary and phrases for ordering food")
            .order(3)
            .build());
        
        // Lesson 3.1: Food vocabulary
        lessonRepository.save(createRestaurantLesson1(unit3.getId()));
        
        return courses;
    }
    
    private List<Course> createFrenchCourses() {
        List<Course> courses = new ArrayList<>();
        
        // Course: French for Beginners (A1)
        Course courseA1 = courseRepository.save(Course.builder()
            .targetLanguageCode("fr")
            .level("A1")
            .title("Français pour Débutants")
            .description("Apprenez les bases du français pour communiquer au quotidien")
            .imageUrl("https://cdn.wespeak.com/courses/fr-a1.png")
            .order(1)
            .requiredXP(0)
            .estimatedHours(25)
            .isPublished(true)
            .build());
        courses.add(courseA1);
        
        // Unit 1: Les Salutations
        Unit unit1 = unitRepository.save(Unit.builder()
            .courseId(courseA1.getId())
            .title("Les Salutations")
            .description("Apprenez à saluer et à vous présenter")
            .order(1)
            .build());
        
        // Lesson: Dire bonjour
        lessonRepository.save(createFrenchGreetingsLesson1(unit1.getId()));
        
        return courses;
    }

    private Lesson createGreetingsLesson1(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: MCQ
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(1)
            .question("How do you say 'Bonjour' in English?")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "Hello"),
                    Map.of("id", "b", "text", "Goodbye"),
                    Map.of("id", "c", "text", "Thank you"),
                    Map.of("id", "d", "text", "Sorry")
                )
            ))
            .correctAnswer(Map.of("optionId", "a", "text", "Hello"))
            .points(10)
            .build());
        
        // Exercise 2: Fill the gap
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.fill_gap)
            .order(2)
            .question("Complete: Nice to ___ you!")
            .hint("A common verb for meetings")
            .content(Map.of(
                "sentence", "Nice to ___ you!",
                "blanks", List.of("___")
            ))
            .correctAnswer(Map.of("text", "meet", "alternatives", List.of("Meet", "MEET")))
            .points(10)
            .build());
        
        // Exercise 3: MCQ
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(3)
            .question("What does 'Good morning' mean?")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "Bonsoir"),
                    Map.of("id", "b", "text", "Bonjour (le matin)"),
                    Map.of("id", "c", "text", "Bonne nuit"),
                    Map.of("id", "d", "text", "Salut")
                )
            ))
            .correctAnswer(Map.of("optionId", "b", "text", "Bonjour (le matin)"))
            .points(10)
            .build());
        
        // Exercise 4: Translation
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.translation)
            .order(4)
            .question("Translate to English: Bonsoir")
            .content(Map.of("sourceText", "Bonsoir"))
            .correctAnswer(Map.of("text", "Good evening", "alternatives", List.of("Good night")))
            .points(15)
            .build());
        
        // Exercise 5: Ordering
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.ordering)
            .order(5)
            .question("Put the words in order to form a greeting:")
            .content(Map.of(
                "words", List.of("you", "are", "Hello,", "how", "?")
            ))
            .correctAnswer(Map.of("order", List.of("Hello,", "how", "are", "you", "?")))
            .points(15)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("Saying Hello")
            .description("Learn basic greetings in English")
            .type(Lesson.LessonType.vocabulary)
            .order(1)
            .estimatedMinutes(10)
            .xpReward(15)
            .exercises(exercises)
            .build();
    }

    private Lesson createGreetingsLesson2(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(1)
            .question("How do you ask someone's name in English?")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "What is your name?"),
                    Map.of("id", "b", "text", "How are you?"),
                    Map.of("id", "c", "text", "Where are you from?"),
                    Map.of("id", "d", "text", "How old are you?")
                )
            ))
            .correctAnswer(Map.of("optionId", "a", "text", "What is your name?"))
            .points(10)
            .build());
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.fill_gap)
            .order(2)
            .question("Complete: My ___ is John.")
            .content(Map.of("sentence", "My ___ is John."))
            .correctAnswer(Map.of("text", "name", "alternatives", List.of("Name")))
            .points(10)
            .build());
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.translation)
            .order(3)
            .question("Translate: Je m'appelle Marie")
            .content(Map.of("sourceText", "Je m'appelle Marie"))
            .correctAnswer(Map.of("text", "My name is Marie", "alternatives", List.of("I am Marie", "I'm Marie")))
            .points(15)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("Introducing Yourself")
            .description("Learn how to introduce yourself")
            .type(Lesson.LessonType.vocabulary)
            .order(2)
            .estimatedMinutes(12)
            .xpReward(15)
            .exercises(exercises)
            .build();
    }

    private Lesson createGreetingsLesson3(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(1)
            .question("What is the correct response to 'How are you?'")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "I'm fine, thank you"),
                    Map.of("id", "b", "text", "My name is John"),
                    Map.of("id", "c", "text", "Nice to meet you"),
                    Map.of("id", "d", "text", "Goodbye")
                )
            ))
            .correctAnswer(Map.of("optionId", "a", "text", "I'm fine, thank you"))
            .points(10)
            .build());
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.match_pairs)
            .order(2)
            .question("Match the questions with the answers:")
            .content(Map.of(
                "left", List.of(
                    Map.of("id", "1", "text", "How are you?"),
                    Map.of("id", "2", "text", "What's your name?"),
                    Map.of("id", "3", "text", "Nice to meet you")
                ),
                "right", List.of(
                    Map.of("id", "a", "text", "I'm fine"),
                    Map.of("id", "b", "text", "Nice to meet you too"),
                    Map.of("id", "c", "text", "My name is John")
                )
            ))
            .correctAnswer(Map.of("pairs", List.of(
                Map.of("left", "1", "right", "a"),
                Map.of("left", "2", "right", "c"),
                Map.of("left", "3", "right", "b")
            )))
            .points(20)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("How are you?")
            .description("Learn to ask about well-being")
            .type(Lesson.LessonType.grammar)
            .order(3)
            .estimatedMinutes(15)
            .xpReward(20)
            .exercises(exercises)
            .build();
    }

    private Lesson createNumbersLesson1(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(1)
            .question("What is 'five' in numbers?")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "3"),
                    Map.of("id", "b", "text", "5"),
                    Map.of("id", "c", "text", "7"),
                    Map.of("id", "d", "text", "9")
                )
            ))
            .correctAnswer(Map.of("optionId", "b", "text", "5"))
            .points(10)
            .build());
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.ordering)
            .order(2)
            .question("Put the numbers in order from smallest to largest:")
            .content(Map.of("words", List.of("seven", "two", "nine", "four")))
            .correctAnswer(Map.of("order", List.of("two", "four", "seven", "nine")))
            .points(15)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("Numbers 1-10")
            .description("Learn numbers from one to ten")
            .type(Lesson.LessonType.vocabulary)
            .order(1)
            .estimatedMinutes(10)
            .xpReward(15)
            .exercises(exercises)
            .build();
    }

    private Lesson createNumbersLesson2(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.translation)
            .order(1)
            .question("Translate: cinquante")
            .content(Map.of("sourceText", "cinquante"))
            .correctAnswer(Map.of("text", "fifty", "alternatives", List.of("50")))
            .points(10)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("Numbers 11-100")
            .description("Learn larger numbers")
            .type(Lesson.LessonType.vocabulary)
            .order(2)
            .estimatedMinutes(15)
            .xpReward(20)
            .exercises(exercises)
            .build();
    }

    private Lesson createRestaurantLesson1(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(1)
            .question("What does 'I would like' mean?")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "Je voudrais"),
                    Map.of("id", "b", "text", "J'ai"),
                    Map.of("id", "c", "text", "Je suis"),
                    Map.of("id", "d", "text", "Je vais")
                )
            ))
            .correctAnswer(Map.of("optionId", "a", "text", "Je voudrais"))
            .points(10)
            .build());
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.fill_gap)
            .order(2)
            .question("Complete: I would like a ___ of water, please.")
            .hint("A container for water")
            .content(Map.of("sentence", "I would like a ___ of water, please."))
            .correctAnswer(Map.of("text", "glass", "alternatives", List.of("bottle", "cup")))
            .points(10)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("Food Vocabulary")
            .description("Learn words for ordering food")
            .type(Lesson.LessonType.vocabulary)
            .order(1)
            .estimatedMinutes(12)
            .xpReward(15)
            .exercises(exercises)
            .build();
    }

    private Lesson createFrenchGreetingsLesson1(String unitId) {
        List<Exercise> exercises = new ArrayList<>();
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.mcq)
            .order(1)
            .question("Comment dit-on 'Hello' en français ?")
            .content(Map.of(
                "options", List.of(
                    Map.of("id", "a", "text", "Bonjour"),
                    Map.of("id", "b", "text", "Au revoir"),
                    Map.of("id", "c", "text", "Merci"),
                    Map.of("id", "d", "text", "Pardon")
                )
            ))
            .correctAnswer(Map.of("optionId", "a", "text", "Bonjour"))
            .points(10)
            .build());
        
        exercises.add(Exercise.builder()
            .id(UUID.randomUUID().toString())
            .type(Exercise.ExerciseType.fill_gap)
            .order(2)
            .question("Complétez : Enchanté de faire votre ___ !")
            .hint("Un mot pour 'rencontre'")
            .content(Map.of("sentence", "Enchanté de faire votre ___ !"))
            .correctAnswer(Map.of("text", "connaissance", "alternatives", List.of("Connaissance")))
            .points(10)
            .build());
        
        return Lesson.builder()
            .unitId(unitId)
            .title("Dire bonjour")
            .description("Apprenez les salutations de base en français")
            .type(Lesson.LessonType.vocabulary)
            .order(1)
            .estimatedMinutes(10)
            .xpReward(15)
            .exercises(exercises)
            .build();
    }
}
