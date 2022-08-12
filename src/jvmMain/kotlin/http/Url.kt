package http

/**
 * url
 * @author luoyangwei by 2022-08-10 13:19 created
 *
 */
enum class Url(val str: String) {

    /**
     * 查询卡号
     */
    CHECK_CARD("https://gm.wendaosoft.com/gm/weixin/classtable/check_cardtypecourse/92882"),

    /**
     * 检查卡号是否可以预约
     */
    CHECK_RULES("https://gm.wendaosoft.com/gm/weixin/classtable/check_rules/92882/454"),

    /**
     * 开始预约
     */
    DO_ADD_BOOK("https://gm.wendaosoft.com/gm/weixin/classtable/do_addbook/92882")


}