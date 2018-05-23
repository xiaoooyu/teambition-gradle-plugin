package com.github.xiaoooyu.gradle.teambition

import com.github.xiaoooyu.gradle.teambition.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class TeambitionPlugin
        implements Plugin<Project> {

    void apply(Project project) {
        def projectContext = project.getExtensions().create("teambition", TeambitionExtension)

        project.task('login', type: TbLoginTask) {
            group = 'Teambition'
            context = projectContext
        }

        project.task('init', type: TbInitTask) {
            group = 'Teambition'
            context = projectContext
        }.dependsOn('login')

        project.task('uploadWork', type: TbUploadWorkTask) {
            group = 'Teambition'
            context = projectContext
        }.dependsOn('init')
    }
}
