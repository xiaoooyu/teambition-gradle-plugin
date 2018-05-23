package com.github.xiaoooyu.gradle.teambition.tasks

import com.github.xiaoooyu.gradle.teambition.TeambitionExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TbBaseTask extends DefaultTask {

    public TbBaseTask() {}

    static final String AUTH_HEADER = "Authorization"
    static final String STRIKER_AUTH_HEADER = "Authorization"
    static final String ENCODING_UTF8 = "utf-8"

    TeambitionExtension context

    @TaskAction
    def TbAction() {

    }
}
