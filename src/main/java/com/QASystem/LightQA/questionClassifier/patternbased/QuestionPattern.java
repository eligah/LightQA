package com.QASystem.LightQA.questionClassifier.patternbased;

public enum QuestionPattern {
    //1、直接和【问题】匹配，如：APDPlat的发起人是谁？
    Question,
    //2、和问题分词及词性标注之后的【词和词性序列】进行匹配，如：apdplat/en 的/uj 发起人/n 是/v 谁/RW.RWPersonSingle
    TermWithNatures,
    //3、和问题分词及词性标注之后的【词性序列】进行匹配，如：en/uj/n/v/RW.RWPersonSingle
    Natures,
    //4、和问题的【主谓宾 词 和 词性】进行匹配，如：发起人/n 是/v 谁/RW.RWPersonSingle
    MainPartPattern,
    //5、和问题的【主谓宾  词性】进行匹配，如：n/v/RW.RWPersonSingle
    MainPartNaturePattern
}
