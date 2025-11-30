package orinnetwork.jpstudy.application.progress.word;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orinnetwork.jpstudy.application.progress.common.component.FsrsScheduler;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewDifficulty;
import orinnetwork.jpstudy.application.progress.common.dto.ReviewResult;
import orinnetwork.jpstudy.application.progress.word.dto.StudySessionResponse;
import orinnetwork.jpstudy.application.progress.word.dto.WordCard;
import orinnetwork.jpstudy.domain.member.Member;
import orinnetwork.jpstudy.domain.member.MemberRepository;
import orinnetwork.jpstudy.domain.progress.MemberWordProgress;
import orinnetwork.jpstudy.domain.progress.MemberWordProgressRepository;
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
    private final FsrsScheduler fsrsScheduler;

    private static final int NEW_CARDS_PER_SESSION = 30;

    public StudySessionResponse getStudySession(Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);
        String lang = member.getLanguagePreference();

        List<WordCard> reviewCards = getReviewCards(member, lang);
        List<WordCard> newCards = getNewCards(memberId, lang);

        return new StudySessionResponse(reviewCards, newCards);
    }

    @Transactional
    public void updateProgress(Long memberId, Long wordId, ReviewDifficulty difficulty) {
        MemberWordProgress progress = getOrCreateProgress(memberId, wordId);

        ReviewResult result = fsrsScheduler.calculateNextReview(
                progress,
                progress.getWord(),
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

    private List<WordCard> getReviewCards(Member member, String lang) {
        return progressRepository.findDueForReview(member, LocalDateTime.now())
                .stream()
                .map(p -> new WordCard(p.getWord(), lang))
                .toList();
    }

    private List<WordCard> getNewCards(Long memberId, String lang) {
        return wordRepository.findNewWordForMember(memberId, PageRequest.of(0, NEW_CARDS_PER_SESSION))
                .stream()
                .map(w -> new WordCard(w, lang))
                .toList();
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
