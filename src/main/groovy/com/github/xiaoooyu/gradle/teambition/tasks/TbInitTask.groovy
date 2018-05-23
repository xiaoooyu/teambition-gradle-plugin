package com.github.xiaoooyu.gradle.teambition.tasks

import groovy.json.JsonSlurper
import org.gradle.api.tasks.TaskAction

class TbInitTask extends TbBaseTask {

    @TaskAction
    def TbAction() {
        if (context.strikerAuth?.trim()) {
            logger.info "Already has strike_token."
            return
        }
        getMe()
    }

    def getMe() {
        def urlString = context.server + '/users/me'
        def accessToken = context.accessToken

        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlString).openConnection()

            httpURLConnection.setRequestMethod("GET")
            httpURLConnection.addRequestProperty("Content-Type", "application/json")
            httpURLConnection.setRequestProperty(TbBaseTask.AUTH_HEADER, String.format("OAuth2 %s", accessToken))

            int responseCode = httpURLConnection.getResponseCode()
            logger.info "init result: ${responseCode}"

            if (responseCode / 100 == 2) {
                def result = new JsonSlurper().parse(httpURLConnection.getInputStream())
                logger.info result.toString()

                context.strikerAuth = result.strikerAuth
            } else {
                logger.error httpURLConnection.getErrorStream().getText("utf-8")
            }
        } catch (IOException ex) {
            logger.error ex.getLocalizedMessage()
        }
    }
}