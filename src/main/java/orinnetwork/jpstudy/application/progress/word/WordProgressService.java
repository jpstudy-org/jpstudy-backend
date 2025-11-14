package orinnetwork.jpstudy.application.progress.word;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.progress.word.dto.ReviewDifficulty;
import orinnetwork.jpstudy.application.progress.word.dto.StudySessionResponse;
import orinnetwork.jpstudy.application.progress.word.dto.WordCardDto;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.progress.MasteryLevel;
import orinnetwork.jpstudy.domain.progress.MemberWordProgress;
import orinnetwork.jpstudy.domain.progress.MemberWordProgressRepository;
import orinnetwork.jpstudy.domain.word.Word;
import orinnetwork.jpstudy.domain.word.WordRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordProgressService {

    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;
    private final MemberWordProgressRepository progressRepository;

    private static final int NEW_CARDS_PER_SESSION = 30;
    private static final double WORD_LEVEL_MODIFIER_BASE = 100.0;

    public StudySessionResponse getStudySession(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        Member member = memberRepository.getReferenceById(memberId);

        final String lang = member.getLanguagePreference();

        List<MemberWordProgress> reviewProgressList = progressRepository
                .findDueForReview(member, now);

        List<WordCardDto> reviewCards = reviewProgressList.stream()
                .map(progress -> new WordCardDto(progress.getWord(), lang))
                .toList();



        List<Word> newWordList = wordRepository
                .findNewWordForMember(memberId, PageRequest.of(0, NEW_CARDS_PER_SESSION));

        List<WordCardDto> newCards = newWordList.stream()
                .map(word -> new WordCardDto(word, lang))
                .toList();

        return new StudySessionResponse(reviewCards, newCards);
    }

    @Transactional
    public void updateProgress(Long memberId, Long wordId, ReviewDifficulty difficulty) {
        LocalDateTime now = LocalDateTime.now();

        Member member = memberRepository.getReferenceById(memberId);
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("Word not found"));

        MemberWordProgress progress = progressRepository
                .findByMemberAndWord(member, word)
                .orElse(new MemberWordProgress(member, word, MasteryLevel.NEW, null, now));

        MasteryLevel currentMasteryLevel = progress.getMasteryLevel();
        MasteryLevel nextMasteryLevel = calculateNextMasteryLevel(currentMasteryLevel, difficulty);

        // fail
        if (difficulty == ReviewDifficulty.AGAIN) {
            Duration immediateReviewInterval = MasteryLevel.NEW.getBaseInterval();
            progress.update(MasteryLevel.NEW, now, now.plus(immediateReviewInterval));
            progressRepository.save(progress);
            return;
        }

        Duration baseInterval = nextMasteryLevel.getBaseInterval();

        double difficultyModifier = Math.max(0.1, word.getLevel() / WORD_LEVEL_MODIFIER_BASE);

        long baseMinutes = baseInterval.toMinutes();
        long finalMinutes = (long) (baseMinutes * difficultyModifier);

        LocalDateTime nextReviewAt = now.plusMinutes(finalMinutes);

        progress.update(nextMasteryLevel, now, nextReviewAt);
        progressRepository.save(progress);
    }

    private MasteryLevel calculateNextMasteryLevel(MasteryLevel current, ReviewDifficulty difficulty) {
        switch (difficulty) {
            case AGAIN -> {
                return MasteryLevel.NEW;
            }
            case HARD -> {
                return current.getPreviousLevel();
            }
            case GOOD -> {
                return current.getNextLevel();
            }
            case EASY -> {
                MasteryLevel next = current.getNextLevel();
                return (next == MasteryLevel.MASTERED) ? MasteryLevel.MASTERED : next.getNextLevel();
            }
        }
        return current;
    }
}
