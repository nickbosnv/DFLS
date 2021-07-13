package dfls.sipservice;

public interface OnCallStateListener {

    /***
     * Exhaling
     */
    void calling();

    /***
     * Object ringing
     */
    void early();

    /***
     * connection succeeded
     */
    void connecting();

    /***
     * calling
     */
    void confirmed();

    /***
     * hang up
     */
    void disconnected();

    /***
     * Call failed
     */
    void error();

}
