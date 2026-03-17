package orinnetwork.jpstudy.application.progress.word;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.progress.common.component.FsrsScheduler;
import orinnetwork.jpstudy.application.progress.common.dto.IntervalPreview;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewDifficulty;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewResponse;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewResult;
import orinnetwork.jpstudy.application.progress.word.dto.StudySessionResponse;
import orinnetwork.jpstudy.application.progress.word.dto.WordCard;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.progress.MemberWordProgress;
import orinnetwork.jpstudy.domain.progress.MemberWordProgressRepository;
import orinnetwork.jpstudy.domain.progress.StudySession;
import orinnetwork.jpstudy.domain.progress.StudySessionRepository;
import orinnetwork.jpstudy.domain.progress.StudySessionType;
import orinnetwork.jpstudy.domain.word.Word;
import orinnetwork.jpstudy.domain.word.WordRepository;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WordProgressService {

    private final MemberRepository memberRepository;
    private final WordRepository wordRepository;
    private final MemberWordProgressRepository progressRepository;
    private final StudySessionRepository sessionRepository;
    private final FsrsScheduler fsrsScheduler;

    private static final int NEW_CARDS_PER_SESSION = 30;

    @Transactional
    public StudySessionResponse getStudySession(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        String lang = member.getLanguagePreference();

        List<WordCard> reviewCards = getReviewCards(member, lang);

        StudySession session = sessionRepository.findActiveSession(member, StudySessionType.WORD)
                .orElseGet(() -> createNewSession(member, memberId));

        if (session == null) {
            return new StudySessionResponse(null, reviewCards, List.of(), 0, 0, true, false);
        }

        List<WordCard> newCards = getSessionNewCards(session, lang);
        boolean hasMore = wordRepository.countNewWordForMember(memberId) > 0;

        return new StudySessionResponse(
                session.getId(), reviewCards, newCards,
                session.getTotalCount(), session.getStudiedCount(),
                session.isCompleted(), hasMore
        );
    }

    @Transactional
    public StudySessionResponse addMoreLearning(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        String lang = member.getLanguagePreference();

        sessionRepository.findActiveSession(member, StudySessionType.WORD)
                .ifPresent(StudySession::complete);

        StudySession session = createNewSession(member, memberId);
        List<WordCard> reviewCards = getReviewCards(member, lang);

        if (session == null) {
            return new StudySessionResponse(null, reviewCards, List.of(), 0, 0, true, false);
        }

        List<WordCard> newCards = getSessionNewCards(session, lang);
        boolean hasMore = wordRepository.countNewWordForMember(memberId) > 0;

        return new StudySessionResponse(
                session.getId(), reviewCards, newCards,
                session.getTotalCount(), session.getStudiedCount(),
                session.isCompleted(), hasMore
        );
    }

    @Transactional
    public ReviewResponse updateProgress(Long memberId, Long wordId, ReviewDifficulty difficulty) {
        MemberWordProgress progress = getOrCreateProgress(memberId, wordId);

        ReviewResult result = fsrsScheduler.calculateNextReview(
                progress, progress.getWord(), difficulty
        );

        progress.updateFsrs(
                result.newStability(), result.newDifficulty(),
                result.reviewedAt(), result.nextReviewAt()
        );

        Member member = memberRepository.getReferenceById(memberId);
        StudySession session = sessionRepository.findActiveSession(member, StudySessionType.WORD)
                .orElse(null);

        long studiedCount = 0;
        int totalCount = 0;
        boolean sessionCompleted = false;

        if (session != null) {
            session.markItemStudied(wordId);
            studiedCount = session.getStudiedCount();
            totalCount = session.getTotalCount();
            sessionCompleted = session.isCompleted();
        }

        return new ReviewResponse(result.nextReviewAt(), studiedCount, totalCount, sessionCompleted);
    }

    // --- Private Helpers ---

    private StudySession createNewSession(Member member, Long memberId) {
        StudySession session = new StudySession(member, StudySessionType.WORD);

        List<MemberWordProgress> unreviewedProgress = progressRepository.findUnreviewedByMember(member);
        int remaining = NEW_CARDS_PER_SESSION - unreviewedProgress.size();

        for (MemberWordProgress p : unreviewedProgress) {
            session.addItem(p.getWord().getId());
        }

        if (remaining > 0) {
            LocalDateTime now = LocalDateTime.now();
            List<Word> freshWords = wordRepository.findNewWordForMember(memberId, PageRequest.of(0, remaining));

            for (Word word : freshWords) {
                progressRepository.save(new MemberWordProgress(member, word, now));
                session.addItem(word.getId());
            }
        }

        if (session.getTotalCount() == 0) {
            return null;
        }

        return sessionRepository.save(session);
    }

    private List<WordCard> getReviewCards(Member member, String lang) {
        return progressRepository.findDueForReview(member, LocalDateTime.now())
                .stream()
                .map(p -> {
                    IntervalPreview preview = fsrsScheduler.previewIntervals(
                            p.getStability(), p.getDifficulty(), p.getLastReviewedAt(),
                            wordIntrinsicDifficulty(p.getWord())
                    );
                    return new WordCard(p.getWord(), lang, preview);
                })
                .toList();
    }

    private List<WordCard> getSessionNewCards(StudySession session, String lang) {
        List<Long> unstudiedIds = session.getUnstudiedItemIds();
        if (unstudiedIds.isEmpty()) {
            return List.of();
        }

        List<Word> wordList = wordRepository.findAllById(unstudiedIds);
        Map<Long, Word> wordMap = wordList.stream()
                .collect(Collectors.toMap(Word::getId, Function.identity()));

        List<WordCard> cards = new ArrayList<>();
        for (Long id : unstudiedIds) {
            Word word = wordMap.get(id);
            if (word != null) {
                IntervalPreview preview = fsrsScheduler.previewIntervals(
                        0.0, 0.0, null, wordIntrinsicDifficulty(word)
                );
                cards.add(new WordCard(word, lang, preview));
            }
        }
        return cards;
    }

    private double wordIntrinsicDifficulty(Word word) {
        return switch (word.getLevel()) {
            case 5 -> 3.0;
            case 4 -> 4.5;
            case 3 -> 6.0;
            case 2 -> 7.5;
            case 1 -> 9.0;
            case 0 -> 5.5;
            default -> 5.0;
        };
    }

    private MemberWordProgress getOrCreateProgress(Long memberId, Long wordId) {
        Member member = memberRepository.getReferenceById(memberId);
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORD_NOT_FOUND));

        return progressRepository.findByMemberAndWord(member, word)
                .orElseGet(() -> progressRepository.save(
                        new MemberWordProgress(member, word, LocalDateTime.now())
                ));
    }
}
