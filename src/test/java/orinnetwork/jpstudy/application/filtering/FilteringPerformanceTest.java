package orinnetwork.jpstudy.application.filtering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.junit.jupiter.api.Test;

public class FilteringPerformanceTest {

    @Test
    void comparePerformance() {
        // 데이터 (금칙어 10_000자, 본문 50_000자)
        int wordCount = 10000;
        List<String> bannedWords = new ArrayList<>();
        for (int i = 0; i < wordCount; i++) {
            bannedWords.add("badword" + i);
        }
        // Matching Test Word
        bannedWords.add("shiba");

        // 본문 생성
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            sb.append("This is a clean sentence without any issues. ");
        }
        sb.append("This sentence contains shiba in the middle. ");
        String content = sb.toString();

        System.out.println("=== [테스트 환경] ===");
        System.out.println("금칙어 개수: " + wordCount + "개");
        System.out.println("본문 길이: " + content.length() + "자");
        System.out.println("====================\n");



        // 1. String.contains 반복문 측정
        long startBasic = System.nanoTime();

        boolean foundBasic = false;
        for (String word : bannedWords) {
            if (content.contains(word)) {
                // 전체 스캔 성능 비교
                foundBasic = true;
            }
        }
        System.out.printf(foundBasic + ": finish");

        long endBasic = System.nanoTime();
        double basicTime = (endBasic - startBasic) / 1_000_000.0;

        System.out.println("1. [String.contains 반복] 소요 시간: " + String.format("%.4f", basicTime) + " ms");


        // 2. 아호코라식 알고리즘 사용 측정
        AhoCorasick ac = new AhoCorasick();
        for (String word : bannedWords) {
            ac.insert(word);
        }
        ac.buildFailureLinks();

        long startAho = System.nanoTime();

        boolean foundAho = ac.search(content);
        System.out.printf(foundAho + ": finish");

        long endAho = System.nanoTime();
        double ahoTime = (endAho - startAho) / 1_000_000.0;

        System.out.println("2. [Aho-Corasick 알고리즘] 소요 시간: " + String.format("%.4f", ahoTime) + " ms");

        // 결과 비교
        System.out.println("\n>> 성능 차이: 약 " + String.format("%.2f", basicTime / ahoTime) + " 배 더 빠름");
    }



    // 아호코라식 로직
    static class AhoCorasick {
        private final TrieNode root = new TrieNode();

        static class TrieNode {
            Map<Character, TrieNode> children = new HashMap<>();
            TrieNode failureLink = null;
            boolean isEndOfWord = false;
        }

        public void insert(String keyword) {
            TrieNode current = root;
            for (char c : keyword.toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new TrieNode());
            }
            current.isEndOfWord = true;
        }

        public void buildFailureLinks() {
            Queue<TrieNode> queue = new LinkedList<>();
            root.failureLink = root;

            for (TrieNode child : root.children.values()) {
                child.failureLink = root;
                queue.add(child);
            }

            while (!queue.isEmpty()) {
                TrieNode current = queue.poll();

                for (Map.Entry<Character, TrieNode> entry : current.children.entrySet()) {
                    char c = entry.getKey();
                    TrieNode child = entry.getValue();
                    TrieNode fallback = current.failureLink;

                    while (fallback != root && !fallback.children.containsKey(c)) {
                        fallback = fallback.failureLink;
                    }

                    child.failureLink = fallback.children.getOrDefault(c, root);

                    if (child.failureLink.isEndOfWord) {
                        child.isEndOfWord = true; // 실패 링크가 단어 끝이면 현재도 단어 끝으로 취급
                    }
                    queue.add(child);
                }
            }
        }

        public boolean search(String text) {
            TrieNode current = root;
            for (char c : text.toCharArray()) {
                while (current != root && !current.children.containsKey(c)) {
                    current = current.failureLink;
                }
                if (current.children.containsKey(c)) {
                    current = current.children.get(c);
                }
                if (current.isEndOfWord) {
                    return true;
                }
            }
            return false;
        }
    }
}
