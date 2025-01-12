package com.example.english_mcqbank.service;

import com.example.english_mcqbank.model.Exam;
import com.example.english_mcqbank.model.ExamTopic;
import com.example.english_mcqbank.model.Question;
import com.example.english_mcqbank.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExamService implements IExamService {
    @Autowired
    private IResultService resultService;
    @Autowired
    private IQuestionService questionService;
    @Autowired
    private ITopicService topicService;
    @Autowired
    private IExamTopicService examTopicService;

    @Autowired
    private ExamRepository examRepository;

    @Override
    @Transactional
    @CachePut(value = "exam", key = "'exam'+#exam.id")
    public void saveExam(Exam exam) {
        examRepository.save(exam);
    }

    @Override
    @Cacheable(value = "exam", key = "'exam'+#id")
    public Exam getExamById(int id) {
        return examRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteExam(Exam exam) {
        resultService.deleteAllByExam(exam);
        examRepository.delete(exam);
    }

    @Override
    public Map<Long, Integer> getExamTopicPercentageMap(Map<String, String> requestParams) {
        Map<Long, Integer> examTopicPercentageMap = new HashMap<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue();

            // Kiểm tra các paramName để xác định danh sách topic và numQu
            // Ví dụ: paramName có dạng "topic1", "numQu.OfTopic1", ...
            if (paramName.startsWith("topic")) {
                // Xử lý danh sách topic được chọn (paramValue chứa giá trị của topic.id)
                Long topicId = Long.parseLong(paramValue);
                // Tạo tên tham số numOfQu tương ứng
                String numOfQuParamName = "numQu.OfTopic" + topicId;
                // Lấy giá trị numOfQu từ requestParams dựa vào tên tham số numOfQu tương ứng
                String percentValue = requestParams.get(numOfQuParamName);
                // Chuyển đổi giá trị numOfQu từ String sang Integer (hoặc bạn có thể sử dụng parseInt)
                Integer percent = Integer.valueOf(percentValue);

                // Đưa cặp topicId và numOfQu vào Map
                examTopicPercentageMap.put(topicId, percent);
            }
        }
        return examTopicPercentageMap;
    }

    @Override
    public int calculateScore(Map<String, String> params) {
        int score = 0;
        for (String paramName : params.keySet()) {
            if (paramName.startsWith("question_")) {
                int questionId = Integer.parseInt(paramName.substring("question_".length()));
                Question question = questionService.get(questionId);
                //Question question = questionMap.get(questionId);
                String selectedOption = params.get(paramName);
                // Do something with the selected option for each question (e.g., save to database)
                if (selectedOption.equals(question.getCorrectAnswer())) {
                    score++;
                }
                //System.out.println("Question " + questionId + ": Selected Option: " + selectedOption);
            } else {
                //System.out.println(paramName);
            }

        }
        return score;
    }


    @Override
    @Cacheable(value = "examCache")
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    public void addExamTopic(Exam exam, Map<Long, Integer> examTopicPercentageMap) {
        for (Map.Entry<Long, Integer> entry : examTopicPercentageMap.entrySet()) {
            Long topicId = entry.getKey();
            Integer percent = entry.getValue();
            ExamTopic examTopic = new ExamTopic();
            examTopic.setTopic(topicService.getTopicById(topicId.intValue()));
            examTopic.setPercent(percent);
            exam.addExamTopic(examTopic);
            //System.out.println("topicId: " + topicId + ", numOfQu: " + numOfQu);
        }
    }

    @Override
    public int getTotalPercentage(Map<Long, Integer> examTopicPercentageMap) {
        int result = 0;
        for (Map.Entry<Long, Integer> entry : examTopicPercentageMap.entrySet()) {
            result += entry.getValue();
        }
        return result;
    }


    @Override
    public void updateExamTopic(Exam exam, Map<Long, Integer> examTopicPercentageMap) {
        examTopicService.deleteAllByExam(exam);
        //exam.clearExamTopic();
        addExamTopic(exam, examTopicPercentageMap);
        //examRepository.save(exam);
//        List<ExamTopic> examTopics = examTopicService.findAllByExam(exam);
//        for (ExamTopic examTopic : examTopics) {
//            System.out.println("examTopic: " + examTopic);
//        }
    }

    @Override
    public List<Exam> getAllExams(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return examRepository.findAll(pageable).getContent();
    }

    @Override
    public int countAllExams() {
        return (int) examRepository.count();
    }
}
