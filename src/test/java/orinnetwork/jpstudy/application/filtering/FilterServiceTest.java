package orinnetwork.jpstudy.application.filtering;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import orinnetwork.jpstudy.domain.filtering.FilterLogRepository;
import orinnetwork.jpstudy.domain.filtering.KeywordType;
import orinnetwork.jpstudy.domain.filtering.engine.AhoCorasickEngine;
import orinnetwork.jpstudy.domain.post.Post;
import orinnetwork.jpstudy.domain.post.PostRepository;
import orinnetwork.jpstudy.domain.post.PostStatus;

@ExtendWith(MockitoExtension.class)
class FilterServiceTest {

    @InjectMocks
    private FilterService filterService;

    @Mock
    private AhoCorasickEngine engine;
    @Mock private PostRepository postRepository;
    @Mock private FilterLogRepository filterLogRepository;

    private Post createPost() {
        return Post.builder().title("제목").content("내용").postStatus(PostStatus.ACTIVE).build();
    }

    @Test
    @DisplayName("깨끗한 경우 ACTIVE 상태 / 로그를 남기지 않음")
    void cleanPostTest() {
        Post post = createPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(engine.search(any())).willReturn(Set.of());

        filterService.filterPost(1L);

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.ACTIVE);
        verify(filterLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("욕설의 경우 ACTIVE 상태 / 단, 로그는 저장")
    void profanityOnlyTest() {
        Post post = createPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(engine.search(any())).willReturn(Set.of(KeywordType.PROFANITY));

        filterService.filterPost(1L);

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.ACTIVE);
        verify(filterLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("모욕의 경우 REJECTED 상태와 로그 저장")
    void insultTest() {
        Post post = createPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(engine.search(any())).willReturn(Set.of(KeywordType.INSULT));

        filterService.filterPost(1L);

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.REJECTED);
        verify(filterLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("욕설과 모욕이 같이 있는 경우 강한 제재가 우선 순위 높음")
    void mixedKeywordsTest() {
        Post post = createPost();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(engine.search(any())).willReturn(Set.of(KeywordType.PROFANITY, KeywordType.INSULT));

        filterService.filterPost(1L);

        assertThat(post.getPostStatus()).isEqualTo(PostStatus.REJECTED);
        verify(filterLogRepository, times(2)).save(any());
    }
}