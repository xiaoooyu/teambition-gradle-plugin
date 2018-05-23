import com.github.xiaoooyu.gradle.teambition.tasks.TbBaseTask
import groovy.json.JsonSlurper
import org.gradle.api.tasks.TaskAction

class TbProjectMsgTask extends TbBaseTask {

    String projectId
    String message

    @TaskAction
    def TbAction() {
        if (!projectId?.trim() || !message?.trim()) {
            logger.error "projectId or message miss."
            return
        }

        sendMsg(getProjectRoom())
    }

    def getProjectRoom() {
        final String path = context.server + "/rooms/projects/" + projectId
        String roomId
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection()

            httpURLConnection.addRequestProperty("Content-Type", "application/json");


            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", context.accessToken))

            int responseCode = httpURLConnection.getResponseCode()
            logger.info "get project room result: ${responseCode}"

            if (responseCode == HttpURLConnection.HTTP_OK) {
                def result = new JsonSlurper().parse(httpURLConnection.getInputStream())
                roomId = result._id
            } else {
                logger.error httpURLConnection.getErrorStream().getText(ENCODING_UTF8)
            }

        } catch (IOException ex) {
            logger.error ex.getLocalizedMessage()
        }

        return roomId
    }

    def sendMsg(String roomId) {
        final String path = context.server + "/rooms/${roomId}/activities"

        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(path).openConnection()

            httpURLConnection.setDoOutput(true)
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.addRequestProperty("Content-Type", "application/json")

            httpURLConnection.setRequestProperty(AUTH_HEADER, String.format("OAuth2 %s", context.accessToken))

            String postBody = "{\"content\": \"${message}\"}"
            logger.info postBody

            OutputStream output = httpURLConnection.getOutputStream()
            InputStream inputBody = new ByteArrayInputStream(postBody.getBytes(ENCODING_UTF8))

            byte[] buff = new byte[1024]
            int read = -1
            while ((read = inputBody.read(buff)) != -1) {
                output.write(buff, 0, read)
            }

            int responseCode = httpURLConnection.getResponseCode()
            logger.info "sendMsg result: ${responseCode}"

            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info httpURLConnection.getInputStream().getText(ENCODING_UTF8)
            } else {
                logger.error httpURLConnection.getErrorStream().getText(ENCODING_UTF8)
            }
        } catch (IOException ex) {
            logger.error ex.getLocalizedMessage()
        }
    }
}
