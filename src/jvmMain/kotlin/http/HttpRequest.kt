package http

import cn.hutool.core.text.UnicodeUtil
import okhttp3.*
import java.net.URLDecoder
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * 请求http
 * @author luoyangwei by 2022-08-10 13:18 created
 *
 */
class HttpRequest {

    fun initHttp(sessionStr: String): OkHttpClient {
        return OkHttpClient().newBuilder().readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .sslSocketFactory(initSSLSocketFactory(), initTrustManager())
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                    TODO("Not yet implemented")
                }

                override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                    val cookies = mutableListOf<Cookie>()
                    cookies.add(
                        Cookie.Builder().name("PHPSESSID")
                            .value(sessionStr)
                            .hostOnlyDomain("gm.wendaosoft.com")
                            .domain("gm.wendaosoft.com").build()
                    )
                    return cookies;
                }
            })
            .build()
    }

    private fun initSSLSocketFactory(): SSLSocketFactory {
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
            val xTrustArray = arrayOf(initTrustManager())
            sslContext.init(
                null,
                xTrustArray, SecureRandom()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return sslContext!!.socketFactory
    }

    private fun initTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }
        }
    }

    fun simpleDealData(response: Response): String = StringBuilder().apply {
        print("\n")
        println("header")
        print("\n")
        println(response.headers())
        println("body")
        print("\n")
        println("responseCode: ${response.code()}")
        val string = response.body()?.string() ?: ""
        println(
            "content: ${
                decode(string.let { s: String ->
                    //对获取到的数据 简单做一下格式化
                    s.split(",").joinToString("\n\t")
                })
            }"
        )

        append(decode(string))
    }.toString()

    //unicode ->String
    private fun decode(encodeText: String): String {
        return UnicodeUtil.toString(encodeText)
    }
}