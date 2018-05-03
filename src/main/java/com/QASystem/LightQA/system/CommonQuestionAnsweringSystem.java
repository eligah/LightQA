package com.QASystem.LightQA.system;

import com.QASystem.LightQA.system.QuestionAnsweringSystemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonQuestionAnsweringSystem extends QuestionAnsweringSystemImpl {

    private static final Logger LOG = LoggerFactory.getLogger(CommonQuestionAnsweringSystem.class);

    public CommonQuestionAnsweringSystem() {
        LOG.info("Start to build lightQA");
        //Todo commonQA

        LOG.info("LightQA has constructed.");
    }
}
