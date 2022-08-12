import cn.hutool.core.date.DateTime
import data.UserInputData
import http.HttpRequest
import http.Result
import http.Url
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import kotlin.math.abs

/**
 * 线程池操作
 *
 * @author luoyangwei by 2022-08-10 14:54 created
 *
 */
class ExecutorsPool {
    private var stater: StateManager = StateManager
    private var contentType = "application/x-www-form-urlencoded"
    private val successCode: Int = 200

    /**
     * 开始线程池
     */
    fun start(userInputData: UserInputData) {

        // 检查是否到时间，没有到时见就一直循环等待
        while (!checkStartOpportunity(userInputData.startTime) && !stater.getCheckingStatus()) Unit
        if (stater.getCheckingStatus()) {
            return
        }

        println("ready..")

        // 检查是否可以预约
        val checkResult = checkStep(userInputData)
        if (checkResult.resultCode == successCode) {
            val repeatCount = 1000;

            repeat(repeatCount) {
                if (stater.getCheckingStatus()) {
                    return@repeat
                }
                val doAddBookResult = doAddBook(userInputData)
                if (doAddBookResult.resultCode == successCode) {
                    stater.reserved()
                    println("reserved")
                    return@repeat
                } else {
                    println("failed")
                }
            }

        } else {
            stater.setReason("意料之外的预约失败 ${checkResult.resultMessage}")
            stater.failed()
        }
    }

    /**
     * 停止线程池
     */
    fun stop() {
        if (!stater.getCheckingStatus()) {
            stater.setCheckingStatus(true)
            stater.updateReservationStatus(StateActive.START)
        }
    }

    private fun checkStartOpportunity(time: String): Boolean {
        val nd = DateFormat.toTimestamp(DateFormat.toDateByHour(time))
        val ts = DateFormat.getNowDateTimeStamp()
        val diff = abs(ts - nd)
        val boolean = diff <= 30 || ts > nd
        println("ts: $ts, nd: $nd, diff: $diff, b: $boolean")
        return boolean
    }


    fun getState(): StateActive {
        return stater.getStatus()
    }

    fun setState(stateActive: StateActive) {
        stater.updateReservationStatus(stateActive)
    }

    fun getStateMessage(): String {
        return stater.getMessage()
    }

    /**
     * 请求第一个步骤
     */
    private fun checkStep(userInputData: UserInputData): Result {
        val response = HttpRequest().initHttp(userInputData.session).newCall(
            Request.Builder().url(Url.CHECK_RULES.str).post(
                RequestBody.create(
                    MediaType.get(contentType),
                    "cardid=" + userInputData.cardId + "&classtableid=" + userInputData.classTableId
                )
            ).build()
        ).execute()
        val string = HttpRequest().simpleDealData(response)
        return if (string == "\"OK\"") {
            Result(200, "请求成功")
        } else {

            // 可能存在已经约过，或者课程还不在可预约返回内，只要不是html内容，就继续
            if (isHtml(string)) {
                Result(999, string)
            } else {
                Result(200, string)
            }
        }
    }

    /**
     * 请求第二个步骤
     */
    private fun doAddBook(userInputData: UserInputData): Result {
        val response = HttpRequest().initHttp(userInputData.session).newCall(
            Request.Builder().url(Url.DO_ADD_BOOK.str).post(
                RequestBody.create(
                    MediaType.get(contentType),
                    "cardid=" + userInputData.cardId + "&classtableid=" + userInputData.classTableId
                )
            ).build()
        ).execute()
        val string = HttpRequest().simpleDealData(response)
        println("执行结果: $string")
        return Result(if (isInteger(string)) 200 else 999, string)
    }

    /**
     * 验证内容是否是字符串
     */
    private fun isInteger(s: String): Boolean {
        return try {
            s.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isHtml(s: String): Boolean {
        return s.contains("<!DOCTYPE html>")
    }

}