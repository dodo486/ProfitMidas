package bulls.exception;

import bulls.hephaestus.HephaLogType;
import bulls.hephaestus.document.ServerMsgDoc;

public abstract class ExceptionForHephaNotice extends Exception {

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        ServerMsgDoc.now(HephaLogType.운영장애, getClass().getSimpleName(), getMessage()).fire();
    }

    public ExceptionForHephaNotice(String msg) {
        super(msg);
    }
}
