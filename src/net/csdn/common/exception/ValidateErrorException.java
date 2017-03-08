package net.csdn.common.exception;

import net.csdn.validate.ValidateResult;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZHA
 * Date: 13-3-1
 * Time: PM3:15
 * To change this template use File | Settings | File Templates.
 */
public class ValidateErrorException extends RuntimeException {
    protected List<ValidateResult> results;
    public ValidateErrorException(List<ValidateResult> results) {
        super("validate error");
        this.results = results;
    }

    public List<ValidateResult> getResults() {
        return results;
    }
}
