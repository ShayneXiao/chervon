package com.chervon.iot.common.exception;

/**
 * @Author: Mike Xu.
 * @Date: Created in 20:58 2017/6/26
 * @Description:
 * @Modified By:
            */
    public enum ResultStatusCode {
        SC_OK(200, "OK","SC_OK"),
        SC_NO_CONTENT(204,"SC_NO_CONTENT","SC_NO_CONTENT"),
        SC_BAD_REQUEST(422,"Field data error.","SC_BAD_REQUEST"),
        SC_PERMISSION_DENIED(401, "Unauthorized request or authorization failure.","SC_PERMISSION_DENIED"),
        SC_CONFLICT(409, "One or more fields conflict with existing data.","SC_CONFLICT"),
        SC_NOT_FOUND(404, "Object not found.","SC_NOT_FOUND"),
        SC_FORBIDDEN(403, "You cannot perform this action.","SC_FORBIDDEN"),
        INTERNAL_SERVER_ERROR(500, "Internal Server Error","INTERNAL_SERVER_ERROR");
        private  int errcode;
        private String title;
        private String errmsg;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }
        private ResultStatusCode(int Errode, String Title, String ErrMsg)
        {
            this.errcode = Errode;
            this.title = Title;
            this.errmsg = ErrMsg;
        }
}
