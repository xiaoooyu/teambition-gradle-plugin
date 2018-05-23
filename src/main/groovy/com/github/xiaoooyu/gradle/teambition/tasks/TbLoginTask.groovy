package com.github.xiaoooyu.gradle.teambition.tasks

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.tasks.TaskAction

import io.jsonwebtoken.Jwts

class TbLoginTask extends TbBaseTask{

    @TaskAction
    def TbAction() {
        if (context.accessToken?.trim()) {
            logger.info "Already has access_token."
            return
        }
        login()
    }

    def login() {
        def path = context.account_server + '/api/login/email'

        String client_id = context.client_id
        String client_secret = context.client_secret
        String account = context.username
        String password = context.password

        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection()

            httpURLConnection.setDoOutput(true)
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.addRequestProperty("Content-Type", "application/json")

            String jwt = Jwts.builder()
                    .claim("client_id", client_id)
                    .setExpiration(null)
                    .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, client_secret.getBytes("UTF-8"))
                    .compact()

            def params = [:]
            params.client_id = client_id
            params.client_secret = client_secret
            params.email = account
            params.password = password
            params.token = jwt
            params.response_type = "token"

            String postBody = JsonOutput.toJson(params)
            logger.info postBody

            OutputStream output = httpURLConnection.getOutputStream()
            InputStream inputBody = new ByteArrayInputStream(postBody.getBytes("utf-8"))

            byte[] buff = new byte[1024]
            int read = -1
            while ((read = inputBody.read(buff)) != -1) {
                output.write(buff, 0, read)
            }

            int responseCode = httpURLConnection.getResponseCode()
            logger.info "login result: ${responseCode}"

            if (responseCode / 100 == 2) {
                InputStream input = httpURLConnection.getInputStream()
                def result = new JsonSlurper().parse(input)
                logger.info result.toString()
                context.accessToken = result.access_token
            } else {
                logger.error new JsonSlurper().parse(httpURLConnection.getErrorStream()).toString()
            }
        } catch (IOException ex) {
            logger.error ex.getLocalizedMessage()
        }
    }
}
