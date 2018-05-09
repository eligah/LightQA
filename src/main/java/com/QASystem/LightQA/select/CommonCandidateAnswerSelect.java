package com.QASystem.LightQA.select;

import com.QASystem.LightQA.datasource.DataSource;
import com.QASystem.LightQA.datasource.FileDataSource;
import com.QASystem.LightQA.files.FilesConfig;
import com.QASystem.LightQA.model.*;
import com.QASystem.LightQA.parser.WordParser;
import com.QASystem.LightQA.select.CandidateAnswerSelect;
import org.apdplat.word.recognition.PersonName;
import org.apdplat.word.segmentation.PartOfSpeech;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.WeakHashMap;

public class CommonCandidateAnswerSelect implements CandidateAnswerSelect{
    private static final Logger LOG = LoggerFactory.getLogger(CommonCandidateAnswerSelect.class);

    @Override
    public void select(Question question, Evidence evidence) {
        CandidateAnswerCollection candidateAnswerCollection = new CandidateAnswerCollection();

        List<Word> words = WordParser.parse(evidence.getTitle() + evidence.getSnippet());
        for (Word word : words) {
            if (word.getText().length() < 2) {
                LOG.debug("Ignore the word( length < 2 )：" + word);
                continue;
            }
            if (word.getPartOfSpeech().getPos().toLowerCase().startsWith(question.getQuestionType().getPos().toLowerCase())) {
                CandidateAnswer answer = new CandidateAnswer();
                answer.setAnswer(word.getText());
                candidateAnswerCollection.addAnswer(answer);
                LOG.debug("Become candidate answer: " + word);
            }

            // Deal with PERSON_NAME type
            else if (question.getQuestionType().getPos().equals("nr") && word.getPartOfSpeech() == PartOfSpeech.I) {
                if (PersonName.is(word.getText())) {
                    CandidateAnswer answer = new CandidateAnswer();
                    answer.setAnswer(word.getText());
                    candidateAnswerCollection.addAnswer(answer);
                    LOG.debug("Become candidate answer: " + word);
                }
            }
        }
        evidence.setCandidateAnswerCollection(candidateAnswerCollection);
    }

    public static void main(String[] args) {
        DataSource dataSource = new FileDataSource(FilesConfig.personNameMaterial);
        List<Question> questions = dataSource.getQuestions();

        CommonCandidateAnswerSelect commonCandidateAnswerSelect = new CommonCandidateAnswerSelect();
        int i = 1;
        for (Question question : questions) {
            LOG.info("Question " + (i++) + ": " + question.getQuestion());
            int j = 1;
            for (Evidence evidence : question.getEvidences()) {
                LOG.info("	Evidence " + j + ": ");
                LOG.info("	Title: " + evidence.getTitle());
                LOG.info("	Snippet: " + evidence.getSnippet());
                LOG.info("	Evidence " + j + " 候选答案: ");
                commonCandidateAnswerSelect.select(question, evidence);
                for (CandidateAnswer candidateAnswer : evidence.getCandidateAnswerCollection().getAllCandidateAnswer()) {
                    LOG.info("			" + candidateAnswer.getAnswer() + " : " + candidateAnswer.getScore());
                }
                j++;
                LOG.info("------------------------------------------------");
            }
            LOG.info("------------------------------------------------");
            LOG.info("");
        }
    }
}
