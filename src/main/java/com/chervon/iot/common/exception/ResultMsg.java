package com.chervon.iot.common.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Mike Xu.
 * @Date: Created in 20:58 2017/6/26
 * @Description:
 * @Modified By:
 */
public class ResultMsg {
    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    private List<Error> errors;

    public ResultMsg( List<Error> errors){
        this.errors = errors;
    }

    public static class Error{
        private int status;
        private String title;
        private String message;
        private Map<String,?> source;

        public Error(int status, String title, String message, Map<String, ?> source) {
            this.status = status;
            this.title = title;
            this.message = message;
            this.source = source;
        }
        public Error(int status, String title, String message) {
            this.status = status;
            this.title = title;
            this.message = message;

            Map<String,String> source = new HashMap<String,String>();
            source.put("pointer","");
            this.source = source;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, ?> getSource() {
            return source;
        }

        public void setSource(Map<String, ?> source) {
            this.source = source;
        }
    }
}
