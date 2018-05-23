package com.github.xiaoooyu.gradle.teambition

import groovy.json.JsonOutput

/**
 * teambition {
 *    server = "https://www.teambition.com/api"
 *    account_server = "https://account.teambition.com"
 *    striker_server = "https://striker.teambition.net"
 *
 *    client_id = "${teambition.client_id}"
 *    client_secret = "${teambition.client_secret}"
 *
 *    username = "${teambition.email}"
 *    password = "${teambition.password}"
 * }
 */
class TeambitionExtension {
    def server = "https://www.teambition.com/api"
    def account_server = "https://account.teambition.com"
    def striker_server = "https://striker.teambition.net"

    def client_id
    def client_secret

    def username
    def password

    def accessToken
    def strikerAuth

    @Override
    String toString() {
        return JsonOutput.toJson(this)
    }
}
