package orinnetwork.jpstudy.application.progress.kanji;

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
import orinnetwork.jpstudy.application.progress.kanji.dto.KanjiCard;
import orinnetwork.jpstudy.application.progress.kanji.dto.StudySessionResponse;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.kanji.KanjiRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.progress.MemberKanjiProgress;
import orinnetwork.jpstudy.domain.progress.MemberKanjiProgressRepository;
import orinnetwork.jpstudy.domain.progress.StudySession;
import orinnetwork.jpstudy.domain.progress.StudySessionRepository;
import orinnetwork.jpstudy.domain.progress.StudySessionType;
import orinnetwork.jpstudy.infrastructure.exception.CustomException;
import orinnetwork.jpstudy.infrastructure.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanjiProgressService {

    private final MemberRepository memberRepository;
    private final KanjiRepository kanjiRepository;
    private final MemberKanjiProgressRepository progressRepository;
    private final StudySessionRepository sessionRepository;
    private final FsrsScheduler fsrsScheduler;

    private static final int NEW_CARDS_PER_SESSION = 30;

    @Transactional
    public StudySessionResponse getStudySession(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        String lang = member.getLanguagePreference();

        List<KanjiCard> reviewCards = getReviewCards(member, lang);

        StudySession session = sessionRepository.findActiveSession(member, StudySessionType.KANJI)
                .orElseGet(() -> createNewSession(member, memberId));

        if (session == null) {
            return new StudySessionResponse(null, reviewCards, List.of(), 0, 0, true, false);
        }

        List<KanjiCard> newCards = getSessionNewCards(session, lang);
        boolean hasMore = kanjiRepository.countNewKanjiForMember(memberId) > 0;

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

        sessionRepository.findActiveSession(member, StudySessionType.KANJI)
                .ifPresent(StudySession::complete);

        StudySession session = createNewSession(member, memberId);
        List<KanjiCard> reviewCards = getReviewCards(member, lang);

        if (session == null) {
            return new StudySessionResponse(null, reviewCards, List.of(), 0, 0, true, false);
        }

        List<KanjiCard> newCards = getSessionNewCards(session, lang);
        boolean hasMore = kanjiRepository.countNewKanjiForMember(memberId) > 0;

        return new StudySessionResponse(
                session.getId(), reviewCards, newCards,
                session.getTotalCount(), session.getStudiedCount(),
                session.isCompleted(), hasMore
        );
    }

    @Transactional
    public ReviewResponse updateProgress(Long memberId, Long kanjiId, ReviewDifficulty difficulty) {
        MemberKanjiProgress progress = getOrCreateProgress(memberId, kanjiId);

        ReviewResult result = fsrsScheduler.calculateNextReview(
                progress, progress.getKanji(), difficulty
        );

        progress.updateFsrs(
                result.newStability(), result.newDifficulty(),
                result.reviewedAt(), result.nextReviewAt()
        );

        Member member = memberRepository.getReferenceById(memberId);
        StudySession session = sessionRepository.findActiveSession(member, StudySessionType.KANJI)
                .orElse(null);

        long studiedCount = 0;
        int totalCount = 0;
        boolean sessionCompleted = false;

        if (session != null) {
            session.markItemStudied(kanjiId);
            studiedCount = session.getStudiedCount();
            totalCount = session.getTotalCount();
            sessionCompleted = session.isCompleted();
        }

        return new ReviewResponse(result.nextReviewAt(), studiedCount, totalCount, sessionCompleted);
    }

    // --- Private Helpers ---

    private StudySession createNewSession(Member member, Long memberId) {
        StudySession session = new StudySession(member, StudySessionType.KANJI);

        List<MemberKanjiProgress> unreviewedProgress = progressRepository.findUnreviewedByMember(member);
        int remaining = NEW_CARDS_PER_SESSION - unreviewedProgress.size();

        for (MemberKanjiProgress p : unreviewedProgress) {
            session.addItem(p.getKanji().getId());
        }

        if (remaining > 0) {
            LocalDateTime now = LocalDateTime.now();
            List<Kanji> freshKanji = kanjiRepository.findNewKanjiForMember(memberId, PageRequest.of(0, remaining));

            for (Kanji kanji : freshKanji) {
                progressRepository.save(new MemberKanjiProgress(member, kanji, now));
                session.addItem(kanji.getId());
            }
        }

        if (session.getTotalCount() == 0) {
            return null;
        }

        return sessionRepository.save(session);
    }

    private List<KanjiCard> getReviewCards(Member member, String lang) {
        return progressRepository.findDueForReview(member, LocalDateTime.now())
                .stream()
                .map(p -> {
                    IntervalPreview preview = fsrsScheduler.previewIntervals(
                            p.getStability(), p.getDifficulty(), p.getLastReviewedAt(),
                            kanjiIntrinsicDifficulty(p.getKanji())
                    );
                    return new KanjiCard(p.getKanji(), lang, preview);
                })
                .toList();
    }

    private List<KanjiCard> getSessionNewCards(StudySession session, String lang) {
        List<Long> unstudiedIds = session.getUnstudiedItemIds();
        if (unstudiedIds.isEmpty()) {
            return List.of();
        }

        List<Kanji> kanjiList = kanjiRepository.findAllById(unstudiedIds);
        Map<Long, Kanji> kanjiMap = kanjiList.stream()
                .collect(Collectors.toMap(Kanji::getId, Function.identity()));

        List<KanjiCard> cards = new ArrayList<>();
        for (Long id : unstudiedIds) {
            Kanji kanji = kanjiMap.get(id);
            if (kanji != null) {
                IntervalPreview preview = fsrsScheduler.previewIntervals(
                        0.0, 0.0, null, kanjiIntrinsicDifficulty(kanji)
                );
                cards.add(new KanjiCard(kanji, lang, preview));
            }
        }
        return cards;
    }

    private double kanjiIntrinsicDifficulty(Kanji kanji) {
        return (100.0 - kanji.getLevel()) / 10.0 + 1.0;
    }

    private MemberKanjiProgress getOrCreateProgress(Long memberId, Long kanjiId) {
        Member member = memberRepository.getReferenceById(memberId);
        Kanji kanji = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new CustomException(ErrorCode.KANJI_NOT_FOUND));

        return progressRepository.findByMemberAndKanji(member, kanji)
                .orElseGet(() -> progressRepository.save(
                        new MemberKanjiProgress(member, kanji, LocalDateTime.now())));
    }
}
