package com.github.xiaoooyu.gradle.teambition
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
    def server
    def account_server
    def striker_server
    def client_id
    def client_secret
    def username
    def password

    def accessToken
    def strikerAuth
}
