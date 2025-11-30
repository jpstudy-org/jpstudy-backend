package orinnetwork.jpstudy.application.progress.kanji;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.progress.common.component.FsrsScheduler;
import orinnetwork.jpstudy.application.progress.kanji.dto.KanjiCard;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewDifficulty;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewResult;
import orinnetwork.jpstudy.application.progress.kanji.dto.StudySessionResponse;
import orinnetwork.jpstudy.domain.kanji.Kanji;
import orinnetwork.jpstudy.domain.kanji.KanjiRepository;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
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
    private final FsrsScheduler fsrsScheduler;

    private static final int NEW_CARDS_PER_SESSION = 30;

    public StudySessionResponse getStudySession(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        String lang = member.getLanguagePreference();

        List<KanjiCard> reviewCards = getReviewCards(member, lang);
        List<KanjiCard> newCards = getNewCards(memberId, lang);

        return new StudySessionResponse(reviewCards, newCards);
    }

    @Transactional
    public void updateProgress(Long memberId, Long kanjiId, ReviewDifficulty difficulty) {
        MemberKanjiProgress progress = getOrCreateProgress(memberId, kanjiId);

        ReviewResult result = fsrsScheduler.calculateNextReview(
                progress,
                progress.getKanji(),
                difficulty
        );

        progress.updateFsrs(
                result.newStability(),
                result.newDifficulty(),
                result.reviewedAt(),
                result.nextReviewAt()
        );
    }

    // --- Private Helpers ---

    private List<KanjiCard> getReviewCards(Member member, String lang) {
        return progressRepository.findDueForReview(member, LocalDateTime.now())
                .stream()
                .map(p -> new KanjiCard(p.getKanji(), lang))
                .toList();
    }

    private List<KanjiCard> getNewCards(Long memberId, String lang) {
        return kanjiRepository.findNewKanjiForMember(memberId, PageRequest.of(0, NEW_CARDS_PER_SESSION))
                .stream()
                .map(k -> new KanjiCard(k, lang))
                .toList();
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
