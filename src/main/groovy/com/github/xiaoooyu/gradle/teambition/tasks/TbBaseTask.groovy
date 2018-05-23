package com.github.xiaoooyu.gradle.teambition.tasks

import com.github.xiaoooyu.gradle.teambition.TeambitionExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TbBaseTask extends DefaultTask {

    static final String AUTH_HEADER = "Authorization"
    static final String STRIKER_AUTH_HEADER = "Authorization"

    TeambitionExtension context

    @TaskAction
    def TbAction() {

    }
}
