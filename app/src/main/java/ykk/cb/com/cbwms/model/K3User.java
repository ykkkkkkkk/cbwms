package ykk.cb.com.cbwms.model;

import java.util.List;

/**
 * 日期：2018-06-07 11:15
 * 描述：
 * 作者：ykk
 */
public class K3User {

        /**
         * ClientType : 1
         * UserName : 肖冰
         * DatabaseType : 2
         * DBid : 5b04e12288291d
         * DataCenterName : 0523测试版
         * LogLocale :
         * CustomName : CLOUD
         * UserId : 100020
         * SessionId : mwuvrmunxfafvpr3u3r3e2y5
         * DisplayVersion : 6.2.639.8
         * OrganizationID : 1
         * OrganizationName : 创明总部
         * organization_functionIds : ids
         * UserToken : 45b43878-1876-4a65-9a8e-0826267d1e05
         */

        private int ClientType;
        private String UserName;
        private int DatabaseType;
        private String DBid;
        private String DataCenterName;
        private String LogLocale;
        private String CustomName;
        private int UserId;
        private String SessionId;
        private String DisplayVersion;
        private int OrganizationID;
        private String OrganizationName;
        private List<Integer> organization_functionIds;
        private String UserToken;

        public int getClientType() {
            return ClientType;
        }

        public void setClientType(int ClientType) {
            this.ClientType = ClientType;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }

        public int getDatabaseType() {
            return DatabaseType;
        }

        public void setDatabaseType(int DatabaseType) {
            this.DatabaseType = DatabaseType;
        }

        public String getDBid() {
            return DBid;
        }

        public void setDBid(String DBid) {
            this.DBid = DBid;
        }

        public String getDataCenterName() {
            return DataCenterName;
        }

        public void setDataCenterName(String DataCenterName) {
            this.DataCenterName = DataCenterName;
        }

        public String getLogLocale() {
            return LogLocale;
        }

        public void setLogLocale(String LogLocale) {
            this.LogLocale = LogLocale;
        }

        public String getCustomName() {
            return CustomName;
        }

        public void setCustomName(String CustomName) {
            this.CustomName = CustomName;
        }

        public int getUserId() {
            return UserId;
        }

        public void setUserId(int UserId) {
            this.UserId = UserId;
        }

        public String getSessionId() {
            return SessionId;
        }

        public void setSessionId(String SessionId) {
            this.SessionId = SessionId;
        }

        public String getDisplayVersion() {
            return DisplayVersion;
        }

        public void setDisplayVersion(String DisplayVersion) {
            this.DisplayVersion = DisplayVersion;
        }

        public int getOrganizationID() {
            return OrganizationID;
        }

        public void setOrganizationID(int OrganizationID) {
            this.OrganizationID = OrganizationID;
        }

        public String getOrganizationName() {
            return OrganizationName;
        }

        public void setOrganizationName(String OrganizationName) {
            this.OrganizationName = OrganizationName;
        }

        public String getUserToken() {
            return UserToken;
        }

        public void setUserToken(String UserToken) {
            this.UserToken = UserToken;
        }

        public List<Integer> getOrganization_functionIds() {
            return organization_functionIds;
        }

        public void setOrganization_functionIds(List<Integer> organization_functionIds) {
            this.organization_functionIds = organization_functionIds;
        }

        public K3User() {
            super();
        }

        @Override
        public String toString() {
            return "K3User [ClientType=" + ClientType + ", UserName=" + UserName + ", DatabaseType=" + DatabaseType
                    + ", DBid=" + DBid + ", DataCenterName=" + DataCenterName + ", LogLocale=" + LogLocale + ", CustomName="
                    + CustomName + ", UserId=" + UserId + ", SessionId=" + SessionId + ", DisplayVersion=" + DisplayVersion
                    + ", OrganizationID=" + OrganizationID + ", OrganizationName=" + OrganizationName
                    + ", organization_functionIds=" + organization_functionIds + ", UserToken=" + UserToken + "]";
        }
}
