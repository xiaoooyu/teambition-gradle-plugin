package com.github.xiaoooyu.gradle.teambition

import com.github.xiaoooyu.gradle.teambition.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class TeambitionPlugin
        implements Plugin<Project> {

    void apply(Project project) {
        def projectContext = project.getExtensions().create("teambition", TeambitionExtension)

        project.getTasks().withType(TbBaseTask.class) {
            group = 'teambition'
            context = projectContext
        }

        project.task('tbLogin', type: TbLoginTask) {
            group = 'Teambition'
            context = projectContext
        }

        project.task('tbInit', type: TbInitTask) {
            group = 'Teambition'
            context = projectContext
        }.dependsOn('tbLogin')

        project.getTasks().withType(TbFileTask.class) {
            group = 'teambition'
            context = projectContext
        }.each{task -> task.dependsOn('tbInit')}
    }
}
