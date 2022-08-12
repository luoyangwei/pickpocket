package data

/**
 * 用户输入数据
 * @author luoyangwei by 2022-08-10 15:02 created
 *
 */
data class UserInputData(
    val session: String,
    val classTableId: String,
    val startTime: String,
    var cardId: String = "635314"
)
