/**
 *
 * @author luoyangwei by 2022-08-10 18:36 created
 *
 */
object StateManager {

    @Volatile
    private var CHECKING_STATUS: Boolean = false;

    // 预约状态
    @Volatile
    private var RESERVATION_STATUS: StateActive = StateActive.START
    private var FAIL_REASON: String = ""

    // 预约成功
    fun reserved() {
        synchronized(updateReservationStatus(StateActive.SUCCESS)) {}
    }

    fun failed() {
        synchronized(updateReservationStatus(StateActive.FAIL)) {}
    }

    fun setReason(reason: String) {
        FAIL_REASON = reason
    }

    fun getStatus(): StateActive {
        return RESERVATION_STATUS
    }

    fun setCheckingStatus(status: Boolean) {
        CHECKING_STATUS = status
    }

    fun getCheckingStatus(): Boolean {
        return CHECKING_STATUS && (StateActive.SUCCESS == getStatus() || StateActive.FAIL == getStatus())
    }

    fun getMessage(): String {
        return FAIL_REASON
    }

    fun updateReservationStatus(state: StateActive) {
        RESERVATION_STATUS = state
    }


}