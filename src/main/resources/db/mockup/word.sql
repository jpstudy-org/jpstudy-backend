-- =================================================================
-- JPStudy Mock Data for PostgreSQL
-- =================================================================

-- 아래 INSERT 문을 실행하기 전, ID 시퀀스 값을 초기화하거나 충돌을 방지하기 위해
-- TRUNCATE TABLE kanji, word, word_kanjis RESTART IDENTITY CASCADE;
-- 와 같은 명령어를 먼저 실행하는 것을 권장합니다.


-- -----------------------------------------------------------------
-- 1. Kanji (한자) 데이터 추가
-- -----------------------------------------------------------------
-- ID는 자동으로 생성되므로, 순서대로 1, 2, 3, ... 으로 들어간다고 가정합니다.
INSERT INTO kanji (character, meaning, meaning_en, onyomi, kunyomi, stroke_count, radical, jlpt_level) VALUES
                                                                                                           ('日', '날, 해', 'Day, Sun, Japan', 'ニチ, ジツ', 'ひ, -び, -か', 4, '日', 5),
                                                                                                           ('本', '근본, 책', 'Book, Origin, Main', 'ホン', 'もと', 5, '木', 5),
                                                                                                           ('語', '말씀', 'Word, Language', 'ゴ', 'かた.る', 14, '言', 5),
                                                                                                           ('学', '배울', 'Study, Learning', 'ガク', 'まな.ぶ', 8, '子', 5),
                                                                                                           ('生', '날, 살', 'Life, Birth, Student', 'セイ, ショウ', 'い.きる, う.まれる', 5, '生', 5),
                                                                                                           ('会', '모일', 'Meet, Society', 'カイ, エ', 'あ.う', 6, '人', 4),
                                                                                                           ('社', '모일, 회사', 'Company, Shrine', 'シャ', 'やしろ', 7, '示', 4),
                                                                                                           ('員', '인원', 'Member, Employee', 'イン', '-', 10, '口', 3);


-- -----------------------------------------------------------------
-- 2. Word (단어) 데이터 추가
-- -----------------------------------------------------------------
-- ID는 자동으로 생성되므로, 순서대로 1, 2, 3, ... 으로 들어간다고 가정합니다.
-- [수정] reading 컬럼 추가
INSERT INTO word (term, reading, meaning, meaning_en) VALUES
                                                          ('日本', 'にほん', '일본', 'Japan'),
                                                          ('日本語', 'にほんご', '일본어', 'Japanese language'),
                                                          ('学生', 'がくせい', '학생', 'Student'),
                                                          ('会社', 'かいしゃ', '회사', 'Company'),
                                                          ('社員', 'しゃいん', '회사원', 'Employee'),
                                                          ('社会', 'しゃかい', '사회', 'Society'),
                                                          ('先生', 'せんせい', '선생님', 'Teacher');


-- -----------------------------------------------------------------
-- 3. word_kanjis (연결 테이블) 데이터 추가
-- -----------------------------------------------------------------
-- 위에서 추가한 Kanji와 Word의 ID를 기반으로 다대다 관계를 설정합니다.
-- ID 값을 실제 데이터베이스에 맞게 확인해야 할 수 있습니다.

-- '日本' (word_id: 1) = '日' (kanji_id: 1) + '本' (kanji_id: 2)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (1, 1), (1, 2);

-- '日本語' (word_id: 2) = '日' (kanji_id: 1) + '本' (kanji_id: 2) + '語' (kanji_id: 3)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (2, 1), (2, 2), (2, 3);

-- '学生' (word_id: 3) = '学' (kanji_id: 4) + '生' (kanji_id: 5)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (3, 4), (3, 5);

-- '会社' (word_id: 4) = '会' (kanji_id: 6) + '社' (kanji_id: 7)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (4, 6), (4, 7);

-- '社員' (word_id: 5) = '社' (kanji_id: 7) + '員' (kanji_id: 8)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (5, 7), (5, 8);

-- '社会' (word_id: 6) = '社' (kanji_id: 7) + '会' (kanji_id: 6)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (6, 7), (6, 6);

-- '先生' (word_id: 7) = '生' (kanji_id: 5)
-- ('先' 한자는 위에서 추가하지 않았으므로 '生'만 연결합니다. 필요 시 '先' 한자를 추가해주세요.)
INSERT INTO word_kanjis (words_id, kanjis_id) VALUES (7, 5);