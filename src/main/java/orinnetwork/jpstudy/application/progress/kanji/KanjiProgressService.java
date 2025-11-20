package orinnetwork.jpstudy.application.progress.kanji;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.progress.kanji.dto.KanjiCard;
import orinnetwork.jpstudy.application.progress.kanji.dto.ReviewDifficulty;
import orinnetwork.jpstudy.application.progress.kanji.dto.StudySessionResponse;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.kanji.KanjiRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.progress.MasteryLevel;
import orinnetwork.jpstudy.domain.progress.MemberKanjiProgress;
import orinnetwork.jpstudy.domain.progress.MemberKanjiProgressRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanjiProgressService {

    private final MemberRepository memberRepository;
    private final KanjiRepository kanjiRepository;
    private final MemberKanjiProgressRepository progressRepository;

    private static final int NEW_CARDS_PER_SESSION = 30;
    private static final double KANJI_LEVEL_MODIFIER_BASE = 100.0;


    public StudySessionResponse getStudySession(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        Member member = memberRepository.getReferenceById(memberId);
        String lang = member.getLanguagePreference();

        List<MemberKanjiProgress> reviewProgressList = progressRepository
                .findDueForReview(member, now);

        List<KanjiCard> reviewCards = reviewProgressList.stream()
                .map(progress -> new KanjiCard(progress.getKanji(), lang))
                .toList();

        List<Kanji> newKanjiList = kanjiRepository
                .findNewKanjiForMember(memberId, PageRequest.of(0, NEW_CARDS_PER_SESSION));

        List<KanjiCard> newCards = newKanjiList.stream()
                .map(kanji -> new KanjiCard(kanji, lang))
                .toList();

        return new StudySessionResponse(reviewCards, newCards);
    }

    @Transactional
    public void updateProgress(Long memberId, Long kanjiId, ReviewDifficulty difficulty) {
        LocalDateTime now = LocalDateTime.now();

        Member member = memberRepository.getReferenceById(memberId);
        Kanji kanji = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new CustomException(ErrorCode.KANJI_NOT_FOUND));

        MemberKanjiProgress progress = progressRepository
                .findByMemberAndKanji(member, kanji)
                .orElse(new MemberKanjiProgress(member, kanji, MasteryLevel.NEW, null, now));

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

        double difficultyModifier = Math.max(0.1, kanji.getLevel() / KANJI_LEVEL_MODIFIER_BASE);

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
