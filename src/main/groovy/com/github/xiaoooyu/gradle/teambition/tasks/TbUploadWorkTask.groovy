package com.github.xiaoooyu.gradle.teambition.tasks

import groovy.json.*
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.*

class TbUploadWorkTask extends TbBaseTask {

    def from
    def projectId
    def folderId

    Set<File> files = new HashSet<>()

    public TbUploadWorkTask ( ) { }

    @TaskAction
    def TbAction() {
        if (from instanceof File) {
            files.add(from)
        } else if (from instanceof FileCollection) {
            files.addAll(from.getFiles())
        }

        files.each { file ->
            def fileResult = uploadFileToStriker(file)
            uploadWork(fileResult)
        }
    }

    def uploadWork(fileParam) {
        def path = context.server + "/works"
        def accessToken = context.accessToken

        def param = [:]
        param._projectId = projectId
        param._parentId = folderId
        param.works = [fileParam]
        param.visible = "members"

        try {

            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection()

            httpURLConnection.setDoOutput(true)
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.addRequestProperty("Content-Type", "application/json")

            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", accessToken))

            def postBody = JsonOutput.toJson(param)
            logger.info postBody

            OutputStream output = httpURLConnection.getOutputStream()
            InputStream inputBody = new ByteArrayInputStream(postBody.getBytes("utf-8"))

            byte[] buff = new byte[1024]
            int read = -1
            while ((read = inputBody.read(buff)) != -1) {
                output.write(buff, 0, read)
            }

            int responseCode = httpURLConnection.getResponseCode()
            logger.info "add work result: " + responseCode

            InputStream input
            if (responseCode / 100 == 2) {
                input = httpURLConnection.getInputStream()
            } else {
                input = httpURLConnection.getErrorStream()
            }

            logger.info input.getText("utf-8")

        } catch (IOException ex) {
            logger.info ex.getLocalizedMessage()
        } finally {

        }
    }

    static final String crlf = "\r\n"
    static final String twoHyphens = "--"
    static final String boundarySeed =  "*****"
    static final String boundary = twoHyphens + boundarySeed + crlf

    def uploadFileToStriker(final File file) {
        def path = context.striker_server + "/upload"
        def strikerAuth = context.strikerAuth
        def result
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection()

            httpURLConnection.setUseCaches(false)
            httpURLConnection.setDoOutput(true)
            httpURLConnection.setRequestMethod('POST')
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive")
            httpURLConnection.setRequestProperty("Cache-Control", "no-cache")
            httpURLConnection.setRequestProperty("Content-Type", String.format("multipart/form-data; boundary=%s", boundarySeed))

            httpURLConnection.setRequestProperty(STRIKER_AUTH_HEADER, strikerAuth)

            OutputStream output = httpURLConnection.getOutputStream()
            DataOutputStream writer = new DataOutputStream(output)

            String mimeType = guessMimeType(file)

            attachFileParameter(writer, file.length(), mimeType)

            writer.writeBytes(boundary)
            writer.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s", "attach", file.getName(), crlf))
            writer.writeBytes(String.format("Content-Type: %s%s", mimeType, crlf))
            writer.writeBytes(crlf)

            FileInputStream fileInputStream = new FileInputStream(file)
            int read = 0
            byte[] buff = new byte[1024]
            while((read = fileInputStream.read(buff)) > 0) {
                writer.write(buff, 0, read)
                writer.flush()
            }
            writer.writeBytes(crlf)
            writer.writeBytes(boundary)

            fileInputStream.close()
            writer.close()


            int responseCode = httpURLConnection.getResponseCode()
            logger.info "response-code: " + responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = new JsonSlurper().parse(httpURLConnection.getInputStream())
                logger.info result.toString()
            } else {
                logger.error = httpURLConnection.getErrorStream().getText("utf-8")
            }
        } catch (IOException ex) {
            logger.error ex.getLocalizedMessage()
        }

        return result
    }

    private void attachFileParameter (final DataOutputStream writer, long len, String mimeType) throws IOException {
        writer.writeBytes(boundary)
        writer.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"%s", "type", crlf))
        writer.writeBytes(crlf)
        writer.writeBytes(mimeType)
        writer.writeBytes(crlf)
        writer.flush()

        String sizeString = String.format("%d", len)

        writer.writeBytes(boundary)
        writer.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"%s", "size", crlf))
        writer.writeBytes(String.format("Content-Length: %s%s", sizeString.length(), crlf))
        writer.writeBytes(crlf)
        writer.writeBytes(sizeString)
        writer.writeBytes(crlf)
        writer.flush()
    }

    static String guessMimeType(final File file) {
        InputStream is = new BufferedInputStream(new FileInputStream(file))
        String mimeType = URLConnection.guessContentTypeFromStream(is)
        is.close()

        if (mimeType == null) {
            mimeType = "application/octet-stream"
        }

        return mimeType
    }
}